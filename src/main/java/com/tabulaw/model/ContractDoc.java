/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 26, 2010
 */
package com.tabulaw.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * ContractDoc
 * @author jopaki
 */
public class ContractDoc extends TimeStampEntity implements Comparable<ContractDoc>, INamedEntity {

	private static final long serialVersionUID = -2095830617611676428L;

	/**
	 * Surrogate primary key.
	 */
	private String id;

	private String title, description, htmlContent;

	/**
	 * Constructor
	 */
	public ContractDoc() {
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
	protected IEntity newInstance() {
		return new ContractDoc();
	}

	@Override
	public String getEntityType() {
		return EntityType.DOC_CONTRACT.name();
	}

	@Override
	public final ModelKey getModelKey() {
		ModelKey mk = super.getModelKey();
		mk.setName(getName());
		return mk;
	}

	@NotEmpty
	@Length(max = 128)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotEmpty
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	@Override
	public String getName() {
		return getTitle();
	}

	@Override
	public void setName(String name) {
		setTitle(name);
	}

	@Override
	public int compareTo(ContractDoc o) {
		return title != null && o.title != null ? title.compareTo(o.title) : 0;
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + getName() + ")";
	}

	@Override
	public Object getPropertyValue(String propertyPath) {

		if("id".equals(propertyPath)) {
			return getId();
		}

		if("title".equals(propertyPath) || "name".equals(propertyPath)) {
			return getTitle();
		}

		if("description".equals(propertyPath)) {
			return getDescription();
		}

		return null;
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		ContractDoc dr = (ContractDoc) cln;
		dr.id = id;
		dr.title = title;
		dr.description = description;
		dr.htmlContent = htmlContent;
	}
}
