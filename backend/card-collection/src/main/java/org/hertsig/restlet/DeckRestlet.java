package org.hertsig.restlet;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.DeckDao;
import org.hertsig.dao.DecklistDao;
import org.hertsig.dto.*;
import org.hertsig.user.HttpRequestException;
import org.hertsig.user.UserManager;
import org.skife.jdbi.v2.IDBI;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Path("deck")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class DeckRestlet {
    private final UserManager userManager;
    private final IDBI dbi;

    @Inject
    public DeckRestlet(IDBI dbi, UserManager userManager) {
        this.dbi = dbi;
        this.userManager = userManager;
    }

    private void checkUser() {
        userManager.throwIfNotAvailable("DeckRestlet is not available without user");
    }

    @GET
    @Path("list")
    public DeckListNode getList() {
        checkUser();
        try (DecklistDao dao = dbi.open(DecklistDao.class)) {
            UUID user = userManager.getUserId();
            return createDecklist(dao.getDecks(user), dao.getTags(user));
        }
    }

    @GET
    @Path("preconstructedlist")
    public DeckListNode getPreconstructedList() {
        try (DecklistDao dao = dbi.open(DecklistDao.class)) {
            return createDecklist(dao.getPreconstructedDecks(), dao.getPreconstructedTags());
        }
    }

    private DeckListNode createDecklist(List<Deck> decks, List<Tag> tags) {
        if (tags.isEmpty()) {
            return new DeckListNode(null, Lists.newArrayList(), decks);
        }

        return createNode(tags.get(0), tags, decks);
    }

    private DeckListNode createNode(Tag root, List<Tag> tags, List<Deck> decks) {
        List<DeckListNode> children = tags.stream()
                .filter(t -> Objects.equal(t.getParentid(), root.getId()))
                .map(t -> createNode(t, tags, decks))
                .collect(Collectors.toList());
        return new DeckListNode(root.getName(), children, decks.stream()
                .filter(d -> d.getTags().contains(root.getId()))
                .collect(Collectors.toList()));
    }

    @Data @AllArgsConstructor
    @VisibleForTesting static class DeckListNode {
        private final String tagName;
        private final List<DeckListNode> children;
        private final List<Deck> decks;
    }

    @GET
    @Path("inventory")
    public Deck getInventory() {
        checkUser();
        return getDeck(userManager.getCurrentUser().getInventoryid());
    }

    @GET
    @Path("{deckId}")
    public Deck getDeck(@PathParam("deckId") UUID deckId) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            checkUser();

            Deck deck = dao.getDeck(deckId);
            if (deck == null) {
                throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with id " + deckId + " does not exist");
            }
            if (deck.getUserid() != null) {
                if (!deck.getUserid().equals(userManager.getUserId())) {
                    throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with id " + deckId + " does not exist for you");
                }
            }

            List<DeckBoard> boards = dao.getBoards(deck.getId());
            for (DeckBoard board : boards) {
                List<DeckEntry> cards = dao.getCardsForBoard(board.getId());
                board.setCards(cards);
            }

            deck.setBoards(boards);
            return deck;
        }
    }

    @PUT
    @Path("card")
    public DeckRow updateRow(DeckRow row) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            getDeckForUser(dao, row.getBoardid());

            if (row.getAmount() < 1) {
                handleUpdate(dao.deleteRow(row.getId()), "Row with id " + row.getId() + " not found");
                return null;
            }

            return dao.updateRow(row);
        }
    }

    @POST
    @Path("card")
    public DeckRow addCard(DeckRow card) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            getDeckForUser(dao, card.getBoardid());

            if (card.getAmount() < 1) {
                card.setAmount(1);
            }

            List<DeckRow> rows = dao.getBoardRows(card.getBoardid(), card.getCardid());
            Optional<DeckRow> existingPrinting = rows.stream().filter(row -> Objects.equal(row.getPrintingid(), card.getPrintingid())).findAny();
            if (existingPrinting.isPresent()) {
                DeckRow existing = existingPrinting.get();
                return dao.updateRow(new DeckRow(existing.getId(), null, null, null, existing.getAmount() + card.getAmount()));
            }
            if (rows.size() > 0 && card.getPrintingid() == null) {
                DeckRow existing = rows.get(0);
                return dao.updateRow(new DeckRow(existing.getId(), null, null, null, existing.getAmount() + card.getAmount()));
            }
            return dao.addCardToDeck(card);
        }
    }

    private Deck getDeckForUser(DeckDao dao, UUID boardId) {
        checkUser();

        Deck deck = dao.getDeckByBoard(boardId);
        if (deck == null) {
            throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with board id " + boardId + " does not exist");
        }
        if (deck.getUserid() != null) {
            if (!deck.getUserid().equals(userManager.getUserId())) {
                throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with board id " + boardId + " does not exist for you");
            }
        }
        return deck;
    }

    private void handleUpdate(int updated, String message) {
        if (updated < 1) {
            throw new HttpRequestException(Response.Status.NOT_FOUND, message);
        }
        if (updated > 1) {
            log.warn("Update hit {} rows: {}", updated, message);
        }
    }
}
