package org.myongoingscalendar.controller;

import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.entity.UserSettingsEntity;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.myongoingscalendar.manipulations.ImageManipulations;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.model.ResponseStatus;
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
    public AjaxResponse<?> settings(@AuthenticationPrincipal JwtUser user) {
        return userService.get(user.getId())
                .map(u -> new AjaxResponse<>(
                        new LoginStatus()
                                .email(u.email())
                                .social(u.social() != SNS.local)
                                .roles(u.authorityEntities().stream().map(UserAuthorityEntity::authorityName).toList())
                                .settings(u.userSettingsEntity())
                ))
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }

    @Transactional
    @RequestMapping(value = "/save")
    public AjaxResponse<?> syncSettings(@RequestBody UserSettingsEntity userSettingsEntity, @AuthenticationPrincipal JwtUser user) {
        return userService.get(user.getId())
                .map(u -> {
                    u.userSettingsEntity(userSettingsEntity.userEntity(u));
                    userService.save(u);
                    return new AjaxResponse<>(ResponseStatus.S11009.getStatus());
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }

    @Transactional
    @RequestMapping(value = "/nickname/change")
    public AjaxResponse<?> nicknamePass(@RequestBody UserSettingsEntity settings, @AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    if (!settings.nickname().matches("^[a-zA-Z0-9]+$"))
                        return new AjaxResponse<>(ResponseStatus.S10005.getStatus());
                    else if (settings.nickname() == null || settings.nickname().length() <= 3 || settings.nickname().length() >= 21)
                        return new AjaxResponse<>(ResponseStatus.S10011.getStatus());
                    else if (dbManipulations.getAllStopWords().stream().anyMatch(s -> settings.nickname().matches("(?i:.*" + s + ".*)")))
                        return new AjaxResponse<>(ResponseStatus.S10006.getStatus());
                    else {
                        u.userSettingsEntity().nickname(settings.nickname());
                        userService.save(u);
                        return new AjaxResponse<>(ResponseStatus.S10006.getStatus());
                    }
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }

    @Transactional
    @RequestMapping(value = "/pass/change")
    public AjaxResponse<?> changePass(@RequestBody UserEntity user, @AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    if (user.password().length() < 8)
                        return new AjaxResponse<>(ResponseStatus.S10002.getStatus());
                    u.password(BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                    userService.save(u);
                    return new AjaxResponse<>(ResponseStatus.S11005.getStatus());
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }

    @Transactional
    @RequestMapping(value = "/avatar/change")
    public AjaxResponse<?> changeAvatar(@RequestParam MultipartFile avatar, @AuthenticationPrincipal JwtUser jwtUser) {
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
                            return new AjaxResponse<>(ResponseStatus.S11019.getStatus(), image);
                        }
                        return new AjaxResponse<>(ResponseStatus.S10015.getStatus());
                    }
                    return new AjaxResponse<>(ResponseStatus.S10034.getStatus());
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }

    @Transactional
    @RequestMapping(value = "/avatar/remove")
    public AjaxResponse<?> removeAvatar(@AuthenticationPrincipal JwtUser jwtUser) {
        return userService.get(jwtUser.getId())
                .map(u -> {
                    Image avatar = u.userSettingsEntity().avatar();
                    if (avatar != null) {
                        imageManipulations.deleteAvatar(avatar);
                        u.userSettingsEntity().avatar(null);
                    }
                    return new AjaxResponse<>(ResponseStatus.S11020.getStatus());
                })
                .orElse(new AjaxResponse<>(ResponseStatus.S10012.getStatus()));
    }
}