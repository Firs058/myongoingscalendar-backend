package org.myongoingscalendar.controller;


import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.entity.FeedbackEntity;
import org.myongoingscalendar.manipulations.ReCaptchaManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.entity.UserSettingsEntity;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping(value = "/api/user", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class UserController {

    private final UserService userService;
    private final UserServiceCustom userServiceCustom;
    private final OngoingServiceCustom ongoingServiceCustom;
    private final ElasticAnimeService elasticAnimeService;
    private final FeedbackService feedbackService;
    private final ReCaptchaManipulations reCaptchaManipulations;

    @Autowired
    public UserController(UserService userService, UserServiceCustom userServiceCustom, OngoingServiceCustom ongoingServiceCustom, ElasticAnimeService elasticAnimeService, FeedbackService feedbackService, ReCaptchaManipulations reCaptchaManipulations) {
        this.userService = userService;
        this.userServiceCustom = userServiceCustom;
        this.ongoingServiceCustom = ongoingServiceCustom;
        this.elasticAnimeService = elasticAnimeService;
        this.feedbackService = feedbackService;
        this.reCaptchaManipulations = reCaptchaManipulations;
    }

    @RequestMapping(value = "/calendar")
    public AjaxResponse returnMyOngoings(@RequestBody UserSettingsEntity userSettingsEntity, @AuthenticationPrincipal JwtUser user, Locale locale) {
        return new AjaxResponse<>(
                userSettingsEntity.hideRepeats()
                        ? ongoingServiceCustom.getUserOngoingsMin(userSettingsEntity.timezone(), user.getId(), locale)
                        : ongoingServiceCustom.getUserOngoingsFull(userSettingsEntity.timezone(), user.getId(), locale)
        );
    }

    @RequestMapping(value = "/title/{tid}")
    public AjaxResponse returnTitleData(@PathVariable("tid") Long tid, @RequestBody InputUserValues inputUserValues, @AuthenticationPrincipal JwtUser user, Locale locale) {
        return new AjaxResponse<>(ongoingServiceCustom.getUserOngoingData(tid, inputUserValues.getTimezone(), user.getId(), locale));
    }

    @RequestMapping(value = "/title/{tid}/toggle")
    public AjaxResponse returnToggleTitleStatus(@PathVariable("tid") Long tid, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse(userServiceCustom.toggleUserTid(tid, user.getId()));
    }

    @RequestMapping(value = "/title/list")
    public AjaxResponse returnTitlesList(@AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(elasticAnimeService.getUserCurrentOngoingsList(user.getId()));
    }

    @RequestMapping(value = "/es/autocomplete")
    public AjaxResponse setESAutocomplete(@RequestBody ElasticQuery elasticQuery, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(elasticAnimeService.autocompleteForUser(elasticQuery, 12, user.getId()));
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