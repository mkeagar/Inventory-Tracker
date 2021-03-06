
package gui.batches;

import gui.common.Controller;
import gui.common.IView;
import gui.item.ItemData;
import gui.product.ProductData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import model.IItem;
import model.Inventory;
import model.command.Command;
import model.command.RemoveItemCommand;

/**
 * Controller class for the remove item batch view.
 */
public class RemoveItemBatchController extends Controller implements
		IRemoveItemBatchController
{
	private String barcode;
	private boolean useBarcodeScanner;
	private final Map<ProductData, List<ItemData>> displayItems;
	private final List<ProductData> displayProducts;
	private final List<IItem> items;
	private Stack<Command> done;
	private Stack<Command> undone;

	/**
	 * Constructor.
	 * 
	 * @param view Reference to the remove item batch view.
	 */
	public RemoveItemBatchController(IView view)
	{
		super(view);
		barcode = "";
		useBarcodeScanner = true;
		displayItems = new HashMap<ProductData, List<ItemData>>();
		displayProducts = new ArrayList<ProductData>();
		items = new ArrayList<IItem>();

		this.done = new Stack<Command>();
		this.undone = new Stack<Command>();

		construct();
	}

	/**
	 * This method is called when the "Item Barcode" field is changed in the
	 * remove item batch view by the user.
	 */
	@Override
	public void barcodeChanged()
	{
		barcode = getView().getBarcode();

		if(this.useBarcodeScanner)
		{
			try
			{
				Thread.sleep(800);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			this.removeItem();
		}

		enableComponents();
	}

	/**
	 * This method is called when the user clicks the "Done" button in the
	 * remove item batch view.
	 */
	@Override
	public void done()
	{
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
		getView().enableRedo(!this.undone.isEmpty());
		getView().enableUndo(!this.done.isEmpty());
		getView().enableItemAction(!barcode.isEmpty());
	}

	/**
	 * Returns a reference to the view for this controller.
	 */
	@Override
	protected IRemoveItemBatchView getView()
	{
		return (IRemoveItemBatchView) super.getView();
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
		getView().setUseScanner(useBarcodeScanner);
		getView().setBarcode(barcode);
	}

	/**
	 * This method is called when the user clicks the "Redo" button in the
	 * remove item batch view.
	 */
	@Override
	public void redo()
	{
		RemoveItemCommand command = (RemoveItemCommand) this.undone.pop();
		command.execute();
		this.done.push(command);
		this.updateCurrentView();
		return;
	}

	/**
	 * This method is called when the user clicks the "Remove Item" button in
	 * the remove item batch view.
	 */
	@Override
	public void removeItem()
	{
		if(barcode.isEmpty())
		{
			getView().displayErrorMessage("Empty barcode");
			return;
		}

		IItem removingItem = Inventory.getInstance().getItem(barcode);

		if(removingItem == null)
		{
			getView().displayErrorMessage("No item with that barcode");
			// barcode = "";
			enableComponents();
			loadValues();
			return;
		}

		Command command =
				new RemoveItemCommand(removingItem.getContainer(),
						removingItem, this.displayProducts, this.displayItems);

		command.execute();

		done.push(command);
		this.updateCurrentView();
	}

	/**
	 * This method is called when the selected product changes in the remove
	 * item batch view.
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
	 * This method is called when the user clicks the "Undo" button in the
	 * remove item batch view.
	 */
	@Override
	public void undo()
	{

		RemoveItemCommand command = (RemoveItemCommand) done.pop();
		command.undo();
		undone.push(command);
		this.updateCurrentView();
		if(getView().getSelectedProduct() == null)
			this.getView().setItems(new ItemData[0]);
		else
			this.selectedProductChanged();
		return;
	}

	private void updateCurrentView()
	{
		ProductData[] temp = new ProductData[displayProducts.size()];
		getView().setProducts(displayProducts.toArray(temp));
		barcode = "";
		loadValues();
		enableComponents();
	}

	/**
	 * This method is called when the "Use Barcode Scanner" setting is changed
	 * in the remove item batch view by the user.
	 */
	@Override
	public void useScannerChanged()
	{
		useBarcodeScanner = getView().getUseScanner();
		enableComponents();
	}
}
