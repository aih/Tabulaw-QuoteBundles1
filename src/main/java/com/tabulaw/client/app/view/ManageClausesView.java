/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 30, 2010
 */
package com.tabulaw.client.app.view;

import com.tabulaw.client.app.ui.ClausesManageWidget;
import com.tabulaw.client.view.StaticViewInitializer;
import com.tabulaw.client.view.ViewClass;

/**
 * ManageClausesView
 * @author jopaki
 */
public class ManageClausesView extends AbstractPocView<StaticViewInitializer> {

	public static final Class klas = new Class();

	public static final class Class extends PocViewClass {

		@Override
		public String getName() {
			return "clauses";
		}

		@Override
		public ManageClausesView newView() {
			return new ManageClausesView();
		}
	}

	private final ClausesManageWidget mcw;

	/**
	 * Constructor
	 */
	public ManageClausesView() {
		super();
		mcw = new ClausesManageWidget("Testing");
	}

	@Override
	public String getLongViewName() {
		return "Manage Clauses";
	}

	@Override
	public ViewClass getViewClass() {
		return klas;
	}

	@Override
	protected void doInitialization(StaticViewInitializer initializer) {
		addWidget(mcw);
		mcw.makeModelChangeAware();
	}

	@Override
	protected void doRefresh() {
		// mcw.refresh();
	}

	@Override
	protected void doDestroy() {
		mcw.unmakeModelChangeAware();
	}

}
