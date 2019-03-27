package org.myongoingscalendar.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"id", "userEntity", "modifyDate"})
@Entity
@Table(name = "users_settings")
public class UserSettingsEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "users_settings_seq", sequenceName = "users_settings_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_settings_seq")
    private Long id;
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private UserEntity userEntity;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    private Date modifyDate;
    @Column(columnDefinition = "text", nullable = false)
    private String timezone = "Asia/Tokyo";
    @Column(name = "hide_repeats", nullable = false)
    private Boolean hideRepeats = true;
    @Column(name = "full_time_format", nullable = false)
    private Boolean fullTimeFormat = true;
    @Column(nullable = false)
    private Boolean dark = true;
    @Column(columnDefinition = "text")
    private String avatar;
    @Column(columnDefinition = "text", nullable = false)
    private String nickname = "Anonymous";
    @Column(columnDefinition = "text", nullable = false)
    private Locale lang = Locale.ENGLISH;
}