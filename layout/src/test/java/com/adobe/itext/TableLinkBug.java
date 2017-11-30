package com.adobe.itext;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.IOException;

public class TableLinkBug {

	public static final String DEST = "target/results/TableLinkBug.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new TableLinkBug().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);
		pdf.setDefaultPageSize(PageSize.A6);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

		document.add(new Paragraph(
				"Sumter County is part of the so-called Black Belt region of central Alabama. " +
						"The region has suffered significant economic depression in recent years. " +
						"But in April 2008, United States Steel announced plans to build at $150 million alloy plant " +
						"near the community of Epes about 50 miles (80 km) southwest of Tuscaloosa, Alabama. " +
						"The plant will require 250 workers to construct in a town of only 206. Up to 235 full-time jobs " +
						"will be created when completed with jobs paying about $50 thousand annually."
				));
		document.add(createTable());

		document.close();
	}

	private Table createTable() {
		final int ROWS = 6;
		final int COLS = 3;
		Table table = new Table(new float[COLS]);
		for (int r = 0; r < 1; r++) {
			for (int c = 0; c < COLS; c++) {
				Cell cell = new Cell();
				String text = String.format("header row %d col %d", r, c);
				if (c <= 1) {
					PdfAction act = PdfAction.createURI("http://www.example.com/");
					Link link = new Link(text, act);
					link.setFontColor(ColorConstants.BLUE);
					if (c == 1) {
						int[] borderVals = {0, 0, 0};
						link.getLinkAnnotation().setBorder(new PdfArray(borderVals));
					}
					cell.add(new Paragraph(link));
				}
				else
					cell.add(new Paragraph(text));
				table.addHeaderCell(cell.setBold());
			}
		}
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				table.addCell(new Cell().add(new Paragraph(String.format("row %d col %d", r, c))));
			}
		}

		return table;
	}
}