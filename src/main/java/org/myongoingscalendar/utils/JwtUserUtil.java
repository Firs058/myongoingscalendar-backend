package org.myongoingscalendar.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.security.JwtUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class JwtUserUtil {

    private JwtUserUtil() {
    }

    public static JwtUser create(UserEntity user) {
        return new JwtUser(
                user.id(),
                user.userSettingsEntity().nickname(),
                null,
                null,
                user.email(),
                user.password(),
                mapToGrantedAuthorities(user.authorityEntities()),
                true,
                user.lastPasswordResetDate()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<UserAuthorityEntity> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name().name()))
                .collect(Collectors.toList());
    }
}
