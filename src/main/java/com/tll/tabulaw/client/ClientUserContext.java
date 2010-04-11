/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.client;

import java.util.List;

import com.tll.common.model.Model;

/**
 * @author jpk
 */
public class ClientUserContext {

	private Model user;

	private List<Model> bundles;

	public Model getUser() {
		return user;
	}

	public void setUser(Model user) {
		this.user = user;
	}

	public List<Model> getBundles() {
		return bundles;
	}

	public void setBundles(List<Model> bundles) {
		this.bundles = bundles;
	}
}
