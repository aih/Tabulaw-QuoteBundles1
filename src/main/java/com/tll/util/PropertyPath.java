/**
 * The Logic Lab
 * @author jpk
 * Feb 16, 2008
 */
package com.tll.util;

/**
 * PropertyPath - Encapsulates a property path String providing convenience
 * methods for accessing and modifying its attributes.
 * <p>
 * A valid property path is: <br>
 * <ol>
 * <li>Standard OGNL format.<br>
 * E.g.: <code>propA.propB.propC[i].propD</code>
 * </ol>
 * @author jpk
 */
public final class PropertyPath {

	private static final char LEFT_INDEX_CHAR = '[';

	private static final char RIGHT_INDEX_CHAR = ']';

	/**
	 * Chains the given arguments together to form the corresponding property
	 * path. Support for <code>null</code> or empty is considered for all
	 * arguments.
	 * @param grandParentPropPath The parent property path to
	 *        <code>parentPropPath</code>.
	 * @param parentPropPath The parent property path to <code>propName</code>.
	 * @param propName The property name.
	 * @return The calculated property path.
	 */
	public static String getPropertyPath(String grandParentPropPath, String parentPropPath, String propName) {
		return getPropertyPath(getPropertyPath(grandParentPropPath, parentPropPath), propName);
	}

	/**
	 * Calculates the property path. Never returns <code>null</code>. Support for
	 * <code>null</code> or empty is considered for all arguments.
	 * <p>
	 * FORMAT: property path = <code>parentPropPath</code> + '.' +
	 * <code>propName</code>
	 * @param parentPropPath Assumed to NOT end in a dot. May be <code>null</code>
	 *        .
	 * @param propName Assumed to NOT have prefixing/suffixing dots. May be
	 *        <code>null</code>.
	 * @return The non-<code>null</code> calculated property path.
	 */
	public static String getPropertyPath(String parentPropPath, String propName) {
		return (parentPropPath == null || parentPropPath.length() < 1) ? (propName == null ? "" : propName)
				: (propName == null || propName.length() < 1) ? parentPropPath : parentPropPath + '.' + propName;
	}

	/**
	 * Is the given property path indexed? E.g.: <code>propA.propB[1]</code> is
	 * indexed but <code>propA[0].propB</code> is NOT.
	 * @param propPath
	 * @return true/false
	 */
	public static boolean isIndexed(String propPath) {
		return StringUtil.isEmpty(propPath) ? false : (propPath.charAt(propPath.length() - 1) == RIGHT_INDEX_CHAR);
	}

	/**
	 * Assembles an indexed property name given the indexable property name and
	 * the desired index.
	 * @param indexablePropName
	 * @param index The numeric index
	 * @return The indexed property name
	 */
	public static String index(String indexablePropName, int index) {
		return indexablePropName + indexToken(index);
	}

	/**
	 * Removes indexing tokens from the end of the given property path.
	 * @param indexedPropName The indexed property path
	 * @return The de-indexed property name or <code>null</code> if the given prop
	 *         is <code>null</code>.
	 */
	public static String deIndex(String indexedPropName) {
		if(indexedPropName != null && indexedPropName.length() > 0
				&& indexedPropName.charAt(indexedPropName.length() - 1) == RIGHT_INDEX_CHAR) {
			final int si = indexedPropName.indexOf(LEFT_INDEX_CHAR);
			if(si > 0) return indexedPropName.substring(0, si);
		}
		return indexedPropName;
	}

	/**
	 * Creates the index token given the numeric index and whether or not is is to
	 * be bound or un-bound.
	 * @param index The numeric index
	 * @return The index token.
	 */
	private static String indexToken(int index) {
		return LEFT_INDEX_CHAR + Integer.toString(index) + RIGHT_INDEX_CHAR;
	}

