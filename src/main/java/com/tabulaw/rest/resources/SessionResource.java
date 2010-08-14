package com.tabulaw.rest.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.tabulaw.common.model.User;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.rest.dto.SessionResponse;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserService;

/**
 * 
 * REST resource to create user session
 * 
 * @author yuri
 *
 */
@Path("/session")
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
public class SessionResource extends BaseResource {
	
	@POST
	public SessionResponse authorization(
			@Context HttpServletRequest request,
			@FormParam("email") String email,
			@FormParam("password") String password)	{
		if (email == null || password == null) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		UserService service = getUserService();
		User user = null;
		try {
			user = service.findByEmail(email);
		} catch (EntityNotFoundException ex) {
			throw new WebApplicationException(
					Response.status(Response.Status.UNAUTHORIZED).
					entity("<error>Unauthorized</error>").build());
		}
		
		if (! user.isEnabled()) {
			throw new WebApplicationException(
					Response.status(Response.Status.UNAUTHORIZED).
					entity("<error>Disabled account</error>").build()
			);
		}				
		if (user.isLocked()) {
			throw new WebApplicationException(
					Response.status(Response.Status.UNAUTHORIZED).
					entity("<error>Locked account</error>").build()
			);
		}
		if (user.isExpired()) {
			throw new WebApplicationException(
					Response.status(Response.Status.UNAUTHORIZED).
					entity("<error>Expired account</error>").build()
			);
		}		
		if (! UserService.isPasswordValid(password, user.getPassword(), user.getEmailAddress())) {
			throw new WebApplicationException(
					Response.status(Response.Status.UNAUTHORIZED).
					entity("<error>Unauthorized</error>").build());
		}		
		UserContext userContext = new UserContext();
		userContext.setUser(user);
		HttpSession session = request.getSession();
		session.setAttribute(REST_USER_KEY, userContext);
		return new SessionResponse(request.getSession().getId(), user);
	}
	
}
