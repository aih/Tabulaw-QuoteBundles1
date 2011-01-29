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
    public static void authenticateUser(HttpSession session, String email, String password) throws LoginNotAllowedException
    {
        User user = getSessionUser(session, email);

        if (!UserService.isPasswordValid(password, user.getPassword(), user.getEmailAddress())) {
            throw new LoginNotAllowedException("Wrong user name or password");
        }
        if (!user.isEnabled()) {
            throw new LoginNotAllowedException("Your account is disabled");
        }
        if (user.isExpired()) {
            throw new LoginNotAllowedException("Your account has expired.");
        }
        if (user.isLocked()) {
            throw new LoginNotAllowedException("Your account is locked.");
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
        } catch (LoginNotAllowedException ex) {
            return false;
        }
        return true;
    }

    private static User getSessionUser(HttpSession session, String email) throws LoginNotAllowedException {
        PersistContext persistContext = (PersistContext) session.getServletContext().getAttribute(PersistContext.KEY);
        UserService userService = persistContext.getUserService();

        User user = null;
        try {
            user = userService.findByEmail(email);
        } catch (EntityNotFoundException ex) {
            throw new LoginNotAllowedException("Wrong user name or password");
        }
        return user;
    }
}
