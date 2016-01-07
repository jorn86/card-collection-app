package org.hertsig.restlet;

import org.hertsig.dao.SearchDao;
import org.hertsig.database.MockDbi;
import org.junit.Test;

import static org.mockito.Mockito.verify;

public class DatabaseRestletTest {
    private final MockDbi mock = new MockDbi();
    private final DatabaseRestlet restlet = new DatabaseRestlet(mock.getDbi());

    @Test
    public void testSearchEscape() {
        SearchDao mockedDao = mock.getMockedDao(SearchDao.class);

        restlet.searchCards("simple");
        verify(mockedDao).searchCardsByName("simple%");

        restlet.searchCards("With\\Backslash");
        verify(mockedDao).searchCardsByName("With\\Backslash%");

        restlet.searchCards("With%Percent");
        verify(mockedDao).searchCardsByName("With\\%Percent%");

        restlet.searchCards("With_Underscore");
        verify(mockedDao).searchCardsByName("With\\_Underscore%");
    }
}
