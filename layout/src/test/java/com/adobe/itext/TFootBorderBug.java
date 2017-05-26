package com.adobe.itext;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.IOException;

/**
 * Demonstrate two bugs.
 */
public class TFootBorderBug {

	public static final String DEST = "target/results/TFootBorderBug.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new TFootBorderBug().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

		document.add(new Paragraph("This one looks good:"));
		Table table = new Table(new float[1]);
		table.addCell("A cell");
		document.add(table);

		document.add(new Paragraph("\nWhen text is bold, width is wrong:"));
		table = new Table(new float[1]);
		table.addCell("A cell").setBold();
		document.add(table);
		document.add(new Paragraph("I think the problem is in TextRenderer.layout. " +
				"The min and max width calculated doesn't include italicSkewAddition and boldSimulationAddition " +
				"in lines 310 and 311:\n" +
				"    widthHandler.updateMinChildWidth(nonBreakablePartWidthWhichDoesNotExceedAllowedWidth);\n" +
				"and several other places."));

		document.add(new Paragraph("\nThis one looks good:"));
		document.add(createTable(false, false));

		document.add(new Paragraph("\nWhen footer background is gray, footer top border disappears.\n" +
				"The cell height includes the border, but then the background is drawn over it.\n" +
				"It was correct before I updated my branch\n" +
				"(after a06ee1f4cf70bfb100dd913b804338279baae7e7 [a06ee1f])"));
		document.add(createTable(false, true));

		document.close();
	}

	private Table createTable(boolean boldHeader, boolean footerBackground) {
		// same with 2 rows of header and footer
		// set border on header and footer
		Table table = new Table(new float[3]);
		for (int r = 0; r < 1; r++) {
			for (int c = 0; c < 3; c++) {
				table.addHeaderCell(new Cell().add(String.format("header row %d col %d", r, c)).setBorder(Border.NO_BORDER).setBold());
			}
		}
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				table.addCell(new Cell().add(String.format("row %d col %d", r, c)).setBorder(Border.NO_BORDER));
			}
		}
		for (int r = 0; r < 1; r++) {
			for (int c = 0; c < 3; c++) {
				table.addFooterCell(new Cell().add(String.format("footer row %d col %d", r, c)).setBorder(Border.NO_BORDER));
			}
		}
		// must add cells to header and footer to create them
		table.getHeader()
				.setBorderTop(new SolidBorder(2))
				.setBorderBottom(new SolidBorder(1));
		if (boldHeader)
			table.getHeader().setBold();
		table.getFooter()
				.setBold()
				.setBorderTop(new SolidBorder(10))
				.setBorderBottom(new SolidBorder(1));
		if (footerBackground)
			table.getFooter().setBackgroundColor(Color.LIGHT_GRAY);

		return table;
	}
}