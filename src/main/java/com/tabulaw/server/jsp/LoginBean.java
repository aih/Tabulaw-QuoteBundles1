package com.tabulaw.server.jsp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

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
		if (request != null) {
			setEmailAddress(StringUtils.defaultString(
					request.getParameter("userEmail")).trim());
			setPassword(StringUtils.defaultString(request
					.getParameter("userPswd")));
		}
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
			try {
				PersistContext persistContext = (PersistContext) session
						.getServletContext().getAttribute(PersistContext.KEY);
				UserService userService = persistContext.getUserService();

				User user = userService.findByEmail(getEmailAddress());

				if (UserService.isPasswordValid(getPassword(),user.getPassword(), user.getEmailAddress())) {
					if (!user.isEnabled()) {
						errors.add("Your account is disabled.");
						return false;
					}
					if (user.isExpired()) {
						errors.add("Your account has expired.");
						return false;
					} 
					if (user.isLocked()) {
						errors.add("Your account is locked.");
						return false;
					} 
					final UserContext context = new UserContext();
					context.setUser(user);
					session.setAttribute(UserContext.KEY, context);
				} else {
					errors.add("Invalid user or password.");
				}
				
			} catch (IllegalArgumentException e) {
				errors.add("Invalid or empty password.");
			} catch (ConstraintViolationException e) {
				errors.add("Invalid email format");
			} catch (EntityNotFoundException e) {
				errors.add("Invalid user or password.");
			}
		}

		return errors.isEmpty();
	}
}
