/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.printing;

import net.sf.paperclips.Border;
import net.sf.paperclips.BorderPrint;
import net.sf.paperclips.GapBorder;
import net.sf.paperclips.Print;

/**
 * This print create gap around a print. This can be seans as a margin.
 * 
 * @author Patrik Dufresne
 * 
 */
public class GapPrint extends BorderPrint {

	public GapPrint(Print target, int value) {
		this(target, value, value, value, value);
	}

	public GapPrint(Print target, int vertical, int horizontal) {
		this(target, vertical, horizontal, vertical, horizontal);
	}

	public GapPrint(Print target, int top, int right, int bottom, int left) {
		super(target, createBorder(top, right, bottom, left));
	}

	private static Border createBorder(int top, int right, int bottom, int left) {
		GapBorder border = new GapBorder();
		border.top = top;
		border.right = right;
		border.bottom = bottom;
		border.left = left;
		return border;
	}

}
