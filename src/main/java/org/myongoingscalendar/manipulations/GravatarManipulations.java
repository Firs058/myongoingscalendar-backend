package org.myongoingscalendar.manipulations;


import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;
import de.bripkens.gravatar.Rating;
import org.myongoingscalendar.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class GravatarManipulations {
    private final UserService userService;

    public GravatarManipulations(UserService userService) {
        this.userService = userService;
    }

    public String getGravatarImageUrl(String email) {
        String avatar = new Gravatar()
                .setSize(200)
                .setHttps(true)
                .setRating(Rating.PARENTAL_GUIDANCE_SUGGESTED)
                .setStandardDefaultImage(DefaultImage.MONSTER)
                .getUrl(email);
        userService.findByEmailContainingIgnoreCase(email).ifPresent(u -> {
            u.userSettingsEntity().avatar(avatar);
            userService.save(u);
        });
        return avatar;
    }
}
