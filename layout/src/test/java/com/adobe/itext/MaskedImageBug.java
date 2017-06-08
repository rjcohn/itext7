package com.adobe.itext;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;

public class MaskedImageBug {

	public static final String IMAGE_ALPHA = "layout/src/test/resources/com/adobe/itext/MaskedImageBug/Meissner_alpha.png";
	public static final String IMAGE_NOALPHA = "layout/src/test/resources/com/adobe/itext/MaskedImageBug/Meissner_noalpha.png";
	public static final String DEST = "target/results/MaskedImageBug.pdf";

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new MaskedImageBug().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

		Paragraph paragraph = new Paragraph(
				"These images have a gray color model and depth 8.\n" +
				"Left has alpha, right does not (alpha removed using Mac Preview):\n" +
				"Source: https://en.wikipedia.org/wiki/Alkaloid#/media/File:Meissner_alkalod_definition_article_1819.png");
		paragraph.add(
				new Image(ImageDataFactory.create(IMAGE_ALPHA))
				.setBorder(new SolidBorder(1f))
				.setMarginLeft(10f));
		paragraph.add(
				new Image(ImageDataFactory.create(IMAGE_NOALPHA))
				.setBorder(new SolidBorder(1f))
				.setMarginLeft(10f));
		document.add(paragraph);

		document.close();
	}
}