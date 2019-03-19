package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AjaxResponse<T> {
    private Status status;
    private T payload;

    public AjaxResponse(Status status) {
        this.status = status;
    }

    public AjaxResponse(Status status, T payload) {
        this.status = status;
        this.payload = payload;
    }
}
