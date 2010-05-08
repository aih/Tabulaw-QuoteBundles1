/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import javax.validation.constraints.NotNull;

import com.tabulaw.schema.BusinessKeyDef;
import com.tabulaw.schema.BusinessObject;

/**
 * Associates a {@link DocRef} to a {@link User}.
 * <p>
 * NOTE: No primary surrogate key is needed here.
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Document User Binding", properties = {
	"docId", "userId"
}))
public class DocUserBinding extends EntityBase {
	private static final long serialVersionUID = -4833982902489953899L;
	
	private String docId, userId;

	/**
	 * Constructor
	 */
	public DocUserBinding() {
		super();
	}

	/**
	 * Constructor
	 * @param docId
	 * @param userId
	 */
	public DocUserBinding(String docId, String userId) {
		super();
		this.docId = docId;
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
		return EntityType.DOC_USER_BINDING.name();
	}

	@Override
	public String getId() {
		return docId + '|' + userId;
	}

	@Override
	public void setId(String id) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		if(docId == null) throw new NullPointerException();
		this.docId = docId;
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