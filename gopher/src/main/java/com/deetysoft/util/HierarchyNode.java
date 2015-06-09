package com.deetysoft.util;

import com.deetysoft.exception.StringFormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class represents a node in a hierarchy.
 * <p>
 * A <code>HierarchyNode</code> holds a name and either a collection of child 
 * nodes or a value.  A non-leaf node has children and no value, 
 * and a leaf node has a value and no children.
 */
@SuppressWarnings("serial")
public class HierarchyNode implements java.io.Serializable {

	/**
	 * The name of this node.
	 */
	private String name;

	/**
	 * The value of this node.
	 * If this node is not a leaf node, <code>value</code> is null.
	 */
	private Object value;

	private HierarchyNode parent;

	/**
	 * A collection of child nodes.
	 */
	private Collection<HierarchyNode> children = new ArrayList<HierarchyNode>();

	/**
	* Creates a HierarchyNode with the given name.
	*
	* @param name the name for this node
	*/
	public HierarchyNode(String name) {
		this.name = name;
	}

	/**
	* Creates a HierarchyNode with the given name and value.
	*
	* @param name the name for this node
	* @param value the value for this node
	*/
	public HierarchyNode(String name, Object value) {
		this (name);
		this.value = value;
	}

	/**
	 * Adds a child node to this node.
	 *
	 * @param childNode the child node to be added
	 */
	public void addChild(HierarchyNode childNode) {
		childNode.setParent (this);
		children.add(childNode);
	}

	/**
	 * Add children to this node.
	 *
	 * @param children the children to be added
	 */
	public void addChildren(Collection<HierarchyNode> children) {
		for (Iterator<HierarchyNode> i=children.iterator(); i.hasNext();)
			addChild (i.next());
	}

	/**
	 * Override {@link java.lang.Object#equals} Object.}
	 */
	public boolean equals (Object obj)
	{
		if (!(obj instanceof HierarchyNode)) return false;

		HierarchyNode o = (HierarchyNode) obj;

		if (!Equals.equals(name, o.name)) return false;
		if (!Equals.equals(value, o.value)) return false;
		if (!CollectionEquality.equalityByValue(children, o.children))
			return false;

		return true;
	}

	/**
	 * Return the name of this node.
	 *
	 * @return the name of this node
	 */
	public String getName() {
		return name;
	}

	public HierarchyNode getParent ()
	{
		return parent;
	}

	public void setParent (HierarchyNode parent)
	{
		this.parent = parent;
	}

	/**
	 * Return the value of this node.
	 *
	 * @return the value of this node.  If this node is not a leaf node, then 
	 * this method returns null.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the value of the node specified by relNodePath.
	 *
	 * @param relNodePath a relative path of node names to the node of 
	 * interest.  Node names must be separated with a '/' delimiter.
	 * For example, if one had the following hierarchy:
	 * <pre>
	 * node: root
	 *     node: branch1
	 *         node: branch2
	 *             node: leaf
	 *                 value: blee
	 * </pre>
	 * And if one had a reference to node "root" in a node named
	 * rootNode then one could get the value of the node
	 * named "leaf" via the following statement:
	 * <br><pre>
	 * String leafValue = rootNode.getValue("branch1/branch2/leaf");
	 * </pre>
	 * @return an Object holding the value of the node specified by
	 * relNodePath.  If no unique node exists 
	 * for the given path, then this method returns null.
	 *
	 * @see #getNodesByPath
	 */
	public Object getValue (String relNodePath)
	{
		Collection<HierarchyNode> nodes = getNodesByPath (relNodePath);

		if (nodes.size () != 1)
			return null;

		Iterator<HierarchyNode> i = nodes.iterator ();

		HierarchyNode node = i.next ();

		return node.getValue();
	}

	/**
	 * If value is a Boolean return it's boolean value.
	 * If value is a String, try to parse a boolean value.
	 *
	 * @return the value of this node.  If this node is not a leaf node, then 
	 * this method returns null.
	 *
	 * @exception	NumberFormatException	on error parsing String value
	 * @exception	IllegalArgumentException	if value is not a String or
	 * 				Boolean
	 */
	public boolean getValueAsBoolean () throws NumberFormatException,
		IllegalArgumentException {

		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue ();

		if (value instanceof String) {
			//return Boolean.valueOf ((String)value).booleanValue();
			String v = (String) value;
			if (v.equalsIgnoreCase("y")) {
				return true;
			}
			if (v.equalsIgnoreCase("true")) {
				return true;
			}
			return false;
		}

		throw new IllegalArgumentException (
			"Value member has class "+value.getClass()+
			" - not a Boolean or a String.");
	}

