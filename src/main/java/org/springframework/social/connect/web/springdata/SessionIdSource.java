package org.springframework.social.connect.web.springdata;

import org.springframework.web.context.request.RequestAttributes;

public interface SessionIdSource {

	public String getSessionId(RequestAttributes requestAttributes,boolean create);
}
