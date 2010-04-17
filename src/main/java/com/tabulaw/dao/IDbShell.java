/**
 * The Logic Lab
 * @author jpk
 * @since Jul 2, 2009
 */
package com.tabulaw.dao;

/**
 * IDbShell - Definition for handling stubbing ops for a db based on a defined
 * schema.
 * @author jpk
 */
public interface IDbShell {

	/**
	 * Creates the database. If the db already exists, nothing happens.
	 * @throws RuntimeException when the create operation fails
	 */
	void create() throws RuntimeException;

	/**
	 * Drops the database. If the db doesn't exist, nothing happens.
	 * @throws RuntimeException when the drop operation fails
	 */
	void drop() throws RuntimeException;

	/**
	 * Removes data from the datastore.
	 * @throws RuntimeException when the clear data operation fails
	 */
	void clearData() throws RuntimeException;

	/**
	 * Inserts data to the db tables.
	 * @throws RuntimeException when the add data operation fails
	 */
	void addData() throws RuntimeException;
}