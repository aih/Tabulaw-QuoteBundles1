/**
 * The Logic Lab
 * @author jpk
 * @since Jan 23, 2010
 */
package com.tabulaw.dao.db4o;

import com.db4o.ObjectContainer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.model.AbstractEntityFactory;

/**
 * Db4oEntityFactory
 * @author jpk
 */
public class Db4oEntityFactory extends AbstractEntityFactory<Long> {

	private Provider<ObjectContainer> oc;
	private IdState state;

	/**
	 * Constructor
	 * @param oc Required {@link ObjectContainer} provider
	 */
	@Inject
	public Db4oEntityFactory(Provider<ObjectContainer> oc) {
		super();
		setObjectContainer(oc);
	}

	@Override
	public boolean isPrimaryKeyGeneratable() {
		return true;
	}

	/**
	 * Sets the object container provider.
	 * @param oc required
	 */
	public void setObjectContainer(Provider<ObjectContainer> oc) {
		if(oc == null) throw new NullPointerException();
		this.oc = oc;
	}

	@Override
	public String primaryKeyToString(Long pk) {
		return pk == null ? null : pk.toString();
	}

	@Override
	public Long stringToPrimaryKey(String s) {
		return s == null ? null : Long.valueOf(s);
	}

	@Override
	public Long generatePrimaryKey(IEntity entity) {
		if(state == null) {
			try {
				state = oc.get().query(IdState.class).get(0);
				log.info(state == null ? "Db4o primary key state NOT acquired." : "Db4o primary key state acquired.");
			}
			catch(Exception e) {
				log.info("Creating Db4o primary key state entity.");
				state = new IdState();
				oc.get().store(state);
			}
		}

		assert state != null;
		Long current = state.getCurrentId(entity.getClass());

		final long next = current == null ? 1L : current + 1;
		state.setCurrentId(entity.getClass(), next);
		oc.get().store(state);
		entity.setGenerated(next);
		log.info("Generated new Db4o primary key: " + next);
		return Long.valueOf(next);
	}

}
