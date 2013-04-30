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
 * 
 * 
 * @author Patrik Dufresne
 */
public class PaddingPrint implements Print {
    final Print target;
    final Rectangle padding;

    /**
     * Constructs a new PaddingPrint.
     * 
     * @param target
     *            the print being aligned.
     * @param padding
     *            the padding.
     */
    public PaddingPrint(Print target, Rectangle padding) {
        Util.notNull(target);
        this.target = target;
        this.padding = checkPadding(padding);
    }

    /**
     * Constructs a new PaddingPrint.
     * 
     * @param target
     *            the print being aligned.
     * @param hPadding
     *            the horizontal offset
     * @param vPadding
     *            the vertical offset.
     */
    public PaddingPrint(Print target, int hPadding, int vPadding) {
        this(target, new Rectangle(hPadding, vPadding, hPadding, vPadding));
    }

    /**
     * Constructs a new PaddingPrint.
     * 
     * @param target
     *            the print being padded.
     * @param hAlign
     * @param vAlign
     * @param leftPadding
     * @param rightPadding
     * @param topPadding
     * @param bottomPadding
     */
    public PaddingPrint(Print target, int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this(target, new Rectangle(leftPadding, topPadding, rightPadding, bottomPadding));
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((padding == null) ? 0 : padding.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PaddingPrint other = (PaddingPrint) obj;
        if (target == null) {
            if (other.target != null) return false;
        } else if (!target.equals(other.target)) return false;
        if (padding == null) {
            if (other.padding != null) return false;
        } else if (!padding.equals(other.padding)) return false;
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

    private static Rectangle checkPadding(Rectangle padding) {
        if (padding.x < 0 || padding.y < 0 || padding.width < 0 || padding.height < 0) {
            PaperClips.error(SWT.ERROR_INVALID_ARGUMENT, "padding must be one greater then 0");
        }
        return padding;
    }

    public PrintIterator iterator(Device device, GC gc) {
        return new PaddingIterator(this, device, gc);
    }
}

class PaddingIterator implements PrintIterator {
    private final PrintIterator target;
    private final Rectangle padding;

    PaddingIterator(PaddingPrint print, Device device, GC gc) {
        this.target = print.target.iterator(device, gc);
        int xPadding = Math.round(print.padding.x * device.getDPI().x / 72f);
        int yPadding = Math.round(print.padding.y * device.getDPI().x / 72f);
        int wPadding = Math.round(print.padding.width * device.getDPI().x / 72f);
        int hPadding = Math.round(print.padding.height * device.getDPI().x / 72f);
        this.padding = new Rectangle(xPadding, yPadding, wPadding, hPadding);
    }

    PaddingIterator(PaddingIterator that) {
        this.target = that.target.copy();
        this.padding = that.padding;
    }

    public boolean hasNext() {
        return target.hasNext();
    }

    public Point minimumSize() {
        Point size = target.minimumSize();
        int x = size.x + this.padding.x + this.padding.width;
        int y = size.y + this.padding.y + this.padding.height;
        return new Point(x, y);
    }

    public Point preferredSize() {
        Point size = target.preferredSize();
        int x = size.x + this.padding.x + this.padding.width;
        int y = size.y + this.padding.y + this.padding.height;
        return new Point(x, y);
    }

    public PrintPiece next(int width, int height) {
        PrintPiece piece = PaperClips.next(target, width - this.padding.x - this.padding.width, height - this.padding.y - this.padding.height);
        if (piece == null) return null;

        Point size = piece.getSize();
        Point offset = new Point(this.padding.x, this.padding.y);

        CompositeEntry entry = new CompositeEntry(piece, offset);

        return new CompositePiece(new CompositeEntry[] { entry }, size);
    }

    public PrintIterator copy() {
        return new PaddingIterator(this);
    }
}
