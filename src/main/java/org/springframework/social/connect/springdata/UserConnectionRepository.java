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

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * @author Michael Lavelle
 */
@NoRepositoryBean
public interface UserConnectionRepository<ID extends UserConnectionKey, U extends UserConnection<ID>>
		extends RemoteUserRepository<ID, U> {

	List<U> findByUserConnectionKeyUserId(@Param("userId") String userId);

	List<U> findByUserConnectionKeyUserIdAndUserConnectionKeyProviderId(
			@Param("userId") String userId, @Param("providerId") String providerId);

}
