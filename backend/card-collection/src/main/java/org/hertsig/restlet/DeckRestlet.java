package org.hertsig.restlet;

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
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
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
        return Lists.newArrayList();
    }

    @GET
    @Path("{deckId}")
    public Object getDeck(@PathParam("deckId") UUID deckId) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            Deck deck = dao.getDeck(deckId);
            if (deck == null) {
                throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with id " + deckId + " does not exist");
            }
            if (deck.getUserid() != null) {
                checkUser();
                if (!deck.getUserid().equals(userManager.getUserId())) {
                    throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with id " + deckId + " does not exist for you");
                }
            }

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
    @Path("{deckId}/row/{rowId}")
    public Object updateAmount(DeckRow card) {
        checkUser();
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            if (!Objects.equal(dao.getDeck(card.getDeckid()).getUserid(), userManager.getUserId())) {
                throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with id " + card.getDeckid() + " does not exist for you");
            }
            if (dao.updateAmount(card) != 1) {
                log.warn("Update amount for row {} failed", card.getId());
                return null;
            }
            return card.getId();
        }
    }

    @POST
    @Path("addcard")
    public Object addCard(DeckRow card) {
        checkUser();
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            if (!Objects.equal(dao.getDeck(card.getDeckid()).getUserid(), userManager.getUserId())) {
                throw new HttpRequestException(Response.Status.NOT_FOUND, "Deck with id " + card.getDeckid() + " does not exist for you");
            }
            return dao.addCardToDeck(card);
        }
    }
}
