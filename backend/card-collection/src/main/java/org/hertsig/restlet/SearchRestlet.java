package org.hertsig.restlet;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.dto.SearchCard;
import org.hertsig.logic.QueryExecutor;
import org.hertsig.startup.Views;
import org.hertsig.user.HttpRequestException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@Path("search")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class SearchRestlet {
    private final QueryExecutor queryExecutor;
    private final Views views;

    @Inject
    public SearchRestlet(QueryExecutor queryExecutor, Views views) {
        this.queryExecutor = queryExecutor;
        this.views = views;
    }

    @GET
    public List<SearchCard> query(@QueryParam("query") String query) {
        if (views.isRunning()) {
            throw new HttpRequestException(Response.Status.SERVICE_UNAVAILABLE, "Search view is updating. Please try again in a few minutes");
        }
        return queryExecutor.executeQuery(query);
    }
}
