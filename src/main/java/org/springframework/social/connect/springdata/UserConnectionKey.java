package org.springframework.social.connect.springdata;

import java.io.Serializable;

public interface UserConnectionKey extends Serializable {

	public String getUserId();

	public String getProviderId();

	public String getProviderUserId();
}
