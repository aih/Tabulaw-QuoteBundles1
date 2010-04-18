package com.tll.schema;

import java.util.Map;

/**
 * ISchemaInfo - Provides entity meta data for all defined entities.
 * @author jpk
 */
public interface ISchemaInfo {

	/**
	 * Provides a map of schema properties keyed by property name for a target entity type.
	 * @param entityClass the entity type
	 * @return the associated schema map
	 */
	Map<String, ISchemaProperty> getSchemaProperties(Class<?> entityClass);

	/**
	 * Provides the schema info for the given property for a given entity type.
	 * @param entityClass the entity type
	 * @param propertyName the property name
	 * @return the corres. schema property type
	 * @throws SchemaInfoException When the given property doesn't exist
	 */
	ISchemaProperty getSchemaProperty(Class<?> entityClass, String propertyName)
	throws SchemaInfoException;
}
