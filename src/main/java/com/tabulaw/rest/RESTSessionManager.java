package com.tabulaw.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class RESTSessionManager implements HttpSessionListener {
	
	private static final Map<String, HttpSession> sessions = 
				Collections.synchronizedMap(new HashMap<String, HttpSession>());

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		sessions.put(event.getSession().getId(), event.getSession());		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		sessions.remove(event.getSession().getId());		
	}
	
	public static HttpSession findSession(String id) {
		return sessions.get(id);
	}
}
