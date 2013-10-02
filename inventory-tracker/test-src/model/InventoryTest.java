package model;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import model.exception.InvalidNameException;

import org.junit.Test;

public class InventoryTest
{
	
	private StorageUnit unit1;
	private StorageUnit unit2;
	private Product prod;

	@Test
	public void constructorTest()
	{
		assertTrue(Inventory.getInstance() != null);
		Inventory.getInstance().removeAllStorageUnits();
		assertEquals(0, Inventory.getInstance().getAllStorageUnits().size());
		assertEquals(0, Inventory.getInstance().getAllProducts().size());
		assertEquals(0, Inventory.getInstance().getItemExpirations().size());
		assertEquals(0, Inventory.getInstance().getNMonthSupplyGroupMap().size());
		assertEquals(0, Inventory.getInstance().getNMonthSupplyMap().size());
		assertEquals(0, Inventory.getInstance().getRemovedItems().size());
	}
	
	@Test
	public void storageUnitTest() throws Exception
	{
		Inventory.getInstance().removeAllStorageUnits();
		assertEquals(0, Inventory.getInstance().getAllStorageUnits().size());
		populateInventory();
		Set<StorageUnit> units = Inventory.getInstance().getAllStorageUnits(); 
		assertEquals(2, units.size());
		assertTrue(units.contains(unit1));
		assertTrue(units.contains(unit2));
	}
	
	@Test
	public void productListTest() throws Exception
	{
		populateInventory();
		Set<Product> products = Inventory.getInstance().getAllProducts();
		assertEquals(1, products.size());
		assertTrue(products.contains(prod));
	}

	private void populateInventory() throws InvalidNameException
	{
		Inventory.getInstance().removeAllStorageUnits();
		unit1 = new StorageUnit("unit1");
		ProductGroup pg1 = new ProductGroup("pg1", unit1, new ThreeMonthSupply(1.0f, UnitType.CHEVROLET));
		HashSet<ProductContainer> set = new HashSet<ProductContainer>();
		set.add(pg1);
		prod = new Product(new Date(), "asdf", new Barcode("1"), new CountUnitSize(1), 1, new CountThreeMonthSupply(1), set);
		pg1.addProduct(prod);
		unit1.addProductGroup(pg1);
		Inventory.getInstance().addStorageUnit(unit1);
		unit2 = new StorageUnit("unit2");
		Inventory.getInstance().addStorageUnit(unit2);
	}
	
	@Test
	public void removeTest() throws Exception
	{
		populateInventory();
		Set<StorageUnit> units = Inventory.getInstance().getAllStorageUnits(); 
		assertEquals(2, units.size());
		Inventory.getInstance().removeStorageUnit(unit1);
		units = Inventory.getInstance().getAllStorageUnits();
		assertEquals(1, units.size());
		Inventory.getInstance().removeAllStorageUnits();
		units = Inventory.getInstance().getAllStorageUnits();
		assertEquals(0, units.size());
	}
	
	@Test
	public void persistenceTest() throws Exception
	{
		populateInventory();
		Inventory.getInstance().getPersistence().saveData();
		Inventory.getInstance().removeAllStorageUnits();
		Inventory.getInstance().getPersistence().loadData();
		assertEquals(2, Inventory.getInstance().getAllStorageUnits().size());
		assertEquals(1, Inventory.getInstance().getAllProducts().size());
		assertTrue(Inventory.getInstance().getAllProducts().iterator().next().getDescription().equals("asdf"));
	}
	
	@Test
	public void reportDeletedItemTest() throws Exception
	{
		populateInventory();
		assertEquals(0, Inventory.getInstance().getRemovedItems().size());
		Inventory.getInstance().reportRemovedItem(new Item(prod, new Barcode("1"), new Date(), new Date(), new Date(), unit1));
		assertEquals(1, Inventory.getInstance().getRemovedItems().size());
	}

}
