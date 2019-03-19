package org.myongoingscalendar.social.google;



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
        "url",
        "isDefault"
})
public class Image {

    @JsonProperty("url")
    private String url;
    @JsonProperty("isDefault")
    private boolean isDefault;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Image() {
    }

    /**
     *
     * @param isDefault
     * @param url
     */
    public Image(String url, boolean isDefault) {
        super();
        this.url = url;
        this.isDefault = isDefault;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public Image withUrl(String url) {
        this.url = url;
        return this;
    }

    @JsonProperty("isDefault")
    public boolean isIsDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Image withIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
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

    public Image withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}