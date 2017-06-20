package com.adobe.itext;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class FloatExample {

	private static final String IMAGE_SRC = "layout/src/test/resources/com/adobe/itext/FloatExample/%d.png";
	private static final String DEST = "target/results/FloatExample.pdf";

	private static final float IMAGE_WIDTH = 200f;
	private static final float IMAGE_WIDTH_PERCENT = 33f;

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new FloatExample().createPdf(DEST);
	}

	private void createPdf(String dest) throws IOException {
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

		ImageWithProperties[] images = new ImageWithProperties[4];
		UnitValue width = new UnitValue(UnitValue.PERCENT, 33f);
		images[0] = new ImageWithProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER, width);
		images[1] = new ImageWithProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER, width);
		images[2] = new ImageWithProperties(FloatPropertyValue.RIGHT, clearValue, null, width);
		images[3] = new ImageWithProperties(FloatPropertyValue.RIGHT, clearValue, null, width);
		Paragraph paragraph = new Paragraph()
				.add("Four images followed by two paragraphs.\n");
		if (wrapImages) {
			paragraph.add("Each image is wrapped in a div.\n");
			paragraph.add("All divs specify CLEAR = " + clearValue + ".\n");
			paragraph.add("All images specify WIDTH = " + width + ".\n");
		}
		else
			paragraph.add("All images specify CLEAR = " + clearValue + ", WIDTH = " + width + "\n");

		for (int i = 1; i < images.length; i++) {
			paragraph.add("Image " + (i+1) + ": " + images[i] + "\n");
		}
		document.add(paragraph);

		for (int i = 1; i < images.length; i++) {
			Image image = new Image(ImageDataFactory.create(String.format(IMAGE_SRC, i + 1)))
					.setBorder(new SolidBorder(1f));
			image.setProperty(Property.WIDTH, images[i].width);
			if (wrapImages) {
				Div div = new Div();
				image.setHorizontalAlignment(images[i].horizontalAlignment);
				div.setProperty(Property.CLEAR, images[i].clearPropertyValue);
				div.setProperty(Property.FLOAT, images[i].floatPropertyValue);
				div.add(image);
				document.add(div);
			} else {
				image.setHorizontalAlignment(images[i].horizontalAlignment);
				image.setProperty(Property.CLEAR, images[i].clearPropertyValue);
				image.setProperty(Property.FLOAT, images[i].floatPropertyValue);
				document.add(image);
			}
		}

		document.add(new Paragraph("The following outline is provided as an over-view of and topical guide to Zambia:"));
		document.add(new Paragraph("Zambia – landlocked sovereign country located in Southern Africa.[1] Zambia has been inhabited for thousands of years by hunter-gatherers and migrating tribes. After sporadic visits by European explorers starting in the 18th century, Zambia was gradually claimed and occupied by the British as protectorate of Northern Rhodesia towards the end of the nineteenth century. On 24 October 1964, the protectorate gained independence with the new name of Zambia, derived from the Zam-bezi river which flows through the country. After independence the country moved towards a system of one party rule with Kenneth Kaunda as president. Kaunda dominated Zambian politics until multiparty elections were held in 1991."));
	}

	class ImageWithProperties {

		FloatPropertyValue floatPropertyValue;
		ClearPropertyValue clearPropertyValue;
		HorizontalAlignment horizontalAlignment;
		UnitValue width;

		public ImageWithProperties(FloatPropertyValue floatPropertyValue, ClearPropertyValue clearPropertyValue,
		                           HorizontalAlignment horizontalAlignment, UnitValue width) {
			this.floatPropertyValue = floatPropertyValue;
			this.clearPropertyValue = clearPropertyValue;
			this.horizontalAlignment = horizontalAlignment;
			this.width = width;
		}

		public String toString() {
			return "float="+floatPropertyValue + " clear="+clearPropertyValue + " horiz_align="+horizontalAlignment;
		}
	}
}
