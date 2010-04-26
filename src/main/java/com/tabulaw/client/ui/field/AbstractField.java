/**
 * The Logic Lab
 * @author jpk Sep 14, 2007
 */
package com.tabulaw.client.ui.field;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.tabulaw.client.convert.IConverter;
import com.tabulaw.client.ui.IHasFormat;
import com.tabulaw.client.util.Fmt;
import com.tabulaw.client.util.GlobalFormat;
import com.tabulaw.client.validate.BooleanValidator;
import com.tabulaw.client.validate.CharacterValidator;
import com.tabulaw.client.validate.CompositeValidator;
import com.tabulaw.client.validate.DateValidator;
import com.tabulaw.client.validate.DecimalValidator;
import com.tabulaw.client.validate.Error;
import com.tabulaw.client.validate.ErrorClassifier;
import com.tabulaw.client.validate.ErrorDisplay;
import com.tabulaw.client.validate.IErrorHandler;
import com.tabulaw.client.validate.IValidator;
import com.tabulaw.client.validate.IntegerValidator;
import com.tabulaw.client.validate.NotEmptyValidator;
import com.tabulaw.client.validate.ValidationException;
import com.tabulaw.schema.IPropertyMetadataProvider;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.util.ObjectUtil;
import com.tabulaw.util.StringUtil;

/**
 * AbstractField - Base class for non-group {@link IField}s.
 * @param <V> the value type
 * @author jpk
 */
public abstract class AbstractField<V> extends Composite implements IFieldWidget<V> {

	/**
	 * Reflects the number of instantiated {@link AbstractField}s. This is
	 * required to ensure a unique DOM id which is used by the associated
	 * {@link FieldLabel}.
	 * <p>
	 * NOTE: The counter always increments and never decrements and is not
	 * reflective of the actual number of fields referenced in the app.
	 */
	private static int fieldCounter = 0;

	private static final String dfltReadOnlyEmptyValue = "-";

	/**
	 * The unique DOM element id of this field.
	 */
	private final String domId;

	/**
	 * The unique field name.
	 */
	private String name;

	/**
	 * The full property path intended to uniquely identify this field relative to
	 * a common root object.
	 * <p>
	 * <em>NOTE: This is distinct from the field's <code>name</code> property.
	 */
	private String property;

	private boolean required = false;
	private boolean readOnly = false;
	private boolean enabled = true;

	/**
	 * Text that appears when the mouse hovers over the field.
	 */
	private String helpText;

	/**
	 * The read-only field Widget.
	 */
	private HTML rof;

	/**
	 * The optional field label that is <em>not</em> a child of this Widget. This
	 * class only *logically* owns the field label and <em>not</em> physically
	 * (dom-wise).
	 */
	private FieldLabel fldLbl;

	/**
	 * The {@link Composite} widget containing the field's contents.
	 */
	private final FlowPanel pnl = new FlowPanel();

	/**
	 * The desired ancestor Widget reference for the field and the field label
	 * necessary respectively ensuring certain styling rules are properly applied
	 * since the field label is not necessarily a child of this Widget.
	 */
	private Widget container, labelContainer;

	/**
	 * The field validator(s).
	 */
	private CompositeValidator validator;

	/**
	 * The initial value and the current old value.
	 */
	private V initialValue, oldValue;

	/**
	 * Flag to indicate whether or not the initial value is set.
	 */
	private boolean initialValueSet;

	/**
	 * The converter for handling in-bound un-typed values.
	 */
	private IConverter<V, Object> converter;

	private IErrorHandler errorHandler;

	/**
	 * The incremental validation flag.
	 */
	private boolean incrValFlag = true;

	/**
	 * Constructor
	 * @param name The field name which should be unique relative to any sibling
	 *        fields under a common parent (field group).
	 * @param propName The optional property name for binding purposes
	 * @param labelText The optional field label text
	 * @param helpText The options field help text that will appear when the mouse
	 *        hovers.
	 * @throws IllegalArgumentException When no field name is given
	 */
	public AbstractField(String name, String propName, String labelText, String helpText) {
		setName(name);
		setPropertyName(propName);
		domId = 'f' + Integer.toString(++fieldCounter);

		// set the label
		setLabelText(labelText);

		// set the help text
		setHelpText(helpText);

		pnl.setStyleName(Styles.FIELD);

		initWidget(pnl);
	}

