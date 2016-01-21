package org.hertsig.integration;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.dao.SearchDao;
import org.hertsig.database.TestDatabaseResource;
import org.hertsig.dto.SetInfo;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

@Slf4j
public class IntegrationTest {
    @Rule public final TestDatabaseResource databaseResource = new TestDatabaseResource(IntegrationTest.class.getSimpleName());

    @Test
    public void testDatabaseAvailable() {
        List<SetInfo> sets = databaseResource.getDbi().open(SearchDao.class).getSetStatistics();
        assertTrue(sets.isEmpty());
    }
}
