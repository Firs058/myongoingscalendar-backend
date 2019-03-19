package org.myongoingscalendar.social.twitter;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "id_str",
        "name",
        "screen_name",
        "location",
        "description",
        "url",
        "entities",
        "protected",
        "followers_count",
        "friends_count",
        "listed_count",
        "created_at",
        "favourites_count",
        "utc_offset",
        "time_zone",
        "geo_enabled",
        "verified",
        "statuses_count",
        "lang",
        "contributors_enabled",
        "is_translator",
        "is_translation_enabled",
        "profile_background_color",
        "profile_background_image_url",
        "profile_background_image_url_https",
        "profile_background_tile",
        "profile_image_url",
        "profile_image_url_https",
        "profile_link_color",
        "profile_sidebar_border_color",
        "profile_sidebar_fill_color",
        "profile_text_color",
        "profile_use_background_image",
        "has_extended_profile",
        "default_profile",
        "default_profile_image",
        "following",
        "follow_request_sent",
        "notifications",
        "translator_type",
        "email"
})
public class TwitterUser {

    @JsonProperty("id")
    private String id;
    @JsonProperty("id_str")
    private String idStr;
    @JsonProperty("name")
    private String name;
    @JsonProperty("screen_name")
    private String screenName;
    @JsonProperty("location")
    private String location;
    @JsonProperty("description")
    private String description;
    @JsonProperty("url")
    private Object url;
    @JsonProperty("entities")
    private Entities entities;
    @JsonProperty("protected")
    private boolean _protected;
    @JsonProperty("followers_count")
    private int followersCount;
    @JsonProperty("friends_count")
    private int friendsCount;
    @JsonProperty("listed_count")
    private int listedCount;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("favourites_count")
    private int favouritesCount;
    @JsonProperty("utc_offset")
    private Object utcOffset;
    @JsonProperty("time_zone")
    private Object timeZone;
    @JsonProperty("geo_enabled")
    private boolean geoEnabled;
    @JsonProperty("verified")
    private boolean verified;
    @JsonProperty("statuses_count")
    private int statusesCount;
    @JsonProperty("lang")
    private String lang;
    @JsonProperty("contributors_enabled")
    private boolean contributorsEnabled;
    @JsonProperty("is_translator")
    private boolean isTranslator;
    @JsonProperty("is_translation_enabled")
    private boolean isTranslationEnabled;
    @JsonProperty("profile_background_color")
    private String profileBackgroundColor;
    @JsonProperty("profile_background_image_url")
    private Object profileBackgroundImageUrl;
    @JsonProperty("profile_background_image_url_https")
    private Object profileBackgroundImageUrlHttps;
    @JsonProperty("profile_background_tile")
    private boolean profileBackgroundTile;
    @JsonProperty("profile_image_url")
    private String profileImageUrl;
    @JsonProperty("profile_image_url_https")
    private String profileImageUrlHttps;
    @JsonProperty("profile_link_color")
    private String profileLinkColor;
    @JsonProperty("profile_sidebar_border_color")
    private String profileSidebarBorderColor;
    @JsonProperty("profile_sidebar_fill_color")
    private String profileSidebarFillColor;
    @JsonProperty("profile_text_color")
    private String profileTextColor;
    @JsonProperty("profile_use_background_image")
    private boolean profileUseBackgroundImage;
    @JsonProperty("has_extended_profile")
    private boolean hasExtendedProfile;
    @JsonProperty("default_profile")
    private boolean defaultProfile;
    @JsonProperty("default_profile_image")
    private boolean defaultProfileImage;
    @JsonProperty("following")
    private boolean following;
    @JsonProperty("follow_request_sent")
    private boolean followRequestSent;
    @JsonProperty("notifications")
    private boolean notifications;
    @JsonProperty("translator_type")
    private String translatorType;
    @JsonProperty("email")
    private String email;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public TwitterUser() {
    }

