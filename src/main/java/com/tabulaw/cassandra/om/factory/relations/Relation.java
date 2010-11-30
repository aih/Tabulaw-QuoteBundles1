package com.tabulaw.cassandra.om.factory.relations;

import me.prettyprint.cassandra.model.Mutator;
import me.prettyprint.cassandra.model.Row;

import com.tabulaw.cassandra.om.factory.SessionImpl;


public interface Relation {
	
	void load(SessionImpl session, Object object, Row<String, byte[]> row);
	
	void mutate(SessionImpl session, Mutator mutator,  Object object, Object existent);
	
	void clone(Object object, Object toClone);
}
