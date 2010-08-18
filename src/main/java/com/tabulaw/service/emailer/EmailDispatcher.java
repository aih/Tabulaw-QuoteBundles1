/**
 * The Logic Lab
 * @author jopaki
 * @since Aug 16, 2010
 */
package com.tabulaw.service.emailer;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.MailManager;

/**
 * Dispatches emails in a dedicated thread.
 * @author jopaki
 */
public class EmailDispatcher implements Runnable {

	private static final Log log = LogFactory.getLog(EmailDispatcher.class);

	private final LinkedBlockingQueue<IMailContext> queue = new LinkedBlockingQueue<IMailContext>();
	private final MailManager mailManager;

	private boolean done;

	/**
	 * Constructor
	 * @param mailManager
	 */
	@Inject
	public EmailDispatcher(MailManager mailManager) {
		super();
		if(mailManager == null) throw new NullPointerException();
		this.mailManager = mailManager;
	}

	public void queueEmail(IMailContext job) throws InterruptedException {
		queue.put(job);
	}
	
	public MailManager getMailManager() {
		return mailManager;
	}

	public synchronized void setDone() {
		done = true;
	}

	@Override
	public void run() {
		try {
			while(true) {
				IMailContext item = queue.take();
				// int len = queue.size();
				// System.out.println("List size now " + len);
				process(item);
				synchronized (this) {
					if(done) return;
				}
			}
		}
		catch(InterruptedException ex) {
			// TODO what?
			System.out.println("Email Dispatcher INTERRUPTED!");
		}
	}

	private void process(IMailContext item) {
		if(log.isDebugEnabled()) log.debug("Processing email job: " + item);
		try {
			mailManager.sendEmail(item);
			if(log.isDebugEnabled()) log.debug("Email job: " + item + " processed.");
		}
		catch(Exception e) {
			log.error("Unable to process email: " + e.getMessage(), e);
		}
	}
}
