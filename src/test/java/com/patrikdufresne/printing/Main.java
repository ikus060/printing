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

import net.sf.paperclips.Print;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.SeriesPrint;
import net.sf.paperclips.TextPrint;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Display display = Display.getDefault();

        PrintFactory factory = new PrintFactory("My Job Name") {

            @Override
            public Print createPrint() {

                // Create multiple page print.
                SeriesPrint series = new SeriesPrint();

                series.add(new TextPrint("Page 1 with some data"));

                series.add(new TextPrint("Page 2 will other data"));

                series.add(new TextPrint("Page 3 last page"));

                return series;

            }
        };

        PrintJob job = new PrintJob(factory.getName(), factory.createPrint());
        PrintPreviewDialog dlg = new PrintPreviewDialog((Shell) null);
        dlg.setPrintJob(job);
        dlg.open();

    }

}
