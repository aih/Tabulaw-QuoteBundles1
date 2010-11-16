package com.tabulaw.server.jsp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.WebAppContext;
import com.tabulaw.server.rpc.UserServiceRpc;
import com.tabulaw.service.entity.UserService;

public class PasswordForgottenBean {

	private static final Log log = LogFactory.getLog(PasswordForgottenBean.class);

	private HttpServletRequest request;

	private String name;
	private String emailAddress;
	private String password;
	private String passwordConfirm;

	private List<String> errors = new ArrayList<String>();

	public void setRequest(HttpServletRequest request) {
		this.request = request;
		setEmailAddress(StringUtils.defaultString(
				request.getParameter("userEmail")).trim());
		setName(StringUtils.defaultString(request.getParameter("userName")));
		setPassword(StringUtils.defaultString(request.getParameter("userPswd")));
		setPasswordConfirm(StringUtils.defaultString(request
				.getParameter("userPswdConfirm")));
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean isRegistrationValid() {

		UserService userService = null;

		setErrors(new ArrayList<String>());

		if (getName().isEmpty()) {
			errors.add("Empty user name.");
		}
		if (getPassword().isEmpty()) {
			errors.add("Empty password.");
		}
		if (!getPassword().equals(getPasswordConfirm())) {
			errors.add("Invalid password confirm.");
		}
		if (getEmailAddress().isEmpty()) {
			errors.add("Empty email address.");
		} else {
			HttpSession session = request.getSession(false);
			if (session == null) {
				log.fatal("No http session exists.");
				errors.add("No http session exists.");
			} else {
				userService = getPersistContext().getUserService();
				try {
					// Check if email address already exists
					userService.findByEmail(getEmailAddress());
					errors.add("Email already exists.");
				} catch (EntityNotFoundException e) {
				}
				try {
					// Check if user name already exists
					userService.getUserRef(getName());
					errors.add("Username already exists.");
				} catch (EntityNotFoundException e) {
				}
			}
		}

		if (!errors.isEmpty()) {
			return false;
		} else {
			if (userService == null) {
				errors.add("Internal error.");
				log.error("userService is null");
			} else {
				doUserRegister(userService);
			}
		}

		return errors.isEmpty();
	}

	private void doUserRegister(UserService userService) {
		try {
			User user = userService.create(getName(), getEmailAddress(),
					getPassword());
			sendEmail(user);
		} catch (EntityExistsException e) {
			errors.add("Email already exists");
		} catch (Exception e) {
			errors.add("Internal error");
			log.error("", e);
		}
	}

	private void sendEmail(User user) {
		try {
			UserServiceRpc.sendEmailConfirmation(user, getWebAppContext());
		} catch (Exception e) {
			log.error("Unable to send email confirmation at this time.", e);
		}
	}

	private PersistContext getPersistContext() {
		return (PersistContext) getRequest().getSession().getServletContext()
				.getAttribute(PersistContext.KEY);
	}

	private WebAppContext getWebAppContext() {
		return (WebAppContext) getRequest().getSession().getServletContext()
				.getAttribute(WebAppContext.KEY);
	}
}
