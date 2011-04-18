package com.tabulaw.service.entity;

import java.beans.XMLEncoder;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.IUserRef;
import com.tabulaw.model.User;
import com.tabulaw.model.User.Role;
import com.tabulaw.util.CryptoUtil;
import com.tabulaw.util.UUID;

/**
 * Manages the persistence of {@link User}s.
 *
 * @author jpk
 */
public class UserService implements IForgotPasswordHandler {

    /**
     * @param password
     * @param salt
     * @return the encoded password
     * @throws IllegalArgumentException
     */
    public static String encodePassword(String password, Object salt) throws IllegalArgumentException {
        if (StringUtils.isEmpty(password)) throw new IllegalArgumentException("Can't encode an empty password");
        return CryptoUtil.encrypt(password);
    }

    /**
     * @param rawPasswordToCheck
     * @param encPassword
     * @param salt
     * @return true/false
     * @throws IllegalArgumentException
     */
    public static boolean isPasswordValid(String rawPasswordToCheck, String encPassword, Object salt)
            throws IllegalArgumentException {
        if (StringUtils.isEmpty(rawPasswordToCheck)) throw new IllegalArgumentException("Empty raw password specified");
        if (StringUtils.isEmpty(encPassword)) throw new IllegalArgumentException("Empty encoded password specified");

        // TODO make more robust
        return CryptoUtil.encrypt(rawPasswordToCheck).equals(encPassword);
    }

    // private final UserCache userCache;

    /**
     * Constructor
     *
     * @param vfactory
     */
    @Inject
    public UserService(ValidatorFactory vfactory) {

    }

    public void init() {

    }

    /**
     * @return list of all users in the system.
     */
    public List<User> getAllUsers() {
        System.out.println("getAllUsers");
        List<User> ret = new ArrayList<User>();
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_user", Statement.NO_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(dao.loadUser(rs));
            }
            return ret;
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    /**
     * Purges a user given its id from the system entirely.
     * <p/>
     * <b>WARNING:</b>All user dependent quote and bundle data is also deleted.
     *
     * @param userId
     * @throws EntityNotFoundException When the user of the given id isn't found
     */
    public void deleteUser(String userId) throws EntityNotFoundException {
        System.out.println("deleteUser " + userId);
        throw new UnsupportedOperationException();
    }

