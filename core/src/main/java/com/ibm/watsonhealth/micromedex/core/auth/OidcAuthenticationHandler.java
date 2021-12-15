package com.ibm.watsonhealth.micromedex.core.auth;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.oak.spi.security.authentication.credentials.CredentialsSupport;
import org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider;
import org.apache.jackrabbit.oak.spi.security.authentication.token.TokenConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.AuthenticationSupport;
import org.apache.sling.auth.core.spi.AuthenticationFeedbackHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.discovery.TopologyEvent;
import org.apache.sling.discovery.TopologyEventListener;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.day.crx.security.token.TokenCookie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watsonhealth.micromedex.core.auth.conf.OidcAuthenticationHandlerConfig;
import com.ibm.watsonhealth.micromedex.core.auth.userinfo.UserInfo;
import com.ibm.watsonhealth.micromedex.core.auth.util.HttpSession;
import com.ibm.watsonhealth.micromedex.core.constants.GlobalConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = { AuthenticationHandler.class, AuthenticationFeedbackHandler.class, TopologyEventListener.class }, property = {
  "service.description=OpenID Connect Authentication Handler" }, configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true)
@Designate(ocd = OidcAuthenticationHandlerConfig.class, factory = true)
public class OidcAuthenticationHandler implements AuthenticationHandler, AuthenticationFeedbackHandler, TopologyEventListener {

    private static final int SECURITY_TOKEN_BITS = 130;
    private static final int SECURITY_TOKEN_RADIX = 32;

    public static final String ATTR_SECURITY_TOKEN = "security_token";
    public static final String ATTR_URL = "url";

    public static final String PARAM_STATE = "state";
    public static final String PARAM_AUTHORIZATION_CODE = "code";
    public static final String RESP_TYPE_AUTH_CODE_FLOW = "code";

    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private OidcAuthenticationHandlerConfig config;

    @Reference
    private SlingSettingsService settings;

