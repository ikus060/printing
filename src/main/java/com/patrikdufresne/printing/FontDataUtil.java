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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * This utility class provide default fonts data that may be used for printing purpose.
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

    /**
     * Font data for mono space font.
     */
    public static final FontData MONOSPACE;
    static {
        // Use jface implementation to get reference to a monospace font.
        MONOSPACE = new FontData();
        MONOSPACE.setName("courier");
        FontData[] data = JFaceResources.getFontRegistry().getFontData(JFaceResources.TEXT_FONT);
        if (data.length > 0) {
            MONOSPACE.setName(data[0].getName());
        }
    }

    /**
     * Font data for bold mono space font.
     */
    public static final FontData MONOSPACE_BOLD;
    static {
        // Use jface implementation to get reference to a monospace font.
        MONOSPACE_BOLD = new FontData();
        MONOSPACE_BOLD.setStyle(BOLD_SMALL.getStyle() | SWT.BOLD);
        MONOSPACE_BOLD.setName("courier");
        FontData[] data = JFaceResources.getFontRegistry().getFontData(JFaceResources.TEXT_FONT);
        if (data.length > 0) {
            MONOSPACE.setName(data[0].getName());
        }
    }
}
