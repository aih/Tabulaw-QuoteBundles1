/**
 * The Logic Lab
 * @author jpk
 * Jun 26, 2008
 */
package com.tabulaw.model.bk;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NullValueInNestedPathException;

import com.google.inject.Inject;
import com.tabulaw.schema.IEntityMetadata;

/**
 * BusinessKeyFactory - Defines all entity business keys in the application and
 * provides utility methods relating to them.
 * @author jpk
 */
@SuppressWarnings("unchecked")
public final class BusinessKeyFactory {

	/**
	 * BusinessKeyDefinition - Local impl of {@link IBusinessKeyDefinition}.
	 * @author jpk
	 * @param <E>
	 */
	private static final class BusinessKeyDefinition<E> implements IBusinessKeyDefinition<E> {

		private final Class<E> entityClass;
		private final String[] propertyNames;
		private final String businessKeyName;

		public BusinessKeyDefinition(Class<E> entityClass, String businessKeyName, String[] propertyNames) {
			if(entityClass == null) throw new IllegalArgumentException("An entity type must be specified.");
			if(propertyNames == null || propertyNames.length < 1) {
				throw new IllegalArgumentException("At least one property must be specified in a business key");
			}
			this.entityClass = entityClass;
			this.propertyNames = propertyNames;
			this.businessKeyName = businessKeyName;
		}

		public Class<E> getType() {
			return entityClass;
		}

		public String getBusinessKeyName() {
			return businessKeyName;
		}

		public String[] getPropertyNames() {
			return propertyNames;
		}
	}
	
	private final IEntityMetadata entityMetadata;

	/**
	 * The cache of discovered business key definitions.
	 */
	private final Map<Class<?>, Set<IBusinessKeyDefinition<?>>> map =
			new HashMap<Class<?>, Set<IBusinessKeyDefinition<?>>>();

	/**
	 * Constructor
	 * @param entityMetadata required
	 */
	@Inject
	public BusinessKeyFactory(IEntityMetadata entityMetadata) {
		super();
		if(entityMetadata == null) throw new NullPointerException();
		this.entityMetadata = entityMetadata;
	}

	/**
	 * Interrogates an entity class' annotations creating a set of
	 * {@link BusinessKeyDefinition}s.
	 * @param <E>
	 * @param entityClass
	 * @return Set of bk definitions or <code>null</code> if no business keys are
	 *         defined for the given entity class.
	 */
	private <E> Set<IBusinessKeyDefinition<E>> discoverBusinessKeys(Class<E> entityClass) {
		BusinessObject bo = entityClass.getAnnotation(BusinessObject.class);
		if(bo == null) {
			// try the root entity
			bo = entityMetadata.getRootEntityClass(entityClass).getAnnotation(BusinessObject.class);
		}
		if(bo == null) {
			// no bks defined
			return null;
		}
		final Set<IBusinessKeyDefinition<E>> set = new HashSet<IBusinessKeyDefinition<E>>();
		for(final BusinessKeyDef def : bo.businessKeys()) {
			set.add(new BusinessKeyDefinition<E>(entityClass, def.name(), def.properties()));
		}
		return set;
	}
	
	/**
	 * Does the given entity type have any defined business keys?
	 * @param <E>
	 * @param entityClass
	 * @return true/false
	 */
	public <E> boolean hasBusinessKeys(Class<E> entityClass) {
		try {
			definitions(entityClass);
			return true;
		}
		catch(final BusinessKeyNotDefinedException e) {
			return false;
		}
	}

	/**
	 * Provides all defined business key definitions for the given entity type.
	 * @param <E> The entity type
	 * @param entityClass The entity class
	 * @return All defined business key definitions
	 * @throws BusinessKeyNotDefinedException Whe no business keys are defined for
	 *         the given entity type.
	 */
	@SuppressWarnings("rawtypes")
	public <E> IBusinessKeyDefinition<E>[] definitions(Class<E> entityClass)
			throws BusinessKeyNotDefinedException {

		if(!map.containsKey(entityClass)) {
			map.put(entityClass, (Set) discoverBusinessKeys(entityClass));
		}

		final Set<?> set = map.get(entityClass);
		if(set == null) {
			throw new BusinessKeyNotDefinedException(entityClass);
		}
		
		return set.toArray(new IBusinessKeyDefinition[set.size()]);
	}

