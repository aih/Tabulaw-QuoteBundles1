/**
 * The Logic Lab
 */
package com.tabulaw.client.ui.login;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.tabulaw.client.data.rpc.IHasRpcHandlers;
import com.tabulaw.client.data.rpc.IRpcHandler;
import com.tabulaw.client.data.rpc.RpcEvent;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.client.ui.SimpleHyperLink;
import com.tabulaw.client.ui.field.AbstractFieldGroupProvider;
import com.tabulaw.client.ui.field.AbstractFieldPanel;
import com.tabulaw.client.ui.field.FieldFactory;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.GridFieldComposer;
import com.tabulaw.client.ui.field.IFieldRenderer;
import com.tabulaw.client.ui.field.PasswordField;
import com.tabulaw.client.ui.field.TextField;
import com.tabulaw.client.validate.ErrorDisplay;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.client.validate.ValidationException;
import com.tabulaw.schema.PropertyMetadata;
import com.tabulaw.schema.PropertyType;

/**
 * LoginPanel
 * @author jpk
 */
public class LoginPanel extends Composite implements IHasUserSessionHandlers, IHasRpcHandlers, HasValueChangeHandlers<LoginPanel.Mode> {

	static class Styles {

		/**
		 * The top-most panel.
		 */
		public static final String LOGIN = "login";
		/**
		 * Table containing username/password fields.
		 */
		public static final String LOGIN_FORM = "loginForm";
		/**
		 * Table containing username/password fields.
		 */
		public static final String LOGIN_GRID = "loginGrid";
		/**
		 * User registration panel.
		 */
		public static final String USER_REG = "userReg";
		/**
		 * Nested panel containing the nav links
		 */
		public static final String LINK_PANEL = "lnkPanel";
		/**
		 * Login status message label
		 */
		public static final String LOGIN_STATUS = "loginStatus";
	}

	public static enum Mode {
		LOGIN,
		FORGOT_PASSWORD,
	}

	static class LoginFieldPanel extends AbstractFieldPanel<FlowPanel> {

		static class FieldProvider extends AbstractFieldGroupProvider {

			@Override
			protected String getFieldGroupName() {
				return "User Login";
			}

			@Override
			protected void populateFieldGroup(final FieldGroup fg) {

				TextField email = FieldFactory.femail("userEmail", "emailAddress", "Email Address", "Your email address", 25);
				PasswordField password = FieldFactory.fpassword("userPswd", "password", "Password", "Specify a password", 12);

				fg.addField(email);
				fg.addField(password);

				final PropertyMetadata userEmailMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 50);
				final PropertyMetadata userPasswordMetadata = new PropertyMetadata(PropertyType.STRING, false, true, 30);

				email.setPropertyMetadata(userEmailMetadata);
				password.setPropertyMetadata(userPasswordMetadata);
				
				fg.validateIncrementally(false);
				fg.setErrorHandler(ErrorHandlerBuilder.build(false, true, null));
			}
		}

		private final FlowPanel panel = new FlowPanel();

		public LoginFieldPanel() {
			super();
			initWidget(panel);
		}

		@Override
		protected FieldGroup generateFieldGroup() {
			return new FieldProvider().getFieldGroup();
		}

