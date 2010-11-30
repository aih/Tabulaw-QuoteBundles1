/**

 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import com.sun.xml.txw2.annotation.XmlCDATA;
import com.tabulaw.cassandra.om.annotations.HelenaBean;
import com.tabulaw.cassandra.om.annotations.KeyProperty;

/**
 * Holds html content for a document.
 * <p>
 * A strong one-to-one relationship exists between this type and {@link DocRef}
 * type.
 * @author jpk
 */
@XmlRootElement(name = "docContent")
@HelenaBean(columnFamily = "DocContent", keyspace = "Tabulaw")
public class DocContent extends EntityBase {

	private static final long serialVersionUID = 7579142517520079175L;

	/**
	 * Surrogate primary key.
	 * <p>
	 * This is the same id as the <code>DocRef</code> id.
	 */
	@KeyProperty
	private String id;

	/**
	 * The HTML content of the doc.
	 */
	private String htmlContent;
	
	/**
	 * positions of page elements from the root element of the htmlContent 
	 */
	private List<int[]> pagesXPath;
	
	/**
	 * number of the first page
	 */
	private int firstPageNumber;

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
		dr.firstPageNumber = firstPageNumber;
		if (pagesXPath != null) {
			dr.pagesXPath = new ArrayList<int[]>(pagesXPath.size());
			for (int[] pageNode : pagesXPath) {
				if (pageNode != null) {
					int[] newNode = new int[pageNode.length];
					for (int i = 0; i < pageNode.length; i++) {
						newNode[i] = pageNode[i];
					}
					dr.pagesXPath.add(newNode);
				} else {
					dr.pagesXPath.add(null);
				}
			}
		} 
	}

	@Override
	@XmlTransient
	public String getEntityType() {
		return EntityType.DOC_CONTENT.name();
	}

	@NotEmpty
	@XmlElement
	@XmlCDATA
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	@XmlTransient
	public List<int[]> getPagesXPath() {
		return pagesXPath;
	}

	public void setPagesXPath(List<int[]> pagesXPath) {
		this.pagesXPath = pagesXPath;
	}
	
	@XmlTransient
	public int getFirstPageNumber() {
		return firstPageNumber;
	}
	
	public void setFirstPageNumber(int firstPageNumber) {
		this.firstPageNumber = firstPageNumber;
	}
}
