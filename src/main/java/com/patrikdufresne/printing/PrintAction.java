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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;

import com.patrikdufresne.util.Localized;

/**
 * Action used to work with printing. It's possible to print directly to a
 * printer or to see a preview first.
 * 
 * @author Patrik Dufresne
 * 
 */
public abstract class PrintAction extends Action {
    /**
     * Runnable to print or preview.
     * 
     * @author Patrik Dufresne
     * 
     */
    protected class PrintPreviewRunnable implements IRunnableWithProgress {

        /** Default printer data */
        private PrinterData data;

        private PrintJob job;

        /**
         * Create a new runnable.
         * 
         * @param data
         *            the printer data or null for default
         */
        public PrintPreviewRunnable(PrinterData data) {
            this.data = data;
        }

        /**
         * Return the printer data.
         * 
         * @return the printer data or null is not set
         */
        public PrinterData getPrinterData() {
            return this.data;
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            // Sets process
            monitor.beginTask(getOperation() == PRINT_ACTION ? localized.get("PrintAction.task.preparePrinting") //$NON-NLS-1$
                    : localized.get("PrintAction.task.preparePreview"), //$NON-NLS-1$
                    IProgressMonitor.UNKNOWN);

            // Query database
            IPrintFactory factory = getPrintFactory();
            initFactory(factory);

            // Create print
            Print print = factory.createPrint();
            this.job = new PrintJob(factory.getName(), print);
            this.job.setMargins(factory.getMargins());
            this.job.setOrientation(factory.getOrientation());

            monitor.subTask(localized.get("PrintAction.task.sendToPrinter")); //$NON-NLS-1$

            // Task completed
            monitor.done();

        }

        public void runAfter() {
            if (getOperation() == PRINT_ACTION) {
                PaperClips.print(this.job, getPrinterData());
            } else {
                PrintPreviewDialog dlg = new PrintPreviewDialog(getShellProvider());
                dlg.setPrinterData(getPrinterData());
                dlg.setPrintJob(this.job);
                if (dlg.open() == Window.OK) {
                    savePref(dlg.getPrinterData());
                }
            }
        }
    }

    /**
     * This constant is used to define the type of operation to do when this
     * action is run.
     */
    public static final int PREVIEW_ACTION = 2;

    /**
     * This constant is used to define the type of operation to do when this
     * action is run.
     */
    public static final int PRINT_ACTION = 1;

    /**
     * The print factory
     */
    private IPrintFactory factory;
    /**
     * Used to localize string.
     */
    private Localized localized = Localized.load(PrintAction.class);

    /**
     * Hold the operation.
     */
    private int operation;

    /**
     * Preference store
     */
    private IPreferenceStore preferenceStore;

    /**
     * Key where to save preference
     */
    private String prefKey;

    /**
     * Context or execution.
     */
    private IRunnableContext runnableContext;
    /**
     * Use to retrieved a Shell object to display the dialog.
     */
    private IShellProvider shell;

    /**
     * Create a new Print Action.
     * 
     * @param shell
     *            the shell provider used to display dialog
     * @param factory
     *            the print factory used to create the print
     * @param operation
     *            the type of operation to run
     */
    public PrintAction(IShellProvider shell, IPrintFactory factory, int operation) {
        this(shell, factory, operation, null);
    }

    /**
     * Create a new Print Action.
     * 
     * @param shell
     *            the shell provider used to display dialog
     * @param factory
     *            the print factory used to create the print
     * @param operation
     *            the type of operation to run
     * @param runnableContext
     *            the runnable context or null to run the action in the Main
     *            thread.
     */
    public PrintAction(IShellProvider shell, IPrintFactory factory, int operation, IRunnableContext runnableContext) {
        if (shell == null || factory == null) {
            throw new NullPointerException();
        }
        this.shell = shell;
        this.factory = factory;
        this.operation = operation == PRINT_ACTION ? PRINT_ACTION : PREVIEW_ACTION;
        this.runnableContext = runnableContext;
    }

    /**
     * Return the operation run by this action.
     * 
     * @return PRINT_ACTION or PREVIEW_ACTION
     */
    public int getOperation() {
        return this.operation;
    }