		@Override
		protected IFieldRenderer<FlowPanel> getRenderer() {
			return new IFieldRenderer<FlowPanel>() {

				public void render(FlowPanel fp, FieldGroup fg) {
					final GridFieldComposer cmpsr = new GridFieldComposer();
					cmpsr.setCanvas(fp);
					cmpsr.addField(fg.getFieldWidget("userEmail"));
					cmpsr.addField(fg.getFieldWidget("userPswd"));
				}
			};
		}
	}

	final FlowPanel topPanel;
	final FlowPanel loginAndForgotPasswordPanel;
	final UserRegisterPanel userRegPanel;
	final FlowPanel opRowPanel;

	final Label lblStatusMsg;
	final FormPanel form;

	final LoginFieldPanel loginFieldPanel;
	final Button btnSubmit;
	final SimpleHyperLink lnkTgl, lnkRegister;

	/**
	 * Constructor
	 * <p>
	 * Default, Spring-Security based, form field names and form action.
	 */
	public LoginPanel() {
		this("j_username", "j_password", "/login");
	}

	/**
	 * Constructor
	 * @param fldUsername name of the username field
	 * @param fldPassword name of the password field
	 * @param formAction path to which the form is submitted (e.g.: "/login") <br>
	 *        <b>NOTE:</b> "j_spring_security_check" will then be appended
	 */
	public LoginPanel(String fldUsername, String fldPassword, String formAction) {
		super();

		lblStatusMsg = new Label("");
		lblStatusMsg.setStyleName(Styles.LOGIN_STATUS);

		loginFieldPanel = new LoginFieldPanel();

		form = new FormPanel();
		form.setStyleName(Styles.LOGIN_FORM);
		form.setAction(formAction/* + "j_spring_security_check"*/);
		form.setMethod(FormPanel.METHOD_POST);

		loginAndForgotPasswordPanel = new FlowPanel();

		loginAndForgotPasswordPanel.add(lblStatusMsg);

		btnSubmit = new Button("Login", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				if(isLoginMode()) {
					
					try {
						loginFieldPanel.getFieldGroup().validate();
						setVisible(false);
						form.submit();
					}
					catch(ValidationException e) {
						loginFieldPanel.getFieldGroup().getErrorHandler().handleErrors(e.getErrors(), ErrorDisplay.LOCAL.flag());
					}
				}
				else {
					final String emailAddress = loginFieldPanel.getFieldGroup().getFieldWidget("userEmail").getFieldValue();
					if(emailAddress.length() == 0) {
						lblStatusMsg.setText("Your email address must be specified for password retrieval.");
						return;
					}

					// TODO implement forgot password service
					// final ForgotPasswordCommand fpc = new
					// ForgotPasswordCommand(emailAddress);
					// fpc.setSource(LoginPanel.this);
					// fpc.execute();
					Window.alert("Forgot password service not yet implemented.");
				}
			}
		});

		lnkTgl = new SimpleHyperLink("Forgot Password", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				loginFieldPanel.getFieldGroup().getErrorHandler().clear();
				if(!isLoginMode()) {
					// to login mode
					lblStatusMsg.setText(null);
					
					loginFieldPanel.getFieldGroup().getFieldWidget("userPswd").setVisible(true);
					
					lnkTgl.setTitle("Forgot Password");
					lnkTgl.setText("Forgot Password");
					btnSubmit.setText("Login");
					// setText("Login");
					ValueChangeEvent.fire(LoginPanel.this, Mode.LOGIN);
				}
				else {
					// to forgot password mode
					lblStatusMsg.setText("Please specify your email address and your password will be emailed to you.");
					
					loginFieldPanel.getFieldGroup().getFieldWidget("userPswd").setVisible(false);

					lnkTgl.setTitle("Back to Login");
					lnkTgl.setText("Back to Login");
					btnSubmit.setText("Email Password");
					// setText("Forgot Password");
					ValueChangeEvent.fire(LoginPanel.this, Mode.FORGOT_PASSWORD);
				}
			}
		});
		lnkTgl.setTitle("Forgot Password");

		lnkRegister = new SimpleHyperLink("Register", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				form.setVisible(false);
				userRegPanel.setVisible(true);
			}

		});
		lnkRegister.setTitle("Register..");

		opRowPanel = new FlowPanel();
		opRowPanel.setStyleName(Styles.LINK_PANEL);
		opRowPanel.add(btnSubmit);
		opRowPanel.add(lnkTgl);
		opRowPanel.add(lnkRegister);

		loginAndForgotPasswordPanel.add(loginFieldPanel);
		loginAndForgotPasswordPanel.add(opRowPanel);

		/*
		form.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				final StringBuilder msg = new StringBuilder(128);
				
				if(tbEmail.getText().length() == 0) {
					msg.append("Please specify your email address.");
					event.cancel();
				}
				if(tbPswd.getText().length() == 0) {
					msg.append("Please specify your password.");
					event.cancel();
				}
				if(event.isCanceled()) {
					setVisible(false);
				}
				
				lblStatusMsg.setText(msg.toString());
			}
		});
		*/

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				final String results = event.getResults();
				if(results == null || results.length() == 0) {
					// successful login

					// userSessionListeners.fireLogin();
					fireEvent(new UserSessionEvent(true));

					//tbEmail.setText(null);
					//tbPswd.setText(null);
					loginFieldPanel.getFieldGroup().clearValue();
					
					return;
				}
				// unsuccessful login
				setVisible(true);
				lblStatusMsg.setText(event.getResults());
			}
		});

		form.setWidget(loginAndForgotPasswordPanel);

		userRegPanel = new UserRegisterPanel();
		userRegPanel.setStyleName(Styles.USER_REG);
		userRegPanel.setVisible(false); // hide initially

		topPanel = new FlowPanel();
		topPanel.addStyleName(Styles.LOGIN);
		topPanel.add(form);
		topPanel.add(userRegPanel);

		initWidget(topPanel);

		addRpcHandler(new RpcUiHandler(this));
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mode> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addUserSessionHandler(IUserSessionHandler handler) {
		return addHandler(handler, UserSessionEvent.TYPE);
	}

	private boolean isLoginMode() {
		return "Forgot Password".equals(lnkTgl.getTitle());
	}

	@Override
	public HandlerRegistration addRpcHandler(IRpcHandler handler) {
		return addHandler(handler, RpcEvent.TYPE);
	}
}
