package org.myongoingscalendar.model;

import javax.servlet.http.HttpServletRequest;


public class UrlDataImpl implements UrlDataDAO {
    @Override
    public UrlData getUrlData(HttpServletRequest request) {
        return new UrlData(
                request.getHeader("x-forwarded-proto"),
                request.getHeader("x-forwarded-host"),
                request.getHeader("x-forwarded-port")
        );
    }
}
