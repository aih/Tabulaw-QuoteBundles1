/**
 * The Logic Lab
 * @author jpk
 */
package com.tll.client.ui.field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.tll.client.ui.IWidgetRef;
import com.tll.client.validate.CompositeValidator;
import com.tll.client.validate.Error;
import com.tll.client.validate.ErrorClassifier;
import com.tll.client.validate.ErrorDisplay;
import com.tll.client.validate.IErrorHandler;
import com.tll.client.validate.IValidator;
import com.tll.client.validate.ValidationException;
import com.tll.schema.IPropertyMetadataProvider;
import com.tll.util.PropertyPath;
import com.tll.util.StringUtil;

/**
 * FieldGroup - A group of {@link IField}s which may in turn be nested
 * {@link FieldGroup}s. Thus a FieldGroup is a hierarchical collection of
 * {@link IField}s.
 * <p>
 * Non-FieldGroup children of {@link FieldGroup}s are:
 * <ol>
 * <li>Assumed to be {@link IFieldWidget} instances
 * <li>Expected to have a unique <em>property</em> name (in addition to a unique
 * name)
 * </ol>
 * Unique property names enable the resolution of {@link IFieldWidget}s.
 * <p>
 * A {@link FieldGroup} is a grouping of {@link IField}s for UI purposes
 * <em>does not necessarily represent model hierarchy boundaries</em>
 * <p>
 * <b>IMPT: </b> {@link FieldGroup}s do <em>NOT</em> (as yet) support handling
 * of circular references! As such, do not add a field to a field group that is
 * a child of another field group where both are children of a common ancestor
 * field group.
 * @author jpk
 */
public final class FieldGroup implements IField, Iterable<IField> {

	/**
	 * Recursively searches the given field group for a field whose name matches
	 * that given. The first found field is returned.
	 * @param name The name to search for. If <code>null</code> is specified,
	 *        <code>null</code> is returned.
	 * @param group The group to search in
	 * @return The found IField or <code>null</code> if no matching field found
	 */
	private static IField findByName(final String name, FieldGroup group) {
		if(name == null) return null;
		if(name.equals(group.name)) return group;

		// first go through the non-group child fields
		for(final IField fld : group) {
			if(fld instanceof FieldGroup == false) {
				if(name.equals(fld.getName())) {
					return fld;
				}
			}
		}

		IField rfld;
		for(final IField fld : group) {
			if(fld instanceof FieldGroup) {
				rfld = findByName(name, (FieldGroup) fld);
				if(rfld != null) return rfld;
			}
		}

		return null;
	}

	/**
	 * Recursively searches the given field group for a nested field whose
	 * property name matches that given. The first found field is returned.
	 * @param propertyName The property name to search for
	 * @param group The group to search in
	 * @return The found IField or <code>null</code> if no matching field found
	 */
	private static IFieldWidget<?> findByPropertyName(final String propertyName, FieldGroup group) {

		// first go through the non-group child fields
		for(final IField fld : group) {
			if(fld instanceof IFieldWidget<?>) {
				if(((IFieldWidget<?>) fld).getPropertyName().equals(propertyName)) {
					return (IFieldWidget<?>) fld;
				}
			}
		}

		IFieldWidget<?> rfld;
		for(final IField fld : group) {
			if(fld instanceof FieldGroup) {
				rfld = findByPropertyName(propertyName, (FieldGroup) fld);
				if(rfld != null) return rfld;
			}
		}

		return null;
	}

