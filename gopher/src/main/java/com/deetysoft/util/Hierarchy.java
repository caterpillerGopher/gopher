package com.deetysoft.util;

import com.deetysoft.collections.ArrayCreator;
import com.deetysoft.exception.MissingPropertyException;
import com.deetysoft.exception.StringFormatException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Build a hierarchy of {@link com.deetysoft.util.HierarchyNode HierarchyNode}
 * given an XML file.
 * Element attributes in XML are ignored.
 */
public class Hierarchy {

	/**
	 * The token used to delimit a substitution -'%'.
	 */
	public static final char	SUBSTITUTION_TOKEN	= '%';

	/**
	 * The prefix '.env' for substitution strings denotes a system property.
	 * For example: home=%env.HOME% sets the property named home
	 * to have the value of the HOME system property.
	 */
	public static final String	ENV_PREFIX			= "env.";

	/**
	 * Prevent this class from being instantiated.
	 */
	private Hierarchy() {}

	/**
	 * Build a hierarchy from the given XML document and it's DTD.
	 * <p>
	 * Returns the root node for the hierarchy.
	 * Each node in the hierarchy represents an element in the original
	 * XML document.
	 * A node's name is the name of the tag for that element.
	 * A node's value is the value for that element, if a value exists.
	 * A node's children represent the elements that are nested in that
	 * element, if any.
	 *
	 * @param xmlStream	an InputStream to an XML document.  The stream is
	 * closed upon return.
	 *
	 * @param dtd	a URL to the DTD for the XML document.  If the xml 
	 * document refers to a DTD, it must be specified here also.
	 * Specify null if a DTD is not used.
	 *
	 * @return	a {@link HierarchyNode} object representing the root node for 
	 * 			this hierarchy.
	 * @throws	IOException					if an IO exception occurs
	 * @throws	ParserConfigurationException	if an exception occurs in 
	 *											retrieving the parser
	 * @throws	SAXException		if an exception occurs during parsing
	 *
	 * @see		com.deetysoft.util.HierarchyNode
	 */
	public static HierarchyNode	buildFromXml (InputStream xmlStream, URL dtd)
		throws IOException, ParserConfigurationException, SAXException { 
	
		return buildFromXml(xmlStream, dtd, false);
	}

	/**
	 * This method is the same as {@link #buildFromXml(InputStream, URL)}
	 * except that parameter replaceEnvVars is defined.
	 * 
	 * @param	xmlStream	an InputStream with XML data
	 * @param	dtd			a URL to an optional DTD
	 * @param replaceEnvVars	if true, replace element value text of the form
	 * %env.ENVIRONMENT_VARIABLE% with the value of that environment variable.
	 * @return				a HierarchyNode representing the XML data
	 * @throws	IOException						on read error
	 * @throws	ParserConfigurationException	on configuration error
	 * @throws	SAXException					on SAX parser error
	 * @see #buildFromXml(InputStream, URL)
	 */
	public static HierarchyNode	buildFromXml(InputStream xmlStream, 	
		URL dtd, boolean replaceEnvVars)  
		throws IOException, ParserConfigurationException, SAXException {

		try
		{
			InputSource inputSource = new InputSource(
				new BufferedInputStream(xmlStream, 32768));

			return buildFromXml (inputSource, dtd, replaceEnvVars);
		}
		finally {
			// This code block will get executed, even if an
			// Exception is thrown above.
			// This may throw an IOException.
			//if (xmlStream != null)
			//	xmlStream.close();
		}
	}

	/**
	 * This method is the same as
	 * {@link #buildFromXml(InputStream, URL, boolean)}
	 * except that a reader is used instead of stream.
	 *
	 * @param	reader			a Reader for a XML document.
	 * @param	dtd				a URL to an optional DTD
	 * @param	replaceEnvVars	if true, replace element value text of the form
	 * %env.ENVIRONMENT_VARIABLE% with the value of that environment variable.
	 *
	 * @return					a HierarchyNode representing the XML data
	 * @throws	IOException						on read error
	 * @throws	ParserConfigurationException	on configuration error
	 * @throws	SAXException					on SAX parser error
	 * @see #buildFromXml(InputStream, URL, boolean)
	 */
	public static HierarchyNode	buildFromXml (Reader reader, 
		URL dtd, boolean replaceEnvVars)  
		throws IOException, ParserConfigurationException, SAXException {

		InputSource inputSource = new InputSource (reader);

		return buildFromXml (inputSource, dtd, replaceEnvVars);
	}

