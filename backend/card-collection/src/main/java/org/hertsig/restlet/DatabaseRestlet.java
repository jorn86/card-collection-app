package org.hertsig.restlet;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.hertsig.dao.SearchDao;
import org.hertsig.dto.Card;
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
        try (SearchDao dao = dbi.open(SearchDao.class)) {
            return dao.getAll();
        }
    }

    @GET
    @Path("search")
    public Object searchCards(@QueryParam("name") String name) {
        try (SearchDao dao = dbi.open(SearchDao.class)) {
            List<Card> cards = dao.searchCardsByName(name + "%");
            log.debug("Got {} results for name {}", cards.size(), name);
            return cards;
        }
    }
}
