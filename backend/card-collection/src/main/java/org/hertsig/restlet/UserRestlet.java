package org.hertsig.restlet;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hertsig.dao.AuthenticationMethodDao;
import org.hertsig.dao.UserDao;
import org.hertsig.dto.User;
import org.skife.jdbi.v2.DBI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("user")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class UserRestlet {
    @Inject private DBI dbi;

    @POST
    public Object ensureUser(User newUser) {
        if (newUser.getAuthenticationOptions().size() != 1) {
            log.warn("Invalid user {}", newUser);
            return null;
        }

        try (AuthenticationMethodDao authDao = dbi.open(AuthenticationMethodDao.class);
             UserDao userDao = dbi.open(UserDao.class)) {

            User.AuthenticationOption auth = newUser.getAuthenticationOptions().get(0);
            UUID existingUserId = authDao.getExistingUser(auth.getId(), auth.getType());
            if (existingUserId != null) {
                return userDao.get(existingUserId);
            }

            UUID createdUserId = userDao.create(newUser.getName(), newUser.getEmail());
            authDao.insert(createdUserId, auth.getId(), auth.getType());
            return userDao.get(createdUserId);
        }
    }
}
