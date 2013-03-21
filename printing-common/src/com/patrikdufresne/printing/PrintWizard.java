package com.patrikdufresne.printing;

import java.lang.reflect.InvocationTargetException;

import net.sf.paperclips.PrintJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.printing.PrinterData;

import com.patrikdufresne.util.Localized;

/**
 * Abstract class providing a wizard for printing. Subclasses should override
 * the following functions:
 * <ul>
 * <li>{@link #addPages()}</li>
 * <li>{@link #initFactory(IPrintFactory)}</li>
 * </ul>
 * 
 * @author Patrik Dufresne
 * 
 */
public abstract class PrintWizard extends Wizard {
	/**
	 * Runnable to create the print.
	 * 
	 * @author Patrik Dufresne
	 * 
	 */
	private class CreatePrintJobRunnable implements IRunnableWithProgress {
		private IPrintFactory factory;

		/**
		 * PrintJob object (used in perform Finish).
		 */
		private PrintJob job;

		/**
		 * Create a new runnable.
		 * 
		 * @param factory
		 */
		public CreatePrintJobRunnable(IPrintFactory factory) {
			if (factory == null) {
				throw new NullPointerException();
			}
			this.factory = factory;
		}

		/**
		 * Return the created print job.
		 * 
		 * @return the print job
		 */
		public PrintJob getPrintJob() {
			return this.job;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {

			// Sets process
			monitor.beginTask(localized.get("PrintAction.task.preparePreview"), //$NON-NLS-1$
					IProgressMonitor.UNKNOWN);

			initFactory(factory);

			this.job = new PrintJob(factory.getName(), factory.createPrint());
			this.job.setMargins(factory.getMargins());
			this.job.setOrientation(factory.getOrientation());

			// Task completed
			monitor.done();
		}

	}

	/**
	 * Used to localize string.
	 */
	private Localized localized = Localized.load(PrintAction.class);

	/**
	 * The factory used to print.
	 */
	private IPrintFactory factory;

	/**
	 * Preference store
	 */
	private IPreferenceStore prefStore;

	/**
	 * Key where to save preference
	 */
	private String printerDataPrefKey;

	/**
	 * Create a new Print wizard.
	 */
	protected PrintWizard(IPrintFactory factory) {
		this.factory = factory;
		setNeedsProgressMonitor(true);
	}

	/**
	 * Returns the key used to save the printer settings.
	 * 
	 * @return a key or null if not set
	 */
	public String getPrefKey() {
		return printerDataPrefKey;
	}

	/**
	 * Returns the preference store used by this Print action to save the
	 * Printer settings selected by the user.
	 * 
	 * @return the preference or null if not set
	 */
	public IPreferenceStore getPrefStore() {
		return prefStore;
	}

	/**
	 * Sub-class may implement this function to initialize the factory.
	 * <p>
	 * This function may not be called by the main Thread.
	 * 
	 * @param factory
	 * 
	 * @exception InvocationTargetException
	 *                if the method must propagate an exception, it should wrap
	 *                it inside an <code>InvocationTargetException</code>; FIXME
	 *                runtime exceptions are automatically wrapped in an
	 *                <code>InvocationTargetException</code> by the calling
	 *                context
	 * @exception InterruptedException
	 *                if the operation is cancel by the user, this method should
	 *                exit by throwing <code>InterruptedException</code>
	 */
	protected void initFactory(IPrintFactory factory)
			throws InvocationTargetException, InterruptedException {
		// Sub-classes may implement this function.
	}

	/**
	 * This implementation start the printing
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		synchronized (this) {

			CreatePrintJobRunnable runnable = new CreatePrintJobRunnable(
					factory);
			try {
				getContainer().run(true, false, runnable);
			} catch (InvocationTargetException e) {
				Policy.getStatusHandler()
						.show(new Status(
								IStatus.ERROR,
								Policy.JFACE,
								this.localized
										.get("PrintAction.previewErrorMessage"),
								e.getCause()), null);
				return false;
			} catch (InterruptedException e) {
				// Nothing to do, the operation was cancel by user
				return false;
			}

			// Open the preview dialog
			PrintPreviewDialog dlg = new PrintPreviewDialog(getShell());
			dlg.setPrintJob(runnable.getPrintJob());

			// Load preference
			if (this.prefStore != null && this.printerDataPrefKey != null) {
				PrinterData data = PrintPreferenceStoreUtil.getPrinterData(
						this.prefStore, this.printerDataPrefKey);
				dlg.setPrinterData(data);
			}
			int returnCode = dlg.open();

			// Save preference
			// Save the preferences
			if (returnCode == Dialog.OK && this.prefStore != null
					&& this.printerDataPrefKey != null) {
				PrintPreferenceStoreUtil.setValue(this.prefStore,
						this.printerDataPrefKey, dlg.getPrinterData());
			}

		}
		return true;
	}

	/**
	 * Sets the preference store to use to save the printer settings selected by
	 * the user.
	 * 
	 * @param preferenceStore
	 *            a preference store or null to unset
	 */
	public void setPrefStore(IPreferenceStore preferenceStore) {
		this.prefStore = preferenceStore;
	}

	/**
	 * Sets the preference key.
	 * 
	 * @param prefKey
	 *            a key or null
	 */
	public void setPrinterDataPrefKey(String prefKey) {
		this.printerDataPrefKey = prefKey;
	}

}
