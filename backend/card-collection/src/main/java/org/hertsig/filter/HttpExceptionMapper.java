package org.hertsig.filter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;
import org.hertsig.user.HttpRequestException;

@Slf4j
@Provider
public class HttpExceptionMapper implements ExceptionMapper<HttpRequestException> {
    @Override
    public Response toResponse(HttpRequestException exception) {
        return Response.status(exception.getStatus())
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
    }
}