	/**
	 * If value is a Double return it's double value.
	 * If value is a String, try to parse a double value.
	 *
	 * @return the value of this node.  If this node is not a leaf node, then 
	 * this method returns null.
	 *
	 * @exception	NumberFormatException	on error parsing String value
	 * @exception	IllegalArgumentException	if value is not a String or
	 * 				Double
	 */
	public double getValueAsDouble () throws NumberFormatException,
		IllegalArgumentException {

		if (value instanceof Double)
			return ((Double)value).doubleValue ();

		if (value instanceof String)
			return Double.valueOf ((String)value).doubleValue();

		throw new IllegalArgumentException (
			"Value member has class "+value.getClass()+
			" - not a Double or a String.");
	}

	/**
	 * Returns the value of this node as an integer.
	 *
	 * @return the value of this node.  If this node is not a leaf node, then 
	 * this method returns null.
	 *
	 * @exception	NumberFormatException	on error parsing String value
	 * @exception	IllegalArgumentException	if value is not a String or
	 * 				Integer
	 */
	public int getValueAsInt() throws NumberFormatException {

		if (value instanceof String)
			return Integer.parseInt ((String)value);

		if (value instanceof Integer)
			return ((Integer)value).intValue ();

		throw new IllegalArgumentException (
			"Value member has class "+value.getClass()+
			" - not a Integer or a String.");
	}

	/**
	 * Call {@link #getValueAsInt getValueAsInt} on the
	 * the first matching node at relNodePath.
	 * @param	relNodePath	get the value of element having this path
	 * @return				the value as an int
	 */
	public int getValueAsInt (String relNodePath) throws NumberFormatException
	{
		Collection<HierarchyNode> nodes = getNodesByName (relNodePath);
		HierarchyNode h = nodes.iterator().next();
		return h.getValueAsInt();
	}

	/**
	 * Returns the value of this node as a long.
	 *
	 * @return the value of this node.  If this node is not a leaf node, then 
	 * this method returns null.
	 */
	public long getValueAsLong() throws NumberFormatException {
		return Long.parseLong (value.toString());
	}

	/**
	 * Returns the value of this node as a Long.
	 *
	 * @return the value of this node.  If this node is not a leaf node, then 
	 * this method returns null.
	 */
	public Long getValueAsLongObj() throws NumberFormatException {
		return new Long(value.toString());
	}
	
	/**
	 * Call {@link #getValueAsLong getValueAsLong} on the
	 * the first matching node at relNodePath.
	 * @param	relNodePath	get the value of element having this path
	 * @return				the value as an Long
	 */
	public long getValueAsLong (String relNodePath) throws NumberFormatException
	{
		Collection<HierarchyNode> nodes = getNodesByName (relNodePath);
		HierarchyNode h = nodes.iterator().next();
		return h.getValueAsLong();
	}

	/**
	 * Call {@link #getValueAsLong getValueAsLong} on the
	 * the first matching node at relNodePath.
	 * If there is no matching node, return null.
	 * @param	relNodePath	get the value of element having this path
	 * @return				the value as an Long
	 */
	public Long getOptionalValueAsLong (String relNodePath)
		throws NumberFormatException {

		Collection<HierarchyNode> nodes = getNodesByName (relNodePath);
		if (nodes.size() == 0)
			return null;
		HierarchyNode h = nodes.iterator().next();
		return new Long (h.getValueAsLong());
	}

	/**
	 * Returns the child nodes for this node.
	 *
	 * @return a Collection of child nodes for this node.  If this node is a 
	 * leaf node, then this method returns a Collection of size 0.
	 * @see #getNodesByName
	 * @see #getNodeByName
	 */
	public Collection<HierarchyNode> getChildren() {
		return children;
	}

	/**
	 * Get a child node by name and value.
	 *
	 * @param	name	the name of the child node
	 * @param	value	the child's value
	 * @return			the node
	 */
	public HierarchyNode	getChild (String name, Object value)
	{
		Iterator<HierarchyNode> i = children.iterator ();

		while (i.hasNext ())
		{
			HierarchyNode n = i.next ();

			String name_ = n.getName();

			if (name == null && name_ != null)
				continue;

			if (name != null && !name.equals (name_))
				continue;

			Object value_ = n.getValue ();

			if (value == null && value_ != null)
				continue;

			if (value != null && !value.equals (value_))
				continue;

			return n;
		}

		return null;
	}

