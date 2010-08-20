/**
 * The Logic Lab
 */
package com.tabulaw.model.validate;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.testng.annotations.Test;

import com.tabulaw.model.IEntity;
import com.tabulaw.model.INamedEntity;
import com.tabulaw.model.TimeStampEntity;

/**
 * AbstractValidatorTest
 * @author jpk
 */
@Test(groups = "model.validate")
public class ValidatorsTest {

	private static final Log logger = LogFactory.getLog(ValidatorsTest.class);

	/**
	 * Constructor
	 */
	public ValidatorsTest() {
		super();
	}

	/**
	 * TestEntity
	 * @author jpk
	 */
	public static final class TestEntity extends TimeStampEntity implements INamedEntity {

		private static final long serialVersionUID = 1L;

		private String name;

		private String phoneNumber;

		protected Set<TestEntity> relatedMany = new LinkedHashSet<TestEntity>();

		@Override
		protected IEntity newInstance() {
			return new TestEntity();
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getEntityType() {
			return "TEST_ENTITY";
		}

		@Override
		public String getId() {
			return getName();
		}

		@Override
		public void setId(String id) {
			setName(id);
		}

		@NotEmpty
		@Override
		public String getName() {
			return name;
		}

		@NotEmpty
		@Length(max = 32, message = "Invalid phone number")
		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		@AtLeastOne(type = "test entity")
		@Valid
		public Set<TestEntity> getRelatedMany() {
			return relatedMany;
		}

		public void setRelatedMany(Set<TestEntity> relatedMany) {
			this.relatedMany = relatedMany;
		}

		@Override
		public String typeDesc() {
			return "Test Entity";
		}
	}

	TestEntity getTestEntity() {
		final TestEntity e = new TestEntity();
		e.setName("name");
		return e;
	}

	/**
	 * Tests entity validation.
	 * @throws Exception
	 */
	public final void testEntityValidation() throws Exception {
		final TestEntity e = getTestEntity();
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		assert validator != null;
		final Set<ConstraintViolation<TestEntity>> invalids = validator.validate(e);
		for(final ConstraintViolation<TestEntity> em : invalids) {
			logger.debug("prop: " + em.getPropertyPath() + ", msg: " + em.getMessage());
		}
	}

}