    @Reference(target = "(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
    private ResourceBundleProvider resourceBundleProvider;

    private ServiceRegistration<?> idpReg;

    private CloseableHttpClient httpClient;

    private String clusterId;
    private String repositoryId;

    private String callbackSuffix;

    @Activate
    protected void activate(final ComponentContext context, final OidcAuthenticationHandlerConfig config) {
        try {
            this.config = config;

            if (!this.allEndpointsHttps()) {
                this.config = null;
                throw new IllegalStateException("Authorization endpoint and token endpoint must use https");
            }

            // register identity provider
            final OidcIdentityProvider idp = new OidcIdentityProvider(this.config.idp_name());
            this.idpReg = context
              .getBundleContext()
              .registerService(new String[] { ExternalIdentityProvider.class.getName(), CredentialsSupport.class.getName() }, idp, null);

            this.httpClient = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();

            this.initializeRepositoryId();

            this.callbackSuffix = this.getCallbackSuffix();
        } catch (final Exception ex) {
            log.error("Error activating", ex);
            throw ex;
        }
    }

    @SuppressWarnings("PMD.NullAssignment")
    @Deactivate
    public void deactivate() {
        try {
            if (this.idpReg != null) {
                this.idpReg.unregister();
                this.idpReg = null;
            }
        } catch (final Exception ex) {
            log.error("Error unregistering identity provider", ex);
        }

        try {
            if (this.httpClient != null) {
                this.httpClient.close();
                this.httpClient = null;
            }
        } catch (final Exception ex) {
            log.error("Error closing http client", ex);
        }
    }

    @Override
    public AuthenticationInfo extractCredentials(final HttpServletRequest request, final HttpServletResponse response) {
        if (StringUtils.endsWith(request.getRequestURI(), this.callbackSuffix)) {
            try {
                // validate state to mitigate request forgery
                final HttpSession session = HttpSession.getSession(request, response, false);
                final String sessionSecurityToken = session == null ? null : (String) session.removeAttribute(ATTR_SECURITY_TOKEN);
                final String state = request.getParameter(PARAM_STATE);
                final String stateSecurityToken = this.getStateAttribute(state, ATTR_SECURITY_TOKEN);
                if (StringUtils.isBlank(sessionSecurityToken) || StringUtils.isBlank(stateSecurityToken) || !StringUtils.equals(
                  sessionSecurityToken, stateSecurityToken)) {
                    log.warn("Invalid security token: session={} <> state={}", sessionSecurityToken, stateSecurityToken);
                    request.setAttribute(FAILURE_REASON, StringUtils.lowerCase(Reason.INVALID_OIDC_SECURITY_TOKEN.toString(), Locale.getDefault()));
                    return AuthenticationInfo.FAIL_AUTH;
                }

                final String code = request.getParameter(PARAM_AUTHORIZATION_CODE);
                final SendTokenResult tokenResult = this.sendTokenRequest(code);
                if (StringUtils.isNotBlank(tokenResult.getFailureReson())) {
                    request.setAttribute(FAILURE_REASON, tokenResult.getFailureReson());
                }
                return tokenResult.getAuthenticationInfo();
            } catch (final IOException | JSONException | JwtException ex) {
                log.error("Error requesting id token", ex);
                request.setAttribute(FAILURE_REASON, StringUtils.lowerCase(Reason.REQUESTING_OIDC_TOKEN_FAILED.toString(), Locale.getDefault()));
                return AuthenticationInfo.FAIL_AUTH;
            }
        }

        return null;
    }

    @NotNull
    private SendTokenResult sendTokenRequest(final String code) throws IOException, JSONException {
        final HttpPost tokenPost = new HttpPost(this.config.tokenEndpoint());
        final List<NameValuePair> tokenForm = new ArrayList<>();
        tokenForm.add(new BasicNameValuePair("grant_type", GRANT_TYPE_AUTHORIZATION_CODE));
        tokenForm.add(new BasicNameValuePair("code", code));
        tokenForm.add(new BasicNameValuePair("redirect_uri", this.config.callbackUrl()));
        tokenForm.add(new BasicNameValuePair("client_id", this.config.clientId()));
        tokenForm.add(new BasicNameValuePair("client_secret", this.config.clientSecret()));
        tokenForm.add(new BasicNameValuePair("scope", this.config.scope()));
        tokenPost.setEntity(new UrlEncodedFormEntity(tokenForm));
        try (final CloseableHttpResponse tokenResponse = this.httpClient.execute(tokenPost)) {

            if (tokenResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final String entity = EntityUtils.toString(tokenResponse.getEntity(), StandardCharsets.UTF_8);
                final JSONObject entityAsJson = new JSONObject(entity);
                final String idToken = entityAsJson.getString("id_token");

                //Authorization
                final Optional<UserInfo> userInfo = this.getUserInfo(entityAsJson.getString("access_token"));
                if (!userInfo.isPresent() || !userInfo.get().isInGroup(this.config.blueGroupNames())) {
                    return new SendTokenResult(
                      AuthenticationInfo.FAIL_AUTH, StringUtils.lowerCase(Reason.INVALID_AUTHORIZATION.toString(), Locale.getDefault()));
                }

                // we can trust the id token because we received it over https
                final String unsignedIdToken = StringUtils.substringBeforeLast(idToken, ".") + ".";

                final Claims claims = Jwts.parserBuilder().build().parseClaimsJwt(unsignedIdToken).getBody();
                final String subject = claims.getSubject();

                final OidcCredentials credentials = new OidcCredentials(subject, claims, this.config.idp_name());
                credentials.setAttribute(TokenConstants.TOKEN_ATTRIBUTE, ""); // indicate that a login-token needs to be generated
                final AuthenticationInfo info = new AuthenticationInfo("OIDC");
                info.put(JcrResourceConstants.AUTHENTICATION_INFO_CREDENTIALS, credentials);
                return new SendTokenResult(info, "");
            } else {
                log.warn("Invalid id token response: status={}", tokenResponse.getStatusLine().getStatusCode());
                return new SendTokenResult(
                  AuthenticationInfo.FAIL_AUTH, StringUtils.lowerCase(Reason.REQUESTING_OIDC_TOKEN_FAILED.toString(), Locale.getDefault()));
            }
        }
    }

    @Override
    public boolean requestCredentials(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Object failureReason = request.getAttribute(FAILURE_REASON);
        if (failureReason != null) {
            final String reason = String.valueOf(failureReason);
            if (StringUtils.equals(reason, "session_timed_out")) {
                log.warn("Session timed out");
            } else if (Reason.contains(reason)) {
                response.sendRedirect(String.format(this.config.errorRedirectUrl(), reason));
                return false;
            } else {
                log.warn("Unknown failure reason: {}", reason);
                return false;
            }
        }

        if (StringUtils.endsWith(request.getRequestURI(), this.callbackSuffix)) {
            return false;
        }

        log.trace("requesting credentials for {}", request.getRequestURI());

        // create state to mitigate request forgery
        // don't create a new security token if login was already initiated in other tab
        final HttpSession session = HttpSession.getSession(request, response);
        final String securityToken;
        if (session.hasAttribute(ATTR_SECURITY_TOKEN)) {
            securityToken = (String) session.getAttribute(ATTR_SECURITY_TOKEN);
        } else {
            securityToken = new BigInteger(SECURITY_TOKEN_BITS, new SecureRandom()).toString(SECURITY_TOKEN_RADIX);
            session.setAttribute(ATTR_SECURITY_TOKEN, securityToken);
        }

        if (this.config.httpRedirect()) {
            try {
                final String url = this.getFullUrl(request);
                final String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.name());
                final String state = String.format(ATTR_SECURITY_TOKEN + "=%s&" + ATTR_URL + "=%s", securityToken, encodedUrl);
                final String target = new URIBuilder(this.config.authorizationEndpoint())
                  .addParameter("client_id", this.config.clientId())
                  .addParameter("response_type", RESP_TYPE_AUTH_CODE_FLOW)
                  .addParameter("redirect_uri", this.config.callbackUrl())
                  .addParameter("scope", this.config.scope())
                  .addParameter(PARAM_STATE, state)
                  .toString();

                response.sendRedirect(target);

                return true;
            } catch (final URISyntaxException ex) {
                log.error("Error requesting credentials", ex);
                return false;
            }
        } else {
            if (!StringUtils.startsWith(request.getRequestURI(), GlobalConstants.LOGIN_RESOURCE_PATH)) {
                response.sendRedirect(GlobalConstants.LOGIN_PAGE_PATH);
            }
            return true;
        }
    }

