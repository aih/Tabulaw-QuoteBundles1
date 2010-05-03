/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.client.app.model;

import java.util.HashMap;
import java.util.Map;

import com.tabulaw.common.model.EntityType;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;
/**
 * Client side entity metadata definitions.
 * @author jpk
 */
public final class EntityMetadataProvider {

	public static final PropertyMetadata METADATA_DATE_CREATED = new PropertyMetadata(PropertyType.DATE, true, false, 30);
	public static final PropertyMetadata METADATA_DATE_MODIFIED = METADATA_DATE_CREATED;

	private static EntityMetadataProvider instance;

	public static EntityMetadataProvider get() {
		if(instance == null) instance = new EntityMetadataProvider();
		return instance;
	}

	private final HashMap<EntityType, Map<String, PropertyMetadata>> metadata =
			new HashMap<EntityType, Map<String, PropertyMetadata>>();

	private EntityMetadataProvider() {
		super();

		HashMap<String, PropertyMetadata> metaCase;

		// user metadata
		metaCase = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.USER, metaCase);
		metaCase.put("dateCreated", METADATA_DATE_CREATED);
		metaCase.put("dateModified", METADATA_DATE_MODIFIED);
		metaCase.put("name", new PropertyMetadata(PropertyType.STRING, false, true, 50));
		metaCase.put("emailAddress", new PropertyMetadata(PropertyType.STRING, false, true, 128));
		metaCase.put("password", new PropertyMetadata(PropertyType.STRING, false, true, 30));
		metaCase.put("locked", new PropertyMetadata(PropertyType.BOOL, false, true, 5));
		metaCase.put("enabled", new PropertyMetadata(PropertyType.BOOL, false, true, 5));
		metaCase.put("expires", new PropertyMetadata(PropertyType.DATE, false, true, 30));

		// case metadata
		metaCase = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.CASE, metaCase);
		metaCase.put("citation", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaCase.put("parties", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaCase.put("year", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		// metaDocument.put("extractHtml", new PropertyMetadata(PropertyType.STRING,
		// false, true, -1));
		metaCase.put("url", new PropertyMetadata(PropertyType.STRING, false, true, -1));

		// document metadata
		HashMap<String, PropertyMetadata> metaDocument = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.DOCUMENT, metaDocument);
		metaDocument.put("title", new PropertyMetadata(PropertyType.STRING, false, true, 64));
		metaDocument.put("date", new PropertyMetadata(PropertyType.DATE, false, true, -1));
		metaDocument.put("hash", new PropertyMetadata(PropertyType.STRING, false, true, 64));

		// quote metadata
		HashMap<String, PropertyMetadata> metaQuote = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.QUOTE, metaQuote);
		metaQuote.put("quote", new PropertyMetadata(PropertyType.STRING, false, true, 255));
		metaQuote.put("tags", new PropertyMetadata(PropertyType.STRING, false, false, 255));
		metaQuote.put("serializedMark", new PropertyMetadata(PropertyType.STRING, false, false, -1));

		// quote bundle metadata
		HashMap<String, PropertyMetadata> metaQuoteBundle = new HashMap<String, PropertyMetadata>();
		metadata.put(EntityType.QUOTE_BUNDLE, metaQuoteBundle);
		metaQuoteBundle.put("name", new PropertyMetadata(PropertyType.STRING, false, true, 50));
		metaQuoteBundle.put("description", new PropertyMetadata(PropertyType.STRING, false, false, 255));
	}

	public Map<String, PropertyMetadata> getEntityMetadata(EntityType entityType) {
		return metadata.get(entityType);
	}

}
