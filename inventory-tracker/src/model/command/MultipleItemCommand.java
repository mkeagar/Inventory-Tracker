
package model.command;

import java.util.List;

import model.IItem;
import model.IProductContainer;

public abstract class MultipleItemCommand extends Command
{
	protected List<IItem> items;

	@Override
	public abstract void execute();

	public MultipleItemCommand(IProductContainer target, List<IItem> items)
	{
		super(target);
		this.items = items;
	}

	@Override
	public abstract void undo();

	public List<IItem> getItems()
	{
		return items;
	}

}
