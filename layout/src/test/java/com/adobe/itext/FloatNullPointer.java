package com.adobe.itext;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.Property;

import java.io.File;
import java.io.IOException;

// No longer actually gets a NullPointerException
// but it does have bad layout without a fix to RootRenderer to reset the margin handler
public class FloatNullPointer {

	private static final String DEST = "target/results/FloatNullPointer.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new FloatNullPointer().createPdf(DEST);
	}

	private void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);
		pdf.setDefaultPageSize(new PageSize(600, 350));
		pdf.setTagged();

		// Initialize document
		Document document = new Document(pdf);

		// Document layout is correct if COLLAPSING_MARGINS is not true
		document.setProperty(Property.COLLAPSING_MARGINS, true);

		document.add(new Paragraph("Some text\nSome text\nSome text\nSome text\nSome text\nSome text"));
		byte data[] = new byte[1];
		ImageData raw = ImageDataFactory.create(1, 1, 1, 8, data, null);
		Image image = new Image(raw).setHeight(200);
		Div div = new Div();
		div.add(image);
		Div captionDiv = new Div();
		captionDiv.add(new Paragraph("Caption line 1\n").add("line 2"));
		div.add(captionDiv);
		div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
		//div.setKeepTogether(true);
		document.add(div);
		document.add(new Paragraph("After float"));
		document.add(new List(ListNumberingType.DECIMAL)
				.add("Some text\nSome text\nSome text\nSome text")
				.add("Some text\nSome text\nSome text")
				.add("Some text\nSome text")
				.add("Some text\nSome text"));

		document.close();
	}
}
