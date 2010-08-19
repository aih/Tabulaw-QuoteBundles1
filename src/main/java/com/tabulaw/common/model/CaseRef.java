/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.tabulaw.model.bk.BusinessKeyDef;
import com.tabulaw.model.bk.BusinessObject;
import com.tabulaw.util.StringUtil;

/**
 * Ref to a remote case.
 * <p>
 * <pre>
 * reftoken  {original full citation text}
 * url:      {the url pointing to the original doc of which this entity is based
 * parties:  "New York Times Co. v. Sullivan"
 * docLoc:   "376 U.S. 254"
 * court:    "Supreme Court" or "5th Circuit"
 * year:     1975
 * </pre>
 * <p>
 * <b>Supreme court</b> case ref full citation format:
 * 
 * <pre>
 * 	New York Times Co. v. Sullivan, 376 U.S. 254 (1964).
 * </pre>
 * <p>
 * <b>Other</b> case ref full citation format:
 * 
 * <pre>
 * 	Curtis Publishing Company v. Butts, 351 F. 2d 702 (5th Cir. 1965)
 * </pre>
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Url", properties = { "url"
}))
@XmlRootElement(name = "caseRef")
public class CaseRef extends EntityBase implements Comparable<CaseRef> {

	/**
	 * Ciation format flags.
	 */
	public static enum CitationFormatFlag {
		ALL(0),
		EXCLUDE_PARTIES(1);

		private final int flag;

		private CitationFormatFlag(int flag) {
			this.flag = flag;
		}

		public int flag() {
			return flag;
		}
		
		public static boolean hasFormat(int flags, CitationFormatFlag flag) {
			return (flags & flag.flag) == flag.flag;
		}

		public static int addFormat(int flags, CitationFormatFlag flag) {
			return flags | flag.flag;
		}
	}

	private static final long serialVersionUID = 6628199715132440622L;

	/**
	 * Surrogate primary key.
	 */
	private String id;

	private String reftoken, parties, docLoc, court, url;
	
	private int year;

	/**
	 * Constructor
	 */
	public CaseRef() {
		super();
	}

	/**
	 * Is this a ref to a supreme court case?
	 * @return true/false
	 */
	public boolean isSupremeCourt() {
		return court != null && (court.indexOf("Supreme Court") >= 0);
	}

	/**
	 * Gets a custom formatted citation token.
	 * @param formatFlags the desired format directive bit flags based on
	 *        <code>CFF_*</code> flag constants.
	 * @return
	 */
	public String format(int formatFlags) {
		StringBuilder sb = new StringBuilder(512);

		if(isSupremeCourt()) {
			// FORMAT: New York Times Co. v. Sullivan, 376 U.S. 254 (1964).
			if(!CitationFormatFlag.hasFormat(formatFlags, CitationFormatFlag.EXCLUDE_PARTIES)) {
				sb.append(getParties());
				sb.append(", ");
			}
			sb.append(getDocLoc());
			sb.append(" (");
			sb.append(getYear());
			sb.append(").");
		}
		else {
			// FORMAT: Curtis Publishing Company v. Butts, 351 F. 2d 702 (5th Cir.
			// 1965)
			if(!CitationFormatFlag.hasFormat(formatFlags, CitationFormatFlag.EXCLUDE_PARTIES)) {
				sb.append(getParties());
				sb.append(", ");
			}
			sb.append(getDocLoc());
			sb.append(" (");
			String theCourt = getCourt();
			if(!StringUtil.isEmpty(theCourt)) {
				sb.append(theCourt);
				sb.append(" ");
			}
			sb.append(getYear());
			sb.append(")");
		}

		return sb.toString();
	}

	@Override
	public String descriptor() {
		// return typeDesc() + " (" + getCitation() + ")";
		return format(0); // default no exclusions
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
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		CaseRef cr = (CaseRef) cln;
		cr.id = id;
		cr.parties = parties;
		cr.reftoken = reftoken;
		cr.docLoc = docLoc;
		cr.court = court;
		cr.url = url;
		cr.year = year;
	}

	@Override
	protected IEntity newInstance() {
		return new CaseRef();
	}

	@Override
	public String getEntityType() {
		return EntityType.CASE.name();
	}

	/**
	 * @return the original citation token.
	 */
	public String getReftoken() {
		return reftoken;
	}

	public void setReftoken(String reftoken) {
		this.reftoken = reftoken;
	}

	/**
	 * @return the parties (e.g.:
	 *         "Board of Supervisors of James City Cty. v. Rowe")
	 */
	public String getParties() {
		return parties;
	}

	public void setParties(String parties) {
		this.parties = parties;
	}

	/**
	 * I.e.: "216 SE 2d 199"
	 * @return the citation's doc location ref token
	 */
	public String getDocLoc() {
		return docLoc;
	}

	public void setDocLoc(String docLoc) {
		this.docLoc = docLoc;
	}

	/**
	 * @return the sourcing url of this case.
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the 4-digit numeric case year (e.g.: 1975)
	 */
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the court (e.g.: "Supreme Court")
	 */
	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	@Override
	public int compareTo(CaseRef o) {
		if(year > o.year) return 1;
		if(o.year > year) return -1;
		if(parties != null && o.parties != null) {
			return parties.compareTo(o.parties);
		}
		return 0;
	}
}
