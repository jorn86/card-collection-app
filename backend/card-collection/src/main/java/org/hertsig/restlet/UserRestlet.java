package org.hertsig.restlet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hertsig.dao.AuthenticationOptionDao;
import org.hertsig.dao.DeckDao;
import org.hertsig.dao.UserDao;
import org.hertsig.dto.User;
import org.hertsig.user.UserManager;
import org.skife.jdbi.v2.DBI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("user")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class UserRestlet {
    @Inject private DBI dbi;
    @Inject private UserManager userManager;

    @POST
    public User ensureUser(User newUser) {
        List<User.AuthenticationOption> options = newUser.getAuthenticationOptions();
        if (options == null || options.isEmpty()) {
            log.warn("Invalid user {}", newUser);
            return null;
        }

        try (AuthenticationOptionDao authDao = dbi.open(AuthenticationOptionDao.class);
             UserDao userDao = dbi.open(UserDao.class);
             DeckDao deckDao = dbi.open(DeckDao.class)) {

            Optional<UUID> user = options.stream().map(authDao::getExistingUser).filter(Objects::nonNull).findFirst();
            if (user.isPresent()) {
                return userDao.get(user.get());
            }

            UUID createdUserId = userDao.create(newUser);
            options.stream().forEach(option -> authDao.create(createdUserId, option));
            UUID inventoryId = deckDao.createDeck("Inventory", createdUserId);
            userDao.setInventory(createdUserId, inventoryId);
            return userDao.get(createdUserId);
        }
    }

    @POST
    @Path("addauthentication")
    public Object addAuthenticationOption(User.AuthenticationOption auth) {
        userManager.throwIfNotAvailable("Cannot add authentication option without user");
        try (AuthenticationOptionDao authDao = dbi.open(AuthenticationOptionDao.class)) {
            authDao.create(userManager.getCurrentUser().getId(), auth);
            return Response.noContent().build();
        }
    }
}
