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

/**
 * This class is used to easily implement a printing.
 * <p>
 * A PrintFactory should create a Print object. It also provide all the settings
 * required to create a PrintJob object : name, margins and orientation.
 * 
 * @author patapouf
 * 
 */
public abstract class PrintFactory implements IPrintFactory {
    /**
     * The job's name.
     */
    private String jobName;
    /**
     * Hold the margin value.
     */
    private Margins margins = new Margins();

    /**
     * Hold the orientation value.
     */
    private int orientation;

    /**
     * Create a new Print factory.
     * 
     * @param jobName
     *            the default job's name
     */
    public PrintFactory(String jobName) {
        this.jobName = jobName;
        setMargins(new Margins(31));
    }

    /**
     * This implementation return the margin size.
     */
    @Override
    public Margins getMargins() {
        return this.margins;
    }

    /**
     * This implementation return the job's name.
     */
    @Override
    public String getName() {
        return this.jobName;
    }

    /**
     * This implementation return the orientation.
     */
    @Override
    public int getOrientation() {
        return this.orientation;
    }

    /**
     * Sets the name of the print job, which will appear in the print queue of
     * the operating system.
     * 
     * @param jobName
     *            a name
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Sets the top, left, right, and bottom margins to the argument.
     * 
     * @param margins
     *            the margins, in points. 72 points = 1 inch.
     * @return this PrintJob (for chaining method calls)
     */
    public void setMargins(Margins margins) {
        if (margins == null) {
            throw new NullPointerException();
        }
        this.margins = margins;
    }

    /**
     * Sets the page orientation.
     * 
     * @param orientation
     *            the page orientation. Must be one of
     *            {@link PaperClips#ORIENTATION_DEFAULT },
     *            {@link PaperClips#ORIENTATION_PORTRAIT } or
     *            {@link PaperClips#ORIENTATION_LANDSCAPE }. Values other than
     *            these choices will be automatically changed to
     *            {@link PaperClips#ORIENTATION_DEFAULT }.
     * @return this PrintJob (for chaining method calls)
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

}
