package com.tabulaw.service.entity;

import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.google.inject.Inject;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.model.IUserRef;
import com.tabulaw.model.User;
import com.tabulaw.model.User.Role;
import com.tabulaw.util.CryptoUtil;
import com.tabulaw.util.UUID;
import com.tabulaw.util.XStreamUtils;

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

    private SimpleJdbcTemplate simpleJdbcTemplate;

    /**
     * Constructor
     *
     * @param vfactory
     */
    @Inject
    public UserService(ValidatorFactory vfactory, DataSource ds) {
    	simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
		
        
    }


    public void init() {

    }

    /**
     * @return list of all users in the system.
     */
    public List<User> getAllUsers() {
        System.out.println("getAllUsers");
        List<User> result = this.simpleJdbcTemplate.query(
        		"select * from tw_user",
				new UserRowMapper());        

        return result;
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
        
        this.simpleJdbcTemplate.update(
        			"update tw_user set " +
        			"	user_emailaddress=?," +
        			"	user_enabled=?," +
        			"	user_expires=?," +
        			"	user_locked=?," +
        			"	user_name=?," +
        			"	user_roles=?" +
        			"	where user_id=?" 
        			,user.getEmailAddress()
        			,user.isEnabled()
        			,new java.sql.Date(user.getExpires().getTime())
        			,user.isLocked()
        			,user.getName()
        			,XStreamUtils.toXML(user.getRoles())
        			,user.getId());

        return user;
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

        this.simpleJdbcTemplate.update(
        		"insert into tw_user(" +
        		"	user_emailaddress," +
        		"	user_enabled," +
        		"	user_expires," +
        		"	user_locked," +
        		"	user_name," +
        		"	user_password," +
        		"	user_roles," +
        		"	user_id)" +
        		" values (?,?,?,?,?,?,?,?)"
                , user.getEmailAddress()
                , user.isEnabled()
                , new java.sql.Date(user.getExpires().getTime())
                , user.isLocked()
                , user.getName()
                , user.getPassword()
                , XStreamUtils.toXML(user.getRoles())
                , user.getId());

        return user;
   }

    public IUserRef getUserRef(String username) throws EntityNotFoundException {
        System.out.println("getUserRef " + username);
        throw new UnsupportedOperationException();
    }

    public User findByEmail(String emailAddress) throws EntityNotFoundException {
        System.out.println("findByEmail " + emailAddress);

		try {
			User user = this.simpleJdbcTemplate.queryForObject("select * from tw_user where user_emailaddress=?",
					new UserRowMapper(), emailAddress);
			return user;

		} catch (EmptyResultDataAccessException erd) {
			throw new EntityNotFoundException("No such user", erd);
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
        this.simpleJdbcTemplate.update("update tw_user set user_password=? where user_id=?"
        		, password
        		, userId);
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
        String likeExp = String.format("%%%s%%", query).toLowerCase();
        List<User> ret = simpleJdbcTemplate.query("select * from tw_user where lower(user_name) like ? or lower(user_emailaddress) like ?  limit ?"
        		, new UserRowMapper()
        		, likeExp
        		, likeExp
        		, suggestionCount
        		);
        return ret;
    }
    
}
