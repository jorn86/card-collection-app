package org.hertsig.user;

import com.google.inject.servlet.RequestScoped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.UserDao;
import org.hertsig.dto.User;
import org.skife.jdbi.v2.IDBI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Slf4j
@RequestScoped
public class UserManager {
    private final IDBI dbi;

    @Getter private User currentUser;

    @Inject
    public UserManager(IDBI dbi) {
        this.dbi = dbi;
    }

    public boolean isAvailable() {
        return currentUser != null;
    }

    public void throwIfNotAvailable(String message) {
        if (!isAvailable()) {
            throw new HttpRequestException(Response.Status.UNAUTHORIZED, message);
        }
    }

    public void setCurrentUser(UUID userId) {
        try (UserDao userDao = dbi.open(UserDao.class)) {
            currentUser = userDao.get(userId);
        }
    }

    public UUID getUserId() {
        return getCurrentUser().getId();
    }
}
