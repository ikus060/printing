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

/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
import org.eclipse.nebula.paperclips.core.*;

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A wrapper print that aligns its target vertically and/or horizontally. An OffsetPrint is vertically greedy when the
 * vertical alignment is SWT.CENTER or SWT.BOTTOM, and horizontally greedy when the horizontal alignment is SWT.CENTER
 * and SWT.RIGHT.
 * 
 * @author Matthew Hall
 */
public class OffsetPrint implements Print {
    private static final int DEFAULT_HORIZONTAL_ALIGN = SWT.LEFT;
    private static final int DEFAULT_VERTICAL_ALIGN = SWT.TOP;

    final Print target;
    final int hAlign;
    final int vAlign;
    final Point offset;

    /**
     * Constructs a new OffsetPrint.
     * 
     * @param target
     *            the print being aligned.
     * @param hAlign
     *            the horizontal alignment. One of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT.
     * @param vAlign
     *            the vertical alignment. One of SWT.TOP, SWT.CENTER, SWT.BOTTOM, or SWT.DEFAULT.
     */
    public OffsetPrint(Print target, int hAlign, int vAlign, Point offset) {
        Util.notNull(target);
        this.target = target;
        this.hAlign = checkHOffset(hAlign);
        this.vAlign = checkVOffset(vAlign);
        this.offset = checkOffset(offset);
    }

    /**
     * Constructs a new OffsetPrint.
     * 
     * @param target
     *            the print being aligned.
     * @param hAlign
     *            the horizontal alignment. One of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT.
     * @param vAlign
     *            the vertical alignment. One of SWT.TOP, SWT.CENTER, SWT.BOTTOM, or SWT.DEFAULT.
     * @param hOffset
     *            the horizontal offset
     * @param vOffset
     *            the vertical offset.
     */
    public OffsetPrint(Print target, int hAlign, int vAlign, int hOffset, int vOffset) {
        this(target, hAlign, vAlign, new Point(hOffset, vOffset));
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hAlign;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        result = prime * result + vAlign;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        OffsetPrint other = (OffsetPrint) obj;
        if (hAlign != other.hAlign) return false;
        if (target == null) {
            if (other.target != null) return false;
        } else if (!target.equals(other.target)) return false;
        if (offset == null) {
            if (other.offset != null) return false;
        } else if (!offset.equals(other.offset)) return false;
        if (vAlign != other.vAlign) return false;
        return true;
    }

    /**
     * Returns the wrapped print being aligned
     * 
     * @return the wrapped print being aligned
     */
    public Print getTarget() {
        return target;
    }

    /**
     * Returns a Point with the x and y fields set to the horizontal and vertical alignment, respectively.
     * 
     * @return a Point with the x and y fields set to the horizontal and vertical alignment, respectively.
     */
    public Point getOffsetment() {
        return new Point(hAlign, vAlign);
    }

    private static int checkHOffset(int hOffset) {
        if (hOffset == SWT.LEFT || hOffset == SWT.CENTER || hOffset == SWT.RIGHT) return hOffset;
        if (hOffset == SWT.DEFAULT) return DEFAULT_HORIZONTAL_ALIGN;
        PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "hOffset must be one of SWT.LEFT, SWT.CENTER or SWT.RIGHT"); //$NON-NLS-1$
        return hOffset;
    }

    private static int checkVOffset(int vOffset) {
        if (vOffset == SWT.TOP || vOffset == SWT.CENTER || vOffset == SWT.BOTTOM) return vOffset;
        if (vOffset == SWT.DEFAULT) return DEFAULT_VERTICAL_ALIGN;
        PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "vOffset must be one of SWT.TOP, SWT.CENTER or SWT.BOTTOM"); //$NON-NLS-1$
        return vOffset;
    }

    private static Point checkOffset(Point offset) {
        if (offset.x < 0 || offset.y < 0) {
            PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "offset must be one greater then 0");
        }
        return offset;
    }

    public PrintIterator iterator(Device device, GC gc) {
        return new OffsetIterator(this, device, gc);
    }
}

class OffsetIterator implements PrintIterator {
    private final PrintIterator target;
    private final int hAlign;
    private final int vAlign;
    private final Point offset;

    OffsetIterator(OffsetPrint print, Device device, GC gc) {
        this.target = print.target.iterator(device, gc);
        this.hAlign = print.hAlign;
        this.vAlign = print.vAlign;
        this.offset = new Point(Math.round(print.offset.x * device.getDPI().x / 72f), Math.round(print.offset.y * device.getDPI().y / 72f));
    }

    OffsetIterator(OffsetIterator that) {
        this.target = that.target.copy();
        this.hAlign = that.hAlign;
        this.vAlign = that.vAlign;
        this.offset = that.offset;
    }

    public boolean hasNext() {
        return target.hasNext();
    }

    public Point minimumSize() {
        Point size = target.minimumSize();
        int x = size.x + this.offset.x;
        int y = size.y + this.offset.y;
        return new Point(x, y);
    }

    public Point preferredSize() {
        Point size = target.preferredSize();
        int x = size.x + this.offset.x;
        int y = size.y + this.offset.y;
        return new Point(x, y);
    }

    public PrintPiece next(int width, int height) {
        PrintPiece piece = PaperClips.next(target, width - this.offset.x, height - this.offset.y);
        if (piece == null) return null;

        Point size = piece.getSize();
        Point offset = new Point(this.offset.x, this.offset.y);

        if (hAlign == SWT.CENTER) offset.x = (width - size.x) / 2;
        else if (hAlign == SWT.RIGHT) offset.x = width - size.x - this.offset.x;

        if (hAlign != SWT.LEFT) size.x = width;

        if (vAlign == SWT.CENTER) offset.y = (height - size.y) / 2;
        else if (vAlign == SWT.BOTTOM) offset.y = height - size.y - this.offset.y;

        if (vAlign != SWT.TOP) size.y = height;

        CompositeEntry entry = new CompositeEntry(piece, offset);

        return new CompositePiece(new CompositeEntry[] { entry }, size);
    }

    public PrintIterator copy() {
        return new OffsetIterator(this);
    }
}