	/**
	 * Get a node by path and value. There may be multiple elements at the location.
	 * Use value for comparison to find the right element.
	 * @param	path	the path to the element
	 * @param	value	the expected value of the element
	 * @return			the node if any
	 */
	public HierarchyNode	getNode (String path, Object value)
	{
		Collection<HierarchyNode> nodes = getNodesByPath (path);

		if (nodes.size () == 0)
			return null;

		Iterator<HierarchyNode> i = nodes.iterator ();

		while (i.hasNext ())
		{
			HierarchyNode n = i.next ();

			if (n.getValue ().equals (value))
				return n;
		}

		return null;
	}

	/**
	 * Get a node by path and value ignoring case.
	 * Use value for comparison to find the right element.
	 * @param	path	the path to the element
	 * @param	value	the expected value of the element
	 * @return			the node if any
	 */
	public HierarchyNode getNodeIgnoreCase (String path, String value) {
		Collection<HierarchyNode> nodes = getNodesByPath (path);

		if (nodes.size () == 0)
			return null;

		Iterator<HierarchyNode> i = nodes.iterator ();

		while (i.hasNext ())
		{
			HierarchyNode n = i.next ();

			if (((String)n.getValue ()).equalsIgnoreCase(value))
				return n;
		}

		return null;
	}

	/**
	 * Returns those children of this node whose names match
	 * <code>nodeName</code>.
	 *
	 * @param nodeName the name of the child nodes to return
	 * @return a Collection of child <code>HierarchyNode</code> objects whose 
	 * names match <code>nodeName</code>.  If no child nodes have the name
	 * <code>nodeName</code>, then this method returns a Collection of 
	 * size 0.
	 * @see #getNodeByName
	 * @see #getChildren
	 */
	public Collection<HierarchyNode> getNodesByName(String nodeName) {
		Collection<HierarchyNode> returnNodes = new ArrayList<HierarchyNode>();

		if (hasChildren()) {
			Collection<HierarchyNode> children = getChildren();
			for (Iterator<HierarchyNode> i = children.iterator(); i.hasNext(); ) {
				HierarchyNode node = i.next();
				if (node.getName().equals(nodeName))
					returnNodes.add(node);
			}
		}

		return returnNodes;
	}

	/**
	 * Like {@link #getNodesByName getNodesByName} except we return only
	 * the non-null values of the nodes.
	 * @param	nodeName	the name of the node
	 * @return	the values
	 */
	public Collection<Object> getNodeValuesByName (String nodeName)
	{
		Collection<HierarchyNode> nodes = getNodesByName (nodeName);

		Collection<Object> values = new ArrayList<Object> ();

		for (Iterator<HierarchyNode> i=nodes.iterator(); i.hasNext();)
		{
			HierarchyNode n = i.next();
			if (n.getValue() != null)
				values.add (n.getValue());
		}

		return values;
	}

	/**
	 * Returns a child of this node whose name matches
	 * <code>nodeName</code>.  This method returns the same result as
	 * <code>getNodesByName(nodeName).iterator().next()</code> for
	 * nodes with exactly one child with name <code>nodeName</code>.
	 * Use this method as a convenience
	 * if this node is known to have at most one child node with this
	 * name, otherwise use {@link #getNodesByName}.
	 *
	 * @param nodeName the name of the child node to return
	 * @return a child <code>HierarchyNode</code> object whose name matches 
	 * <code>nodeName</code>.  If no child node has the name
	 * <code>nodeName</code>, then this method returns null.
	 * If more than one child node has the name <code>nodeName</code>
	 * then this method returns one of those child nodes at random.
	 * @see #getNodesByName
	 * @see #getChildren
	 */
	public HierarchyNode getNodeByName(String nodeName) {
		if (hasChildren()) {
			Collection<HierarchyNode> children = getChildren();
			for (Iterator<HierarchyNode> i = children.iterator(); i.hasNext(); ) {
				HierarchyNode node = i.next();
				if (node.getName().equals(nodeName))
					return node;
			}
		}

		return null;
	}

