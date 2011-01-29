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
import com.tabulaw.service.LoginNotAllowedException;
import com.tabulaw.service.LoginService;

public class LoginBean {

    private static final Log log = LogFactory.getLog(LoginBean.class);
    protected HttpServletRequest request;
    protected String emailAddress;
    protected String password;
    protected List<String> errors = new ArrayList<String>();

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        if (request != null) {
            setEmailAddress(StringUtils.defaultString(
                    request.getParameter("userEmail")).trim());
            setPassword(StringUtils.defaultString(request.getParameter("userPswd")));
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

    protected HttpSession getSession() {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.fatal("No http session exists.");
            errors.add("No http session exists.");
        }
        return session;
    }

    public boolean isLoginValid() {
        setErrors(new ArrayList<String>());

        if (request == null) {
            return false;
        } else if (request.getParameter("submitLogin") == null) {
            return false;
        }

        HttpSession session = getSession();

        if (session != null) {
            try {
                LoginService.authenticateUser(session, emailAddress, password);

            } catch (LoginNotAllowedException ex) {
                errors.add(ex.getMessage());
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