    /**
     *
     * @param isTranslator
     * @param isTranslationEnabled
     * @param friendsCount
     * @param profileUseBackgroundImage
     * @param profileBackgroundColor
     * @param followRequestSent
     * @param location
     * @param favouritesCount
     * @param screenName
     * @param hasExtendedProfile
     * @param profileImageUrl
     * @param timeZone
     * @param profileSidebarBorderColor
     * @param defaultProfileImage
     * @param lang
     * @param id
     * @param following
     * @param profileSidebarFillColor
     * @param description
     * @param createdAt
     * @param verified
     * @param name
     * @param contributorsEnabled
     * @param profileBackgroundImageUrlHttps
     * @param profileImageUrlHttps
     * @param listedCount
     * @param geoEnabled
     * @param entities
     * @param _protected
     * @param url
     * @param idStr
     * @param profileBackgroundTile
     * @param profileLinkColor
     * @param translatorType
     * @param email
     * @param notifications
     * @param followersCount
     * @param utcOffset
     * @param profileTextColor
     * @param profileBackgroundImageUrl
     * @param statusesCount
     * @param defaultProfile
     */
    public TwitterUser(String id, String idStr, String name, String screenName, String location, String description, Object url, Entities entities, boolean _protected, int followersCount, int friendsCount, int listedCount, String createdAt, int favouritesCount, Object utcOffset, Object timeZone, boolean geoEnabled, boolean verified, int statusesCount, String lang, boolean contributorsEnabled, boolean isTranslator, boolean isTranslationEnabled, String profileBackgroundColor, Object profileBackgroundImageUrl, Object profileBackgroundImageUrlHttps, boolean profileBackgroundTile, String profileImageUrl, String profileImageUrlHttps, String profileLinkColor, String profileSidebarBorderColor, String profileSidebarFillColor, String profileTextColor, boolean profileUseBackgroundImage, boolean hasExtendedProfile, boolean defaultProfile, boolean defaultProfileImage, boolean following, boolean followRequestSent, boolean notifications, String translatorType, String email) {
        super();
        this.id = id;
        this.idStr = idStr;
        this.name = name;
        this.screenName = screenName;
        this.location = location;
        this.description = description;
        this.url = url;
        this.entities = entities;
        this._protected = _protected;
        this.followersCount = followersCount;
        this.friendsCount = friendsCount;
        this.listedCount = listedCount;
        this.createdAt = createdAt;
        this.favouritesCount = favouritesCount;
        this.utcOffset = utcOffset;
        this.timeZone = timeZone;
        this.geoEnabled = geoEnabled;
        this.verified = verified;
        this.statusesCount = statusesCount;
        this.lang = lang;
        this.contributorsEnabled = contributorsEnabled;
        this.isTranslator = isTranslator;
        this.isTranslationEnabled = isTranslationEnabled;
        this.profileBackgroundColor = profileBackgroundColor;
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
        this.profileBackgroundTile = profileBackgroundTile;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlHttps = profileImageUrlHttps;
        this.profileLinkColor = profileLinkColor;
        this.profileSidebarBorderColor = profileSidebarBorderColor;
        this.profileSidebarFillColor = profileSidebarFillColor;
        this.profileTextColor = profileTextColor;
        this.profileUseBackgroundImage = profileUseBackgroundImage;
        this.hasExtendedProfile = hasExtendedProfile;
        this.defaultProfile = defaultProfile;
        this.defaultProfileImage = defaultProfileImage;
        this.following = following;
        this.followRequestSent = followRequestSent;
        this.notifications = notifications;
        this.translatorType = translatorType;
        this.email = email;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public TwitterUser withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("id_str")
    public String getIdStr() {
        return idStr;
    }

    @JsonProperty("id_str")
    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public TwitterUser withIdStr(String idStr) {
        this.idStr = idStr;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public TwitterUser withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("screen_name")
    public String getScreenName() {
        return screenName;
    }

    @JsonProperty("screen_name")
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public TwitterUser withScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    public TwitterUser withLocation(String location) {
        this.location = location;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public TwitterUser withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("url")
    public Object getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(Object url) {
        this.url = url;
    }

    public TwitterUser withUrl(Object url) {
        this.url = url;
        return this;
    }

    @JsonProperty("entities")
    public Entities getEntities() {
        return entities;
    }

    @JsonProperty("entities")
    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public TwitterUser withEntities(Entities entities) {
        this.entities = entities;
        return this;
    }

    @JsonProperty("protected")
    public boolean isProtected() {
        return _protected;
    }

    @JsonProperty("protected")
    public void setProtected(boolean _protected) {
        this._protected = _protected;
    }

    public TwitterUser withProtected(boolean _protected) {
        this._protected = _protected;
        return this;
    }

    @JsonProperty("followers_count")
    public int getFollowersCount() {
        return followersCount;
    }

    @JsonProperty("followers_count")
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public TwitterUser withFollowersCount(int followersCount) {
        this.followersCount = followersCount;
        return this;
    }

    @JsonProperty("friends_count")
    public int getFriendsCount() {
        return friendsCount;
    }

    @JsonProperty("friends_count")
    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public TwitterUser withFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
        return this;
    }

    @JsonProperty("listed_count")
    public int getListedCount() {
        return listedCount;
    }

    @JsonProperty("listed_count")
    public void setListedCount(int listedCount) {
        this.listedCount = listedCount;
    }

    public TwitterUser withListedCount(int listedCount) {
        this.listedCount = listedCount;
        return this;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public TwitterUser withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @JsonProperty("favourites_count")
    public int getFavouritesCount() {
        return favouritesCount;
    }

    @JsonProperty("favourites_count")
    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public TwitterUser withFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
        return this;
    }

    @JsonProperty("utc_offset")
    public Object getUtcOffset() {
        return utcOffset;
    }

    @JsonProperty("utc_offset")
    public void setUtcOffset(Object utcOffset) {
        this.utcOffset = utcOffset;
    }

    public TwitterUser withUtcOffset(Object utcOffset) {
        this.utcOffset = utcOffset;
        return this;
    }

    @JsonProperty("time_zone")
    public Object getTimeZone() {
        return timeZone;
    }

    @JsonProperty("time_zone")
    public void setTimeZone(Object timeZone) {
        this.timeZone = timeZone;
    }

    public TwitterUser withTimeZone(Object timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    @JsonProperty("geo_enabled")
    public boolean isGeoEnabled() {
        return geoEnabled;
    }

    @JsonProperty("geo_enabled")
    public void setGeoEnabled(boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
    }

    public TwitterUser withGeoEnabled(boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
        return this;
    }

    @JsonProperty("verified")
    public boolean isVerified() {
        return verified;
    }

    @JsonProperty("verified")
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public TwitterUser withVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    @JsonProperty("statuses_count")
    public int getStatusesCount() {
        return statusesCount;
    }

    @JsonProperty("statuses_count")
    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public TwitterUser withStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
        return this;
    }

    @JsonProperty("lang")
    public String getLang() {
        return lang;
    }

    @JsonProperty("lang")
    public void setLang(String lang) {
        this.lang = lang;
    }

    public TwitterUser withLang(String lang) {
        this.lang = lang;
        return this;
    }

    @JsonProperty("contributors_enabled")
    public boolean isContributorsEnabled() {
        return contributorsEnabled;
    }

    @JsonProperty("contributors_enabled")
    public void setContributorsEnabled(boolean contributorsEnabled) {
        this.contributorsEnabled = contributorsEnabled;
    }

    public TwitterUser withContributorsEnabled(boolean contributorsEnabled) {
        this.contributorsEnabled = contributorsEnabled;
        return this;
    }

    @JsonProperty("is_translator")
    public boolean isIsTranslator() {
        return isTranslator;
    }

    @JsonProperty("is_translator")
    public void setIsTranslator(boolean isTranslator) {
        this.isTranslator = isTranslator;
    }

    public TwitterUser withIsTranslator(boolean isTranslator) {
        this.isTranslator = isTranslator;
        return this;
    }

    @JsonProperty("is_translation_enabled")
    public boolean isIsTranslationEnabled() {
        return isTranslationEnabled;
    }

    @JsonProperty("is_translation_enabled")
    public void setIsTranslationEnabled(boolean isTranslationEnabled) {
        this.isTranslationEnabled = isTranslationEnabled;
    }

    public TwitterUser withIsTranslationEnabled(boolean isTranslationEnabled) {
        this.isTranslationEnabled = isTranslationEnabled;
        return this;
    }

    @JsonProperty("profile_background_color")
    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    @JsonProperty("profile_background_color")
    public void setProfileBackgroundColor(String profileBackgroundColor) {
        this.profileBackgroundColor = profileBackgroundColor;
    }

    public TwitterUser withProfileBackgroundColor(String profileBackgroundColor) {
        this.profileBackgroundColor = profileBackgroundColor;
        return this;
    }

    @JsonProperty("profile_background_image_url")
    public Object getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    @JsonProperty("profile_background_image_url")
    public void setProfileBackgroundImageUrl(Object profileBackgroundImageUrl) {
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
    }

    public TwitterUser withProfileBackgroundImageUrl(Object profileBackgroundImageUrl) {
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
        return this;
    }

    @JsonProperty("profile_background_image_url_https")
    public Object getProfileBackgroundImageUrlHttps() {
        return profileBackgroundImageUrlHttps;
    }

    @JsonProperty("profile_background_image_url_https")
    public void setProfileBackgroundImageUrlHttps(Object profileBackgroundImageUrlHttps) {
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
    }

    public TwitterUser withProfileBackgroundImageUrlHttps(Object profileBackgroundImageUrlHttps) {
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
        return this;
    }

    @JsonProperty("profile_background_tile")
    public boolean isProfileBackgroundTile() {
        return profileBackgroundTile;
    }

    @JsonProperty("profile_background_tile")
    public void setProfileBackgroundTile(boolean profileBackgroundTile) {
        this.profileBackgroundTile = profileBackgroundTile;
    }

    public TwitterUser withProfileBackgroundTile(boolean profileBackgroundTile) {
        this.profileBackgroundTile = profileBackgroundTile;
        return this;
    }

    @JsonProperty("profile_image_url")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @JsonProperty("profile_image_url")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public TwitterUser withProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    @JsonProperty("profile_image_url_https")
    public String getProfileImageUrlHttps() {
        return profileImageUrlHttps;
    }

    @JsonProperty("profile_image_url_https")
    public void setProfileImageUrlHttps(String profileImageUrlHttps) {
        this.profileImageUrlHttps = profileImageUrlHttps;
    }

    public TwitterUser withProfileImageUrlHttps(String profileImageUrlHttps) {
        this.profileImageUrlHttps = profileImageUrlHttps;
        return this;
    }

    @JsonProperty("profile_link_color")
    public String getProfileLinkColor() {
        return profileLinkColor;
    }

    @JsonProperty("profile_link_color")
    public void setProfileLinkColor(String profileLinkColor) {
        this.profileLinkColor = profileLinkColor;
    }

    public TwitterUser withProfileLinkColor(String profileLinkColor) {
        this.profileLinkColor = profileLinkColor;
        return this;
    }

    @JsonProperty("profile_sidebar_border_color")
    public String getProfileSidebarBorderColor() {
        return profileSidebarBorderColor;
    }

    @JsonProperty("profile_sidebar_border_color")
    public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
        this.profileSidebarBorderColor = profileSidebarBorderColor;
    }

    public TwitterUser withProfileSidebarBorderColor(String profileSidebarBorderColor) {
        this.profileSidebarBorderColor = profileSidebarBorderColor;
        return this;
    }

    @JsonProperty("profile_sidebar_fill_color")
    public String getProfileSidebarFillColor() {
        return profileSidebarFillColor;
    }

    @JsonProperty("profile_sidebar_fill_color")
    public void setProfileSidebarFillColor(String profileSidebarFillColor) {
        this.profileSidebarFillColor = profileSidebarFillColor;
    }

    public TwitterUser withProfileSidebarFillColor(String profileSidebarFillColor) {
        this.profileSidebarFillColor = profileSidebarFillColor;
        return this;
    }

    @JsonProperty("profile_text_color")
    public String getProfileTextColor() {
        return profileTextColor;
    }

    @JsonProperty("profile_text_color")
    public void setProfileTextColor(String profileTextColor) {
        this.profileTextColor = profileTextColor;
    }

    public TwitterUser withProfileTextColor(String profileTextColor) {
        this.profileTextColor = profileTextColor;
        return this;
    }

    @JsonProperty("profile_use_background_image")
    public boolean isProfileUseBackgroundImage() {
        return profileUseBackgroundImage;
    }

    @JsonProperty("profile_use_background_image")
    public void setProfileUseBackgroundImage(boolean profileUseBackgroundImage) {
        this.profileUseBackgroundImage = profileUseBackgroundImage;
    }

    public TwitterUser withProfileUseBackgroundImage(boolean profileUseBackgroundImage) {
        this.profileUseBackgroundImage = profileUseBackgroundImage;
        return this;
    }

    @JsonProperty("has_extended_profile")
    public boolean isHasExtendedProfile() {
        return hasExtendedProfile;
    }

    @JsonProperty("has_extended_profile")
    public void setHasExtendedProfile(boolean hasExtendedProfile) {
        this.hasExtendedProfile = hasExtendedProfile;
    }

    public TwitterUser withHasExtendedProfile(boolean hasExtendedProfile) {
        this.hasExtendedProfile = hasExtendedProfile;
        return this;
    }

    @JsonProperty("default_profile")
    public boolean isDefaultProfile() {
        return defaultProfile;
    }

    @JsonProperty("default_profile")
    public void setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public TwitterUser withDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
        return this;
    }

