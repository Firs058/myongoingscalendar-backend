package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlData {
    private String scheme;
    private String host;
    private String port;

    public String getAll(String sub) {
        return (sub != null) ? this.scheme + "://" + this.host + sub : null;
    }

    public String getDomainAddress() {
        return this.scheme + "://" + this.host;
    }
}
