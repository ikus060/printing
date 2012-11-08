package com.patrikdufresne.printing;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.ui.PrintPreview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.patrikdufresne.util.Localized;

/**
 * Instances of this class allow the user to preview the Print object before
 * sending it to the printer. It also provide an easy way to change the Print
 * setting.
 * 
 */
public class PrintPreviewDialog extends Dialog {

	/**
	 * Static function to return a printer data.
	 * 
	 * @return
	 */
	private static PrinterData getDefaultPrinterData() {
		// Try to get the default printer
		PrinterData data = Printer.getDefaultPrinterData();
		if (data != null) {
			return data;
		}

		PrinterData datas[] = Printer.getPrinterList();
		if (datas != null && datas.length > 0) {
			return datas[0];
		}

		return null;
	}

	/**
	 * The PrinterData used to print the job
	 */
	protected PrinterData data;
	/**
	 * The PrintJob to preview
	 */
	protected PrintJob job;

	/**
	 * Used for localization.
	 */
	private Localized localized = Localized.load(PrintPreviewDialog.class);
	/**
	 * Action to preview next page.
	 */
	private Action nextPage;
	protected Label pageNumber;

	/**
	 * Item containing the pageNumber Label.
	 */
	ToolItem pageNumberToolItem;

	/**
	 * Composite displaying the preview of a page.
	 */
	protected PrintPreview preview;

	/**
	 * Action to preview previous page.
	 */
	private Action previousPage;

	private ScrolledComposite scroll;

	private double[] scrollingPosition;

	private ToolBarManager toolbarManager;

	/**
	 * Create a new Print Preview Dialog
	 * 
	 * @param shell
	 *            the parent shell
	 */
	public PrintPreviewDialog(IShellProvider shell) {
		super(shell);

		this.data = getDefaultPrinterData();
	}

	/**
	 * Create a new Print Preview Dialog
	 * 
	 * @param shell
	 *            the parent shell
	 */
	public PrintPreviewDialog(Shell shell) {
		super(shell);
		this.data = getDefaultPrinterData();
	}

	/**
	 * Clear the current scrolling position.
	 */
	protected void clearScrollingPosition() {
		this.scrollingPosition = null;
	}

