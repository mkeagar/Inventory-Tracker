
package gui.batches;

import gui.common.Controller;
import gui.common.IView;
import gui.inventory.ProductContainerData;
import gui.item.ItemData;
import gui.product.ProductData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.BarcodeLabelPage;
import model.CountAmount;
import model.CountThreeMonthSupply;
import model.IItem;
import model.IProduct;
import model.Inventory;
import model.Item;
import model.ItemBarcode;
import model.NonCountAmount;
import model.Product;
import model.ProductContainer;
import model.ProductGroup;
import model.StorageUnit;

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
	private Date entryDate;
	private int count;
	private boolean validCount;
	private String barcode;
	private Map<ProductData, List<ItemData>> displayItems;
	private List<ProductData> displayProducts;
	private List<IItem> items;
	private List<IProduct> products;

	/**
	 * @param displayItems the displayItems to set
	 */
	public void setDisplayItems(Map<ProductData, List<ItemData>> displayItems)
	{
		this.displayItems = displayItems;
	}

	/**
	 * @param displayProducts the displayProducts to set
	 */
	public void setDisplayProducts(List<ProductData> displayProducts)
	{
		this.displayProducts = displayProducts;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<IItem> items)
	{
		this.items = items;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(List<IProduct> products)
	{
		this.products = products;
	}

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
		this.target = target;
		count = 1;
		entryDate = DateUtils.currentDate();
		useBarcodeScanner = true;
		validCount = true;
		barcode = "";
		displayItems = new HashMap<ProductData, List<ItemData>>();
		displayProducts = new ArrayList<ProductData>();
		items = new ArrayList<IItem>();
		products = new ArrayList<IProduct>();
		product = null;

		construct();
	}

	/**
	 * @return the displayItems
	 */
	public Map<ProductData, List<ItemData>> getDisplayItems()
	{
		return displayItems;
	}

	/**
	 * @return the displayProducts
	 */
	public List<ProductData> getDisplayProducts()
	{
		return displayProducts;
	}

	/**
	 * @return the items
	 */
	public List<IItem> getItems()
	{
		return items;
	}

	/**
	 * @return the products
	 */
	public List<IProduct> getProducts()
	{
		return products;
	}

	private IProduct createProduct()
	{
		return new Product(AddItemBatchController.product.getCreationDate(),
				AddItemBatchController.product.getDescription()
						.getDescription(),
				AddItemBatchController.product.getBarcode(),
				AddItemBatchController.product.getSize(),
				AddItemBatchController.product.getShelfLife(),
				(CountThreeMonthSupply) AddItemBatchController.product
						.getThreeMonthSupply());
	}

	private ProductData createProductData(IProduct product)
	{
		ProductData result = new ProductData();
		result.setBarcode(product.getBarcode().getNumber());
		result.setCount(count + "");
		result.setDescription(product.getDescription().getDescription());
		result.setShelfLife(product.getShelfLife() + "");

		if(product.getSize() instanceof CountAmount)
			result.setSize(((CountAmount) product.getSize()).getUnitType() + "");
		else
			result.setSize(((NonCountAmount) product.getSize()).getUnitType()
					+ "");

		if(product.getThreeMonthSupply() instanceof CountAmount)
			result.setSupply(((CountAmount) product.getThreeMonthSupply())
					.getAmount() + "");
		else
			result.setSupply(((NonCountAmount) product.getThreeMonthSupply())
					.getAmount() + "");

		result.setTag(null);

		return result;
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
			_view.displayErrorMessage("Invalid count");
			return;
		}

		if(entryDate.after(DateUtils.currentDate()))
		{
			_view.displayErrorMessage("Invalid entry date");
			return;
		}

		boolean found = false;

		for(IProduct ip: Inventory.getInstance().getAllProducts())
			if(ip.getBarcode().getNumber().equals(barcode))
				found = true;

		ProductData adding = null;
		IProduct product = null;

		if(!found)
		{
			getView().displayAddProductView();
			product = createProduct();

			adding = createProductData(product);

			adding.setTag(product);

			products.add(product);
			displayProducts.add(adding);
		}
		else
		{
			for(ProductData pd: displayProducts)
				if(pd.getBarcode().equals(barcode))
					adding = pd;

			adding.setCount(adding.getCount() + count);
			products.add(product);
		}

		for(int i = 0; i < count; i++)
		{
			ItemData temp = new ItemData();
			temp.setBarcode(barcode);
			temp.setEntryDate(entryDate);
			temp.setExpirationDate(entryDate); // FIX THIS
			temp.setProductGroup("");

			if(target.getTag() instanceof ProductGroup)
				temp.setProductGroup(((ProductGroup) target.getTag()).getName());

			temp.setStorageUnit(((StorageUnit) target.getTag()).getName());
			temp.setTag(null);
			if(displayItems.get(adding) == null)
			{
				List<ItemData> tempList = new ArrayList<ItemData>();
				tempList.add(temp);
				displayItems.put(adding, tempList);
			}
			else
			{
				displayItems.get(adding).add(temp);
			}

			items.add(new Item(product, new ItemBarcode(Inventory.getInstance()
					.getUniqueBarCode() + ""), entryDate, null));
		}

		count = 1;
		entryDate = DateUtils.currentDate();
		useBarcodeScanner = true;
		validCount = true;
		barcode = "";
		loadValues();
		ProductData[] temp = new ProductData[displayProducts.size()];
		getView().setProducts(displayProducts.toArray(temp));
		enableComponents();
	}

	/**
	 * This method is called when the "Product Barcode" field in the add item
	 * batch view is changed by the user.
	 */
	@Override
	public void barcodeChanged()
	{
		barcode = getView().getBarcode();

		if(useBarcodeScanner)
			addItem();

		enableComponents();
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

	/**
	 * This method is called when the user clicks the "Done" button in the add
	 * item batch view.
	 */
	@Override
	public void done()
	{
		if(products.isEmpty() || items.isEmpty())
			return;

		for(IProduct ip: products)
			if(((ProductContainer) target.getTag()).ableToAddProduct(ip))
				((ProductContainer) target.getTag()).addProduct(ip);

		for(IItem ii: items)
			if(((ProductContainer) target.getTag()).ableToAddItem(ii))
				((ProductContainer) target.getTag()).addItem(ii);

		BarcodeLabelPage printer = new BarcodeLabelPage(items);
		try
		{
			printer.createPDF();
		}
		catch(DocumentException | IOException e)
		{
			_view.displayErrorMessage("There was a barcode label creation error");
		}

		getView().close();
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
		if(validCount && !barcode.isEmpty()
				&& !entryDate.after(DateUtils.currentDate()))
			getView().enableItemAction(true);
		else
			getView().enableItemAction(false);

		getView().enableUndo(false);
		getView().enableRedo(false);

	}

	/**
	 * This method is called when the "Entry Date" field in the add item batch
	 * view is changed by the user.
	 */
	@Override
	public void entryDateChanged()
	{
		entryDate = getView().getEntryDate();
		enableComponents();
	}

	/**
	 * Returns a reference to the view for this controller.
	 */
	@Override
	protected IAddItemBatchView getView()
	{
		return (IAddItemBatchView) super.getView();
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
		ItemData[] temp = new ItemData[displayItems.size()];
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
}
