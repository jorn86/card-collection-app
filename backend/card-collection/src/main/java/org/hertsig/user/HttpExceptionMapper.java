package org.hertsig.user;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class HttpExceptionMapper implements ExceptionMapper<HttpRequestException> {
    @Override
    public Response toResponse(HttpRequestException e) {
        return Response.status(e.getStatus())
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
    }
}