	/**
	 * Resolves the numeric index from an indexed property path node String (no
	 * dots).
	 * @param path The single node property path
	 * @return The resolved index or <code>-1</code> if the given prop is not
	 *         indexed.
	 * @throws IllegalArgumentException When the index is non numeric or negative.
	 */
	private static int resolveIndex(String path) throws IllegalArgumentException {
		if(path == null) return -1;
		final int bi = path.indexOf(LEFT_INDEX_CHAR), ebi = path.indexOf(RIGHT_INDEX_CHAR);
		if(bi > 0) {
			// indexed property prop name
			final String sindx = path.substring(bi + 1, ebi);
			try {
				final int rmIndx = Integer.parseInt(sindx);
				if(rmIndx < 0) {
					throw new IllegalArgumentException("Negative index in property path: " + path);
				}
				return rmIndx;
			}
			catch(final NumberFormatException nfe) {
				throw new IllegalArgumentException("Invalid index '" + sindx + "' in property path: " + path);
			}
		}
		return -1;
	}

	/**
	 * The property path buffer.
	 */
	private StringBuilder buf;

	/**
	 * The number of "nodes" in the property path.
	 */
	private int len;

	/**
	 * Constructor
	 */
	public PropertyPath() {
		super();
	}

	/**
	 * Constructor
	 * @param propPath A property path String.
	 */
	public PropertyPath(String propPath) {
		this();
		parse(propPath);
	}

	/**
	 * Constructor
	 * @param parentPropPath A parent property path
	 * @param propPath A property path that is appended to the given parent
	 *        property path
	 */
	public PropertyPath(String parentPropPath, String propPath) {
		this();
		parse(getPropertyPath(parentPropPath, propPath));
	}

	/**
	 * Constructor
	 * @param parentPropPath A parent property path
	 * @param index The numeric index
	 */
	public PropertyPath(String parentPropPath, int index) {
		this();
		parse(index(parentPropPath, index));
	}

	/**
	 * Parses a property path.
	 * @param propPath The property path String to be parsed. When
	 *        <code>null</code> or empty, en empty property path is set.
	 */
	public void parse(String propPath) {
		if(propPath == null || propPath.length() == 0) {
			len = 0;
			this.buf = null;
		}
		else {
			len = propPath.split("\\.").length;
			this.buf = new StringBuilder(propPath);
		}
	}

	/**
	 * @return The atomic property "nodes" that makeup this property path. <br>
	 *         E.g.: <code>propA.propB</code> returns
	 *         <code>{ propA, propB }</code>
	 */
	public String[] nodes() {
		return buf == null ? null : buf.toString().split("\\.");
	}

	/**
	 * @return The length, in characters, of the property path String.
	 */
	public int length() {
		return buf == null ? 0 : buf.length();
	}

	/**
	 * @return The number of "nodes" in the parsed property path.<br>
	 *         E.g.: <code>propA.propB[3].propC</code> has a depth of 3.
	 */
	public int depth() {
		return len;
	}

	/**
	 * Does this property path point to an indexed property?<br>
	 * (E.g.: <code>propA.propB[1]</code> or <code>propA.propB{1}</code>) <br>
	 * <em>NOTE: </em>nested indexed properties are not considered.
	 * @return true/false
	 */
	public boolean isIndexed() {
		if(buf == null) return false;
		final char end = buf.charAt(buf.length() - 1);
		return end == RIGHT_INDEX_CHAR;
	}

	/**
	 * Indexes the property path by simply appending the given index surrounded by
	 * bound index chars. <br>
	 * <em>NOTE: </em>No checking for existing indexing is performed.
	 * @param index The index num
	 */
	public void index(int index) {
		if(buf != null) buf.append(indexToken(index));
	}

	/**
	 * Returns the numeric index.
	 * <p>
	 * E.g.: "indexable[3]" returns 3.
	 * @return The resolved numeric index or <code>-1</code> if this property path
	 *         empty or is not indexed.
	 * @throws IllegalArgumentException When the index is non-numeric or negative.
	 */
	public int index() throws IllegalArgumentException {
		return buf == null ? -1 : indexAt(len - 1);
	}

	/**
	 * Strips indexing from the tail of this property path.
	 * <p>
	 * E.g.: "indexable[3]" will return "indexable"
	 * @return String with ending index tokens stripped.
	 */
	public String deIndex() {
		return deIndex(buf.toString());
	}

