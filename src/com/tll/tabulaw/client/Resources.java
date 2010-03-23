/**
 * The Logic Lab
 * @author jpk
 * @since Feb 17, 2010
 */
package com.tll.tabulaw.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * Poc app resources.
 * @author jpk
 */
public interface Resources extends ClientBundle {
		
	Resources INSTANCE = GWT.create(Resources.class);
	
	@Source("note-icon.png")
	ImageResource searchImage();
	
	/*
	@Source("cases/Buckley-v-Valeo.htm")
  DataResource caseBuckley();

	@Source("cases/CitizensUnited-v-FEC.htm")
  DataResource caseCitizensUnited();

	@Source("cases/FirstNatl-v-Belotti.htm")
  DataResource caseFirstNatl();

	@Source("cases/McConnell-v-FEC.htm")
  DataResource caseMcConnell();

	@Source("cases/Times-v-Sullivan.htm")
  DataResource caseTimes();
  */
}
