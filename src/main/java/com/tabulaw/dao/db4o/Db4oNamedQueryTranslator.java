/**
 * The Logic Lab
 * @author jpk
 * @since Sep 21, 2009
 */
package com.tabulaw.dao.db4o;

import java.util.List;

import com.db4o.query.Query;
import com.tabulaw.criteria.SelectNamedQueries;
import com.tabulaw.model.User;
import com.tll.criteria.ISelectNamedQueryDef;
import com.tll.criteria.InvalidCriteriaException;
import com.tll.dao.db4o.IDb4oNamedQueryTranslator;
import com.tll.model.INamedEntity;
import com.tll.schema.IQueryParam;

/**
 * Db4oNamedQueryTranslator
 * @author jpk
 */
public class Db4oNamedQueryTranslator implements IDb4oNamedQueryTranslator {

	@Override
	public void translateNamedQuery(ISelectNamedQueryDef queryDef, List<IQueryParam> params, Query q)
			throws InvalidCriteriaException {

		final String qname = queryDef.getQueryName();
		if(SelectNamedQueries.USER_LISTING.getQueryName().equals(qname)) {
			q.constrain(User.class);
			q.descend(INamedEntity.NAME).orderAscending();
		}

		else
			throw new InvalidCriteriaException("Unhandled named query: " + qname);
	}

}
