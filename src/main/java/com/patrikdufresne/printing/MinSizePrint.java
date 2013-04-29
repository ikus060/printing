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

import org.eclipse.nebula.paperclips.core.EmptyPrint;
import org.eclipse.nebula.paperclips.core.LayerPrint;
import org.eclipse.nebula.paperclips.core.Print;

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
