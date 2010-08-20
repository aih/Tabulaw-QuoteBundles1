/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import javax.validation.constraints.NotNull;

import com.tabulaw.model.bk.BusinessKeyDef;
import com.tabulaw.model.bk.BusinessObject;

/**
 * Associates a {@link Quote} to a {@link User}.
 * <p>
 * NOTE: No primary surrogate key is needed here.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Quote User Binding", properties = {
	"quoteId", "userId"
}))
public class QuoteUserBinding extends EntityBase {

	private static final long serialVersionUID = -1625084909880633134L;

	private String quoteId, userId;

	/**
	 * Constructor
	 */
	public QuoteUserBinding() {
		super();
	}

	/**
	 * Constructor
	 * @param quoteId
	 * @param userId
	 */
	public QuoteUserBinding(String quoteId, String userId) {
		super();
		this.quoteId = quoteId;
		this.userId = userId;
	}

	@Override
	protected void doClone(IEntity cln) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected IEntity newInstance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEntityType() {
		return EntityType.QUOTE_USER_BINDING.name();
	}

	@Override
	public String getId() {
		return quoteId + '|' + userId;
	}

	@Override
	public void setId(String id) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public String getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(String quoteId) {
		if(quoteId == null) throw new NullPointerException();
		this.quoteId = quoteId;
	}

	@NotNull
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		if(userId == null) throw new NullPointerException();
		this.userId = userId;
	}
}
