package com.patrikdufresne.printing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * This utility class provide default fonts data that may be used for printing
 * purpose.
 * 
 * @author Patrik Dufresne
 * 
 */
public class FontDataUtil {

	/**
	 * The default font data.
	 */
	public static final FontData DEFAULT;
	static {
		DEFAULT = new FontData();
		DEFAULT.height = 10;
	}

	/**
	 * Smaller font.
	 */
	public static final FontData SMALL;
	static {
		SMALL = new FontData();
		SMALL.height = 8;
	}

	/**
	 * The bold font data.
	 */
	public static final FontData BOLD;
	static {
		BOLD = new FontData();
		BOLD.setStyle(BOLD.getStyle() | SWT.BOLD);
		BOLD.height = 10;
	}

	/**
	 * Smaller bold fount
	 */
	public static final FontData BOLD_SMALL;
	static {
		BOLD_SMALL = new FontData();
		BOLD_SMALL.setStyle(BOLD_SMALL.getStyle() | SWT.BOLD);
		BOLD_SMALL.height = 8;
	}

	/**
	 * The header 1 font data.
	 */
	public static final FontData HEADER1;
	static {
		HEADER1 = new FontData();
		HEADER1.height = 16;
	}

	/**
	 * The header 2 font data.
	 */
	public static final FontData HEADER2;
	static {
		HEADER2 = new FontData();
		HEADER2.height = 12;
	}

}
