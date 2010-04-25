package com.tabulaw.service.entity;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.common.model.Authority;
import com.tabulaw.common.model.IUserRef;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.Authority.AuthorityRoles;
import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.dao.IEntityDao;
import com.tabulaw.service.ChangeUserCredentialsFailedException;
import com.tabulaw.service.IForgotPasswordHandler;
import com.tabulaw.util.CryptoUtil;

/**
 * Manages the persistence of {@link User}s.
 * @author jpk
 */
public class UserService extends AbstractEntityService implements IForgotPasswordHandler {

	private static final Log log = LogFactory.getLog(UserService.class);

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
	public UserService(IEntityDao dao, ValidatorFactory vfactory) {
		super(dao, vfactory);
	}

	@Transactional
	public void init() {

		// stub authorities if not present
		for(AuthorityRoles role : AuthorityRoles.values()) {
			try {
				dao.load(Authority.class, role.name());
			}
			catch(EntityNotFoundException e) {
				dao.persist(new Authority(role.name()));
			}
		}

		try {
			// load admin account and create if not present
			dao.load(User.class, "admin@tabulaw.com");
			log.debug("Admin user retrieved.");
		}
		catch(EntityNotFoundException e) {
			// create the admin user
			User adminUser = new User();
			Authority adminAuth = dao.load(Authority.class, AuthorityRoles.ROLE_ADMINISTRATOR.name());
			adminUser.addAuthority(adminAuth);
			Date now = new Date();
			adminUser.setDateCreated(now);
			adminUser.setDateModified(now);
			adminUser.setEmailAddress("admin@tabulaw.com");
			adminUser.setPassword(encodePassword("admin123", adminUser.getEmailAddress()));
			adminUser.setEnabled(true);
			adminUser.setLocked(false);
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			c.add(Calendar.YEAR, 1);
			adminUser.setExpires(c.getTime());
			adminUser.setName("Tabulaw Administrator");

			dao.persist(adminUser);
			log.debug("admin user created.");
		}
	}

	/**
	 * Create a user.
	 * @param emailAddress
	 * @param password
	 * @return
	 * @throws ValidationException
	 * @throws EntityExistsException
	 */
	@Transactional
	public User create(String emailAddress, String password) throws ValidationException, EntityExistsException {
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

		// set default expiry date to 1 day from now
		final Calendar clndr = Calendar.getInstance();
		clndr.add(Calendar.DAY_OF_MONTH, 1);
		final Date expires = clndr.getTime();
		user.setExpires(expires);

		// set the user as un-locked by default
		user.setLocked(false);

		// set the role as user by default
		user.addAuthority(dao.load(Authority.class, AuthorityRoles.ROLE_ADMINISTRATOR.name()));

		dao.persist(user);

		return user;
	}

	@Transactional(readOnly = true)
	@Override
	public IUserRef getUserRef(String username) throws EntityNotFoundException {
		// NOTE: the username is the email address
		return findByEmail(username);
	}

	@Transactional(readOnly = true)
	public User findByEmail(String emailAddress) throws EntityNotFoundException {
		User user;
		try {
			final Criteria<User> criteria = new Criteria<User>(User.class);
			criteria.getPrimaryGroup().addCriterion("emailAddress", emailAddress, true);
			user = dao.findEntity(criteria);
		}
		catch(final InvalidCriteriaException e) {
			throw new IllegalStateException("Unexpected invalid criteria exception occurred");
		}
		catch(EntityNotFoundException e) {
			throw new EntityNotFoundException("No user with email address: '" + emailAddress + "' was found.");
		}

		assert user != null;
		return user;
	}

	@Transactional
	public void purge(User user) throws EntityNotFoundException {
		dao.purge(user);
		// updateSecurityContextIfNecessary(user.getUsername(), null, null, true);
	}

	@Transactional(rollbackFor = {
		ChangeUserCredentialsFailedException.class, RuntimeException.class })
	@Override
	public String resetPassword(String userId) throws ChangeUserCredentialsFailedException {

		try {
			// get the user
			final User user = dao.load(User.class, userId);
			final String username = user.getEmailAddress();

			// encode the new password
			final String random = RandomStringUtils.randomAlphanumeric(8);
			final String encNewPassword = encodePassword(random, username);

			// set the credentials
			user.setPassword(encNewPassword);
			dao.persist(user);

			// updateSecurityContextIfNecessary(username, username, random, false);

			return random;
		}
		catch(final EntityNotFoundException nfe) {
			throw new ChangeUserCredentialsFailedException("Unable to re-set user password: User of id: " + userId
					+ " not found");
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
}