
package gui.batches;

import gui.common.Controller;
import gui.common.IView;
import gui.inventory.ProductContainerData;
import gui.item.ItemData;
import gui.product.ProductData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.IInventory;
import model.IItem;
import model.IProductContainer;
import model.Inventory;

/**
 * Controller class for the transfer item batch view.
 */
public class TransferItemBatchController extends Controller implements
		ITransferItemBatchController
{
	private String barcode;
	private boolean useBarcodeScanner;
	private final Map<ProductData, List<ItemData>> displayItems;
	private final IInventory inventory = Inventory.getInstance();
	private IItem item = null;
	private IProductContainer container = null;

	/**
	 * Constructor.
	 * 
	 * @param view Reference to the transfer item batch view.
	 * @param target Reference to the storage unit to which items are being
	 *            transferred.
	 */
	public TransferItemBatchController(IView view, ProductContainerData target)
	{
		super(view);
		this.barcode = "";
		this.useBarcodeScanner = true;
		this.displayItems = new HashMap<ProductData, List<ItemData>>();
		this.container = (IProductContainer) target.getTag();

		construct();
	}

	/**
	 * This method is called when the "Item Barcode" field in the transfer item
	 * batch view is changed by the user.
	 */
	@Override
	public void barcodeChanged()
	{
		this.barcode = getView().getBarcode();
		this.enableComponents();
	}

	/**
	 * This method is called when the user clicks the "Done" button in the
	 * transfer item batch view.
	 */
	@Override
	public void done()
	{
		this.getView().close();
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
		this.getView().enableRedo(false);
		this.getView().enableUndo(false);
		this.getView().enableItemAction(!barcode.isEmpty());
	}

	/**
	 * Returns a reference to the view for this controller.
	 */
	@Override
	protected ITransferItemBatchView getView()
	{
		return (ITransferItemBatchView) super.getView();
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
		this.getView().setBarcode(barcode);
		this.getView().setUseScanner(useBarcodeScanner);
	}

	/**
	 * This method is called when the user clicks the "Redo" button in the
	 * transfer item batch view.
	 */
	@Override
	public void redo()
	{
		// not implemented yet
		return;
	}

	/**
	 * This method is called when the selected product changes in the transfer
	 * item batch view.
	 */
	@Override
	public void selectedProductChanged()
	{
		ItemData[] temp =
				new ItemData[displayItems.get(getView().getSelectedProduct())
						.size()];
		this.getView().setItems(
				this.displayItems.get(getView().getSelectedProduct()).toArray(
						temp));

		this.enableComponents();
	}

	/**
	 * This method is called when the user clicks the "Transfer Item" button in
	 * the transfer item batch view.
	 */
	@Override
	public void transferItem()
	{
		this.item = this.inventory.getItem(this.barcode);

		if(this.item == null)
		{
			this.getView().displayErrorMessage(
					"The specified item does not exist.");
		}
		else
		{

		}

	}

	/**
	 * This method is called when the user clicks the "Undo" button in the
	 * transfer item batch view.
	 */
	@Override
	public void undo()
	{
		// not implemented yet
		return;
	}

	/**
	 * This method is called when the "Use Barcode Scanner" setting in the
	 * transfer item batch view is changed by the user.
	 */
	@Override
	public void useScannerChanged()
	{
		this.useBarcodeScanner = getView().getUseScanner();
		this.enableComponents();
	}
}