	/**
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(this.localized.format(
				"PrintPreviewDialog.title", (Object) this.job.getName())); //$NON-NLS-1$

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		shell.setLayout(layout);

		// Create toolbar
		Control control = createToolBarControl(shell);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	@Override
	protected Control createContents(Composite parent) {

		// Create Preview area
		Control ctrl = createScrollingPreview(parent);
		ctrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		preview();

		return ctrl;
	}

	protected Control createScrollingPreview(Composite parent) {
		this.scroll = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		this.scroll.setExpandHorizontal(true);
		this.scroll.setExpandVertical(true);

		this.preview = new PrintPreview(this.scroll, SWT.NONE);
		this.scroll.setContent(this.preview);

		this.scroll.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleResizeEvent(event);
			}
		});

		this.preview.setFitVertical(true);
		this.preview.setFitHorizontal(true);

		Listener dragListener = new Listener() {
			private final Point dpi = Display.getCurrent().getDPI();
			private boolean dragging = false;

			private Point dragStartMouseAnchor = null;
			private Point dragStartScrollOrigin = null;
			private boolean scrollable = false;

			private void beginDragging(Event event) {
				dragStartScrollOrigin = scroll.getOrigin();
				dragStartMouseAnchor = preview.toDisplay(event.x, event.y);
				dragging = true;
			}

			private void endDragging() {
				dragging = false;
				dragStartMouseAnchor = null;
				dragStartScrollOrigin = null;
			}

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Resize:
					clearScrollingPosition();
					Rectangle bounds = scroll.getClientArea();
					Point size = preview.getSize();
					scrollable = size.x > bounds.width
							|| size.y > bounds.height;
					if (!scrollable && dragging)
						endDragging();
					break;
				case SWT.MouseDown:
					clearScrollingPosition();
					if (scrollable && event.button == 1)
						beginDragging(event);
					break;
				case SWT.MouseMove:
					if (dragging) {
						clearScrollingPosition();
						Point point = preview.toDisplay(event.x, event.y);
						scroll.setOrigin(dragStartScrollOrigin.x
								+ dragStartMouseAnchor.x - point.x,
								dragStartScrollOrigin.y
										+ dragStartMouseAnchor.y - point.y);
					}
					break;
				case SWT.MouseUp:
					clearScrollingPosition();
					if (dragging)
						endDragging();
					break;
				case SWT.MouseEnter:
					Display.getCurrent().addFilter(SWT.MouseWheel, this);
					break;
				case SWT.MouseWheel:
					if (event.count != 0) {
						if (scrollable
								&& !dragging
								&& (event.stateMask == SWT.NONE || event.stateMask == SWT.SHIFT)) {
							clearScrollingPosition();
							bounds = scroll.getClientArea();
							size = preview.getSize();
							Point origin = scroll.getOrigin();
							int direction = event.count > 0 ? -1 : 1;
							// Prefer vertical scrolling unless user is
							// pressing Shift
							if (size.y > bounds.height
									&& event.stateMask == SWT.NONE)
								origin.y += direction
										* Math.min(dpi.y, bounds.height / 4);
							else if (size.x > bounds.width)
								origin.x += direction
										* Math.min(dpi.x, bounds.width / 4);
							scroll.setOrigin(origin);
							event.doit = false;
						} else if (event.stateMask == SWT.CTRL) { // Ctrl+MouseWheel
							// ->
							// zoom
							float scale = preview.getAbsoluteScale();
							setPreviewScale(event.count < 0 ? scale / 1.1f
									: scale * 1.1f);
						}
					}
					break;
				case SWT.MouseExit:
					Display.getCurrent().removeFilter(SWT.MouseWheel, this);
					break;
				}
			}
		};

		scroll.addListener(SWT.Resize, dragListener);
		preview.addListener(SWT.MouseDown, dragListener);
		preview.addListener(SWT.MouseMove, dragListener);
		preview.addListener(SWT.MouseUp, dragListener);

		// These are for mouse wheel handling
		preview.addListener(SWT.MouseEnter, dragListener);
		preview.addListener(SWT.MouseExit, dragListener);

		return scroll;
	}

	/**
	 * Creates the control for the tool bar manager.
	 * <p>
	 * Subclasses may override this method to customize the tool bar manager.
	 * </p>
	 * 
	 * @param parent
	 *            the parent used for the control
	 * @return a Control
	 */
	protected Control createToolBarControl(Composite parent) {
		this.toolbarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT
				| SWT.BORDER);
		/*
		 * Previous action
		 */
		this.toolbarManager.add(this.previousPage = new Action() {
			@Override
			public void run() {
				setPreviewPageIndex(preview.getPageIndex()
						- preview.getHorizontalPageCount()
						* preview.getVerticalPageCount());
				updatePreviousNextAction();
			}
		});
		this.previousPage.setText(this.localized
				.get("PrintPreviewDialog.previous")); //$NON-NLS-1$
		this.previousPage.setToolTipText(this.localized
				.get("PrintPreviewDialog.previous.tooltip")); //$NON-NLS-1$

		/*
		 * Next Action
		 */
		this.toolbarManager.add(this.nextPage = new Action() {
			@Override
			public void run() {
				setPreviewPageIndex(preview.getPageIndex()
						+ preview.getHorizontalPageCount()
						* preview.getVerticalPageCount());
				updatePreviousNextAction();
			}
		});
		this.nextPage.setText(this.localized.get("PrintPreviewDialog.next")); //$NON-NLS-1$
		this.nextPage.setToolTipText(this.localized
				.get("PrintPreviewDialog.next.tooltip")); //$NON-NLS-1$