    @Override
    public void dropCredentials(final HttpServletRequest request, final HttpServletResponse response) {
        // session cookie is already cleared by TokenAuthenticationHandler
    }

    @Override
    public void authenticationFailed(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationInfo authInfo) {
        HttpSession.endSession(request, response);
    }

    @Override
    public boolean authenticationSucceeded(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationInfo authInfo) {
        try {
            this.updateLoginTokenCookie(request, response, authInfo);

            HttpSession.endSession(request, response);

            String redirectUrl = this.config.defaultRedirectUrl();
            final String state = request.getParameter(PARAM_STATE);
            final String stateUrl = this.getStateAttribute(state, ATTR_URL);
            if (AuthUtil.isRedirectValid(request, stateUrl)) {
                redirectUrl = stateUrl;
            }

            response.sendRedirect(redirectUrl);

            return true;
        } catch (final IOException ex) {
            log.error("Error redirecting user", ex);
            return false;
        }
    }

    @Override
    public void handleTopologyEvent(final TopologyEvent event) {
        if (event.getType() == TopologyEvent.Type.TOPOLOGY_CHANGED || event.getType() == TopologyEvent.Type.TOPOLOGY_INIT) {
            this.clusterId = event.getNewView().getLocalInstance().getClusterView().getId();
            this.initializeRepositoryId();
        }
    }

    private void initializeRepositoryId() {
        String id = this.clusterId;
        if (id == null) {
            id = this.settings.getSlingId();
            if (id == null) {
                id = UUID.randomUUID().toString();
                log.warn("Failure to acquire unique ID for this authenticator. Using random UUID {}", id);
            }
        }
        this.repositoryId = id;
    }

    private boolean allEndpointsHttps() {
        return StringUtils.startsWith(this.config.authorizationEndpoint(), "https://") && StringUtils.startsWith(
          this.config.tokenEndpoint(), "https://");
    }

