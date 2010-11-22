/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 1, 2010
 */
package com.tabulaw.common.data.rpc;

import java.util.List;

import com.tabulaw.common.data.Status;
import com.tabulaw.model.User;

/**
 * @author jpk
 */
public class UserListPayload extends Payload {

	private List<User> users;

	public UserListPayload() {
		super();
	}

	public UserListPayload(Status status) {
		super(status);
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
