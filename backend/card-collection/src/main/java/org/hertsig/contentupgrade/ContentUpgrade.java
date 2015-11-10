package org.hertsig.contentupgrade;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hertsig.dao.ContentUpgradeDao;
import org.hertsig.dto.Card;
import org.hertsig.dto.Color;
import org.hertsig.dto.Printing;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.DBIException;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ContentUpgrade {
    @Inject
    public ContentUpgrade(DBI dbi) {
        try (ContentUpgradeDao dao = dbi.open(ContentUpgradeDao.class); Reader sets = ensureSetFile()) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            Map<String, FullSet> map = gson.fromJson(sets, Types.mapOf(String.class, FullSet.class));
            for (FullSet fullSet : map.values()) {
                log.debug("Checking database for set {}", fullSet.name);
                UUID setId = ensureSet(dao, fullSet);
                for (FullSet.Card card : fullSet.cards) {
                    UUID cardId = ensureCard(dao, card);
                    ensurePrinting(dao, cardId, setId, card);
                }
            }
        }
        catch (IOException e) {
            log.error("Exception during content upgrade", e);
        }
    }

    private UUID ensurePrinting(ContentUpgradeDao dao, UUID cardId, UUID setId, FullSet.Card card) {
        Printing printing = dao.getPrinting(setId, cardId);
        if (printing == null) {
            return dao.createPrinting(new Printing(null, setId, cardId, card.getMultiverseid(), card.getNumber(),
                    card.getRarity(), card.getOriginalText(), card.getOriginalType(), card.getFlavor()));
        }
        return printing.getId();
    }

    private UUID ensureCard(ContentUpgradeDao dao, FullSet.Card card) {
        Card existingCard = dao.getCard(card.getName());
        if (existingCard == null) {
            try {
                return dao.createCard(new Card(null, card.getName(), card.getType(), card.getSupertypes(), card.getSubtypes(),
                        card.getManaCost(), d(card.getCmc(), 0d), mapColors(card.getColors()), card.getText(),
                        card.getPower(), card.getToughness(), card.getLoyalty(), card.getLayout(), null, null));
            }
            catch (DBIException e) {
                log.debug("Inserting card {} failed", card, e);
                return null;
            }
        }
        return existingCard.getId();
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

    private UUID ensureSet(ContentUpgradeDao dao, FullSet fullSet) {
        Set set = dao.getSet(fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode);
        if (set == null) {
            return dao.createSet(new Set(null, fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode, fullSet.code, fullSet.name, fullSet.releaseDate));
        }
        if (!fullSet.code.equals(set.getCode()) || !fullSet.name.equals(set.getName()) || !fullSet.releaseDate.equals(set.getReleasedate())) {
            log.warn("Inconsistency: database {}; external {} {}", set, fullSet.code, fullSet.name, fullSet.releaseDate);
        }
        return set.getId();
    }

    private Reader ensureSetFile() throws IOException {
        Path folder = Paths.get("json");
        if (!Files.isDirectory(folder)) {
            Files.createDirectory(folder);
        }

        Path file = folder.resolve("AllSets-x.json");
        if (!Files.isRegularFile(file)) {
            log.debug("Downloading sets file");
            try (ZipInputStream zipInputStream = new ZipInputStream(new URL("http", "mtgjson.com", "/json/AllSets-x.json.zip").openStream());
                    FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
                Preconditions.checkState(zipInputStream.getNextEntry().getName().equals("AllSets-x.json"), "Invalid zip file contents");
                ByteStreams.copy(zipInputStream, outputStream);
            }
        }

        return new InputStreamReader(new FileInputStream(file.toFile()), Charsets.UTF_8);
    }
}
