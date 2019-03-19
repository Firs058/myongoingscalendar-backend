package org.myongoingscalendar.controller;

import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;
import de.bripkens.gravatar.Rating;
import org.myongoingscalendar.elastic.FillElastic;
import org.myongoingscalendar.manipulations.ParseAniDBManipulations;
import org.myongoingscalendar.manipulations.ParseMALManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.AbstractMap;

@RestController
@RolesAllowed("ROLE_ADMIN")
@RequestMapping(value = "/api/admin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class AdminController {
    private final ParseAniDBManipulations parseAniDBManipulations;
    private final ParseMALManipulations parseMALManipulations;
    private final FillElastic fillElastic;
    private final OngoingService ongoingService;
    private final UserService userService;

    @Autowired
    public AdminController(ParseAniDBManipulations parseAniDBManipulations, ParseMALManipulations parseMALManipulations, FillElastic fillElastic, OngoingService ongoingService, UserService userService) {
        this.parseAniDBManipulations = parseAniDBManipulations;
        this.parseMALManipulations = parseMALManipulations;
        this.fillElastic = fillElastic;
        this.ongoingService = ongoingService;
        this.userService = userService;
    }

    @PostMapping("/hex")
    public AjaxResponse findHEX() {
        parseAniDBManipulations.findHEXAndFillTable();
        return new AjaxResponse<>(new Status(11000, "OK"));
    }

    @PostMapping("/elastic")
    public AjaxResponse fillElastic() {
        fillElastic.loadAnimeIntoElastic();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>(new Status(11000, "OK"));
    }

    @PostMapping("/data")
    public AjaxResponse getAdminData() {
        return new AjaxResponse<>(
                new Status(11000, "OK"),
                ongoingService.getAdminData()
        );
    }

    @PostMapping("/update")
    public AjaxResponse updateOngoing(@RequestBody AdminData adminData, @AuthenticationPrincipal JwtUser user) {
        return ongoingService.findByTid(adminData.tid())
                .map(o -> {
                    if (adminData.aid() != null) o.aid(adminData.aid());
                    if (adminData.malid() != null) o.malid(adminData.malid());
                    ongoingService.save(o);
                    return new AjaxResponse<>(new Status(11000, "OK"));
                })
                .orElse(new AjaxResponse<>(new Status(10016, "Server error. What you expect?")));
    }

    @PostMapping("/avatars")
    public AjaxResponse updateAllUsersAvatars() {
        userService.findByUserSettingsEntity_AvatarIsNull().stream()
                .map(userEntity -> new AbstractMap.SimpleImmutableEntry<>(userEntity.email(), new Gravatar()
                        .setSize(200)
                        .setHttps(true)
                        .setRating(Rating.PARENTAL_GUIDANCE_SUGGESTED)
                        .setStandardDefaultImage(DefaultImage.MONSTER)
                        .getUrl(userEntity.email())))
                .forEach(e -> userService.findByEmail(e.getValue()).ifPresent(u -> {
                    u.userSettingsEntity().avatar(e.getKey());
                    userService.save(u);
                }));
        return new AjaxResponse<>(new Status(11000, "OK"));
    }

    @PostMapping("/mal")
    public AjaxResponse forceParseMAL() {
        parseMALManipulations.parseMAL();
        ongoingService.clearOngoingsCache();
        return new AjaxResponse<>(new Status(11000, "OK"));
    }
}