	/**
	 * Resolves a business key name to its definition for a given entity type.
	 * @param <E>
	 * @param entityClass
	 * @param businessKeyName
	 * @return The found definition or <code>null</code> if none exist for the
	 *         given name of the given entity type.
	 * @throws BusinessKeyNotDefinedException When no business keys are defined
	 *         for the given entity type.
	 */
	public <E> IBusinessKeyDefinition<E> getDefinition(Class<E> entityClass, String businessKeyName)
			throws BusinessKeyNotDefinedException {
		final IBusinessKeyDefinition<E>[] defs = definitions(entityClass);
		for(final IBusinessKeyDefinition<E> def : defs) {
			if(def.getBusinessKeyName().equals(businessKeyName)) {
				return def;
			}
		}
		return null;
	}

	/**
	 * Creates new and empty business keys for the given entity type.
	 * @param <E> The entity type
	 * @param entityClass The entity class
	 * @return Array of empty business keys.
	 * @throws BusinessKeyNotDefinedException When no business keys are defined
	 *         for the given entity type.
	 */
	public <E> IBusinessKey<E>[] create(Class<E> entityClass)
			throws BusinessKeyNotDefinedException {
		final IBusinessKeyDefinition<E>[] defs = definitions(entityClass);
		final BusinessKey<E>[] bks = new BusinessKey[defs.length];
		for(int i = 0; i < defs.length; ++i) {
			bks[i] = new BusinessKey<E>(defs[i]);
		}
		return bks;
	}

	/**
	 * Creates a new and empty business key for the given entity type and business
	 * key name.
	 * @param <E> The entity type.
	 * @param def The business key definition
	 * @return The created business key
	 * @throws BusinessKeyNotDefinedException When no business keys exist for the
	 *         given entity type or no business key is found having the specified
	 *         name.
	 */
	public static <E> IBusinessKey<E> create(IBusinessKeyDefinition<E> def)
			throws BusinessKeyNotDefinedException {
		return new BusinessKey<E>(def);
	}

	/**
	 * Creates a new and empty business key for the given entity type and business
	 * key name.
	 * @param <E> The entity type.
	 * @param entityClass The entity class
	 * @param businessKeyName The business key name
	 * @return The created business key
	 * @throws BusinessKeyNotDefinedException When no business keys exist for the
	 *         given entity type or no business key is found having the specified
	 *         name.
	 */
	public <E> IBusinessKey<E> create(Class<E> entityClass, String businessKeyName)
			throws BusinessKeyNotDefinedException {
		return create(getDefinition(entityClass, businessKeyName));
	}

	/**
	 * Creates all defined business keys with state extracted from the given
	 * entity.
	 * @param <E> The entity type
	 * @param entity The entity from which business keys are created
	 * @return Array of business keys with state extracted from the entity.
	 * @throws BusinessKeyNotDefinedException When no business keys are defined
	 *         for the given entity type.
	 * @throws BusinessKeyPropertyException When a business key property is unable
	 *         to be set.
	 */
	public <E> IBusinessKey<E>[] create(E entity) throws BusinessKeyNotDefinedException,
			BusinessKeyPropertyException {
		final IBusinessKeyDefinition<E>[] defs = definitions((Class<E>) entityMetadata.getEntityClass(entity));
		final BusinessKey<E>[] bks = new BusinessKey[defs.length];
		for(int i = 0; i < defs.length; ++i) {
			bks[i] = new BusinessKey<E>(defs[i]);
		}
		fill(entity, bks);
		return bks;
	}

	/**
	 * Creates a single business key for the given entity type and business key
	 * name.
	 * @param <E> The entity type.
	 * @param entity The entity instance
	 * @param def The business key definition
	 * @return The created business key
	 * @throws BusinessKeyNotDefinedException When no business keys exist for the
	 *         given entity type or no business key is found having the specified
	 *         name.
	 * @throws BusinessKeyPropertyException When a business key property is unable
	 *         to be set.
	 */
	public static <E> IBusinessKey<E> create(E entity, IBusinessKeyDefinition<E> def)
			throws BusinessKeyNotDefinedException, BusinessKeyPropertyException {
		final IBusinessKey<E> bk = create(def);
		fill(new BeanWrapperImpl(entity), bk);
		return bk;
	}

	/**
	 * Creates a single business key for the given entity type and business key
	 * name.
	 * @param <E> The entity type.
	 * @param entity The entity instance
	 * @param businessKeyName The business key name
	 * @return The created business key
	 * @throws BusinessKeyNotDefinedException When no business keys exist for the
	 *         given entity type or no business key is found having the specified
	 *         name.
	 * @throws BusinessKeyPropertyException When a business key property is unable
	 *         to be set.
	 */
	public <E> BusinessKey<E> create(E entity, String businessKeyName)
			throws BusinessKeyNotDefinedException, BusinessKeyPropertyException {
		final IBusinessKeyDefinition<E> theDef = getDefinition((Class<E>) entityMetadata.getEntityClass(entity), businessKeyName);
		final BusinessKey<E>[] bks = new BusinessKey[] { new BusinessKey<E>(theDef) };
		fill(entity, bks);
		return bks[0];
	}
	
