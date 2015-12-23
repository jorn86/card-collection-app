package org.hertsig.restlet;

import com.google.common.collect.Lists;
import org.hertsig.dao.AuthenticationOptionDao;
import org.hertsig.dao.DeckDao;
import org.hertsig.dao.UserDao;
import org.hertsig.database.MockDbi;
import org.hertsig.dto.User;
import org.hertsig.user.HttpRequestException;
import org.hertsig.user.UserManager;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class UserRestletTest {
    private final MockDbi mock = new MockDbi();
    private final UserManager userManager = new UserManager(mock.getDbi());
    private final UserRestlet restlet = new UserRestlet(mock.getDbi(), userManager);

    @Test
    public void testRejectOnNoAuthenticationOptions() {
        assertNull(restlet.ensureUser(new User(null, "test1", "e@mail", null, null)));
    }

    @Test
    public void testExistingUserIsReturned() {
        UUID existingUserId = UUID.randomUUID();
        User existingUser = new User(existingUserId, null, null, null, null);
        User.AuthenticationOption authenticationOption = new User.AuthenticationOption("", "");

        when(mock.getMockedDao(AuthenticationOptionDao.class).getExistingUser(authenticationOption)).thenReturn(existingUserId);
        when(mock.getMockedDao(UserDao.class).get(existingUserId)).thenReturn(existingUser);

        User user = restlet.ensureUser(new User(null, "test2", "e@mail", null, Lists.newArrayList(authenticationOption)));

        assertSame(existingUser, user);
    }

    @Test
    public void testNewUserHasInventory() {
        UUID newUserId = UUID.randomUUID();
        UUID newDeckId = UUID.randomUUID();
        User newUser = new User(null, "test3", null, null, Lists.newArrayList(new User.AuthenticationOption("", "")));
        User createdUser = new User(newUserId, "created", null, null, null);

        UserDao userDao = mock.getMockedDao(UserDao.class);

        when(userDao.create(newUser)).thenReturn(newUserId);
        when(mock.getMockedDao(DeckDao.class).createDeck("Inventory", newUserId)).thenReturn(newDeckId);
        when(userDao.get(newUserId)).thenReturn(createdUser);
        when(userDao.setInventory(newUserId, newDeckId)).thenReturn(1);

        User user = restlet.ensureUser(newUser);

        assertSame(createdUser, user);
        verify(userDao).setInventory(newUserId, newDeckId);
    }

    @Test(expected = HttpRequestException.class)
    public void testUnauthorizedOnNoUser() {
        restlet.addAuthenticationOption(new User.AuthenticationOption("", ""));
    }

    @Test
    public void testAddAuthenticationOption() {
        UUID userId = UUID.randomUUID();
        User.AuthenticationOption newOption = new User.AuthenticationOption("", "");

        when(mock.getMockedDao(UserDao.class).get(userId)).thenReturn(new User(userId, "test5", null, null, null));

        userManager.setCurrentUser(userId);
        Object result = restlet.addAuthenticationOption(newOption);

        assertNull(result);
        verify(mock.getMockedDao(AuthenticationOptionDao.class)).create(userId, newOption);
    }
}
