package com.patrikdufresne.printing;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.ui.PrintPreview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.patrikdufresne.util.Localized;

/**
 * Instances of this class allow the user to preview the Print object before
 * sending it to the printer. It also provide an easy way to change the Print
 * setting.
 * 
 */
public class PrintPreviewDialog extends TrayDialog {

	/**
	 * This class is an adaptation of the ResizeListener class from TrayDialog
	 * to avoid resizing the left pane.
	 * 
	 * @author Patrik Dufresne
	 * 
	 */
	private class ResizeListener extends ControlAdapter {

		private final GridData data;

		// to tray when resizing
		private int remainder = 0; // Used to prevent rounding errors from
									// accumulating
		private final Shell shell;
		int shellWidth;
		private final int TRAY_RATIO = 0; // Percentage of extra width devoted

		public ResizeListener(GridData data, Shell shell) {
			this.data = data;
			this.shell = shell;
			this.shellWidth = shell.getSize().x;
		}

		public void controlResized(ControlEvent event) {
			int newWidth = shell.getSize().x;
			if (newWidth != shellWidth) {
				int shellWidthIncrease = newWidth - shellWidth;
				int trayWidthIncreaseTimes100 = (shellWidthIncrease * TRAY_RATIO)
						+ remainder;
				int trayWidthIncrease = trayWidthIncreaseTimes100 / 100;
				remainder = trayWidthIncreaseTimes100
						- (100 * trayWidthIncrease);
				data.widthHint = data.widthHint + trayWidthIncrease;
				shellWidth = newWidth;
				if (!shell.isDisposed()) {
					shell.layout();
				}
			}
		}
	}

	/**
	 * Resource id for close icon.
	 */
	public static final String ICON_CLOSE_16 = "PrintPreviewDialog.iconClose16";
	/**
	 * Resource id for next page icon.
	 */
	public static final String ICON_NEXT_PAGE_16 = "PrintPreviewDialog.iconNextPage16"; //$NON-NLS-1$
	/**
	 * Resource id for previous page icon.
	 */
	public static final String ICON_PREVIOUS_PAGE_16 = "PrintPreviewDialog.iconPreviousPage16"; //$NON-NLS-1$
	/**
	 * Resource id for print icon.
	 */
	public static final String ICON_PRINT_16 = "PrintPreviewDialog.iconPrint16"; //$NON-NLS-1$
	/**
	 * Resource id best fit zoom.
	 */
	public static final String ICON_ZOOM_FIT_BEST_16 = "PrintPreviewDialog.iconZoomFitBest16"; //$NON-NLS-1$
	/**
	 * Resource id for zoom-in icon.
	 */
	public static final String ICON_ZOOM_IN_16 = "PrintPreviewDialog.iconZoomIn16"; //$NON-NLS-1$
	/**
	 * Resource id for original size zoom icon.
	 */
	public static final String ICON_ZOOM_ORIGINAL_16 = "PrintPreviewDialog.iconZoomOriginal16"; //$NON-NLS-1$

	/**
	 * Resource id for zoom-out icon.
	 */
	public static final String ICON_ZOOM_OUT_16 = "PrintPreviewDialog.iconZoomOut16"; //$NON-NLS-1$

	/**
	 * Path to icons
	 */
	private final static String ICONS_PATH = "icons/";//$NON-NLS-1$

