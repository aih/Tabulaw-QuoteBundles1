package com.tabulaw.sso.oauth.server;

import com.google.inject.servlet.ServletModule;
import com.tabulaw.sso.oauth.server.servlet.DocsServlet;
import com.tabulaw.sso.oauth.server.servlet.LoginServlet;

public class OAuthModule extends ServletModule {
	@Override
	protected void configureServlets() {
		System.out.println("INSTALL GUICE");
		servlets();
	}

	private void servlets() {
		serve("/login").with(LoginServlet.class);
		serve("/docs").with(DocsServlet.class);
	}
}
