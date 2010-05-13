/**
 * The Logic Lab
 * @author jpk
 * @since May 7, 2010
 */
package com.tabulaw.client.app.field;

import java.util.Map;

import com.tabulaw.client.app.model.EntityMetadataProvider;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.DateField;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.TextAreaField;
import com.tabulaw.client.ui.field.TextField;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.schema.PropertyMetadata;

/**
 * Generates doc related fields.
 * @author jpk
 */
public class DocFieldProvider extends AbstractFieldGroupProvider {

	public static enum DocUseCase {
		CREATE_CASEDOC,
		CREATE_NONCASE;
	}

	private final DocUseCase useCase;

	/**
	 * Constructor
	 * @param useCase
	 */
	public DocFieldProvider(DocUseCase useCase) {
		super();
		if(useCase == null) throw new NullPointerException();
		this.useCase = useCase;
	}

	@Override
	protected String getFieldGroupName() {
		return useCase.name();
	}

	@Override
	protected void populateFieldGroup(final FieldGroup fg) {
		Map<String, PropertyMetadata> metaDoc = EntityMetadataProvider.get().getEntityMetadata(EntityType.DOCUMENT);
		Map<String, PropertyMetadata> metaCase = EntityMetadataProvider.get().getEntityMetadata(EntityType.CASE);
		int visibleLen = 30;

		if(useCase == DocUseCase.CREATE_NONCASE) {
			// title
			TextField fname = ftext("docTitle", "title", "Title", "Title", visibleLen);
			fname.setPropertyMetadata(metaDoc.get("title"));
			fg.addField(fname);

			// date
			DateField fdate = fdate("docDate", "date", "Date", "Date");
			fdate.setPropertyMetadata(metaDoc.get("date"));
			fg.addField(fdate);
		}

		if(useCase == DocUseCase.CREATE_CASEDOC) {
			// url
			TextAreaField fcaseUrl = ftextarea("caseUrl", "url", "Remote Url", "Location of the remote case to fetch", 2, 20);
			fcaseUrl.setPropertyMetadata(metaCase.get("url"));
			fg.addField(fcaseUrl);

		}

		fg.validateIncrementally(false);
	}
}