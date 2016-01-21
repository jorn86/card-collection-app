package org.hertsig.startup;

import org.hertsig.database.TestDatabaseResource;
import org.junit.Rule;
import org.junit.Test;

import java.io.*;

public class ContentUpgradeIT {
    @Rule public final TestDatabaseResource databaseResource = new TestDatabaseResource(ContentUpgradeIT.class.getSimpleName());

    @Test
    public void testNoSets() throws StartupActionException {
        new TestContentUpgradeFromString("{}").run();
    }

    @Test
    public void testEmptySet() throws StartupActionException {
        new TestContentUpgradeFromResource(ContentUpgradeIT.class.getResourceAsStream("TestSets.json"));
    }

    private class TestContentUpgradeFromResource extends ContentUpgrade {
        private InputStream resource;

        public TestContentUpgradeFromResource(InputStream resource) {
            super(ContentUpgradeIT.this.databaseResource.getDbi());
            this.resource = resource;
        }

        @Override
        Reader ensureSetFile() throws IOException {
            return new InputStreamReader(resource);
        }
    }
    private class TestContentUpgradeFromString extends ContentUpgrade {
        private String content;

        public TestContentUpgradeFromString(String content) {
            super(ContentUpgradeIT.this.databaseResource.getDbi());
            this.content = content;
        }

        @Override
        Reader ensureSetFile() throws IOException {
            return new StringReader(content);
        }
    }
}
