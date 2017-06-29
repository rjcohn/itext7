package com.adobe.itext;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
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

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new FloatExample().createPdf(DEST);
	}

	private static final Color IMAGE_BORDER_COLOR = Color.LIGHT_GRAY;
	private static final float BORDER_MARGIN = 5f;
	private static final float IMAGE_BORDER_WIDTH = 5f;
	private static final float DIV_BORDER_WIDTH = 1f;

	private static final UnitValue IMAGE_WIDTH = new UnitValue(UnitValue.PERCENT, 33f);
	private static final UnitValue DIV_IMAGE_WIDTH = new UnitValue(UnitValue.POINT, 150f);

	private void createPdf(String dest) throws IOException {
		//Initialize PDF writer
		PdfWriter writer = new PdfWriter(dest);

		//Initialize PDF document
		PdfDocument pdf = new PdfDocument(writer);

		// Initialize document
		Document document = new Document(pdf);
		pdf.setTagged();

//		addContent(document, false, false, ClearPropertyValue.BOTH);
//		document.add(new AreaBreak());
//		addContent(document, true, false, ClearPropertyValue.BOTH);
//		document.add(new AreaBreak());
		addContent(document, false, true, ClearPropertyValue.BOTH);
//		document.add(new AreaBreak());
//		addContent(document, true, true, ClearPropertyValue.BOTH);
//		document.add(new AreaBreak());
//		addContent(document, false, ClearPropertyValue.NONE);
//		document.add(new AreaBreak());
//		addContent(document, true, ClearPropertyValue.NONE);

		document.close();
	}

	private void addContent(Document document, boolean maxWidth, boolean wrapImages, ClearPropertyValue clearValue)
			throws MalformedURLException {

		ImageProperties[] images = new ImageProperties[4];
		int widthProperty = maxWidth ? Property.WIDTH : Property.WIDTH;
		images[0] = new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER, IMAGE_WIDTH);
		images[1] = new ImageProperties(FloatPropertyValue.NONE, clearValue, HorizontalAlignment.CENTER, IMAGE_WIDTH);
		images[2] = new ImageProperties(FloatPropertyValue.RIGHT, clearValue, HorizontalAlignment.CENTER, IMAGE_WIDTH);
		images[3] = new ImageProperties(FloatPropertyValue.RIGHT, clearValue, HorizontalAlignment.CENTER, IMAGE_WIDTH);
		Paragraph paragraph = new Paragraph()
				.add("Four images followed by two paragraphs.\n");
		if (wrapImages)
			paragraph.add("Each image is wrapped in a div.\n");
		String elName = wrapImages ? "div" : "image";
		paragraph.add("All " + elName + "s specify CLEAR = " + clearValue + ", " +
				(maxWidth ? "MAX_WIDTH" : "WIDTH") + "= " + IMAGE_WIDTH + ".\n");
		if (wrapImages)
			paragraph.add("All images specify MAX_WIDTH = " + DIV_IMAGE_WIDTH + ".\n");

		for (int i = 1; i < images.length; i++) {
			paragraph.add(elName + " " + (i) + ": " + images[i] + "\n");
		}
		document.add(paragraph);

		for (int i = 1; i < images.length; i++) {
			Image image = new Image(ImageDataFactory.create(String.format(IMAGE_SRC, i + 1)))
					.setBorder(new SolidBorder(IMAGE_BORDER_COLOR, IMAGE_BORDER_WIDTH));
			if (wrapImages) {
				Div div = new Div()
						.setBorder(new SolidBorder(DIV_BORDER_WIDTH))
						.setMargins(BORDER_MARGIN, 0, BORDER_MARGIN, BORDER_MARGIN);
				div.setHorizontalAlignment(images[i].horizontalAlignment);
				div.setProperty(Property.CLEAR, images[i].clearPropertyValue);
				div.setProperty(Property.FLOAT, images[i].floatPropertyValue);
				div.setProperty(widthProperty, images[i].width);
				image.setProperty(widthProperty, DIV_IMAGE_WIDTH);
				div.add(image);
				document.add(div);
			} else {
				image.setMargins(BORDER_MARGIN, 0, BORDER_MARGIN, BORDER_MARGIN);
				image.setHorizontalAlignment(images[i].horizontalAlignment);
				image.setProperty(Property.CLEAR, images[i].clearPropertyValue);
				image.setProperty(Property.FLOAT, images[i].floatPropertyValue);
				image.setProperty(widthProperty, images[i].width);
				document.add(image);
			}
		}

		document.add(new Paragraph("The following outline is provided as an over-view of and topical guide to Zambia:"));
		document.add(new Paragraph("Zambia – landlocked sovereign country located in Southern Africa.[1] Zambia has been inhabited for thousands of years by hunter-gatherers and migrating tribes. After sporadic visits by European explorers starting in the 18th century, Zambia was gradually claimed and occupied by the British as protectorate of Northern Rhodesia towards the end of the nineteenth century. On 24 October 1964, the protectorate gained independence with the new name of Zambia, derived from the Zam-bezi river which flows through the country. After independence the country moved towards a system of one party rule with Kenneth Kaunda as president. Kaunda dominated Zambian politics until multiparty elections were held in 1991."));
	}

	class ImageProperties {

		FloatPropertyValue floatPropertyValue;
		ClearPropertyValue clearPropertyValue;
		HorizontalAlignment horizontalAlignment;
		UnitValue width;

		ImageProperties(FloatPropertyValue floatPropertyValue, ClearPropertyValue clearPropertyValue,
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