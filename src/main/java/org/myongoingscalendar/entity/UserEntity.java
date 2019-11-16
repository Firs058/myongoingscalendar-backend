package org.myongoingscalendar.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.myongoingscalendar.model.SNS;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "id", name = "users_id_uindex"),
        @Index(columnList = "email", name = "users_email_uindex")
})
@TypeDef(
        name = "jsonb-node", typeClass = JsonNodeBinaryType.class
)
public class UserEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long id;
    @JsonProperty("userSettings")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userEntity", orphanRemoval = true)
    private UserSettingsEntity userSettingsEntity;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userEntity", orphanRemoval = true)
    private List<UserTitleEntity> usersTitleEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userEntity", orphanRemoval = true)
    private List<UserTitleDropEntity> userTitleDropEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userEntity", orphanRemoval = true)
    private List<UserAuthorityEntity> authorityEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userEntity", orphanRemoval = true)
    private List<CommentEntity> commentEntities;
    @Column(columnDefinition = "text")
    private String password;
    @Column(columnDefinition = "text", nullable = false)
    private String email;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date", nullable = false, updatable = false)
    private Date registrationDate;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(name = "last_password_reset_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordResetDate;
    @Column(name = "confirm_token", columnDefinition = "text")
    private String confirmToken;
    @Column(name = "recover_token", columnDefinition = "text")
    private String recoverToken;
    @Transient
    private String recaptchaToken;
    @Column(nullable = false)
    private Boolean active = false;
    @Column(nullable = false)
    private Boolean muted = false;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SNS social = SNS.local;
    @Type(type = "jsonb-node")
    @Column(name = "refresh_tokens", columnDefinition = "jsonb")
    private JsonNode refreshTokens;
}