	/**
	 * Recursively extracts all {@link IFieldWidget}s whose property name matches
	 * by regular expression the given property path. The found fields are added
	 * to the given set.
	 * @param propPath The regex token of the property path. If <code>null</code>,
	 *        all encountered {@link IFieldWidget}s are included
	 * @param group The field group to search
	 * @param set The set of found fields
	 */
	private static void findFieldWidgets(final String propPath, FieldGroup group, Set<IFieldWidget<?>> set) {
		Set<FieldGroup> gset = null;
		for(final IField fld : group) {
			if(fld instanceof IFieldWidget<?>) {
				if(propPath == null || ((IFieldWidget<?>) fld).getPropertyName().matches(propPath)) {
					set.add((IFieldWidget<?>) fld);
				}
			}
			else {
				if(gset == null) gset = new HashSet<FieldGroup>();
				gset.add((FieldGroup) fld);
			}
		}
		if(gset != null) {
			for(final FieldGroup fg : gset) {
				findFieldWidgets(propPath, fg, set);
			}
		}
	}

	/**
	 * Recursively pre-pends the given parent property path to all applicable
	 * child fields' property names.
	 * @param field
	 * @param parentPropPath The parent property path to pre-pend
	 */
	private static void setParentPropertyPath(IField field, final String parentPropPath) {
		if(field instanceof FieldGroup) {
			for(final IField f : (FieldGroup) field) {
				setParentPropertyPath(f, parentPropPath);
			}
		}
		else {
			assert field instanceof IFieldWidget<?>;
			((IFieldWidget<?>) field).setPropertyName(PropertyPath.getPropertyPath(parentPropPath, ((IFieldWidget<?>) field)
					.getPropertyName()));
		}
	}

	/**
	 * Recursively replaces the parent property path with the given replacement
	 * parent property path.
	 * @param field the current field
	 * @param old the existing parent property path
	 * @param repl the replacement parent property path
	 */
	private static void replaceParentPropertyPath(IField field, final String old, final String repl) {
		if(field instanceof FieldGroup) {
			for(final IField f : (FieldGroup) field) {
				replaceParentPropertyPath(f, old, repl);
			}
		}
		else {
			assert field instanceof IFieldWidget<?>;
			final IFieldWidget<?> fw = ((IFieldWidget<?>) field);
			final String pname = fw.getPropertyName();
			final PropertyPath p = new PropertyPath(pname);
			p.replace(old, repl);
			fw.setPropertyName(p.toString());
		}
	}

	/**
	 * Verifies a field's worthiness to add to this group based on:
	 * <ol>
	 * <li>Name uniqueness for <em>all</em> existing fields in this group.
	 * <li>Property name uniqueness <em>if</em> the given field is an
	 * {@link IFieldWidget} instance.
	 * </ol>
	 * @param f the field to verify
	 * @param group
	 * @throws IllegalArgumentException When the verification fails.
	 */
	private static void verifyAddField(final IField f, FieldGroup group) throws IllegalArgumentException {
		assert group != null;
		if(f == null) throw new IllegalArgumentException("No field specified.");
		final boolean isWidget = (f instanceof IFieldWidget<?>);
		for(final IField ef : group) {
			if(f.getName().equals(ef.getName())) {
				throw new IllegalArgumentException("Field name: '" + f.getName() + "' already exists.");
			}
			if(isWidget && (ef instanceof IFieldWidget<?>)) {
				if(((IFieldWidget<?>) f).getPropertyName().equals(((IFieldWidget<?>) ef).getPropertyName())) {
					throw new IllegalArgumentException("Field property name: '" + ((IFieldWidget<?>) f).getPropertyName()
							+ "' already exists.");
				}
			}
			if(ef instanceof FieldGroup) {
				verifyAddField(f, (FieldGroup) ef);
			}
		}
	}

	/**
	 * Creates a nested path token.
	 * @param parents
	 * @param field
	 * @param includeFirstParent include the first parent in the path?
	 * @return the created nested path
	 */
	private static String nestedPath(List<IField> parents, IField field, boolean includeFirstParent) {
		final StringBuilder sb = new StringBuilder();
		for(int i = (includeFirstParent ? 0 : 1); i < parents.size(); i++) {
			sb.append(parents.get(i).descriptor());
			sb.append(" - ");
		}
		sb.append(field.descriptor());
		return sb.toString();
	}

