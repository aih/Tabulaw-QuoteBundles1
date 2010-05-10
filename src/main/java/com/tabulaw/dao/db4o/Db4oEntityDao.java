/**
 * The Logic Lab
 * @author jpk
 * @since Sep 15, 2009
 */
package com.tabulaw.dao.db4o;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springextensions.db4o.Db4oCallback;
import org.springextensions.db4o.Db4oTemplate;
import org.springextensions.db4o.support.Db4oDaoSupport;
import org.springframework.dao.DataAccessException;

import com.db4o.ObjectContainer;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectEventArgs;
import com.db4o.query.Constraint;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.inject.Inject;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.model.INamedEntity;
import com.tabulaw.common.model.ITimeStampEntity;
import com.tabulaw.common.model.NameKey;
import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.Criterion;
import com.tabulaw.criteria.CriterionGroup;
import com.tabulaw.criteria.ICriterion;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.dao.IEntityDao;
import com.tabulaw.dao.IPageResult;
import com.tabulaw.dao.NonUniqueResultException;
import com.tabulaw.dao.SortColumn;
import com.tabulaw.dao.Sorting;
import com.tabulaw.model.IEntityTypeResolver;
import com.tabulaw.model.bk.BusinessKeyFactory;
import com.tabulaw.model.bk.BusinessKeyPropertyException;
import com.tabulaw.model.bk.IBusinessKey;
import com.tabulaw.model.bk.NonUniqueBusinessKeyException;
import com.tabulaw.util.DBType;
import com.tabulaw.util.DateRange;
import com.tabulaw.util.PropertyPath;

/**
 * Db4oEntityDao
 * @author jpk
 */
public class Db4oEntityDao extends Db4oDaoSupport implements IEntityDao {

	public static class IdState extends HashMap<String, Long> {

		private static final long serialVersionUID = 5319169153141045247L;

		public IdState() {
			super();
		}

	}

	@SuppressWarnings("unchecked")
	private static void registerCallbacks(ObjectContainer oc) {
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(oc);

		// NOTE: there is no mechanism for rolling back on the edits these listeners
		// perform on the entities passed in in the case of a db4o save error!

		registry.creating().addListener(new Timestamper(true));
		registry.updating().addListener(new Timestamper(false));

		final Versioner vsnr = new Versioner();
		registry.creating().addListener(vsnr);
		registry.updating().addListener(vsnr);
	}

	/**
	 * Timestamper
	 * @author jpk
	 */
	@SuppressWarnings("unchecked")
	static class Timestamper implements EventListener4 {

		static final Log log = LogFactory.getLog(Timestamper.class);

		private final boolean creating;

		public Timestamper(boolean creating) {
			super();
			this.creating = creating;
		}

		@Override
		public void onEvent(Event4 e, EventArgs args) {
			final ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
			final Object o = queryArgs.object();
			if(o instanceof ITimeStampEntity) {
				final Date now = new Date();
				if(creating) ((ITimeStampEntity) o).setDateCreated(now);
				((ITimeStampEntity) o).setDateModified(now);
				log.debug("Timestamped entity: " + o);
			}
		}

	} // Timestamper

	/**
	 * Versioner
	 * @author jpk
	 */
	@SuppressWarnings("unchecked")
	static class Versioner implements EventListener4 {

		static final Log log = LogFactory.getLog(Versioner.class);

		@Override
		public void onEvent(Event4 e, EventArgs args) {
			final ObjectEventArgs queryArgs = ((ObjectEventArgs) args);
			final Object o = queryArgs.object();
			if(o instanceof IEntity) {
				final int cv = ((IEntity) o).getVersion();
				((IEntity) o).setVersion(cv + 1);
				log.debug("Versioned entity: " + o);
			}
		}

	} // Versioner

