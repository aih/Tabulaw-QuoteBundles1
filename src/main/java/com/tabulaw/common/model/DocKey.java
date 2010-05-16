/**
 * The Logic Lab
 * @author jpk
 * @since May 16, 2010
 */
package com.tabulaw.common.model;

public class DocKey extends ModelKey {
	private static final long serialVersionUID = -6924690054100145683L;
	
	private String fullCitation;

	public DocKey() {
		super();
	}

	public DocKey(String type, String id, String name) {
		super(type, id, name);
	}

	public DocKey(String type, String id) {
		super(type, id);
	}

	public DocKey(String type, String id, String name, String fullCitation) {
		super(type, id, name);
		this.fullCitation = fullCitation;
	}

	public String getFullCitation() {
		return fullCitation;
	}

	public void setFullCitation(String fullCitation) {
		this.fullCitation = fullCitation;
	}

	@Override
	public String descriptor() {
		return fullCitation == null ? super.descriptor() : fullCitation;
	}
}