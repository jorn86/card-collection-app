package org.hertsig.restlet;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.logic.QueryExecutor;
import org.hertsig.query.QueryNode;
import org.hertsig.query.QueryParser;
import org.hertsig.user.HttpRequestException;
import org.parboiled.errors.ParsingException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("search")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class SearchRestlet {
    @GET
    public QueryNode query(@QueryParam("query") String query) {
        return QueryExecutor.parse(query);
    }
}
