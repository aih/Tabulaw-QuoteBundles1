/**
 * The Logic Lab
 */
package com.tabulaw.client.ui.login;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.tabulaw.client.Poc;
import com.tabulaw.client.data.rpc.IHasRpcHandlers;
import com.tabulaw.client.data.rpc.IRpcHandler;
import com.tabulaw.client.data.rpc.RpcEvent;
import com.tabulaw.client.ui.RpcUiHandler;
import com.tabulaw.client.ui.SimpleHyperLink;
import com.tabulaw.client.ui.field.FieldGroup;
import com.tabulaw.client.ui.field.IFieldWidget;
import com.tabulaw.client.ui.msg.GlobalMsgPanel;
import com.tabulaw.client.validate.ErrorHandlerBuilder;
import com.tabulaw.client.validate.ErrorHandlerDelegate;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.rpc.UserRegistrationRequest;
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
	Mode mode = Mode.LOGIN;

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
	 */
	public LoginTopPanel() {
		super();

		// grab the sole global msg panel for use by this panal for the duration of
		// user login
		msgPanel = Poc.unparkGlobalMsgPanel();

		errorHandler = ErrorHandlerBuilder.build(true, true, msgPanel);

		loginFieldPanel = new FieldPanel(Mode.LOGIN);
		loginFieldPanel.setErrorHandler(errorHandler);

		form = new FormPanel();
		form.setStyleName(Styles.LOGIN_FORM);
		form.setAction("/login");
		form.setMethod(FormPanel.METHOD_POST);

		btnSubmit = new Button("Login", new ClickHandler() {

			@SuppressWarnings( {
				"synthetic-access", "unchecked"
			})
			@Override
			public void onClick(ClickEvent event) {
				switch(mode) {
					case FORGOT_PASSWORD: {
						IFieldWidget<String> femail =
								(IFieldWidget<String>) loginFieldPanel.getFieldGroup().getFieldWidget("userEmail");
						if(!femail.isValid()) {
							msgPanel.add(new Msg("Your email address must be specified for password retrieval.", MsgLevel.ERROR),
									null);
							return;
						}
						String emailAddress = femail.getValue();
						Poc.getUserRegisterService().requestPassword(emailAddress, new AsyncCallback<Payload>() {

							@Override
							public void onSuccess(Payload result) {
								switchMode(Mode.LOGIN);
								msgPanel.add(result.getStatus().getMsgs(), null);
							}

							@Override
							public void onFailure(Throwable caught) {
								switchMode(Mode.LOGIN);
								msgPanel.add(new Msg("An error occurred while sending the reminder email.", MsgLevel.ERROR), null);
							}
						});
						break;
					}
					case LOGIN:
						if(loginFieldPanel.getFieldGroup().isValid()) {
							form.submit();
						}
						break;
					case REGISTER: {
						FieldGroup fg = registerFieldPanel.getFieldGroup();
						if(fg.isValid()) {
							String emailAddress = (String) fg.getFieldWidget("userEmail").getValue();
							String password = (String) fg.getFieldWidget("userPswd").getValue();
							UserRegistrationRequest request = new UserRegistrationRequest(emailAddress, password);
							Poc.getUserRegisterService().registerUser(request, new AsyncCallback<Payload>() {

								@Override
								public void onSuccess(Payload result) {
									Status status = result.getStatus();
									List<Msg> msgs = status.getMsgs();
									if(!status.hasErrors()) {
										if(msgs == null) msgs = new ArrayList<Msg>(1);
										msgs.add(new Msg("Now login!", MsgLevel.INFO));
									}
									switchMode(Mode.LOGIN);
									if(msgs != null) msgPanel.add(msgs, null);
								}

								@Override
								public void onFailure(Throwable caught) {
									msgPanel.add(new Msg("An error occurred while registering.", MsgLevel.ERROR), null);
								}
							});
						}
						break;
					}
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
				switch(mode) {
					case LOGIN:
						switchMode(Mode.FORGOT_PASSWORD);
						break;
					case FORGOT_PASSWORD:
					case REGISTER:
						switchMode(Mode.LOGIN);
						break;
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
				Msg emsg = new Msg(event.getResults(), MsgLevel.ERROR);
				msgPanel.add(emsg, null);
			}
		});

		form.setWidget(loginFieldPanel);

		topPanel = new FlowPanel();
		topPanel.addStyleName(Styles.LOGIN);
		topPanel.add(msgPanel);
		topPanel.add(form);
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
				btnSubmit.setText("Register");
				lnkTgl.setText("Cancel");
				loginFieldPanel.getFieldGroup().clearValue();
				break;
			}
		}

		boolean isRegister = (newMode == Mode.REGISTER);
		form.setVisible(!isRegister);
		lnkRegister.setVisible(newMode == Mode.LOGIN);
		if(registerFieldPanel != null) {
			registerFieldPanel.setVisible(isRegister);
			if(!isRegister) registerFieldPanel.getFieldGroup().clearValue();
		}

		mode = newMode;
		ValueChangeEvent.fire(LoginTopPanel.this, mode);
	}
}
