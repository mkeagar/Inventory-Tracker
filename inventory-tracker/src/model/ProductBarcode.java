/**
 * 
 */

package model;

/**
 * @author Michael
 * 
 */
public class ProductBarcode extends Barcode
{
	private static final long serialVersionUID = -1451440044816950946L;

	/**
	 * @pre number must not be null or empty
	 * @param number string of the ProductBarcode number to validate
	 * @return true if valid number, ie not empty
	 */
	public static boolean isValid(String number)
	{
		boolean response = false;

		if(number != null && !number.isEmpty())
		{
			response = true;
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.Barcode(java.lang.String)
	 */
	public ProductBarcode(String number)
	{
		super(number);
	}

	/**
	 * @param number the number to attempt to set
	 * @return whether or not the number can be set
	 */
	@Override
	public boolean ableToSetNumber(String number)
	{
		boolean response = false;

		if(number != null)
		{
			response = ProductBarcode.isValid(number);
		}

		return response;
	}

}
