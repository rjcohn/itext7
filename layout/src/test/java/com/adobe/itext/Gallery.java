package com.adobe.itext;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.*;
import com.itextpdf.test.annotations.WrapToTest;

import java.io.File;
import java.io.IOException;

@WrapToTest
public class Gallery {

	private static final String IMAGE_SRC = "layout/src/test/resources/com/adobe/itext/FloatExample/%d.png";
	private static final int N_IMAGES = 4;
	public static final String DEST = "target/results/Gallery.pdf";
	public static final String IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus non est sapien. Curabitur ultricies odio ac nibh pulvinar ornare. Sed non magna lectus. Etiam vitae scelerisque diam. Duis rhoncus ante sed massa volutpat, eu blandit lorem aliquet. Cras tristique sodales efficitur. Fusce tortor ligula, dictum ut hendrerit in, ornare et mi. Integer lacus massa, imperdiet non dolor vitae, rhoncus gravida augue. Nunc mollis ex vel felis vestibulum lacinia. Fusce tempus lacus in leo rhoncus, et rutrum augue vestibulum. Aenean blandit massa nec sollicitudin rutrum. Mauris faucibus massa orci, sit amet euismod risus ullamcorper ac. Donec ullamcorper bibendum pharetra. Vestibulum eget laoreet mi, a pulvinar ante. ";
	//Vestibulum rutrum posuere est, id commodo purus feugiat ut. Pellentesque enim augue, euismod nec erat sit amet, condimentum ultrices odio.

	public static void main(String args[]) throws IOException {
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new Gallery().createPdf(DEST);
	}

	public void createPdf(String dest) throws IOException {
		PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
		Document document = new Document(pdf);
		pdf.setTagged();

		document.add(new Paragraph("Image Gallery")
				.setTextAlignment(TextAlignment.CENTER)
				.setFontSize(24f)
				.setBold()
				.setMarginBottom(12f));
		document.add(new Paragraph(IPSUM));

		Div gallery = new Div()
				.setHorizontalAlignment(HorizontalAlignment.CENTER);
		for (int i = 0; i < 4; i++) {
			Div item = createItem(i);
			gallery.add(item);
		}
		document.add(gallery);

		Paragraph p = new Paragraph(IPSUM);
		p.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
		document.add(p);
		document.close();
	}

	private Div createItem(int i) throws IOException {
		Div div = new Div()
				//item.setWidth(100);
				;
		div.setRole(PdfName.Figure);
		div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

		String url = String.format(IMAGE_SRC, i % N_IMAGES + 1);
		Image img = new Image(ImageDataFactory.create(url))
				.setWidthPercent(100)
				.setMaxHeight(50);
		div.add(img);

		Div caption = new Div();
		caption.setRole(PdfName.Caption);
		Paragraph captionText = new Paragraph("Image " + i);
		captionText.setRole(null);
		caption.add(captionText);
		div.add(caption);

		return div;
	}

}