	@Override
	public final HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}

	@Override
	public final HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addDomHandler(handler, BlurEvent.getType());
	}

	@Override
	public final HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addDomHandler(handler, FocusEvent.getType());
	}

	@Override
	public final HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/*
	 * We need to honor the IField contract and this method is declated soley to make it publicly visible.
	 */
	@Override
	public final Widget getWidget() {
		return this;
	}

	@Override
	public final String descriptor() {
		return getLabelText();
	}

	/**
	 * Sets the Widget to be the field's containing Widget. This is necessary to
	 * apply certain styling to the appropriate dom node.
	 * @param fieldContainer The desired containing Widget
	 */
	public final void setFieldContainer(Widget fieldContainer) {
		this.container = fieldContainer;
	}

	/**
	 * Sets the Widget to be the field label's containing Widget. This is
	 * necessary to apply certain styling to the appropriate dom node.
	 * @param labelContainer The desired Widget containing the label
	 */
	public final void setFieldLabelContainer(Widget labelContainer) {
		this.labelContainer = labelContainer;
	}

	/**
	 * @return The {@link FieldLabel}. <br>
	 *         <em>NOTE: </em>The field label is <em>NOT</em> a child of this
	 *         composite Widget.
	 */
	public final FieldLabel getFieldLabel() {
		return fldLbl;
	}

	public String getLabelText() {
		return fldLbl == null ? "" : fldLbl.getText();
	}

	/**
	 * Set the field's associated label.
	 * @param labelText The label text. If <code>null</code>, the label will be
	 *        removed.
	 */
	public void setLabelText(String labelText) {
		if(labelText != null) {
			if(fldLbl == null) {
				fldLbl = new FieldLabel();
				fldLbl.setFor(domId);
			}
			fldLbl.setText(labelText);
		}
	}

	/**
	 * @return the domId
	 */
	public final String getDomId() {
		return domId;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		if(StringUtil.isEmpty(name)) {
			throw new IllegalArgumentException("A field must have a name.");
		}
		this.name = name;
	}

	public final String getPropertyName() {
		return property;
	}

	public final void setPropertyName(String propName) {
		this.property = propName;
	}

	public final void clearValue() {
		setValue(null);
		// remove all msgs, edit and validation styling
		resolveError(ErrorDisplay.ALL_FLAGS);
		removeStyleName(Styles.DIRTY);
		this.initialValue = null;
		this.initialValueSet = false;
		this.oldValue = null;
	}

	public final boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		// hide the field label required indicator if we are in read-only state
		if(fldLbl != null) fldLbl.setRequired(readOnly ? false : required);

		this.readOnly = readOnly;
		if(isAttached()) draw();
	}

	public final boolean isRequired() {
		return required;
	}

	public final void setRequired(boolean required) {
		// show/hide the field label required indicator
		if(fldLbl != null) {
			fldLbl.setRequired(readOnly ? false : required);
		}
		this.required = required;
		if(required) {
			addValidator(NotEmptyValidator.INSTANCE);
		}
		else {
			removeValidator(NotEmptyValidator.class);
		}
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if(isAttached()) draw();
	}

	/**
	 * We override to handle setting the visibility for the label as well as the
	 * visibility to the field and label containers.
	 */
	@Override
	public final void setVisible(boolean visible) {
		super.setVisible(visible);

		if(fldLbl != null) {
			fldLbl.setVisible(visible);
		}

		if(container != null) {
			container.setVisible(visible);
		}
		if(labelContainer != null) {
			labelContainer.setVisible(visible);
		}
	}

	/**
	 * @return the helpText
	 */
	public final String getHelpText() {
		return helpText;
	}

	/**
	 * @param helpText the helpText to set
	 */
	public final void setHelpText(String helpText) {
		if(!ObjectUtil.equals(this.helpText, helpText)) {
			this.helpText = helpText;
			if(isAttached()) draw();
		}
	}

	/**
	 * Obtains the editable form control Widget.
	 * @return The form control Widget
	 */
	public abstract IEditable<V> getEditable();

	/**
	 * Either marks or un-marks this field as dirty in the UI based on its current
	 * value and the set initial value.
	 */
	private void dirtyCheck() {
		if(initialValueSet) {
			if(!ObjectUtil.equals(initialValue, getValue())) {
				addStyleName(Styles.DIRTY);
			}
			else {
				removeStyleName(Styles.DIRTY);
			}
		}
	}

	public final void addValidator(IValidator vldtr) {
		if(vldtr != null) {
			if(this.validator == null) {
				this.validator = new CompositeValidator();
			}
			this.validator.remove(vldtr.getClass());
			this.validator.add(vldtr);
		}
	}

	@Override
	public final void setPropertyMetadata(PropertyMetadata metadata) {
		// requiredness
		setRequired(metadata.isRequired() && !metadata.isManaged());

		// maxlength
		if(this instanceof IHasMaxLength) {
			final int maxlen = metadata.getMaxLen();
			((IHasMaxLength) this).setMaxLen(maxlen);
		}

		final GlobalFormat format = (this instanceof IHasFormat) ? ((IHasFormat) this).getFormat() : null;

		// set the type coercion validator
		switch(metadata.getPropertyType()) {
		case STRING:
		case ENUM:
			// no type coercion validator needed
			break;

		case BOOL:
			addValidator(BooleanValidator.INSTANCE);
			break;

		case DATE:
			addValidator(DateValidator.get(format == null ? GlobalFormat.DATE : format));
			break;

		case INT:
			addValidator(IntegerValidator.INSTANCE);
			break;

		case FLOAT:
		case DOUBLE:
			addValidator(new DecimalValidator(Fmt.getDecimalFormat(format == null ? GlobalFormat.DECIMAL : format)));
			break;

		case CHAR:
			addValidator(CharacterValidator.INSTANCE);
			break;

		case LONG:
			// TODO handle - intentional fall through
		case STRING_MAP:
			// TODO handle string map type coercion - intentional fall through
		default:
			throw new IllegalStateException("Unhandled property type: " + metadata.getPropertyType().name());
		}
	}

	public final void applyPropertyMetadata(IPropertyMetadataProvider provider) {
		// Log.debug("AbstractField.applyPropertyMetadata() for " + toString());
		final PropertyMetadata metadata = provider.getPropertyMetadata(getPropertyName());
		if(metadata == null) {
			Log.warn("No property metadata found for field: " + toString());
		}
		else {
			setPropertyMetadata(metadata);
		}
		// apply model new flag
		//validateIncrementally(!isNewModelData);
	}

	public final void removeValidator(Class<? extends IValidator> type) {
		if(validator != null) validator.remove(type);
	}

	/**
	 * Resolves client-side errors.
	 * @param displayFlags
	 */
	private void resolveError(int displayFlags) {
		if(errorHandler != null) {
			errorHandler.resolveError(this, ErrorClassifier.CLIENT, displayFlags);
		}
	}

	/**
	 * Handles client-side errors.
	 * @param errors
	 */
	private void handleError(List<Error> errors) {
		if(errorHandler != null) {
			errorHandler.handleErrors(errors, ErrorDisplay.LOCAL.flag());
		}
	}

	@Override
	public boolean isValid() {
		if(errorHandler != null) errorHandler.resolveError(this, null, ErrorDisplay.ALL_FLAGS);
		try {
			validate();
			return true;
		}
		catch(ValidationException e) {
			if(errorHandler != null) errorHandler.handleErrors(e.getErrors(), ErrorDisplay.ALL_FLAGS);
		}
		return false;
	}

	public final void validate() throws ValidationException {
		validate(getValue());
	}

	public final Object validate(Object value) throws ValidationException {
		if(validator != null) {
			try {
				value = validator.validate(value);
			}
			catch(final ValidationException e) {
				for(final Error error : e.getErrors()) {
					error.setTarget(this);
				}
				throw e;
			}
		}
		return value;
	}
	
	/**
	 * @return The raw field text which may be <code>null</code>.
	 */
	protected abstract String doGetText();

	@Override
	public final String getText() {
		String sval = doGetText();
		sval = StringUtil.isEmpty(sval) ? dfltReadOnlyEmptyValue : sval;
		return sval;
	}

	private void draw() {

		Widget formWidget;

		// assemble?
		// NOTE: we *always* have the editable form control part of the DOM so as to
		// make sure change type events are handled etc.
		// the editable form control widget drives the field's behavior and is the
		// master
		formWidget = (Widget) getEditable();
		if(pnl.getWidgetCount() == 0) {
			formWidget.getElement().setPropertyString("id", domId);
			pnl.add(formWidget);
		}
		else if(formWidget != pnl.getWidget(0)) {
			pnl.insert(formWidget, 0);
		}
		assert formWidget != null;
		formWidget.setTitle(helpText);

		if(readOnly) {
			if(pnl.getWidgetCount() != 2) {
				assert rof == null;
				rof = new HTML();
				pnl.add(rof);
			}
			assert rof != null;
			// set help text
			rof.setTitle(helpText);

			rof.setText(getText());
		}
		if(fldLbl != null) fldLbl.setFor(readOnly ? "" : domId);

		// set readonly/editable display
		if(rof != null) rof.setVisible(readOnly);
		formWidget.setVisible(!readOnly);

		// resolve the containers
		final Widget fldContainer = container == null ? this : container;
		final Widget lblContainer = fldLbl == null ? null : labelContainer == null ? fldLbl : labelContainer;

		// apply readOnly property to "containing" widget
		if(readOnly) {
			fldContainer.addStyleName(Styles.READ_ONLY);
			if(lblContainer != null) lblContainer.addStyleName(Styles.READ_ONLY);
		}
		else {
			fldContainer.removeStyleName(Styles.READ_ONLY);
			if(lblContainer != null) lblContainer.removeStyleName(Styles.READ_ONLY);
		}

		// apply enabled property to "containing" widget
		if(enabled) {
			fldContainer.removeStyleName(Styles.DISABLED);
			if(lblContainer != null) lblContainer.removeStyleName(Styles.DISABLED);
		}
		else {
			fldContainer.addStyleName(Styles.DISABLED);
			if(lblContainer != null) lblContainer.addStyleName(Styles.DISABLED);
		}

		if(!enabled || readOnly) {
			// remove all msgs, edit and validation styling
			resolveError(ErrorDisplay.LOCAL.flag());
			removeStyleName(Styles.DIRTY);
		}
		else if(enabled && !readOnly) {
			// show/hide edit styling
			dirtyCheck();
		}
	}

	@Override
	public final int getTabIndex() {
		return getEditable().getTabIndex();
	}

	@Override
	public final void setTabIndex(int index) {
		if(!readOnly) getEditable().setTabIndex(index);
	}

	@Override
	public final void setAccessKey(char key) {
		if(!readOnly) getEditable().setAccessKey(key);
	}

	@Override
	public final void setFocus(boolean focused) {
		if(!readOnly) getEditable().setFocus(focused);
	}

	@Override
	public void onFocus(FocusEvent event) {
		addStyleName(Styles.ACTIVE);
	}

	@Override
	public void onBlur(BlurEvent event) {
		removeStyleName(Styles.ACTIVE);
		resolveError(ErrorDisplay.LOCAL.flag());	// clear out old first
		if(incrValFlag) {
			try {
				validate();
				dirtyCheck();
			}
			catch(final ValidationException e) {
				handleError(e.getErrors());
			}
		}
	}

	@Override
	public final String getFieldValue() {
		Object value = getValue();
		if(value == null) return "";
		return value.toString();
	}

	@Override
	public final void onValueChange(ValueChangeEvent<V> event) {
		assert event.getSource() == getEditable();
		final V old = oldValue;
		oldValue = event.getValue();
		if(!ObjectUtil.equals(old, oldValue)) {
			ValueChangeEvent.fire(this, oldValue);
			// we don't want auto-transfer!!!
			//adapter.getChangeSupport().firePropertyChange(PROPERTY_VALUE, old, oldValue);
		}
	}

	@Override
	public final IConverter<V, Object> getConverter() {
		return converter;
	}

	@Override
	public final void setConverter(IConverter<V, Object> converter) {
		this.converter = converter;
	}

	@Override
	public final V getValue() {
		return getEditable().getValue();
	}

	@Override
	public final void setValue(V value) {
		setValue(value, false);
	}

	@Override
	public final void setValue(V value, boolean fireEvents) {
		getEditable().setValue(value, fireEvents);
		if(!initialValueSet) {
			initialValue = getValue();
			initialValueSet = true;
		}
	}

	public final void reset() {
		if(initialValueSet) {
			setValue(initialValue);
			resolveError(ErrorDisplay.ALL_FLAGS);
			removeStyleName(Styles.DIRTY);
		}
	}

	@Override
	public final IErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public final void setErrorHandler(IErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public final void validateIncrementally(boolean validate) {
		this.incrValFlag = validate;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		draw();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
	}

	/**
	 * Fields are considered if their property names match.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		return (!name.equals(((AbstractField) obj).name));
	}

	@Override
	public final int hashCode() {
		return name.hashCode();
	}

	@Override
	public final String toString() {
		return property == null ? name : name + " [ " + property + " ]";
	}
}
