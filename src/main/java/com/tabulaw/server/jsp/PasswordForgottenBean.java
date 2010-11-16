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
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.WebAppContext;
import com.tabulaw.server.rpc.UserServiceRpc;
import com.tabulaw.service.entity.UserService;

public class PasswordForgottenBean {

	private static final Log log = LogFactory
			.getLog(PasswordForgottenBean.class);

	private HttpServletRequest request;

	private String emailAddress;

	private List<String> errors = new ArrayList<String>();

	public void setRequest(HttpServletRequest request) {
		this.request = request;
		if (request != null) {
			setEmailAddress(StringUtils.defaultString(
					request.getParameter("userEmail")).trim());
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

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean isRemindEmailSent() {
		setErrors(new ArrayList<String>());

		if (request == null) {
			return false;
		} else if (request.getParameter("submitPasswordRemind") == null) {
			return false;
		}

		if (getEmailAddress().isEmpty()) {
			errors.add("Empty email address.");
		} else {
			HttpSession session = request.getSession(false);
			if (session == null) {
				log.fatal("No http session exists.");
				errors.add("No http session exists.");
			} else {
				try {
					UserService userService = getPersistContext()
							.getUserService();

					userService.findByEmail(getEmailAddress());

					UserServiceRpc.sendPasswordReminderEmail(getEmailAddress(),
							userService, getWebAppContext());
				} catch (ConstraintViolationException e) {
					errors.add("Invalid email format");
				} catch (EntityNotFoundException e) {
					errors.add("The account has not been found.");
				} catch (Exception e) {
					errors.add("Internal error.");
					log.error("", e);
				}
			}
		}

		return errors.isEmpty();
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
