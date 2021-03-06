
package model;

/**
 * Describes the possible actions that can occur to an object in the system.
 * Used for notifying NSA of changes
 * @author Brian
 * 
 */
public enum UpdateType
{
	ADDED, UPDATED, REMOVED, TEMP_REMOVED, PERMANENTLY_REMOVED
}
