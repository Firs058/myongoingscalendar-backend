package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AjaxResponse<T> {
    static final Status DEFAULT_STATUS = ResponseStatus.S11000.getStatus();

    private Status status;
    private T payload;

    public AjaxResponse(Status status) {
        this.status = status;
    }

    public AjaxResponse(Status status, T payload) {
        this.status = status;
        this.payload = payload;
    }

    public AjaxResponse(T payload) {
        this.status = DEFAULT_STATUS;
        this.payload = payload;
    }

    public AjaxResponse() {
        this.status = DEFAULT_STATUS;
    }
}
