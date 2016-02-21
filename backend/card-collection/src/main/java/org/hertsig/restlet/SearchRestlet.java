package org.hertsig.restlet;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.parser.QueryNode;
import org.hertsig.parser.QueryParser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("search")
@Produces(MediaType.APPLICATION_JSON) @Consumes(MediaType.APPLICATION_JSON)
public class SearchRestlet {
    @GET
    public QueryNode query(@QueryParam("query") String query) {
        log.debug("Got {}", query);
        QueryNode node = new QueryParser().parse(query);
        log.debug("Parsed {}", node);
        return node;
    }
}
