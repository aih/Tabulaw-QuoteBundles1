package com.tabulaw.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;

public class VelocityUtil {

	/**
	 * Merges specified Velocity template and parameters map using velocity
	 * library
	 * @param velocityEngine velocity engine instance used for merge
	 * @param templatePath path to velocity template
	 * @param params parameters map used for merge
	 * @return a result of merge.
	 * @throws VelocityException Upon merge error
	 * @throws IOException Upon closing the string writer that holds the text that
	 *         is returned
	 */
	public static String mergeVelocityTemplate(VelocityEngine velocityEngine, String templatePath,
			Map<String, Object> params) throws VelocityException, IOException {
		final StringWriter text = new StringWriter();
		final Map<String, Object> mergeObjects = new HashMap<String, Object>();
		mergeObjects.putAll(params);

		final VelocityContext velocityContext = new VelocityContext(mergeObjects);
		try {
			velocityEngine.mergeTemplate(templatePath, "UTF-8", velocityContext, text);
		}
		catch(final Exception e) {
			throw new VelocityException(e);
		}

		text.close();

		return text.toString();
	}
}