	/**
	 * Takes the elements in the given collection and adds them to a new
	 * {@link ArrayList} ensuring the elements exist in a natural (native) java
	 * collection such that it may be eligible for gwt rpc.
	 * @param <E>
	 * @param clc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <E> List<E> naturalize(Collection<E> clc) {
		if(clc instanceof ArrayList) return (ArrayList<E>) clc;
		ArrayList<E> list = new ArrayList<E>(clc == null ? 0 : clc.size());
		if(clc != null) {
			for(E e : clc) {
				list.add(e);
			}
		}
		return list;
	}

	private final IEntityTypeResolver entityTypeResolver;

	private final BusinessKeyFactory businessKeyFactory;

	/**
	 * Constructor
	 * @param container The required db4o object container
	 * @param entityTypeResolver
	 * @param businessKeyFactory
	 */
	@Inject
	public Db4oEntityDao(ObjectContainer container, IEntityTypeResolver entityTypeResolver,
			BusinessKeyFactory businessKeyFactory) {
		super();
		setObjectContainer(container);
		this.entityTypeResolver = entityTypeResolver;
		this.businessKeyFactory = businessKeyFactory;
	}

	@Override
	protected Db4oTemplate createDb4oTemplate(ObjectContainer container) {
		final Db4oTemplate t = super.createDb4oTemplate(container);
		registerCallbacks(t.getObjectContainer());
		return t;
	}

	@SuppressWarnings( { "unchecked"
	})
	@Override
	public <E extends IEntity> List<E> findEntities(Criteria<E> criteria, final Sorting sorting)
			throws InvalidCriteriaException, DataAccessException {
		if(criteria == null) throw new InvalidCriteriaException("No criteria specified.");

		final Query query = getDb4oTemplate().query();

		if(criteria.getCriteriaType().isQuery()) {
			// if(nqt == null) throw new
			// InvalidCriteriaException("No db4o named query translator specified.");
			// nqt.translateNamedQuery(criteria.getNamedQueryDefinition(),
			// criteria.getQueryParams(), query);
			throw new InvalidCriteriaException("Named queries not supported");
		}
		query.constrain(criteria.getEntityClass());
		final CriterionGroup pg = criteria.getPrimaryGroup();
		if(pg != null && pg.isSet()) {
			for(final ICriterion ic : pg) {
				if(ic.isGroup()) throw new InvalidCriteriaException("Nested criterion groups are not supported");
				if(!ic.isSet()) throw new InvalidCriteriaException("criterion not set");
				final Criterion ctn = (Criterion) ic;
				final Object checkValue = ctn.getValue();
				final String pname = ctn.getPropertyName();

				Query pquery;
				if(pname.indexOf('.') > 0) {
					pquery = query;
					// descend one time for each node in the pname (which may be a dot
					// notated property path)
					final PropertyPath path = new PropertyPath(pname);
					for(final String node : path.nodes()) {
						pquery = pquery.descend(node);
					}
				}
				else {
					pquery = query.descend(pname);
				}

				switch(ctn.getComparator()) {
					case BETWEEN: {
						Object min, max;
						if(checkValue instanceof NumberRange) {
							final NumberRange range = (NumberRange) checkValue;
							min = range.getMinimumNumber();
							max = range.getMaximumNumber();
						}
						else if(checkValue instanceof DateRange) {
							final DateRange range = (DateRange) checkValue;
							min = range.getStartDate();
							max = range.getEndDate();
						}
						else {
							// presume an object array
							final Object[] oarr = (Object[]) checkValue;
							min = oarr[0];
							max = oarr[1];
						}
						pquery.constrain(min).greater().equal().or(pquery.constrain(max).smaller().equal());
						break;
					}
					case CONTAINS:
						pquery.constrain(checkValue).contains();
						break;
					case ENDS_WITH:
						pquery.constrain(checkValue).endsWith(ctn.isCaseSensitive());
						break;
					case EQUALS:
						if(!ctn.isCaseSensitive())
							throw new InvalidCriteriaException("Case insensitive equals checking is currently not supported");
						pquery.constrain(checkValue);
						break;
					case GREATER_THAN:
						pquery.constrain(checkValue).greater();
						break;
					case GREATER_THAN_EQUALS:
						pquery.constrain(checkValue).greater().equal();
						break;
					case IN: {
						Object[] arr;
						if(checkValue.getClass().isArray()) {
							arr = (Object[]) checkValue;
						}
						else if(checkValue instanceof Collection<?>) {
							arr = ((Collection) checkValue).toArray();
						}
						else if(checkValue instanceof String) {
							// assume comma-delimited string
							arr =
									org.springframework.util.ObjectUtils.toObjectArray(org.springframework.util.StringUtils
											.commaDelimitedListToStringArray((String) checkValue));
						}
						else {
							throw new InvalidCriteriaException(
									"Unsupported or null type for IN comparator: " + checkValue == null ? "<null>" : checkValue
											.getClass().toString());
						}
						Constraint c = null;
						for(final Object o : arr) {
							if(c == null) {
								c = pquery.constrain(o);
							}
							else {
								c.or(pquery.constrain(o));
							}
						}
						break;
					}
					case IS:
						if(checkValue instanceof DBType == false) {
							throw new InvalidCriteriaException("IS clauses support only check values of type: "
									+ DBType.class.getSimpleName());
						}
						final DBType dbType = (DBType) checkValue;
						if(dbType == DBType.NULL) {
							// null
							pquery.constrain(null);
						}
						else {
							// not null
							pquery.constrain(null).not();
						}
					case LESS_THAN:
						pquery.constrain(checkValue).smaller();
						break;
					case LESS_THAN_EQUALS:
						pquery.constrain(checkValue).smaller().equal();
						break;
					case LIKE:
						pquery.constrain(checkValue).like();
						break;
					case NOT_EQUALS:
						pquery.constrain(checkValue).not();
						break;
					case STARTS_WITH:
						pquery.constrain(checkValue).startsWith(ctn.isCaseSensitive());
						break;
				} // comparator switch
			}
		}

		// apply sorting
		if(sorting != null) {
			for(final SortColumn sc : sorting.getColumns()) {
				if(sc.isAscending()) {
					query.descend(sc.getPropertyName()).orderAscending();
				}
				else {
					query.descend(sc.getPropertyName()).orderDescending();
				}
			}
		}

		return naturalize((List<E>) getDb4oTemplate().execute(new Db4oCallback() {

			@Override
			public Object doInDb4o(ObjectContainer container) throws RuntimeException {
				return query.execute();
			}
		}));
	}

