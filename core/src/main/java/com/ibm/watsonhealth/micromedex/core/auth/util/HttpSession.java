package com.ibm.watsonhealth.micromedex.core.auth.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Custom implementation of http session because {@link HttpServletRequest#getSession()}
 * doesn't work for all requests.
 */
public final class HttpSession {

    private static final Logger logger = LoggerFactory.getLogger(HttpSession.class);

    private static final String SESSION_ID_COOKIE = "OIDCSESSIONID";

    public static final String HEADER_SET_COOKIE = "Set-Cookie";

    public static final int MAX_SESSIONS = 1000;
    public static final int SESSION_EXPIRATION = 60; // minutes

    public static final int SESSION_ID_BITS = 130;
    public static final int SESSION_ID_RADIX = 32;

    private static final Cache<String, HttpSession> instances = CacheBuilder
      .newBuilder()
      .maximumSize(MAX_SESSIONS)
      .expireAfterAccess(SESSION_EXPIRATION, TimeUnit.MINUTES)
      .build();

    private final String id;
    private final Map<String, Object> attributes;

    private HttpSession() {
        this.id = new BigInteger(SESSION_ID_BITS, new SecureRandom()).toString(SESSION_ID_RADIX);
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return this.id;
    }

    public boolean hasAttribute(final String key) {
        return this.attributes.containsKey(key);
    }

    public Object getAttribute(final String key) {
        final Object value = this.attributes.get(key);
        logger.trace("Read from session {}: {}=\"{}\"", this.getId(), key, value);
        return value;
    }

    public Object setAttribute(final String key, final Object value) {
        logger.trace("Write to session {}: {}=\"{}\"", this.getId(), key, value);
        return this.attributes.put(key, value);
    }

    public Object removeAttribute(final String key) {
        logger.trace("Remove from session {}: {}", this.getId(), key);
        return this.attributes.remove(key);
    }

    public static HttpSession getSession(final HttpServletRequest request, final HttpServletResponse response) {
        return getSession(request, response, true);
    }

    public static HttpSession getSession(final HttpServletRequest request, final HttpServletResponse response, final boolean create) {
        HttpSession session = fromAttribute(request);

        if (session == null) {
            session = fromCookie(request);

            if (session == null && create) {
                session = newInstance(request, response);
            }
        }

        return session;
    }

    public static void endSession(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpSession session = getSession(request, response, false);
        if (session != null) {
            request.removeAttribute(HttpSession.class.getName());
            setCookie(response, SESSION_ID_COOKIE, "", 0);
            instances.invalidate(session.getId());

            logger.trace("End session {} (uri={})", session.getId(), request.getRequestURI());
        }
    }

    private static HttpSession fromAttribute(final HttpServletRequest request) {
        return (HttpSession) request.getAttribute(HttpSession.class.getName());
    }

    private static HttpSession fromCookie(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), SESSION_ID_COOKIE)) {
                    final String sessionId = cookie.getValue();
                    if (StringUtils.isNotBlank(sessionId)) {
                        final HttpSession session = instances.getIfPresent(sessionId);
                        if (session != null) {
                            request.setAttribute(HttpSession.class.getName(), session);
                            logger.trace("Access session {} (uri={})", session.getId(), request.getRequestURI());
                        }
                        return session;
                    }
                }
            }
        }

        return null;
    }

    private static HttpSession newInstance(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpSession session = new HttpSession();

        request.setAttribute(HttpSession.class.getName(), session);
        setCookie(response, SESSION_ID_COOKIE, session.getId(), -1);
        instances.put(session.getId(), session);

        logger.trace("Start session {} (uri={})", session.getId(), request.getRequestURI());

        return session;
    }

    /**
     * We need to set the cookie manually because {@link HttpServletResponse#addCookie(Cookie)}
     * doesn't work for all requests
     */
    private static void setCookie(final HttpServletResponse response, final String name, final String value, final int maxAge) {
        final StringBuilder builder = new StringBuilder(String.format("%s=%s; Path=/; HttpOnly", name, value));

        if (maxAge >= 0) {
            builder.append("; Max-Age=").append(maxAge);
        }

        response.addHeader(HEADER_SET_COOKIE, builder.toString());
    }

}
