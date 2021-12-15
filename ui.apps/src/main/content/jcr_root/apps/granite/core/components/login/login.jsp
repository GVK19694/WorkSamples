<%--

ADOBE CONFIDENTIAL
__________________

Copyright 2015 Adobe
All Rights Reserved.

NOTICE:  All information contained herein is, and remains
the property of Adobe and its suppliers,
if any.  The intellectual and technical concepts contained
herein are proprietary to Adobe and its
suppliers and are protected by trade secret or copyright law.
Dissemination of this information or reproduction of this material
is strictly forbidden unless prior written permission is obtained
from Adobe.
--%>
<%@page session="false" contentType="text/html" pageEncoding="utf-8" import="java.util.Arrays,
                                                                             java.util.Calendar,
                                                                             java.util.HashMap,
                                                                             java.util.Iterator,
                                                                             java.util.List,
                                                                             java.util.Locale,
                                                                             java.util.Map,
                                                                             java.util.ResourceBundle,
                                                                             org.apache.commons.io.IOUtils,
                                                                             org.apache.commons.lang3.StringUtils,
                                                                             org.apache.sling.api.SlingHttpServletRequest,
                                                                             org.apache.sling.api.resource.Resource,
                                                                             org.apache.sling.api.resource.ResourceUtil,
                                                                             org.apache.sling.api.resource.ValueMap,
                                                                             org.apache.sling.auth.core.AuthConstants,
                                                                             org.apache.sling.auth.core.AuthUtil,
                                                                             com.adobe.granite.auth.ims.ImsConfigProvider,
                                                                             com.adobe.granite.license.ProductInfoProvider,
                                                                             com.adobe.granite.security.user.UserManagementService,
                                                                             com.adobe.granite.ui.clientlibs.HtmlLibrary,
                                                                             com.adobe.granite.ui.clientlibs.HtmlLibraryManager,
                                                                             com.adobe.granite.ui.clientlibs.LibraryType,
                                                                             com.adobe.granite.xss.XSSAPI,
                                                                             com.day.cq.i18n.I18n" %>
<%%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%%>
<%@ taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %><%--
login
=====

    The component to render the login screen.

    It has the following content structure:

   /**
    * The HTML title.
    * Defaults to "Adobe Experience Cloud".
    */
    - title (String)


   /**
    * The favicon.
    * Defaults to "login/adobe-logo.png".
    */
    - favicon (String)


   /**
    * The title in the box.
    * Defaults to "Welcome to Adobe Experience Cloud".
    */
    - box/title (String)


   /**
    * The text in the box.
    * Defaults to "All the tools you need to solve these complex digital business challenges.".
    */
    - box/text (String)


   /**
    * The text of the learn more link. The link is following the text.
    * Defaults to "Learn More".
    */
    - /box/learnMore/text (String)


   /**
    * The href of the learn more link.
    * Defaults to "#".
    */
    - /box/learnMore/link (String)


   /**
    * Enables autocomplete for fields username and password.
    * Defaults to "false".
    */
    - box/autocomplete (Boolean)


   /**
    * The title of the login form. Note that this title is not shown in browsers that display field labels instead of
    * placeholders (IE8 and older).
    * Defaults to "Sign In".
    */
    - box/formTitle (String)


   /**
    * The title of the change password form. Note that this title is not shown in browsers that display field labels instead of
    * placeholders (IE8 and older).
    * Defaults to "Change Password".
    */
    - box/changePasswordTitle (String)


   /**
    * The placeholder of the user field.
    * Defaults to "User name".
    */
    - box/userPlaceholder (String)


   /**
    * The placeholder of the password field in the login form.
    * Defaults to "Password".
    */
    - box/passwordPlaceholder (String)


   /**
    * The placeholder of the password field in the change password form.
    * Defaults to "Old password".
    */
    - box/oldPasswordPlaceholder (String)


   /**
    * The placeholder of the new password field.
    * Defaults to "New password".
    */
    - box/newPasswordPlaceholder (String)


   /**
    * The placeholder of the confirm password field.
    * Defaults to "Confirm new password".
    */
    - box/confirmPasswordPlaceholder (String)


   /**
    * The text of the submit button in the login form.
    * Defaults to "Sign In".
    */
    - box/submitText (String)


   /**
    * The text of the submit button in the change password form.
    * Defaults to "Submit".
    */
    - box/changePasswordSubmitText (String)


   /**
    * The text of the back button.
    * Defaults to "Back".
    */
    - box/backText (String)


   /**
    * The error message displayed when login fails.
    * Defaults to "User name and password do not match".
    */
    - box/invalidLoginText (String)


	/**
	GRANITE-29649
    * The error message displayed when username and password fields are blank.
    * Defaults to "Username and password fields cannot be blank".
    */
    - box/blankUserAndPasswordText (String)


	/**
	GRANITE-29649
    * The error message displayed when username field is blank.
    * Defaults to "Username field cannot be blank".
    */
    - box/blankUserText (String)


   /**
    * The error message displayed when the session timed out.
    * Defaults to "Session timed out, please login again".
    */
    - box/sessionTimedOutText (String)


   /**
    * The error message displayed when the password is expired.
    * Defaults to "Your password has expired".
    */
    - box/loginExpiredText (String)

   /**
    * The error message displayed when the password is expired and the newly chosen password is in the password history.
    * Defaults to "New password is in password history".
    */
    - box/loginInHistoryText (String)

   /**
    * The error message displayed when the new and confirm passwords do not match.
    * Defaults to "New passwords do not match".
    */
    - box/passwordsDoNotMatchText (String)


   /**
    * The error message displayed when the new password is blank.
    * Defaults to "New password must not be blank".
    */
    - box/passwordEmptyText (String)


    /**
     * The title of the success modal.
     * Defaults to "Password Changed"
     */
     - changePasswordSuccessTitle


    /**
     * The text of the success modal.
     * Defaults to "Your password has been changed successfully."
     */
     - changePasswordSuccessText


   /**
    * The items on the left side of the footer.
    * Default items are "Help", "Term of Use" and "Privacy Policy and Cookies".
    */
    - footer/items (String)


   /**
    * The copyright on the right side of the footer.
    * Defaults to "© 2014 Adobe. All Rights Reserved.".
    */
    - footer/copy/text (String)