	/**
	 * Recursive validation routine that populates a list or {@link Error}s.
	 * instance and tracks the nesting. Nesting is tracked to assemble a "fully
	 * qualified" field better validation feedback.
	 * @param errors the sole constant instance
	 * @param group the field group
	 * @param parents the field group parents
	 */
	private static void validate(final ArrayList<Error> errors, FieldGroup group, List<FieldGroup> parents) {
		for(final IField field : group) {
			if(field instanceof FieldGroup) {
				final ArrayList<FieldGroup> list = new ArrayList<FieldGroup>(parents.size() + 1);
				list.addAll(parents);
				list.add(group);
				validate(errors, ((FieldGroup) field), list);
			}
			else {
				try {
					field.validate();
				}
				catch(final ValidationException e) {
					final ArrayList<IField> list = new ArrayList<IField>(parents.size() + 1);
					list.addAll(parents);
					list.add(group);
					for(final Error error : e.getErrors()) {
						error.setTarget(new IWidgetRef() {

							@Override
							public Widget getWidget() {
								return field.getWidget();
							}

							@SuppressWarnings("synthetic-access")
							@Override
							public String descriptor() {
								return nestedPath(list, field, false);
							}
						});
					}
					errors.addAll(e.getErrors());
				}
			}
		}

		if(group.validator != null) {
			try {
				group.validator.validate(null);
			}
			catch(final ValidationException e) {
				for(final Error error : e.getErrors()) {
					error.setTarget(group);
				}
				errors.addAll(e.getErrors());
			}
		}
	}

	/**
	 * The required presentation worthy and unique name to ascribe to this group.
	 */
	private String name;

	/**
	 * The collection of child fields with order preservation.
	 */
	private final LinkedHashSet<IField> fields = new LinkedHashSet<IField>();

	/**
	 * The field group validator(s).
	 */
	private CompositeValidator validator;

	/**
	 * The Widget that is used to convey validation feedback.
	 */
	private Widget feedbackWidget;

	/**
	 * The error handler to employ for validation.
	 */
	private IErrorHandler errorHandler;

	/**
	 * Constructor
	 * @param name The required unique name for this field group.
	 */
	public FieldGroup(String name) {
		super();
		setName(name);
	}

	@Override
	public String descriptor() {
		return getName();
	}

	public Iterator<IField> iterator() {
		return fields.iterator();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(StringUtil.isEmpty(name)) {
			throw new IllegalArgumentException("A field group must have a name.");
		}
		this.name = name;
	}

	/**
	 * @return The designated Widget to receive validation messages.
	 */
	public Widget getWidget() {
		return feedbackWidget;
	}

	/**
	 * Sets the Widget to for which validation messages are bound.
	 * @param feedbackWidget A Widget designated to be the validation feeback
	 *        hook.
	 */
	public void setWidget(Widget feedbackWidget) {
		this.feedbackWidget = feedbackWidget;
	}

	/**
	 * Recursively searches for a field having the given name.
	 * @param nme
	 * @return The found field or <code>null</code> if no field exists with the
	 *         given name.
	 */
	public IField getFieldByName(String nme) {
		return findByName(nme, this);
	}

	/**
	 * Recursively searches for a field widget having the given name.
	 * @param nme Unique name of the field widget to retrieve
	 * @return The found field widget or <code>null</code> if no field widget
	 *         exists with the given name.
	 */
	public IFieldWidget<?> getFieldWidget(String nme) {
		final IField f = findByName(nme, this);
		return f instanceof IFieldWidget<?> ? (IFieldWidget<?>) f : null;
	}

	/**
	 * Recursively searches for a field widget having the given property name.
	 * @param propertyName
	 * @return The found field or <code>null</code> if it doesn't exist.
	 */
	public IFieldWidget<?> getFieldWidgetByProperty(String propertyName) {
		return propertyName == null ? null : findByPropertyName(propertyName, this);
	}

