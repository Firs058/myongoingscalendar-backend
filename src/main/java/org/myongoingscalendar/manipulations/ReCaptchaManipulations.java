package org.myongoingscalendar.manipulations;


import org.myongoingscalendar.model.ReCaptchaGoogleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class ReCaptchaManipulations {
    @Value("${recaptcha.validation.url}")
    private String reCaptchaApiUrl;
    @Value("${recaptcha.validation.secretKey}")
    private String secretKey;

    private RestTemplate restTemplate = new RestTemplate();

    public ReCaptchaGoogleResponse verify(String recaptchaResponse) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", recaptchaResponse);
        return restTemplate.postForObject(reCaptchaApiUrl, form, ReCaptchaGoogleResponse.class);
    }
}
