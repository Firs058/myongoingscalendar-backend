package org.myongoingscalendar.social.google;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kind",
        "etag",
        "emails",
        "objectType",
        "id",
        "displayName",
        "name",
        "image",
        "isPlusUser",
        "language",
        "verified"
})
public class GoogleUser {

    @JsonProperty("kind")
    private String kind;
    @JsonProperty("etag")
    private String etag;
    @JsonProperty("emails")
    private List<Email> emails = null;
    @JsonProperty("objectType")
    private String objectType;
    @JsonProperty("id")
    private String id;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("name")
    private Name name;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("isPlusUser")
    private boolean isPlusUser;
    @JsonProperty("language")
    private String language;
    @JsonProperty("verified")
    private boolean verified;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public GoogleUser() {
    }

    /**
     *
     * @param id
     * @param etag
     * @param verified
     * @param name
     * @param isPlusUser
     * @param image
     * @param language
     * @param emails
     * @param displayName
     * @param objectType
     * @param kind
     */
    public GoogleUser(String kind, String etag, List<Email> emails, String objectType, String id, String displayName, Name name, Image image, boolean isPlusUser, String language, boolean verified) {
        super();
        this.kind = kind;
        this.etag = etag;
        this.emails = emails;
        this.objectType = objectType;
        this.id = id;
        this.displayName = displayName;
        this.name = name;
        this.image = image;
        this.isPlusUser = isPlusUser;
        this.language = language;
        this.verified = verified;
    }

    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    public GoogleUser withKind(String kind) {
        this.kind = kind;
        return this;
    }

    @JsonProperty("etag")
    public String getEtag() {
        return etag;
    }

    @JsonProperty("etag")
    public void setEtag(String etag) {
        this.etag = etag;
    }

    public GoogleUser withEtag(String etag) {
        this.etag = etag;
        return this;
    }

    @JsonProperty("emails")
    public List<Email> getEmails() {
        return emails;
    }

    @JsonProperty("emails")
    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public GoogleUser withEmails(List<Email> emails) {
        this.emails = emails;
        return this;
    }

    @JsonProperty("objectType")
    public String getObjectType() {
        return objectType;
    }

    @JsonProperty("objectType")
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public GoogleUser withObjectType(String objectType) {
        this.objectType = objectType;
        return this;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public GoogleUser withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GoogleUser withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @JsonProperty("name")
    public Name getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(Name name) {
        this.name = name;
    }

    public GoogleUser withName(Name name) {
        this.name = name;
        return this;
    }

    @JsonProperty("image")
    public Image getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(Image image) {
        this.image = image;
    }

    public GoogleUser withImage(Image image) {
        this.image = image;
        return this;
    }

    @JsonProperty("isPlusUser")
    public boolean isIsPlusUser() {
        return isPlusUser;
    }

    @JsonProperty("isPlusUser")
    public void setIsPlusUser(boolean isPlusUser) {
        this.isPlusUser = isPlusUser;
    }

    public GoogleUser withIsPlusUser(boolean isPlusUser) {
        this.isPlusUser = isPlusUser;
        return this;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    public GoogleUser withLanguage(String language) {
        this.language = language;
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

    public GoogleUser withVerified(boolean verified) {
        this.verified = verified;
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

    public GoogleUser withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
