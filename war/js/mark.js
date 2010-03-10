function stringTrim(s) {
	return s.replace(/^\s+|\s+$/g,"");
}
function stringLTrim(s) {
	return s.replace(/^\s+/,"");
}
function stringRTrim(s) {
	return s.replace(/\s+$/,"");
}

/**
 * Clears out all text selections in the given window or doc.
 * <p>Works for w3c and IE. 
 * @param wnd the window ref
 * @param doc the doc ref
 * @return void
 */
function clearWindowSelections(wnd, doc) {
	if (wnd && wnd.getSelection) wnd.getSelection().removeAllRanges();
	else if (doc && doc.selection) doc.selection.empty();
}

/*
if (!window['Node']) {
	alert('No node!');
	window.Node = new Object();
	Node.ELEMENT_NODE = 1;
	Node.ATTRIBUTE_NODE = 2;
	Node.TEXT_NODE = 3;
	Node.CDATA_SECTION_NODE = 4;
	Node.ENTITY_REFERENCE_NODE = 5;
	Node.ENTITY_NODE = 6;
	Node.PROCESSING_INSTRUCTION_NODE = 7;
	Node.COMMENT_NODE = 8;
	Node.DOCUMENT_NODE = 9;
	Node.DOCUMENT_TYPE_NODE = 10;
	Node.DOCUMENT_FRAGMENT_NODE = 11;
	Node.NOTATION_NODE = 12;
}
*/

function NodePath(root) {
	var rootNode = root || document.body,
		nodeNumbers = [];

	this.toString = function() {
		var s, i;

		s = ' [ ' + nodeNumbers[0];
		for (i=1; i < nodeNumbers.length; i++) {
			s += ', '+ nodeNumbers[i];
		}
		s += ' ] ';

		return s;
	}

	this.addParent = function(nodeNumber) {
		var oldNumbers = nodeNumbers, i;

		nodeNumbers = [nodeNumber]; // FIXME this should be a method of Array
		for (i=0; i < oldNumbers.length; i++) {
			nodeNumbers[i+1] = oldNumbers[i];
		}

		return this;
	}

	this.addChild = function(nodeNumber) {
		nodeNumbers[nodeNumbers.length] = nodeNumber;

		return this;
	}

	this.getNode = function() {
		var i, n;

		n = rootNode;
		for (i=0; i <nodeNumbers.length; i++) {
			n = nodeGetChild(n, nodeNumbers[i]);
		}

		return n;
	}

	this.getNumbers = function() {
		return nodeNumbers;
	}

	this.setNumbers = function(numbers) {
		nodeNumbers = numbers;
		return this;
	}

	this.length = function() {
		return nodeNumbers.length;
	}

	this.last = function() {
		return nodeNumbers[nodeNumbers.length - 1];
	}

	this.eq = function(otherNodePath) {
		var i;

		if (! (otherNodePath instanceof NodePath)) {
			return false;
		}

		othersNodeNumbers = otherNodePath.getNumbers();
		if (othersNodeNumbers.length != nodeNumbers.length) {
			return false;
		}

		for (i=0; i < nodeNumbers; i++) {
			if (nodeNumbers[i] != othersNodeNumbers[i]) {
				return false;
			}
		}

		return true;
	}
}

//Node.prototype.siblingNumber = function () {
function nodeSiblingNumber(e) {
	var i = 0;
	
	while (e = e.previousSibling) {
		//alert('e: ' + e);
		if (e.isNotOriginal) {
			continue;
		}
		i++;
	}

	return i;
}

//Node.prototype.getChild = function (number) {
function nodeGetChild(e, number) {
	var e, i;

	e = e.firstChild;
	for ( i=1; i <= number; i++) {
		e = e.nextSibling;
		while (e.isNotOriginal) {
			e = e.nextSibling;
		}
	}

	return e;
}

