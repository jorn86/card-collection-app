package org.hertsig.restlet;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.SearchDao;
import org.skife.jdbi.v2.DBI;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("database")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class DatabaseRestlet {
    @Inject private DBI dbi;

    @GET
    @Path("sets")
    public Object getSets() {
        try (SearchDao dao = dbi.open(SearchDao.class)) {
            return dao.getAll();
        }
    }

    @GET
    @Path("search")
    public Object searchCards(@QueryParam("name") String name) {
        try (SearchDao dao = dbi.open(SearchDao.class)) {
            return dao.searchCardsByName(name + "%");
        }
    }
}
