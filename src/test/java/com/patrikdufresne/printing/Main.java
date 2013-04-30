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

import org.eclipse.nebula.paperclips.core.AlignPrint;
import org.eclipse.nebula.paperclips.core.ColumnPrint;
import org.eclipse.nebula.paperclips.core.LayerPrint;
import org.eclipse.nebula.paperclips.core.LinePrint;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.ScalePrint;
import org.eclipse.nebula.paperclips.core.SeriesPrint;
import org.eclipse.nebula.paperclips.core.SidewaysPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.swt.SWT;
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

                // Test LayerPrint with offset
                LayerPrint layerPrint = new LayerPrint();
                layerPrint.add(new LinePrint());

                layerPrint.add(new OffsetPrint(new TextPrint("Top-left (0,0)."), SWT.LEFT, SWT.TOP, 0, 0));

                layerPrint.add(new OffsetPrint(new TextPrint("Top-Left(36,36)."), SWT.LEFT, SWT.TOP, 36, 36));

                layerPrint.add(new OffsetPrint(new TextPrint("Top-right (0,0)"), SWT.RIGHT, SWT.TOP, 0, 0));

                layerPrint.add(new OffsetPrint(new TextPrint("Bottom-left (72,72)"), SWT.LEFT, SWT.BOTTOM, 72, 72));

                layerPrint.add(new OffsetPrint(new LinePrint(), SWT.LEFT, SWT.BOTTOM, 72, 144));

                layerPrint.add(new OffsetPrint(new TextPrint("Bottom-right (36,36)"), SWT.RIGHT, SWT.BOTTOM, 36, 36));

                layerPrint.add(new PaddingPrint(new LinePrint(SWT.VERTICAL), 36, 288 - 36, 36, 288));

                series.add(layerPrint);

                // Test with MaxSizePrint
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < 25; i++) {
                    buf.append("This is some text");
                }
                LayerPrint layerPrint2 = new LayerPrint();
                // Limited width
                layerPrint2.add(new MaxSizePrint(new TextPrint("36pt width " + buf.toString()), 36, SWT.DEFAULT));

                // Limited height
                layerPrint2.add(new MaxSizePrint(new SidewaysPrint(new TextPrint("72pt height " + buf.toString())), SWT.DEFAULT, 72));

                series.add(layerPrint2);

                return series;

            }
        };

        PrintJob job = new PrintJob(factory.getName(), factory.createPrint());
        PrintPreviewDialog dlg = new PrintPreviewDialog((Shell) null);
        dlg.setPrintJob(job);
        dlg.open();

    }
}
