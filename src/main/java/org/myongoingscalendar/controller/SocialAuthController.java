package org.myongoingscalendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.myongoingscalendar.entity.UserAuthorityEntity;
import org.myongoingscalendar.model.*;
import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.manipulations.GravatarManipulations;
import org.myongoingscalendar.manipulations.URLManipulations;
import org.myongoingscalendar.model.Token;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.social.github.GithubUser;
import org.myongoingscalendar.social.google.GoogleUser;
import org.myongoingscalendar.social.twitter.TwitterUser;
import org.myongoingscalendar.utils.JwtTokenUtil;
import org.myongoingscalendar.utils.JwtUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
public class SocialAuthController {

    private static final String GOOGLE_PROTECTED_RESOURCE_URL = "https://www.googleapis.com/plus/v1/people/me";
    private static final String FACEBOOK_PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v2.11/me";
    private static final String TWITTER_PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true";
    private static final String GITHUB_PROTECTED_RESOURCE_URL = "https://api.github.com/user/public_emails";
    private static final String SCOPE = "email";
    private static final String GITHUB_SCOPE = "user:email";
    @Value("${auth.google.clientId}")
    private String GOOGLE_CLIENT_ID;
    @Value("${auth.google.clientSecret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${auth.facebook.clientId}")
    private String FACEBOOK_CLIENT_ID;
    @Value("${auth.facebook.clientSecret}")
    private String FACEBOOK_CLIENT_SECRET;
    @Value("${auth.twitter.clientId}")
    private String TWITTER_CLIENT_ID;
    @Value("${auth.twitter.clientSecret}")
    private String TWITTER_CLIENT_SECRET;
    @Value("${auth.github.clientId}")
    private String GITHUB_CLIENT_ID;
    @Value("${auth.github.clientSecret}")
    private String GITHUB_CLIENT_SECRET;
    private OAuth20Service googleService;
    private OAuth20Service facebookService;
    private OAuth10aService twitterService;
    private OAuth20Service githubService;
    private String SECRET_STATE = String.valueOf(new Random().nextInt(999_999));
    private GravatarManipulations gravatarManipulations;
    private final URLManipulations urlManipulations;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public SocialAuthController(GravatarManipulations gravatarManipulations, URLManipulations urlManipulations, UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.gravatarManipulations = gravatarManipulations;
        this.urlManipulations = urlManipulations;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostConstruct
    private void postInit() {
        googleService = new ServiceBuilder(GOOGLE_CLIENT_ID)
                .apiSecret(GOOGLE_CLIENT_SECRET)
                .scope(SCOPE)
                .state(SECRET_STATE)
                .callback(genCallbackAddress("google"))
                .build(GoogleApi20.instance());
        facebookService = new ServiceBuilder(FACEBOOK_CLIENT_ID)
                .apiSecret(FACEBOOK_CLIENT_SECRET)
                .scope(SCOPE)
                .state(SECRET_STATE)
                .callback(genCallbackAddress("facebook"))
                .build(FacebookApi.instance());
        twitterService = new ServiceBuilder(TWITTER_CLIENT_ID)
                .apiSecret(TWITTER_CLIENT_SECRET)
                .state(SECRET_STATE)
                .callback(genCallbackAddress("twitter"))
                .build(TwitterApi.instance());
        githubService = new ServiceBuilder(GITHUB_CLIENT_ID)
                .apiSecret(GITHUB_CLIENT_SECRET)
                .scope(GITHUB_SCOPE)
                .state(SECRET_STATE)
                .callback(genCallbackAddress("github"))
                .build(GitHubApi.instance());
    }

    @RequestMapping(value = "/google")
    public AjaxResponse initPostGoogleAuth(@RequestBody VueSocialAuth20 vueSocialAuth20) throws InterruptedException, ExecutionException, IOException {
        if (!vueSocialAuth20.state().equalsIgnoreCase(SECRET_STATE))
            return new AjaxResponse<>(new Status(10021, "Wrong secret state"));
        else if (vueSocialAuth20.code() == null || vueSocialAuth20.state() == null || vueSocialAuth20.userSettingsEntity() == null)
            return new AjaxResponse<>(new Status(10001, "Empty fields are not allowed"));
        OAuth2AccessToken accessToken = googleService.getAccessToken(vueSocialAuth20.code());
        accessToken = googleService.refreshAccessToken(accessToken.getAccessToken());
        final OAuthRequest request = new OAuthRequest(Verb.GET, GOOGLE_PROTECTED_RESOURCE_URL);
        googleService.signRequest(accessToken, request);
        final Response response = googleService.execute(request);
        final GoogleUser googleUser = new ObjectMapper().readValue(response.getBody(), GoogleUser.class);
        return authUserViaEmail(
                new UserEntity()
                        .email(googleUser.getEmails().get(0).getValue())
                        .social(SNS.google)
                        .userSettingsEntity(vueSocialAuth20.userSettingsEntity())
        );
    }

    @RequestMapping(value = "/google/url")
    public AjaxResponse getGoogleAuthorizationUrl() {
        return new AjaxResponse<>(new Status(11000, "OK"), googleService.getAuthorizationUrl());
    }

    @RequestMapping(value = "/twitter")
    public AjaxResponse initPostTwitterAuth(@RequestBody VueSocialAuth10 vueTwitterSocialAuth) throws InterruptedException, ExecutionException, IOException {
        if (vueTwitterSocialAuth.oauth_token() == null || vueTwitterSocialAuth.oauth_verifier() == null || vueTwitterSocialAuth.userSettingsEntity() == null)
            return new AjaxResponse<>(new Status(10001, "Empty fields are not allowed"));
        final OAuth1RequestToken requestToken = new OAuth1RequestToken(vueTwitterSocialAuth.oauth_token(), vueTwitterSocialAuth.oauth_verifier());
        final OAuth1AccessToken accessToken = twitterService.getAccessToken(requestToken, vueTwitterSocialAuth.oauth_verifier());
        final OAuthRequest request = new OAuthRequest(Verb.GET, TWITTER_PROTECTED_RESOURCE_URL);
        twitterService.signRequest(accessToken, request);
        final Response response = twitterService.execute(request);
        final TwitterUser twitterUser = new ObjectMapper().readValue(response.getBody(), TwitterUser.class);
        return authUserViaEmail(
                new UserEntity()
                        .email(twitterUser.getEmail())
                        .social(SNS.twitter)
                        .userSettingsEntity(vueTwitterSocialAuth.userSettingsEntity())
        );
    }

    @RequestMapping(value = "/twitter/url")
    public AjaxResponse getTwitterAuthorizationUrl() throws InterruptedException, ExecutionException, IOException {
        return new AjaxResponse<>(new Status(11000, "OK"), twitterService.getAuthorizationUrl(twitterService.getRequestToken()));
    }

    @RequestMapping(value = "/github")
    public AjaxResponse initPostGithubAuth(@RequestBody VueSocialAuth20 vueSocialAuth20) throws InterruptedException, ExecutionException, IOException {
        if (!vueSocialAuth20.state().equalsIgnoreCase(SECRET_STATE))
            return new AjaxResponse<>(new Status(10021, "Wrong secret state"));
        else if (vueSocialAuth20.code() == null || vueSocialAuth20.state() == null || vueSocialAuth20.userSettingsEntity() == null)
            return new AjaxResponse<>(new Status(10001, "Empty fields are not allowed"));
        OAuth2AccessToken accessToken = githubService.getAccessToken(vueSocialAuth20.code());
        final OAuthRequest request = new OAuthRequest(Verb.GET, GITHUB_PROTECTED_RESOURCE_URL);
        githubService.signRequest(accessToken, request);
        final Response response = githubService.execute(request);
        final GithubUser[] githubUser = new ObjectMapper().readValue(response.getBody(), GithubUser[].class);
        List<GithubUser> emailsList = Arrays.asList(githubUser);
        return authUserViaEmail(
                new UserEntity()
                        .email(emailsList.get(0).getEmail())
                        .social(SNS.github)
                        .userSettingsEntity(vueSocialAuth20.userSettingsEntity())
        );
    }

    @RequestMapping(value = "/github/url")
    public AjaxResponse getGithubAuthorizationUrl() {
        return new AjaxResponse<>(new Status(11000, "OK"), githubService.getAuthorizationUrl());
    }

    private String genCallbackAddress(String provider) {
        return urlManipulations.makeURL("/auth/" + provider);
    }

    @Transactional
    AjaxResponse authUserViaEmail(UserEntity user) {
        return userService.findByEmail(user.email())
                .map(u -> {
                    u.active(true);
                    user.userSettingsEntity().userEntity(u);
                    return userService.save(u);
                })
                .orElseGet(() -> {
                    user.authorityEntities(Collections.singletonList(new UserAuthorityEntity().authorityName(AuthorityName.ROLE_USER).userEntity(user)));
                    user.userSettingsEntity().nickname(user.email().replaceAll("@.*?$", "").trim());
                    user.userSettingsEntity().userEntity(user);
                    return userService.save(user);
                })
                .map(userEntity -> {
                    HashMap<String, Object> tokens = new HashMap<>();
                    Token generatedAccessToken = jwtTokenUtil.generateAccessToken(JwtUserUtil.create(userEntity));
                    Token generatedRefreshToken = jwtTokenUtil.generateRefreshToken(JwtUserUtil.create(userEntity));
                    tokens.put("accessToken", generatedAccessToken.token());
                    tokens.put("refreshToken", generatedRefreshToken.token());
                    tokens.put("expires_in", generatedAccessToken.expires_in());

                    userEntity.refreshTokens(jwtTokenUtil.collectRefreshTokens(userEntity.refreshTokens(), generatedRefreshToken, null));

                    userService.save(userEntity);

                    return new AjaxResponse<>(
                            new Status(11010, "Successful login"),
                            new LoginStatus()
                                    .email(user.email())
                                    .social(true)
                                    .tokens(tokens)
                                    .roles(userEntity.authorityEntities().stream().map(UserAuthorityEntity::authorityName).collect(Collectors.toList()))
                                    .settings(userEntity.userSettingsEntity().avatar(gravatarManipulations.getGravatarImageUrl(user.email()))));
                })
                .orElseGet(() -> new AjaxResponse<>(new Status(10001, "Empty fields are not allowed")));
    }
}