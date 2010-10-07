/**
 * The Logic Lab
 * @author jpk
 * @since Feb 17, 2010
 */
package com.tabulaw.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Poc app resources.
 * @author jpk
 */
public interface Resources extends ClientBundle {

	Resources INSTANCE = GWT.create(Resources.class);

	@Source("../../public/images/trash.gif")
	ImageResource trash();

	@Source("../../public/images/word-16.gif")
	ImageResource msword();

	@Source("../../public/images/magnifying_glass_alt_24x24.png")
	ImageResource magnifyingGlass();

	@Source("../../public/images/google-scholar.png")
	ImageResource googleScholarLogo();

	@Source("../../public/images/go-to-highlight2.png")
	ImageResource gotoHighlight();

	@Source("../../public/images/note-icon.png")
	ImageResource searchImage();

	@Source("../../public/images/edit-icon.png")
	ImageResource edit();
	
	//@Source("../../public/images/delete-button.png")
	//ImageResource delete();

	//@Source("../../public/images/delete-button-larger.png")
	//ImageResource deleteLarger();
	
	@Source("../../public/images/x-button.png")
	ImageResource XButton();

	@Source("../../public/images/document-icon.png")
	ImageResource documentIcon();

	@Source("../../public/images/quote-bundle-icon.png")
	ImageResource quoteBundleIcon();
	
	@Source("../../public/images/plus.png")
	ImageResource plus();

	@Source("../../public/images/import-button.png")
	ImageResource importButton();

	@Source("../../public/images/upload-button.png")
	ImageResource uploadButton();

	@Source("../../public/images/permalink.gif")
	ImageResource permalink();

//-----Rich text toolbar buttons---// 
	@Source("../../public/images/toolbar/bold.gif")
	ImageResource bold();

	@Source("../../public/images/toolbar/indent.gif")
	ImageResource indent();

	@Source("../../public/images/toolbar/outdent.gif")
	ImageResource outdent();
	
	@Source("../../public/images/toolbar/italic.gif")
	ImageResource italic();

	@Source("../../public/images/toolbar/justifyCenter.gif")
	ImageResource justifyCenter();

	@Source("../../public/images/toolbar/justifyLeft.gif")
	ImageResource justifyLeft();

	@Source("../../public/images/toolbar/justifyRight.gif")
	ImageResource justifyRight();

	@Source("../../public/images/toolbar/subscript.gif")
	ImageResource subscript();

	@Source("../../public/images/toolbar/superscript.gif")
	ImageResource superscript();
	@Source("../../public/images/toolbar/underline.gif")
	ImageResource underline();
	@Source("../../public/images/toolbar/ul.gif")
	ImageResource ul();
	@Source("../../public/images/toolbar/ol.gif")
	ImageResource ol();
	@Source("../../public/images/toolbar/removeFormat.gif")
	ImageResource removeFormat();
}
