
package model;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import model.exception.*;

/**
 * Singleton class to manage the entire Inventory system
 * @author Brian
 * 
 */
public class Inventory implements Serializable
{
	/**
	 * Gets the static Inventory instance. Creates it if it does not exist.
	 * @pre (none)
	 * @post The single static instance of Inventory is not null
	 * @return The one and only instance of Inventory
	 */
	public static Inventory getInstance()
	{
		if(instance == null)
			instance = new Inventory();
		return instance;
	}
	
	protected static void setInstance(Inventory inventory)
	{
		instance = inventory;
	}

	/**
	 * A list of all the storage units
	 */
	private Set<StorageUnit> storageUnits;

	/**
	 * Persistence object for saving and loading data
	 */
	private IPersistence persistence;

	/**
	 * Mapping of all Item expirations, for easy retrieval
	 */
	private SortedMap<Date, Set<Item>> itemExpirations;

	/**
	 * Mapping of all removed Items, for easy retrieval
	 */
	private SortedMap<Date, Set<Item>> removedItems;
	/**
	 * Mapping for the nMonthSupply report. The Date key represents a month. The
	 * corresponding products are those which will last up to the key's month
	 */
	private SortedMap<Date, Set<Product>> nMonthSupplyMap;

	/**
	 * Date Mapping for the nMonthSupply report. The Date key represents a
	 * month. The corresponding ProductGroups are those which will last up to
	 * the key's month
	 */
	private SortedMap<Date, Set<ProductGroup>> nMonthSupplyGroupMap;

	/**
	 * Static reference to the one and only Inventory instance
	 */
	private static Inventory instance;

	/**
	 * Creates a new instance of the Inventory class. Used only by singleton
	 * getter.
	 * @pre (none)
	 * @post This instance of Inventory will be ready for use
	 */
	private Inventory()
	{
		this.storageUnits = new HashSet<StorageUnit>();
		this.persistence = new Serializer("./data.inventory");
		this.itemExpirations = new TreeMap<Date, Set<Item>>();
		this.removedItems = new TreeMap<Date, Set<Item>>();
		this.nMonthSupplyMap = new TreeMap<Date, Set<Product>>();
		this.nMonthSupplyGroupMap = new TreeMap<Date, Set<ProductGroup>>();
		this.lastGeneratedBarCode = 400000000000l;
	}

	/**
	 * Adds a new Storage Unit to the system
	 * @pre A storage unit does not exist with the same name
	 * @post The passed storage unit has been inserted into the system
	 * @param storageUnit The new storage unit
	 * @throws InvalidNameException, NullPointerException 
	 */
	public void addStorageUnit(StorageUnit storageUnit) 
			throws InvalidNameException, NullPointerException
	{
		if(storageUnit == null)
			throw new NullPointerException();
		else if (this.storageUnitNameInUse(storageUnit.getName()))
			throw new InvalidNameException();
		
		this.storageUnits.add(storageUnit);
	}

	/**
	 * Gets all known products throughout all containers and their
	 * sub-containers
	 * @pre (none)
	 * @post A list has been created containing all known products. This list
	 *       may be empty
	 * @return A list containing references to all of the products
	 */
	public Set<Product> getAllProducts()
	{
		HashSet<Product> products = new HashSet<Product>();
		for(StorageUnit unit : this.storageUnits)
		{
			recurseProductContainer(unit, products);
		}
		return products;
	}

	/**
	 * Adds all products from a container to the working set and recurses the sub-containers
	 * @param container The container to add the products from and recurse the children of
	 * @pre workingSet is not null
	 * @post workingSet contains all products from container and its children
	 * @param workingSet The current set being built
	 */
	private void recurseProductContainer(ProductContainer container, Set<Product> workingSet)
	{
		workingSet.addAll(container.getAllProducts());
		for(ProductContainer subContainer : container.getAllProductGroups())
		{
			recurseProductContainer(subContainer, workingSet);
		}
	}
	
	/**
	 * Gets a list of all Storage Units in the system
	 * @pre (none)
	 * @post A list has been created containing all known Storage Units. This
	 *       list may be empty
	 * @return A list containing references to all top level ProductContainers
	 */
	public Set<StorageUnit> getAllStorageUnits()
	{
		return new HashSet<StorageUnit>(this.storageUnits);
	}