    private String getCallbackSuffix() {
        if (StringUtils.countMatches(this.config.callbackUrl(), "/") < 3) {
            throw new IllegalStateException(String.format("Callback URL not valid: %s", this.config.callbackUrl()));
        }
        return StringUtils.substring(this.config.callbackUrl(), StringUtils.ordinalIndexOf(this.config.callbackUrl(), "/", 3));
    }

    private String getFullUrl(final HttpServletRequest request) {
        final StringBuilder builder = new StringBuilder(request.getRequestURI());

        final String queryString = request.getQueryString();
        if (queryString != null) {
            builder.append('?').append(queryString);
        }

        return builder.toString();
    }

    private String getStateAttribute(final String state, final String attribute) {
        final List<NameValuePair> stateParams = URLEncodedUtils.parse(state, StandardCharsets.UTF_8);
        for (final NameValuePair stateParam : stateParams) {
            if (StringUtils.equals(stateParam.getName(), attribute)) {
                return stateParam.getValue();
            }
        }
        return null;
    }

    private void updateLoginTokenCookie(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationInfo authInfo) {
        final OidcCredentials creds = (OidcCredentials) authInfo.get(JcrResourceConstants.AUTHENTICATION_INFO_CREDENTIALS);
        final String token = (String) creds.getAttribute(TokenConstants.TOKEN_ATTRIBUTE);
        final String repositoryIdFromToken = this.getRepositoryId(token);
        final String workspace = this.getWorkspace(request);
        TokenCookie.update(request, response, repositoryIdFromToken, token, workspace, true);
    }

    private String getRepositoryId(final String token) {
        if (this.isEncapsulatedToken(token)) {
            return "login";
        } else {
            return this.repositoryId;
        }
    }

    @SuppressWarnings("java:S109")
    private boolean isEncapsulatedToken(final String token) {
        return token != null && StringUtils.countMatches(token, ".") == 2 && token.startsWith("ey");
    }

    private String getWorkspace(final HttpServletRequest request) {
        final Object resourceResolver = request.getAttribute(AuthenticationSupport.REQUEST_ATTRIBUTE_RESOLVER);
        if (resourceResolver instanceof ResourceResolver) {
            final Session session = ((ResourceResolver) resourceResolver).adaptTo(Session.class);
            if (session != null) {
                return session.getWorkspace().getName();
            }
        }
        return null;
    }

    private Optional<UserInfo> getUserInfo(final String accessToken) throws IOException {
        final HttpPost userInfoRequest = new HttpPost(this.config.userInfoUrl());
        userInfoRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        userInfoRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        final HttpResponse userInfoResponse = this.httpClient.execute(userInfoRequest);
        if (userInfoResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            final String userInfoEntity = EntityUtils.toString(userInfoResponse.getEntity(), StandardCharsets.UTF_8);
            return Optional.of(OBJECT_MAPPER.readValue(userInfoEntity, UserInfo.class));
        }
        return Optional.empty();
    }

    public String getSingleSignOnRedirectUrl(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        String result = StringUtils.EMPTY;

        result += this.config.authorizationEndpoint();
        result += '?';
        result += "response_type=";
        result += RESP_TYPE_AUTH_CODE_FLOW;
        result += '&';
        result += "client_id=";
        result += this.config.clientId();
        result += '&';
        result += "redirect_uri=";
        result += this.config.callbackUrl();
        result += '&';
        result += "scope=";
        result += this.config.scope();
        result += '&';
        result += "state=";
        result += ATTR_SECURITY_TOKEN + "=" + this.getSecurityToken(request, response);
        return result;
    }

    private String getSecurityToken(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        final String result;
        final HttpSession session = HttpSession.getSession(request, response);
        if (session.hasAttribute(ATTR_SECURITY_TOKEN)) {
            result = (String) session.getAttribute(ATTR_SECURITY_TOKEN);
        } else {
            result = new BigInteger(SECURITY_TOKEN_BITS, new SecureRandom()).toString(SECURITY_TOKEN_RADIX);
            session.setAttribute(ATTR_SECURITY_TOKEN, result);
        }
        return result;
    }

}
