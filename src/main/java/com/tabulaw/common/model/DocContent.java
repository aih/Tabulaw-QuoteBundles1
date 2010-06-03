/**

 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Holds html content for a document.
 * <p>
 * A strong one-to-one relationship exists between this type and {@link DocRef}
 * type.
 * @author jpk
 */
public class DocContent extends EntityBase {

	private static final long serialVersionUID = 7579142517520079175L;

	/**
	 * Surrogate primary key.
	 * <p>
	 * This is the same id as the <code>DocRef</code> id.
	 */
	private String id;

	/**
	 * The HTML content of the doc.
	 */
	private String htmlContent;

	/**
	 * Constructor
	 */
	public DocContent() {
		super();
	}

	/**
	 * @return the same id as {@link DocRef#getId()}.
	 */
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if(id == null) throw new NullPointerException();
		this.id = id;
	}

	@Override
	protected IEntity newInstance() {
		return new DocContent();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);
		DocContent dr = (DocContent) cln;
		dr.id = id;
		dr.htmlContent = htmlContent;
	}

	@Override
	public String getEntityType() {
		return EntityType.DOC_CONTENT.name();
	}

	@NotEmpty
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
}
