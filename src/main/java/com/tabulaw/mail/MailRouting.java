package com.tabulaw.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Mail message routing properties: to, from, cc list and bcc list.
 * @author jpk
 */
public final class MailRouting implements Cloneable {

	/**
	 * The email sender.
	 */
	private NameEmail sender;

	/**
	 * The email message recipients.
	 */
	private final List<NameEmail> recipients = new ArrayList<NameEmail>();

	/**
	 * Email addresses that will be cc'd.
	 */
	private final List<NameEmail> ccList = new ArrayList<NameEmail>();

	/**
	 * Email addresses that will be bcc'd.
	 */
	private final List<NameEmail> bccList = new ArrayList<NameEmail>();

	/**
	 * Constructor
	 */
	public MailRouting() {
		super();
	}

	/**
	 * Constructor
	 * @param sender
	 */
	public MailRouting(NameEmail sender) {
		super();
		setSender(sender);
	}

	/**
	 * Constructor
	 * @param sender
	 * @param recipient
	 */
	public MailRouting(NameEmail sender, NameEmail recipient) {
		super();
		setSender(sender);
		addRecipient(recipient);
	}

	/**
	 * Constructor
	 * @param sender
	 * @param recipient
	 * @param ccList
	 * @param bccList
	 */
	public MailRouting(NameEmail sender, NameEmail recipient, List<NameEmail> ccList, List<NameEmail> bccList) {
		super();
		setSender(sender);
		addRecipient(recipient);
		setCcList(ccList);
		setBccList(bccList);
	}

	public List<NameEmail> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<NameEmail> recipients) {
		if(recipients == null || recipients.size() < 1) return;
		this.recipients.clear();
		this.recipients.addAll(recipients);
	}

	public void addRecipient(NameEmail address) {
		recipients.add(address);
	}

	public void addRecipient(String emailAddress) {
		addRecipient(new NameEmail(emailAddress));
	}

	public void addRecipient(String name, String emailAddress) {
		addRecipient(new NameEmail(name, emailAddress));
	}

	public NameEmail getSender() {
		return sender;
	}

	public void setSender(NameEmail defaultSender) {
		this.sender = defaultSender;
	}

	public void setSender(String emailAddress) {
		setSender(null, emailAddress);
	}

	public void setSender(String name, String emailAddress) {
		setSender(new NameEmail(name, emailAddress));
	}

	public List<NameEmail> getCcList() {
		return ccList;
	}

	public void setCcList(List<NameEmail> ccs) {
		if(ccs == null || ccs.size() < 1) return;
		this.ccList.clear();
		this.ccList.addAll(ccs);
	}

	public void addCc(NameEmail address) {
		ccList.add(address);
	}

	public void addCc(String emailAddress) {
		addCc(new NameEmail(null, emailAddress));
	}

	public void addCc(String name, String emailAddress) {
		addCc(new NameEmail(name, emailAddress));
	}

	public List<NameEmail> getBccList() {
		return bccList;
	}

	public void setBccList(List<NameEmail> bccs) {
		if(bccs == null || bccs.size() < 1) return;
		this.bccList.clear();
		this.bccList.addAll(bccs);
	}

	public void addBcc(NameEmail address) {
		bccList.add(address);
	}

	public void addBcc(String emailAddress) {
		addBcc(new NameEmail(null, emailAddress));
	}

	public void addBcc(String name, String emailAddress) {
		addBcc(new NameEmail(name, emailAddress));
	}

	@Override
	public MailRouting clone() {
		try {
			final MailRouting cln = (MailRouting) super.clone();
			if(this.sender != null) {
				cln.sender = this.sender.clone();
			}
			for(final NameEmail recipient : recipients) {
				cln.recipients.add(recipient.clone());
			}
			for(final NameEmail cc : ccList) {
				cln.ccList.add(cc.clone());
			}
			for(final NameEmail bcc : bccList) {
				cln.bccList.add(bcc.clone());
			}
			return cln;
		}
		catch(final CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}