	/**
	 * Returns the node path of the given node index. <br>
	 * @param nodeIndex The node index
	 * @return The node path
	 * @throws ArrayIndexOutOfBoundsException When the node index is less than 0
	 *         or greater than number of nodes of this property path.
	 */
	public String pathAt(int nodeIndex) throws ArrayIndexOutOfBoundsException {
		if(buf == null) return null;
		if(nodeIndex < 0 || nodeIndex > len - 1) throw new ArrayIndexOutOfBoundsException();
		int cni = 0;
		final StringBuilder sub = new StringBuilder();
		for(int i = 0; i < buf.length(); ++i) {
			if(buf.charAt(i) == '.') {
				if(cni == nodeIndex) {
					// done
					break;
				}
				++cni;
			}
			else if(cni == nodeIndex) {
				sub.append(buf.charAt(i));
			}
		}
		return sub.toString();
	}

	/**
	 * @return The first node path.
	 */
	public String first() {
		return buf == null ? null : pathAt(0);
	}

	/**
	 * @return The last node path. This can be considered as the property name in
	 *         a property path.
	 */
	public String last() {
		return buf == null ? null : pathAt(depth() - 1);
	}

	/**
	 * Trims the property path by the given number of nodes from the end of the
	 * path returning an ancestral path.
	 * @param n The number of nodes to trim from the end of the path
	 * @return An ancestral path token or <code>null</code> if the given number of
	 *         nodes is incompatible with the currently held path.
	 */
	public String trim(int n) {
		final int indx = bufIndex(depth() - n);
		final String s = indx <= 0 ? null : buf.substring(0, indx - 1);
		return s;
	}

	/**
	 * Clips the property path by the given number of nodes from the
	 * <em>start</em> of the path returning a child path.
	 * @param n The number of nodes to trim from the start of the path
	 * @return A child path token or <code>null</code> if the given number of
	 *         nodes is incompatible with the currently held path.
	 */
	public String clip(int n) {
		final int indx = bufIndex(n);
		final String s = indx <= 0 ? null : buf.substring(indx);
		return s;
	}

	/**
	 * Calculates the String-wise index from a given node index.
	 * @param nodeIndex The node index
	 * @return The corres. String index of this property path.
	 */
	private int bufIndex(int nodeIndex) {
		if(nodeIndex == 0) return 0;
		if(buf != null) {
			int cni = 0;
			for(int i = 0; i < buf.length(); ++i) {
				if(buf.charAt(i) == '.') {
					if(++cni == nodeIndex) return i + 1;
				}
			}
		}
		return -1;
	}

	/**
	 * Extracts the property name at the given node index stripping indexing
	 * @param nodeIndex The node index (depth into the property path).
	 * @return The property name w/o indexing symbols
	 */
	public String nameAt(int nodeIndex) {
		return buf == null ? null : deIndex(pathAt(nodeIndex));
	}

	/**
	 * Extracts the resolved index of an indexed property at the given node index.
	 * @param nodeIndex The node index (depth into the property path).
	 * @return The resolved numeric index or <code>-1</code> if the property is
	 *         not indexed at the given node index.
	 * @throws IllegalArgumentException When the index is non-numeric or negative.
	 */
	public int indexAt(int nodeIndex) throws IllegalArgumentException {
		return buf == null ? -1 : resolveIndex(pathAt(nodeIndex));
	}

	/**
	 * Calculates the node index for the given node path String presumed to be
	 * part of this property path.
	 * @param nodePath The node path String that is part of this property path.
	 * @return The corresponding node index or <code>-1</code> if not found or if
	 *         this property path has not been set.
	 */
	private int nodeIndexOf(String nodePath) {
		if(buf == null) return -1;
		for(int i = 0; i < len; ++i) {
			if(nodePath.equals(pathAt(i))) return i;
		}
		return -1;
	}

	/**
	 * Resolves the parent property path of this property path.
	 * <p>
	 * <em>NOTE: If this property path is indexed, the parent property path is considered as the property path with out indexing.</em>
	 * <p>
	 * E.g.: <br>
	 * "node1.node2.node3" resolves to "node1.node2" <br>
	 * "node1[1]" resolves to "node1"
	 * @return The parent property path of this property path.
	 */
	public String getParentPropertyPath() {
		if(isIndexed()) {
			return deIndex();
		}
		final int d = depth();
		if(d < 2) return null;
		final int indx = bufIndex(d - 1);
		return buf.substring(0, indx - 1);
	}

