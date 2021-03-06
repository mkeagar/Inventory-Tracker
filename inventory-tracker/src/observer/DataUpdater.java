
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
	public static ItemData createItemData(IItem item)
	{
		ItemData itemData = new ItemData();
		updateItemDataFields(item, itemData);
		return itemData;
	}

	public static ProductContainerData createProductContainerData(
			IProductContainer productContainer)
	{
		ProductContainerData pcData = new ProductContainerData();
		updatePcDataFields(productContainer, pcData);
		return pcData;
	}

	public static ProductData createProductData(IProduct product)
	{
		ProductData productData = new ProductData();
		updateProductDataFields(product, productData);
		return productData;
	}

	private static void updateItemDataFields(IItem item, ItemData itemData)
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

	private static void updatePcDataFields(IProductContainer productContainer,
			ProductContainerData pcData)
	{
		pcData.setName(productContainer.getName());
		productContainer.setTag(pcData);
		pcData.setTag(productContainer);
	}

	private static void updateProductDataFields(IProduct product,
			ProductData productData)
	{
		productData.setBarcode(product.getBarcode().toString());
		productData.setDescription(product.getDescription().toString());
		productData.setShelfLife("" + product.getShelfLife());
		productData.setSize(product.getSize().toString());
		productData.setSupply(product.getThreeMonthSupply().toString());
		// product.setTag(productData);
		productData.setTag(product);
	}

	public static void verifyTagData(IItem item)
	{
		if(item.getTag() == null)
			item.setTag(createItemData(item));
		else
			updateItemDataFields(item, (ItemData) item.getTag());
	}

	public static void verifyTagData(IProduct product)
	{
		if(product.getTag() == null)
			product.setTag(createProductData(product));
		else
			updateProductDataFields(product, (ProductData) product.getTag());
	}

	public static void verifyTagData(IProductContainer productContainer)
	{
		if(productContainer.getTag() == null)
			productContainer
					.setTag(createProductContainerData(productContainer));
		else
			updatePcDataFields(productContainer,
					(ProductContainerData) productContainer.getTag());
	}

}