	/**
	 * Gets a map of Dates to what items expire on those dates
	 * @pre (none)
	 * @post A Map has been created with the aforementioned expiration dates.
	 *       This map may be empty.
	 * @return A copy of the map containing all known expiration dates mapped to
	 *         which items are expiring
	 */
	public SortedMap<Date, Set<Item>> getItemExpirations()
	{
		return new TreeMap<Date, Set<Item>>(this.itemExpirations);
	}

	/**
	 * Gets a map of Dates (each of which represents a specific month) mapped to
	 * what ProductGroups' three month supplies expire in the key's month
	 * @pre (none)
	 * @post A map has been created with the aforementioned properties. This map
	 *       may be empty.
	 * @return A copy of the map containing months correlated to what
	 *         ProductGroups' three month supplies expire on that month
	 */
	public SortedMap<Date, Set<ProductGroup>> getNMonthSupplyGroupMap()
	{
		return new TreeMap<Date, Set<ProductGroup>>(this.nMonthSupplyGroupMap);
	}

	/**
	 * Gets a map of Dates (each of which represents a specific month) mapped to
	 * what Products' three month supplies expire in the key's month
	 * @pre (none)
	 * @post A map has been created with the aforementioned properties. This map
	 *       may be empty.
	 * @return A copy of the map containing months correlated to what products'
	 *         three month supplies expire on that month
	 */
	public SortedMap<Date, Set<Product>> getNMonthSupplyMap()
	{
		return new TreeMap<Date, Set<Product>>(this.nMonthSupplyMap);
	}

	/**
	 * Gets a map of Dates to what items were removed on each date
	 * @pre (none)
	 * @post A map has been created with the aforementioned properties. This map
	 *       may be empty.
	 * @return A copy of the map containing all item removal dates mapped to the
	 *         items removed.
	 */
	public SortedMap<Date, Set<Item>> getRemovedItems()
	{
		return new TreeMap<Date, Set<Item>>(this.removedItems);
	}
	
	/**
	 * Reports that an item has been removed from the system
	 * @pre Item is not null
	 * @post Item's exit time has been set to the current time, 
	 * its container is null, and it has been added to the map
	 * of removed items
	 * @param item The item that has been removed
	 */
	protected void reportRemovedItem(Item item)
	{
		Date current = new Date();
		item.setExitTime(current);
		item.setContainer(null);
		
		Date month = new Date(current.getYear(), current.getMonth(), current.getDate());
		
		Set<Item> itemsForMonth;
		if(removedItems.containsKey(month))
			itemsForMonth = removedItems.get(month);
		else itemsForMonth = new HashSet<Item>();
		itemsForMonth.add(item);
		
		this.removedItems.put(month, itemsForMonth);
	}

	/**
	 * Clears all Storage Units from the system
	 * @pre (none)
	 * @post No storage units exist in the system
	 */
	public void removeAllStorageUnits()
	{
		this.storageUnits.clear();
	}

	/**
	 * Removes a storage unit from the system
	 * @pre The passed storage unit exists in the system
	 * @post The passed storage unit is no longer in the system
	 * @param storageUnit The Storage Unit to remove
	 */
	public void removeStorageUnit(StorageUnit storageUnit)
	{
		if(!this.storageUnits.contains(storageUnit))
			throw new InvalidParameterException("Passed StorageUnit was not found");
			
		this.storageUnits.remove(storageUnit);
	}
	
	/**
	 * Determined if a storage unit is valid for addition
	 * @param storageUnit The new storage unit to check
	 * @pre (none)
	 * @post A boolean value is generated
	 * @return True if the storage unit may be added. Otherwise, false
	 */
	public boolean ableToAddStorageUnit(StorageUnit storageUnit)
	{
		if(storageUnit == null)
			return false;
		
		if(storageUnitNameInUse(storageUnit.getName()))
			return false;
		
		return true;
		
	}

	/**
	 * Checks if a storage unit name is taken
	 * @param unitName The name to check
	 * @pre (none)
	 * @post A boolean value is generated
	 * @return True if the name is taken. Otherwise, false
	 */
	private boolean storageUnitNameInUse(String unitName)
	{
		for(StorageUnit unit : storageUnits)
		{
			if(unit.getName().equals(unitName))
			{
				return true;
			}
		}
		return false;
	}
	
	private long lastGeneratedBarCode;
	
	/**
	 * Returns a unique bar code
	 * @return A bar code which is unique
	 */
	protected long getUniqueBarCode()
	{
		return lastGeneratedBarCode++;
	}

	/**
	 * Gets the persistence object for saving and loading data to this object
	 * @return An object capable of saving and loading this object's state
	 */
	public IPersistence getPersistence()
	{
		return this.persistence;
	}

}
