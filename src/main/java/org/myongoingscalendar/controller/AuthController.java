package org.myongoingscalendar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.manipulations.GravatarManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.utils.JwtTokenUtil;
import org.myongoingscalendar.utils.JwtUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class AuthController {
    private final DBManipulations dbManipulations;
    private final GravatarManipulations gravatarManipulations;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthController(DBManipulations dbManipulations, GravatarManipulations gravatarManipulations, UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.dbManipulations = dbManipulations;
        this.gravatarManipulations = gravatarManipulations;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    @RequestMapping(value = "/login")
    public AjaxResponse createAuthenticationToken(@RequestBody UserEntity user) {
        if (user.email() == null || user.password() == null || user.email().equals(user.password()))
            return new AjaxResponse<>(new Status(10022, "The query conditions are not met"));
        return userService.findByEmailContainingIgnoreCase(user.email())
                .map(userEntity -> {
                    if (userEntity.password() == null)
                        return new AjaxResponse<>(new Status(10031, "Access is available only through a social network"));
                    if (userEntity.active() && BCrypt.checkpw(user.password(), userEntity.password())) {
                        HashMap<String, Object> tokens = new HashMap<>();
                        Token generatedAccessToken = jwtTokenUtil.generateAccessToken(JwtUserUtil.create(userEntity));
                        Token generatedRefreshToken = jwtTokenUtil.generateRefreshToken(JwtUserUtil.create(userEntity));
                        tokens.put("accessToken", generatedAccessToken.token());
                        tokens.put("refreshToken", generatedRefreshToken.token());
                        tokens.put("expires_in", generatedAccessToken.expires_in());

                        userEntity.refreshTokens(jwtTokenUtil.collectRefreshTokens(userEntity.refreshTokens(), generatedRefreshToken, null));

                        userService.save(userEntity);

                        return new AjaxResponse<>(
                                new Status(11010, "Successful login"),
                                new LoginStatus()
                                        .email(user.email())
                                        .social(userEntity.social() != SNS.local)
                                        .tokens(tokens)
                                        .roles(userEntity.authorityEntities().stream().map(UserAuthorityEntity::authorityName).collect(Collectors.toList()))
                                        .settings(userEntity.userSettingsEntity().avatar(gravatarManipulations.getGravatarImageUrl(user.email()))));
                    } else
                        return new AjaxResponse<>(new Status(10023, "Sorry, you account not activate yet. Check you email"));
                })
                .orElse(new AjaxResponse<>(new Status(10024, "Sorry, wrong email or password")));
    }

    @Transactional
    @RequestMapping(value = "/refresh")
    public AjaxResponse refreshAndGetAuthenticationToken(@RequestBody Token inputToken) {
        final String token = inputToken.token();
        Long id = jwtTokenUtil.getJwtUserIdFromToken(token);
        return userService.get(id)
                .map(userEntity -> {
                    List<Token> tokenList = new ArrayList<>();
                    if (userEntity.refreshTokens() != null)
                        tokenList = new ObjectMapper().convertValue(userEntity.refreshTokens(), new TypeReference<List<Token>>() {
                        });
                    if (tokenList.stream().anyMatch(t -> t.token().equals(token) && new Date().getTime() / 1000 < t.expires_in())) {
                        HashMap<String, Object> tokens = new HashMap<>();
                        Token generatedAccessToken = jwtTokenUtil.generateAccessToken(JwtUserUtil.create(userEntity));
                        Token refreshedToken = jwtTokenUtil.refreshToken(token);
                        tokens.put("accessToken", generatedAccessToken.token());
                        tokens.put("refreshToken", refreshedToken.token());
                        tokens.put("expires_in", generatedAccessToken.expires_in());

                        userEntity.refreshTokens(jwtTokenUtil.collectRefreshTokens(userEntity.refreshTokens(), refreshedToken, inputToken));

                        userService.save(userEntity);

                        return new AjaxResponse<>(
                                new Status(11010, "Successful login"),
                                new LoginStatus()
                                        .tokens(tokens));
                    } else if (tokenList.stream().anyMatch(t -> t.token().equals(token) && new Date().getTime() / 1000 > t.expires_in()))
                        return new AjaxResponse<>(new Status(11018, "No need for refresh"));
                    return new AjaxResponse<>(new Status(11017, "Need re login"));
                })
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @RequestMapping("/settings/timezones")
    public AjaxResponse getAllTimezones() {
        return new AjaxResponse<>(new Status(11000, "OK"), dbManipulations.getAllTimezones());
    }
}