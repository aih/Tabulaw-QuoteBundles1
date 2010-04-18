/**
 * The Logic Lab
 */
package com.tabulaw.client.ui.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.tll.client.data.rpc.IHasRpcHandlers;
import com.tll.client.data.rpc.IRpcHandler;
import com.tll.client.data.rpc.RpcEvent;
import com.tll.client.ui.RpcUiHandler;
import com.tll.client.ui.SimpleHyperLink;

/**
 * LoginPanel
 * @author jpk
 */
public class LoginPanel extends Composite implements IHasUserSessionHandlers, IHasRpcHandlers, HasValueChangeHandlers<LoginPanel.Mode> {

	public static enum Mode {
		LOGIN,
		FORGOT_PASSWORD,
	}

	final Label lblStatusMsg;
	final FormPanel form;
	final TextBox tbEmail;
	final Label lblEmail, lblPswd;
	final PasswordTextBox tbPswd;
	final Button btnSubmit;
	final SimpleHyperLink lnkTgl;

	/**
	 * Constructor
	 * <p>
	 * Default, Spring-Security based, form field names and form action.
	 */
	public LoginPanel() {
		this("j_username", "j_password", GWT.getModuleBaseURL());
	}

	/**
	 * Constructor
	 * <p>
	 * Uses default username/password field names.
	 * @param formRoot where to send login form submission.
	 */
	public LoginPanel(String formRoot) {
		this("j_username", "j_password", formRoot);
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

		// setText("Login");

		lblStatusMsg = new Label("");
		lblStatusMsg.setStyleName("loginStatus");

		lblEmail = new Label("Email Address");
		lblEmail.setStyleName("loginLbl");
		tbEmail = new TextBox();
		tbEmail.addStyleName("loginUsername");
		tbEmail.setName(fldUsername);
		tbEmail.setMaxLength(50);

		lblPswd = new Label("Password");
		lblPswd.setStyleName("loginLbl");
		tbPswd = new PasswordTextBox();
		tbPswd.addStyleName("loginPswd");
		tbPswd.setName(fldPassword);
		tbPswd.setMaxLength(50);

		form = new FormPanel();
		form.setStyleName("loginForm");
		form.setAction(formAction/* + "j_spring_security_check"*/);
		form.setMethod(FormPanel.METHOD_POST);

		final VerticalPanel vert = new VerticalPanel();
		vert.setSpacing(2);

		vert.add(lblStatusMsg);

		btnSubmit = new Button("Login", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				if(isLoginMode()) {
					// DeferredCommand.addCommand(new FocusCommand(btnSubmit, false));
					setVisible(false);
					form.submit();
				}
				else {
					final String emailAddress = tbEmail.getText();
					if(emailAddress.length() == 0) {
						lblStatusMsg.setText("Your email address must be specified for password retrieval.");
						return;
					}

					// TODO implement forgot password service
					//final ForgotPasswordCommand fpc = new ForgotPasswordCommand(emailAddress);
					//fpc.setSource(LoginPanel.this);
					//fpc.execute();
					Window.alert("Forgot password service not yet implemented.");
				}
			}
		});

		lnkTgl = new SimpleHyperLink("Forgot Password", new ClickHandler() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(ClickEvent event) {
				if(!isLoginMode()) {
					// to login mode
					lblStatusMsg.setText(null);
					lblPswd.setVisible(true);
					tbPswd.setVisible(true);
					lnkTgl.setTitle("Forgot Password");
					lnkTgl.setText("Forgot Password");
					btnSubmit.setText("Login");
					// setText("Login");
					ValueChangeEvent.fire(LoginPanel.this, Mode.LOGIN);
				}
				else {
					// to forgot password mode
					lblStatusMsg.setText("Please specify your email address and your password will be emailed to you.");
					lblPswd.setVisible(false);
					tbPswd.setVisible(false);
					lnkTgl.setTitle("Back to Login");
					lnkTgl.setText("Back to Login");
					btnSubmit.setText("Email Password");
					// setText("Forgot Password");
					ValueChangeEvent.fire(LoginPanel.this, Mode.FORGOT_PASSWORD);
				}
				// center();
			}
		});
		lnkTgl.setTitle("Forgot Password");

		final Grid grid = new Grid(3, 2);
		grid.setStyleName("loginGrid");
		grid.setWidth("100%");
		grid.setCellSpacing(2);
		grid.setWidget(0, 0, lblEmail);
		grid.setWidget(0, 1, tbEmail);
		grid.setWidget(1, 0, lblPswd);
		grid.setWidget(1, 1, tbPswd);
		grid.setWidget(2, 0, btnSubmit);
		grid.setWidget(2, 1, lnkTgl);
		vert.add(grid);

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
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				final String results = event.getResults();
				if(results == null || results.length() == 0) {
					// successful login

					// userSessionListeners.fireLogin();
					fireEvent(new UserSessionEvent(true));

					tbEmail.setText(null);
					tbPswd.setText(null);
					return;
				}
				// unsuccessful login
				setVisible(true);
				lblStatusMsg.setText(event.getResults());
			}
		});

		form.setWidget(vert);

		initWidget(form);

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

	/*
	@Override
	public void center() {
		super.center();
		DeferredCommand.addCommand(new FocusCommand(tbEmail, true));
	}
	*/

	private boolean isLoginMode() {
		return "Forgot Password".equals(lnkTgl.getTitle());
	}

	@Override
	public HandlerRegistration addRpcHandler(IRpcHandler handler) {
		return addHandler(handler, RpcEvent.TYPE);
	}
}
