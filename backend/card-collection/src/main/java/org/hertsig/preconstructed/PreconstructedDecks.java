package org.hertsig.preconstructed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Charsets;
import org.hertsig.dao.PreconstructedDao;
import org.hertsig.dto.DeckBoard;
import org.hertsig.dto.Printing;
import org.hertsig.dto.Tag;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.startup.StartupAction;
import org.hertsig.startup.StartupActionException;
import org.skife.jdbi.v2.IDBI;

@Slf4j
@Singleton
public class PreconstructedDecks implements StartupAction {
    private final Gson gson = new Gson();
    private final IDBI dbi;

    @Inject
    PreconstructedDecks(IDBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public void run() throws StartupActionException {

        try (Scanner files = new Scanner(PreconstructedDecks.class.getResourceAsStream("list"));
                PreconstructedDao dao = dbi.open(PreconstructedDao.class)) {
            if (dao.getPreconstructedTag() == null) {
                dao.createPreconstructedTag();
            }
            Tag baseTag = dao.getPreconstructedTag();

            while (files.hasNext()) {
                String name = files.nextLine();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(PreconstructedDecks.class.getResourceAsStream(name), Charsets.UTF_8))) {
                    importPreconstructedDeck(dao, baseTag, reader);
                }
            }
        }
        catch (IOException e) {
            throw new StartupActionException("Exception while loading preconstructed decks", e);
        }
    }

    private void importPreconstructedDeck(PreconstructedDao dao, Tag baseTag, BufferedReader file) throws IOException {
        log.trace("Checking file {}", file);
        PreconstructedDeck deck = gson.fromJson(file, PreconstructedDeck.class);

        UUID tagId = ensureTagChain(dao, baseTag, deck.getTag());
        UUID existingDeck = dao.getPreconstructedDeck(tagId, deck.getName());
        if (existingDeck != null) {
            return;
        }

        Integer defaultSet = dao.getSet(deck.getSet());
        if (defaultSet == null) {
            log.warn("Preconstructed deck {} has unknown default set {}", file, deck.getSet());
            return;
        }

        UUID deckId = dao.createPreconstructedDeck(deck.getName());
        dao.addTag(deckId, tagId);
        importPreconstructedDeckBoard(dao, deckId, deck, defaultSet, "Mainboard", 0, deck.getMainboard());
        importPreconstructedDeckBoard(dao, deckId, deck, defaultSet, "Sideboard", 1, deck.getSideboard());
    }

    private void importPreconstructedDeckBoard(PreconstructedDao dao, UUID deckId, PreconstructedDeck deck, int defaultSet,
                                               String boardName, int order, List<PreconstructedDeck.Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return;
        }

        UUID boardId = dao.createBoard(new DeckBoard(null, deckId, boardName, order, null));
        for (PreconstructedDeck.Card card : cards) {
            int setId = card.getEdition() == null ? defaultSet : dao.getSet(card.getEdition());
            Printing printing = dao.getPrinting(setId, card.getName());
            if (printing == null) {
                Printing fallback = dao.getFallbackPrinting(card.getName(), setId);
                if (fallback == null) {
                    log.error("Invalid name for card {} in deck {}", card.getName(), deck.getName());
                }
                else {
                    log.trace("For preconstructed deck {} ({}), card {} does not exist in set {} ({}), falling back to {}",
                            deck.getName(), boardName, card.getName(), card.getEdition(), deck.getSet(), fallback.getSetid());
                    dao.addCard(boardId, fallback.getCardid(), fallback.getId(), card.getAmount());
                }
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
}
