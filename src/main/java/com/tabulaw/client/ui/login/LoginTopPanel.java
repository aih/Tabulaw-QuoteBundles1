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
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.tabulaw.client.data.rpc.IHasRpcHandlers;
import com.tabulaw.client.data.rpc.IRpcHandler;
import com.tabulaw.client.data.rpc.RpcEvent;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.client.ui.SimpleHyperLink;
import com.tabulaw.client.ui.msg.GlobalMsgPanel;
import com.tabulaw.client.validate.ErrorDisplay;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.client.validate.ErrorHandlerDelegate;
import com.tabulaw.client.validate.ValidationException;
import com.tabulaw.common.msg.Msg;
import com.tabulaw.common.msg.Msg.MsgLevel;

/**
 * Houses user login panel and registration panels.
 * @author jpk
 */
public class LoginTopPanel extends Composite implements IHasUserSessionHandlers, IHasRpcHandlers, HasValueChangeHandlers<LoginTopPanel.Mode> {

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
		 * User registration panel.
		 */
		public static final String USER_REG = "userReg";
		/**
		 * Nested panel containing the nav links
		 */
		public static final String OP_PANEL = "opPanel";
	}

	static enum Mode {
		LOGIN,
		FORGOT_PASSWORD,
		REGISTER;
	}

	/**
	 * The current mode.
	 */
	Mode mode;

	final FlowPanel topPanel, opRowPanel;

	final GlobalMsgPanel msgPanel;
	final FormPanel form;

	final FieldPanel loginFieldPanel;
	FieldPanel registerFieldPanel;

	final Button btnSubmit, btnCancel;
	final SimpleHyperLink lnkTgl, lnkRegister;

	final ErrorHandlerDelegate errorHandler;

	/**
	 * Constructor
	 * <p>
	 * Default, Spring-Security based, form field names and form action.
	 */
	public LoginTopPanel() {
		this("j_username", "j_password", "/login");
	}

	/**
	 * Constructor
	 * @param fldUsername name of the username field
	 * @param fldPassword name of the password field
	 * @param formAction path to which the form is submitted (e.g.: "/login") <br>
	 *        <b>NOTE:</b> "j_spring_security_check" will then be appended
	 */
	public LoginTopPanel(String fldUsername, String fldPassword, String formAction) {
		super();

		msgPanel = new GlobalMsgPanel();

		errorHandler = ErrorHandlerBuilder.build(true, true, msgPanel);

		loginFieldPanel = new FieldPanel(Mode.LOGIN);
		loginFieldPanel.setErrorHandler(errorHandler);

		form = new FormPanel();
		form.setStyleName(Styles.LOGIN_FORM);
		form.setAction(formAction/* + "j_spring_security_check"*/);
		form.setMethod(FormPanel.METHOD_POST);

		btnSubmit = new Button("Login", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				if(mode == Mode.LOGIN) {

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
						msgPanel.add(new Msg("Your email address must be specified for password retrieval.", MsgLevel.ERROR), null);
						return;
					}

					// TODO implement forgot password service
					// final ForgotPasswordCommand fpc = new
					// ForgotPasswordCommand(emailAddress);
					// fpc.setSource(LoginTopPanel.this);
					// fpc.execute();
					Window.alert("Forgot password service not yet implemented.");
				}
			}
		});

		// cancel user registration
		btnCancel = new Button("Cancel", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				switchMode(Mode.LOGIN);
			}
		});

		lnkTgl = new SimpleHyperLink("Forgot Password", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				loginFieldPanel.getFieldGroup().getErrorHandler().clear();
				if(mode != Mode.LOGIN) {
					// to login mode
					switchMode(Mode.LOGIN);
				}
				else {
					// to forgot password mode
					switchMode(Mode.FORGOT_PASSWORD);
				}
			}
		});
		lnkTgl.setTitle("Forgot Password");

		lnkRegister = new SimpleHyperLink("Register", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				switchMode(Mode.REGISTER);
			}

		});
		lnkRegister.setTitle("Register..");

		opRowPanel = new FlowPanel();
		opRowPanel.setStyleName(Styles.OP_PANEL);
		opRowPanel.add(btnSubmit);
		opRowPanel.add(lnkTgl);
		opRowPanel.add(lnkRegister);

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				final String results = event.getResults();
				if(results == null || results.length() == 0) {
					// successful login
					fireEvent(new UserSessionEvent(true));
					loginFieldPanel.getFieldGroup().clearValue();
					return;
				}
				// unsuccessful login
				setVisible(true);
				Msg emsg = new Msg(event.getResults(), MsgLevel.ERROR);
				msgPanel.add(emsg, null);
			}
		});

		form.setWidget(loginFieldPanel);

		topPanel = new FlowPanel();
		topPanel.addStyleName(Styles.LOGIN);
		topPanel.add(msgPanel);
		topPanel.add(form);
		// topPanel.add(registerFieldPanel);
		topPanel.add(opRowPanel);

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

	@Override
	public HandlerRegistration addRpcHandler(IRpcHandler handler) {
		return addHandler(handler, RpcEvent.TYPE);
	}

	private void switchMode(Mode newMode) {
		if(mode == newMode) return;
		switch(newMode) {
			case FORGOT_PASSWORD: {
				Msg imsg =
						new Msg("Please specify your email address and your password will be emailed to you.", MsgLevel.INFO);
				msgPanel.add(imsg, null);
				loginFieldPanel.getFieldGroup().getFieldWidget("userPswd").setVisible(false);
				lnkTgl.setTitle("Back to Login");
				lnkTgl.setText("Back to Login");
				btnSubmit.setText("Email Password");
				break;
			}
			case LOGIN: {
				msgPanel.clear();
				loginFieldPanel.getFieldGroup().getFieldWidget("userPswd").setVisible(true);
				lnkTgl.setTitle("Forgot Password");
				lnkTgl.setText("Forgot Password");
				btnSubmit.setText("Login");
				break;
			}
			case REGISTER: {
				if(registerFieldPanel == null) {
					registerFieldPanel = new FieldPanel(Mode.REGISTER);
					registerFieldPanel.setErrorHandler(errorHandler);
					registerFieldPanel.setStyleName(Styles.USER_REG);
					// insert it just above the op row
					topPanel.insert(registerFieldPanel, topPanel.getWidgetCount() - 2);
				}
				break;
			}
		}

		boolean isRegister = (mode == Mode.REGISTER);
		form.setVisible(!isRegister);
		if(registerFieldPanel != null) registerFieldPanel.setVisible(isRegister);

		mode = newMode;
		ValueChangeEvent.fire(LoginTopPanel.this, mode);
	}
}
