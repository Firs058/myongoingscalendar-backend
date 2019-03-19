package org.myongoingscalendar.model;

import lombok.*;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public class PostgresResponse extends Status {
    private boolean isBanned;
    private boolean isActive;
    private Integer id;
    private boolean social;
    private Role role;
    private String nickname;

    @Builder
    public PostgresResponse(int code, String message, boolean isBanned, boolean isActive, Integer id, Boolean social, Role role, String nickname) {
        super(code, message);
        this.isBanned = isBanned;
        this.isActive = isActive;
        this.id = id;
        this.social = social;
        this.role = role;
        this.nickname = nickname;
    }

    @Override
    public int getCode() {
        return super.getCode();
    }

    @Override
    public void setCode(int code) {
        super.setCode(code);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }
}
