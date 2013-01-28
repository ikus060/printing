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
