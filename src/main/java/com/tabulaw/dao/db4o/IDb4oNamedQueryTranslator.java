/**
 * The Logic Lab
 * @author jpk
 * @since Sep 21, 2009
 */
package com.tabulaw.dao.db4o;

import java.util.List;

import com.db4o.query.Query;
import com.tabulaw.criteria.ISelectNamedQueryDef;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tll.schema.IQueryParam;

/**
 * IDb4oNamedQueryTranslator - Since db4o doesn't support named queries, we
 * institute an interface to handle them.
 * @author jpk
 */
public interface IDb4oNamedQueryTranslator {

	/**
	 * Translates native named query search criteria putting the results in a
	 * given empy db4o query instance.
	 * @param queryDef native query def
	 * @param params native query params
	 * @param db4oQuery The empty db4o query to fill
	 * @throws InvalidCriteriaException When the translation fails for any reason
	 */
	void translateNamedQuery(ISelectNamedQueryDef queryDef, List<IQueryParam> params, Query db4oQuery)
	throws InvalidCriteriaException;
}
