package com.adobe.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class FloatTest3 {

	public static final String imageFolder = "layout/src/test/resources/com/adobe/layout/FloatTest3/";
	public static final String IMAGE_1 = imageFolder + "1.png";
	public static final String IMAGE_2 = imageFolder + "2.png";
	public static final String IMAGE_3 = imageFolder + "3.png";
	public static final String IMAGE_4 = imageFolder + "4.png";
	public static final String DEST = "target/results/FloatTest3.pdf";

	public static final float IMAGE_WIDTH = 200f;

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new FloatTest3().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

		addContent(document, false, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());
		addContent(document, true, ClearPropertyValue.BOTH);
		document.add(new AreaBreak());
		addContent(document, false, ClearPropertyValue.NONE);
		document.add(new AreaBreak());
		addContent(document, true, ClearPropertyValue.NONE);

		document.close();
	}

	private void addContent(Document document, boolean wrapImages, ClearPropertyValue clearValue)
			throws MalformedURLException {
		Paragraph paragraph = new Paragraph()
				.add("Four images followed by two paragraphs.\n");
		if (wrapImages) {
			paragraph.add("Each image is wrapped in a div.\n");
			paragraph.add("All divs specify CLEAR = " + clearValue + ".\n");
			paragraph.add("All images specify WIDTH = " + (int) IMAGE_WIDTH + ".\n");
		}
		else
			paragraph.add("All images specify CLEAR = " + clearValue + ", WIDTH = " + (int) IMAGE_WIDTH + "\n");
		paragraph
				.add("Image 1: FLOAT = LEFT\n")
				.add("Image 2: FLOAT = RIGHT\n")
				.add("Image 3: FLOAT = NONE, HORIZONTAL_ALIGNMENT = CENTER\n")
				.add("Image 4: FLOAT = LEFT");
		document.add(paragraph);

		Image image = new Image(ImageDataFactory.create(IMAGE_1))
				.setBorder(new SolidBorder(1f))
				.setWidth(IMAGE_WIDTH);
		if (wrapImages) {
			Div div = new Div();
			div.setProperty(Property.CLEAR, clearValue);
			div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
			div.add(image);
			document.add(div);
		}
		else {
			image.setProperty(Property.CLEAR, clearValue);
			image.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
			document.add(image);
		}

		//document.add(new Paragraph("Text between image 1 & 2"));

		image = new Image(ImageDataFactory.create(IMAGE_2))
				.setBorder(new SolidBorder(1f))
				.setWidth(IMAGE_WIDTH);
		if (wrapImages) {
			Div div = new Div();
			div.setProperty(Property.CLEAR, clearValue);
			div.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
			div.add(image);
			document.add(div);
		} else {
			image.setProperty(Property.CLEAR, clearValue);
			image.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
			document.add(image);
		}

		//document.add(new Paragraph("Text between image 2 & 3"));

		image = new Image(ImageDataFactory.create(IMAGE_3))
				.setBorder(new SolidBorder(1f))
				.setWidth(IMAGE_WIDTH);
		if (wrapImages) {
			Div div = new Div();
			div.setHorizontalAlignment(HorizontalAlignment.CENTER);
			div.setProperty(Property.CLEAR, clearValue);
			div.setProperty(Property.FLOAT, FloatPropertyValue.NONE);
			div.add(image);
			document.add(div);
		} else {
			image.setHorizontalAlignment(HorizontalAlignment.CENTER);
			image.setProperty(Property.CLEAR, clearValue);
			image.setProperty(Property.FLOAT, FloatPropertyValue.NONE);
			document.add(image);
		}

		//document.add(new Paragraph("Text between image 3 & 4"));

		image = new Image(ImageDataFactory.create(IMAGE_4))
				.setBorder(new SolidBorder(1f))
				.setWidth(IMAGE_WIDTH);
		if (wrapImages) {
			Div div = new Div();
			div.setProperty(Property.CLEAR, clearValue);
			div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
			div.add(image);
			document.add(div);
		} else {
			image.setProperty(Property.CLEAR, clearValue);
			image.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
			document.add(image);
		}

		document.add(new Paragraph("The following outline is provided as an over-view of and topical guide to Zambia:"));
		document.add(new Paragraph("Zambia – landlocked sovereign country located in Southern Africa.[1] Zambia has been inhabited for thousands of years by hunter-gatherers and migrating tribes. After sporadic visits by European explorers starting in the 18th century, Zambia was gradually claimed and occupied by the British as protectorate of Northern Rhodesia towards the end of the nineteenth century. On 24 October 1964, the protectorate gained independence with the new name of Zambia, derived from the Zam-bezi river which flows through the country. After independence the country moved towards a system of one party rule with Kenneth Kaunda as president. Kaunda dominated Zambian politics until multiparty elections were held in 1991."));
	}
}
