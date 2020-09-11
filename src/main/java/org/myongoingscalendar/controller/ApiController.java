package org.myongoingscalendar.controller;

import org.myongoingscalendar.entity.FeedbackEntity;
import org.myongoingscalendar.entity.UserSettingsEntity;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.manipulations.ReCaptchaManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping(value = "/api/public", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class ApiController {

    private final ElasticAnimeService elasticAnimeService;
    private final OngoingServiceCustom ongoingServiceCustom;
    private final SyoboiInfoService syoboiInfoService;
    private final GenreService genreService;
    private final CommentServiceCustom commentServiceCustom;
    private final FeedbackService feedbackService;
    private final DBManipulations dbManipulations;
    private final ReCaptchaManipulations reCaptchaManipulations;

    @Autowired
    public ApiController(ElasticAnimeService elasticAnimeService, OngoingServiceCustom ongoingServiceCustom, SyoboiInfoService syoboiInfoService, GenreService genreService, CommentServiceCustom commentServiceCustom, FeedbackService feedbackService, DBManipulations dbManipulations, ReCaptchaManipulations reCaptchaManipulations) {
        this.elasticAnimeService = elasticAnimeService;
        this.ongoingServiceCustom = ongoingServiceCustom;
        this.syoboiInfoService = syoboiInfoService;
        this.genreService = genreService;
        this.commentServiceCustom = commentServiceCustom;
        this.feedbackService = feedbackService;
        this.dbManipulations = dbManipulations;
        this.reCaptchaManipulations = reCaptchaManipulations;
    }

    @RequestMapping(value = "/calendar")
    public AjaxResponse returnOngoings(@RequestBody UserSettingsEntity userSettingsEntity, Locale locale) {
        return new AjaxResponse<>(
                userSettingsEntity.hideRepeats()
                        ? ongoingServiceCustom.getOngoingsMin(userSettingsEntity.timezone(), locale)
                        : ongoingServiceCustom.getOngoingsFull(userSettingsEntity.timezone(), locale)
        );
    }

    @RequestMapping(value = "/title/{tid}")
    public AjaxResponse returnTitleData(@PathVariable("tid") Long tid, @RequestBody InputUserValues inputUserValues, Locale locale) {
        return new AjaxResponse<>(ongoingServiceCustom.getOngoingData(tid, inputUserValues.getTimezone(), locale));
    }

    @RequestMapping(value = "/title/{tid}/comments/{path}/{offset}")
    public AjaxResponse getUserComments(@PathVariable("tid") Long tid, @PathVariable("path") String path, @PathVariable("offset") Integer offset) {
        return new AjaxResponse<>(commentServiceCustom.getComments(tid, path, offset));
    }

    @RequestMapping(value = "/title/list")
    public AjaxResponse returnTitlesList() {
        return new AjaxResponse<>(elasticAnimeService.getCurrentOngoingsList());
    }

    @RequestMapping(value = "/es/supply")
    public AjaxResponse returnAllGenres() {
        return new AjaxResponse<>(new ElasticInfo(genreService.getAll(), syoboiInfoService.getYearsRanges(), new int[]{0, 10}));
    }

    @RequestMapping(value = "/es/autocomplete")
    public AjaxResponse setESAutocomplete(@RequestBody ElasticQuery elasticQuery) {
        return new AjaxResponse<>(elasticAnimeService.autocomplete(elasticQuery, 12));
    }

    @RequestMapping("/timezones")
    public AjaxResponse getAllTimezones() {
        return new AjaxResponse<>(dbManipulations.getAllTimezones());
    }

    @RequestMapping(value = "/feedback/add")
    public AjaxResponse addFeedback(@RequestBody Feedback feedback) {
        ReCaptchaGoogleResponse reCaptchaResponse = reCaptchaManipulations.verify(feedback.recaptchaToken());
        if (reCaptchaResponse.isSuccess()) {
            feedbackService.save(new FeedbackEntity().userEntity(null).text(feedback.text()));
            return new AjaxResponse<>(new Status(11018, "Thanks for feedback!"));
        }
        return new AjaxResponse<>(new Status(10008, "Invalid captcha"));
    }
}

