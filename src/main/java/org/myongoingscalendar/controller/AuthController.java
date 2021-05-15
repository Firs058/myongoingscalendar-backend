package org.myongoingscalendar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
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
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    @RequestMapping(value = "/login")
    public AjaxResponse<?> createAuthenticationToken(@RequestBody UserEntity user) {
        if (user.email() == null || user.password() == null || user.email().equals(user.password()))
            return new AjaxResponse<>(ResponseStatus.S10022.getStatus());
        return userService.findByEmailContainingIgnoreCase(user.email())
                .map(userEntity -> {
                    if (userEntity.password() == null)
                        return new AjaxResponse<>(ResponseStatus.S10031.getStatus());
                    if (!userEntity.active())
                        return new AjaxResponse<>(ResponseStatus.S10023.getStatus());
                    if (BCrypt.checkpw(user.password(), userEntity.password())) {
                        HashMap<String, Object> tokens = new HashMap<>();
                        Token generatedAccessToken = jwtTokenUtil.generateAccessToken(JwtUserUtil.create(userEntity));
                        Token generatedRefreshToken = jwtTokenUtil.generateRefreshToken(JwtUserUtil.create(userEntity));
                        tokens.put("accessToken", generatedAccessToken.token());
                        tokens.put("refreshToken", generatedRefreshToken.token());
                        tokens.put("expires_in", generatedAccessToken.expires_in());

                        userEntity.refreshTokens(jwtTokenUtil.collectRefreshTokens(userEntity.refreshTokens(), generatedRefreshToken, null));

                        userService.save(userEntity);

                        return new AjaxResponse<>(
                                ResponseStatus.S11010.getStatus(),
                                new LoginStatus()
                                        .email(user.email())
                                        .social(userEntity.social() != SNS.local)
                                        .tokens(tokens)
                                        .roles(userEntity.authorityEntities().stream().map(UserAuthorityEntity::authorityName).toList())
                                        .settings(userEntity.userSettingsEntity()));
                    } else
                        return new AjaxResponse<>(ResponseStatus.S10024.getStatus());
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10024.getStatus()));
    }

    @Transactional
    @RequestMapping(value = "/refresh")
    public AjaxResponse<?> refreshAndGetAuthenticationToken(@RequestBody Token inputToken) {
        final String token = inputToken.token();
        Long id = jwtTokenUtil.getJwtUserIdFromToken(token);
        return userService.get(id)
                .map(userEntity -> {
                    List<Token> tokenList = new ArrayList<>();
                    if (userEntity.refreshTokens() != null)
                        tokenList = new ObjectMapper().convertValue(userEntity.refreshTokens(), new TypeReference<>() {
                        });
                    if (tokenList.stream().anyMatch(refreshToken -> refreshToken.token().equals(token) && new Date().getTime() / 1000 < refreshToken.expires_in())) {
                        HashMap<String, Object> tokens = new HashMap<>();
                        Token generatedAccessToken = jwtTokenUtil.generateAccessToken(JwtUserUtil.create(userEntity));
                        Token refreshedToken = jwtTokenUtil.refreshToken(token);
                        tokens.put("accessToken", generatedAccessToken.token());
                        tokens.put("refreshToken", refreshedToken.token());
                        tokens.put("expires_in", generatedAccessToken.expires_in());

                        userEntity.refreshTokens(jwtTokenUtil.collectRefreshTokens(userEntity.refreshTokens(), refreshedToken, inputToken));

                        userService.save(userEntity);

                        return new AjaxResponse<>(
                                ResponseStatus.S11010.getStatus(),
                                new LoginStatus()
                                        .tokens(tokens));
                    }
                    return new AjaxResponse<>(ResponseStatus.S11017.getStatus());
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }
}