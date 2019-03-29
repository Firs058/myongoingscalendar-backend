package org.myongoingscalendar.controller;

import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.CommentServiceCustom;
import org.myongoingscalendar.service.GenreService;
import org.myongoingscalendar.service.OngoingServiceCustom;
import org.myongoingscalendar.service.SyoboiInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final DBManipulations dbManipulations;

    @Autowired
    public ApiController(ElasticAnimeService elasticAnimeService, OngoingServiceCustom ongoingServiceCustom, SyoboiInfoService syoboiInfoService, GenreService genreService, CommentServiceCustom commentServiceCustom, DBManipulations dbManipulations) {
        this.elasticAnimeService = elasticAnimeService;
        this.ongoingServiceCustom = ongoingServiceCustom;
        this.syoboiInfoService = syoboiInfoService;
        this.genreService = genreService;
        this.commentServiceCustom = commentServiceCustom;
        this.dbManipulations = dbManipulations;
    }

    @RequestMapping(value = "/calendar")
    public AjaxResponse returnAllOngoings(@RequestBody UserTimezone userTimezone, Locale locale) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                ongoingServiceCustom.getOngoingsFull(userTimezone.getUserTimezone(), locale)
        );
    }

    @RequestMapping(value = "/calendar_min")
    public AjaxResponse returnAllOngoingsMin(@RequestBody UserTimezone userTimezone, Locale locale) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                ongoingServiceCustom.getOngoingsMin(userTimezone.getUserTimezone(), locale)
        );
    }

    @RequestMapping(value = "/title/{tid}")
    public AjaxResponse returnTitleData(@PathVariable("tid") Long tid, @RequestBody InputUserValues inputUserValues, Locale locale) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                ongoingServiceCustom.getOngoingData(tid, inputUserValues.getTimezone(), locale)
        );
    }

    @RequestMapping(value = "/title/{tid}/comments/{path}/{offset}")
    public AjaxResponse getUserComments(@PathVariable("tid") Long tid, @PathVariable("path") String path, @PathVariable("offset") Integer offset, @AuthenticationPrincipal JwtUser user) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                commentServiceCustom.getComments(tid, path, offset)
        );
    }

    @RequestMapping(value = "/title/list")
    public AjaxResponse returnTitlesList() {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                elasticAnimeService.getCurrentOngoingsList()
        );
    }

    @RequestMapping(value = "/es/supply")
    public AjaxResponse returnAllGenres() {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                new ElasticInfo(genreService.getAll(), syoboiInfoService.getYearsRanges(), new int[]{0, 10})
        );
    }

    @RequestMapping(value = "/es/{tid}")
    public AjaxResponse setESTidSearch(@PathVariable("tid") Long tid) {
        return elasticAnimeService.findByTid(tid)
                .map(r -> new AjaxResponse<>(new Status(11000, "OK"), r))
                .orElse(new AjaxResponse<>(new Status(10018, "Not found")));

    }

    @RequestMapping(value = "/es/autocomplete")
    public AjaxResponse setESAutocomplete(@RequestBody ElasticQuery elasticQuery) {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                elasticAnimeService.autocomplete(elasticQuery, 12)
        );
    }

    @RequestMapping("/timezones")
    public AjaxResponse getAllTimezones() {
        return new AjaxResponse<>(new Status(11000, "OK"), dbManipulations.getAllTimezones());
    }
}