	/**
	 * This method is the same as
	 * {@link #buildFromXml(InputStream, URL, boolean)}
	 * except that an InputSource is used instead of stream.
	 *
	 * @param	inputSource		an InputSource for a XML document.
	 * @param	dtd				a URL to an optional DTD
	 * @param	replaceEnvVars	if true, replace element value text of the form
	 * %env.ENVIRONMENT_VARIABLE% with the value of that environment variable.
	 *
	 * @return					a HierarchyNode representing the XML data
	 * @throws	IOException						on read error
	 * @throws	ParserConfigurationException	on configuration error
	 * @throws	SAXException					on SAX parser error
	 *
	 * @see #buildFromXml(InputStream, URL, boolean)
	 */
	public static HierarchyNode	buildFromXml(InputSource inputSource,
		URL dtd, boolean replaceEnvVars)
		throws IOException, ParserConfigurationException, SAXException { 

		boolean validate = dtd != null;

		// This may throw a ParserConfigurationException.
		DocumentBuilderFactory builderFactory = 
			DocumentBuilderFactory.newInstance();

		builderFactory.setValidating(validate);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		if (dtd != null) {
			inputSource.setSystemId(dtd.toString());
		}

		Document document = null;

		// This may throw a SAXParseException, SAXException, or
		// IOException.
		document = builder.parse(inputSource);

		document.getDocumentElement().normalize();

		Element rootElement = document.getDocumentElement();

		return buildHierarchy(rootElement, replaceEnvVars);
	}

	/**
	 * Build a hierarchy from the given XML documents and their DTD's.
	 * <p>
	 * Elements from the xml documents are merged into the hierarchy
	 * in the given document order.
	 * A new, artifical root element (named "root")
	 * is introduced to allow for differing root elements in the documents.
	 * Therefore if a document has root "dog" then the returned
	 * {@link com.deetysoft.util.HierarchyNode HierarchyNode}
	 * will have a path "root/dog."
	 * <p>
	 * A leaf element from a file replaces a leaf element with the same
	 * path in the current hierarchy.
	 * This rule is repeated each time a file is read.
	 * Since siblings with the same name are allowed,
	 * the result of merging siblings with the same name is not predictable.
	 * The first matching sibling in the hierarchy is used in the merge.
	 * <p>
	 * A new branch is added to the first available matching parent.
	 * 
	 * @param xmlStreams a List of InputStream objects to XML documents.  
	 * They will be closed upon return.
	 * @param dtds a List of URL objects to the DTDs for the XML documents.
	 * If an XML document refers to a DTD, it must be specified here also.
	 * Specify null if a DTD is not used.
	 * @param	replace							if true, replace matching nodes as they are traversed,
	 * 											otherwise add to the child list
	 * @return	a {@link com.deetysoft.util.HierarchyNode} named "root" representing
	 * the root node for the hierarchy
	 * @throws	IOException						if an IO exception occurs
	 * @throws	ParserConfigurationException	if an exception occurs in 
	 *											retrieving the parser
	 * @throws	SAXException			if an exception occurs during parsing
	 * @see		com.deetysoft.util.HierarchyNode
	 */
	public static HierarchyNode	buildFromXml (List<InputStream> xmlStreams, List<URL> dtds, boolean replace)  
	throws IOException, ParserConfigurationException, SAXException {
		HierarchyNode root = new HierarchyNode ("root");

		int index = 0;

		for (Iterator<InputStream> i = xmlStreams.iterator(); i.hasNext(); ) {
			@SuppressWarnings("resource")
			InputStream xmlStream = i.next();
			URL dtd = dtds.get(index++);

			HierarchyNode kid = buildFromXml(xmlStream, dtd);

			if (replace) {
				merge (root, kid);
			} else {
				root.addChild(kid);
			}
		}

		return root;
	}

	/**
	 * Do the merge for {@link #buildFromXml(List, List)
	 * buildFromXml(List, List)}.
	 * Comments on the algorithm are in the comments for
	 * {@link #buildFromXml(List, List) buildFromXml(List, List)}.
	 */
	private static void merge (HierarchyNode parent, HierarchyNode orphan) {
		Collection<HierarchyNode>	kids_	= parent.getChildren ();
		Iterator<HierarchyNode>	kids	= kids_.iterator ();

		boolean matched = false;

		while (kids.hasNext ())
		{
			HierarchyNode kid = kids.next ();

			if (kid.getName().equals(orphan.getName()))
			{
				if (!kid.hasChildren ())
				{
					if (!orphan.hasChildren())
					{
						kid.setValue (orphan.getValue ());
					}
					else
					{
						//kid.addChildren (orphan.getChildren ());

						Collection<HierarchyNode> k_ = orphan.getChildren ();
						Iterator<HierarchyNode>	k = k_.iterator ();

						while (k.hasNext ())
						{
							kid.addChild (k.next ());
						}
					}
				}
				else
				{
					Collection<HierarchyNode> k_ = orphan.getChildren ();
					Iterator<HierarchyNode>	k = k_.iterator ();

					while (k.hasNext ())
					{
						merge (kid, (k.next ()));
					}
				}
				matched = true;
				break;
			}
		}
		if (!matched)
			parent.addChild (orphan);
	}

