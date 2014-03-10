package org.springframework.social.connect.web.springdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.RequestAttributes;

public abstract class AbstractSpringDataSessionStrategy<ID extends SessionAttributeId,A extends SessionAttribute<ID>> implements SessionStrategy {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private SessionAttributeRepository<ID,A> sessionAttributeRepository;
	
	@Autowired
	private SessionIdSource sessionIdSource;
	
	@Override
	public void setAttribute(RequestAttributes request, String name,
			Object value) {		
		String sessionId = sessionIdSource.getSessionId(request,true);
		
		ID sessionAttributeId = createSessionAttributeId(sessionId,name);
		A sessionAttribute = createSessionAttribute(sessionAttributeId,value);
		sessionAttributeRepository.save(sessionAttribute);
	}
	
	protected abstract ID createSessionAttributeId(String sessionId,String name);

	protected abstract A createSessionAttribute(ID sessionAttributeId,Object value);

	
	@Override
	public Object getAttribute(RequestAttributes request, String name) {
		
		String sessionId = sessionIdSource.getSessionId(request,false);
		if (sessionId == null)
		{
			return null;
		}
		else
		{
			try
			{
				ID sessionAttributeId = createSessionAttributeId(sessionId,name);

				A sessionAttribute = sessionAttributeRepository.findOne(sessionAttributeId);
				return sessionAttribute.getValue();
			}
			catch (Exception e)
			{
				logger.error(e);
				return null;
			}
		}
	}

	
	@Override
	public void removeAttribute(RequestAttributes request, String name) {
				
		String sessionId = sessionIdSource.getSessionId(request,false);

		if (sessionId != null)
		{
			ID sessionAttributeId = createSessionAttributeId(sessionId,name);
			try
			{
				sessionAttributeRepository.delete(sessionAttributeId);
			}
			catch (EmptyResultDataAccessException e)
			{
				logger.warn(e);
			}
		}
		
	}
}
