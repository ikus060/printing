package com.patrikdufresne.printing;

import net.sf.paperclips.Margins;
import net.sf.paperclips.PageDecoration;
import net.sf.paperclips.PageNumber;
import net.sf.paperclips.PagePrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;

/**
 * This class is used to easily implement a printing.
 * <p>
 * A PrintFactory should create a Print object. It also provide all the settings
 * required to create a PrintJob object : name, margins and orientation.
 * 
 * @author patapouf
 * 
 */
public abstract class AbstractPrintFactory implements IPrintFactory {
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
	 * The print time. Is resuexd on every pages.
	 */
	protected String printTime;

	/**
	 * Create a new Print factory.
	 * 
	 * @param jobName
	 *            the default job's name
	 */
	public AbstractPrintFactory(String jobName) {
		this.jobName = jobName;
		setMargins(new Margins(31));
	}

	/**
	 * Sub-Class should the body content.
	 * 
	 * @return a new Print object
	 */
	protected abstract Print createBodyPrint();

	/**
	 * This function creates the print for the footer of every pages. this
	 * implementation add the current date and the page number.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @return the print or null.
	 */
	protected Print createFooterPrint(PageNumber pageNumber) {
		return null;
	}

	/**
	 * This function create the print for the header of every pages. This
	 * implementation return null, so there is no header for the page.
	 * <p>
	 * Sub-classes may override this function by creating a print.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @return the print decorating the given page number, or null
	 */
	protected Print createHeaderPrint(PageNumber pageNumber) {
		return null;
	}

	/**
	 * This function create the footer content.
	 * 
	 * @return a Page Decorator
	 */
	private PageDecoration createPageDecorationFooter() {
		return new PageDecoration() {
			@Override
			public Print createPrint(PageNumber pageNumber) {
				return createFooterPrint(pageNumber);
			}
		};
	}

	/**
	 * Sub-class may implementation this function to return a PageDecorator. By
	 * default this function return null.
	 * 
	 * @return a PageDecoration or null
	 */
	private PageDecoration createPageDecorationHeader() {
		return new PageDecoration() {
			@Override
			public Print createPrint(PageNumber pageNumber) {
				return createHeaderPrint(pageNumber);
			}
		};
	}

	/**
	 * This implementation provide an easy way to create Print object.
	 * 
	 * @param monitor
	 *            a progress monitor or null.
	 * @return a Print object
	 */
	@Override
	public Print createPrint() {
		synchronized (this) {
			PageDecoration header = createPageDecorationHeader();
			Print body = createBodyPrint();
			PageDecoration footer = createPageDecorationFooter();
			Print print = new PagePrint(header, body, footer);
			return print;
		}
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
