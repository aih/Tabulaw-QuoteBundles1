package com.tabulaw.sso.step2.simple.servlet;

import com.google.inject.servlet.ServletModule;
import com.tabulaw.sso.step2.OAuthModule;

public class TestModule extends ServletModule {
	@Override
	protected void configureServlets() {
		System.out.println("INSTALL GUICE");
		install(new OAuthModule());
		servlets();
	}

	private void servlets() {
		serve("/login").with(LoginServlet.class);
		serve("/checkauth").with(CheckAuthServlet.class);
		serve("/docs").with(DocsServlet.class);
	}
}
