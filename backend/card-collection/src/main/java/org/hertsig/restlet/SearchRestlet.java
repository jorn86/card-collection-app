package org.hertsig.restlet;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.parser.QueryNode;
import org.hertsig.parser.QueryParser;
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
        try {
            QueryNode node = new QueryParser().parse(query);
            log.debug("Parsed {}", node);
            return node;
        }
        catch (ParsingException e) {
            throw new HttpRequestException(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }
}
