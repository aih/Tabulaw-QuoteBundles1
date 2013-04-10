package com.tabulaw.server.jsp;

import com.tabulaw.service.LoginService;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

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
import com.tabulaw.util.CryptoUtil;

public class RegisterBean extends LoginBean{

	private static final Log log = LogFactory.getLog(RegisterBean.class);

	private String userName;

	private String betaKey;
	private String passwordConfirm;
	private boolean isLoginRequired = false;
	private boolean generatePassword = false;

        @Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
		if (request != null) {
			setUserName(StringUtils.defaultString(
					request.getParameter("userName")).trim());

			setEmailAddress(StringUtils.defaultString(
					request.getParameter("userEmail")).trim());
			setPassword(StringUtils.defaultString(request
					.getParameter("userPswd")));
			setPasswordConfirm(StringUtils.defaultString(request
					.getParameter("userPswdConfirm")));

			setBetaKey(StringUtils.defaultString(request
					.getParameter("betaKey")));
		}
	}
	

	public String getBetaKey() {
		return betaKey;
	}


	public void setBetaKey(String betaKey) {
		this.betaKey = betaKey;
	}


	public boolean isLoginRequired() {
		return isLoginRequired;
	}

	public void setLoginRequired(boolean isLoginRequired) {
		this.isLoginRequired = isLoginRequired;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isRegistrationValid() {
		setErrors(new ArrayList<String>());

		if (request == null) {
			return false;
		} else if (request.getParameter("submitRegister") == null) {
			return false;
		}

		UserService userService = null;
		
		
		if (getUserName().isEmpty()) {
			errors.add("User name is required.");
		}

                if (!generatePassword) {
                    if (getPassword().isEmpty()) {
                            errors.add("Password is required.");
                    }
                    if (!getPassword().equals(getPasswordConfirm())) {
                            errors.add("Password doesn't match.");
                    }
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
        User user = null;
        try {
            String persistedPassword;
            if (generatePassword) {
                persistedPassword = CryptoUtil.generatePassword();
            } else {
                persistedPassword = getPassword();
            }
            user = userService.create(getUserName(),
                    getEmailAddress(), persistedPassword);
            sendEmail(user);
        } catch (EntityExistsException e) {
            errors.add("Email already exists");
        } catch (ConstraintViolationException e) {
            errors.add("Invalid email format");
        } catch (Exception e) {
            errors.add("Internal error");
            log.error("", e);
        }
        if (isLoginRequired && errors.size() == 0) {
            HttpSession session = getSession();
            LoginService.putUserToSessionContext(session, user);
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

    /**
     * @return the generatePassword
     */
    public boolean isGeneratePassword() {
        return generatePassword;
    }

    /**
     * @param generatePassword the generatePassword to set
     */
    public void setGeneratePassword(boolean generatePassword) {
        this.generatePassword = generatePassword;
    }

}
