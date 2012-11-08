/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.printing;

import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.Print;

//TODO Comments this class 
public class PaddingHelper {

	public static Print create(Print print, int top, int left, int bottom,
			int right) {
		GridPrint spacerPrint = new GridPrint();
		DefaultGridLook look = new DefaultGridLook();
		look.setCellPadding(left, top, right, bottom);
		spacerPrint.setLook(look);
		spacerPrint.addColumn("l:d:g"); //$NON-NLS-1$
		spacerPrint.add(print);
		return spacerPrint;
	}

	public static Print create(Print print, int topBottom, int leftRight) {
		return create(print, topBottom, leftRight, topBottom, leftRight);
	}
	
	public static Print create(Print print, int padding) {
		return create(print, padding, padding, padding, padding);
	}

}
