/**

 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * The doc entity.
 * <p>
 * {@link DocContent} types hold the corresponding html content constituting the
 * document.
 * @author jpk
 */
public class DocRef extends EntityBase implements Comparable<DocRef>, INamedEntity {

	private static final long serialVersionUID = -8257785916791525146L;

	/**
	 * Surrogate primary key.
	 */
	private String id;

	private String title;
	private Date date;
	private CaseRef caseRef;

	/**
	 * This property is not persisted.
	 */
	private transient String htmlContent;

	/**
	 * Constructor
	 */
	public DocRef() {
		super();
	}

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
	public final DocKey getModelKey() {
		return new DocKey(getEntityType(), getId(), getName(), getCitation());
	}

	@Override
	protected IEntity newInstance() {
		return new DocRef();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);
		DocRef dr = (DocRef) cln;
		dr.id = id;
		dr.title = title;
		dr.date = date == null ? null : new Date(date.getTime());
		dr.caseRef = (CaseRef) (caseRef == null ? null : caseRef.clone());
	}

	@Override
	public String getEntityType() {
		return EntityType.DOCUMENT.name();
	}

	/**
	 * Is this a document for a case or a non-case doc?
	 * @return true/false
	 */
	public boolean isCaseDoc() {
		return caseRef != null;
	}

	/**
	 * Provides the citation text if this is a case doc or <code>null</code> if
	 * not a case doc.
	 * @return the citation text or <code>null</code>
	 */
	public String getCitation() {
		return caseRef == null ? null : caseRef.descriptor();
	}

	@NotEmpty
	@Length(max = 512)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotNull
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@NotEmpty
	@Length(max = 128)
	public String getName() {
		return title;
	}

	@Override
	public void setName(String name) {
		setTitle(name);
	}

	/**
	 * Optional case ref.
	 * @return the case ref if specified
	 */
	@Valid
	public CaseRef getCaseRef() {
		return caseRef;
	}

	public void setCaseRef(CaseRef caseRef) {
		this.caseRef = caseRef;
	}

	/**
	 * @return transient html content.
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	/**
	 * Sets the non-persisting (transient) html content property
	 * @param htmlContent
	 */
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	@Override
	public int compareTo(DocRef o) {
		return title != null && o.title != null ? title.compareTo(o.title) : 0;
	}

	@Override
	public Object getPropertyValue(String propertyPath) {

		if("id".equals(propertyPath)) {
			return getId();
		}

		if("title".equals(propertyPath) || "name".equals(propertyPath)) {
			return getTitle();
		}

		if("date".equals(propertyPath)) {
			return getDate();
		}

		return null;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + getTitle() + ")";
	}
}
