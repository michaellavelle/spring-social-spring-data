package org.springframework.social.connect.web.springdata;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

public class CookieSessionIdSource implements SessionIdSource {

	private String DEFAULT_SESSIONID_COOKIE_NAME = "SPRING_SOCIAL_SPRING_DATA_SESSION_ID";

	private String cookiePath = "/";
	private String domain;
	private int maxAge = -1;
	private String sessionIdCookieName = DEFAULT_SESSIONID_COOKIE_NAME;

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public void setSessionIdCookieName(String sessionIdCookieName) {
		this.sessionIdCookieName = sessionIdCookieName;
	}

	public String getSessionId(RequestAttributes request, boolean create) {
		String sessionId = null;
		HttpServletRequest httpServletRequest = getHttpServletRequest(request);
		String domain = this.domain == null ? httpServletRequest.getServerName(): this.domain;
		for (Cookie cookie : httpServletRequest.getCookies()) {
			if (cookie.getName().equals(sessionIdCookieName)) {
				String value = cookie.getValue();
				sessionId = value;
			}
		}
		if (create && sessionId == null) {
			sessionId = UUID.randomUUID().toString();
			Cookie cookie = new Cookie(sessionIdCookieName, sessionId);
			cookie.setPath(cookiePath);
			cookie.setMaxAge(maxAge);
			cookie.setDomain(domain);
			getHttpServletResponse(request).addCookie(cookie);
		}
		return sessionId;
	}

	private HttpServletRequest getHttpServletRequest(RequestAttributes request) {
		return ((ServletWebRequest) request).getRequest();
	}

	private HttpServletResponse getHttpServletResponse(RequestAttributes request) {
		return ((ServletWebRequest) request).getResponse();
	}

}
