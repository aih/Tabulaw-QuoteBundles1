/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.server.rpc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.tabulaw.common.data.rpc.Payload;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.server.PersistContext;

/**
 * @author jpk
 */
public class PersistHelper {

	@SuppressWarnings("unchecked")
	public static void handleValidationException(PersistContext context, ConstraintViolationException cve,
			Payload payload) {
		// final Class<? extends IEntity> entityClass =
		// (Class<? extends IEntity>)
		// context.getEntityTypeResolver().resolveEntityClass(model.getEntityType());
		for(final ConstraintViolation<?> iv : cve.getConstraintViolations()) {
			// resolve index if we have a violation on under an indexed entity
			// property
			// since the validation api doesn't provide the index rather only empty
			// brackets ([])
			// in the ConstraintViolation's propertyPath property
			payload.getStatus().addMsg(iv.getMessage(), MsgLevel.ERROR, MsgAttr.FIELD.flag,
					iv.getPropertyPath().toString());
		}
	}

	private PersistHelper() {
	}
}
