package org.hertsig.restlet;

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

import org.hertsig.dto.Deck;
import org.hertsig.dto.Row;
import org.hertsig.user.UnauthorizedException;
import org.hertsig.user.UserManager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("deck")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class DeckRestlet {
    private UserManager userManager;

    @Inject
    public DeckRestlet(UserManager userManager) {
        this.userManager = userManager;
    }

    private void checkUser() {
        userManager.throwIfNotAvailable(() -> new UnauthorizedException("DeckRestlet is not available without user"));
    }

    @GET
    @Path("list")
    public Object getList() {
        checkUser();
        return ImmutableMap.of(
                "decks", Lists.newArrayList(new Deck(UUID.randomUUID(), "One"), new Deck(UUID.randomUUID(), "Two")),
                "tags", Lists.newArrayList());
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
        checkUser();
        return Lists.newArrayList();
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
}
