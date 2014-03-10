package org.springframework.social.connect.web.springdata;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SessionAttributeRepository<ID extends SessionAttributeId,T extends SessionAttribute<ID>> extends CrudRepository<T, ID> {

}
