
package model.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import model.CountAmount;
import model.CountThreeMonthSupply;
import model.IItem;
import model.IProduct;
import model.IProductGroup;
import model.IStorageUnit;
import model.Inventory;
import model.NonCountAmount;
import model.ProductGroupSupply;
import model.ProductSupply;
import model.UnitType;
import model.exception.InvalidUnitTypeException;

import common.UnitUtils;

public class NMonthSupplyVisitor implements IVisitor
{
	private List<ProductGroupSupply> groupSupplies;

	private List<ProductSupply> productSupplies;
	private int months;

	public NMonthSupplyVisitor(int months)
	{
		groupSupplies = new ArrayList<ProductGroupSupply>();
		productSupplies = new ArrayList<ProductSupply>();

		this.months = months;
	}

	public List<ProductGroupSupply> getGroupSupplies()
	{
		return groupSupplies;
	}

	public List<ProductSupply> getProductSupplies()
	{
		return productSupplies;
	}

	private boolean productAlreadyVisited(IProduct product)
	{
		for(ProductSupply supply: productSupplies)
		{
			if(supply.getProduct() == product)
				return true;
		}
		return false;
	}

	@Override
	public void visitItem(IItem item)
	{}

	@Override
	public void visitProduct(IProduct product)
	{
		if(!productAlreadyVisited(product))
		{
			SortedSet<IItem> items =
					Inventory.getInstance().getAllItems(product);

			float nMonthSupply =
					product.getThreeMonthSupply().getAmount() * (months / 3.0f);

			if(nMonthSupply > items.size())
			{
				ProductSupply supply = new ProductSupply(product);
				supply.setSupply(new CountThreeMonthSupply(items.size()));
				productSupplies.add(supply);
			}
		}
	}

	@Override
	public void visitProductGroup(IProductGroup group)
	{
		SupplyTallyVisitor visitor = new SupplyTallyVisitor();
		group.traverse(visitor);
		UnitType unitType = group.getThreeMonthSupply().getUnitType();
		if(unitType == UnitType.COUNT)
		{
			CountAmount count = (CountAmount) group.getThreeMonthSupply();
			float nMonthSupply = count.getAmount() * (months / 3.0f);
			if(nMonthSupply > visitor.getTotalCount())
			{
				ProductGroupSupply supply = new ProductGroupSupply(group);
				supply.setSupply(count);
				groupSupplies.add(supply);
			}
		}
		else
		{
			NonCountAmount amount =
					(NonCountAmount) group.getThreeMonthSupply();
			float nMonthSupply = amount.getAmount() * (months / 3.0f);
			try
			{
				if((UnitUtils.UnitTypeIsWeight(unitType) && nMonthSupply > visitor
						.getWeight(unitType))
						|| (UnitUtils.UnitTypeIsVolume(unitType) && nMonthSupply > visitor
								.getVolume(unitType)))
				{
					ProductGroupSupply supply = new ProductGroupSupply(group);
					supply.setSupply(amount);
					groupSupplies.add(supply);
				}
			}
			catch(InvalidUnitTypeException e)
			{}

		}
	}

	@Override
	public void visitStorageUnit(IStorageUnit unit)
	{}

}
