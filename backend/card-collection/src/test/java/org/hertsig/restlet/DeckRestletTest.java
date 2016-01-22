package org.hertsig.restlet;

import com.google.common.collect.Lists;
import org.hertsig.dao.DeckDao;
import org.hertsig.dao.DecklistDao;
import org.hertsig.dao.UserDao;
import org.hertsig.database.MockDbi;
import org.hertsig.dto.*;
import org.hertsig.user.UserManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeckRestletTest {
    private final MockDbi mock = new MockDbi();
    private final UserManager userManager = new UserManager(mock.getDbi());
    private final DeckRestlet restlet = new DeckRestlet(mock.getDbi(), userManager);
    private UUID userId;

    @Rule public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void initUser() {
        userId = UUID.randomUUID();
        when(mock.getMockedDao(UserDao.class).get(userId)).thenReturn(new User(userId, getClass().getSimpleName(), null, null, null));
        userManager.setCurrentUser(userId);
    }

    @Test
    public void testEmptyDecklist() {
        when(mock.getMockedDao(DecklistDao.class).getTags(userId)).thenReturn(Lists.newArrayList());
        DeckRestlet.DeckListNode list = restlet.getList();
        assertNotNull(list);
        assertNull(list.getTagName());
        assertTrue(list.getDecks().isEmpty());
        assertTrue(list.getChildren().isEmpty());
    }

    @Test
    public void testSimpleTagTree() {
        UUID parentId = UUID.randomUUID();
        UUID firstChildId = UUID.randomUUID();
        when(mock.getMockedDao(DecklistDao.class).getTags(userId)).thenReturn(Lists.newArrayList(
                new Tag(parentId, null, "parent", userId),
                new Tag(firstChildId, parentId, "first child", userId),
                new Tag(UUID.randomUUID(), parentId, "second child", userId),
                new Tag(UUID.randomUUID(), firstChildId, "first grandchild", userId)
        ));
        DeckRestlet.DeckListNode list = restlet.getList();
        assertEquals(2, list.getChildren().size());
        assertEquals("first child", list.getChildren().get(0).getTagName());
        assertEquals("second child", list.getChildren().get(1).getTagName());
        assertEquals(1, list.getChildren().get(0).getChildren().size());
        assertEquals("first grandchild", list.getChildren().get(0).getChildren().get(0).getTagName());
    }

    @Test
    public void testGetNonexistingDeck() {
        expectedException.expect(new HttpRequestExceptionMatcher(Response.Status.NOT_FOUND));
        restlet.getDeck(UUID.randomUUID());
    }

    @Test
    public void testGetMyDeck() {
        UUID deckId = UUID.randomUUID();
        Deck deck = new Deck(deckId, userId, null, "My deck", false, null);
        when(mock.getMockedDao(DeckDao.class).getDeck(deckId)).thenReturn(deck);
        restlet.getDeck(deckId);
    }

    @Test
    public void testGetDeckForOtherUser() {
        UUID deckId = UUID.randomUUID();
        when(mock.getMockedDao(DeckDao.class).getDeck(deckId)).thenReturn(new Deck(deckId, UUID.randomUUID(), null, "Not my deck", false, null));
        expectedException.expect(new HttpRequestExceptionMatcher(Response.Status.NOT_FOUND));
        restlet.getDeck(deckId);
    }

    @Test
    public void testGetPreconstructedDeck() {
        UUID deckId = UUID.randomUUID();
        when(mock.getMockedDao(DeckDao.class).getDeck(deckId)).thenReturn(new Deck(deckId, null, null, "Preconstructed deck", false, null));
        restlet.getDeck(deckId);
    }

    @Test
    public void testAddCardAsNewRow() {
        UUID boardid = initMyDeck();
        DeckRow input = new DeckRow(null, boardid, 0, null, -1);

        DeckDao dao = mock.getMockedDao(DeckDao.class);
        when(dao.getBoardRows(boardid, 0)).thenReturn(Lists.newArrayList());

        restlet.addCard(input);

        assertEquals(1, input.getAmount());
        verify(dao).addCardToDeck(input);
    }

    @Test
    public void testAddCardToExistingRowByPrinting() {
        UUID boardId = initMyDeck();
        int cardId = 42;
        int printingId = 1337;
        DeckRow input = new DeckRow(null, boardId, cardId, printingId, 2);

        DeckDao dao = mock.getMockedDao(DeckDao.class);
        UUID expectedRowUpdate = UUID.randomUUID();
        when(dao.getBoardRows(boardId, cardId)).thenReturn(Lists.newArrayList(
                new DeckRow(UUID.randomUUID(), boardId, cardId, null, 1),
                new DeckRow(expectedRowUpdate, boardId, cardId, printingId, 2)
        ));
        restlet.addCard(input);
        verify(dao).updateRow(new DeckRow(expectedRowUpdate, null, 0, null, 4));
    }

    @Test
    public void testAddCardToSingleExistingRow() {
        UUID boardId = initMyDeck();
        int cardId = 42;
        int printingId = 1337;
        DeckRow input = new DeckRow(null, boardId, cardId, null, 2);

        DeckDao dao = mock.getMockedDao(DeckDao.class);
        UUID expectedRowUpdate = UUID.randomUUID();
        when(dao.getBoardRows(boardId, cardId)).thenReturn(Lists.newArrayList(
                new DeckRow(expectedRowUpdate, boardId, cardId, printingId, 1)
        ));
        restlet.addCard(input);
        verify(dao).updateRow(new DeckRow(expectedRowUpdate, null, 0, null, 3));
    }

    @Test
    public void testAddCardWithPrintingToSingleExistingRow() {
        UUID boardId = initMyDeck();
        int cardId = 42;
        int printingId = 1337;
        DeckRow input = new DeckRow(null, boardId, cardId, printingId, 2);

        DeckDao dao = mock.getMockedDao(DeckDao.class);
        UUID expectedRowUpdate = UUID.randomUUID();
        when(dao.getBoardRows(boardId, cardId)).thenReturn(Lists.newArrayList(
                new DeckRow(expectedRowUpdate, boardId, cardId, null, 1)
        ));
        restlet.addCard(input);
        verify(dao).addCardToDeck(input);
    }

    @Test
    public void testUpdateRowAmount() {
        DeckRow input = new DeckRow(UUID.randomUUID(), initMyDeck(), 0, null, 2);
        restlet.updateRow(input);
        verify(mock.getMockedDao(DeckDao.class)).updateRow(input);
    }

    @Test
    public void testUpdateRowAmountToZero() {
        UUID rowId = UUID.randomUUID();
        DeckRow input = new DeckRow(rowId, initMyDeck(), 0, null, 0);

        DeckDao dao = mock.getMockedDao(DeckDao.class);
        when(dao.deleteRow(rowId)).thenReturn(1);
        restlet.updateRow(input);
        verify(dao).deleteRow(rowId);
    }

    @Test
    public void testUpdateNonexistingRowAmountToZero() {
        expectedException.expect(new HttpRequestExceptionMatcher(Response.Status.NOT_FOUND));
        restlet.updateRow(new DeckRow(UUID.randomUUID(), initMyDeck(), 0, null, 0));
    }

    private UUID initMyDeck() {
        UUID deckId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        Deck deck = new Deck(deckId, userId, null, "My deck", false, Lists.newArrayList(new DeckBoard(boardId, deckId, "Test mainboard", 1, null)));
        when(mock.getMockedDao(DeckDao.class).getDeck(deckId)).thenReturn(deck);
        when(mock.getMockedDao(DeckDao.class).getDeckByBoard(boardId)).thenReturn(deck);
        return boardId;
    }
}
