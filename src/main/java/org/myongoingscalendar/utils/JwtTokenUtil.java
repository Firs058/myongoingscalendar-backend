package org.myongoingscalendar.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.extern.slf4j.Slf4j;
import org.myongoingscalendar.model.Token;
import org.myongoingscalendar.security.JwtUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;
    private Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access}")
    private Long expirationAccess;
    @Value("${jwt.expiration.refresh}")
    private Long expirationRefresh;

    public JwtUser getJwtUserFromToken(String token) {
        return new JwtUser(
                getJwtUserIdFromToken(token),
                null,
                null,
                null,
                null,
                null,
                Arrays.stream(getAllClaimsFromToken(token).get("authorities").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()),
                true,
                null);
    }

    public Long getJwtUserIdFromToken(String token) {
        return Long.valueOf(getClaimFromToken(token, Claims::getSubject));
    }

    private Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public Token generateAccessToken(JwtUser jwtUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", jwtUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        return doGenerateTokenAndExpiration(claims, jwtUser.getId(), expirationAccess);
    }

    public Token generateRefreshToken(JwtUser jwtUser) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateTokenAndExpiration(claims, jwtUser.getId(), expirationRefresh);
    }

    private Token doGenerateTokenAndExpiration(Map<String, Object> claims, Long id, Long expiration) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate, expiration);

        return new Token()
                .token(Jwts.builder()
                        .setClaims(claims)
                        .setSubject(id.toString())
                        .setIssuedAt(createdDate)
                        .setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS512, secret)
                        .compact())
                .expires_in(expirationDate.getTime() / 1000);
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public Token refreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate, expirationRefresh);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return new Token()
                .token(Jwts.builder()
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS512, secret)
                        .compact())
                .expires_in(expirationDate.getTime() / 1000);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final Long username = getJwtUserIdFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (
                username.equals(user.getId())
                        && !isTokenExpired(token)
                        && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }

    private Date calculateExpirationDate(Date createdDate, Long expiration) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

    public JsonNode collectRefreshTokens(JsonNode refreshTokens, Token token, Token old) {
        try {
            List<Token> tokenList = new ArrayList<>();
            if (refreshTokens != null)

                tokenList = new ObjectMapper().readValue(String.valueOf(refreshTokens), new TypeReference<List<Token>>() {
                });

            if (tokenList.size() > 9)
                tokenList.stream().min(Comparator.comparing(Token::expires_in)).map(tokenList::remove);

            if (old != null)
                tokenList.stream().filter(e -> e.token().equals(old.token())).findFirst().map(tokenList::remove);

            tokenList.add(token);

            return JacksonUtil.toJsonNode(new ObjectMapper().writeValueAsString(tokenList));

        } catch (IOException e) {
            log.error("Can't collect refreshToken: " + token, e);
        }
        return refreshTokens;
    }
}