	/**
	 * Allocate the action image
	 */
	static {
		JFaceResources.getImageRegistry().put(
				ICON_NEXT_PAGE_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "go-next-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_PREVIOUS_PAGE_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "go-previous-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_PRINT_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "printer-printing-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_ZOOM_FIT_BEST_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "zoom-fit-best-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_ZOOM_IN_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "zoom-in-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_ZOOM_ORIGINAL_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "zoom-original-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_ZOOM_OUT_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "zoom-out-16.png"));
		JFaceResources.getImageRegistry().put(
				ICON_CLOSE_16,
				ImageDescriptor.createFromFile(PrintPreviewDialog.class,
						ICONS_PATH + "window-close-16.png"));
	}

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
	 * Action changing the destination.
	 */
	private Action actionChangeDestination = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.changeDestination")) { //$NON-NLS-1$
		@Override
		public void run() {

			PrinterDialog dlg = new PrinterDialog(getShell());
			dlg.setPrinterData(data);

			if (dlg.open() != Window.OK) {
				return;
			}
			// FIXME Set the printer data.
			setPrinterData(dlg.getPrinterData());
		}
	};

	/**
	 * Action closing the print preview dialog.
	 */
	private Action actionClose = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.close"),
			JFaceResources.getImageRegistry().getDescriptor(ICON_CLOSE_16)) {
		@Override
		public void run() {
			// Cancel
			setReturnCode(Window.CANCEL);
			PrintPreviewDialog.this.close();
		}
	};
	private Action actionLandscape = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.landscape"),
			IAction.AS_RADIO_BUTTON) {

		@Override
		public void run() {
			data.orientation = PrinterData.LANDSCAPE;
			setPrinterData(data);
			// updateOrientation();
			// preview.setPrinterData(data);
			// updateOrientation();
			// updatePreviewSize();
			// updatePageNumber();
			// updatePreviousNextAction();
		}
	};
	/**
	 * Action to preview next page.
	 */
	private Action actionNextPage = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.next.text"),
			JFaceResources.getImageRegistry().getDescriptor(ICON_NEXT_PAGE_16)) {

		{
			setToolTipText(Localized.get(PrintPreviewDialog.class,
					"PrintPreviewDialog.next.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			setPreviewPageIndex(preview.getPageIndex()
					+ preview.getHorizontalPageCount()
					* preview.getVerticalPageCount());
			updatePreviousNextAction();
		}
	};

	private Action actionPortrait = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.portrait"),
			IAction.AS_RADIO_BUTTON) {
		@Override
		public void run() {
			data.orientation = PrinterData.PORTRAIT;
			setPrinterData(data);
			// updateOrientation();
			// preview.setPrinterData(data);
			// updatePreviewSize();
			// updatePageNumber();
			// updatePreviousNextAction();
		}
	};

	/**
	 * Action to preview previous page.
	 */
	private Action actionPreviousPage = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.previous.text"), //$NON-NLS-1$
			JFaceResources.getImageRegistry().getDescriptor(
					ICON_PREVIOUS_PAGE_16)) {

		{
			setToolTipText(Localized.get(PrintPreviewDialog.class,
					"PrintPreviewDialog.previous.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			setPreviewPageIndex(preview.getPageIndex()
					- preview.getHorizontalPageCount()
					* preview.getVerticalPageCount());
			updatePreviousNextAction();
		}
	};

	/**
	 * Action to print. Will prompt the user for a destination file when
	 * printing to file.
	 */
	private Action actionPrint = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.print"),
			JFaceResources.getImageRegistry().getDescriptor(ICON_PRINT_16)) {
		@Override
		public void run() {

			if (data.printToFile) {
				FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
				dlg.setFileName(data.fileName);
				String fileName = dlg.open();
				if (fileName == null) {
					return;
				}
				data.fileName = fileName;
			}

			// Send the PrintJob to the printer
			PaperClips.print(job, data);

			// Close the preview
			setReturnCode(Window.OK);
			PrintPreviewDialog.this.close();
		}

	};

	/**
	 * Action to print using the system platform print dialog.
	 */
	private Action actionSystemPrint = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.systemPrint.text"),
			JFaceResources.getImageRegistry().getDescriptor(ICON_PRINT_16)) {

		{
			setToolTipText(Localized.get(PrintPreviewDialog.class,
					"PrintPreviewDialog.systemPrint.toolTipText"));
		}

		@Override
		public void run() {
			PrintDialog dlg = new PrintDialog(
					PrintPreviewDialog.this.getShell());
			dlg.setPrinterData(data);
			PrinterData newData = dlg.open();
			if (newData == null) {
				return;
			}

			// PrintPreviewDialog.this.data = newData;
			// preview.setPrinterData(data);

			// Send the PrintJob to the printer
			PaperClips.print(job, newData);

			// Close the preview
			setReturnCode(Window.OK);
			PrintPreviewDialog.this.close();
		}
	};

	private Action actionZoomFit = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.zoomBestFit"),
			JFaceResources.getImageRegistry().getDescriptor(
					ICON_ZOOM_FIT_BEST_16)) {
		@Override
		public void run() {
			preview.setFitVertical(true);
			preview.setFitHorizontal(true);
			updatePreviewSize();
		}
	};

	private Action actionZoomIn = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.zoomIn"),
			JFaceResources.getImageRegistry().getDescriptor(ICON_ZOOM_IN_16)) {
		@Override
		public void run() {
			setPreviewScale(preview.getAbsoluteScale() * 1.1f);
		}
	};

	private Action actionZoomOriginal = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.zoomOriginal"),
			JFaceResources.getImageRegistry().getDescriptor(
					ICON_ZOOM_ORIGINAL_16)) {
		@Override
		public void run() {
			setPreviewScale(1);
		}
	};

	private Action actionZoomOut = new Action(Localized.get(
			PrintPreviewDialog.class, "PrintPreviewDialog.zoomOut"),
			JFaceResources.getImageRegistry().getDescriptor(ICON_ZOOM_OUT_16)) {
		@Override
		public void run() {
			setPreviewScale(preview.getAbsoluteScale() / 1.1f);
		}
	};

	/**
	 * The PrinterData used to print the job
	 */
	protected PrinterData data;

	/**
	 * Label to display the destination.
	 */
	private CLabel destination;

	/**
	 * The PrintJob to preview
	 */
	protected PrintJob job;

	protected Label pageNumber;

	/**
	 * Composite displaying the preview of a page.
	 */
	protected PrintPreview preview;

	private ScrolledComposite scroll;

	/**
	 * Create a new print preview dialog.
	 * 
	 * @param shell
	 *            a shell provider
	 */
	public PrintPreviewDialog(IShellProvider shell) {
		super(shell);
		this.data = getDefaultPrinterData();
	}

	/**
	 * Create a new print preview dialog.
	 * 
	 * @param shell
	 *            the parent shell
	 */
	public PrintPreviewDialog(Shell shell) {
		super(new SameShellProvider(shell));
		this.data = getDefaultPrinterData();
	}

	/**
	 * This implementation does nothing to avoid creating buttons bars.
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		// Does nothing.
		return null;
	}

	/**
	 * This implementation is an adaptation of the openTray function from
	 * TrayDialog to display the Tray on the left side and to avoid resizing the
	 * tray.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		/*
		 * Create Preview area
		 */
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createScrollingPreview(composite);
		this.preview.setPrinterData(this.data);
		this.preview.setLazyPageLayout(false);
		if (this.job != null) {
			this.preview.setPrintJob(this.job);
		}

		/*
		 * Create left pane area
		 */
		final Shell shell = getShell();
		final Sash sash = new Sash(shell, SWT.VERTICAL);
		sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		final Label rightSeparator = new Label(shell, SWT.SEPARATOR
				| SWT.VERTICAL);
		rightSeparator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		Control trayControl = createPaneContents(shell);
		Rectangle clientArea = shell.getClientArea();
		final GridData data = new GridData(GridData.FILL_VERTICAL);
		data.widthHint = trayControl
				.computeSize(SWT.DEFAULT, clientArea.height).x;
		trayControl.setLayoutData(data);
		int trayWidth = sash.computeSize(SWT.DEFAULT, clientArea.height).x
				+ rightSeparator.computeSize(SWT.DEFAULT, clientArea.height).x
				+ data.widthHint;
		Rectangle bounds = shell.getBounds();
		shell.setBounds(bounds.x
				- ((getDefaultOrientation() == SWT.RIGHT_TO_LEFT) ? trayWidth
						: 0), bounds.y, bounds.width + trayWidth, bounds.height);
		sash.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail != SWT.DRAG) {
					Rectangle clientArea = shell.getClientArea();
					int newWidth = clientArea.width - event.x
							- (sash.getSize().x + rightSeparator.getSize().x);
					if (newWidth != data.widthHint) {
						data.widthHint = newWidth;
						shell.layout();
					}
				}
			}
		});

		shell.addControlListener(new ResizeListener(data, shell));

		// Update widgets
		updateShellTitle();
		updateOrientation();
		updatePrinterName();
		updatePageNumber();
		updatePreviousNextAction();
		updatePreviewSize();

		return composite;
	}

	/**
	 * Create the content of the dialog tray displayedon the right side of the
	 * dialog.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return
	 */
	protected Control createPaneContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));

		GridLayout layout;
		ActionContributionItem aci;

		// Navigation
		ToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		toolbar.add(this.actionPreviousPage);
		toolbar.add(this.actionNextPage);
		toolbar.add(new Separator());
		toolbar.add(this.actionZoomIn);
		toolbar.add(this.actionZoomOut);
		toolbar.add(this.actionZoomOriginal);
		toolbar.add(this.actionZoomFit);
		toolbar.createControl(composite);

		// Page Count
		this.pageNumber = new Label(composite, SWT.NONE);
		this.pageNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.pageNumber.setText(""); //$NON-NLS-1$

		// Separator
		Label separator;
		separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Destination
		Label label = new Label(composite, SWT.NONE);
		label.setText(Localized.get(PrintPreviewDialog.class,
				"PrintPreviewDialog.destination")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite destinationComp = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		destinationComp.setLayout(layout);
		destinationComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.destination = new CLabel(destinationComp, SWT.NONE);
		this.destination.setBackground(composite.getBackground());
		this.destination.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		aci = new ActionContributionItem(this.actionChangeDestination);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		aci.fill(destinationComp);

		// Separator
		separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Layouts : landscape / portrait
		label = new Label(composite, SWT.NONE);
		label.setText(Localized.get(PrintPreviewDialog.class,
				"PrintPreviewDialog.layout")); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Portrait
		aci = new ActionContributionItem(this.actionPortrait);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		aci.fill(composite);
		((Control) aci.getWidget()).setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		// Landscape
		aci = new ActionContributionItem(this.actionLandscape);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		aci.fill(composite);
		((Control) aci.getWidget()).setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		// Separator
		separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Spacer
		Composite spacer = new Composite(composite, SWT.NONE);
		spacer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		/*
		 * Button bars
		 */
		Composite buttonsBar = new Composite(composite, SWT.NONE);
		layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonsBar.setLayout(layout);

		// SystemPrint
		aci = new ActionContributionItem(this.actionSystemPrint);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		aci.fill(buttonsBar);

		// Print
		aci = new ActionContributionItem(this.actionPrint);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		aci.fill(buttonsBar);

		// Close
		aci = new ActionContributionItem(this.actionClose);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		aci.fill(buttonsBar);

		return composite;

	}

	protected Control createScrollingPreview(Composite parent) {
		this.scroll = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		this.scroll.setExpandHorizontal(true);
		this.scroll.setExpandVertical(true);

		this.preview = new PrintPreview(this.scroll, SWT.NONE);
		this.scroll.setContent(this.preview);

		this.preview.setFitVertical(false);
		this.preview.setFitHorizontal(false);
		this.preview.setScale(1);

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
					Rectangle bounds = scroll.getClientArea();
					Point size = preview.getSize();
					scrollable = size.x > bounds.width
							|| size.y > bounds.height;
					if (!scrollable && dragging)
						endDragging();
					break;
				case SWT.MouseDown:
					if (scrollable && event.button == 1)
						beginDragging(event);
					break;
				case SWT.MouseMove:
					if (dragging) {
						Point point = preview.toDisplay(event.x, event.y);
						scroll.setOrigin(dragStartScrollOrigin.x
								+ dragStartMouseAnchor.x - point.x,
								dragStartScrollOrigin.y
										+ dragStartMouseAnchor.y - point.y);
					}
					break;
				case SWT.MouseUp:
					if (dragging)
						endDragging();
					break;
				case SWT.MouseEnter:
					Display.getCurrent().addFilter(SWT.MouseWheel, this);
					break;
				case SWT.MouseWheel:
					// In some circumstance, this listener may not receive a
					// MouseExit therefore this listener may receive
					// notification while the widget is dispose.
					if (scroll.isDisposed()) {
						Display.getCurrent().removeFilter(SWT.MouseWheel, this);
					}
					if (event.count != 0) {
						if (scrollable
								&& !dragging
								&& (event.stateMask == SWT.NONE || event.stateMask == SWT.SHIFT)) {
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
	 * This implementation return true.
	 * 
	 * @see Dialog
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * Used by actions to set the page index to be view.
	 * 
	 * @param pageIndex
	 */
	protected void setPreviewPageIndex(int pageIndex) {
		this.preview.setPageIndex(Math.max(
				Math.min(pageIndex, this.preview.getPageCount() - 1), 0));
		updatePageNumber();
	}

	/**
	 * Used by actions to sets the scale property of the preview widget.
	 * 
	 * @param scale
	 */
	protected void setPreviewScale(float scale) {
		this.preview.setFitVertical(false);
		this.preview.setFitHorizontal(false);
		this.preview.setScale(scale);
		updatePreviewSize();
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
		if (this.preview != null && !this.preview.isDisposed()) {
			this.preview.setPrinterData(data);
		}
		updateOrientation();
		updatePrinterName();
		updatePageNumber();
		updatePreviewSize();
		updatePreviousNextAction();
	}

	/**
	 * Sets the Print job to preview.
	 * 
	 * @param job
	 *            a Job
	 */
	public void setPrintJob(PrintJob job) {
		this.job = job;
		updateOrientation();
		updateShellTitle();
	}

	/**
	 * Update the orientation widgets.
	 */
	protected void updateOrientation() {
		if (this.actionLandscape == null || this.actionPortrait == null
				|| this.job == null) {
			return;
		}
		int orientation = this.job.getOrientation() == PaperClips.ORIENTATION_LANDSCAPE
				|| (this.job.getOrientation() == PaperClips.ORIENTATION_DEFAULT && this.data.orientation == PrinterData.LANDSCAPE) ? PrinterData.LANDSCAPE
				: PrinterData.PORTRAIT;
		this.actionPortrait.setChecked(orientation == PrinterData.PORTRAIT);
		this.actionLandscape.setChecked(orientation == PrinterData.LANDSCAPE);
	}

	/**
	 * Update the page counter label : 1 of 10
	 */
	protected void updatePageNumber() {
		if (this.preview == null || this.pageNumber == null
				|| this.preview.isDisposed() || this.pageNumber.isDisposed()) {
			return;
		}
		int pageIndex = this.preview.getPageIndex();
		int pageCount = this.preview.getPageCount();
		String text = Localized.format(PrintPreviewDialog.class,
				"PrintPreviewDialog.pageOfPages", //$NON-NLS-1$
				Integer.valueOf(pageIndex + 1), Integer.valueOf(pageCount));
		this.pageNumber.setText(text);
	}

	/**
	 * Update the preview size
	 */
	protected void updatePreviewSize() {
		if (this.preview == null || this.scroll == null
				|| this.preview.isDisposed() || this.scroll.isDisposed()) {
			return;
		}
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

		if (preview == null || preview.isDisposed()
				|| actionPreviousPage == null || actionNextPage == null) {
			return;
		}

		// Update buttons
		actionPreviousPage.setEnabled(preview.getPageIndex() > 0);
		actionNextPage.setEnabled(preview.getPageIndex()
				+ preview.getHorizontalPageCount()
				* preview.getVerticalPageCount() < preview.getPageCount());
	}

	/**
	 * Update the printer name label
	 */
	protected void updatePrinterName() {
		if (this.destination == null || this.destination.isDisposed()) {
			return;
		}

		// Update the widget
		if (this.data.printToFile) {
			this.destination.setText(Localized.get(PrintPreviewDialog.class,
					"PrintPreviewDialog.printToFile")); //$NON-NLS-1$
		} else {
			this.destination.setText(this.data.name);
		}
	}

	/**
	 * Update the shell title using the job's name.
	 */
	protected void updateShellTitle() {

		if (getShell() == null || getShell().isDisposed()) {
			return;
		}

		getShell().setText(
				this.job != null ? Localized.format(PrintPreviewDialog.class,
						"PrintPreviewDialog.title", //$NON-NLS-1$
						(Object) this.job.getName()) : Localized.get(
						PrintPreviewDialog.class, "PrintPreviewDialog.title2"));
	}

}
