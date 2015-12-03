package org.hertsig.restlet;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.DeckDao;
import org.hertsig.dao.DecklistDao;
import org.hertsig.dto.Deck;
import org.hertsig.dto.DeckEntry;
import org.hertsig.dto.DeckRow;
import org.hertsig.dto.Tag;
import org.hertsig.user.HttpRequestException;
import org.hertsig.user.UserManager;
import org.skife.jdbi.v2.DBI;

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
    @Inject private UserManager userManager;
    @Inject private DBI dbi;

    private void checkUser() {
        userManager.throwIfNotAvailable("DeckRestlet is not available without user");
    }

    @GET
    @Path("list")
    public Object getList() {
        checkUser();
        try (DecklistDao dao = dbi.open(DecklistDao.class)) {
            UUID user = userManager.getUserId();
            return createDecklist(dao.getDecks(user), dao.getTags(user));
        }
    }

    @GET
    @Path("preconstructedlist")
    public Object getPreconstructedList() {
        try (DecklistDao dao = dbi.open(DecklistDao.class)) {
            return createDecklist(dao.getPreconstructedDecks(), dao.getPreconstructedTags());
        }
    }

    private Object createDecklist(List<Deck> decks, List<Tag> tags) {
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
    private static class DeckListNode {
        private final String tagName;
        private final List<DeckListNode> children;
        private final List<Deck> decks;
    }

    @GET
    @Path("inventory")
    public Object getInventory() {
        checkUser();
        return getDeck(userManager.getCurrentUser().getInventoryid());
    }

    @GET
    @Path("{deckId}")
    public Object getDeck(@PathParam("deckId") UUID deckId) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            Deck deck = getDeckForUser(dao, deckId);
            List<DeckEntry> cards = dao.getCards(deck.getId());
            deck.setCards(cards);
            return deck;
        }
    }

    @POST
    public Object createDeck(Deck deck) {
        checkUser();
        deck.setId(UUID.randomUUID());
        return deck;
    }

    @PUT
    @Path("card")
    public Object updateRow(DeckRow row) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            getDeckForUser(dao, row.getDeckid());

            if (row.getAmount() < 1) {
                handleUpdate(dao.deleteRow(row.getId()), "Row with id " + row.getId() + " not found");
                return null;
            }

            handleUpdate(dao.updateRow(row), "Row with id " + row.getId() + " not found");
            return row.getId();
        }
    }

    @POST
    @Path("card")
    public Object addCard(DeckRow card) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            getDeckForUser(dao, card.getDeckid());

            if (card.getAmount() < 1) {
                card.setAmount(1);
            }

            List<DeckRow> rows = dao.getRows(card.getDeckid(), card.getCardid());
            Optional<DeckRow> existingPrinting = rows.stream().filter(row -> Objects.equal(row.getPrintingid(), card.getPrintingid())).findAny();
            if (existingPrinting.isPresent()) {
                DeckRow existing = existingPrinting.get();
                return dao.updateRow(new DeckRow(existing.getId(), null, null, existing.getPrintingid(), existing.getAmount() + card.getAmount()));
            }
            if (rows.size() > 0) {
                DeckRow existing = rows.get(0);
                return dao.updateRow(new DeckRow(existing.getId(), null, null, existing.getPrintingid(), existing.getAmount() + card.getAmount()));
            }
            return dao.addCardToDeck(card);
        }
    }

    private Deck getDeckForUser(DeckDao dao, UUID deckId) {
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