	@Override
	public <E extends IEntity> E findEntity(Criteria<E> criteria) throws InvalidCriteriaException,
			EntityNotFoundException, NonUniqueResultException, DataAccessException {
		final List<E> list = findEntities(criteria, null);
		if(list == null || list.size() < 1) {
			throw new EntityNotFoundException("No matching entity found.");
		}
		else if(list.size() > 1) {
			throw new NonUniqueResultException("More than one matching entity found.");
		}
		assert list.size() == 1;
		return list.get(0);
	}

	@Override
	public <E extends IEntity> IPageResult<E> getPage(Criteria<E> criteria, Sorting sorting, int offset, int pageSize)
			throws InvalidCriteriaException, DataAccessException {
		List<E> elist = findEntities(criteria, sorting);
		if(elist == null) {
			elist = new ArrayList<E>();
		}
		final int size = elist.size();
		if(size >= 1) {
			int fi = offset;
			int li = fi + pageSize;
			if(fi > size - 1) {
				fi = size - 1;
			}
			if(li > size - 1) {
				li = size; // NOTE: exclusive index
			}
			elist = elist.subList(fi, li);
		}
		final List<E> subList = elist;
		return new IPageResult<E>() {

			@Override
			public List<E> getPageList() {
				return subList;
			}

			@Override
			public int getResultCount() {
				return size;
			}
		};
	}

