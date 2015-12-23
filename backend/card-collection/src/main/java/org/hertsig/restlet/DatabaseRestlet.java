package org.hertsig.restlet;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.SearchDao;
import org.skife.jdbi.v2.IDBI;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("database")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class DatabaseRestlet {
    private static final Escaper LIKE_ESCAPER = Escapers.builder().addEscape('_', "\\_").addEscape('%', "\\%").build();

    private final IDBI dbi;

    @Inject
    public DatabaseRestlet(IDBI dbi) {
        this.dbi = dbi;
    }

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
            return dao.searchCardsByName(LIKE_ESCAPER.escape(name) + "%");
        }
    }

    @GET
    @Path("statistics")
    @JacksonFeatures(serializationEnable = SerializationFeature.INDENT_OUTPUT)
    public Object getSetStatistics() {
        try (SearchDao dao = dbi.open(SearchDao.class)) {
            return dao.getSetStatistics();
        }
    }
}