--%>
<%!

    static final String PARAM_NAME_REASON = "j_reason";

    static final String REASON_KEY_INVALID_LOGIN = "invalid_login";
    static final String REASON_KEY_SESSION_TIMED_OUT = "session_timed_out";

    static final String DEFAULT_AUTH_URL_SUFFIX = "/j_security_check";

    static final String ERROR_SELECTOR = "error";
    static final String CHANGE_PWD_SELECTOR = "changepassword";

    String imsLoginUrl = null;

    private String printProperty(final ValueMap cfg, final I18n i18n, final XSSAPI xssAPI, final String name, final String defaultText) {
        final String text = this.getText(cfg, i18n, name, defaultText);
        return xssAPI.encodeForHTML(text);
    }

    private String printAttribute(final ValueMap cfg, final I18n i18n, final XSSAPI xssAPI, final String name, final String defaultText) {
        final String text = this.getText(cfg, i18n, name, defaultText);
        return xssAPI.encodeForHTMLAttr(text);
    }

    private String getText(final ValueMap cfg, final I18n i18n, final String name, final String defaultText) {
        final String text = cfg.get(name, String.class);
        return text != null ? i18n.getVar(text) : defaultText;
    }

    /**
     * Select the configuration root resource among those stored under <code>configs</code> node.
     * The configuration with the highest order property is selected.
     * @param current the
     * @return the selected configuration root resource or <code>null</code> if no configuration root could be found.
     */
    private Resource getConfigRoot(final Resource current) {
        final Resource configs = current.getChild("configs");
        Resource configRoot = null;
        if (configs != null) {
            long maxOrder = Long.MIN_VALUE;
            for (final Iterator<Resource> cfgs = configs.listChildren(); cfgs.hasNext(); ) {
                final Resource cfg = cfgs.next();
                final ValueMap props = ResourceUtil.getValueMap(cfg);
                final Long order = props.get("order", Long.class);
                if (order != null) {
                    if (order > maxOrder) {
                        configRoot = cfg;
                        maxOrder = order;
                    }
                }
            }
        }
        return configRoot;
    }

    /**
     * Returns a URL suffix which ensures that the request is handled by {@link org.apache.sling.auth.core.impl.SlingAuthenticator}
     * If no custom suffices are found, this method returns <code>DEFAULT_AUTH_URL_SUFFIX</code>
     *
     * @return a URL suffix which will ensure that the URL is handled by the authenticator.
     */
    private String getAuthURLSuffix(final SlingHttpServletRequest req) {
        final Object authUriSufficesObj = req.getAttribute(AuthConstants.ATTR_REQUEST_AUTH_URI_SUFFIX);
        if (authUriSufficesObj instanceof String[]) {
            final String[] authUriSuffices = (String[]) authUriSufficesObj;
            if (authUriSuffices.length > 0) {
                // Any suffix from this array would be valid. Return the first.
                return authUriSuffices[0];
            }
        }
        return DEFAULT_AUTH_URL_SUFFIX;
    }

