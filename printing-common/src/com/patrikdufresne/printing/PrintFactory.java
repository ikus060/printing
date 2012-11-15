/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

