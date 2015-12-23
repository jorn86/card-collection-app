package org.hertsig.database;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.Getter;
import org.skife.jdbi.v2.IDBI;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

public class MockDbi {
    private final LoadingCache<Class<?>, Object> cache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Object>() {
        @Override
        public Object load(Class<?> key) throws Exception {
            return mock(key);
        }
    });

    @Getter private final IDBI dbi = mock(IDBI.class); {
        when(dbi.open(any())).thenAnswer(invocationOnMock -> cache.get((Class<Object>) invocationOnMock.getArguments()[0]));
    }

    public <T> T getMockedDao(Class<T> type) {
        try {
            return (T) cache.get(type);
        }
        catch (UncheckedExecutionException | ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public void clearCache() {
        cache.invalidateAll();
    }
}
