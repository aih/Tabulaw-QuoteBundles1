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

/**
 * DEBUG
 */
function nodeToString(node) {
  var val;
  switch(node.nodeType) {
  case 3: // text
    val = node.nodeValue;
    if(val.length > 7) val = val.substr(0, 7) + '...';
    return 'TEXT_NODE: ' + val + '(parent: ' + node.parentNode.id + ')';
  case 1: // element
  	return 'ELEMENT_NODE: ' + node.id;
  case 8: // comment
    return 'COMMENT_NODE: ' + node.nodeValue;
  }
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

		s = '[' + nodeNumbers[0];
		for (i=1; i < nodeNumbers.length; i++) {
			s += ','+ nodeNumbers[i];
		}
		s += ']';

		return s;
	}
	
	this.fromString = function(s) {
		var sp, iarr, i, nn;
		
		sp = s.substring(1, s.length-1);
		sp = sp.split(',')
		iarr = [];
		for(i=0; i<sp.length; i++) {
			nn = parseInt(sp[i]);
			iarr[i] = nn;
		}
		this.setNumbers(iarr);
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

/**
 * Surrounds the given text node with the given element node.
 * @param textNode the text node to surround
 * @param surroundElm the element with which to surround
 * @return
 */
function textNodeSurround(textNode, surroundElm) {
	textNode.parentNode.insertBefore(surroundElm, textNode);
	surroundElm.appendChild(textNode);
}

/**
 * Finds the nearest non-whitespace char index in a text node starting the the
 * specified offset checking either left or right
 * @param tn text node to check
 * @param startOffset offset to start at
 * @param toleft iterate to the left or right
 * @return nearest index (relative to start of text node) containing non-whitespace char or -1 if none is found
 */
function getNearestNonWhitespaceIndex(tn, startOffset, toleft) {
	var tnv = tn.nodeValue;
	var tlen = tnv.length;
	
	if(!isWhitespaceChar(tnv[startOffset])) return startOffset;

	if(toleft) {
		
	} else {
	}
}

var mcounter = 0;

/**
 * Mark - constructor
 */
function Mark() {

	this.markId = 'mark_' + (++mcounter);
	this.text = '';
	this.startNodePath = null; // NodePath type
	this.startOffset = 0;
	this.endNodePath = null; // NodePath type
	this.endOffset = 0;
	
	/* 
	 * the highlight spans
	 * FORMAT:
	 *   class="highlight {markId}"
	 */
	this.hspans = null;
	
	/**
	 * Serializes the state of this mark instance to a string.
	 * FORMAT: 
	 * "{markId}|{text}|{startNodePath.toString()}|{startOffset}|{endNodePath.toString()}|{endOffset}"
	 * @return string
	 * @throws 'No range set' When no range has been applied to this instance
	 */
	this.serialize = function() {
	  var sb;
		
		sb =  new goog.string.StringBuffer();
	  if(!this.startNodePath) throw 'No range set';
	  sb.append(this.markId);
	  sb.append('|');
	  sb.append(this.text);
	  sb.append('|');
	  sb.append(this.startNodePath.toString());
	  sb.append('|');
	  sb.append(this.startOffset);
	  sb.append('|');
	  sb.append(this.endNodePath.toString());
	  sb.append('|');
	  sb.append(this.endOffset);
	  return sb.toString();
	}
	
	/**
	 * De-serializes from the given string settting this instance's state.
	 * @param root dom root node (required for a range may be created)
	 * @param str serialized state as a string ascribing to the format defined in
	 *            the serialize() method
	 * @throws 'Invalid serialized token'
	 */
	this.deserialize = function(root, str) {
		var s, sarr;
		
		sarr = str.split('|');
		if(sarr.length != 6) throw 'Invalid serialized token';
		
		// clear state
		this.unhighlight();
		this.markId = null;
		this.text = '';
		this.startNodePath = null;
		this.startOffset = 0;
		this.endNodePath = null;
		this.endOffset = 0;
	
		for(var i=0; i<sarr.length; i++) {
			s = sarr[i];
			switch(i) {
			case 0:
				this.markId = s;
				break;
			case 1:
				this.text = s;
				break;
			case 2:
				this.startNodePath = new NodePath(root);
				this.startNodePath.fromString(s);
				break;
			case 3:
				this.startOffset = parseInt(s);
				break;
			case 4:
				this.endNodePath = new NodePath(root);
				this.endNodePath.fromString(s);
				break;
			case 5:
				this.endOffset = parseInt(s);
				break;
			}
		}
	}

	/**
	 * Applies the given range re-setting this Mark's state
	 */
	this.applyRange = function(range) {
		var root, urange;
		
		try {
			this.validateRange(range);
		} catch(e) {
			clearWindowSelections(range.getWindow(), range.getDocument());
			throw e;
		}
		
		urange = range;
		
		var root = urange.getDocument().body;
		this.text = urange.getText();
		this.startNodePath = nodeGetNodePath(urange.getStartNode(), root);
		this.endNodePath = nodeGetNodePath(urange.getEndNode(), root);
		this.startOffset = urange.getStartOffset();
		this.endOffset = urange.getEndOffset();

		//alert('urange.getWindow(): ' + urange.getWindow() + ', urange.getDocument(): ' + urange.getDocument());
		clearWindowSelections(urange.getWindow(), urange.getDocument());
	}
	
	this.onHover = function (event) {
		// TODO set focus of corres. quote
	}
	
	this.getText = function () {
		return this.text;
	}
	
	this.getStartNode = function() {
		return this.startNodePath == null ? null : this.startNodePath.getNode();
	}

	/*
	this.eq = function (other, noCheckBack) {
		var startsAreEqual = false,
			endsAreEqual = false;

		noCheckBack = noCheckBack || false;

		if (! (other instanceof Mark)) {
			return false;
		}

		//if (this.pageUrl != other.pageUrl) {
			//return false;
		//}

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
	*/
	
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
	
	/**
	 * Appends a highlight span to the member hspans array w/o affecting the DOM.
	 * @param rdoc the document ref of the associated text range 
	 * @return the added span element for convenience
	 */
	this.addHighlightSpan = function(rdoc) {
		var span, index;
		
		index = this.hspans.length;
		
		span = rdoc.createElement('span');
		span.isNotOriginal = true;
		span.className = 'highlight ' + this.markId;
		//span.addEventListener( "mouseover", this.onHover, false);

		this.hspans[index] = span;
		return span;
	}
	
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
		var span, node;

		if(this.hspans) return;	// already highlighted
		
		this.hspans = [];
		
		if(!range) range = this.createRange();

		var startNode = range.getStartNode();
		var startOffset = range.getStartOffset();
		var endNode = range.getEndNode();
		var endOffset = range.getEndOffset();
		
		if(startNode.nodeType != 3 || endNode.nodeType != 3) throw 'start and end nodes must be textual';
		
		var rdoc = range.getDocument();
		
		// array of text nodes to highlight
		var htnodes = [];

	  var tri = new goog.dom.TextRangeIterator(startNode, startOffset, endNode, endOffset, false);
	  
	  while(true) {
	    try {
	      node = tri.next();
	      
	      // startNode
	      if(node == startNode) {
	      	if(node == endNode) {
      			// single text node case
	      		var tlen = node.nodeValue.length;
	      		var skip = false;
	      		if(startOffset > 0) {
	      			node = node.splitText(startOffset);
	      			skip = true; // skip over just split node
	      			endOffset -= startOffset; // adjust the end offset
	      			tlen -= startOffset;
	      		}
      			htnodes[htnodes.length] = node;
      			if(endOffset <= tlen) {
      				node.splitText(endOffset);
      			}
      			if(skip) tri.next();
      			// we're done
      			break;
	      	} else {
      			// different start/end text nodes case
	      		if(startOffset > 0) {
	      			htnodes[htnodes.length] = node.splitText(startOffset);
	      			tri.next(); // skip over just split node
	      		} else {
	      			htnodes[htnodes.length] = node;
	      		}
	      	}
	      }
	      
	      // endNode
	      else if(node == endNode) {
      		var tlen = node.nodeValue.length;
    			if(endOffset <= tlen) {
    				node.splitText(endOffset);
    			}
  				htnodes[htnodes.length] = node;
	      }

	      // middle text node
	      else if(/*node != startNode && node != endNode && */node.nodeType == 3) {
	      	htnodes[htnodes.length] = node;
	      }

	    } catch(e) {
	    	if(e === goog.iter.StopIteration) break;
	    	alert('woops: ' + e);
	    }
	  }
	  
	  // surround the identified text nodes with highlight spans
	  for(var i=0; i<htnodes.length; i++) {
	  	var htnode = htnodes[i];
	  	var parent = node.parentNode;
	  	var pcn = parent.className;
	  	if(pcn && pcn.indexOf('highlight') >= 0 && pcn.indexOf(this.markId) == -1) {
				// existing highlighted text node
				parent.className += (' ' + this.markId);
				this.hspans[this.hspans.length] = span;
			} else {
	  		var span = this.addHighlightSpan(rdoc);
	    	textNodeSurround(htnode, span);
			}
	  }
	}
	
	/**
	 * Un-highlights this mark restoring the dom to its original state prior to
	 * any highlighting for this mark.
	 */
	this.unhighlight = function() {
	  var span;
		
		if(!this.hspans) return; // not selected
		
		var reg = /mark_*/g;
		
	  for(var i = 0; i < this.hspans.length; i++) {
		  span = this.hspans[i];
	  	// if this span isn't serving any other highlights, promote its children and remove it
		  // otherwise, just remove the mark id from the span's class name
		  var matches = span.className.match(reg);
		  if(matches.length > 1) {
		  	span.className.replace(this.markId,'');
		  } else {
			  nodePromoteChildren(span);
		  }
	  }
	  
	  this.hspans = null;
	}
	
	if(arguments) {
		if(arguments.length == 1) {
			// from range
			var arg0 = arguments[0];
			this.applyRange(arg0);
		} else if(arguments.length == 2) {
			// from serialized token
			var arg0 = arguments[0];
			var arg1 = arguments[1];
			this.deserialize(arg0, arg1);
		}
	}
} // Mark constructor
