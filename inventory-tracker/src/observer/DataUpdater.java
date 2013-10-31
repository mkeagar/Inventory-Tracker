package observer;

import gui.inventory.ProductContainerData;
import gui.item.ItemData;
import gui.product.ProductData;
import model.IItem;
import model.IProduct;
import model.IProductContainer;
import model.IProductGroup;

public class DataUpdater
{
	public static void verifyObjTag(Object changedObj)
	{
		if(changedObj instanceof IItem)
		{
			IItem item = (IItem) changedObj;
			ItemData itemData;
			if(item.getTag() != null)
				itemData = (ItemData) item.getTag();
			else
				itemData = new ItemData();
			updateItemData(item, itemData);
		}
		else if(changedObj instanceof IProduct)
		{
			IProduct product = (IProduct) changedObj;
			ProductData productData;
			if(product.getTag() != null)
				productData = (ProductData) product.getTag();
			else
				productData = new ProductData();
			updateProductData(product, productData);
		}
		else if(changedObj instanceof IProductContainer)
		{
			IProductContainer productContainer = (IProductContainer) changedObj;
			ProductContainerData pcData =
					(ProductContainerData) productContainer.getTag();
			if(productContainer.getTag() != null)
				pcData = (ProductContainerData) productContainer.getTag();
			else
				pcData = new ProductContainerData();
			updateProductContainerData(productContainer, pcData);
		}
	}
	
	private static void updateProductContainerData(IProductContainer productContainer,
			ProductContainerData pcData)
	{
		pcData.setName(productContainer.getName());
		productContainer.setTag(pcData);
		pcData.setTag(productContainer);
	}

	private static void updateProductData(IProduct product, ProductData productData)
	{
		productData.setBarcode(product.getBarcode().toString());
		productData.setDescription(product.getDescription().toString());
		productData.setShelfLife("" + product.getShelfLife());
		productData.setSize(product.getSize().toString());
		productData.setSupply(product.getThreeMonthSupply().toString());
		product.setTag(productData);
		productData.setTag(product);
	}
	
	private static void updateItemData(IItem item, ItemData itemData)
	{
		itemData.setBarcode(item.getBarcode().toString());
		itemData.setEntryDate(item.getEntryDate());
		itemData.setExpirationDate(item.getExpirationDate());
		if(item.getContainer() instanceof IProductGroup)
			itemData.setProductGroup(item.getContainer().getName());
		if(item.getContainer() != null)
			itemData.setStorageUnit(item.getContainer().getStorageUnit()
					.getName());
		item.setTag(itemData);
		itemData.setTag(item);
	}
	
}
