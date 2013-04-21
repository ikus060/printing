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

import net.sf.paperclips.Margins;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;

/**
 * Class implementing this interface provide a way to create Print object.
 * Sub-class may alternate the creation of the Print by providing l
 * 
 * @author Patrik Dufresne
 * 
 */
public interface IPrintFactory {

    /**
     * Sub-class must return the job name.
     * 
     * @return
     */
    String getName();

    /**
     * Sub-class may create a print.
     * 
     * @return a Print object
     */
    Print createPrint();

    /**
     * Returns the page margins, expressed in points. 72 points = 1".
     * 
     * @return the page margins, expressed in points. 72 points = 1".
     */
    Margins getMargins();

    /**
     * Returns the page orientation. One of the constants :
     * {@link PaperClips#ORIENTATION_DEFAULT },
     * {@link PaperClips#ORIENTATION_PORTRAIT } or
     * {@link PaperClips#ORIENTATION_LANDSCAPE }
     * 
     * @return the page orientation.
     */
    int getOrientation();

}