	/**
	 * Loads entities by a given {@link Predicate}.
	 * @param <E>
	 * @param p the predicate
	 * @param key The key that identifies the entity to be loaded
	 * @return All matching entities
	 */
	@SuppressWarnings("unchecked")
	private <E extends IEntity> E loadByPredicate(Predicate<E> p, Object key) throws EntityNotFoundException,
			DataAccessException {
		final List<E> list = getDb4oTemplate().query(p);
		if(list == null || list.size() < 1) {
			final String msg = "No matching entity found for key: [" + key + ']';
			logger.debug(msg);
			throw new EntityNotFoundException(msg);
		}
		if(list.size() > 1) {
			final String msg = list.size() + " matching entities found (not one) for key: [" + key + ']';
			logger.debug(msg);
			throw new EntityNotFoundException(msg);
		}
		assert list.size() == 1;
		return list.get(0);
	}

	@SuppressWarnings("serial")
	@Override
	public <E extends IEntity> E load(Class<E> entityType, final String id) throws EntityNotFoundException,
			DataAccessException {
		logger.debug("Loading entity by Id: " + id);
		return loadByPredicate(new Predicate<E>(entityType) {

			@Override
			public boolean match(E candidate) {
				return candidate.getId().equals(id);
			}
		}, id);
	}

	@SuppressWarnings("serial")
	@Override
	public <E extends IEntity> E load(final IBusinessKey<E> key) throws EntityNotFoundException, DataAccessException {
		return loadByPredicate(new Predicate<E>(key.getType()) {

			@Override
			public boolean match(E candidate) {
				return businessKeyFactory.equals(candidate, key);
			}
		}, key);
	}

	@SuppressWarnings( {
		"unchecked", "serial"
	})
	@Override
	public INamedEntity load(final NameKey nameKey) throws EntityNotFoundException, NonUniqueResultException,
			DataAccessException {
		Class<?> entityClass = entityTypeResolver.resolveEntityClass(nameKey.getEntityType());
		if(!INamedEntity.class.isAssignableFrom(entityClass)) {
			throw new IllegalArgumentException();
		}
		return loadByPredicate(new Predicate<INamedEntity>((Class<INamedEntity>) entityClass) {

			@Override
			public boolean match(INamedEntity candidate) {
				return nameKey.getName().equals(candidate.getName());
			}
		}, nameKey);
	}

