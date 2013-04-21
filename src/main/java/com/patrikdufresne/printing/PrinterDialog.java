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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.patrikdufresne.util.Localized;

/**
 * Dialog used to select a printer.
 * 
 * @author Patrik Dufresne
 * 
 */
public class PrinterDialog extends TitleAreaDialog {

    // In GTK, the printer list will include Print To File printer,
    // but with the wrong flags. Let fix it using
    private static final String GTK_FILE_BACKEND = "GtkPrintBackendFile"; //$NON-NLS-1$

    PrinterData printerData = new PrinterData();

    /**
     * Create a new dialog
     * 
     * @param parentShell
     */
    public PrinterDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * This implementation display a list of printer to be selected.
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite composite = (Composite) super.createDialogArea(parent);

        // Set title and title area
        getShell().setText(Localized.get(PrinterDialog.class, "PrinterDialog.title")); //$NON-NLS-1$
        setTitle(Localized.get(PrinterDialog.class, "PrinterDialog.titleArea.title")); //$NON-NLS-1$
        setMessage(Localized.get(PrinterDialog.class, "PrinterDialog.titleArea.message")); //$NON-NLS-1$

        Composite comp = new Composite(composite, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Create the viewer with it's content
        PrinterData[] printers = Printer.getPrinterList();
        TableViewer viewer = new TableViewer(comp);
        viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                return ((PrinterData) element).name;
            }
        });
        viewer.setInput(printers);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                PrinterData selected = (PrinterData) ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selected != null) {
                    printerData = selected;
                }
            }
        });

        // Build the separator line
        Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return composite;
    }

    /**
     * Returns the printer data that will be used when the dialog is opened.
     * 
     * @return the data that will be used when the dialog is opened
     */
    public PrinterData getPrinterData() {
        if (GTK_FILE_BACKEND.equals(this.printerData.driver)) {
            this.printerData.printToFile = true;
        }
        return this.printerData;
    }

    /**
     * Sets the printer data that will be used when the dialog is opened.
     * <p>
     * Setting the printer data to null is equivalent to resetting all data
     * fields to their default values.
     * </p>
     * 
     * @param data
     *            the data that will be used when the dialog is opened or null
     *            to use default data
     */
    public void setPrinterData(PrinterData data) {
        if (data == null) data = new PrinterData();
        this.printerData = data;
    }

}