%><sling:defineObjects/><%final String DEFAULT_LANG = "en";

    final Resource configs = this.getConfigRoot(resource);

    final String browserAcceptLang = request.getHeader("Accept-Language");

    final String browserRegion;
    String browserLang = null;
    Locale browserLocale = null;

    if (browserAcceptLang != null) {
        if (browserAcceptLang.matches("^[a-zA-Z][a-zA-Z](-|_)[a-zA-Z][a-zA-Z].*")) {
            browserLang = browserAcceptLang.substring(0, 2);
            browserRegion = browserAcceptLang.substring(3, 5);
            browserLocale = new Locale(browserLang, browserRegion);
        } else if (browserAcceptLang.matches("^[a-zA-Z][a-zA-Z].*")) {
            browserLang = browserAcceptLang.substring(0, 2);
            browserLocale = new Locale(browserLang);
        }
    } else {
        // default lang in case the request does not have any language
        browserLang = DEFAULT_LANG;
    }

    final I18n i18n;
    if (browserLocale != null) {
        final ResourceBundle browserLocaleBundle = slingRequest.getResourceBundle(browserLocale);
        i18n = new I18n(browserLocaleBundle);
    } else {
        i18n = new I18n(slingRequest);
    }

    final XSSAPI xssAPI = sling.getService(XSSAPI.class).getRequestSpecificAPI(slingRequest);
    final UserManagementService userManagementService = sling.getService(UserManagementService.class);
    final ValueMap cfg = ResourceUtil.getValueMap(configs);

    final String authType = request.getAuthType();
    final String user = request.getRemoteUser();
    final String contextPath = slingRequest.getContextPath();

    // used to map readable reason codes to valid reason messages to avoid phishing attacks through j_reason param
    final Map<String, String> validReasons = new HashMap<String, String>();
    validReasons.put(REASON_KEY_INVALID_LOGIN, this.printProperty(cfg, i18n, xssAPI, "box/invalidLoginText", i18n.get("User name and password do not match")));
    validReasons.put(
            REASON_KEY_SESSION_TIMED_OUT, this.printProperty(cfg, i18n, xssAPI, "box/sessionTimedOutText", i18n.get("Session timed out, please login again")));
    // load custom error types
    final Resource errors = resource.getChild("errors");
    if (errors != null) {
        for (final Iterator<Resource> customErrors = errors.listChildren(); customErrors.hasNext(); ) {
            final Resource customError = customErrors.next();
            validReasons.put(customError.getName(), this.printProperty(customError.adaptTo(ValueMap.class), i18n, xssAPI, "/text", i18n.get("Error")));
        }
    }

    String reason = request.getParameter(PARAM_NAME_REASON) != null ? request.getParameter(PARAM_NAME_REASON) : "";

    if (!StringUtils.isEmpty(reason)) {
        if (validReasons.containsKey(reason)) {
            reason = validReasons.get(reason);
        } else {
            // a reason param value not matching a key in the validReasons map is considered bogus
            log.warn("{} param value '{}' cannot be mapped to a valid reason message: ignoring", PARAM_NAME_REASON, reason);
            reason = "";
        }
    }

    final List<String> selectors = Arrays.asList(slingRequest.getRequestPathInfo().getSelectors());

    final boolean isLogin = !selectors.contains(CHANGE_PWD_SELECTOR);
    final boolean isError = selectors.contains(ERROR_SELECTOR);

