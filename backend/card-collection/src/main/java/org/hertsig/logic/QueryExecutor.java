package org.hertsig.logic;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.database.BetterBeanMapper;
import org.hertsig.dto.SearchCard;
import org.hertsig.query.DbiQueryCreator;
import org.hertsig.query.QueryNode;
import org.hertsig.query.QueryParser;
import org.hertsig.query.QueryWithArguments;
import org.hertsig.user.HttpRequestException;
import org.parboiled.errors.ParsingException;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.Query;
import scala.collection.JavaConversions;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Singleton
public class QueryExecutor {
    private final IDBI dbi;

    @Inject
    public QueryExecutor(IDBI dbi) {
        this.dbi = dbi;
    }

    public List<SearchCard> executeQuery(String query, UUID userId) {
        QueryWithArguments parsedQuery = DbiQueryCreator.toPostgres(parse(query), userId != null);
        try (Handle handle = dbi.open()) {
            Query<Map<String, Object>> jdbiQuery = handle.createQuery(parsedQuery.query());
            if (userId != null) {
                jdbiQuery.bind("userid", userId);
            }
            JavaConversions.mapAsJavaMap(parsedQuery.values()).forEach(jdbiQuery::bind);
            return jdbiQuery.map(new BetterBeanMapper<>(SearchCard.class)).list();
        }
    }

    public static QueryNode parse(String query) {
        try {
            return new QueryParser().parse(query);
        }
        catch (ParsingException e) {
            throw new HttpRequestException(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }
}