    /**
     * Updates a user not including its password.
     *
     * @param user
     * @return
     */
    public User updateUser(User user) {
        System.out.println("updateUser " + user.getId() +" pwd="+user.getPassword());
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("update tw_user set user_emailaddress=?, user_enabled=?, user_expires=?, user_locked=?, user_name=?, user_roles=? where user_id=?", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,user.getEmailAddress());
            ps.setBoolean(2, user.isEnabled());
            ps.setDate(3,new java.sql.Date(user.getExpires().getTime()));
            ps.setBoolean(4,user.isLocked());
            ps.setString(5,user.getName());
            // ps.setString(6,user.getPassword());
            ps.setString(6, dao.toXML(user.getRoles()));
            ps.setString(7, user.getId());

            ps.executeUpdate();
            return user;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }

    }

    /**
     * Create a user.
     *
     * @param name
     * @param emailAddress
     * @param password
     * @return
     * @throws ValidationException
     * @throws EntityExistsException
     */
    public User create(String name, String emailAddress, String password) throws ValidationException,
            EntityExistsException {
        System.out.println("create user " + name);
        if(name == null || emailAddress == null || password == null) throw new NullPointerException();

        final User user = new User();

        String encPassword = null;
        try {
            encPassword = encodePassword(password, emailAddress);
        }
        catch(final IllegalArgumentException iae) {
            throw new ValidationException("Invalid password");
        }

        user.setEmailAddress(emailAddress);
        user.setPassword(encPassword);
        user.setName(name);

        // set default expiry date to 6 years from now
        final Calendar clndr = Calendar.getInstance();
        clndr.add(Calendar.YEAR, 6);
        final java.util.Date expires = clndr.getTime();
        user.setExpires(expires);

        // set the user as un-locked by default
        user.setLocked(false);

        // set the role as user
        user.addRole(Role.USER);

        // set id
        user.setId(UUID.uuid());

        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("insert into tw_user(user_emailaddress, user_enabled, user_expires, user_locked, user_name, user_password, user_roles, user_id) values (?,?,?,?,?,?,?,?)", Statement.NO_GENERATED_KEYS);
            ps.setString(1,user.getEmailAddress());
            ps.setBoolean(2, user.isEnabled());
            ps.setDate(3,new java.sql.Date(user.getExpires().getTime()));
            ps.setBoolean(4,user.isLocked());
            ps.setString(5,user.getName());
            ps.setString(6,user.getPassword());
            ps.setString(7, dao.toXML(user.getRoles()));
            ps.setString(8, user.getId());

            ps.executeUpdate();
            System.out.println("id="+user.getId());
            return user;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
   }

    public IUserRef getUserRef(String username) throws EntityNotFoundException {
        System.out.println("getUserRef " + username);
        throw new UnsupportedOperationException();
    }

    public User findByEmail(String emailAddress) throws EntityNotFoundException {
        System.out.println("findByEmail " + emailAddress);
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_user where user_emailaddress=?", Statement.NO_GENERATED_KEYS);
            ps.setString(1,emailAddress);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return dao.loadUser(rs);
            }
            throw new EntityNotFoundException("No user with email address: '" + emailAddress + "' was found.");

        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    @Override
    public String resetPassword(String userId) throws ChangeUserCredentialsFailedException {
        System.out.println("resetPassword " + userId);
        throw new UnsupportedOperationException();
    }

    /**
     * Manually sets a user's password.
     *
     * @param userId   id of the user for which to set the password
     * @param password the new un-encoded password to set
     * @throws ChangeUserCredentialsFailedException
     *          when the password set fails
     */
    public void setPassword(String userId, String password) throws ChangeUserCredentialsFailedException {
        System.out.println("setPassword " + userId);
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("update tw_user set user_password=? where user_id=?", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, password);
            ps.setString(2, userId);

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }

    /*
     private final void updateSecurityContextIfNecessary(final String originalUsername, final String newUsername,
             final String newPassword, final boolean justRemove) {

         final SecurityContext securityContext = SecurityContextHolder.getContext();
         if(securityContext == null) return;

         final Authentication authentication = securityContext.getAuthentication();
         if(authentication == null) return;

         final Object principal = authentication.getPrincipal();
         if(principal instanceof User == false) return;
         final User user = (User) authentication.getPrincipal();

         if(user.getUsername().equals(originalUsername)) {
             if(userCache != null) {
                 userCache.removeUserFromCache(originalUsername);
             }
             if(justRemove) {
                 SecurityContextHolder.clearContext();
             }
             else {
                 final UsernamePasswordAuthenticationToken token =
                         new UsernamePasswordAuthenticationToken(newUsername, newPassword);
                 token.setDetails(authentication.getDetails());
                 SecurityContextHolder.getContext().setAuthentication(token);
             }
             log.info((justRemove ? "Removed" : "Reset") + " security context for user: " + originalUsername);
         }
     }
     */
    
    /**
     * @return tries to suggest user using name or email address.
     */
    public List<User> suggestUsername(String query, int suggestionCount) {
        System.out.println("suggestUsername");
        List<User> ret = new ArrayList<User>();
        String likeExp = String.format("%%%s%%", query);
        Dao dao = new Dao();
        try {
            PreparedStatement ps = dao.getPreparedStatement("select * from tw_user where user_name like ? or user_emailaddress like ? limit ?", Statement.NO_GENERATED_KEYS);
            ps.setString(1, likeExp);
            ps.setString(2, likeExp);
            ps.setInt(3, suggestionCount);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(dao.loadUser(rs));
            }
            return ret;
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            dao.cleanUp();
        }
    }
    
}
