
package gui.batches;

import gui.common.IView;
import gui.item.ItemData;
import gui.product.ProductData;

import java.util.Date;

/**
 * View interface for add item batch view.
 */
public interface IAddItemBatchView extends IView
{

	/**
	 * Closes the add item batch view.
	 */
	void close();

	/**
	 * Displays the add item view.
	 */
	void displayAddProductView();

	/**
	 * Sets the enable/disable state of the "Add Item" button.
	 * 
	 * @param value New enable/disable state
	 */
	void enableItemAction(boolean value);

	/**
	 * Sets the enable/disable state of the "Redo" button.
	 * 
	 * @param value New enable/disable state
	 */
	void enableRedo(boolean value);

	/**
	 * Sets the enable/disable state of the "Undo" button.
	 * 
	 * @param value New enable/disable state
	 */
	void enableUndo(boolean value);

	/**
	 * Returns the value of the "Product Barcode" field.
	 */
	String getBarcode();

	/**
	 * Returns the value of the "Count" field.
	 */
	String getCount();

	/**
	 * Returns the value of the "Entry Date" field, or null if the field's value
	 * is invalid.
	 */
	Date getEntryDate();

	/**
	 * Returns the currently selected item in the "Items" table, or null if no
	 * item is selected.
	 */
	ItemData getSelectedItem();

	/**
	 * Returns the currently selected product in the "Products" table, or null
	 * if no product is selected.
	 */
	ProductData getSelectedProduct();

	/**
	 * Returns the "Use Barcode Scanner" setting.
	 */
	boolean getUseScanner();

	/**
	 * Gives the keyboard focus to the "Item Barcode" field.
	 */
	void giveBarcodeFocus();

	/**
	 * Selects the specified item in the "Items" table, or selects nothing if
	 * item is null.
	 * 
	 * @param item The item to be selected. This must be one of the items
	 *            previously passed to setItems, or null.
	 */
	void selectItem(ItemData item);

	/**
	 * Selects the specified product in the "Products" table, or selects nothing
	 * if product is null.
	 * 
	 * @param product The product to be selected. This must be one of the
	 *            products previously passed to setProducts, or null.
	 */
	void selectProduct(ProductData product);

	/**
	 * Sets the value of the "Product Barcode" field.
	 * 
	 * @param barcode New "Product Barcode" value
	 */
	void setBarcode(String barcode);

	/**
	 * Sets the value of the "Count" field.
	 * 
	 * @param value New "Count" value
	 */
	void setCount(String value);

	/**
	 * Sets the value of the "Entry Date" field.
	 * 
	 * @param value New "Entry Date" value
	 */
	void setEntryDate(Date value);

	/**
	 * Sets the items displayed in the "Items" table.
	 * 
	 * @param items Array of items to display
	 */
	void setItems(ItemData[] items);

	/**
	 * Sets the products displayed in the "Products" table.
	 * 
	 * @param products Array of products to display
	 */
	void setProducts(ProductData[] products);

	/**
	 * Sets the value of the "Use Barcode Scanner" setting.
	 * 
	 * @param value New "Use Barcode Scanner" value
	 */
	void setUseScanner(boolean value);

}
