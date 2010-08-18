/**
 * The Logic Lab
 */
package com.tabulaw.schema;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.tabulaw.IDescriptorProvider;
import com.tabulaw.ITypeDescriptorProvider;
import com.tabulaw.model.validate.AtLeastOne;

/**
 * SchemaInfoTest
 * @author jpk
 */
@Test(groups = "schema")
public class SchemaInfoTest {

	enum TestEnum {
		A,
		B,
		C;
	}

	static class AllTypesData implements Serializable {

		private static final long serialVersionUID = 1L;

		private TestEnum enm;
		private String string;
		private int integer;
		private double dbl;
		private float flot;
		private char character;
		private long lng;
		private Date date;

		public TestEnum getEnm() {
			return enm;
		}

		public void setEnm(TestEnum enm) {
			this.enm = enm;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		public int getInteger() {
			return integer;
		}

		public void setInteger(int integer) {
			this.integer = integer;
		}

		public double getDbl() {
			return dbl;
		}

		public void setDbl(double dbl) {
			this.dbl = dbl;
		}

		public float getFlot() {
			return flot;
		}

		public void setFlot(float flot) {
			this.flot = flot;
		}

		public char getCharacter() {
			return character;
		}

		public void setCharacter(char character) {
			this.character = character;
		}

		public long getLng() {
			return lng;
		}

		public void setLng(long lng) {
			this.lng = lng;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
	}

	static class EntityBase {

		private long id;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

	}

	static class NamedTimeStampEntity extends EntityBase {

		private String name;
		private Date dateCreated, dateModified;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getDateCreated() {
			return dateCreated;
		}

		public void setDateCreated(Date dateCreated) {
			this.dateCreated = dateCreated;
		}

		public Date getDateModified() {
			return dateModified;
		}

		public void setDateModified(Date dateModified) {
			this.dateModified = dateModified;
		}
	}

	static class TestEntityA extends EntityBase {

		private String aProp;

		public String getAProp() {
			return aProp;
		}

		public void setAProp(String prop) {
			aProp = prop;
		}

	}

	static class TestEntityB extends EntityBase {

		private TestEntityA entityA;

		public TestEntityA getEntityA() {
			return entityA;
		}

		public void setEntityA(TestEntityA entityA) {
			this.entityA = entityA;
		}
	}

	static class TestEntityC extends NamedTimeStampEntity {

		private static final long serialVersionUID = -8237732782824087760L;
		public static final int MAXLEN_NAME = 64;

		private TestEnum enm;
		private String string;
		private int integer;
		private double dbl;
		private float flot;
		private char character;
		private long lng;
		private Date date;
		private TestEntityB relatedOne;
		private Set<TestEntityC> relatedMany = new LinkedHashSet<TestEntityC>();
		private transient AllTypesData nested;
		private Map<String, String> smap;

		@Override
		@NotEmpty
		@Length(max = MAXLEN_NAME)
		public String getName() {
			return super.getName();
		}

		public TestEnum getEnm() {
			return enm;
		}

		public void setEnm(TestEnum enm) {
			this.enm = enm;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		public int getInteger() {
			return integer;
		}

		public void setInteger(int integer) {
			this.integer = integer;
		}

		public double getDbl() {
			return dbl;
		}

		public void setDbl(double dbl) {
			this.dbl = dbl;
		}

		public float getFlot() {
			return flot;
		}

		public void setFlot(float flot) {
			this.flot = flot;
		}

		public char getCharacter() {
			return character;
		}

		public void setCharacter(char character) {
			this.character = character;
		}

		public long getLng() {
			return lng;
		}

		public void setLng(long lng) {
			this.lng = lng;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		@Nested
		@NotNull
		public AllTypesData getNested() {
			return nested;
		}

		public void setNested(AllTypesData testData) {
			this.nested = testData;
		}

		public TestEntityB getRelatedOne() {
			return relatedOne;
		}

		public void setRelatedOne(TestEntityB relatedOne) {
			this.relatedOne = relatedOne;
		}

		@AtLeastOne(type = "relatedMany")
		// @BusinessKeyUniqueness(type = "relatedMany")
		// TODO create bk validator test elsewhere
		@Valid
		public Set<TestEntityC> getRelatedMany() {
			return relatedMany;
		}

		public void setRelatedMany(Set<TestEntityC> related) {
			this.relatedMany = related;
		}

		public Map<String, String> getSmap() {
			return smap;
		}

		public void setSmap(Map<String, String> smap) {
			this.smap = smap;
		}
	}

	static class TestEntityMetadata implements IEntityMetadata {

		@Override
		public boolean isEntityType(Class<?> claz) {
			return EntityBase.class.isAssignableFrom(claz);
		}

		@Override
		public Class<?> getEntityClass(Object entity) {
			return entity == null ? null : entity.getClass();
		}

		@Override
		public String getEntityInstanceDescriptor(Object entity) {
			if(entity == null) return null;
			if(entity instanceof IDescriptorProvider) return ((IDescriptorProvider) entity).descriptor();
			return entity.toString();
		}

		@Override
		public String getEntityTypeDescriptor(Object entity) {
			if(entity == null) return null;
			if(entity instanceof ITypeDescriptorProvider) return ((ITypeDescriptorProvider) entity).typeDesc();
			return entity.getClass().getSimpleName();
		}

		@Override
		public Class<?> getRootEntityClass(Class<?> entityClass) {
			if(entityClass.getAnnotation(Extended.class) != null) {
				Class<?> ec = entityClass;
				do {
					ec = ec.getSuperclass();
				} while(ec != null && ec.getAnnotation(Root.class) == null);
				if(ec != null) return ec;
			}
			return entityClass;
		}
	}

	static final IEntityMetadata entityMetadata = new TestEntityMetadata();

	/**
	 * Constructor
	 */
	public SchemaInfoTest() {
		super();
	}

	@Test
	public void test() throws Exception {
		final ISchemaInfo si = new SchemaInfo(entityMetadata);
		Assert.assertNotNull(si);
		ISchemaProperty sp;

		sp = si.getSchemaProperty(TestEntityC.class, "enm");
		assert sp.getPropertyType() == PropertyType.ENUM;

		sp = si.getSchemaProperty(TestEntityC.class, "string");
		assert sp.getPropertyType() == PropertyType.STRING;

		sp = si.getSchemaProperty(TestEntityC.class, "integer");
		assert sp.getPropertyType() == PropertyType.INT;

		sp = si.getSchemaProperty(TestEntityC.class, "dbl");
		assert sp.getPropertyType() == PropertyType.DOUBLE;

		sp = si.getSchemaProperty(TestEntityC.class, "flot");
		assert sp.getPropertyType() == PropertyType.FLOAT;

		sp = si.getSchemaProperty(TestEntityC.class, "character");
		assert sp.getPropertyType() == PropertyType.CHAR;

		sp = si.getSchemaProperty(TestEntityC.class, "lng");
		assert sp.getPropertyType() == PropertyType.LONG;

		sp = si.getSchemaProperty(TestEntityC.class, "date");
		assert sp.getPropertyType() == PropertyType.DATE;

		sp = si.getSchemaProperty(TestEntityC.class, "relatedOne");
		assert sp.getPropertyType() == PropertyType.RELATED_ONE;

		sp = si.getSchemaProperty(TestEntityC.class, "relatedMany");
		assert sp.getPropertyType() == PropertyType.RELATED_MANY;

		sp = si.getSchemaProperty(TestEntityC.class, "nested");
		assert sp.getPropertyType() == PropertyType.NESTED;

		sp = si.getSchemaProperty(TestEntityC.class, "relatedOne.entityA.aProp");
		assert sp.getPropertyType() == PropertyType.STRING;

		sp = si.getSchemaProperty(TestEntityC.class, "smap");
		assert sp.getPropertyType() == PropertyType.STRING_MAP;
	}
}
