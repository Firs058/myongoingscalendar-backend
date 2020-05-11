package org.myongoingscalendar.controller;

import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.manipulations.EmailManipulations;
import org.myongoingscalendar.manipulations.ReCaptchaManipulations;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.utils.JwtTokenUtil;
import org.myongoingscalendar.utils.JwtUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class RegistrationController {
    private final ReCaptchaManipulations reCaptchaManipulations;
    private final EmailManipulations emailManipulations;
    private final UrlDataDAO urlDataDAO;
    private final DBManipulations dbManipulations;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public RegistrationController(ReCaptchaManipulations reCaptchaManipulations, EmailManipulations emailManipulations, UrlDataDAO urlDataDAO, DBManipulations dbManipulations, UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.reCaptchaManipulations = reCaptchaManipulations;
        this.emailManipulations = emailManipulations;
        this.urlDataDAO = urlDataDAO;
        this.dbManipulations = dbManipulations;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @RequestMapping(value = "/registration")
    public AjaxResponse newUser(@RequestBody UserEntity user, HttpServletRequest request) {
        if (user.email() == null || user.password() == null) {
            return new AjaxResponse<>(new Status(10001, "Empty fields are not allowed"));
        } else if (user.password().length() <= 7) {
            return new AjaxResponse<>(new Status(10002, "Password cannot contain less than 8 character"));
        } else if (user.recaptchaToken() == null) {
            return new AjaxResponse<>(new Status(10003, "Did you forgot about captcha?"));
        } else if (user.userSettingsEntity().nickname() == null || user.userSettingsEntity().nickname().length() <= 3 || user.userSettingsEntity().nickname().length() >= 21) {
            return new AjaxResponse<>(new Status(10004, "The length of the nickname does not match the requirements"));
        } else if (!user.userSettingsEntity().nickname().matches("^[a-zA-Z0-9]+$")) {
            return new AjaxResponse<>(new Status(10005, "Only latin text and numbers allowed"));
        } else if (dbManipulations.getAllStopWords().stream().anyMatch(s -> user.userSettingsEntity().nickname().matches("(?i:.*" + s + ".*)"))) {
            return new AjaxResponse<>(new Status(10006, "Hey, what's up with you nickname? Does not fit"));
        } else {
            ReCaptchaGoogleResponse reCaptchaResponse = reCaptchaManipulations.verify(user.recaptchaToken());
            if (reCaptchaResponse.isSuccess()) {
                return userService.findByEmailContainingIgnoreCase(user.email())
                        .map(u -> new AjaxResponse<>(new Status(10007, "Sorry, account with that email is already existed")))
                        .orElseGet(() -> {
                            UserEntity userToSave = new UserEntity()
                                    .email(user.email())
                                    .password(BCrypt.hashpw(user.password(), BCrypt.gensalt()))
                                    .confirmToken(UUID.randomUUID().toString());

                            userToSave.userSettingsEntity(user.userSettingsEntity().userEntity(userToSave));
                            userToSave.authorityEntities(Collections.singletonList(new UserAuthorityEntity().authorityName(AuthorityName.ROLE_USER).userEntity(userToSave)));

                            userService.save(userToSave);
                            emailManipulations.sendRegistrationMail(urlDataDAO.getUrlData(request).getDomainAddress(), userToSave);
                            return new AjaxResponse<>(new Status(11001, "OK, check you mail to activate account"));
                        });
            }
            return new AjaxResponse<>(new Status(10008, "Invalid captcha"));
        }
    }

    @RequestMapping(value = "/registration/confirm")
    public AjaxResponse confirmRegistration(@RequestBody Token confirmToken) {
        return userService.findByConfirmToken(confirmToken.token())
                .map(u -> {
                    u.active(true);
                    u.confirmToken(null);
                    userService.save(u);
                    return new AjaxResponse<>(new Status(11002, "Account successful activated"));
                })
                .orElse(new AjaxResponse<>(new Status(10010, "Token not exists")));
    }

    @RequestMapping(value = "/pass/recover")
    public AjaxResponse recoverPass(@RequestBody UserEntity user, HttpServletRequest request) {
        return userService.findByEmailContainingIgnoreCase(user.email())
                .map(u -> {
                    ReCaptchaGoogleResponse reCaptchaResponse = reCaptchaManipulations.verify(user.recaptchaToken());
                    if (!reCaptchaResponse.isSuccess())
                        return new AjaxResponse<>(new Status(10008, "Invalid captcha"));
                    if (u.password() == null)
                        return new AjaxResponse<>(new Status(10032, "Password recovery is not available for accounts created through a social network"));
                    u.recoverToken(UUID.randomUUID().toString());
                    userService.save(u);
                    emailManipulations.sendRecoverPassMail(urlDataDAO.getUrlData(request).getDomainAddress(), u);
                    return new AjaxResponse<>(new Status(11003, "OK, check you mail for recover you password"));
                })
                .orElse(new AjaxResponse<>(new Status(10013, "Sorry, account with that email not existed")));
    }

    @RequestMapping(value = "/pass/recover/confirm")
    public AjaxResponse recoverPass(@RequestBody Token recoverToken) {
        return userService.findByRecoverToken(recoverToken.token())
                .map(userEntity -> {
                    userEntity.recoverToken(null);
                    HashMap<String, Object> tokens = new HashMap<>();
                    Token generatedAccessToken = jwtTokenUtil.generateAccessToken(JwtUserUtil.create(userEntity));
                    tokens.put("accessToken", generatedAccessToken.token());
                    tokens.put("expires_in", generatedAccessToken.expires_in());

                    userService.save(userEntity);
                    return new AjaxResponse<>(
                            new Status(11004, "Need change password!"),
                            new LoginStatus()
                                    .email(userEntity.email())
                                    .social(false)
                                    .tokens(tokens)
                                    .roles(userEntity.authorityEntities().stream().map(UserAuthorityEntity::authorityName).collect(Collectors.toList()))
                                    .settings(userEntity.userSettingsEntity()));
                })
                .orElse(new AjaxResponse<>(new Status(10014, "Token not exists. Repeat recover")));
    }
}
