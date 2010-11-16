package com.tabulaw.server.jsp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserService;

public class LoginBean {

	private static final Log log = LogFactory.getLog(LoginBean.class);

	private HttpServletRequest request;
	private String emailAddress;
	private String password;
	private List<String> errors = new ArrayList<String>();

	public void setRequest(HttpServletRequest request) {
		this.request = request;
		setEmailAddress(StringUtils.defaultString(
				request.getParameter("emailAddress")).trim());
		setPassword(StringUtils.defaultString(request.getParameter("password")));
	}

	public HttpServletRequest getRequest() {
		return request;
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

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean isLoginValid() {
		setErrors(new ArrayList<String>());

		if (request == null) {
			return false;
		} else if (request.getParameter("submitLogin") == null) {
			return false;
		}

		HttpSession session = request.getSession(false);

		if (session == null) {
			log.fatal("No http session exists.");
			errors.add("No http session exists.");
		} else {
			String username = getEmailAddress();
			String password = getPassword();
			try {
				PersistContext persistContext = (PersistContext) session
						.getServletContext().getAttribute(PersistContext.KEY);
				UserService userService = persistContext.getUserService();

				User user = userService.findByEmail(username);

				if (!user.isEnabled()) {
					errors.add("Your account is disabled.");
				} else if (user.isExpired()) {
					errors.add("Your account has expired.");
				} else if (user.isLocked()) {
					errors.add("Your account is locked.");
				} else {
					try {
						if (UserService.isPasswordValid(password,
								user.getPassword(), user.getEmailAddress())) {
							final UserContext context = new UserContext();
							context.setUser(user);
							session.setAttribute(UserContext.KEY, context);
						} else {
							errors.add("Invalid password.");
						}
					} catch (IllegalArgumentException e) {
						errors.add("Invalid or empty password.");
					}
				}
			} catch (EntityNotFoundException e) {
				errors.add("The account has not been found.");
			}
		}

		return errors.isEmpty();
	}
}