	/**
	 * Fills the given business key with values held in the given entity.
	 * @param <E> The entity type
	 * @param entity The entity whose state is applied to the given business keys.
	 * @param bks The business keys.
	 */
	private static <E> void fill(E entity, IBusinessKey<E>[] bks) {
		final BeanWrapperImpl wrappedEntity = new BeanWrapperImpl(entity);
		for(final IBusinessKey<E> bk : bks) {
			fill(wrappedEntity, bk);
		}
	}

	/**
	 * Fills the given business key with values held in the given entity.
	 * @param <E> The entity type
	 * @param wrappedEntity The entity whose state is applied to the given
	 *        business key.
	 * @param bk The business key.
	 */
	private static <E> void fill(BeanWrapperImpl wrappedEntity, IBusinessKey<E> bk) {
		for(final String pname : bk.getPropertyNames()) {
			try {
				bk.setPropertyValue(pname, wrappedEntity.getPropertyValue(pname));
			}
			catch(final NullValueInNestedPathException e) {
				// ok
			}
		}
	}

	/**
	 * Updates the the given entity with the property values held in the given
	 * business keys.
	 * @param <E> The entity type
	 * @param entity The entity instance
	 * @param bk The business key whose state is applied to the given entity.
	 * @throws BusinessKeyPropertyException When a business key property is unable
	 *         to be set.
	 */
	public <E> void apply(E entity, IBusinessKey<E> bk) throws BusinessKeyPropertyException {
		BeanWrapperImpl bw = new BeanWrapperImpl(entity);
		for(String pname : bk.getPropertyNames()) {
			try {
				bw.setPropertyValue(pname, bk.getPropertyValue(pname));
			}
			catch(NullValueInNestedPathException e) {
				throw new BusinessKeyPropertyException(entityMetadata.getEntityClass(entity), bk.getBusinessKeyName(), e.getPropertyName());
			}
		}
	}

	/**
	 * Updates the the given entity with the property values held in the given
	 * business keys.
	 * @param <E> The entity type
	 * @param entity The entity instance
	 * @param bks The business keys whose state is applied to the given entity.
	 * @throws BusinessKeyPropertyException When a business key property is unable
	 *         to be set.
	 */
	public <E> void apply(E entity, IBusinessKey<E>[] bks) throws BusinessKeyPropertyException {
		for(IBusinessKey<E> bk : bks) {
			apply(entity, bk);
		}
	}

	/**
	 * Compares an entities' state to that of an {@link IBusinessKey} instance.
	 * @param <E>
	 * @param entity
	 * @param bk
	 * @return true/false
	 */
	public <E> boolean equals(E entity, IBusinessKey<E> bk) {
		if(entity != null && bk != null) {
			try {
				return bk.equals(BusinessKeyFactory.create(entity, bk));
			}
			catch(BusinessKeyNotDefinedException e) {
				// shouldn't happen
				throw new IllegalArgumentException("No business keys defined for: " + entityMetadata.getEntityTypeDescriptor(entity)
						+ " yet a business key of that type was provided.");
			}
			catch(BusinessKeyPropertyException e) {
				// woops
			}
		}
		return false;
	}

	/**
	 * Ensures all entities w/in the collection are unique against oneanother
	 * based on the defined business keys for corresponding the entity type.
	 * @param <E> The entity type
	 * @param clctn The entity collection. May not be <code>null</code>.
	 * @throws BusinessKeyPropertyException When a business key property is unable
	 *         to be set.
	 * @throws NonUniqueBusinessKeyException When the collection is found to be
	 *         non-unique by the defined business keys for the collection entity
	 *         type.
	 */
	public <E> void isBusinessKeyUnique(Collection<E> clctn) throws BusinessKeyPropertyException,
			NonUniqueBusinessKeyException {
		if(clctn != null && clctn.size() > 1) {
			try {
				for(E e : clctn) {
					IBusinessKey<E>[] bks = create(e);
					for(IBusinessKey<E> bk : bks) {
						for(E e2 : clctn) {
							if(e != e2) {
								IBusinessKey<E>[] otherBks = create(e2);
								for(IBusinessKey<E> bk2 : otherBks) {
									if(bk2.equals(bk)) {
										throw new NonUniqueBusinessKeyException(bk);
									}
								}
							}
						}
					}
				}
			}
			catch(BusinessKeyNotDefinedException bknde) {
				// ok
			}
		}
	}
}
