package com.tabulaw.oauth;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gwt.dev.util.collect.HashMap;

public class OAuthParameters extends GoogleOAuthParameters {

	private final static Log log = LogFactory.getLog(OAuthParameters.class);

	public OAuthParameters() {
	}

	public OAuthParameters(GoogleOAuthParameters init) {
		fromMap(init.getBaseParameters());
		fromMapExtra(init.getExtraParameters());
	}

	public OAuthParameters(Map<String, String> parameters,
			Map<String, String> parametersExtra) {
		fromMap(parameters);
		fromMapExtra(parametersExtra);
	}

	public HashMap<String, String> toMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : getExtraParameters().entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	public HashMap<String, String> toMapExtra() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : getExtraParameters().entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	public void fromMap(Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			log.debug("basic: " + entry.getKey() + " -> " + entry.getValue());
			addCustomBaseParameter(entry.getKey(), entry.getValue());
		}
	}

	public void fromMapExtra(Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			log.debug("extra: " + entry.getKey() + " -> " + entry.getValue());
			addExtraParameter(entry.getKey(), entry.getValue());
		}
	}
}
