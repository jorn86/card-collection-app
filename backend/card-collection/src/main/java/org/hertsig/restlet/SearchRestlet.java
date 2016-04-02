package org.hertsig.restlet;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.dto.SearchCard;
import org.hertsig.logic.QueryExecutor;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("search")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class SearchRestlet {
    private final QueryExecutor queryExecutor;

    @Inject
    public SearchRestlet(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @GET
    public List<SearchCard> query(@QueryParam("query") String query) {
        return queryExecutor.executeQuery(query);
    }
}
