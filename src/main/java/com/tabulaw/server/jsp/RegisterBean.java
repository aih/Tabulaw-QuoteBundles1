package com.tabulaw.server.jsp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.common.data.rpc.UserRegistrationRequest;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.service.entity.UserService;

public class RegisterBean {

	private static final Log log = LogFactory.getLog(RegisterBean.class);

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
		setErrors(new ArrayList<String>());

		if (getName().isEmpty()) {
			errors.add("Empty user name.");
		}
		if (getPassword().isEmpty()) {
			errors.add("Empty password.");
		}
		if (getPassword().isEmpty()) {
			errors.add("Empty password.");
		}
		if (!getPassword().equals(getPasswordConfirm())) {
			errors.add("Invalid confirm password.");
		}

		if (getEmailAddress().isEmpty()) {
			errors.add("Empty email address.");
		} else {
			// Check if email address already exists
			HttpSession session = request.getSession(false);
			UserService userService = null;
			User user = null;

			if (session == null) {
				log.fatal("No http session exists.");
				errors.add("No http session exists.");
			} else {
				try {
					PersistContext persistContext = (PersistContext) session
							.getServletContext().getAttribute(
									PersistContext.KEY);
					userService = persistContext.getUserService();
					user = userService.findByEmail(getEmailAddress());
					errors.add("Email already exists.");
				} catch (EntityNotFoundException e) {
				}
			}
		}

		if (!errors.isEmpty()) {
			return false;
		}

		UserRegistrationRequest request = new UserRegistrationRequest();
		request.setName(getName());
		request.setEmailAddress(getEmailAddress());
		request.setPassword(getPassword());

		return false;
	}
}
