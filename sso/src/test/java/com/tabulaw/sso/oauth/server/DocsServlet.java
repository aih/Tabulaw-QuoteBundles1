package com.tabulaw.sso.oauth.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class DocsServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IOUtils.write("<a href=\"/\">Home</a><br />", response.getOutputStream());
		IOUtils.write("Google Docs", response.getOutputStream());
	}
}
