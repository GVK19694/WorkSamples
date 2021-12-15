package com.ibm.watsonhealth.micromedex.core.auth.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@SuppressWarnings("java:S100")
@ObjectClassDefinition(name = "ecx.io - OpenID Connect Authentication Handler")
public @interface OidcAuthenticationHandlerConfig {

    @AttributeDefinition(name = "Service Ranking", description = "OSGi Framework Service Ranking value to indicate the order in which to call this service. This is an int value where higher values designate higher precedence. Default value is 0.") int service_ranking() default 0;

    @AttributeDefinition(name = "Path", description = "Repository path for which this authentication handler should be used by Sling. If this is empty, the authentication handler will be disabled.") String path();

    @AttributeDefinition(name = "Identity Provider Name", description = "Name of the identity provider. This is used to reference the identity provider by the login modules.") String idp_name();

    @AttributeDefinition(name = "HTTP Redirect", description = "Use HTTP redirect to the Authorization Endpoint instead of HTML form to request credentials (Fragment of URL can only be maintained by HTML form).") boolean httpRedirect() default false;

    @AttributeDefinition(name = "Default Redirect", description = "The default location to redirect to after successful authentication.") String defaultRedirectUrl();

    @AttributeDefinition(name = "Error Redirect", description = "The location to redirect to after failed authentication. This is a format string and the first argument is the failure reason.") String errorRedirectUrl() default "/libs/granite/core/content/login.oidcerror.html?j_reason=%s";

    @AttributeDefinition(name = "Authorization Endpoint") String authorizationEndpoint();

    @AttributeDefinition(name = "Token Endpoint") String tokenEndpoint();

    @AttributeDefinition(name = "Client ID") String clientId();

    @AttributeDefinition(name = "Client Secret", type = AttributeType.PASSWORD) String clientSecret();

    @AttributeDefinition(name = "Scope", description = "List of scopes that should be requested during authentication. See https://openid.net/specs/openid-connect-basic-1_0.html#Scopes for more details.") String scope() default "openid profile email";

    @AttributeDefinition(name = "Callback URL", description = "Redirection URI to which the response will be sent back by the authorization server.") String callbackUrl();

    @AttributeDefinition(name = "Userinfo URL", description = "URI which is used to get all userdata") String userInfoUrl();

    @AttributeDefinition(name = "BlueGroup Names", description = "Allowed BlueGroup Names") String[] blueGroupNames();

}
