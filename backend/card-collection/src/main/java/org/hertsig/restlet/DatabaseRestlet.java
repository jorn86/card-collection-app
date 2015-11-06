package org.hertsig.restlet;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hertsig.dao.SetDao;
import org.skife.jdbi.v2.DBI;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("database")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class DatabaseRestlet {
    @Inject private DBI dbi;

    @GET
    @Path("sets")
    public Object getSets() {
        try (SetDao setDao = dbi.open(SetDao.class)) {
            return setDao.getAll();
        }
    }
}
