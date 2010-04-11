/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.service.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tll.dao.IEntityDao;
import com.tll.model.IEntityAssembler;
import com.tll.service.IService;

/**
 * @author jpk
 */
public abstract class AbstractEntityService implements IService {

	protected final Log log;

	/**
	 * The entity dao.
	 */
	protected final IEntityDao dao;

	/**
	 * The entity assembler.
	 */
	protected final IEntityAssembler entityAssembler;

	/**
	 * The jsr-303 validation factory.
	 */
	private final ValidatorFactory validationFactory;

	/**
	 * Constructor
	 * @param dao
	 * @param entityAssembler
	 * @param validationFactory
	 */
	public AbstractEntityService(IEntityDao dao, IEntityAssembler entityAssembler, ValidatorFactory validationFactory) {
		super();
		this.log = LogFactory.getLog(getClass());
		this.dao = dao;
		this.entityAssembler = entityAssembler;
		this.validationFactory = validationFactory;
	}

	/**
	 * Validates an entity instance.
	 * @param e the entity to validate
	 * @returns The set of invalids
	 */
	protected final <E> Set<ConstraintViolation<E>> validateNoException(E e) {
		return validationFactory.getValidator().validate(e);
	}

	protected final <E> void validate(E e) throws ConstraintViolationException {
		final Set<ConstraintViolation<E>> invalids = validateNoException(e);
		if(invalids != null && invalids.size() > 0) {
			final HashSet<ConstraintViolation<?>> bunk = new HashSet<ConstraintViolation<?>>(invalids.size());
			bunk.addAll(invalids);
			throw new ConstraintViolationException(bunk);
		}
	}

	/**
	 * Validates <em>all</em> entities in a collection.
	 * @param entities The entity collection to validate
	 * @throws ConstraintViolationException When one or more entities are found to
	 *         be invalid in the collection.
	 */
	protected final <E> void validateAll(Collection<E> entities) throws ConstraintViolationException {
		if(entities != null && entities.size() > 0) {
			final HashSet<ConstraintViolation<?>> all = new HashSet<ConstraintViolation<?>>();
			for(final E e : entities) {
				if(e != null) {
					final Set<ConstraintViolation<E>> invalids = validateNoException(e);
					if(invalids != null) {
						all.addAll(invalids);
					}
				}
			}
			if(all.size() > 0) {
				throw new ConstraintViolationException(all);
			}
		}
	}
}
