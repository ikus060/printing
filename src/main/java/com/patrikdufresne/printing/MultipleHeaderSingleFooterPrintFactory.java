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

import org.eclipse.nebula.paperclips.core.page.PageDecoration;
import org.eclipse.nebula.paperclips.core.page.PageNumber;
import org.eclipse.nebula.paperclips.core.page.PagePrint;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.SeriesPrint;

/**
 * This implementation of {@link IPrintFactory} allows to create a different
 * header for the same document bu only allows a single footer. The use case is
 * a header with the title and a footer with the page number.
 * 
 * @author Patrik Dufresne
 * 
 */
public class MultipleHeaderSingleFooterPrintFactory extends PrintFactory {

    /**
     * Instance of this class represent a section
     * 
     * @author Patrik Dufresne
     * 
     */
    public interface HeaderSection {

        public Print createHeaderArea(PageNumber pageNumber);

        public Print createBodyArea();

    }

    private SeriesPrint series;

    /**
     * Create a new print factory.
     * 
     * @param jobName
     */
    public MultipleHeaderSingleFooterPrintFactory(String jobName) {
        super(jobName);
    }

    /**
     * This implementation create the required PagePrint to support multiple
     * header and a single footer.
     */
    @Override
    public Print createPrint() {
        // Create the series print to contains the sections.
        this.series = new SeriesPrint();

        createSections();

        Print footerPagePrint = new PagePrint(null, this.series, new PageDecoration() {
            @Override
            public Print createPrint(PageNumber pageNumber) {
                return createFooterArea(pageNumber);
            }
        });

        return footerPagePrint;
    }

    /**
     * Sub-classes may implement this function to create a new footer
     * 
     * @param pageNumber
     *            the page number
     * @return the footer print
     */
    protected Print createFooterArea(PageNumber pageNumber) {
        return null;
    }

    /**
     * This function is called to add a new section to this print.
     * 
     * @param section
     *            The section
     */
    protected void addSection(final HeaderSection section) {

        PagePrint pagePrint = new PagePrint(new PageDecoration() {
            @Override
            public Print createPrint(PageNumber pageNumber) {
                return section.createHeaderArea(pageNumber);
            }
        }, section.createBodyArea(), null);
        this.series.add(pagePrint);

    }

    /**
     * This function is intended to be implemented by subclasses to create the
     * section of this print. Sub-classes should call the function addSection to
     * populate the print.
     */
    protected void createSections() {
        // Nothing to do
    }

}