    /**
     * Returns the preference store used by this Print action to save the
     * Printer settings selected by the user.
     * 
     * @return the preference or null if not set
     */
    public IPreferenceStore getPreferenceStore() {
        return this.preferenceStore;
    }

    /**
     * Returns the key used to save the printer settings.
     * 
     * @return a key or null if not set
     */
    public String getPrefKey() {
        return this.prefKey;
    }

    /**
     * Return the print factory.
     * 
     * @return the print factory.
     */
    public IPrintFactory getPrintFactory() {
        return this.factory;
    }

    /**
     * Returns the runnable context or null if not set
     * 
     * @return a context
     */
    public IRunnableContext getRunnableContext() {
        return this.runnableContext;
    }

    /**
     * Return the shell provide for this action.
     * 
     * @return the shell provider
     */
    public IShellProvider getShellProvider() {
        return this.shell;
    }

    /**
     * This function is used to setup the factory object before it get to create
     * a Print object. Sub-class may implement this function to sets factory
     * data.
     * <p>
     * This function is not called by the main Thread.
     * 
     * @param factory
     *            the factory
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
    protected void initFactory(IPrintFactory f) throws InvocationTargetException, InterruptedException {
        // Implemented by sub-class
    }

    /**
     * Load the Printer data from preference store if available.
     * 
     * @return
     */
    protected PrinterData loadPref() {
        if (this.preferenceStore != null && this.prefKey != null) {
            return PrintPreferenceStoreUtil.getPrinterData(this.preferenceStore, this.prefKey);
        }
        return null;
    }

    /**
     * Preview the Print object.
     */
    protected void preview() {
        // Run the preview
        runWithRunnableContext(new PrintPreviewRunnable(loadPref()));
    }

    /**
     * Send the Print object to the printer
     */
    protected void print() {

        // Open printing dialog
        PrintDialog dlg = new PrintDialog(this.shell.getShell());
        dlg.setText(this.localized.get("PrintAction.printDialog.title")); //$NON-NLS-1$
        dlg.setPrinterData(loadPref());
        PrinterData data = dlg.open();
        if (data == null) {
            // Operation cancel by user
            return;
        }
        // Save the preferences
        savePref(data);

        // Print to printer using runnable context
        runWithRunnableContext(new PrintPreviewRunnable(data));

    }

    /**
     * This implementation print or preview the print
     */
    @Override
    public void run() {
        if (this.operation == PRINT_ACTION) {
            // Print the print
            print();
        } else {
            // Preview the Print
            preview();
        }
    }

    /**
     * Run the given runnable with a {@link IRunnableContext} if define.
     * Otherwise run the runnable in the main thread.
     * <p>
     * We don't know if this function block.
     * 
     * @param runnable
     */
    protected void runWithRunnableContext(PrintPreviewRunnable runnable) {
        // Run the Runnable - Run the printing
        try {
            if (this.runnableContext != null) {
                this.runnableContext.run(true, false, runnable);
            } else {
                runnable.run(new NullProgressMonitor());
            }
        } catch (InvocationTargetException e) {
            Policy.getStatusHandler().show(
                    new Status(IStatus.ERROR, Policy.JFACE, getOperation() == PRINT_ACTION
                            ? this.localized.get("PrintAction.printErrorMessage")
                            : this.localized.get("PrintAction.previewErrorMessage"), e.getCause()),
                    null);
            return;
        } catch (InterruptedException e) {
            // Nothing to do, the opperation was cancel by user
            return;
        }

        runnable.runAfter();

    }

    /**
     * Save the printer data into preference store.
     */
    protected void savePref(PrinterData data) {
        if (this.preferenceStore != null && this.prefKey != null) {
            PrintPreferenceStoreUtil.setValue(this.preferenceStore, this.prefKey, data);
        }
    }

    /**
     * Sets the preference store to use to save the printer settings selected by
     * the user.
     * 
     * @param preferenceStore
     *            a preference store or null to unset
     */
    public void setPreferenceStore(IPreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }

    /**
     * Sets the preference key.
     * 
     * @param prefKey
     *            a key or null
     */
    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    /**
     * Sets the runnable context to used while printing.
     * 
     * @param runnableContext
     *            a context
     */
    public void setRunnableContext(IRunnableContext runnableContext) {
        this.runnableContext = runnableContext;
    }
}