	@SuppressWarnings( {
		"unchecked", "serial"
	})
	@Override
	public <E extends IEntity> List<E> loadAll(Class<E> entityType) throws DataAccessException {
		final List<E> list = naturalize(getDb4oTemplate().query(new Predicate<E>(entityType) {

			@Override
			public boolean match(E candidate) {
				return true;
			}
		}));
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends IEntity> E persist(E entity) throws EntityExistsException, DataAccessException {
		logger.debug("Persisting entity: " + entity);
		Class<E> entityClass = (Class<E>) entityTypeResolver.resolveEntityClass(entity.getEntityType());
		
		// must check for business key uniqueness first!
		if(businessKeyFactory.hasBusinessKeys(entityClass)) {
			try {
				final List<E> list = loadAll(entityClass);
				final ArrayList<E> mlist = new ArrayList<E>((list == null ? 0 : list.size()) + 1);
				mlist.addAll(list);
				if(entity.isNew()) {
					mlist.add(entity);
				}
				else {
					// find it in mlist and replace
					for(int i = 0; i < mlist.size(); i++) {
						final E e = mlist.get(i);
						if(e.equals(entity)) {
							mlist.set(i, entity);
							break;
						}
					}
				}
				businessKeyFactory.isBusinessKeyUnique(mlist);
			}
			catch(final NonUniqueBusinessKeyException e) {
				throw new EntityExistsException(e.getMessage());
			}
			catch(final BusinessKeyPropertyException e) {
				throw new IllegalStateException(e);
			}
		}
		
		// we must purge the existing entity first!
		if(!entity.isNew() && entity.getId() != null) {
			try {
				purge(entity);
				if(logger.isDebugEnabled()) 
					logger.debug("Purged existing entity by id before updating: " + entity);
			}
			catch(EntityNotFoundException e) {
				// ok
			}
		}

		// NOTE: this doesn't handle any ancestor entities that my exist under this
		// entity!
		// the calling service is responsible for individually persisting child
		// entities!
		if(entity.isNew() && entity.getId() == null) {
			String surrogatePk = generatePrimaryKey(entityClass);
			entity.setId(surrogatePk);
			logger.info("Generated id for entity: " + entity + " just before persisting it.");
		}

		getDb4oTemplate().store(entity);

		return entity;
	}

	@Override
	public <E extends IEntity> Collection<E> persistAll(Collection<E> entities) throws DataAccessException {
		if(entities != null) {
			for(final E e : entities) {
				persist(e);
			}
		}
		return entities;
	}

	@Override
	public <E extends IEntity> void purge(E entity) throws EntityNotFoundException, DataAccessException {
		logger.debug("Purging entity: " + entity);
		Class<? extends IEntity> entityClass = entityTypeResolver.resolveEntityClass(entity.getEntityType());
		purge(entityClass, entity.getId());
	}

	@Override
	public <E extends IEntity> void purge(Class<E> entityType, String id) throws EntityNotFoundException,
			DataAccessException {
		final E existing = load(entityType, id);
		if(existing == null) throw new EntityNotFoundException("Entity of id: " + id + " not found for purging");
		getDb4oTemplate().delete(existing);
		getDb4oTemplate().purge(existing);
	}

	@Override
	public <E extends IEntity> void purgeAll(Collection<E> entities) throws DataAccessException {
		if(entities != null) {
			for(final E e : entities) {
				purge(e);
			}
		}
	}

	@SuppressWarnings( {
		"unchecked", "serial"
	})
	@Override
	public <E extends IEntity> List<E> findByIds(Class<E> entityType, final Collection<String> ids, Sorting sorting)
			throws DataAccessException {
		return naturalize(getDb4oTemplate().query(new Predicate<E>(entityType) {

			@Override
			public boolean match(E candidate) {
				return ids.contains((candidate.getId()));
			}
		}));
	}

	@Override
	public <E extends IEntity> List<String> getIds(Criteria<E> criteria, Sorting sorting)
			throws InvalidCriteriaException, DataAccessException {
		final List<E> list = findEntities(criteria, sorting);
		if(list == null) {
			return null;
		}
		final ArrayList<String> idlist = new ArrayList<String>();
		for(final E e : list) {
			idlist.add(e.getId());
		}
		return idlist;
	}

	private IdState getIdState() {
		IdState idState;
		List<IdState> list = getObjectContainer().query(IdState.class);
		if(list == null || list.size() == 0) {
			idState = new IdState();
			getObjectContainer().store(idState);
			logger.info("Created and stored IdState instance");
		}
		else {
			idState = list.get(0);
		}
		if(idState == null) throw new IllegalStateException();
		return idState;
	}

	private String generatePrimaryKey(Class<?> clz) {
		IdState idState = getIdState();
		Long current = idState.get(clz.getName());
		final long next = current == null ? 1L : current + 1;
		idState.put(clz.getName(), Long.valueOf(next));
		getObjectContainer().store(idState);

		String surrogatePk = Long.toString(next);
		logger.info("Generated new surrogate primary key: " + surrogatePk);
		return surrogatePk;
	}

	public long[] generatePrimaryKeyBatch(Class<?> entityClz, int numIds) {
		IdState idState = getIdState();
		Long current = idState.get(entityClz.getName());

		long start = (current == null ? 0L : current.longValue()) + 1;
		long end = start + numIds;

		idState.put(entityClz.getName(), Long.valueOf(end));
		getObjectContainer().store(idState);

		return new long[] {
			start, end
		};
	}
}
