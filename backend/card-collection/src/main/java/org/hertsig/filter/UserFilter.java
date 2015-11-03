package org.hertsig.filter;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.hertsig.user.UserManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class UserFilter implements ContainerRequestFilter {
    @Inject private javax.inject.Provider<UserManager> userManagerProvider;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String userId = containerRequestContext.getHeaderString("X-UserId");
        if (userId != null) {
            userManagerProvider.get().setCurrentUser(UUID.fromString(userId));
        }
        log.debug("{} '{}' with user {}", containerRequestContext.getMethod(), containerRequestContext.getUriInfo().getPath(), userId);
    }
}