	/**
	 * Finds all {@link IFieldWidget}s whose property name matches the given regex
	 * prop path token.
	 * @param propPath The property path as a regular expression token. If
	 *        <code>null</code>, all {@link IFieldWidget}s are included.
	 * @return Set of matching fields never <code>null</code> but may be empty
	 *         (when no matches found).
	 */
	public Set<IFieldWidget<?>> getFieldWidgets(String propPath) {
		final Set<IFieldWidget<?>> set = new HashSet<IFieldWidget<?>>();
		findFieldWidgets(propPath, this, set);
		return set;
	}

	/**
	 * Adds a field to this field group.
	 * @param field The field to add
	 * @throws IllegalArgumentException When this field instance already exists or
	 *         another field exists with the same name.
	 */
	public void addField(IField field) throws IllegalArgumentException {
		verifyAddField(field, this);
		// NOTE: the field add op should go through based on the verify routine
		if(!fields.add(field)) throw new IllegalStateException();
	}

	/**
	 * Adds a field to this field group pre-pending the given parent property path
	 * to the field's <em>existing</em> property name.
	 * @param parentPropPath Pre-pended to the field's existing property path
	 *        before the field is added. May be <code>null</code> in which case
	 *        the field's property name remains un-altered.
	 * @param field The field to add
	 */
	public void addField(String parentPropPath, IField field) {
		setParentPropertyPath(field, parentPropPath);
		addField(field);
	}

	/**
	 * Recursively pre-pends the given parent property path to all held field
	 * widgets in this group.
	 * @param parentPropPath
	 */
	public void setParentPropertyPath(String parentPropPath) {
		setParentPropertyPath(this, parentPropPath);
	}

	/**
	 * Replaces the parent property paths of all held field widgets.
	 * @param existing the existing parent property path to replace
	 * @param repl the replacement parent property path
	 */
	public void replaceParentPropertyPath(String existing, String repl) {
		replaceParentPropertyPath(this, existing, repl);
	}

	/**
	 * Adds multiple fields to this group.
	 * @param flds The fields to add
	 */
	public void addFields(Iterable<IField> flds) {
		if(flds != null) {
			for(final IField fld : flds) {
				addField(fld);
			}
		}
	}

	/**
	 * Adds an array of fields to this group.
	 * @param flds The array of fields to add
	 */
	public void addFields(IField[] flds) {
		if(flds != null) {
			for(final IField fld : flds) {
				addField(fld);
			}
		}
	}

	/**
	 * Adds multiple fields to this group.
	 * @param parentPropPath Pre-pended to the each field's property name before
	 *        the fields are added. May be <code>null</code> in which case the
	 *        fields' property names remain un-altered.
	 * @param flds The fields to add
	 */
	public void addFields(String parentPropPath, Iterable<IField> flds) {
		if(flds != null) {
			for(final IField fld : flds) {
				addField(parentPropPath, fld);
			}
		}
	}

	/**
	 * Adds multiple fields to this group.
	 * @param parentPropPath Pre-pended to the each field's property name before
	 *        the fields are added. May be <code>null</code> in which case the
	 *        fields' property names remain un-altered.
	 * @param flds The fields to add
	 */
	public void addFields(String parentPropPath, IField[] flds) {
		if(flds != null) {
			for(final IField fld : flds) {
				addField(parentPropPath, fld);
			}
		}
	}

	/**
	 * Recursively removes all errors bound to the given field and any and all child fields.
	 * @param field the target field
	 */
	private void clearErrors(IField field) {
		assert errorHandler != null;
		if(field instanceof FieldGroup) {
			for(final IField fld : ((FieldGroup) field)) {
				clearErrors(fld);
			}
		}
		else {
			errorHandler.resolveError(field, ErrorClassifier.CLIENT, ErrorDisplay.ALL_FLAGS);
		}
	}

