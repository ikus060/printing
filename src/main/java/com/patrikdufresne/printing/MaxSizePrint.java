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

import org.eclipse.nebula.paperclips.core.CompositeEntry;
import org.eclipse.nebula.paperclips.core.CompositePiece;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * 
 */
public class MaxSizePrint implements Print {

    final Print target;
    final int width;
    final int height;

    /**
     * Constructs a new OffsetPrint.
     * 
     * @param target
     *            the print being max with a sized.
     * @param width
     *            the maximum width or SWT.DEFAULT
     * @param height
     *            the maximum height or SWT.DEFAULT
     */
    public MaxSizePrint(Print target, int width, int height) {
        Util.notNull(target);
        this.target = target;
        this.width = width;
        this.height = height;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + width;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + height;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MaxSizePrint other = (MaxSizePrint) obj;
        if (height != other.height) return false;
        if (target == null) {
            if (other.target != null) return false;
        } else if (!target.equals(other.target)) return false;
        if (width != other.width) return false;
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
     * Returns a Point with the x and y fields set to the width and height, respectively.
     * 
     * @return
     */
    public Point getSize() {
        return new Point(width, height);
    }

    public PrintIterator iterator(Device device, GC gc) {
        return new MaxSizeIterator(this, device, gc);
    }
}

class MaxSizeIterator implements PrintIterator {
    private final PrintIterator target;
    private final int maxWidth;
    private final int maxHeight;

    MaxSizeIterator(MaxSizePrint print, Device device, GC gc) {
        this.target = print.target.iterator(device, gc);
        this.maxWidth = Math.round(print.width * device.getDPI().x / 72f);
        this.maxHeight = Math.round(print.height * device.getDPI().y / 72f);
    }

    MaxSizeIterator(MaxSizeIterator that) {
        this.target = that.target.copy();
        this.maxWidth = that.maxWidth;
        this.maxHeight = that.maxHeight;
    }

    public boolean hasNext() {
        return target.hasNext();
    }

    public Point minimumSize() {
        Point size = target.minimumSize();
        int x = maxWidth > 0 ? Math.min(size.x, maxWidth) : size.x;
        int y = maxHeight > 0 ? Math.min(size.y, maxHeight) : size.y;
        return new Point(x, y);
    }

    public Point preferredSize() {
        Point size = target.preferredSize();
        int x = maxWidth > 0 ? Math.min(size.x, maxWidth) : size.x;
        int y = maxHeight > 0 ? Math.min(size.y, maxHeight) : size.y;
        return new Point(x, y);
    }

    public PrintPiece next(int width, int height) {
        int x = maxWidth > 0 ? Math.min(width, maxWidth) : width;
        int y = maxHeight > 0 ? Math.min(height, maxHeight) : height;
        PrintPiece piece = PaperClips.next(target, x, y);
        if (piece == null) return null;

        Point size = piece.getSize();

        CompositeEntry entry = new CompositeEntry(piece, new Point(0, 0));

        return new CompositePiece(new CompositeEntry[] { entry }, size);
    }

    public PrintIterator copy() {
        return new MaxSizeIterator(this);
    }
}
