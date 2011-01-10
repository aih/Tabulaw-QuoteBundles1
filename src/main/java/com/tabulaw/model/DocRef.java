/**

 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * The doc entity.
 * <p>
 * {@link DocContent} types hold the corresponding html content constituting the
 * document.
 * @author jpk
 */
@XmlRootElement(name = "docRef")
public class DocRef extends EntityBase implements Comparable<DocRef>, INamedEntity {

	private static final long serialVersionUID = -8257785916791525146L;
	
	/**
	 * Surrogate primary key.
	 */
	private String id;

	private String title;
	private Date date;
	private Reference reference;
	
	private boolean referenceDoc;

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
	@XmlTransient
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
		dr.referenceDoc = referenceDoc;
		dr.date = date == null ? null : new Date(date.getTime());
		dr.reference = (Reference) (reference == null ? null : reference.clone());
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
		return reference != null;
	}

	/**
	 * Provides the citation text if this is a case doc or <code>null</code> if
	 * not a case doc.
	 * @return the citation text or <code>null</code>
	 */
	@XmlTransient
	public String getCitation() {
		return reference == null ? null : reference.descriptor();
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
	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
	
	public boolean isReferenceDoc() {
		return referenceDoc;
	}

	public void setReferenceDoc(boolean referenceDoc) {
		this.referenceDoc = referenceDoc;
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
		
		if("referenceDoc".equals(propertyPath)) {
			return isReferenceDoc();
		}

		return null;
	}
	
	@Override
	public String descriptor() {
		return typeDesc() + " (" + getTitle() + ")";
	}
}