	/**
	 * Removes a field by reference searching recursively. If the given field is
	 * <code>null</code> or is <em>this</em> field group, no field is removed and
	 * <code>false</code> is returned.
	 * @param field The field to remove.
	 * @param clearErrors remove all errors bound to the field?
	 * @return <code>true</code> if the field was removed, <code>false</code> if
	 *         not.
	 */
	public boolean removeField(IField field, final boolean clearErrors) {
		if(field != null && !(field == this)) {
			boolean rval = false;
			for(final IField fld : fields) {
				if(fld == field) {
					if(!fields.remove(field)) throw new IllegalStateException("Unable to remove field: " + field);
					rval = true;
					break;
				}
				else if(fld instanceof FieldGroup) {
					if(((FieldGroup) fld).removeField(field, clearErrors)) {
						rval = true;
						break;
					}
				}
			}
			if(rval && clearErrors && errorHandler != null) {
				// remove all associated errors for the removed field
				clearErrors(field);
			}
			return rval;
		}
		return false;
	}

	/**
	 * Removes a collection of fields from this group.
	 * @param clc The collection of fields to remove
	 * @param clearErrors Remove errors bound to the given fields?
	 */
	public void removeFields(Iterable<IField> clc, final boolean clearErrors) {
		if(clc != null) {
			for(final IField fld : clc) {
				removeField(fld, clearErrors);
			}
		}
	}

	@Override
	public void applyPropertyMetadata(IPropertyMetadataProvider provider, boolean isNewModelData) {
		for(final IField f : fields) {
			f.applyPropertyMetadata(provider, isNewModelData);
		}
	}

	@Override
	public boolean isRequired() {
		for(final IField field : fields) {
			if(field.isRequired()) return true;
		}
		return false;
	}

	@Override
	public void setRequired(boolean required) {
		for(final IField field : fields) {
			field.setRequired(required);
		}
	}

	@Override
	public boolean isReadOnly() {
		for(final IField field : fields) {
			if(!field.isReadOnly()) return false;
		}
		return true;
	}

	/**
	 * Iterates over the child fields, setting their readOnly property.
	 * @param readOnly true/false
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		for(final IField field : fields) {
			field.setReadOnly(readOnly);
		}
	}

	@Override
	public boolean isEnabled() {
		for(final IField field : fields) {
			if(!field.isEnabled()) return false;
		}
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
		for(final IField field : fields) {
			field.setEnabled(enabled);
		}
	}

	@Override
	public boolean isVisible() {
		for(final IField field : fields) {
			if(field.isVisible()) return true;
		}
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		for(final IField field : fields) {
			field.setVisible(visible);
		}
	}

	@Override
	public void clearValue() {
		for(final IField f : fields) {
			f.clearValue();
		}
	}

	/**
	 * Removes all child fields from this group.
	 */
	public void clear() {
		fields.clear();
	}

	@Override
	public void reset() {
		for(final IField f : fields) {
			f.reset();
		}
	}

	@Override
	public void validateIncrementally(boolean validate) {
		for(final IField f : fields) {
			f.validateIncrementally(validate);
		}
	}

	/**
	 * @return The number of child fields.
	 */
	public int size() {
		return fields.size();
	}

	public void addValidator(IValidator vldtr) {
		if(vldtr != null) {
			if(this.validator == null) {
				this.validator = new CompositeValidator();
			}
			this.validator.add(vldtr);
		}
	}

	public void removeValidator(Class<? extends IValidator> type) {
		if(validator != null) this.validator.remove(type);
	}

	public void validate() throws ValidationException {
		final ArrayList<Error> errors = new ArrayList<Error>();
		validate(errors, this, new ArrayList<FieldGroup>());
		if(errors.size() > 0) {
			throw new ValidationException(errors);
		}
	}

	@Override
	public IErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public void setErrorHandler(IErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		for(final IField f : fields) {
			f.setErrorHandler(errorHandler);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final FieldGroup other = (FieldGroup) obj;
		assert name != null;
		return name.equals(other.name);
	}

	@Override
	public int hashCode() {
		assert name != null;
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "FieldGroup[" + name + ']';
	}
}
