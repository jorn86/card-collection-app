package org.hertsig.startup;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.ContentUpgradeDao;
import org.hertsig.dto.*;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

@Slf4j
@Singleton
public class ContentUpgrade implements StartupAction {
    private static final Pattern MANA_PATTERN = Pattern.compile("\\{.+?\\}");
    private final IDBI dbi;

    @Inject
    public ContentUpgrade(IDBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public void run() throws StartupActionException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        try (ContentUpgradeDao dao = dbi.open(ContentUpgradeDao.class);
             Reader sets = ensureSetFile()) {
            Map<String, FullSet> map = gson.fromJson(sets, Types.mapOf(String.class, FullSet.class));
            for (FullSet fullSet : map.values()) {
                log.debug("Checking database for set {}", fullSet.name);
                int setId = ensureSet(dao, fullSet);
                java.util.Set<List<String>> splitCards = Sets.newHashSet();
                for (FullSet.Card card : fullSet.cards) {
                    if (card.getNames() != null) {
                        splitCards.add(card.getNames());
                    }
                    int cardId = ensureCard(dao, card);
                    ensurePrinting(dao, cardId, setId, card);
                    ensureLegalities(dao, cardId, card.getLegalities());
                }
                ensureSplitCards(dao, setId, splitCards);
            }
        }
        catch (IOException e) {
            throw new StartupActionException("Exception during content upgrade", e);
        }
    }

    private void ensureLegalities(ContentUpgradeDao dao, int cardId, List<FullSet.Legality> legalities) {
        if (legalities != null) {
            EnumSet<Format> formats = EnumSet.allOf(Format.class);
            legalities.forEach(l -> {
                if (l != null && l.getFormat() != null) {
                    if (l.getLegality() != null) {
                        formats.remove(l.getFormat());
                        dao.ensureLegality(cardId, l.getFormat(), l.getLegality());
                    }
                }
            });
            formats.forEach(f -> dao.removeLegality(cardId, f));
        }
    }

    private void ensureSplitCards(ContentUpgradeDao dao, int setId, java.util.Set<List<String>> splitCards) {
        if (splitCards.isEmpty()) {
            return;
        }

        log.debug("Ensuring {} split cards", splitCards.size());

        for (List<String> splitCard : splitCards) {
            if (splitCard.size() != 2) {
                log.debug("Ignoring split card {}", splitCard);
                continue;
            }

            Card left = dao.getCard(splitCard.get(0));
            Card right = dao.getCard(splitCard.get(1));

            if (left.getLayout().equals("split")) {
                String name = left.getName() + " / " + right.getName();
                Card parent = dao.getCard(name);
                if (parent == null) {
                    parent = new Card(0, name, left.getFulltype(), left.getSupertypes(), left.getTypes(), left.getSubtypes(),
                        left.getCost() + "/" + right.getCost(), left.getCmc() + right.getCmc(),
                        joinColors(left.getColors(), right.getColors()), null, null, null, null, "split-parent", null, null);

                    int parentId = dao.createCard(parent);
                    dao.setParent(left.getId(), parentId);
                    dao.setParent(right.getId(), parentId);
                    parent.setId(parentId);
                }
                ensureSplitPrinting(dao, parent.getId(), setId, left, right);
            }
            else if (left.getLayout().equals("flip") || left.getLayout().equals("double-faced")) {
                dao.setFlipFront(left.getId(), right.getId());
            }
        }
    }

    private void ensureSplitPrinting(ContentUpgradeDao dao, int parentId, int setId, Card left, Card right) {
        List<Printing> printing = dao.getPrintings(setId, parentId);
        if (printing.isEmpty()) {
            Printing leftPrinting = dao.getPrintings(setId, left.getId()).get(0);
            dao.createPrinting(new Printing(0, setId, parentId, leftPrinting.getMultiverseid(),
                    null, leftPrinting.getRarity(), null, null, null));
        }
    }

    private List<Color> joinColors(List<Color> leftColors, List<Color> rightColors) {
        return Lists.newArrayList(Sets.newTreeSet(Iterables.concat(leftColors, rightColors)));
    }

    private int ensurePrinting(ContentUpgradeDao dao, int cardId, int setId, FullSet.Card card) {
        List<Printing> printings = dao.getPrintings(setId, cardId);
        Optional<Printing> printing = printings.stream().filter(p -> Objects.equals(p.getNumber(), card.getNumber())).findAny();
        if (printing.isPresent()) {
            return printing.get().getId();
        }
        return dao.createPrinting(new Printing(0, setId, cardId, card.getMultiverseid(), card.getNumber(),
                card.getRarity(), card.getOriginalText(), card.getOriginalType(), card.getFlavor()));
    }

