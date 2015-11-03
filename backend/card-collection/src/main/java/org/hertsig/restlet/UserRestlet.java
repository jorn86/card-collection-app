package org.hertsig.restlet;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hertsig.dto.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("user")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class UserRestlet {
    @POST
    public Object ensureUser(User user) {
        user.setId(UUID.randomUUID());
        return user;
    }
}
