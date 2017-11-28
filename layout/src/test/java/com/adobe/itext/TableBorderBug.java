package com.adobe.itext;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;

import java.io.File;
import java.io.IOException;

public class TableBorderBug {

	public static final String DEST = "target/results/TableBorderBug.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new TableBorderBug().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

		Style style = new Style();
		document.add(new Paragraph("This border is ok").addStyle(style));
		document.add(createTable());
		style.setProperty(Property.KEEP_WITH_NEXT, true);
		document.add(new Paragraph("Same table but this text has KEEP_WITH_NEXT").addStyle(style));
		document.add(createTable());

		document.close();
	}

	private Table createTable() {
		Table table = new Table(new float[2])
				.setBorderTop(new SolidBorder(2))
				.setBorderBottom(new SolidBorder(2))
				.setMarginTop(10)
				.setMarginBottom(10);
		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < 2; c++) {
				Cell cell = new Cell().add(new Paragraph(r + "," + c));
				if (r == 0)
					table.addHeaderCell(cell);
				else
					table.addCell(cell);
			}
		}
		table.getHeader()
				.setBold()
				.setBorderBottom(new SolidBorder(1));

		return table;
	}
}