    private int ensureCard(ContentUpgradeDao dao, FullSet.Card card) {
        Card existingCard = dao.getCard(card.getName());
        if (existingCard == null) {
            try {
                return dao.createCard(new Card(0, card.getName(), card.getType(), card.getSupertypes(), card.getTypes(), card.getSubtypes(),
                        mapManaCost(card.getManaCost()), d(card.getCmc(), 0d), mapColors(card.getColors()), card.getText(),
                        card.getPower(), card.getToughness(), card.getLoyalty(), card.getLayout(), null, null));
            }
            catch (DBIException e) {
                log.debug("Inserting card {} failed", card, e);
                return 0;
            }
        }
        return existingCard.getId();
    }

    @VisibleForTesting static String mapManaCost(String manaCost) {
        if (manaCost == null) return null;

        StringBuilder result = new StringBuilder();
        for (Matcher m = MANA_PATTERN.matcher(manaCost); m.find();) {
            String symbol = m.group();
            symbol = symbol.substring(1, symbol.length() - 1);
            if (symbol.length() == 1 || symbol.matches("\\d+")) {
                result.append(symbol);
            }
            else if (symbol.length() == 3) {
                result.append('{').append(symbol.charAt(0)).append(symbol.charAt(2)).append('}');
            }
            else {
                result.append('{').append(symbol.toUpperCase()).append('}');
            }
        }
        return result.toString();
    }

    private <T> T d(T value, T fallback) {
        return value == null ? fallback : value;
    }

    private List<Color> mapColors(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            return Lists.newArrayList();
        }
        return Lists.transform(colors, Color::forName);
    }

    private int ensureSet(ContentUpgradeDao dao, FullSet fullSet) {
        Set set = dao.getSet(fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode);
        if (set == null) {
            return dao.createSet(new Set(0, coalesce(fullSet.gathererCode, fullSet.code), fullSet.code, fullSet.magicCardsInfoCode,
                    fullSet.name, fullSet.releaseDate, fullSet.type, priority(fullSet.type), fullSet.onlineOnly));
        }
        if (!fullSet.code.equals(set.getCode()) || !fullSet.name.equals(set.getName()) || !fullSet.releaseDate.equals(set.getReleasedate())) {
            log.warn("Inconsistency: database {}; external {} {}", set, fullSet.code, fullSet.name, fullSet.releaseDate);
        }
        return set.getId();
    }

    private int priority(String type) {
        switch (type) {
            case "core":
            case "expansion":
                return 1;
            case "conspiracy":
            case "reprint":
            case "starter":
            case "un":
                return 2;
            case "archenemy":
            case "box":
            case "commander":
            case "duel deck":
            case "from the vault":
            case "planechase":
            case "premium deck":
                return 3;
            default:
                return 4;
        }
    }

    @VisibleForTesting Reader ensureSetFile() throws IOException {
        Path folder = Paths.get("json");
        boolean inMemory = false;
        if (!Files.isDirectory(folder)) {
            try {
                Files.createDirectory(folder);
            }
            catch (IOException e) {
                log.debug("No permission to write to file system, falling back to in memory", e);
                inMemory = true;
            }
        }

        Path file = folder.resolve("AllSets-x.json");
        if (!Files.isRegularFile(file)) {
            log.debug("Downloading sets file");
            if (inMemory) {
                ZipInputStream zipInputStream = new ZipInputStream(new URL("http", "mtgjson.com", "/json/AllSets-x.json.zip").openStream());
                Preconditions.checkState(zipInputStream.getNextEntry().getName().equals("AllSets-x.json"), "Invalid zip file contents");
                return new InputStreamReader(zipInputStream);
            }

            try (ZipInputStream zipInputStream = new ZipInputStream(new URL("http", "mtgjson.com", "/json/AllSets-x.json.zip").openStream());
                    FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
                Preconditions.checkState(zipInputStream.getNextEntry().getName().equals("AllSets-x.json"), "Invalid zip file contents");
                ByteStreams.copy(zipInputStream, outputStream);
            }
        }

        return new InputStreamReader(new FileInputStream(file.toFile()), Charsets.UTF_8);
    }

    @SafeVarargs
    private final <T> T coalesce(T... values) {
        return Stream.of(values).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
