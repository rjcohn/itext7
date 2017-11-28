package com.adobe.itext;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.IOException;

// Three different bugs (described in the output)
public class TableWithCaption {

	private static final String DEST = "target/results/TableWithCaption.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new TableWithCaption().createPdf(DEST);
	}

	private void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();
		Paragraph p;

		p = new Paragraph("We try to create a Table with a Caption by creating a Div with two children: " +
				"a Div that is a caption and a Table. " +
				"To tag this correctly, I set the outer Div role to Table, the inner Div to Caption, and the " +
				"Table to null." +
				"The table contents don't get tagged correctly. " +
				"When the role is set to null, the Table tries to turn off tagging. " +
				"This means that there are no TR tags. " +
				"(But there are still TD tags because Cells don't check if their parent table is tagged.)" +
				"One fix would be to check if the Table's parent is tagged (or any parent).");
		document.add(p);

		p = new Paragraph("This table is tagged correctly.");
		document.add(p);
		document.add(createTable(false));

		p = new Paragraph("This table has a caption and is tagged incorrectly. " +
				"Also note that (I believe) the caption is centered incorrectly. " +
				"The width of the div is computed to be the full width of the column, " +
				"so the text is centered in the column. " +
				"The width should be computed to be the width of the widest element (the table). " +
				"If this were done, then the caption would be centered on the table. " +
				"I saw this before when looking at widths. " +
				"The text width is initialized to be the full width so that it's place correctly " +
				"for various text alignments. " +
				"It would be better to calculate the width and then place the text.");
		document.add(p);
		document.add(createTable(true));

		document.close();
	}

	private IBlockElement createTable(boolean useCaption) {
		Table table = new Table(new float[3])
				.setMarginTop(10)
				.setMarginBottom(10);
		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < 3; c++) {
				String content = r + "," + c;
				Cell cell = new Cell();
				cell.add(new Paragraph(content));
				table.addCell(cell);
			}
		}
		if (useCaption) {
			Div div = new Div();
			div.getAccessibilityProperties().setRole(StandardRoles.TABLE);
			Paragraph p = new Paragraph("Caption");
			p.getAccessibilityProperties().setRole(null);
			p.setTextAlignment(TextAlignment.CENTER).setBold();
			Div caption = new Div().add(p);
			caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
			div.add(caption);
			table.getAccessibilityProperties().setRole(null);
			div.add(table);
			return div;
		} else
			return table;
	}
}