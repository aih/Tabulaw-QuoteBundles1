package com.tabulaw.cassandra.om.factory;

import java.util.List;


public class SessionFacade implements Session {

	private Session impl;
	private boolean closed = false;
	
	SessionFacade(Session session) {
		impl = session;
	}

	@Override
	public void close() {
		if (! closed) {
			impl.close();
			closed = true;
		}		
	}

	@Override
	public <T> List<T> find(Class<T> klass, Object... keys) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}		
		return impl.find(klass, keys);
	}

	@Override
	public <T> T find(Class<T> klass, Object key) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		return impl.find(klass, key);
	}

	@Override
	public void flush() {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		impl.flush();
	}

	@Override
	public <T> boolean isAttached(T object) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		return impl.isAttached(object);
	}

	@Override
	public <T> T merge(T object) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		return impl.merge(object);
	}

	@Override
	public <T> void persist(T object) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		impl.persist(object);
	}

	@Override
	public <T> void refresh(T object) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		impl.refresh(object);
	}

	@Override
	public <T> void remove(T object) {
		if (closed) {
			throw new IllegalStateException("Session is closed");
		}
		impl.remove(object);
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public SessionFactory getFactory() {
		return impl.getFactory();
	}		
}