	/**
	 * Behaves the same as {@link #getNodeByName} but ignores case when
	 * finding the node name.
	 * @param	nodeName	the name of the node
	 * @return				the node if any
	 */
	public HierarchyNode getNodeByNameIgnoreCase(String nodeName) {
		if (hasChildren()) {
			Collection<HierarchyNode> children = getChildren();
			for (Iterator<HierarchyNode> i = children.iterator(); i.hasNext(); ) {
				HierarchyNode node = i.next();
				if (node.getName().equalsIgnoreCase(nodeName))
					return node;
			}
		}

		return null;
	}

	/**
	 * Get all the HierarchyNodes corresponding to a path, similar to
	 * {@link #getValue(String) getValue(String)}.
	 *
	 * @param	path					the path to the nodes
	 * @return							a collection of nodes
	 * @throws	StringFormatException	if path string is invalid
	 * @see		#getValue(String)		for explanation of defining path
	 */
	public Collection<HierarchyNode> getNodesByPath (String path) throws StringFormatException
	{
		Collection<HierarchyNode> results = new ArrayList<HierarchyNode> ();

		getNodesByPath (this, path, results);

		return results;
	}

	/**
	 * Get all the nodes under node matching path. Store matching nodes in
	 * results.
	 */
	private void	getNodesByPath (HierarchyNode node, String path,
					Collection<HierarchyNode> results) throws StringFormatException
	{
		if (path == null)
			return;
		if (path.equals (""))
			return;

		int	delimIndex = path.indexOf ('/');

		if (delimIndex == 0)
		{
			throw new StringFormatException ("Path '" + path + 
					"' begins with the '/' character");
		}

		String	name		= null;
		String	nextPath	= null;

		if (delimIndex == -1)
		{
			name		= path.substring (0,path.length ());
			nextPath	= "";
		}
		else
		{
			name		= path.substring (0,delimIndex);
			nextPath	= path.substring (delimIndex+1,path.length());
		}

		Collection<HierarchyNode>	kids		= node.getChildren ();
		Iterator<HierarchyNode>	kidsIterator	= kids.iterator ();

		while (kidsIterator.hasNext ())
		{
			HierarchyNode kid = kidsIterator.next ();

			if (kid.getName ().equals (name))
			{
				if (nextPath.equals (""))
				{
					results.add(kid);
				}
				else
				{
					if (kid.hasChildren ())
						getNodesByPath (kid, nextPath, results);
				}
			}
		}
	}

	/**
	 * Returns whether or not this node has children.
	 *
	 * @return <code>true</code> if this node has children, 
	 * <code>false</code> otherwise.
	 */
	public boolean hasChildren() {
		return (!(children.size() == 0));
	}

	/**
	 * Assigns a value for this node.
	 *
	 * @param value the value for this leaf node.
	 */
	public void setValue(Object value) {

		this.value = value;
	}

	/**
	 * Returns a String representation of the hierarchy beginning at this 
	 * node.
	 *
	 * @return a String representation of the hierarchy beginning at this 
	 * node.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		traverseTree(this, sb, 0);
		return sb.toString();
	}

	/**
	 * Appends a textual representation of the hierarchy tree beginning at 
	 * <code>node</code> to <code>stringBuffer</code>.
	 *
	 * @param node the root node of the tree
	 * @param stringBuffer the StringBuffer to append the text to
	 * @param indent the number of characters to indent the text
	 */
	private void traverseTree(HierarchyNode node, StringBuffer stringBuffer, 
			int indent) {

		indent(stringBuffer, indent);

		if (node.getValue() != null) {
			stringBuffer.append("Node: " + node.getName() + " ");
			indent(stringBuffer, indent + 2);
			stringBuffer.append("Value: " + node.getValue() + "\n");
		}
		else {
			stringBuffer.append("Node: " + node.getName() + "\n");
		}
		if (node.hasChildren()) {
			Collection<HierarchyNode> children = node.getChildren();

			for (Iterator<HierarchyNode> i = children.iterator(); i.hasNext(); ) {
				HierarchyNode child = i.next();
				traverseTree(child, stringBuffer, indent + 2);
			}
		}
	}

	/**
	 * Appends <code>indent</code> number of spaces to 
	 * <code>stringBuffer</code>.
	 */
	private static void indent(StringBuffer stringBuffer, int indent) {
		for (int i = 0; i < indent; i++)
			stringBuffer.append(" ");
	}
}
