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

var mcounter = 0;

/**
 * Mark - constructor
 * 
 * @param range goog.dom.AbstractRange
 * 
 * @throws 'No text selected'
 * @throws 'Non-textual range'
 * @throws 'Unsupported range type'
 */
function Mark(range) {

	this.markId = 'mark_' + (++mcounter);
	this.text = '';
	this.startNodePath = null;
	this.startOffset = 0;
	this.endNodePath = null;
	this.endOffset = 0;
	
	/* 
	 * the highlight spans
	 * FORMAT:
	 * 	 id="{markId}_{span index}"
	 *   class="highlight {markId}"
	 */
	this.hspan = null;

	this.onHover = function (event) {
		// TODO set focus of corres. quote
	}
	
	this.toString = function () {
		return this.text;
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
	 * Appends a highlight span to the member hspan array.
	 * @param rdoc the document ref of the associated text range 
	 * @return the added span element for convenience
	 */
	this.addHighlightSpan = function(rdoc) {
		var span, index;
		
		if(!this.hspan) this.hspan = [];
		index = this.hspan.length;
		
		span = rdoc.createElement('span');
		span.isNotOriginal = true;
		span.id = this.markId + '_' + index;
		span.className = 'highlight ' + this.markId;
		//span.addEventListener( "mouseover", this.onHover, false);
		
		this.hspan[index] = span;
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

		if(this.hspan) return;	// already highlighted
		
		if(!range) range = this.createRange();

		/*
		try {
			range.surroundContents(this.hspan);
		} catch(e) {
			this.hspan = null;
			throw e;
		}
		*/
		
		var startNode = range.getStartNode();
		var startOffset = range.getStartOffset();
		var endNode = range.getEndNode();
		var endOffset = range.getEndOffset();
		
		if(startNode.nodeType != 3 || endNode.nodeType != 3) throw 'start and end nodes must be textual';
		
		var rdoc = range.getDocument();

	  var tri = new goog.dom.TextRangeIterator(startNode, startOffset, endNode, endOffset, false);
	  while(true) {
	    try {
	      node = tri.next();
	      
	      // startNode
	      if(node == startNode) {
	      	var prefixTextNode = null;
      		
	      	// split the text node at the offset to allow for insertion of highlight span
      		if(startOffset > 0) {
		      	var prefixText = node.nodeValue.substr(0, startOffset);
	      		prefixTextNode = rdoc.createTextNode(prefixText);
      		}
    			
      		if(node == endNode) {
      			var tlen = node.nodeValue.length;
      			var suffixTextNode = null;
      			if(endOffset <= tlen) {
	      			var suffixText = node.nodeValue.substr(endOffset+1);
		      		suffixTextNode = rdoc.createTextNode(suffixText);
      			}
        		if(prefixTextNode != null) {
  	      		node.parentNode.insertBefore(prefixTextNode, node);
        		}
        		if(suffixTextNode != null) {
        			node.parentNode.insertBefore(suffixTextNode, node.nextSibling);
        		}
        		span = this.addHighlightSpan(rdoc);
	      		node.nodeValue = node.nodeValue.substr(startOffset, endOffset - startOffset);
        		textNodeSurround(node, span);
	      		break;	// we're done
      		} else {
	      		node.nodeValue = node.nodeValue.substr(startOffset);
      		}
	      }
	      
	      // endNode
	      if(node == endNode) {
	      	var textlen = node.nodeValue.length;
	      	if(endOffset < textlen) {
	      		// split the text node at the offset to allow for insertion of highlight span
	      		var suffixText = node.nodeValue.substr(endOffset+1);
	      		var suffixTextNode = rdoc.createTextNode(suffixText);
	      		node.parentNode.insertBefore(suffixTextNode, node.nextSibling);
	      		node.nodeValue = node.nodeValue.substr(endOffset);
	      	}
	      	span = this.addHighlightSpan(rdoc);
      		textNodeSurround(node, span);
	      }

	      // middle text node
	      if(node != startNode && node != endNode && node.nodeType == 3) {
	      	// surround the text node with a highlight span
	      	span = this.addHighlightSpan(rdoc);
	      	//alert('span: ' + span);
	      	textNodeSurround(node, span);
	      }
	      
	    } catch(e) {
	    	if(e === goog.iter.StopIteration) break;
	    	alert('woops: ' + e);
	    }
	  }
	}
	
	/**
	 * Un-highlights this mark restoring the dom to its original state prior to
	 * any highlighting for this mark.
	 */
	this.unhighlight = function() {
	  var span;
		
		if(!this.hspan) return; // not selected
		
	  for(var i = 0; i < this.hspan.length; i++) {
		  span = this.hspan[i];
	  	nodePromoteChildren(span);
	  }
	  
	  this.hspan = null;
	}
	
	var root, urange;
	
	this.validateRange(range);
	urange = range;
	
	var root = urange.getDocument().body;
	this.text = urange.getText();
	this.startNodePath = nodeGetNodePath(urange.getStartNode(), root);
	this.endNodePath = nodeGetNodePath(urange.getEndNode(), root);
	this.startOffset = urange.getStartOffset();
	this.endOffset = urange.getEndOffset();

	//this.highlight(urange);
	
	//alert('urange.getWindow(): ' + urange.getWindow() + ', urange.getDocument(): ' + urange.getDocument());
	clearWindowSelections(urange.getWindow(), urange.getDocument());
} // Mark constructor