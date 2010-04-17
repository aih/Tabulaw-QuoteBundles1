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
 * NOTE: No surrogate primary key is needed here.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Doc Hash", properties = { "hash"
}))
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

	/**
	 * Constructor
	 * @param title
	 * @param hash
	 * @param date
	 * @param caseRef
	 */
	public DocRef(String title, String hash, Date date, CaseRef caseRef) {
		super();
		this.title = title;
		this.hash = hash;
		this.date = date;
		this.caseRef = caseRef;
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
	public DocRef clone() {
		Date cdate = date == null ? null : new Date(date.getTime());
		return new DocRef(title, hash, cdate, caseRef == null ? null : caseRef.clone());
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(getClass() != obj.getClass()) return false;
		DocRef other = (DocRef) obj;
		if(hash == null) {
			if(other.hash != null) return false;
		}
		else if(!hash.equals(other.hash)) return false;
		return true;
	}

}
