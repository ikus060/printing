/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.printing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;

import net.sf.paperclips.CellBackgroundProvider;
import net.sf.paperclips.GridCell;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.Print;

public class BackgroundProvider implements CellBackgroundProvider {

	private Map<Print, RGB> table;

	private GridPrint grid;

	public BackgroundProvider(GridPrint grid) {
		if (grid == null) {
			throw new NullPointerException();
		}
		this.grid = grid;
	}

	@Override
	public RGB getCellBackground(int row, int column, int colspan) {
		if (this.table == null) {
			return null;
		}
		GridCell[][] cells = this.grid.getBodyCells();
		if (cells.length < row || cells[row].length < column) {
			return null;
		}

		// Lookup the table.
		return this.table.get(cells[row][column].getContent());

	}

	/**
	 * Sets the color for the cell specified.
	 * 
	 * @param cell
	 *            the cell
	 * @param color
	 *            the color or null to unset.
	 */
	public void setBackground(Print cell, RGB color) {
		if (this.table == null) {
			this.table = new HashMap<Print, RGB>();
		}
		if (color != null) {
			this.table.put(cell, color);
		} else {
			this.table.remove(cell);
		}
	}

	/**
	 * Return the color associated with the cell specified.
	 * 
	 * @param cell
	 *            the cell
	 * @return the color or null is not set.
	 */
	public RGB getBackground(Print cell) {
		if (this.table == null) {
			return null;
		}
		return this.table.get(cell);
	}

}
