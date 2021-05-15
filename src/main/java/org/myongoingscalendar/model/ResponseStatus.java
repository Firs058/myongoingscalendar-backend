package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author firs
 */
public enum ResponseStatus {
    S10000(new Status(10000, "Connection problems")),
    S10001(new Status(10001, "Empty fields are not allowed")),
    S10002(new Status(10002, "Password cannot contain less than 8 character")),
    S10003(new Status(10003, "Did you forgot about captcha?")),
    S10004(new Status(10004, "The length of the nickname does not match the requirements")),
    S10005(new Status(10005, "Only latin text and numbers allowed")),
    S10006(new Status(10006, "This nickname is prohibited")),
    S10007(new Status(10007, "Sorry, account with that email is already existed")),
    S10008(new Status(10008, "Invalid captcha")),
    S10009(new Status(10009, "Url is not relevant")),
    S10010(new Status(10010, "Token not exists")),
    S10011(new Status(10011, "The length of the nickname does not match the requirements")),
    S10012(new Status(10012, "You must be logged")),
    S10013(new Status(10013, "Sorry, account with that email not existed")),
    S10014(new Status(10014, "Token not exists. Repeat recover")),
    S10015(new Status(10015, "One of our services does not work. Do not worry, we'll fix it soon")),
    S10016(new Status(10016, "Server error. What you expect?")),
    S10017(new Status(10017, "Sorry, user not exists")),
    S10018(new Status(10018, "Not found")),
    S10019(new Status(10019, "Not admin")),
    S10020(new Status(10020, "Invalid timezone")),
    S10021(new Status(10021, "Wrong secret state")),
    S10022(new Status(10022, "The query conditions are not met")),
    S10023(new Status(10023, "Sorry, you account not activate yet. Check you email")),
    S10024(new Status(10024, "Sorry, wrong email or password")),
    S10025(new Status(10025, "Your account has muted, you can't add a comment")),
    S10026(new Status(10026, "Can't add comment")),
    S10027(new Status(10027, "Why null comment?")),
    S10028(new Status(10028, "Already liked")),
    S10029(new Status(10029, "Already disliked")),
    S10030(new Status(10030, "Already reported")),
    S10031(new Status(10031, "Access is available only through a social network")),
    S10032(new Status(10032, "Password recovery is not available for accounts created through a social network")),
    S10033(new Status(10033, "You can't interact with finished anime")),
    S10034(new Status(10034, "Wrong file")),
    S10035(new Status(10035, "You are already logged in, please log out first")),
    S10036(new Status(10036, "Error while logging in via social network")),
    S10037(new Status(10037, "Email not verified")),
    S10038(new Status(10038, "Score must be between 1 and 10")),
    S10039(new Status(10039, "First add this title to your calendar")),
    S11000(new Status(11000, "OK")),
    S11001(new Status(11001, "OK, check you mail to activate account")),
    S11002(new Status(11002, "Account successful activated")),
    S11003(new Status(11003, "OK, check you mail for recover you password")),
    S11004(new Status(11004, "Need change password!")),
    S11005(new Status(11005, "Password changed")),
    S11006(new Status(11006, "Nickname changed")),
    S11007(new Status(11007, "Title removed")),
    S11008(new Status(11008, "Title added")),
    S11009(new Status(11009, "Settings saved")),
    S11010(new Status(11010, "Successful login")),
    S11011(new Status(11011, "Comment added")),
    S11012(new Status(11012, "Like added")),
    S11013(new Status(11013, "Dislike added")),
    S11014(new Status(11014, "Thanks for report")),
    S11015(new Status(11015, "Successful exit")),
    S11016(new Status(11016, "Address copied to clipboard")),
    S11017(new Status(11017, "Need re login")),
    S11018(new Status(11018, "Thanks for feedback!")),
    S11019(new Status(11019, "Avatar saved")),
    S11020(new Status(11020, "Avatar removed")),
    S11021(new Status(11021, "Added to favorites")),
    S11022(new Status(11022, "Removed from favorites")),
    S11023(new Status(11023, "Score added")),
    S11024(new Status(11024, "Score updated")),
    S11025(new Status(11025, "Score removed"));

    private final Status status;

    ResponseStatus(Status status) {
        this.status = status;
    }

    @JsonValue
    public Status getStatus() {
        return status;
    }
}
