package org.myongoingscalendar.controller;

import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.entity.FeedbackEntity;
import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.manipulations.ReCaptchaManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.entity.UserSettingsEntity;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.FeedbackService;
import org.myongoingscalendar.service.OngoingServiceCustom;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.service.UserServiceCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/user", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class UserController {

    private final UserService userService;
    private final UserServiceCustom userServiceCustom;
    private final DBManipulations dbManipulations;
    private final OngoingServiceCustom ongoingServiceCustom;
    private final ElasticAnimeService elasticAnimeService;
    private final FeedbackService feedbackService;
    private final ReCaptchaManipulations reCaptchaManipulations;

    @Autowired
    public UserController(UserService userService, UserServiceCustom userServiceCustom, DBManipulations dbManipulations, OngoingServiceCustom ongoingServiceCustom, ElasticAnimeService elasticAnimeService, FeedbackService feedbackService, ReCaptchaManipulations reCaptchaManipulations) {
        this.userService = userService;
        this.userServiceCustom = userServiceCustom;
        this.dbManipulations = dbManipulations;
        this.ongoingServiceCustom = ongoingServiceCustom;
        this.elasticAnimeService = elasticAnimeService;
        this.feedbackService = feedbackService;
        this.reCaptchaManipulations = reCaptchaManipulations;
    }

    @RequestMapping(value = "/calendar")
    public AjaxResponse returnMyOngoings(@RequestBody UserSettingsEntity userSettingsEntity, @AuthenticationPrincipal JwtUser user, Locale locale) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                userSettingsEntity.hideRepeats()
                        ? ongoingServiceCustom.getUserOngoingsMin(userSettingsEntity.timezone(), user.getId(), locale)
                        : ongoingServiceCustom.getUserOngoingsFull(userSettingsEntity.timezone(), user.getId(), locale)
        );
    }

    @RequestMapping(value = "/title/{tid}")
    public AjaxResponse returnTitleData(@PathVariable("tid") Long tid, @RequestBody InputUserValues inputUserValues, @AuthenticationPrincipal JwtUser user, Locale locale) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                ongoingServiceCustom.getUserOngoingData(tid, inputUserValues.getTimezone(), user.getId(), locale)
        );
    }

    @RequestMapping(value = "/title/{tid}/toggle")
    public AjaxResponse returnToggleTitleStatus(@PathVariable("tid") Long tid, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse(userServiceCustom.toggleUserTid(tid, user.getId()));
    }

    @RequestMapping(value = "/title/list")
    public AjaxResponse returnTitlesList(@AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                elasticAnimeService.getUserCurrentOngoingsList(user.getId())
        );
    }

    @RequestMapping(value = "/es/autocomplete")
    public AjaxResponse setESAutocomplete(@RequestBody ElasticQuery elasticQuery, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                elasticAnimeService.autocompleteForUser(elasticQuery, 12, user.getId())
        );
    }

    @Transactional
    @RequestMapping("/sync")
    public AjaxResponse settings(@AuthenticationPrincipal JwtUser user) {
        return userService.get(user.getId())
                .map(u -> new AjaxResponse<>(new Status(11000, "OK"),
                        new LoginStatus()
                                .email(u.email())
                                .social(u.social() != SNS.local)
                                .roles(u.authorityEntities().stream().map(UserAuthorityEntity::authorityName).collect(Collectors.toList()))
                                .settings(u.userSettingsEntity())
                ))
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @Transactional
    @RequestMapping(value = "/settings/save")
    public AjaxResponse syncSettings(@RequestBody UserSettingsEntity userSettingsEntity, @AuthenticationPrincipal JwtUser user) {
        return userService.get(user.getId())
                .map(u -> {
                    u.userSettingsEntity(userSettingsEntity.userEntity(u));
                    userService.save(u);
                    return new AjaxResponse<>(new Status(11009, "Settings saved"));
                })
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @Transactional
    @RequestMapping(value = "/nickname/change")
    public AjaxResponse nicknamePass(@RequestBody UserSettingsEntity settings, @AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    if (!settings.nickname().matches("^[a-zA-Z0-9]+$"))
                        return new AjaxResponse<>(new Status(10005, "Only latin text and numbers allowed"));
                    else if (settings.nickname() == null || settings.nickname().length() <= 3 || settings.nickname().length() >= 21)
                        return new AjaxResponse<>(new Status(10011, "The length of the nickname does not match the requirements"));
                    else if (dbManipulations.getAllStopWords().stream().anyMatch(s -> settings.nickname().matches("(?i:.*" + s + ".*)")))
                        return new AjaxResponse<>(new Status(10006, "Hey, what's up with you nickname? Does not fit"));
                    else {
                        u.userSettingsEntity().nickname(settings.nickname());
                        userService.save(u);
                        return new AjaxResponse<>(new Status(11006, "Nickname changed"));
                    }
                })
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @Transactional
    @RequestMapping(value = "/pass/change")
    public AjaxResponse changePass(@RequestBody UserEntity user, @AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    if (user.password().length() < 8)
                        return new AjaxResponse<>(new Status(10002, "Password cannot contain less than 8 character"));
                    u.password(BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                    userService.save(u);
                    return new AjaxResponse<>(new Status(11005, "Password changed"));
                })
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @RequestMapping(value = "/feedback/add")
    public AjaxResponse addFeedback(@RequestBody Feedback feedback, @AuthenticationPrincipal JwtUser user) {
        ReCaptchaGoogleResponse reCaptchaResponse = reCaptchaManipulations.verify(feedback.recaptchaToken());
        if (reCaptchaResponse.isSuccess()) {
            return userService.get(user.getId())
                    .map(u -> {
                        feedbackService.save(new FeedbackEntity().userEntity(u).text(feedback.text()));
                        return new AjaxResponse<>(new Status(11018, "Thanks for feedback!"));
                    })
                    .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
        }
        return new AjaxResponse<>(new Status(10008, "Invalid captcha"));
    }
}