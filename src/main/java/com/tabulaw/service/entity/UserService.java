package com.tabulaw.service.entity;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.tabulaw.util.StringUtil;

/**
 * Manages the persistence of {@link User}s.
 * @author jpk
 */
public class UserService implements IForgotPasswordHandler {

	// private static PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

	/**
	 * @param password
	 * @param salt
	 * @return the encoded password
	 * @throws IllegalArgumentException
	 */
	public static String encodePassword(String password, Object salt) throws IllegalArgumentException {
		if(StringUtils.isEmpty(password)) throw new IllegalArgumentException("Can't encode an empty password");
		// return passwordEncoder.encodePassword(password, salt);

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
		if(StringUtils.isEmpty(rawPasswordToCheck)) throw new IllegalArgumentException("Empty raw password specified");
		if(StringUtils.isEmpty(encPassword)) throw new IllegalArgumentException("Empty encoded password specified");
		// return passwordEncoder.isPasswordValid(encPassword, rawPasswordToCheck,
		// salt);

		// TODO make more robust
		return CryptoUtil.encrypt(rawPasswordToCheck).equals(encPassword);
	}

	// private final UserCache userCache;

	/**
	 * Constructor
	 * @param dao
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
		return null;
	}

	/**
	 * Loads a user given from the given user id.
	 * @param userId
	 * @return the found user
	 * @throws EntityNotFoundException when the user can't be found
	 */
	public User loadUser(String userId) throws EntityNotFoundException {
		return null;
	}

	/**
	 * Purges a user given its id from the system entirely.
	 * <p>
	 * <b>WARNING:</b>All user dependent quote and bundle data is also deleted.
	 * @param userId
	 * @throws EntityNotFoundException When the user of the given id isn't found
	 */
	public void deleteUser(String userId) throws EntityNotFoundException {

	}

	/**
	 * Updates a user not including its password.
	 * @param user
	 * @return
	 */
	public User updateUser(User user) {
		return null;
	}

	/**
	 * Create a user.
	 * @param name
	 * @param emailAddress
	 * @param password
	 * @return
	 * @throws ValidationException
	 * @throws EntityExistsException
	 */
	public User create(String name, String emailAddress, String password) throws ValidationException,
			EntityExistsException {
		return null;
	}

	public IUserRef getUserRef(String username) throws EntityNotFoundException {
		return null;
	}

	public User findByEmail(String emailAddress) throws EntityNotFoundException {
		return null;
	}

	@Override
	public String resetPassword(String userId) throws ChangeUserCredentialsFailedException {
		return null;
	}

	/**
	 * Manually sets a user's password.
	 * @param userId id of the user for which to set the password
	 * @param password the new un-encoded password to set
	 * @throws ChangeUserCredentialsFailedException when the password set fails
	 */
	public void setPassword(String userId, String password) throws ChangeUserCredentialsFailedException {

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
}
