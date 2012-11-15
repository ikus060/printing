/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.printing;

import net.sf.paperclips.EmptyPrint;
import net.sf.paperclips.LayerPrint;
import net.sf.paperclips.Print;

/**
 * This print is used to define a minimum height and/or width.
 * 
 * @author Patrik Dufresne
 * 
 */
public class MinSizePrint extends LayerPrint {

	/**
	 * This function throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public void add(Print print) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This function throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public void add(Print print, int align) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a print.
	 * 
	 * @param target
	 *            the target print
	 * @param width
	 *            minimum width of the Print, in points (72pts = 1").
	 * @param height
	 *            minimum height of the Print, in points (72pts = 1").
	 */
	public MinSizePrint(Print target, int width, int height) {
		super.add(target);
		super.add(new EmptyPrint(width, height));
	}
}
