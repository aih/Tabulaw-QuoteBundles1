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
 * NOTE: No surrogate primary key is needed here.
 * @author jpk
 */
// NO business keys since this is the primary key!
// @BusinessObject(businessKeys = @BusinessKeyDef(name = "Doc Hash", properties
// = { "hash"
// }))
public class DocRef extends EntityBase implements Comparable<DocRef>, INamedEntity {

	private static final long serialVersionUID = -8257785916791525146L;

	private String title, hash;
	private Date date;
	private CaseRef caseRef;

	private transient String htmlContent;

	/**
	 * Constructor
	 */
	public DocRef() {
		super();
	}

	@Override
	public String getId() {
		return hash;
	}

	@Override
	public void setId(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@Override
	protected IEntity newInstance() {
		return new DocRef();
	}

	@Override
	public void doClone(IEntity cln) {
		super.doClone(cln);
		DocRef dr = (DocRef) cln;
		dr.title = title;
		dr.hash = hash;
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
	 * Provides the citation text if this is a case doc or <code>null</code>
	 * if not a case doc.
	 * @return the citation text or <code>null</code>
	 */
	public String getCitation() {
		return caseRef == null ? null : caseRef.getCitation();
	}

	@NotEmpty
	@Length(max = 512)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotEmpty
	@Length(max = 64)
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		if(hash == null) throw new NullPointerException();
		this.hash = hash;
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

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	@Override
	public int compareTo(DocRef o) {
		return title != null && o.title != null ? title.compareTo(o.title) : 0;
	}

	@Override
	public Object getPropertyValue(String propertyPath) {

		if("title".equals(propertyPath) || "name".equals(propertyPath)) {
			return getTitle();
		}

		if("hash".equals(propertyPath)) {
			return getHash();
		}

		if("date".equals(propertyPath)) {
			return getHash();
		}

		return null;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + getTitle() + ")";
	}
}
