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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.printing.PrinterData;

/**
 * This utility class provide an easy way to store preferences related to
 * Printing (e.g.: PrintData).
 * 
 * @author patapouf
 * 
 */
public class PrintPreferenceStoreUtil {
    /**
     * Preference key to save and retrieved collate value.
     */
    private static final String COLLATE_KEY = ".collate"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved driver value.
     */
    private static final String DRIVER_KEY = ".driver"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved name value.
     */
    private static final String NAME_KEY = ".name"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved scope value.
     */
    private static final String SCOPE_KEY = ".scope"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved startPage value.
     */
    private static final String START_PAGE_KEY = ".startPage"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved endPage value.
     */
    private static final String END_PAGE_KEY = ".endPage"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved printToFile value.
     */
    private static final String PRINT_TO_FILE_KEY = ".printToFile"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved fileName value.
     */
    private static final String FILENAME_KEY = ".fileName"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved copyCount value.
     */
    private static final String COPY_COUNT_KEY = ".copyCount"; //$NON-NLS-1$
    /**
     * Preference key to save and retrieved orientation value.
     */
    private static final String ORIENTATION_KEY = ".orientation"; //$NON-NLS-1$

    /**
     * This function save the given printer data into the preference store.
     * 
     * @param store
     *            the preference store.
     * @param key
     *            the key
     * @param data
     *            the value
     */
    public static void setValue(IPreferenceStore store, String key, PrinterData data) {
        if (data != null) {
            store.setValue(key + DRIVER_KEY, data.driver != null ? data.driver : ""); //$NON-NLS-1$
            store.setValue(key + NAME_KEY, data.name != null ? data.name : ""); //$NON-NLS-1$
            store.setValue(key + SCOPE_KEY, data.scope);
            store.setValue(key + START_PAGE_KEY, data.startPage);
            store.setValue(key + END_PAGE_KEY, data.endPage);
            store.setValue(key + PRINT_TO_FILE_KEY, data.printToFile);
            store.setValue(key + FILENAME_KEY, data.fileName != null ? data.fileName : ""); //$NON-NLS-1$	
            store.setValue(key + COPY_COUNT_KEY, data.copyCount);
            store.setValue(key + COLLATE_KEY, data.collate);
            store.setValue(key + ORIENTATION_KEY, data.orientation);
        }

    }

    /**
     * This function recreated the Printer Data from the preference store
     * 
     * @param store
     *            the preference store
     * @param key
     *            the key
     * @return the Printer Data
     */
    public static PrinterData getPrinterData(IPreferenceStore store, String key) {

        PrinterData data = null;
        String driver = store.getString(key + DRIVER_KEY);
        String name = store.getString(key + NAME_KEY);
        // Make sure the driver and the name are not empty.
        if (driver != null && name != null && driver.length() != 0 && name.length() != 0) {
            data = new PrinterData(driver, name);
        }

        // Check if a printer have been found.
        if (data == null) {
            return null;
        }

        int scope = store.getInt(key + SCOPE_KEY);
        if (scope == PrinterData.ALL_PAGES || scope == PrinterData.PAGE_RANGE || scope == PrinterData.SELECTION) {
            data.scope = scope;
        }

        int startPage = store.getInt(key + START_PAGE_KEY);
        if (startPage > 0) {
            data.startPage = startPage;
        }
        int endPage = store.getInt(key + END_PAGE_KEY);
        if (endPage >= startPage) {
            data.endPage = endPage;
        }

        data.printToFile = store.getBoolean(key + PRINT_TO_FILE_KEY);

        data.fileName = store.getString(key + FILENAME_KEY);
        if (data.fileName.length() == 0) {
            data.fileName = null;
        } else if (data.fileName.startsWith("file://")) { //$NON-NLS-1$
            data.fileName = data.fileName.substring("file://".length()); //$NON-NLS-1$
        }

        int copyCount = store.getInt(key + COPY_COUNT_KEY);
        if (copyCount > 0) {
            data.copyCount = copyCount;
        }

        data.collate = store.getBoolean(key + COLLATE_KEY);

        int orientation = store.getInt(key + ORIENTATION_KEY);
        if (orientation == PrinterData.LANDSCAPE || orientation == PrinterData.PORTRAIT) {
            data.orientation = orientation;
        }
        return data;
    }
}
