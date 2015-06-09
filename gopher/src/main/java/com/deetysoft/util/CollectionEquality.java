package com.deetysoft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * A class that tests for equality between Collections and Maps.
 */
@SuppressWarnings("rawtypes")
public class CollectionEquality {
	/** 
	 * Don't allow instances to be made.
	 */
	private CollectionEquality() {}

	/**
	 * Returns true if the two collections are equal by value, instead of 
	 * equal by reference.
	 *
	 * @param c1 the first Collection to compare
	 * @param c2 the second Collection to compare
	 * @return true if the two collections are equal by value, instead of 
	 * equal by reference.  That is, two collections are equal if and only 
	 * if:<br><br>
	 * 1) <code>c1.size() == c2.size()</code><br><br>
	 * 2) for every object <code>o</code> in <code>c1</code>, 
	 * <code>o.equals(p)</code> for some object <code>p</code> in 
	 * <code>c2</code>.<br><br>
	 * 3) for every object <code>p</code> in <code>c2</code>, 
	 * <code>p.equals(o)</code> for some object <code>o</code> in 
	 * <code>c1</code>. 
	 */
	public static boolean equalityByValue(Collection c1, Collection c2) {

		if (c1 == null) {
			if (c2 == null) {
				return true;
			}
			return false;
		}
		if (c2 == null) {
			return false;
		}

		if (c1.size() != c2.size()) return false;
		
LOOP1:  for (Iterator i = c1.iterator(); i.hasNext(); ) {
			Object o = i.next();
			for (Iterator j = c2.iterator(); j.hasNext(); ) {
				if (o.equals(j.next())) continue LOOP1;
			}

			return false;
		}

LOOP2:  for (Iterator i = c2.iterator(); i.hasNext(); ) {
			Object o = i.next();
			for (Iterator j = c1.iterator(); j.hasNext(); ) {
				if (o.equals(j.next())) continue LOOP2;
			}

			return false;
		}

		return true;
	}

	/**
	 * Returns true if the two collections are equivalent.  
	 *
	 * @param c1 the first Collection to compare.  Every element of the 
	 * Collection must implement the Equivalence interface.
	 * @param c2 the second Collection to compare.  Every element of the
	 * Collection must implement the Equivalence interface.
	 * @return true if the two collections are equivalent.  That is, two 
	 * collections are equivalent if and only if:<br><br>
	 * 1) <code>c1.size() == c2.size()</code><br><br>
	 * 2) for every object <code>o</code> in <code>c1</code>, 
	 * <code>o.equivalent(p)</code> for some object <code>p</code> in 
	 * <code>c2</code>.<br><br>
	 * 3) for every object <code>p</code> in <code>c2</code>, 
	 * <code>p.equivalent(o)</code> for some object <code>o</code> in 
	 * <code>c1</code>. 
	 */
	public static boolean equivalent(Collection c1, Collection c2) {
		if (c1.size() != c2.size()) return false;
		
LOOP1:  for (Iterator i = c1.iterator(); i.hasNext(); ) {
			Equivalence e1 = (Equivalence)i.next();
			for (Iterator j = c2.iterator(); j.hasNext(); ) {
				Equivalence e2 = (Equivalence)j.next();
				if (e1.equivalent(e2)) continue LOOP1;
			}

			return false;
		}

LOOP2:  for (Iterator i = c2.iterator(); i.hasNext(); ) {
			Equivalence e1 = (Equivalence)i.next();
			for (Iterator j = c1.iterator(); j.hasNext(); ) {
				Equivalence e2 = (Equivalence)j.next();
				if (e1.equivalent(e2)) continue LOOP2;
			}

			return false;
		}

		return true;
	}

	/**
	 * Returns true if the two maps are equal by value, instead of 
	 * equal by reference.
	 *
	 * @param m1 the first Map to compare
	 * @param m2 the second Map to compare
	 * @return true if the two maps are equal by value, instead of 
	 * equal by reference.  That is, two maps are equal if and only 
	 * if:<br><br>
	 * 1) <code>m1.keySet().size() == m2.keySet().size()</code><br><br>
	 * 2) for every key <code>k</code> in <code>m1</code>, 
	 * <code>m1.get(k).equals(m2.get(k))</code>.<br><br>
	 * 3) for every key <code>k</code> in <code>m2</code>, 
	 * <code>m2.get(k).equals(m1.get(k))</code>.<br><br>
	 */
	public static boolean equalityByValue(Map m1, Map m2) {
		if (m1.keySet().size() != m2.keySet().size()) return false;

		for (Iterator i = m1.keySet().iterator(); i.hasNext(); ) {
			Object k = i.next();

			if (m1.get(k) instanceof Collection) {
				if (!equalityByValue((Collection)m1.get(k), 
							(Collection)m2.get(k))) return false;
			}
			else {
				if (!m1.get(k).equals(m2.get(k))) return false;
			}
		}

		for (Iterator i = m2.keySet().iterator(); i.hasNext(); ) {
			Object k = i.next();

			if (m2.get(k) instanceof Collection) {
				if (!equalityByValue((Collection)m2.get(k), 
							(Collection)m1.get(k))) return false;
			}
			else {
				if (!m2.get(k).equals(m1.get(k))) return false;
			}
		}

		return true;
	}

	/**
	 * Does the given collection have an object equivalent to e?
	 * @param	c	the collection to be searched
	 * @param	e	the object to be compared
	 * @return	the equivalent object or null
	 */
	public static Object findEquivalentObject (Collection c, Equivalence e)
	{
		for (Iterator i=c.iterator(); i.hasNext();)
		{
			Object o = i.next();
			if (o != null && o instanceof Equivalence)
			{
				Equivalence e2 = (Equivalence) o;
				if (e.equivalent (e2))
					return o;
			}
		}
		return null;
	}

	/**
	 * Does the given collection have an object equivalent to e?
	 * @param	c	the collection to be searched
	 * @param	e	the object to be compared
	 * @return	true if an equivalent object is in the collection
	 */
	public static boolean hasEquivalentObject (Collection c, Equivalence e)
	{
		for (Iterator i=c.iterator(); i.hasNext();)
		{
			Object o = i.next();
			if (o != null && o instanceof Equivalence)
			{
				Equivalence e2 = (Equivalence) o;
				if (e.equivalent (e2))
					return true;
			}
		}
		return false;
	}
}