%><!DOCTYPE html>
<html lang="<%= xssAPI.encodeForHTMLAttr(browserLang) %>">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <%-- optimized for mobile, zoom/scaling disabled --%>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta http-equiv="X-UA-Compatible" content="chrome=1"/>
    <%final ImsConfigProvider imsConfigProvider = sling.getService(ImsConfigProvider.class);
        if (imsConfigProvider != null) {
            this.imsLoginUrl = imsConfigProvider.getImsLoginUrl(slingRequest);%>
    <meta name="granite.login.imsLoginUrl" content="<%= xssAPI.getValidHref(this.imsLoginUrl) %>">
    <%}%><title>MDX AEM Sign In</title>
    <style type="text/css"><%final HtmlLibraryManager htmlMgr = sling.getService(HtmlLibraryManager.class);
        final HtmlLibrary lib = htmlMgr.getLibrary(LibraryType.CSS, "/libs/granite/core/content/login/clientlib");
        IOUtils.copy(lib.getInputStream(true), out, "utf-8");%></style>
    <ui:includeClientLib categories="coralui3"/><%String favicon = xssAPI.getValidHref(cfg.get("favicon", "login/adobe-logo.png"));
    favicon = xssAPI.getValidHref(favicon);%>
    <link rel="shortcut icon" href="<%= favicon %>" type="image/png">
    <link rel="icon" href="<%= favicon %>" type="image/png">
    <%-- Load the clientlib(s). Extension libraries should use the  'granite.core.login.extension' category. --%>
    <ui:includeClientLib js="jquery,typekit,granite.core.login,granite.core.login.extension"/>
    <ui:includeClientLib css="granite.core.login.extension"/>
