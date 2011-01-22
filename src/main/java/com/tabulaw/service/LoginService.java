package com.tabulaw.service;

import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserService;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Andrey Levchenko
 */
public class LoginService {
    public static void authenticateUser(HttpSession session, String email, String password) throws
                                                AccountDisabledException,
                                                AccountLockedException,
                                                InvalidCredentialsException,
                                                AccountExpiredException
    {
        User user = getSessionUser(session, email);

        if (!UserService.isPasswordValid(password, user.getPassword(), user.getEmailAddress())) {
            throw new InvalidCredentialsException("Wrong user name or password");
        }
        if (!user.isEnabled()) {
            throw new AccountDisabledException("Your account is disabled");
        }
        if (user.isExpired()) {
            throw new AccountExpiredException("Your account has expired.");
        }
        if (user.isLocked()) {
            throw new AccountLockedException("Your account is locked.");
        }

        putUserToSessionContext(session, user);
    }

    public static void putUserToSessionContext(HttpSession session, User user) {
        final UserContext context = new UserContext();
        context.setUser(user);
        session.setAttribute(UserContext.KEY, context);
    }

    public static boolean checkRegisterationAndPutToSession (HttpSession session, String email) {
        try {
            User user = getSessionUser(session, email);
            putUserToSessionContext(session, user);
        } catch (InvalidCredentialsException ex) {
            return false;
        }
        return true;
    }

    private static User getSessionUser(HttpSession session, String email) throws InvalidCredentialsException {
        PersistContext persistContext = (PersistContext) session.getServletContext().getAttribute(PersistContext.KEY);
        UserService userService = persistContext.getUserService();

        User user = null;
        try {
            user = userService.findByEmail(email);
        } catch (EntityNotFoundException ex) {
            throw new InvalidCredentialsException("Wrong user name or password");
        }
        return user;
    }
}
