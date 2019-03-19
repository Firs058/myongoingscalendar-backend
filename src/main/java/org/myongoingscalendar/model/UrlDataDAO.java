package org.myongoingscalendar.model;

import javax.servlet.http.HttpServletRequest;

public interface UrlDataDAO {
    UrlData getUrlData(HttpServletRequest request);
}
