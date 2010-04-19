/**
 * The Logic Lab
 * @author jpk
 * @since Apr 11, 2010
 */
package com.tabulaw.server.rpc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.model.IEntity;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.schema.ISchemaInfo;
import com.tabulaw.schema.ISchemaProperty;
import com.tabulaw.server.PersistContext;
import com.tabulaw.util.PropertyPath;

/**
 * @author jpk
 */
public class PersistHelper {

	/**
	 * Client-izes the given property path (need to account for possible nested).
	 * <p>
	 * It is assumed nested entities are only 1-level deep
	 * @param <T> the entity type
	 * @param schemaInfo
	 * @param entityClass
	 * @param path
	 * @return the clientized path
	 */
	public static final <T> String clientizePropertyPath(ISchemaInfo schemaInfo, Class<T> entityClass, String path) {
		final PropertyPath p = new PropertyPath(path);
		if(p.depth() > 2) {
			final String ppp = p.trim(1);
			final ISchemaProperty sp = schemaInfo.getSchemaProperty(entityClass, ppp);
			if(sp.getPropertyType().isNested()) {
				path = ppp + '_' + p.last();
			}
		}
		return path;
	}

	@SuppressWarnings("unchecked")
	public static void handleValidationException(PersistContext context, ConstraintViolationException cve,
			ModelPayload payload) {
		// final Class<? extends IEntity> entityClass =
		// (Class<? extends IEntity>)
		// context.getEntityTypeResolver().resolveEntityClass(model.getEntityType());
		for(final ConstraintViolation<?> iv : cve.getConstraintViolations()) {
			// resolve index if we have a violation on under an indexed entity
			// property
			// since the validation api doesn't provide the index rather only empty
			// brackets ([])
			// in the ConstraintViolation's propertyPath property
			Class<? extends IEntity> entityClass = (Class<? extends IEntity>) iv.getRootBeanClass();
			payload.getStatus().addMsg(iv.getMessage(), MsgLevel.ERROR, MsgAttr.FIELD.flag,
					clientizePropertyPath(context.getSchemaInfo(), entityClass, iv.getPropertyPath().toString()));
		}
	}

	private PersistHelper() {
	}
}
