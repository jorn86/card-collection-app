package org.hertsig.filter;

import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class DbiExceptionMapper implements ExceptionMapper<DBIException> {
    @Override
    public Response toResponse(DBIException exception) {
        log.error("Request returned with DBIException", exception);
        return Response.serverError()
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
    }
}
