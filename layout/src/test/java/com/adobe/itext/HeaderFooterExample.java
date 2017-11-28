/*

	This file is part of the iText (R) project.
	Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/27780756/adding-footer-with-itext-doesnt-work
 */
package com.adobe.itext;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.property.TabAlignment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HeaderFooterExample {
	public static final String DEST = "target/results/HeaderFooterTest.pdf";

	private static final String FONT_NAME = FontConstants.HELVETICA_OBLIQUE;
	private static final float FONT_SIZE = 10.0f;

	public static void main(String[] args) throws Exception {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new HeaderFooterExample().manipulatePdf(DEST);
	}

	protected void manipulatePdf(String dest) throws Exception {
		PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST, new WriterProperties().addXmpMetadata()));
		Document doc = new Document(pdfDoc);

		//Setting some required parameters
		pdfDoc.setTagged();
		pdfDoc.getCatalog().setLang(new PdfString("en-US"));
		pdfDoc.getCatalog().setViewerPreferences(
				new PdfViewerPreferences().setDisplayDocTitle(true));
		PdfDocumentInfo info = pdfDoc.getDocumentInfo();
		info.setTitle("iText7 Headers and Footers Example");

		pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterEventHandler(doc));
		int lastPage = 3;
		for (int i = 1; i <= lastPage; i++) {
			doc.add(new Paragraph("Test " + i));
			if (i !=  lastPage) {
				doc.add(new AreaBreak());
			}
		}
		doc.close();
	}


	protected class HeaderFooterEventHandler implements IEventHandler {
		protected Document doc;
		private PdfFont font;
		private PdfDictionary headerDict;
		private PdfDictionary footerDict;

		public HeaderFooterEventHandler(Document doc) {
			this.doc = doc;
			try {
				font = PdfFontFactory.createFont(FONT_NAME);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Map<PdfName, PdfObject> map = new HashMap<>();
			map.put(PdfName.Type, new PdfName("Pagination"));
			map.put(PdfName.Subtype, new PdfName("Header"));
			headerDict = new PdfDictionary(map);
			map = new HashMap<>();
			map.put(PdfName.Type, new PdfName("Pagination"));
			map.put(PdfName.Subtype, new PdfName("Footer"));
			footerDict = new PdfDictionary(map);
		}

		@Override
		public void handleEvent(Event event) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfDocument pdfDoc = docEvent.getDocument();
			PdfPage page = docEvent.getPage();
			PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
			Rectangle pageSize = page.getPageSize();
			Rectangle rect = new Rectangle(pageSize.getX() + doc.getLeftMargin(),
					pageSize.getTop() - doc.getTopMargin(),
					pageSize.getWidth() - doc.getLeftMargin() - doc.getRightMargin(),
					1.5f * FONT_SIZE);
			Date now = new Date();
			int pageNumber = pdfDoc.getPageNumber(page);
			Paragraph p = new Paragraph()
					.addTabStops(
							new TabStop(rect.getWidth() / 2, TabAlignment.CENTER),
							new TabStop(rect.getWidth(), TabAlignment.RIGHT))
					.add(new SimpleDateFormat("MMMM d, yyyy").format(now))
					.add(new Tab())
					.add(pdfDoc.getDocumentInfo().getTitle())
					.add(new Tab());

			switch (pageNumber) {
				case 1:  // skip
					return;
				case 2: // broken because 2nd text added is on a new line
					p.add("Page ").add(Integer.toString(pageNumber));
					break;
				default: // works - but can't do this if you want to do "Page i of n" where is is a Form
					p.add("Page "+ Integer.toString(pageNumber));
					break;
			}

			// Default top/bottom margin value for paragraph is 4
			//p.setRole(PdfName.Artifact); // doesn't seem to work
			canvas.beginMarkedContent(PdfName.Artifact, headerDict);
			new Canvas(canvas, pdfDoc, rect)
					.setFont(font)
					.setFontSize(FONT_SIZE)
					.add(p)
					.close();
			canvas.setStrokeColor(ColorConstants.BLACK)
					.setLineWidth(0.5f)
					.moveTo(rect.getLeft(), rect.getBottom())
					.lineTo(rect.getRight(), rect.getBottom())
					.stroke();
			canvas.endMarkedContent();
			rect = new Rectangle(pageSize.getX() + doc.getLeftMargin(),
					pageSize.getBottom() + doc.getBottomMargin() - 1.5f * FONT_SIZE,
					pageSize.getWidth() - doc.getLeftMargin() - doc.getRightMargin(),
					1.5f * FONT_SIZE);
			// Default top/bottom margin value for paragraph is 4
			p = new Paragraph()
					.setMargins(0, 0, 0, 0)
					.addTabStops(new TabStop(rect.getWidth() / 2, TabAlignment.CENTER))
					.add(new Tab())
					.add("this is a really long footer so we can see if it's centered");
			canvas.beginMarkedContent(PdfName.Artifact, footerDict);
			new Canvas(canvas, pdfDoc, rect)
					.setFont(font)
					.setFontSize(FONT_SIZE)
					.add(p);
			canvas.endMarkedContent();
		}

		public void handleEventOLD(Event event) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfPage page = docEvent.getPage();
			PdfCanvas canvas = new PdfCanvas(page);
			Rectangle pageSize = page.getPageSize();
			canvas.beginText();
			try {
				canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE), 15);
			} catch (IOException e) {
				e.printStackTrace();
			}
			double x = (pageSize.getRight() - doc.getRightMargin() - (pageSize.getLeft() + doc.getLeftMargin())) / 2 + doc.getLeftMargin();
			double y = pageSize.getTop() - doc.getTopMargin() + 10;
			canvas.moveText(x, y)
					.showText("this is a really long header so we can see if it's centered")
					.moveText(0, (pageSize.getBottom() + doc.getBottomMargin()) - (pageSize.getTop() + doc.getTopMargin()) - 20)
					.showText("this is a footer")
					.endText()
					.release();
		}
	}
}
