/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.adobe.itext;


import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class FloatTest2 extends ExtendedITextTest {

    public static final String imageFolder = "./src/test/resources/com/itextpdf/layout/FloatTest/";
    public static final String sourceFolder = "./src/test/resources/com/adobe/layout/FloatTest2/";
    public static final String destinationFolder = "./target/test/com/adobe/layout/FloatTest2/";

    private static final String text =
            "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document. " +
            "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries. " +
            "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme. " +
            "Save time in Word with new buttons that show up where you need them. To change the way a picture fits in your document, click it and a button for layout options appears next to it. When you work on a table, click where you want to add a row or a column, and then click the plus sign. " +
            "Reading is easier, too, in the new Reading view. You can collapse parts of the document and focus on the text you want. If you need to stop reading before you reach the end, Word remembers where you left off - even on another device. ";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void floatingImageToNextPage() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingImageToNextPage.pdf";
        String outFile = destinationFolder + "floatingImageToNextPage.pdf";
        String imageSrc = imageFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(100, 100);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleAbsolute(100, 500);
        img1.setMarginLeft(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(new Paragraph(text));
        document.add(new Paragraph(text));
        document.add(img2);
        document.add(new Paragraph(text));
        document.close();

        //Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void floatingTwoImages() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTwoImages.pdf";
        String outFile = destinationFolder + "floatingTwoImages.pdf";
        String imageSrc = imageFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(400, 400);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(400, 400);
        img1.setMarginRight(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(img2);
        document.add(new Paragraph(text));
        document.close();

        //Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void floatingTwoImagesLR() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatingTwoImagesLR.pdf";
        String outFile = destinationFolder + "floatingTwoImagesLR.pdf";
        String imageSrc = imageFolder + "itis.jpg";

        Document document = new Document(new PdfDocument(new PdfWriter(outFile)));

        Image img1 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(350, 350);
        Image img2 = new Image(ImageDataFactory.create(imageSrc)).scaleToFit(350, 350);
        img1.setMarginLeft(10);
        img1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        img2.setMarginRight(10);
        img2.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

        document.add(img1);
        document.add(img2);
        document.add(new Paragraph(text));
        document.close();

        //Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }


}
