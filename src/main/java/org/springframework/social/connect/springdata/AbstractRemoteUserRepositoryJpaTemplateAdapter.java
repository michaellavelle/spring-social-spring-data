/*
 * Copyright 2011 the original author or authors.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.jpa.JpaTemplate;
import org.springframework.social.connect.jpa.RemoteUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

/**
 * Abstract base JpaTemplate implementation to enable use of Spring-Data
 * repositories to persist subclasses of RemoteUser.
 * 
 * @author Michael Lavelle
 */
public abstract class AbstractRemoteUserRepositoryJpaTemplateAdapter<ID extends Serializable, U extends RemoteUser, R extends RemoteUserRepository<ID, U>>
		implements JpaTemplate {

	@Autowired
	protected R repository;

	@Override
	public Set<String> findUsersConnectedTo(String providerId,
			Set<String> providerUserIds) {
		Set<String> userIds = new HashSet<String>();

		for (String providerUserId : providerUserIds) {
			List<RemoteUser> remoteUsers = get(providerId, providerUserId);
			for (RemoteUser remoteUser : remoteUsers) {
				if (!userIds.contains(remoteUser.getUserId())) {
					userIds.add(remoteUser.getUserId());
				}
			}
		}

		return userIds;
	}

	@Override
	public List<RemoteUser> getPrimary(String userId, String providerId) {
		Integer lowestRank = null;
		List<RemoteUser> remoteUsers = new ArrayList<RemoteUser>();

		for (RemoteUser remoteUser : getAll(userId, providerId)) {
			if (lowestRank == null || remoteUser.getRank() < lowestRank) {
				lowestRank = remoteUser.getRank();
			}
		}
		if (lowestRank != null) {
			for (RemoteUser remoteUser : getAll(userId, providerId)) {
				if (remoteUser.getRank() == lowestRank) {
					remoteUsers.add(remoteUser);
				}
			}
			return remoteUsers;
		} else {
			return remoteUsers;
		}
	}

	@Override
	@Transactional
	public int getRank(String userId, String providerId) {

		int highestRank = 0;
		for (RemoteUser remoteUser : getAll(userId, providerId)) {
			if (remoteUser.getRank() > highestRank) {
				highestRank = remoteUser.getRank();
			}
		}
		return highestRank + 1;
	}

	@Override
	public List<RemoteUser> getAll(String userId,
			MultiValueMap<String, String> providerUsers) {
		List<RemoteUser> remoteUsers = new ArrayList<RemoteUser>();
		for (Map.Entry<String, List<String>> providerUsersEntry : providerUsers
				.entrySet()) {
			String providerId = providerUsersEntry.getKey();
			for (String providerUserId : providerUsersEntry.getValue()) {
				try {
					RemoteUser remoteUser = get(userId, providerId,
							providerUserId);
					if (remoteUser != null) {
						remoteUsers.add(remoteUser);
					}
				} catch (EmptyResultDataAccessException e) {
					// No such user
				}
			}
		}

		return remoteUsers;
	}

	protected List<RemoteUser> asRemoteUserList(List<U> list) {
		if (list == null)
			return null;
		List<RemoteUser> remoteUserList = new ArrayList<RemoteUser>();
		for (RemoteUser remoteUser : list) {
			remoteUserList.add(remoteUser);
		}
		return remoteUserList;
	}

	protected abstract List<U> findByProviderIdAndProviderUserId(
			String providerId, String providerUserId);

	@Override
	public List<RemoteUser> get(String providerId, String providerUserId) {
		return asRemoteUserList(findByProviderIdAndProviderUserId(providerId,
				providerUserId));

	}

	@Override
	@Transactional
	public RemoteUser createRemoteUser(String userId, String providerId,
			String providerUserId, int rank, String displayName,
			String profileUrl, String imageUrl, String accessToken,
			String secret, String refreshToken, Long expireTime) {

		try {
			RemoteUser existingConnection = get(userId, providerId,
					providerUserId);
			if (existingConnection != null)
				throw new DuplicateConnectionException(new ConnectionKey(
						providerId, providerUserId));
		} catch (EmptyResultDataAccessException e) {
		}

		U remoteUser = instantiateRemoteUser();
		remoteUser.setUserId(userId);
		remoteUser.setProviderId(providerId);
		remoteUser.setProviderUserId(providerUserId);
		remoteUser.setRank(rank);
		remoteUser.setDisplayName(displayName);
		remoteUser.setProfileUrl(profileUrl);
		remoteUser.setImageUrl(imageUrl);
		remoteUser.setAccessToken(accessToken);
		remoteUser.setSecret(secret);
		remoteUser.setRefreshToken(refreshToken);
		remoteUser.setExpireTime(expireTime);
		repository.save(remoteUser);
		return remoteUser;
	}

	public abstract U instantiateRemoteUser();

	public U toUserConnection(RemoteUser remoteUser) {
		if (remoteUser == null)
			return null;
		U userConnection = instantiateRemoteUser();
		userConnection.setUserId(remoteUser.getUserId());
		userConnection.setProviderId(remoteUser.getProviderId());
		userConnection.setProviderUserId(remoteUser.getProviderUserId());

		userConnection.setRank(remoteUser.getRank());
		userConnection.setDisplayName(remoteUser.getDisplayName());
		userConnection.setProfileUrl(remoteUser.getProfileUrl());
		userConnection.setImageUrl(remoteUser.getImageUrl());
		userConnection.setAccessToken(remoteUser.getAccessToken());
		userConnection.setSecret(remoteUser.getSecret());
		userConnection.setRefreshToken(remoteUser.getRefreshToken());
		userConnection.setExpireTime(remoteUser.getExpireTime());
		return userConnection;

	}

	@Override
	@Transactional
	public RemoteUser save(RemoteUser remoteUser) {

		U userConnection = toUserConnection(remoteUser);
		repository.save(userConnection);
		return userConnection;
	}

}