	/**
	 * Sets the parent property path or removes the existing one if the given
	 * parent property path is <code>null</code>.
	 * @param parentPropPath The parent property path. May be <code>null</code> in
	 *        which case, the existing parent property path is removed.
	 * @return <code>true</code> if the parent property path was successfully set
	 *         or <code>false</code> when this property path is empty.
	 */
	public boolean setParentPropertyPath(String parentPropPath) {
		final int d = depth();
		if(d == 0) {
			return false;
		}
		else if(d == 1) {
			parse(getPropertyPath(parentPropPath, buf.toString()));
		}
		else {
			// d > 1
			parse(getPropertyPath(parentPropPath, last()));
		}
		return true;
	}

	/**
	 * @return The property name ignoring with no parent property path.
	 */
	public String getPropertyName() {
		return last();
	}

	/**
	 * Sets the property name retaining any existing parent property path. In
	 * other words, this method replaces the <em>last</em> node in a nested
	 * property path or replaces the entire property path when it isn't nested
	 * (i.e. no dots).
	 * @param propName The property <em>name</em> to set. If this name is empty,
	 *        no modification is made to this property path.
	 */
	public void setPropertyName(String propName) {
		if(!StringUtil.isEmpty(propName)) {
			parse(getPropertyPath(getParentPropertyPath(), propName));
		}
	}

	/**
	 * Replaces a single node in the property path.
	 * @param nodeIndex The index of the node to replace. If the given node index
	 *        exceeds the depth of this property path, this property path is
	 *        unaltered.
	 * @param prop The node replacement String. If <code>null</code> the property
	 *        path is "shortened" and will <em>not</em> contain the prop at the
	 *        given node index.
	 * @return <code>true</code> if the replacement was successful.
	 */
	public boolean replaceAt(int nodeIndex, String prop) {
		if(buf == null || nodeIndex > len - 1) return false;
		assert buf != null && len >= 0;
		final int i = bufIndex(nodeIndex);
		if(i == -1) return false;
		int j;
		if(nodeIndex == len - 1) {
			j = buf.length();
		}
		else {
			j = bufIndex(nodeIndex + 1) - 1;
		}
		if(prop == null) {
			// remove the node
			buf.replace(i, j + 1, "");
			len--;
			assert len >= 0;
		}
		else {
			buf.replace(i, j, prop);
		}
		return true;
	}

	/**
	 * Replaces a node path String.
	 * @param nodePath The target node path String to replace.
	 * @param replNodePath The replacement node path String.
	 * @return <code>true</code> if the replacement was successful.
	 */
	public boolean replace(String nodePath, String replNodePath) {
		if(buf == null) return false;
		final int ni = nodeIndexOf(nodePath);
		return ni == -1 ? false : replaceAt(ni, replNodePath);
	}

	/**
	 * Prepends a property path to this property path.
	 * @param path The property path to insert at the start
	 */
	public void prepend(String path) {
		parse(getPropertyPath(path, buf == null ? null : buf.toString()));
	}

	/**
	 * Prepends an indexed property path to this property path. <br>
	 * E.g.: A path of <code>parameters</code> and an index of <code>2</code>
	 * would prepend: <code>parameters[2]</code>.
	 * @param path The indexable property name
	 * @param index The index
	 */
	public void prepend(String path, int index) {
		prepend(index(path, index));
	}

	/**
	 * Appends a property path to this property path.
	 * @param path The property path to append
	 */
	public void append(String path) {
		parse(getPropertyPath(buf == null ? null : buf.toString(), path));
	}

	/**
	 * Appends an indexed property path to this property path. <br>
	 * E.g.: A path of <code>parameters</code> and an index of <code>2</code>
	 * would append: <code>parameters[2]</code>.
	 * @param path The indexable property name
	 * @param index The index
	 */
	public void append(String path, int index) {
		append(index(path, index));
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final PropertyPath other = (PropertyPath) obj;
		if(buf == null) {
			if(other.buf != null) return false;
		}
		else if(!buf.toString().equals(other.buf == null ? null : other.buf.toString())) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (buf == null) ? 0 : 31 + buf.toString().hashCode();
	}

	@Override
	public String toString() {
		return buf == null ? null : buf.toString();
	}
}
