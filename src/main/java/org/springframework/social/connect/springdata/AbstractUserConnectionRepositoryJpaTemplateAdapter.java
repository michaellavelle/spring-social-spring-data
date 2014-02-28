/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.connect.springdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.social.connect.jpa.RemoteUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract Base JpaTemplate implementation to enable use of Spring-Data
 * repositories to persist subclasses of UserConnection.
 * 
 * @author Michael Lavelle
 */
public abstract class AbstractUserConnectionRepositoryJpaTemplateAdapter<ID extends UserConnectionKey, U extends UserConnection<ID>, R extends UserConnectionRepository<ID, U>>
		extends AbstractRemoteUserRepositoryJpaTemplateAdapter<ID, U, R> {

	@Override
	public List<RemoteUser> getAll(String userId) {

		return asRemoteUserList(repository
				.findByUserConnectionKeyUserId(userId));
	}

	@Override
	public List<RemoteUser> getAll(String userId, String providerId) {
		List<RemoteUser> remoteUsers = asRemoteUserList(repository
				.findByUserConnectionKeyUserIdAndUserConnectionKeyProviderId(
						userId, providerId));

		Collections.sort(remoteUsers, new Comparator<RemoteUser>() {

			@Override
			public int compare(RemoteUser o1, RemoteUser o2) {
				return new Integer(o1.getRank()).compareTo(o2.getRank());
			}

		});
		return remoteUsers;
	}

	@Override
	public RemoteUser get(String userId, String providerId,
			String providerUserId) {

		RemoteUser remoteUser = repository
				.findOne(instantiateUserConnectionKey(userId, providerId,
						providerUserId));
		if (remoteUser == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return remoteUser;

	}

	@Override
	@Transactional
	public void remove(String userId, String providerId, String providerUserId) {
		try {
			repository.delete(instantiateUserConnectionKey(userId, providerId,
					providerUserId));

		} catch (EmptyResultDataAccessException e) {
		}
	}

	public abstract ID instantiateUserConnectionKey(String userId,
			String providerId, String providerUserId);

	@Override
	@Transactional
	public void remove(String userId, String providerId) {

		List<U> connections = new ArrayList<U>();
		for (RemoteUser remoteUser : repository
				.findByUserConnectionKeyUserIdAndUserConnectionKeyProviderId(
						userId, providerId)) {
			connections.add(toUserConnection(remoteUser));
		}
		if (connections.size() > 0) {
			repository.delete(connections);
		}

	}

}
