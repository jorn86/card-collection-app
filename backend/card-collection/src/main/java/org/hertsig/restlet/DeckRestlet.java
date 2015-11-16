package org.hertsig.restlet;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hertsig.dao.DeckDao;
import org.hertsig.dao.DecklistDao;
import org.hertsig.dto.Card;
import org.hertsig.dto.Deck;
import org.hertsig.dto.DeckEntry;
import org.hertsig.dto.Row;
import org.hertsig.user.UnauthorizedException;
import org.hertsig.user.UserManager;
import org.skife.jdbi.v2.DBI;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("deck")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class DeckRestlet {
    @Inject private UserManager userManager;
    @Inject private DBI dbi;

    private void checkUser() {
        userManager.throwIfNotAvailable(() -> new UnauthorizedException("DeckRestlet is not available without user"));
    }

    @GET
    @Path("list")
    public Object getList() {
        checkUser();
        return getPreconstructedList();
    }

    @GET
    @Path("preconstructedlist")
    public Object getPreconstructedList() {
        try (DecklistDao dao = dbi.open(DecklistDao.class)) {
            return ImmutableMap.of("decks", dao.getPreconstructedDecks(), "tags", Lists.newArrayList());
        }
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
            if (deck.getUserid() != null) {
                checkUser();
                if (!deck.getUserid().equals(userManager.getCurrentUser().getId())) {
                    throw new UnauthorizedException("Not your deck");
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
    public Object updateAmount(@PathParam("deckId") UUID deckId, @PathParam("rowId") UUID rowId, Row row) {
        checkUser();
        row.setId(rowId);
        return row;
    }

    @POST
    @Path("{deckId}/card")
    public Object addCard(@PathParam("deckId") UUID deckId, Row card) {
        checkUser();
        return card;
    }
}
