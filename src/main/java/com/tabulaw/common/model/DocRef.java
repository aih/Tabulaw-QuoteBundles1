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

import com.tll.schema.BusinessKeyDef;
import com.tll.schema.BusinessObject;

/**
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Doc Hash", properties = { "hash"
}))
public class DocRef extends EntityBase {

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

	/**
	 * Constructor
	 * @param title
	 * @param hash
	 * @param date
	 * @param caseRef
	 * @param htmlContent
	 */
	public DocRef(String title, String hash, Date date, CaseRef caseRef, String htmlContent) {
		super();
		this.title = title;
		this.hash = hash;
		this.date = date;
		this.caseRef = caseRef;
		this.htmlContent = htmlContent;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.DOCUMENT;
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
}
