
package model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseAccess
{
	private final String databaseName;
	private Connection connection;
	private Statement statement;

	public static boolean databaseExists(String name)
	{
		return false;
	}

	public DatabaseAccess(String databaseName)
	{
		this.databaseName = databaseName;
	}

	/**
	 * Adds an item to the database
	 * @param item The item to add
	 */
	public void addItem(IItem item)
	{
		// I'm assuming that all the information about the item, its product and
		// product container have been set correctly before we get here...
		String query =
				"INSERT INTO Item (\"productID\", \"Barcode\", \"EntryDate\", \"ExitTime\", "
						+ "\"ExpirationDate\", \"containerID\") VALUES (\""
						+ item.getProduct().getId() + "\", \""
						+ item.getBarcode().getNumber() + "\", \""
						+ item.getEntryDate().getTime() + "\", \""
						+ item.getExitTime().getTime() + "\", \""
						+ item.getExpirationDate().getTime() + "\", \""
						+ item.getContainer().getId() + "\")";

		try
		{
			this.createConnection();
			this.statement.executeUpdate(query);
		}
		catch(ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				this.closeConnection();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * Adds a product to the database
	 * @param product The product to add
	 */
	public void addProduct(IProduct product)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds a product container to the database
	 * @param productContainer The product container to add
	 */
	public void addProductContainer(IProductContainer productContainer)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the complete Product Container tree from the database. This will go
	 * get EVERYTHING from the database and build the in memory representation
	 */
	public List<IStorageUnit> getAllStorageUnits()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * This method will create a connection with the database
	 * @throws SQLException If something bad happens when attempting to create
	 *             the statements to be executed
	 * @throws ClassNotFoundException If something bad happens when attempting
	 *             to load the SQLite driver
	 */
	private void createConnection() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");
		String path = "jdbc:sqlite:database" + File.separator + databaseName;
		this.connection = DriverManager.getConnection(path);
		this.statement = this.connection.createStatement();
	}

	/**
	 * This method will close the connection with the database
	 * @throws SQLException If something bad happens when trying to close the
	 *             connection
	 */
	private void closeConnection() throws SQLException
	{
		this.statement.close();
		this.connection.close();
	}

	/**
	 * Removes an item by id from the database
	 * @param item The item to remove
	 */
	public void removeItem(IItem item)
	{

	}

	/**
	 * Removes a product by id from the database
	 * @param product The product to remove
	 */
	public void removeProduct(IProduct product)
	{

	}

	/**
	 * Removes a product container by id from the database
	 * @param container The container to remove
	 */
	public void removeProductContainer(IProductContainer container)
	{

	}

	/**
	 * Updates an item in the database
	 */
	public void updateItem(IItem item)
	{

	}

	/**
	 * Updates a product container in the database
	 * @param productContainer
	 */
	public void updateProductContainer(IProductContainer productContainer)
	{

	}

	/**
	 * Updates a product in the database
	 * @param product
	 */
	public void updateProdut(IProduct product)
	{

	}
}
