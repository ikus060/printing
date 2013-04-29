/**
 * Copyright(C) 2013 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.patrikdufresne.printing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;

import org.eclipse.nebula.paperclips.core.grid.CellBackgroundProvider;
import org.eclipse.nebula.paperclips.core.grid.GridCell;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.Print;

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
