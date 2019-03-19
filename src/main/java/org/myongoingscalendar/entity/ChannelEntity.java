package org.myongoingscalendar.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "channels")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChannelEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "channels_seq", sequenceName = "channels_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channels_seq")
    private Long id;
    @Column(columnDefinition = "text", nullable = false, unique = true)
    private String ja;
    @Column(columnDefinition = "text", unique = true)
    private String en;
}
