package org.myongoingscalendar.social.github;

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
        "email",
        "primary",
        "verified",
        "visibility"
})
public class GithubUser {

    @JsonProperty("email")
    private String email;
    @JsonProperty("primary")
    private boolean primary;
    @JsonProperty("verified")
    private boolean verified;
    @JsonProperty("visibility")
    private String visibility;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public GithubUser() {
    }

    /**
     *
     * @param visibility
     * @param email
     * @param verified
     * @param primary
     */
    public GithubUser(String email, boolean primary, boolean verified, String visibility) {
        super();
        this.email = email;
        this.primary = primary;
        this.verified = verified;
        this.visibility = visibility;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public GithubUser withEmail(String email) {
        this.email = email;
        return this;
    }

    @JsonProperty("primary")
    public boolean isPrimary() {
        return primary;
    }

    @JsonProperty("primary")
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public GithubUser withPrimary(boolean primary) {
        this.primary = primary;
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

    public GithubUser withVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    @JsonProperty("visibility")
    public String getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public GithubUser withVisibility(String visibility) {
        this.visibility = visibility;
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

    public GithubUser withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
