package org.myongoingscalendar.controller;

import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.entity.UserSettingsEntity;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.manipulations.ImageManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.security.JwtUser;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/user/settings", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class UserSettingsController {

    private final UserService userService;
    private final DBManipulations dbManipulations;
    private final ImageManipulations imageManipulations;

    @Autowired
    public UserSettingsController(UserService userService, DBManipulations dbManipulations, ImageManipulations imageManipulations) {
        this.userService = userService;
        this.dbManipulations = dbManipulations;
        this.imageManipulations = imageManipulations;
    }

    @Transactional
    @RequestMapping("/sync")
    public AjaxResponse settings(@AuthenticationPrincipal JwtUser user) {
        return userService.get(user.getId())
                .map(u -> new AjaxResponse<>(
                        new LoginStatus()
                                .email(u.email())
                                .social(u.social() != SNS.local)
                                .roles(u.authorityEntities().stream().map(UserAuthorityEntity::authorityName).collect(Collectors.toList()))
                                .settings(u.userSettingsEntity())
                ))
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @Transactional
    @RequestMapping(value = "/save")
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

    @Transactional
    @RequestMapping(value = "/avatar/change")
    public AjaxResponse changeAvatar(@RequestParam MultipartFile avatar, @AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    if (imageManipulations.validateAvatar(avatar)) {
                        Image oldAvatar = u.userSettingsEntity().avatar();
                        String name = imageManipulations.saveAvatar(avatar);
                        if (name != null) {
                            Image image = new Image().paths(AnimeUtil.createAvatarPaths(name));
                            if (oldAvatar != null) {
                                imageManipulations.deleteAvatar(oldAvatar);
                            }
                            u.userSettingsEntity().avatar(image);
                            return new AjaxResponse<>(new Status(11019, "Avatar saved"), image);
                        }
                        return new AjaxResponse<>(new Status(10015, "One of our services does not work. Do not worry, we'll fix it soon"));
                    }
                    return new AjaxResponse<>(new Status(11034, "Wrong file"));
                })
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }

    @Transactional
    @RequestMapping(value = "/avatar/remove")
    public AjaxResponse removeAvatar(@AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    Image avatar = u.userSettingsEntity().avatar();
                    if (avatar != null) {
                        imageManipulations.deleteAvatar(avatar);
                        u.userSettingsEntity().avatar(null);
                    }
                    return new AjaxResponse<>(new Status(11020, "Avatar removed"));
                })
                .orElse(new AjaxResponse<>(new Status(10012, "You must be logged")));
    }
}