//Node.prototype.getNodePath = function(root) {
function nodeGetNodePath(e, root) {
	var np = new NodePath(root);
	
	while (e != root) {
		np.addParent(nodeSiblingNumber(e));
		e = e.parentNode;
	}

	return np;
}

function nodeIsAncestor(nsubject, ntree) {
	var p = ntree;
	while(p) {
		if(p == nsubject) return true;
		p = p.parentNode;
	}
	return false;
}

/**
 * Takes the children under the given element ref putting them at element parent
 * level then removes the empty element from the dom.
 * 
 * @param e element ref
 * @return void
 */
function nodePromoteChildren(e) {
	var parent, adj, rmvd;

	if(!e) return;
	
	// promote the span children up one level re-integrating text boundaries into existing text nodes!
	parent = e.parentNode;
  adj = e;
  while(e.firstChild) {
    rmvd = e.removeChild(e.firstChild);
    adj = parent.insertBefore(rmvd, adj);
    adj = adj.nextSibling;
  }
  parent.removeChild(e);
  
  // TODO IE doesn't support normalize()  Is it needed for IE?
  if(parent.normalize) parent.normalize();
}

var mcounter = 0;

/**
 * Mark - constructor
 * 
 * @param elmDoc The dom document ref to use as the root. This is necessary to
 *          dis-ambiguate when applying this to nested documents in iframes for
 *          example.
 * @param range goog.dom.AbstractRange
 * 
 * @throws 'No text selected'
 * @throws 'Non-textual range'
 * @throws 'Unsupported range type'
 */