</head>
<body class="coral--light">
<div id="wrap" role="main">
    <div id="backgrounds">
        <%-- this holds all the background divs that are dynamically loaded --%>
        <div id="bg_default" class="background"></div>
    </div>
    <div id="tag"></div>
    <%// make sure the redirect path is valid and prefixed with the context path
        String redirect = request.getParameter("resource");
        if (redirect == null || !AuthUtil.isRedirectValid(request, redirect)) {
            redirect = "/";
        }
        if (!redirect.startsWith(contextPath)) {
            redirect = contextPath + redirect;
        }
        final String urlLogin = request.getContextPath() + resource.getPath() + ".html" + this.getAuthURLSuffix(slingRequest);

        if (authType == null || user == null || userManagementService.getAnonymousId().equals(user)) {%>
    <div id="login-box" class="coral--dark">
        <div id="leftbox" class="box">
            <div class="header">
                <h1 class="coral-Heading coral-Heading--1">Welcome to Micromedex AEM</h1>
            </div>
            <p>
                <%=this.printProperty(cfg, i18n, xssAPI, "box/text", i18n.get("All the tools you need to solve these complex digital business challenges.")) %>
                <a class="coral-Link"
                   id="learnmore"
                   href="<%= xssAPI.getValidHref(i18n.getVar(cfg.get("box/learnMore/href", "#"))) %>"
                   x-cq-linkchecker="skip"><%=this.printProperty(cfg, i18n, xssAPI, "box/learnMore/text", i18n.get("Learn More")) %>
                </a>
            </p>
        </div>

        <%-- If IMS is provided we render the choice --%>
        <% if (this.imsLoginUrl != null) { %>
        <div id="rightbox" class="box">
            <button is="coral-button" id="submit-button-ims" variant="primary" type="submit" block><%= xssAPI.encodeForHTML(i18n.get("Sign in with Adobe")) %>
            </button>
            <coral-accordion variant="quiet">
                <coral-accordion-item>
                    <coral-accordion-item-label><%= xssAPI.encodeForHTML(i18n.get("Sign in locally (admin tasks only)")) %>
                    </coral-accordion-item-label>
                    <coral-accordion-item-content>
                        <% if (isError && reason.length() > 0) { %>
                        <p><%= xssAPI.encodeForHTML(i18n.get("Please contact your administrator or try again later.")) %>
                        </p>
                        <coral-alert style="background-color:#aa0016;" variant="error">
                            <coral-alert-content style="color:white;"><%= xssAPI.encodeForHTML(reason) %>
                            </coral-alert-content>
                        </coral-alert>
                        <% } else { %>
                        <% final String autocomplete = cfg.get("box/autocomplete", false) ? "on" : "off"; %>
                        <form class="coral-Form coral-Form--vertical"
                              name="login"
                              method="POST"
                              id="login"
                              action="<%= xssAPI.getValidHref(urlLogin) %>"
                              novalidate="novalidate">
                            <input type="hidden" name="_charset_" value="UTF-8">
                            <input type="hidden" name="errorMessage" value="<%= validReasons.get(REASON_KEY_INVALID_LOGIN) %>">
                            <input type="hidden" name="resource" id="resource" value="<%= xssAPI.encodeForHTMLAttr(redirect) %>">
                            <%final String loginTitle = this.printProperty(cfg, i18n, xssAPI, "box/formTitle", i18n.get("Sign In"));
                                final String changeTitle = this.printProperty(cfg, i18n, xssAPI, "box/changePasswordTitle", i18n.get("Change Password"));
                                final String loginSubmitText = this.printProperty(cfg, i18n, xssAPI, "box/submitText", i18n.get("Sign In"));
                                final String changeSubmitText = this.printProperty(cfg, i18n, xssAPI, "box/changePasswordSubmitText", i18n.get("Submit"));
                                final String userPlaceholder = this.printAttribute(cfg, i18n, xssAPI, "box/userPlaceholder", i18n.get("User name"));
                                final String loginPasswordPlaceholder = this.printAttribute(cfg, i18n, xssAPI, "box/passwordPlaceholder", i18n.get("Password"));
                                final String changePasswordPlaceholder = this.printAttribute(
                                        cfg, i18n, xssAPI, "box/oldPasswordPlaceholder", i18n.get("Old password"));
                                final String newPasswordPlaceholder = this.printAttribute(
                                        cfg, i18n, xssAPI, "box/newPasswordPlaceholder", i18n.get("New password"));
                                final String confirmPasswordPlaceholder = this.printAttribute(
                                        cfg, i18n, xssAPI, "box/confirmPasswordPlaceholder", i18n.get("Confirm new password"));%>
                            <div class="coral-Form-fieldwrapper">
                                <input is="coral-textfield"
                                       aria-label="<%= userPlaceholder %>"
                                       class="coral-Form-field"
                                       id="username"
                                       name="j_username"
                                       type="text"
                                       autofocus="autofocus"
                                       pattern=".*"
                                       placeholder="<%= userPlaceholder %>"
                                       spellcheck="false"
                                       autocomplete="<%= autocomplete %>">
                            </div>
                            <div class="coral-Form-fieldwrapper">
                                <input is="coral-textfield"
                                       aria-label="<%= isLogin ? loginPasswordPlaceholder : changePasswordPlaceholder %>"
                                       class="coral-Form-field"
                                       id="password"
                                       name="j_password"
                                       type="password"
                                       placeholder="<%= isLogin ? loginPasswordPlaceholder : changePasswordPlaceholder %>"
                                       spellcheck="false"
                                       autocomplete="<%= autocomplete %>">
                            </div>
                            <div class="coral-Form-fieldwrapper">
                                <input is="coral-textfield"
                                       aria-label="<%= newPasswordPlaceholder %>"
                                       class="coral-Form-field"
                                       id="new_password"
                                       name="<%= isLogin ? "" : "j_newpassword" %>"
                                       type="password"
                                       placeholder="<%= newPasswordPlaceholder %>"
                                       spellcheck="false"
                                       autocomplete="false" <%= isLogin ? "hidden" : "" %>>
                            </div>
                            <div class="coral-Form-fieldwrapper">
                                <input is="coral-textfield"
                                       aria-label="<%= confirmPasswordPlaceholder %>"
                                       class="coral-Form-field"
                                       id="confirm_password"
                                       name=""
                                       type="password"
                                       placeholder="<%= confirmPasswordPlaceholder %>"
                                       spellcheck="false"
                                       autocomplete="false" <%= isLogin ? "hidden" : "" %>>
                            </div>
                            <coral-alert id="error" style="background-color:#aa0016;" variant="error" <%= reason.length() > 0 ? "" : "hidden" %>>
                                <coral-alert-content style="color:white;"><%= xssAPI.encodeForHTML(reason) %>
                                </coral-alert-content>
                            </coral-alert>
                            <br>
                            <button is="coral-button" id="submit-button" variant="primary" type="submit"><%= isLogin ? loginSubmitText : changeSubmitText %>
                            </button>
                            <br>
                            <button is="coral-button" id="back-button" hidden><%=this.printProperty(cfg, i18n, xssAPI, "box/backText", i18n.get("Back")) %>
                            </button>
                        </form>
                        <input id="login_title" type="hidden" value="<%= loginTitle %>">
                        <input id="change_title" type="hidden" value="<%= changeTitle %>">
                        <input id="login_password_placeholder" type="hidden" value="<%= loginPasswordPlaceholder %>">
                        <input id="change_password_placeholder" type="hidden" value="<%= changePasswordPlaceholder %>">
                        <input id="login_submit_text" type="hidden" value="<%= loginSubmitText %>">
                        <input id="change_submit_text" type="hidden" value="<%= changeSubmitText %>">
                        <input id="invalid_message" type="hidden" value="<%= validReasons.get(REASON_KEY_INVALID_LOGIN) %>"/>
                        <input id="blank_user"
                               type="hidden"
                               value="<%=this.printProperty(cfg, i18n, xssAPI, "box/blankUserText", i18n.get("User name field cannot be blank")) %>"/>
                        <input id="blank_user_passwd"
                               type="hidden"
                               value="<%=this.printProperty(cfg, i18n, xssAPI, "box/blankUserAndPasswordText", i18n.get("User name and password fields cannot be blank")) %>"/>
                        <input id="expired_message"
                               type="hidden"
                               value="<%=this.printProperty(cfg, i18n, xssAPI, "box/loginExpiredText", i18n.get("Your password has expired")) %>"/>
                        <input id="in_history_message"
                               type="hidden"
                               value="<%=this.printProperty(cfg, i18n, xssAPI, "box/loginInHistoryText", i18n.get("New password was found in password history")) %>"/>
                        <input id="not_match_message"
                               type="hidden"
                               value="<%=this.printProperty(cfg, i18n, xssAPI, "box/passwordsDoNotMatchText", i18n.get("New passwords do not match")) %>"/>
                        <input id="empty_message"
                               type="hidden"
                               value="<%=this.printProperty(cfg, i18n, xssAPI, "box/passwordEmptyText", i18n.get("New password must not be blank")) %>"/>
                        <% } %>
                    </coral-accordion-item-content>
                </coral-accordion-item>
            </coral-accordion>
        </div>
        <%-- else render standard local login --%>
        <% } else { %>
        <div id="rightbox" class="box">
            <% if (isError && reason.length() > 0) { %>
            <p><%= xssAPI.encodeForHTML(i18n.get("Please contact your administrator or try again later.")) %>
            </p>
            <coral-alert style="background-color:#aa0016;" variant="error">
                <coral-alert-content style="color:white;"><%= xssAPI.encodeForHTML(reason) %>
                </coral-alert-content>
            </coral-alert>
            <% } else { %>
            <% final String autocomplete = cfg.get("box/autocomplete", false) ? "on" : "off"; %>
            <a is="coral-anchorbutton" variant="primary" icon="login" iconsize="L" href="/servlets/ibmsinglesignonservlet.html">IBM Login (Single Sign
                On)</a><br/><br/>
            <form class="coral-Form coral-Form--vertical"
                  name="login"
                  method="POST"
                  id="login"
                  action="<%= xssAPI.getValidHref(urlLogin) %>"
                  novalidate="novalidate">
                <input type="hidden" name="_charset_" value="UTF-8">
                <input type="hidden" name="errorMessage" value="<%= validReasons.get(REASON_KEY_INVALID_LOGIN) %>">
                <input type="hidden" name="resource" id="resource" value="<%= xssAPI.encodeForHTMLAttr(redirect) %>">
                <%final String loginTitle = this.printProperty(cfg, i18n, xssAPI, "box/formTitle", i18n.get("Sign In"));
                    final String changeTitle = this.printProperty(cfg, i18n, xssAPI, "box/changePasswordTitle", i18n.get("Change Password"));
                    final String loginSubmitText = this.printProperty(cfg, i18n, xssAPI, "box/submitText", i18n.get("Sign In"));
                    final String changeSubmitText = this.printProperty(cfg, i18n, xssAPI, "box/changePasswordSubmitText", i18n.get("Submit"));
                    final String userPlaceholder = this.printAttribute(cfg, i18n, xssAPI, "box/userPlaceholder", i18n.get("User name"));
                    final String loginPasswordPlaceholder = this.printAttribute(cfg, i18n, xssAPI, "box/passwordPlaceholder", i18n.get("Password"));
                    final String changePasswordPlaceholder = this.printAttribute(cfg, i18n, xssAPI, "box/oldPasswordPlaceholder", i18n.get("Old password"));
                    final String newPasswordPlaceholder = this.printAttribute(cfg, i18n, xssAPI, "box/newPasswordPlaceholder", i18n.get("New password"));
                    final String confirmPasswordPlaceholder = this.printAttribute(
                            cfg, i18n, xssAPI, "box/confirmPasswordPlaceholder", i18n.get("Confirm new password"));%>
                <p id="sign-in-title"><%= isLogin ? loginTitle : changeTitle %>
                </p>
                <div class="coral-Form-fieldwrapper">
                    <input is="coral-textfield"
                           aria-label="<%= userPlaceholder %>"
                           class="coral-Form-field"
                           id="username"
                           name="j_username"
                           type="text"
                           autofocus="autofocus"
                           pattern=".*"
                           placeholder="<%= userPlaceholder %>"
                           spellcheck="false"
                           autocomplete="<%= autocomplete %>">
                </div>
                <div class="coral-Form-fieldwrapper">
                    <input is="coral-textfield"
                           aria-label="<%= isLogin ? loginPasswordPlaceholder : changePasswordPlaceholder %>"
                           class="coral-Form-field"
                           id="password"
                           name="j_password"
                           type="password"
                           placeholder="<%= isLogin ? loginPasswordPlaceholder : changePasswordPlaceholder %>"
                           spellcheck="false"
                           autocomplete="<%= autocomplete %>">
                </div>
                <div class="coral-Form-fieldwrapper">
                    <input is="coral-textfield"
                           aria-label="<%= newPasswordPlaceholder %>"
                           class="coral-Form-field"
                           id="new_password"
                           name="<%= isLogin ? "" : "j_newpassword" %>"
                           type="password"
                           placeholder="<%= newPasswordPlaceholder %>"
                           spellcheck="false"
                           autocomplete="false" <%= isLogin ? "hidden" : "" %>>
                </div>
                <div class="coral-Form-fieldwrapper">
                    <input is="coral-textfield"
                           aria-label="<%= confirmPasswordPlaceholder %>"
                           class="coral-Form-field"
                           id="confirm_password"
                           name=""
                           type="password"
                           placeholder="<%= confirmPasswordPlaceholder %>"
                           spellcheck="false"
                           autocomplete="false" <%= isLogin ? "hidden" : "" %>>
                </div>
                <coral-alert id="error" style="background-color:#aa0016;" variant="error" <%= reason.length() > 0 ? "" : "hidden" %>>
                    <coral-alert-content style="color:white;"><%= xssAPI.encodeForHTML(reason) %>
                    </coral-alert-content>
                </coral-alert>
                <br>
                <button is="coral-button" id="submit-button" variant="primary" type="submit"><%= isLogin ? loginSubmitText : changeSubmitText %>
                </button>
                <br>
                <button is="coral-button" id="back-button" hidden><%=this.printProperty(cfg, i18n, xssAPI, "box/backText", i18n.get("Back")) %>
                </button>
            </form>
            <input id="login_title" type="hidden" value="<%= loginTitle %>">
            <input id="change_title" type="hidden" value="<%= changeTitle %>">
            <input id="login_password_placeholder" type="hidden" value="<%= loginPasswordPlaceholder %>">
            <input id="change_password_placeholder" type="hidden" value="<%= changePasswordPlaceholder %>">
            <input id="login_submit_text" type="hidden" value="<%= loginSubmitText %>">
            <input id="change_submit_text" type="hidden" value="<%= changeSubmitText %>">
            <input id="invalid_message" type="hidden" value="<%= validReasons.get(REASON_KEY_INVALID_LOGIN) %>"/>
            <input id="blank_user"
                   type="hidden"
                   value="<%=this.printProperty(cfg, i18n, xssAPI, "box/blankUserText", i18n.get("User name field cannot be blank")) %>"/>
            <input id="blank_user_passwd"
                   type="hidden"
                   value="<%=this.printProperty(cfg, i18n, xssAPI, "box/blankUserAndPasswordText", i18n.get("User name and password fields cannot be blank")) %>"/>
            <input id="expired_message"
                   type="hidden"
                   value="<%=this.printProperty(cfg, i18n, xssAPI, "box/loginExpiredText", i18n.get("Your password has expired")) %>"/>
            <input id="in_history_message"
                   type="hidden"
                   value="<%=this.printProperty(cfg, i18n, xssAPI, "box/loginInHistoryText", i18n.get("New password was found in password history")) %>"/>
            <input id="not_match_message"
                   type="hidden"
                   value="<%=this.printProperty(cfg, i18n, xssAPI, "box/passwordsDoNotMatchText", i18n.get("New passwords do not match")) %>"/>
            <input id="empty_message"
                   type="hidden"
                   value="<%=this.printProperty(cfg, i18n, xssAPI, "box/passwordEmptyText", i18n.get("New password must not be blank")) %>"/>
            <% } %>
        </div>
        <% } %>
    </div>
    <div id="push"></div>
</div>
<div id="footer" role="contentinfo">
    <div class="legal-footer"><%// Footer: default copyright (removable)
        if (cfg.containsKey("footer/copy/text")) {
            final ProductInfoProvider productInfoProvider = sling.getService(ProductInfoProvider.class);
            String year = productInfoProvider == null ? null : productInfoProvider.getProductInfo().getYear();
            if (year == null) {
                year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            }
            final String text = cfg.get("footer/copy/text", "");%><span><%= xssAPI.encodeForHTML(
            i18n.getVar(text, "{0} is the product year", year)) %></span><%}%>
        <ul id="usage-box"><%

            // Footer: dynamic items (config/footer/items)
            if (configs.getChild("footer/items") != null) {
                final Iterator<Resource> footerItems = configs.getChild("footer/items").listChildren();
                while (footerItems.hasNext()) {%>
            <li><%final String itemName = footerItems.next().getName();
                final String href = i18n.getVar(cfg.get("footer/items/" + itemName + "/href", String.class));
                if (href != null) {%><a href="<%= xssAPI.getValidHref(href) %>"><%}%><%=this.printProperty(
                    cfg, i18n, xssAPI, "footer/items/" + itemName + "/text", "") %><%if (href != null) {%></a><%}%></li>
            <%}
            }%>
        </ul>
    </div>
</div>
<%final String modalTitle = this.printProperty(cfg, i18n, xssAPI, "changePasswordSuccessTitle", i18n.get("Password Changed"));%>
<coral-dialog id="success-dialog" variant="success" closable="true">
    <coral-dialog-header><%= modalTitle %>
    </coral-dialog-header>
    <coral-dialog-content>
        <%=this.printProperty(cfg, i18n, xssAPI, "changePasswordSuccessText", i18n.get("Your password has been changed successfully.")) %>
    </coral-dialog-content>
    <coral-dialog-footer>
        <button is="coral-button" variant="primary" coral-close><%= i18n.get("Ok") %>
        </button>
    </coral-dialog-footer>
</coral-dialog>
<script type="text/javascript">
    // try to append the current hash/fragment to the redirect resource
    if (window.location.hash) {
        var resource = document.getElementById("resource");
        if (resource) {
            resource.value += window.location.hash;
        }
    }
</script>
<% } else { %>
<script type="text/javascript">
    var redirect = '<%= xssAPI.encodeForJSString(xssAPI.getValidHref(redirect)) %>';
    if (window.location.hash) {
        redirect += window.location.hash;
    }
    document.location = redirect;
</script>
<% } %>
<!-- QUICKSTART_HOMEPAGE - (string used for readyness detection, do not remove) -->
</body>
</html>