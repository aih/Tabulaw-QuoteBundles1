package com.tabulaw.mail;

import java.util.Map;

/**
 * Mail context that holds templating info providing the ability to sent emails
 * based on a template.
 * @author jpk
 */
class TemplatedMailContext extends AbstractMailContext {

	/**
	 * The name of the template
	 */
	private String template;

	/**
	 * Is the template html based or plain-text based?
	 */
	private boolean htmlTemplate = false;

	/**
	 * The template parameters.
	 */
	private Map<String, Object> parameters;

	/**
	 * Constructor
	 * @param routing
	 * @param encoding
	 */
	public TemplatedMailContext(MailRouting routing, String encoding) {
		super(routing, encoding);
	}

	/**
	 * Constructor
	 * @param routing
	 * @param encoding
	 * @param template
	 * @param htmlTemplate
	 * @param parameters
	 */
	public TemplatedMailContext(MailRouting routing, String encoding, String template, boolean htmlTemplate,
			Map<String, Object> parameters) {
		this(routing, encoding);
		setTemplate(template, htmlTemplate, parameters);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String templateName, boolean isHtml, Map<String, Object> parameters) {
		this.template = templateName;
		this.htmlTemplate = isHtml;
		this.parameters = parameters;
	}

	public boolean isHtmlTemplate() {
		return htmlTemplate;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getName() {
		final Object o = parameters.get(TemplateComposer.SUBJECT_KEY);
		return o == null ? getTemplate() : o.toString();
	}
}
