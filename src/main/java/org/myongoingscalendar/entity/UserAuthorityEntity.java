package org.myongoingscalendar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.model.AuthorityName;

import javax.persistence.*;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "users_authorities")
public class UserAuthorityEntity {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "users_authorities_seq", sequenceName = "users_authorities_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_authorities_seq")
    private Long id;
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityName name;
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
}