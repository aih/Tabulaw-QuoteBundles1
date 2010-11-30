package com.tabulaw.service.entity;

import com.tabulaw.cassandra.om.factory.SessionFactory;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.User;


public class TabulawSession {
	
	public static SessionFactory FACTORY = new SessionFactory("Tabulaw", "localhost", 9160, 
			Quote.class,
			QuoteBundle.class,
			DocRef.class,
			User.class);

}