	/**
	 * Builds a hierarchy from the given root element.
	 */
	private static HierarchyNode buildHierarchy(Element rootElement,
			boolean replaceEnvVars) {
		String nodeName = rootElement.getNodeName().trim();
		//System.err.println(nodeName);
		HierarchyNode node = new HierarchyNode(nodeName);
		addChildren(node, rootElement, replaceEnvVars);

		return node;
	}

	/**
	 * Adds children to the hierarchy.
	 */
	private static void addChildren(HierarchyNode hierarchyNode, 
			Node xmlNode, boolean replaceEnvVars) {
		if (hasAnElementChild(xmlNode)) {
			NodeList childXmlNodes = xmlNode.getChildNodes();

			for (int i = 0; i < childXmlNodes.getLength(); i++) {
				Node childXmlNode = childXmlNodes.item(i);
				int type = childXmlNode.getNodeType();

				switch (type) {
					case Node.ELEMENT_NODE:
						HierarchyNode childHierarchyNode = new HierarchyNode(
								childXmlNode.getNodeName().trim());

						hierarchyNode.addChild(childHierarchyNode);

						addChildren(childHierarchyNode, childXmlNode,
								replaceEnvVars);
						break;
					default:
						//System.err.println ("Hierarchy node type "+type+" not recognized.");
				}
			}
		}
		else {
			String value = "";

			int childCount = xmlNode.getChildNodes().getLength();

			if (childCount > 0) {
				NodeList nl = xmlNode.getChildNodes();
				Node childXmlNode = nl.item(0);
				for (int i=1; i<nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if (n.getNodeType() == Node.CDATA_SECTION_NODE)
						childXmlNode = n;
				}

				value = childXmlNode.getNodeValue().trim();
			}

			if (replaceEnvVars) {
				List<Integer> tokenIndices = TokenIndexRetriever.getIndices (value,
						SUBSTITUTION_TOKEN);

				// If the tokens are not paired
				if (tokenIndices.size() % 2 != 0)
				{
					throw new StringFormatException
					("The element value \""+value+"\" has unmatched \""+
					SUBSTITUTION_TOKEN+"\".",
					"Config.STRING_FORMAT_EXCEPTION",
					ArrayCreator.create (value));
				}

				for (int i = tokenIndices.size()-2; i >= 0; i -= 2)
				{
					int startIndex = tokenIndices.get(i);
					int endIndex = tokenIndices.get(i+1);

					String insertName = value.substring
					(startIndex+1, endIndex);

					String insertValue = null;

					if (insertName.indexOf (ENV_PREFIX) == 0)
					{
						String envName = insertName.substring
						(ENV_PREFIX.length(), insertName.length());

						insertValue = System.getProperty (envName);
					}

					if (insertValue == null)
					{
						throw new MissingPropertyException
						(insertName, Hierarchy.class.getName ());
					}

					value = StringReplacer.substitute (value, insertValue,
							startIndex, endIndex);
				}

			}

			hierarchyNode.setValue(value);
			//System.err.println(hierarchyNode.getName()+" "+value);
		}
	}

	/**
	 * Has the given node any children that are
	 * elements?
	 */
	private static boolean hasAnElementChild(Node node) {
		NodeList childNodes = node.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			int type = childNode.getNodeType();

			switch (type) {
				case Node.ELEMENT_NODE:
					return true;
				default:
					//System.err.println ("HierarchyNode type "+type+" not recognized.");
			}
		}

		return false;
	}

	/**
	 * Get a depth-first iterator for the tree rooted at node.
	 * This method may not be desirable for large trees since it
	 * creates a List and iterates over that.
	 * 
	 * @param	node	a node from which we will traverse
	 * @return			an Iterator over the branch
	 */
	public static Iterator<HierarchyNode> getDepthFirstIterator (HierarchyNode node)
	{
		if (node == null)
			throw new IllegalArgumentException ("node is null.");

		return new DepthFirstIterator (node);
	}
}

class DepthFirstIterator implements java.util.Iterator<HierarchyNode>
{
	List<HierarchyNode> nodes = new ArrayList<HierarchyNode> ();
	Iterator<HierarchyNode> i = null;

	public DepthFirstIterator (HierarchyNode root)
	{
		if (root == null)
			throw new IllegalArgumentException ("root node is null");

		traverse (root);
		i = nodes.iterator();
	}

	public boolean hasNext()
	{
		return i.hasNext();
	}

	public HierarchyNode next ()
	{
		return i.next();
	}

	public void remove ()
	{
		throw new UnsupportedOperationException ("remove");
	}

	private void traverse (HierarchyNode n)
	{
		nodes.add (n);
		if (n.hasChildren())
		{
			Collection<HierarchyNode> kids = n.getChildren();
			for (Iterator<HierarchyNode> j=kids.iterator(); j.hasNext();)
				traverse (j.next());
		}
	}
}
