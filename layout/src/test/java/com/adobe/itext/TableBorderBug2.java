package com.adobe.itext;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.ListNumberingType;

import java.io.File;
import java.io.IOException;

// Three different bugs (described in the output)
public class TableBorderBug2 {

	public static final String DEST = "target/results/TableBorderBug2.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new TableBorderBug2().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();
		pdf.setDefaultPageSize(new PageSize(252f, 360f));
		Paragraph p;

		Div div = new Div().setFontSize(10);
		div.add(new Paragraph("This code shows three different bugs:"));
		List list = new List().setListSymbol(ListNumberingType.DECIMAL);
		list.add(new ListItem(
				"If a table follows an element (e.g. paragraph) that has KEEP_WITH_NEXT " +
						"and the table has a split row, the text is messed up and the non-split cells " +
						"don't show up."));
		list.add(new ListItem(
				"If a Style is used to set properties for a table Cell and that cell is in a split row, " +
						"the properties are lost because the Cell styles aren't cloned in Cell.clone(). " +
						"You can see this at the bottom border of last row of the first page."));
		list.add(new ListItem(
				"If a table Cell is built up from repeated calls to add(String), the cell text is" +
						"wrapped at the individual string boundaries, not according to the cumulative content."));
		div.add(list);
		document.add(div);
		document.add(new AreaBreak());

		p = new Paragraph("This table looks ok. It doesn't use keep with next or a Style or multiple Cell.add calls");
		document.add(p);
		document.add(createTable(false, false));
		document.add(new AreaBreak());

		p = new Paragraph("This table messes up the split row. The pre-split content disappears. " +
				"This text has keep with next.")
				.setKeepWithNext(true);
		document.add(p);
		document.add(createTable(false, false));
		document.add(new AreaBreak());

		p = new Paragraph("This table messes up the border of the split row. It uses a Style.");
		document.add(p);
		document.add(createTable(true, false));
		document.add(new AreaBreak());

		p = new Paragraph("This table messes up text wrapping. It uses multiple Cell.add calls.");
		document.add(p);
		document.add(createTable(false, true));

		document.close();
	}

	private Table createTable(boolean useStyle, boolean useMultipleAdds) {
		Table table = new Table(new float[5])
				.setBorder(Border.NO_BORDER)
				.setMarginTop(10)
				.setMarginBottom(10);
		Style cellStyle = new Style();
		cellStyle.setBorderLeft(Border.NO_BORDER)
				.setBorderRight(Border.NO_BORDER)
				.setBorderTop(new SolidBorder(Color.BLUE, 1))
				.setBorderBottom(new SolidBorder(Color.BLUE, 1));
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 5; c++) {
				// BUG: If content is added in pieces with 2 add() calls, text is wrapped at add boundary
				String content = r + "," + c;
				Cell cell = new Cell();
				if (useMultipleAdds) {
					cell.add(content);
					content = "";
				}
				if (useStyle) {
					cell.addStyle(cellStyle);
				} else {
					cell.setBorderLeft(Border.NO_BORDER)
							.setBorderRight(Border.NO_BORDER)
							.setBorderTop(new SolidBorder(Color.BLUE, 1))
							.setBorderBottom(new SolidBorder(Color.BLUE, 1));
				}
				if (c == 4) {
					if (r == 4)
						content += " Powerful and large tornado remained over rural areas.";
					else
						content += " some more text";
				}
				cell.add(content);
				if (r == 0)
					table.addHeaderCell(cell);
				else
					table.addCell(cell);
			}
		}
		table.getHeader()
				.setBold()
				.setBorderTop(new SolidBorder(Color.BLUE, 1))
				.setBorderBottom(new SolidBorder(Color.BLUE, 1));

		return table;
	}
}