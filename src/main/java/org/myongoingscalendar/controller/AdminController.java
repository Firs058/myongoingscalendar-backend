package org.myongoingscalendar.controller;

import org.myongoingscalendar.elastic.FillElastic;
import org.myongoingscalendar.manipulations.ParseAniDBManipulations;
import org.myongoingscalendar.manipulations.ParseAnnManipulations;
import org.myongoingscalendar.manipulations.ParseMALManipulations;
import org.myongoingscalendar.manipulations.ParseSyoboiManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/admin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class AdminController {
    private final ParseAniDBManipulations parseAniDBManipulations;
    private final ParseMALManipulations parseMALManipulations;
    private final ParseAnnManipulations parseAnnManipulations;
    private final ParseSyoboiManipulations parseSyoboiManipulations;
    private final FillElastic fillElastic;
    private final OngoingService ongoingService;

    @Autowired
    public AdminController(ParseAniDBManipulations parseAniDBManipulations, ParseMALManipulations parseMALManipulations, ParseAnnManipulations parseAnnManipulations, ParseSyoboiManipulations parseSyoboiManipulations, FillElastic fillElastic, OngoingService ongoingService) {
        this.parseAniDBManipulations = parseAniDBManipulations;
        this.parseMALManipulations = parseMALManipulations;
        this.parseAnnManipulations = parseAnnManipulations;
        this.parseSyoboiManipulations = parseSyoboiManipulations;
        this.fillElastic = fillElastic;
        this.ongoingService = ongoingService;
    }

    @PostMapping("/hex")
    public AjaxResponse findHEX() {
        parseAniDBManipulations.findHEXAndFillTable();
        return new AjaxResponse<>();
    }

    @PostMapping("/elastic")
    public AjaxResponse fillElastic() {
        fillElastic.loadAnimeIntoElastic();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }

    @PostMapping("/data")
    public AjaxResponse getAdminData() {
        return new AjaxResponse<>(ongoingService.getAdminData());
    }

    @PostMapping("/update")
    public AjaxResponse updateOngoing(@RequestBody AdminData adminData) {
        return ongoingService.findByTid(adminData.tid())
                .map(o -> {
                    if (adminData.aid() != null) o.aid(adminData.aid());
                    if (adminData.malid() != null) o.malid(adminData.malid());
                    if (adminData.annid() != null) o.annid(adminData.annid());
                    ongoingService.save(o);
                    return new AjaxResponse<>();
                })
                .orElse(new AjaxResponse<>(new Status(10016, "Server error. What you expect?")));
    }

    @PostMapping("/mal")
    public AjaxResponse forceParseMALForCurrentOngoings() {
        parseMALManipulations.parseMALForCurrentOngoings();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }

    @PostMapping("/ann")
    public AjaxResponse forceParseAnnForCurrentOngoings() {
        parseAnnManipulations.parseAnnForCurrentOngoings();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }

    @PostMapping("/mal/all")
    public AjaxResponse forceParseMALForAll() {
        parseMALManipulations.parseMALForAll();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }

    @PostMapping("/anidb")
    public AjaxResponse forceParseAniDBForCurrentOngoings() {
        parseAniDBManipulations.parseAniDBForCurrentOngoings();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }

    @PostMapping("/anidb/all")
    public AjaxResponse forceParseAniDBForAll() {
        parseAniDBManipulations.parseAniDBForAll();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }

    @PostMapping("/syoboi")
    public AjaxResponse forceParseSyoboiForCurrentOngoings() {
        parseSyoboiManipulations.parseSyoboiAnimeOngoingsList();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>();
    }
}