package com.tabulaw.sso.oauth.server;

import com.google.inject.servlet.ServletModule;

public class OAuthModule extends ServletModule {
	@Override
	protected void configureServlets() {
		System.out.println("INSTALL GUICE");
		servlets();
	}

	private void servlets() {
//		serve("/login").with(LoginServlet.class);
//		serve("/checkauth").with(CheckAuthServlet.class);
//		serve("/docs").with(DocsServlet.class);
	}
}
