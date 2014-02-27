package org.springframework.social.connect.springdata;

import org.springframework.social.connect.jpa.RemoteUser;

public interface UserConnection<ID extends UserConnectionKey> extends
		RemoteUser {

	public void setUserConnectionKey(ID userConnectionKey);
}
