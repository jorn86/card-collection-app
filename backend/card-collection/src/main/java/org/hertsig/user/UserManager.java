package org.hertsig.user;

import java.util.UUID;

import javax.inject.Inject;

import org.hertsig.dao.UserDao;
import org.hertsig.dto.User;
import org.skife.jdbi.v2.DBI;

import com.google.inject.servlet.RequestScoped;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class UserManager {
    @Inject private DBI dbi;

    @Getter private User currentUser;

    public boolean isAvailable() {
        return currentUser != null;
    }

    public void setCurrentUser(UUID userId) {
        try (UserDao userDao = dbi.open(UserDao.class)) {
            currentUser = userDao.get(userId);
        }
    }
}
