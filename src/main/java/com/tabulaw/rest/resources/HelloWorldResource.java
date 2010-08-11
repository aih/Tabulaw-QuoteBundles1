package com.tabulaw.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.tabulaw.rest.AuthorizationRequired;

@Path("/helloworld")
@Produces("text/plain")
@AuthorizationRequired
public class HelloWorldResource {

	@GET
	public String sayHello() {
		return "Hello world!";
	}
}
