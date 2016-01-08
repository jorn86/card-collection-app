package org.hertsig.startup;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hertsig.dao.PreconstructedDao;
import org.hertsig.dto.DeckBoard;
import org.hertsig.dto.Printing;
import org.hertsig.dto.Tag;

import com.google.gson.Gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.IDBI;

@Slf4j
@Singleton
public class PreconstructedDecks implements StartupAction {
    private final Gson gson = new Gson();
    @Inject private IDBI dbi;

    @Override
    public void run() throws StartupActionException {
        try (PreconstructedDao dao = dbi.open(PreconstructedDao.class)) {
            if (dao.getPreconstructedTag() == null) {
                dao.createPreconstructedTag();
            }
            Tag baseTag = dao.getPreconstructedTag();

            Files.walkFileTree(Paths.get("json", "preconstructed"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    importPreconstructedDeck(dao, baseTag, file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            throw new StartupActionException("Exception while loading preconstructed decks", e);
        }
    }

    private void importPreconstructedDeck(PreconstructedDao dao, Tag baseTag, Path file) throws IOException {
        log.debug("Checking file {}", file);
        PreconstructedDeck deck = gson.fromJson(Files.newBufferedReader(file), PreconstructedDeck.class);

        UUID tagId = ensureTagChain(dao, baseTag, deck.getTag());
        UUID existingDeck = dao.getPreconstructedDeck(tagId, deck.getName());
        if (existingDeck != null) {
            return;
        }

        UUID defaultSet = dao.getSet(deck.getSet());
        if (defaultSet == null) {
            log.warn("Preconstructed deck {} has unknown default set {}", file, deck.getSet());
            return;
        }

        UUID deckId = dao.createPreconstructedDeck(deck.getName());
        dao.addTag(deckId, tagId);
        importPreconstructedDeckBoard(dao, deckId, deck, defaultSet, "Mainboard", 0, deck.getMainboard());
        importPreconstructedDeckBoard(dao, deckId, deck, defaultSet, "Sideboard", 1, deck.getSideboard());
    }

    private void importPreconstructedDeckBoard(PreconstructedDao dao, UUID deckId, PreconstructedDeck deck, UUID defaultSet, String boardName, int order, List<PreconstructedDeck.Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return;
        }

        UUID boardId = dao.createBoard(new DeckBoard(null, deckId, boardName, order, null));
        for (PreconstructedDeck.Card card : cards) {
            UUID setId = card.getEdition() == null ? defaultSet : dao.getSet(card.getEdition());
            Printing printing = dao.getPrinting(setId, card.getName());
            if (printing == null) {
                log.warn("For preconstructed deck {} ({}), card {} does not exist in set {} ({})", deck.getName(), boardName, card.getName(), card.getEdition(), deck.getSet());
                dao.addCard(boardId, card.getName(), card.getAmount());
            }
            else {
                dao.addCard(boardId, printing.getCardid(), printing.getId(), card.getAmount());
            }
        }
    }

    private UUID ensureTagChain(PreconstructedDao dao, Tag baseTag, List<String> tag) {
        UUID parent = baseTag.getId();
        for (String t : tag) {
            UUID child = dao.getTag(parent, t);
            if (child == null) {
                parent = dao.createTag(new Tag(null, parent, t, null));
            }
            else {
                parent = child;
            }
        }
        return parent;
    }

    @Data
    private static class PreconstructedDeck {
        private List<String> tag;
        private String name;
        private String set;
        private List<Card> mainboard;
        private List<Card> sideboard;

        @Data
        private static class Card {
            private int amount;
            private String name;
            private String edition;
        }
    }
}
