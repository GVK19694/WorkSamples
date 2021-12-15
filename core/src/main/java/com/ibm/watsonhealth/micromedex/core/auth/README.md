# ecx.io AEM Core Tools | Authentication | OpenID Connect Authentication Handler

## How to use OpenId Connect Authentication Handler with Okta as Authorization Server

Here you can find an example how to configure the OpenId Connect Authentication Handler using Okta as Authorization Server.

1. Start Author instance and add the configurations described in [Configuration](#configuration).
2. Open http://localhost:4502/
    1. You should get redirected to https://dev-93908838.okta.com/login/login.htm
3. Authenticate as john.doe@ecx.io (password: 1supersecure23)
4. You will be logged in as John Doe

Note: You can still login as admin by accessing http://localhost:4502/libs/granite/core/content/login.html.

## Configuration

The configuration consists of a configuration of OidcAuthenticationHandler, DefaultSyncHandler and ExternalLoginModule.

### OidcAuthenticationHandler

The Authentication Handler handles the main login flow by redirecting the user to the Authorization Server's login page and extracting credentials from incoming
requests. It also requests the Login Token from the configured OpenID Connect Provider. (Note: The `clientSecret`
should normally be encrypted)

io.ecx.aem.core.tools.auth.oidc.OidcAuthenticationHandler-okta.config

```
# Configuration created by Apache Sling JCR Installer
service.ranking=I"5002"
path="/"
idp.name="Okta"
httpRedirect=B"false"
defaultRedirectUrl="/aem/start.html"
authorizationEndpoint="https://dev-93908838.okta.com/oauth2/v1/authorize"
tokenEndpoint="https://dev-93908838.okta.com/oauth2/v1/token"
clientId="0oaaqvc19fdERyHol5d6"
clientSecret="n5XTCOF-P8LylkZIHS1JvdyPvWQZ3Unlj5WyGB4j"
scope="openid profile email"
callbackUrl="http://localhost:4502/oidc_login"
```

### DefaultSyncHandler

The Sync Handler stores the User/Group within AEM. Here you can also specify which user properties you want to sync. Synchronization of Groups other than the
default Group is not implemented.

org.apache.jackrabbit.oak.spi.security.authentication.external.impl.DefaultSyncHandler-okta.config

```
# Configuration created by Apache Sling JCR Installer
group.pathPrefix=""
user.membershipNestingDepth=I"1"
user.propertyMapping=("profile/email\=email","profile/familyName\=name")
user.autoMembership=["contributor"]
handler.name="Okta"
user.pathPrefix=""
user.disableMissing=B"true"
```

### ExternalLoginModule

The External Login Module links the Authentication Handler and Sync Handler together. The `sync.handlerName` and `idp.name` has to match the respective values
in the previous configs.

org.apache.jackrabbit.oak.spi.security.authentication.external.impl.ExternalLoginModuleFactory-okta.config

```
# Configuration created by Apache Sling JCR Installer
sync.handlerName="Okta"
idp.name="Okta"
```

## Source Directories

* `core/src/main/java/io/ecx/aem/core/tools/auth/oidc`
* `core/src/main/resources/io/ecx/aem/core/tools/auth/oidc`
* `ui.apps/src/main/content/jcr_root/apps/granite/core/components/login`

## Maven Dependencies

Note that following dependencies must be added to the bundle by using `Embed-Dependency` or `Include-Resource` depending on your maven setup.

```xml
<!-- Java JWT -->
<dependency>
   <groupId>io.jsonwebtoken</groupId>
   <artifactId>jjwt-api</artifactId>
   <version>0.11.2</version>
</dependency>
<dependency>
<groupId>io.jsonwebtoken</groupId>
<artifactId>jjwt-impl</artifactId>
<version>0.11.2</version>
</dependency>
<dependency>
<groupId>io.jsonwebtoken</groupId>
<artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
<version>0.11.2</version>
</dependency>
```