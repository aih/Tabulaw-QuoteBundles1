/**
 * The Logic Lab
 * @author jpk
 * @since Mar 31, 2010
 */
package com.tabulaw.common.data.dto;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author jpk
 */
public class Doc implements IsSerializable {

	private String id, title;

	private Date date;

	public Doc() {
		super();
	}

	public Doc(String title, Date date) {
		super();
		this.title = title;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Doc DTO [title=" + title + "]";
	}
}