function Mark(elmDoc, range) {

	this.markId = 'mark_' + (++mcounter);
	this.text = '';
	this.startNodePath = null;
	this.startOffset = 0;
	this.endNodePath = null;
	this.endOffset = 0;
	this.elmDoc = elmDoc;

	this.onHover = function (event) {
		// TODO set focus of corres. quote
	}
	
	this.toString = function () {
		return this.text;
	}

	this.eq = function (other, noCheckBack) {
		var startsAreEqual = false,
			endsAreEqual = false;

		noCheckBack = noCheckBack || false;

		if (! (other instanceof Mark)) {
			return false;
		}

		/*
		if (this.pageUrl != other.pageUrl) {
			return false;
		}
		*/

		if ( this.startNodePath.eq(other.startNodePath) ) {
			startsAreEqual = true;
		} else {
			if ( this.startOffset === 0 ) {
				if (this.startNodePath.length() === other.startNodePath.length() + 1) {
					if ((this.startNodePath.last() + 1) === other.startOffset) {
						startsAreEqual = true;
					}
				}
			}
		}

		if ( this.endNodePath.eq(other.endNodePath) ) {
			endsAreEqual = true;
		} else {
			if ( this.endOffset === 0 ) {
				if (this.endNodePath.length() === other.endNodePath.length() + 1) {
					if ((this.endNodePath.last() + 1) == other.endOffset) {
						endsAreEqual = true;
					}
				}
			}
		}

		if (startsAreEqual && endsAreEqual) {
			return true;
		} else {
			if (noCheckBack) {
				return false;
			} else {
				if (other.eq(this, true)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * Creates a new goog.dom.TextRange from the state of this Mark.
	 */
	this.createRange = function() {
		var startNode, endNode, startOffset, endOffset;
		
		startNode   = this.startNodePath.getNode();
		endNode     = this.endNodePath.getNode();
		startOffset = this.startOffset;
		endOffset   = this.endOffset;
		return goog.dom.Range.createFromNodes(startNode, startOffset, endNode, endOffset);
	}
	
	/**
	 * Validates the given range ensuring it is eligible for being converted to a Mark.
	 * @throws 'No text selected'
	 * @throws 'Non-textual range'
	 * @throws 'Ranges can not overlap'
	 */
	this.validateRange = function(range) {
		var text, shtml;
		text = range.getText();
		if(!text || stringTrim(text).length == 0) 
			throw 'No text selected';
		if(range.getStartNode().nodeType != 3 || range.getEndNode().nodeType != 3)
			throw 'Non-textual range';
		shtml = range.getValidHtml();
		//alert('shtml: ' + shtml);
		if(shtml.indexOf('mark_') > 0)
			throw 'Ranges can not overlap';
	}
	
	this.isPartialRange = function(range) {
		// TODO impl
	}

	/**
	 * Attempts to provide an un-partialized range from the given range.
	 * @return An un-partialized range
	 * @throws error when the range is un-partialize-able
	 */
	// TODO fix
	/*
	this.unpartialize = function(range) {
		var elmCnt, trange, tnStart = null, tnEnd = null, tnEndLen = 0;
		
		if(!this.isPartialRange(range)) {
			// we have a good range
			return range;
		}
		
		// try to select the entire containing element text
		elmCnt = range.getContainerElement();
		if(elmCnt == this.elmDoc.body)
			throw 'Un-resolvable partialized range';
		
		// get first text node child
		var nitr = new goog.dom.NodeIterator(elmCnt);
		var ndesc;
		try {
			while(ndesc = nitr.next()) {
				//alert('ndesc.nodeName: ' + ndesc.nodeName);
				if(ndesc.nodeType == 3 && ndesc.nodeValue.trim().length > 1) {
					if(!tnStart) {
						tnStart = ndesc;
					} else {
						tnEnd = ndesc;
					}
				}
				//if(nitr.isEndTag()) break;
			}
		} catch(e) {
			if(!(e instanceof goog.iter.StopIteration)) throw e;
		}
		
		//alert('tnStart: ' + tnStart);
		//alert('tntnEnd.nodeValue: ' + tnEnd.nodeValue);
		//alert('tnEnd.nodeValue.length: ' + tnEnd.nodeValue.length);
		trange = goog.dom.Range.createFromNodes(tnStart, 0, tnEnd, tnEnd.nodeValue.length);
		//alert('trange: ' + trange);
		
		// validate again
		this.validateRange(trange);
		
		return trange;
	}
	*/
	
	/**
	 * Highlights the text bounded by the given range -OR- the text bounded by the
	 * current state of this Mark if the range param is not specified.
	 * <p>
	 * This method checks to see if this mark is already highlighted and if so
	 * does nothing.
	 * <p>
	 * This method should not fail as it is assumed the range has already been
	 * verified to NOT partially select any bounded nodes.
	 * 
	 * @param range optional. When specified, this is used over the state of this Mark
	 * 
	 * @throws 'Highlighting failed'
	 */
	this.highlight = function(range) {
		if(this.hspan) return;	// already highlighted
		
		if(!range) range = this.createRange();
		
		this.hspan = this.elmDoc.createElement('span');
		this.hspan.isNotOriginal = true;
		this.hspan.id = this.markId;
		this.hspan.className = 'highlight';
		//this.hspan.addEventListener( "mouseover", this.onHover, false);
		
		try {
			range.surroundContents(this.hspan);
		} catch(e) {
			this.hspan = null;
			throw e;
		}

		// the all important reify of hspan necessary for IE
		this.hspan = this.elmDoc.getElementById(this.markId);
		if(!this.hspan) throw 'Highlighting failed';

		this.text = range.getText();
	}
	
	/**
	 * Un-highlights this mark restoring the dom to its original state prior to
	 * any highlighting for this mark.
	 */
	this.unhighlight = function() {
	  if(!this.hspan) return; // not selected
	  nodePromoteChildren(this.hspan);
	  this.hspan = null;
	}
	
	var root, urange;
	
	this.validateRange(range);
	//urange = this.unpartialize(range);
	urange = range;
	
	var root = this.elmDoc.body;
	this.startNodePath = nodeGetNodePath(urange.getStartNode(), root);
	this.endNodePath = nodeGetNodePath(urange.getEndNode(), root);
	this.startOffset = urange.getStartOffset();
	this.endOffset = urange.getEndOffset();

	this.highlight(urange);
} // Mark constructor
