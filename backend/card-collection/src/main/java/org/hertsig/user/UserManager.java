package org.hertsig.user;

import java.util.UUID;

import org.hertsig.dto.User;

import com.google.common.collect.Lists;
import com.google.inject.servlet.RequestScoped;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class UserManager {
    @Getter private User currentUser;

    public boolean isAvailable() {
        return currentUser != null;
    }

    public void setCurrentUser(UUID userId) {
        currentUser = new User(userId, "Jorn", null, Lists.newArrayList());
    }
}
