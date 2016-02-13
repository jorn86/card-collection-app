package org.hertsig.startup;

import org.hertsig.dao.ContentUpgradeDao;
import org.hertsig.dao.SearchDao;
import org.hertsig.database.TestDatabaseResource;
import org.hertsig.dto.Printing;
import org.hertsig.dto.SetInfo;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ContentUpgradeIT {
    @Rule public final TestDatabaseResource databaseResource = new TestDatabaseResource(ContentUpgradeIT.class.getSimpleName());

    @Test
    public void testNoSets() throws StartupActionException {
        contentFromString("{}");
        List<SetInfo> sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(0, sets.size());
    }

    @Test
    public void testEmptySet() {
        contentFromResource("EmptySet.json");
        List<SetInfo> sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(1, sets.size());
        assertEquals(0, sets.get(0).getPrints());
    }

    @Test
    public void testAddCardToSet() {
        contentFromResource("EmptySet.json");
        List<SetInfo> sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(1, sets.size());
        assertEquals(0, sets.get(0).getPrints());

        contentFromResource("SingleSet.json");
        sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(1, sets.size());
        assertEquals(1, sets.get(0).getPrints());
    }

    @Test
    public void testAddNewSet() {
        contentFromResource("SingleSet.json");
        List<SetInfo> sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(1, sets.size());
        assertEquals(1, sets.get(0).getPrints());

        contentFromResource("TwoSetsWithReprint.json");
        sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(2, sets.size());
        assertEquals("Was " + sets.get(0), 1, sets.get(0).getPrints());
        assertEquals("Was " + sets.get(1), 2, sets.get(1).getPrints());
    }

    @Test
    public void testReprint() {
        contentFromResource("TwoSetsWithReprint.json");
        List<SetInfo> sets = query(SearchDao.class, SearchDao::getSetStatistics);
        assertEquals(2, sets.size());
        assertEquals("Was " + sets.get(0), 1, sets.get(0).getPrints());
        assertEquals("Was " + sets.get(0), 1, sets.get(0).getCards());
        assertEquals("Was " + sets.get(1), 2, sets.get(1).getPrints());
        assertEquals("Was " + sets.get(1), 2, sets.get(1).getCards());
        assertEquals("Was " + sets.get(1), 1, sets.get(1).getNewcards());
        assertEquals("Was " + sets.get(1), 1, sets.get(1).getReprints());

        List<Printing> printings = query(ContentUpgradeDao.class, (dao) -> dao.getPrintings(dao.getCard("Card one").getId()));
        assertEquals(2, printings.size());
        assertEquals("Was " + printings.get(0), printings.get(0).getCardid(), printings.get(1).getCardid());
        assertNotEquals(printings.get(0).getSetid(), printings.get(1).getSetid());
    }

    private void refreshSetStatisticsView() {
        try (Connection connection = databaseResource.getDatasource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("REFRESH MATERIALIZED VIEW setstatistics");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends AutoCloseable, R> R query(Class<T> daoType, Function<T, R> query) {
        try (T dao = databaseResource.getDbi().open(daoType)) {
            return query.apply(dao);
        }
        // We need to catch the exception from #close since we don't know here that all DAO's override it
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private ContentUpgrade contentFromString(String content) {
        ContentUpgrade contentUpgrade = run(new ContentUpgrade(databaseResource.getDbi()) {
            @Override
            Reader ensureSetFile() throws IOException {
                return new StringReader(content);
            }
        });
        refreshSetStatisticsView();
        return contentUpgrade;
    }

    private ContentUpgrade contentFromResource(String fileName) {
        ContentUpgrade upgrade = run(new ContentUpgrade(databaseResource.getDbi()) {
            @Override
            Reader ensureSetFile() throws IOException {
                return new InputStreamReader(ContentUpgradeIT.class.getResourceAsStream(fileName));
            }
        });
        refreshSetStatisticsView();
        return upgrade;
    }

    private static ContentUpgrade run(ContentUpgrade contentUpgrade) {
        try {
            contentUpgrade.run();
            return contentUpgrade;
        }
        catch (StartupActionException e) {
            throw new IllegalStateException(e);
        }
    }
}
