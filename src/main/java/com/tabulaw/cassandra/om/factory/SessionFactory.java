package com.tabulaw.cassandra.om.factory;

import java.beans.IntrospectionException;
import java.util.Map;

import org.apache.cassandra.thrift.ConsistencyLevel;

import com.google.common.collect.Maps;
import com.tabulaw.cassandra.om.annotations.HelenaBean;

import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.model.ConsistencyLevelPolicy;
import me.prettyprint.cassandra.model.HFactory;
import me.prettyprint.cassandra.model.KeyspaceOperator;
import me.prettyprint.cassandra.service.Cluster;


public class SessionFactory {

	private ColumnFamiliesBuilder cfBuilder;
	private Cluster cluster;
	private String keyspace;
	private String host;
	private int port;
	private ConsistencyLevelPolicy consistencyLevelPolicy;
	
	public SessionFactory(String keyspace, String host, int port, Class<?>... classes) {
		try {
			cfBuilder = new ColumnFamiliesBuilder(classes);
		} catch (IntrospectionException ex) {
			throw new RuntimeException(ex);
		}	
		buildConsistencyLevelPolicy();
		this.keyspace = keyspace;
		this.host = host;
		this.port = port;
		cluster = HFactory.getOrCreateCluster("Tabulaw", host + ":" + port);		
	}
	
	private void buildConsistencyLevelPolicy() {
		final Map<String, ConsistencyLevel> writeLevels = Maps.newHashMap();
		final Map<String, ConsistencyLevel> readLevels = Maps.newHashMap();
		for (ColumnFamilyDescriptor descriptor : cfBuilder.getAllCFs()) {
			if (descriptor.getDescription() == null) {
				continue;
			}
			HelenaBean description = descriptor.getDescription();
			writeLevels.put(description.columnFamily(), description.writeConsistency());
			readLevels.put(description.columnFamily(), description.readConsistency());
		}
		consistencyLevelPolicy = new ConsistencyLevelPolicy() {
			
			@Override
			public ConsistencyLevel get(OperationType operationType, String cf) {
				if (operationType == OperationType.READ) {
					return readLevels.get(cf);
				} else {
					return writeLevels.get(cf);
				}
			}
			
			@Override
			public ConsistencyLevel get(OperationType operationType) {
				return ConsistencyLevel.QUORUM;
			}
		};
	}
	
	public Session createSession() {
		return new SessionFacade(new SessionImpl(this));
	}
	
	public ColumnFamilyDescriptor getCFDescriptor(Class<?> klass) {
		return cfBuilder.gtCF(klass);
	}
	
	public KeyspaceOperator createKeyspaceOperator() {
		return HFactory.createKeyspaceOperator(keyspace, cluster, consistencyLevelPolicy);
	}	
	
	public <T> T executeCommand(Command<T> command) {
		return command.execute(host, port, keyspace);
	}
}
