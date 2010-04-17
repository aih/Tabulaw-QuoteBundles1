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
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IUserRef;
import com.tabulaw.common.model.NameKey;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.Authority.AuthorityRoles;
import com.tabulaw.criteria.Criteria;
import com.tabulaw.criteria.InvalidCriteriaException;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.dao.IEntityDao;
import com.tabulaw.service.ChangeUserCredentialsFailedException;
import com.tabulaw.service.IForgotPasswordHandler;

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

		// TODO make more robust
		return Integer.toString(password.hashCode());
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
		return Integer.toString(rawPasswordToCheck.hashCode()).equals(encPassword);
	}

	// private final AclProviderManager aclProviderManager;

	// private final UserCache userCache;

	/**
	 * Constructor
	 * @param dao
	 * @param vfactory
	 */
	@Inject
	public UserService(IEntityDao dao, ValidatorFactory vfactory) {
		super(dao, vfactory);
		// this.aclProviderManager = aclProviderManager;
		// this.userCache = userCache;
		init();
	}

	@Transactional
	public void init() {
		// stub anon user if not alreay specified
		User anonUser = new User();
		Authority a = new Authority();
		a.setAuthority(AuthorityRoles.ROLE_USER.name());
		a = new Authority();
		a.setAuthority(AuthorityRoles.ROLE_ADMINISTRATOR.name());
		anonUser.addAuthority(a);
		Date now = new Date();
		anonUser.setDateCreated(now);
		anonUser.setDateModified(now);
		anonUser.setEmailAddress("anon@tabulaw.com");
		anonUser.setPassword(encodePassword("anon", anonUser.getEmailAddress()));
		anonUser.setEnabled(true);
		anonUser.setLocked(false);
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.YEAR, 1);
		anonUser.setExpires(c.getTime());
		anonUser.setName("Tabulaw Discovery User");
		try {
			dao.persist(anonUser);
			log.debug("Test anon user created.");
		}
		catch(EntityExistsException e) {
			// ok
			log.debug("Test anon user found to exist.");
		}
	}

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
		user.addAuthority((Authority) dao.load(new NameKey(EntityType.AUTHORITY.name(),
				AuthorityRoles.ROLE_USER.toString(), Authority.FIELDNAME_AUTHORITY)));

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
	private User findByEmail(String emailAddress) throws EntityNotFoundException {
		User user;
		try {
			final Criteria<User> criteria = new Criteria<User>(User.class);
			criteria.getPrimaryGroup().addCriterion("emailAddress", emailAddress, true);
			user = dao.findEntity(criteria);
		}
		catch(final InvalidCriteriaException e) {
			throw new IllegalArgumentException("Unexpected invalid criteria exception occurred");
		}
		if(user == null) {
			throw new EntityNotFoundException("User with username: " + emailAddress + " was not found.");
		}
		return user;
	}

	@Transactional
	public void purge(User user) throws EntityNotFoundException {
		dao.purge(user);
		// updateSecurityContextIfNecessary(user.getUsername(), null, null, true);
	}

	@Transactional(rollbackFor = {
		ChangeUserCredentialsFailedException.class, RuntimeException.class
	})
	public void setCredentials(String username, String newUsername, String newRawPassword)
			throws ChangeUserCredentialsFailedException {

		try {
			// get the user
			final Criteria<User> criteria = new Criteria<User>(User.class);
			criteria.getPrimaryGroup().addCriterion("emailAddress", username, true);
			final User user = dao.findEntity(criteria);

			// encode the new password
			final String encNewPassword = encodePassword(newRawPassword, newUsername);

			// set the credentials
			setCredentialsById(user.getKey().getId(), newUsername, encNewPassword);

			// updateSecurityContextIfNecessary(user.getUsername(), newUsername,
			// newRawPassword, false);
		}
		catch(final InvalidCriteriaException e) {
			throw new IllegalArgumentException(
					"Unable to chnage user credentials due to an unexpected invalid criteria exception: " + e.getMessage(), e);
		}
		catch(final EntityNotFoundException nfe) {
			throw new ChangeUserCredentialsFailedException("Unable to set user credentials: Username: '" + username
					+ "' not found");
		}
	}

	@Transactional(rollbackFor = {
		ChangeUserCredentialsFailedException.class, RuntimeException.class
	})
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
			setCredentials(userId, username, encNewPassword);

			// updateSecurityContextIfNecessary(username, username, random, false);

			return random;
		}
		catch(final EntityNotFoundException nfe) {
			throw new ChangeUserCredentialsFailedException("Unable to re-set user password: User of id: " + userId
					+ " not found");
		}

	}

	private void setCredentialsById(String id, String newUsername, String encNewPassword) {
		/*
		dao.executeQuery("user.setCredentials", new QueryParam[] {
			new QueryParam(IEntity.PK_FIELDNAME, PropertyType.STRING, pk),
			new QueryParam("username", PropertyType.STRING, newUsername),
			new QueryParam("password", PropertyType.STRING, encNewPassword)
		});
		*/
		throw new UnsupportedOperationException();
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
