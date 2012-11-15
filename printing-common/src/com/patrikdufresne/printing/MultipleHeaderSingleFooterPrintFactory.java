/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.patrikdufresne.printing;

import net.sf.paperclips.PageDecoration;
import net.sf.paperclips.PageNumber;
import net.sf.paperclips.PagePrint;
import net.sf.paperclips.Print;
import net.sf.paperclips.SeriesPrint;

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

		Print footerPagePrint = new PagePrint(null, this.series,
				new PageDecoration() {
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
