package org.hertsig.database;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.skife.jdbi.v2.BuiltInArgumentFactory;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlStatementCustomizer;
import org.skife.jdbi.v2.sqlobject.SqlStatementCustomizerFactory;
import org.skife.jdbi.v2.sqlobject.SqlStatementCustomizingAnnotation;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@Retention(RetentionPolicy.RUNTIME)
@SqlStatementCustomizingAnnotation(UseBetterBeanMapper.MapAsBeanFactory.class)
@Target(ElementType.METHOD)
public @interface UseBetterBeanMapper {
    class MapAsBeanFactory implements SqlStatementCustomizerFactory {
        @Override
        public SqlStatementCustomizer createForMethod(Annotation annotation, Class sqlObjectType, Method method) {
            return q -> ((Query) q).registerMapper(new ResultSetMapperFactory() {
                @Override
                public boolean accepts(Class type, StatementContext ctx) {
                    return !BuiltInArgumentFactory.canAccept(type);
                }

                @Override
                public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
                    return new BetterBeanMapper<>(type);
                }
            });
        }

        @Override
        public SqlStatementCustomizer createForType(Annotation annotation, Class sqlObjectType) {
            throw new UnsupportedOperationException("Not allowed on type");
        }

        @Override
        public SqlStatementCustomizer createForParameter(Annotation annotation, Class sqlObjectType, Method method, Object arg) {
            throw new UnsupportedOperationException("Not allowed on parameter");
        }
    }
}
