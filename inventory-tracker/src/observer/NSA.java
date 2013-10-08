package observer;

import gui.inventory.IInventoryController;

import java.util.Observable;
import java.util.Observer;

import model.Inventory;

/**
 * Notification System Arbitrator
 * Receives update notifications from the model and passes them on to the controller
 * @author Brian
 *
 */
public class NSA implements Observer
{
	/**
	 * Reference to the inventory we're tracking
	 */
	private Inventory inventory;
	/**
	 * Reference to the controller we're reporting to
	 */
	private IInventoryController controller;

	/**
	 * Receiving function for then an observable in the system changes
	 * @param o The Observable that changed
	 * @param arg Type object for the interface, but is expected to be enumeration type
	 * UpdateType. Described what the change was to the Observable
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		// TODO Auto-generated method stub

	}
	

}