    @JsonProperty("default_profile_image")
    public boolean isDefaultProfileImage() {
        return defaultProfileImage;
    }

    @JsonProperty("default_profile_image")
    public void setDefaultProfileImage(boolean defaultProfileImage) {
        this.defaultProfileImage = defaultProfileImage;
    }

    public TwitterUser withDefaultProfileImage(boolean defaultProfileImage) {
        this.defaultProfileImage = defaultProfileImage;
        return this;
    }

    @JsonProperty("following")
    public boolean isFollowing() {
        return following;
    }

    @JsonProperty("following")
    public void setFollowing(boolean following) {
        this.following = following;
    }

    public TwitterUser withFollowing(boolean following) {
        this.following = following;
        return this;
    }

    @JsonProperty("follow_request_sent")
    public boolean isFollowRequestSent() {
        return followRequestSent;
    }

    @JsonProperty("follow_request_sent")
    public void setFollowRequestSent(boolean followRequestSent) {
        this.followRequestSent = followRequestSent;
    }

    public TwitterUser withFollowRequestSent(boolean followRequestSent) {
        this.followRequestSent = followRequestSent;
        return this;
    }

    @JsonProperty("notifications")
    public boolean isNotifications() {
        return notifications;
    }

    @JsonProperty("notifications")
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public TwitterUser withNotifications(boolean notifications) {
        this.notifications = notifications;
        return this;
    }

    @JsonProperty("translator_type")
    public String getTranslatorType() {
        return translatorType;
    }

    @JsonProperty("translator_type")
    public void setTranslatorType(String translatorType) {
        this.translatorType = translatorType;
    }

    public TwitterUser withTranslatorType(String translatorType) {
        this.translatorType = translatorType;
        return this;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public TwitterUser withEmail(String email) {
        this.email = email;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public TwitterUser withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}