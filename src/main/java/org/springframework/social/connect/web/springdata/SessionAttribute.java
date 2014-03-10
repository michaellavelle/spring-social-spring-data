package org.springframework.social.connect.web.springdata;

import java.io.Serializable;

public interface SessionAttribute<ID extends SessionAttributeId> {

	public void setSessionAttributeId(ID sessionAttributeId);
	public Serializable getValue();
}
