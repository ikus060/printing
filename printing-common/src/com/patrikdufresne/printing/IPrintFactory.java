package com.patrikdufresne.printing;

import net.sf.paperclips.Margins;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;

/**
 * Class implementing this interface provide a way to create Print object.
 * Sub-class may alternate the creation of the Print by providing l
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IPrintFactory {

	/**
	 * Sub-class must return the job name.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Sub-class may create a print.
	 * 
	 * @return a Print object
	 */
	Print createPrint();

	/**
	 * Returns the page margins, expressed in points. 72 points = 1".
	 * 
	 * @return the page margins, expressed in points. 72 points = 1".
	 */
	Margins getMargins();

	/**
	 * Returns the page orientation. One of the constants :
	 * {@link PaperClips#ORIENTATION_DEFAULT },
	 * {@link PaperClips#ORIENTATION_PORTRAIT } or
	 * {@link PaperClips#ORIENTATION_LANDSCAPE }
	 * 
	 * @return the page orientation.
	 */
	int getOrientation();

}
