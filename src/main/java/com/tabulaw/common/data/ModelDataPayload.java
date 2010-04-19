/**
 * The Logic Lab
 * @author jpk Nov 6, 2007
 */
package com.tabulaw.common.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tabulaw.common.model.IEntity;

/**
 * Construct to hold model data based on what is requested in model related
 * request.
 * @author jpk
 */
public class ModelDataPayload extends Payload {

	/**
	 * Map of entity lists keyed by the entity type.
	 */
	protected Map<String, List<IEntity>> entityMap;

	/**
	 * Set of entity prototypes
	 */
	protected Set<IEntity> entityPrototypes;

	/**
	 * Constructor
	 */
	public ModelDataPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public ModelDataPayload(Status status) {
		super(status);
	}

	public Map<String, List<IEntity>> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, List<IEntity>> entityMap) {
		this.entityMap = entityMap;
	}

	public Set<IEntity> getEntityPrototypes() {
		return entityPrototypes;
	}

	public void setEntityPrototypes(Set<IEntity> entityPrototypes) {
		this.entityPrototypes = entityPrototypes;
	}

}
