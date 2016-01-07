package org.hertsig.restlet;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hertsig.user.HttpRequestException;

import javax.ws.rs.core.Response;

class HttpRequestExceptionMatcher extends BaseMatcher<HttpRequestException> {
    private final Response.Status expectedStatus;

    public HttpRequestExceptionMatcher(Response.Status expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    @Override
    public boolean matches(Object item) {
        return item instanceof HttpRequestException && ((HttpRequestException) item).getStatus() == expectedStatus;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(HttpRequestException.class.getSimpleName() + " with status " + expectedStatus);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        super.describeMismatch(item, description);
        if (item instanceof HttpRequestException) {
            description.appendText(" and status " + ((HttpRequestException) item).getStatus());
        }
    }
}
