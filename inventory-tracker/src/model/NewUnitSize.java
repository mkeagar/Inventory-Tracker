/**
 * 
 */

package model;

/**
 * Class to represent a unit size where the unit type is not "count"
 * @author Michael
 * 
 */
public class NewUnitSize extends NonCountAmount
{
	// Variables
	private float size;

	/**
	 * @precondition size must be > 0.0f
	 * @precondition unitType must != UnitType.COUNT
	 * @postcondition size == passed in size
	 * @postcondition unitType == passed in UnitType
	 * @param size
	 * @param unitType
	 */
	public NewUnitSize(float size, UnitType unitType)
	{
		super(size, unitType);
		this.size = size;
	}

	/**
	 * @precondition size must be > 0
	 * @param size the size to test if we can set
	 */
	public boolean ableToSetSize(float size)
	{
		return true;
	}

	/**
	 * @return the size
	 */
	public float getSize()
	{
		return size;
	}

	/**
	 * @precondition size must be > 0.0f
	 * @postcondition size == passed in size
	 * @param size the size to set
	 */
	public void setSize(float size)
	{
		this.size = size;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "NewUnitSize [size=" + size + ", unitType=" + unitType + "]";
	}

}