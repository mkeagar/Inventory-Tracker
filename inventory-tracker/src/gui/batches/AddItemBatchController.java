
package gui.batches;

import gui.common.Controller;
import gui.common.IView;
import gui.inventory.ProductContainerData;
import gui.item.ItemData;
import gui.product.ProductData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import model.BarcodeLabelPage;
import model.IItem;
import model.IProduct;
import model.IProductContainer;
import model.Inventory;
import model.Item;
import model.ItemBarcode;
import model.command.AddItemCommand;
import model.command.Command;
import model.command.MultipleItemCommand;
import observer.DataUpdater;

import com.itextpdf.text.DocumentException;
import common.util.DateUtils;

/**
 * Controller class for the add item batch view.
 */
public class AddItemBatchController extends Controller implements
		IAddItemBatchController
{
	private final ProductContainerData target;
	private boolean useBarcodeScanner;
	private boolean validDate;
	private Date entryDate;
	private int count;
	private boolean validCount;
	private String barcode;
	private final Map<ProductData, List<ItemData>> displayItems;
	private final List<ProductData> displayProducts;
	private List<IItem> items;
	private final Stack<Command> executedActions;
	private final Stack<Command> undoneActions;

	public static IProduct product;

	/**
	 * Constructor.
	 * 
	 * @param view Reference to the add item batch view.
	 * @param target Reference to the storage unit to which items are being
	 *            added.
	 */
	public AddItemBatchController(IView view, ProductContainerData target)
	{
		super(view);
		initializeValues();
		this.target = target;
		useBarcodeScanner = true;
		validDate = true;
		displayItems = new HashMap<ProductData, List<ItemData>>();
		displayProducts = new ArrayList<ProductData>();
		items = new ArrayList<IItem>();
		product = null;
		executedActions = new Stack<Command>();
		undoneActions = new Stack<Command>();

		construct();
	}

	/**
	 * This method is called when the user clicks the "Add Item" button in the
	 * add item batch view.
	 */
	@Override
	public void addItem()
	{
		if(!validCount)
		{
			getView().displayErrorMessage("Invalid count");
			return;
		}

		if(barcode.isEmpty())
		{
			getView().displayErrorMessage("Empty barcode");
			return;
		}

		IProduct addingProduct = findProductInInventory();
		boolean productExistsInSystem = false;

		if(addingProduct != null)
			productExistsInSystem = true;

		ProductData addingProductData = findProductDataInView();
		boolean productDataExistsInDialog = false;

		if(addingProductData != null)
			productDataExistsInDialog = true;

		if(productExistsInSystem)
		{
			if(productDataExistsInDialog)
				updateProductDataCount(addingProductData);
			else
			{
				addingProductData =
						DataUpdater.createProductData(addingProduct);
				addingProductData.setCount(count + "");
				displayProducts.add(addingProductData);
				AddItemBatchController.product = addingProduct;
			}
		}
		else
		{
			if(productDataExistsInDialog)
			{
				updateProductDataCount(addingProductData);
				addingProduct = (IProduct) addingProductData.getTag();
			}
			else
			{
				AddItemBatchController.product = null;
				getView().displayAddProductView();

				if(AddItemBatchController.product == null)
					return;

				addingProduct = AddItemBatchController.product;
				addingProductData =
						DataUpdater.createProductData(addingProduct);
				addingProductData.setCount(count + "");
				displayProducts.add(addingProductData);
				AddItemBatchController.product = addingProduct;
			}
		}

		addItems(addingProduct, addingProductData);

		Command command =
				new AddItemCommand((IProductContainer) target.getTag(), items,
						productExistsInSystem);
		((AddItemCommand) command).execute();

		executedActions.push(command);

		items = new ArrayList<IItem>();

		initializeValues();
		loadValues();
		ProductData[] temp = new ProductData[displayProducts.size()];
		getView().setProducts(displayProducts.toArray(temp));
		enableComponents();
	}

	private void addItemData(ProductData productData, ItemData itemData)
	{
		if(displayItems.get(productData) == null)
		{
			List<ItemData> tempList = new ArrayList<ItemData>();
			tempList.add(itemData);
			displayItems.put(productData, tempList);
		}
		else
			displayItems.get(productData).add(itemData);
	}

	private void addItems(IProduct product, ProductData productData)
	{
		for(int i = 0; i < count; i++)
		{
			Calendar expiration = constructExpiration(product);
			IItem addingItem = createItem(product, expiration);

			if(product.getShelfLife() == 0)
				addingItem.setExpirationDate(null);

			items.add(addingItem);
			ItemData addingItemData = DataUpdater.createItemData(addingItem);

			addItemData(productData, addingItemData);
		}
	}

	/**
	 * This method is called when the "Product Barcode" field in the add item
	 * batch view is changed by the user.
	 */
	@Override
	public void barcodeChanged()
	{
		barcode = getView().getBarcode();
		enableComponents();
	}

	private Calendar constructExpiration(IProduct addingProduct)
	{
		Calendar expiration = Calendar.getInstance();
		expiration.set(Calendar.MONTH, entryDate.getMonth());
		expiration.set(Calendar.DAY_OF_MONTH, entryDate.getDate());
		expiration.set(Calendar.YEAR, entryDate.getYear() + 1900);
		expiration.add(Calendar.MONTH, addingProduct.getShelfLife());

		return expiration;
	}

	/**
	 * This method is called when the "Count" field in the add item batch view
	 * is changed by the user.
	 */
	@Override
	public void countChanged()
	{
		try
		{
			count = Integer.parseInt(getView().getCount());
			validCount = true;

		}
		catch(NumberFormatException nfe)
		{
			validCount = false;
		}
		finally
		{
			enableComponents();
		}
	}

	private IItem createItem(IProduct addingProduct, Calendar expiration)
	{
		return new Item(addingProduct, new ItemBarcode(Inventory.getInstance()
				.getUniqueBarCode() + ""), entryDate,
				DateUtils.removeTimeFromDate(expiration.getTime()));
	}

	/**
	 * This method is called when the user clicks the "Done" button in the add
	 * item batch view.
	 */
	@Override
	public void done()
	{
		if(displayProducts.isEmpty() || displayItems.isEmpty())
		{
			getView().close();
			return;
		}

		printBarcodes();

		getView().close();
	}

	private void printBarcodes()
	{
		List<IItem> items = new ArrayList<IItem>();

		for(Command command: executedActions)
			for(IItem item: ((MultipleItemCommand) command).getItems())
				items.add(item);

		BarcodeLabelPage printer = new BarcodeLabelPage(items);
		try
		{
			printer.createPDF();
		}
		catch(DocumentException e)
		{
			getView().displayErrorMessage(
					"There was a barcode label creation error");
		}
		catch(IOException e)
		{
			getView().displayErrorMessage(
					"There was a barcode label creation error");
		}
	}

	/**
	 * Sets the enable/disable state of all components in the controller's view.
	 * A component should be enabled only if the user is currently allowed to
	 * interact with that component.
	 * 
	 * {@pre None}
	 * 
	 * {@post The enable/disable state of all components in the controller's
	 * view have been set appropriately.}
	 */
	@Override
	protected void enableComponents()
	{
		if(validCount && !barcode.isEmpty() && validDate)
			getView().enableItemAction(true);
		else
			getView().enableItemAction(false);

		getView().enableUndo(!executedActions.isEmpty());
		getView().enableRedo(!undoneActions.isEmpty());

	}

	/**
	 * This method is called when the "Entry Date" field in the add item batch
	 * view is changed by the user.
	 */
	@Override
	public void entryDateChanged()
	{
		Date requestedDate = getView().getEntryDate();

		if(requestedDate == null)
			validDate = false;
		else
		{
			boolean valid = valiDate(requestedDate);

			if(valid)
			{
				entryDate = requestedDate;
				validDate = true;
			}
			else
				validDate = false;
		}

		enableComponents();
	}

	private ProductData findProductDataInView()
	{
		for(ProductData pd: displayProducts)
			if(pd.getBarcode().equals(barcode))
				return pd;

		return null;
	}

	private IProduct findProductInInventory()
	{
		for(IProduct ip: Inventory.getInstance().getAllProducts())
			if(ip.getBarcode().getNumber().equals(barcode))
				return ip;

		return null;
	}

	/**
	 * Returns a reference to the view for this controller.
	 */
	@Override
	protected IAddItemBatchView getView()
	{
		return (IAddItemBatchView) super.getView();
	}

	private void initializeValues()
	{
		count = 1;
		entryDate = DateUtils.removeTimeFromDate(DateUtils.currentDate());
		validCount = true;
		barcode = "";
	}

	/**
	 * Loads data into the controller's view.
	 * 
	 * {@pre None}
	 * 
	 * {@post The controller has loaded data into its view}
	 */
	@Override
	protected void loadValues()
	{
		getView().setBarcode(barcode);
		getView().setCount(count + "");
		getView().setEntryDate(entryDate);
		getView().setUseScanner(useBarcodeScanner);
	}

	/**
	 * This method is called when the user clicks the "Redo" button in the add
	 * item batch view.
	 */
	@Override
	public void redo()
	{
		return;
		// don't have to implement yet
	}

	/**
	 * This method is called when the selected product changes in the add item
	 * batch view.
	 */
	@Override
	public void selectedProductChanged()
	{
		ItemData[] temp =
				new ItemData[displayItems.get(getView().getSelectedProduct())
						.size()];
		getView().setItems(
				displayItems.get(getView().getSelectedProduct()).toArray(temp));

		enableComponents();
	}

	/**
	 * This method is called when the user clicks the "Undo" button in the add
	 * item batch view.
	 */
	@Override
	public void undo()
	{
		return;
		// don't have to implement yet
	}

	private void updateProductDataCount(ProductData addingProductData)
	{
		addingProductData.setCount(Integer.parseInt(addingProductData
				.getCount()) + count + "");
	}

	/**
	 * This method is called when the "Use Barcode Scanner" setting in the add
	 * item batch view is changed by the user.
	 */
	@Override
	public void useScannerChanged()
	{
		useBarcodeScanner = getView().getUseScanner();
		enableComponents();
	}

	private boolean valiDate(Date date)
	{
		date = DateUtils.removeTimeFromDate(date);
		boolean response = false;

		if((date != null)
				&& (!date.before(DateUtils.removeTimeFromDate(DateUtils
						.earliestDate())))
				&& (!date.after(DateUtils.removeTimeFromDate(DateUtils
						.currentDate()))))
		{
			response = true;
		}

		return response;
	}
}
