/**
 * The Logic Lab
 * @author jpk
 * @since Sep 21, 2009
 */
package com.tabulaw.di;

import com.db4o.config.Configuration;
import com.tabulaw.common.model.Authority;
import com.tabulaw.common.model.BundleUserBinding;
import com.tabulaw.common.model.CaseRef;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.dao.db4o.Db4oNamedQueryTranslator;
import com.tll.config.Config;
import com.tll.dao.db4o.IDb4oNamedQueryTranslator;
import com.tll.di.AbstractDb4oDaoModule;


/**
 * Db4oDaoModule
 * @author jpk
 */
public class Db4oDaoModule extends AbstractDb4oDaoModule {

	/**
	 * Constructor
	 */
	public Db4oDaoModule() {
		super();
	}

	/**
	 * Constructor
	 * @param config
	 */
	public Db4oDaoModule(Config config) {
		super(config);
	}

	@Override
	protected void configureConfiguration(Configuration c) {
		c.objectClass(Authority.class).updateDepth(1);
		c.objectClass(User.class).updateDepth(2);
		c.objectClass(CaseRef.class).updateDepth(1);
		c.objectClass(DocRef.class).updateDepth(2);
		c.objectClass(Quote.class).updateDepth(1);
		c.objectClass(QuoteBundle.class).updateDepth(2);
		c.objectClass(BundleUserBinding.class).updateDepth(1);
	}

	@Override
	protected Class<? extends IDb4oNamedQueryTranslator> getNamedQueryTranslatorImpl() {
		return Db4oNamedQueryTranslator.class;
	}

}
