package com.tabulaw.service.entity;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.UserCache;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;
import com.tabulaw.common.model.Authority;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.Authority.AuthorityRoles;
import com.tll.criteria.Criteria;
import com.tll.criteria.InvalidCriteriaException;
import com.tll.criteria.QueryParam;
import com.tll.dao.EntityExistsException;
import com.tll.dao.EntityNotFoundException;
import com.tll.dao.IEntityDao;
import com.tll.model.IEntity;
import com.tll.model.IEntityAssembler;
import com.tll.model.IUserRef;
import com.tll.model.NameKey;
import com.tll.schema.PropertyType;
import com.tll.service.ChangeUserCredentialsFailedException;
import com.tll.service.IForgotPasswordHandler;

/**
 * Manages the persistence of {@link User}s.
 * @author jpk
 */
public class UserService extends AbstractEntityService implements UserDetailsService, IForgotPasswordHandler  {
	
	private static final Log log = LogFactory.getLog(UserService.class);

	private static PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
	
	/**
	 * @param password
	 * @param salt
	 * @return the encoded password
	 * @throws IllegalArgumentException
	 */
	public static String encodePassword(String password, Object salt) throws IllegalArgumentException {
		if(StringUtils.isEmpty(password)) throw new IllegalArgumentException("Can't encode an empty password");
		return passwordEncoder.encodePassword(password, salt);
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
		return passwordEncoder.isPasswordValid(encPassword, rawPasswordToCheck, salt);
	}

	// private final AclProviderManager aclProviderManager;

	private final UserCache userCache;

	/**
	 * Constructor
	 * @param dao
	 * @param entityAssembler
	 * @param vfactory
	 * @param userCache
	 */
	@Inject
	public UserService(IEntityDao dao, IEntityAssembler entityAssembler, ValidatorFactory vfactory, UserCache userCache) {
		super(dao, entityAssembler, vfactory);
		// this.aclProviderManager = aclProviderManager;
		this.userCache = userCache;
		init();
	}
	
	@Transactional
	public void init() {
		// stub anon user if not alreay specified
		User anonUser = new User();
		anonUser.setId(1L);
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
		final User user = entityAssembler.assembleEntity(User.class, null);

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
		user.addAuthority(dao.load(new NameKey<Authority>(Authority.class, AuthorityRoles.ROLE_USER.toString(),
				Authority.FIELDNAME_AUTHORITY)));

		dao.persist(user);

		return user;
	}

	@Transactional(readOnly = true)
	@Override
	public IUserRef getUserRef(String username) throws EntityNotFoundException {
		// NOTE: the username is the email address
		return findByEmail(username);
	}

	/**
	 * {@link UserDetailsService} implementation
	 * @param username
	 * @return the found user
	 * @throws UsernameNotFoundException
	 * @throws DataAccessException
	 */
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		try {
			return findByEmail(username);
		}
		catch(final EntityNotFoundException enfe) {
			throw new UsernameNotFoundException("Username '" + username + "' not found");
		}
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
		updateSecurityContextIfNecessary(user.getUsername(), null, null, true);
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
			setCredentials(user.getId(), newUsername, encNewPassword);

			updateSecurityContextIfNecessary(user.getUsername(), newUsername, newRawPassword, false);
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
	public String resetPassword(Object userPk) throws ChangeUserCredentialsFailedException {

		try {
			// get the user
			final User user = dao.load(User.class, userPk);
			final String username = user.getUsername();

			// encode the new password
			final String random = RandomStringUtils.randomAlphanumeric(8);
			final String encNewPassword = encodePassword(random, username);

			// set the credentials
			setCredentials(userPk, username, encNewPassword);

			updateSecurityContextIfNecessary(username, username, random, false);

			return random;
		}
		catch(final EntityNotFoundException nfe) {
			throw new ChangeUserCredentialsFailedException("Unable to re-set user password: User of id: " + userPk
					+ " not found");
		}

	}

	private void setCredentials(Object pk, String newUsername, String encNewPassword) {
		dao.executeQuery("user.setCredentials", new QueryParam[] {
			new QueryParam(IEntity.PK_FIELDNAME, PropertyType.STRING, pk),
			new QueryParam("username", PropertyType.STRING, newUsername),
			new QueryParam("password", PropertyType.STRING, encNewPassword)
		});
	}

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
}
