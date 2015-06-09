package com.deetysoft.util;

/**
 * Interface for a class that implements equivalence.
 */
public interface Equivalence {
	/**
	 * Returns true if the given object is equivalent to this object.
	 * @param	obj		an object for comparison
	 * @return	true		if the given object is equivalent to this object
	 */
	public boolean equivalent(Object obj);
}