		/*
		 * Page counter : 1 of 10
		 */
		this.toolbarManager.add(new ContributionItem() {
			@Override
			public final void fill(ToolBar t, int index) {
				Composite comp = new Composite(t, SWT.NONE);
				comp.setLayout(new GridLayout());
				pageNumber = new Label(comp, SWT.NONE);
				pageNumber.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
						false, false));
				pageNumberToolItem = new ToolItem(t, SWT.SEPARATOR, index);
				pageNumberToolItem.setControl(comp);
				pageNumberToolItem.setWidth(comp.computeSize(SWT.DEFAULT,
						SWT.DEFAULT).x);
			}
		});

		/*
		 * Separator
		 */
		this.toolbarManager.add(new Separator());

		/*
		 * Zoom best fit
		 */
		Action zoomBestFit;
		toolbarManager.add(zoomBestFit = new Action() {
			@Override
			public void run() {
				preview.setFitVertical(true);
				preview.setFitHorizontal(true);
				rememberScrollingPosition();
				updatePreviewSize();
				restoreScrollingPosition();
			}
		});
		zoomBestFit.setText(this.localized
				.get("PrintPreviewDialog.zoomBestFit")); //$NON-NLS-1$

		/*
		 * Zoom in
		 */
		Action zoomIn;
		this.toolbarManager.add(zoomIn = new Action() {
			@Override
			public void run() {
				setPreviewScale(preview.getAbsoluteScale() * 1.1f);
			}
		});
		zoomIn.setText(this.localized.get("PrintPreviewDialog.zoomIn")); //$NON-NLS-1$

		/*
		 * Zoom out
		 */
		Action zoomOut;
		this.toolbarManager.add(zoomOut = new Action() {
			@Override
			public void run() {
				setPreviewScale(preview.getAbsoluteScale() / 1.1f);
			}
		});
		zoomOut.setText(this.localized.get("PrintPreviewDialog.zoomOut")); //$NON-NLS-1$

		/*
		 * Zoom original
		 */
		Action zoomOriginal;
		this.toolbarManager.add(zoomOriginal = new Action() {
			@Override
			public void run() {
				setPreviewScale(1);
			}
		});
		zoomOriginal.setText(this.localized
				.get("PrintPreviewDialog.zoomOriginal")); //$NON-NLS-1$

		/*
		 * Separator
		 */
		this.toolbarManager.add(new Separator());

		/*
		 * Portrait
		 */
		Action portrait;
		this.toolbarManager.add(portrait = new Action() {
			@Override
			public void run() {
				job.setOrientation(PaperClips.ORIENTATION_PORTRAIT);
				preview.setPrintJob(job);
				clearScrollingPosition();
				updatePreviewSize();
				updatePageNumber();
			}
		});
		portrait.setText(this.localized.get("PrintPreviewDialog.portrait")); //$NON-NLS-1$

		/*
		 * Landscape
		 */
		Action landscape;
		this.toolbarManager.add(landscape = new Action() {
			@Override
			public void run() {
				job.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);
				preview.setPrintJob(job);
				clearScrollingPosition();
				updatePreviewSize();
				updatePageNumber();
			}
		});
		landscape.setText(this.localized.get("PrintPreviewDialog.landscape")); //$NON-NLS-1$

		/*
		 * Separator
		 */
		this.toolbarManager.add(new Separator());

		/*
		 * Print
		 */
		Action print;
		this.toolbarManager.add(print = new Action() {
			@Override
			public void run() {
				PrintDialog dlg = new PrintDialog(PrintPreviewDialog.this
						.getShell());
				dlg.setPrinterData(data);
				PrinterData newData = dlg.open();
				if (newData == null) {
					return;
				}

				PrintPreviewDialog.this.data = newData;
				preview.setPrinterData(data);

				// Send the PrintJob to the printer
				PaperClips.print(job, data);

				// Close the preview
				setReturnCode(Dialog.OK);
				PrintPreviewDialog.this.close();
			}
		});
		print.setText(this.localized.get("PrintPreviewDialog.print")); //$NON-NLS-1$

		/*
		 * Close
		 */
		Action close;
		this.toolbarManager.add(close = new Action() {
			@Override
			public void run() {
				// Cancel
				setReturnCode(Dialog.CANCEL);
				PrintPreviewDialog.this.close();
			}
		});
		close.setText(this.localized.get("PrintPreviewDialog.close")); //$NON-NLS-1$

		ToolBar bar = this.toolbarManager.createControl(parent);
		return bar;
	}

	/**
	 * Returns the PrinterData used to print the job.
	 * 
	 * @return a PrinterData
	 */
	public PrinterData getPrinterData() {
		return this.data;
	}

	/**
	 * Returns the PrintJob to preview.
	 * 
	 * @return a PrintJob or null if not set
	 */
	public PrintJob getPrintJob() {
		return this.job;
	}

	/**
	 * Notify this class about scroll resize.
	 * 
	 * @param event
	 */
	void handleResizeEvent(Event event) {
		Rectangle bounds = this.scroll.getClientArea();

		this.scroll.getHorizontalBar().setPageIncrement(bounds.width * 2 / 3);
		this.scroll.getVerticalBar().setPageIncrement(bounds.height * 2 / 3);

		if (this.preview.isFitHorizontal() ^ this.preview.isFitVertical()) {
			rememberScrollingPosition();
			updatePreviewSize();
			restoreScrollingPosition();
		}
	}

	/**
	 * This implementation return true.
	 * 
	 * @see Dialog
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * @param monitor
	 */
	void preview(/* IProgressMonitor monitor */) {
		// monitor.beginTask(
		//				localized.get("PrintPreviewDialog.task.preparingPreview"), //$NON-NLS-1$
		// IProgressMonitor.UNKNOWN);

		// Set printer data
		preview.setPrinterData(data);

		// Set the print job
		//		monitor.subTask(localized.get("PrintPreviewDialog.task.creatingPages")); //$NON-NLS-1$
		if (job != null) {
			preview.setPrintJob(job);
		}

		updatePageNumber();
		updatePreviousNextAction();

		// Task completed.
		// monitor.done();
	}

	/**
	 * Used to remember the scrolling position
	 */
	protected void rememberScrollingPosition() {
		Point size = this.preview.getSize();
		if (size.x == 0 || size.y == 0) {
			clearScrollingPosition();
		} else if (this.scrollingPosition == null) {
			Point origin = this.scroll.getOrigin();
			this.scrollingPosition = new double[] {
					(double) origin.x / (double) size.x,
					(double) origin.y / (double) size.y };
		}
	}

	protected void restoreScrollingPosition() {
		if (this.scrollingPosition != null) {
			Point size = this.preview.getSize();
			this.scroll.setOrigin(
					(int) Math.round(this.scrollingPosition[0] * size.x),
					(int) Math.round(this.scrollingPosition[1] * size.y));
		}
	}

	protected void setPreviewPageIndex(int pageIndex) {
		this.preview.setPageIndex(Math.max(
				Math.min(pageIndex, this.preview.getPageCount() - 1), 0));
		updatePageNumber();
	}

	protected void setPreviewScale(float scale) {
		this.preview.setFitVertical(false);
		this.preview.setFitHorizontal(false);
		this.preview.setScale(scale);
		rememberScrollingPosition();
		updatePreviewSize();
		restoreScrollingPosition();
	}

	/**
	 * Sets the PrinterData used to print the job.
	 * 
	 * @param data
	 *            a PrinterData
	 */
	public void setPrinterData(PrinterData data) {
		if (data == null) {
			this.data = getDefaultPrinterData();
		} else {
			this.data = data;
		}
	}

	/**
	 * Sets the Print job to preview.
	 * 
	 * @param job
	 *            a Job
	 */
	public void setPrintJob(PrintJob job) {
		this.job = job;
	}

	/**
	 * Update the page counter label : 1 of 10
	 */
	protected void updatePageNumber() {
		int pageIndex = this.preview.getPageIndex();
		int pageCount = this.preview.getPageCount();
		int visiblePageCount = this.preview.getHorizontalPageCount()
				* this.preview.getVerticalPageCount();
		String text = this.localized.format("PrintPreviewDialog.pageOfPages", //$NON-NLS-1$
				pageIndex + 1, pageCount);
		this.pageNumber.setText(text);
		this.previousPage.setEnabled(pageIndex > 0);
		this.nextPage.setEnabled(pageIndex < pageCount - visiblePageCount);

		// pageNumber.getParent().layout();

		this.pageNumberToolItem.setWidth(this.pageNumberToolItem.getControl()
				.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);

		// toolbarManager.update(true);
		this.toolbarManager.getControl().layout();
	}

	/**
	 * Update the preview size
	 */
	protected void updatePreviewSize() {
		Point minSize;
		Rectangle bounds = scroll.getClientArea();
		if (preview.isFitHorizontal()) {
			if (preview.isFitVertical())
				minSize = new Point(0, 0); // Best fit
			else
				minSize = new Point(0, preview.computeSize(bounds.width,
						SWT.DEFAULT).y); // Fit to width
		} else {
			if (preview.isFitVertical())
				minSize = new Point(preview.computeSize(SWT.DEFAULT,
						bounds.height).x, 0); // Fit to height
			else
				minSize = preview.computeSize(SWT.DEFAULT, SWT.DEFAULT); // Custom
			// scale
		}
		scroll.setMinSize(minSize);
	}

	/**
	 * This function update the enabled state of the previous and next buttons
	 */
	protected void updatePreviousNextAction() {
		// Update buttons
		previousPage.setEnabled(preview.getPageIndex() > 0);
		nextPage.setEnabled(preview.getPageIndex()
				+ preview.getHorizontalPageCount()
				* preview.getVerticalPageCount() < preview.getPageCount());
	}

}
