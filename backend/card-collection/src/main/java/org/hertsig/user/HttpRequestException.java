package org.hertsig.user;

import javax.ws.rs.core.Response;

import lombok.Getter;

public class HttpRequestException extends RuntimeException {
    @Getter private final Response.Status status;

    public HttpRequestException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }
}
