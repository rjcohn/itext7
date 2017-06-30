/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

public class ImageRenderer extends AbstractRenderer implements ILeafElementRenderer {

    protected Float fixedXPosition;
    protected Float fixedYPosition;
    protected float pivotY;
    protected float deltaX;
    protected float imageWidth;
    protected float imageHeight;
    float[] matrix = new float[6];
    private Float height;
    private Float width;
    private float imageItselfScaledWidth;
    private float imageItselfScaledHeight;
    private Rectangle initialOccupiedAreaBBox;
    private float rotatedDeltaX;
    private float rotatedDeltaY;

    /**
     * Creates an ImageRenderer from its corresponding layout object.
     *
     * @param image the {@link com.itextpdf.layout.element.Image} which this object should manage
     */
    public ImageRenderer(Image image) {
        super(image);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea().clone();
        Rectangle layoutBox = area.getBBox().clone();
        Float retrievedWidth = retrieveWidth(layoutBox.getWidth());

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        float clearHeightCorrection = FloatingHelper.calculateClearHeightCorrection(this, floatRendererAreas, layoutBox);
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);
        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            layoutBox.decreaseHeight(clearHeightCorrection);
            FloatingHelper.adjustFloatedBlockLayoutBox(this, layoutBox, retrievedWidth, floatRendererAreas, floatPropertyValue);
        } else {
            clearHeightCorrection = FloatingHelper.adjustLayoutBoxAccordingToFloats(floatRendererAreas, layoutBox, retrievedWidth, clearHeightCorrection, null);
        }
        this.width = retrievedWidth;
        height = retrieveHeight();

        applyMargins(layoutBox, false);
        Border[] borders = getBorders();
        applyBorderBox(layoutBox, borders, false);

		// TODO perhaps follow pattern in BlockRenderer - check for 100%
        // Compute min/max width based on margins and borders but not layout
	    // Without this adjustment, image width set to maxWidth won't fit since it will exceed border
	    // - same for width set to specified width?
        Rectangle wBox = area.getBBox().clone();
        applyMargins(wBox, false);
        applyBorderBox(wBox, borders, false);
        Float minWidth = retrieveMinWidth(wBox.getWidth());
        Float maxWidth = retrieveMaxWidth(wBox.getWidth());

        if (isAbsolutePosition()) {
            applyAbsolutePosition(layoutBox);
        }

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        Image modelElement = (Image) (getModelElement());
        PdfXObject xObject = modelElement.getXObject();
        imageWidth = modelElement.getImageWidth();
        imageHeight = modelElement.getImageHeight();

        if (width == null && height == null) {
            width = imageWidth;
            height = (float) width / imageWidth * imageHeight;
        } else if (width == null) {
            width = (float) height / imageHeight * imageWidth;
        } else if (height == null) {
            height = (float) width / imageWidth * imageHeight;
        }

        fixedXPosition = this.getPropertyAsFloat(Property.X);
        fixedYPosition = this.getPropertyAsFloat(Property.Y);

        Float horizontalScaling = this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);
        Float verticalScaling = this.getPropertyAsFloat(Property.VERTICAL_SCALING, 1f);

        AffineTransform t = new AffineTransform();

        if (xObject instanceof PdfFormXObject && width != imageWidth) {
            horizontalScaling *= width / imageWidth;
            verticalScaling *= height / imageHeight;
        }

        if (horizontalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale((float) horizontalScaling, 1);
                width = imageWidth * (float) horizontalScaling;
            } else {
                width *= (float) horizontalScaling;
            }
        }
        if (verticalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale(1, (float) verticalScaling);
                height = imageHeight * (float) verticalScaling;
            } else {
                height *= (float) verticalScaling;
            }
        }

        // Constrain width and height according to min/max width
        if (null != minWidth && width < minWidth) {
            height *= minWidth / width;
            width = minWidth;
        } else if (null != maxWidth && width > maxWidth) {
            height *= maxWidth / width;
            width = maxWidth;
        }

        // Constrain width and height according to min/max height, which has precedence over width settings
        if (null != retrieveMinHeight() && height < retrieveMinHeight()) {
            width *= retrieveMinHeight() / height;
            height = retrieveMinHeight();
        } else if (null != retrieveMaxHeight() && height > retrieveMaxHeight()) {
            width *= retrieveMaxHeight() / height;
            height = retrieveMaxHeight();
        } else if (null != retrieveHeight() && !height.equals(retrieveHeight())) {
            width *= retrieveHeight() / height;
            height = retrieveHeight();
        }

        imageItselfScaledWidth = (float) width;
        imageItselfScaledHeight = (float) height;

        // See in adjustPositionAfterRotation why angle = 0 is necessary
        if (null == angle) {
            angle = 0f;
        }
        t.rotate((float) angle);
        initialOccupiedAreaBBox = getOccupiedAreaBBox().clone();
        float scaleCoef = adjustPositionAfterRotation((float) angle, layoutBox.getWidth(), layoutBox.getHeight());

        imageItselfScaledHeight *= scaleCoef;
        imageItselfScaledWidth *= scaleCoef;

        initialOccupiedAreaBBox.moveDown(imageItselfScaledHeight);
        initialOccupiedAreaBBox.setHeight(imageItselfScaledHeight);
        initialOccupiedAreaBBox.setWidth(imageItselfScaledWidth);
        if (xObject instanceof PdfFormXObject) {
            t.scale(scaleCoef, scaleCoef);
        }

        getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);

        // indicates whether the placement is forced
        boolean isPlacingForced = false;
        if (width > layoutBox.getWidth() || height > layoutBox.getHeight()) {
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                isPlacingForced = true;
            } else {
                return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this);
            }
        }

        occupiedArea.getBBox().moveDown((float) height);
        if (borders[3] != null) {
            height += (float) Math.sin((float) angle) * borders[3].getWidth();
        }
        occupiedArea.getBBox().setHeight((float) height);
        occupiedArea.getBBox().setWidth((float) width);

        float leftMargin = (float) this.getPropertyAsFloat(Property.MARGIN_LEFT);
        float topMargin = (float) this.getPropertyAsFloat(Property.MARGIN_TOP);
        if (leftMargin != 0 || topMargin != 0) {
            translateImage(leftMargin, topMargin, t);
            getMatrix(t, imageItselfScaledWidth, imageItselfScaledHeight);
        }

        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);

        if (angle != 0) {
            applyRotationLayout((float) angle);
        }

        float unscaledWidth = occupiedArea.getBBox().getWidth() / scaleCoef;
        MinMaxWidth minMaxWidth = new MinMaxWidth(0, area.getBBox().getWidth(), unscaledWidth, unscaledWidth);
        UnitValue rendererWidth = this.<UnitValue>getProperty(Property.WIDTH);
        UnitValue rendererMinWidth = this.<UnitValue>getProperty(Property.MIN_WIDTH);
        UnitValue rendererMaxWidth = this.<UnitValue>getProperty(Property.MAX_WIDTH);

        if (rendererWidth != null && rendererWidth.isPercentValue()) {
            minMaxWidth.setChildrenMinWidth(0);
            float coeff = imageWidth / (float) retrieveWidth(area.getBBox().getWidth());
            minMaxWidth.setChildrenMaxWidth(unscaledWidth * coeff);
        } else {
            boolean autoScale = hasProperty(Property.AUTO_SCALE) && (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE);
            boolean autoScaleWidth = hasProperty(Property.AUTO_SCALE_WIDTH) && (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE_WIDTH);
            boolean hasMinWidth = hasProperty(Property.MIN_WIDTH) && (boolean) this.<Boolean>getProperty(Property.MIN_WIDTH);
            if ((autoScale || autoScaleWidth) && !hasMinWidth) {
                minMaxWidth.setChildrenMinWidth(0);
            }
        }

        FloatingHelper.removeFloatsAboveRendererBottom(floatRendererAreas, this);
        LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, floatRendererAreas, layoutContext.getArea().getBBox(), clearHeightCorrection, false);

        return new MinMaxWidthLayoutResult(LayoutResult.FULL, editedArea, null, null, isPlacingForced ? this : null)
                .setMinMaxWidth(minMaxWidth);
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(ImageRenderer.class);
            logger.error(MessageFormat.format(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, "Drawing won't be performed."));
            return;
        }
        applyMargins(occupiedArea.getBBox(), false);
        applyBorderBox(occupiedArea.getBBox(), getBorders(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        if (fixedYPosition == null) {
            fixedYPosition = occupiedArea.getBBox().getY() + pivotY;
        }
        if (fixedXPosition == null) {
            fixedXPosition = occupiedArea.getBBox().getX();
        }

        PdfDocument document = drawContext.getDocument();
        boolean isTagged = drawContext.isTaggingEnabled();
        boolean modelElementIsAccessible = isTagged && getModelElement() instanceof IAccessibleElement;
        boolean isArtifact = isTagged && !modelElementIsAccessible;
        TagTreePointer tagPointer = null;
        if (isTagged) {
            tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
            if (modelElementIsAccessible) {
                IAccessibleElement accessibleElement = (IAccessibleElement) getModelElement();
                PdfName role = accessibleElement.getRole();
                if (role != null && !PdfName.Artifact.equals(role)) {
                    tagPointer.addTag(accessibleElement);
                    PdfDictionary layoutAttributes = AccessibleAttributesApplier.getLayoutAttributes(accessibleElement.getRole(), this, tagPointer);
                    applyGeneratedAccessibleAttributes(tagPointer, layoutAttributes);
                } else {
                    modelElementIsAccessible = false;
                    if (PdfName.Artifact.equals(role)) {
                        isArtifact = true;
                    }
                }
            }
        }

        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            fixedXPosition += rotatedDeltaX;
            fixedYPosition -= rotatedDeltaY;
            drawContext.getCanvas().saveState();
            applyConcatMatrix(drawContext, angle);
        }
        super.draw(drawContext);
        if (angle != null) {
            drawContext.getCanvas().restoreState();
        }

        PdfCanvas canvas = drawContext.getCanvas();
        if (isTagged) {
            if (isArtifact) {
                canvas.openTag(new CanvasArtifact());
            } else {
                canvas.openTag(tagPointer.getTagReference());

            }
        }

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        beginElementOpacityApplying(drawContext);
        canvas.addXObject(xObject, matrix[0], matrix[1], matrix[2], matrix[3], (float) fixedXPosition + deltaX, (float) fixedYPosition);
        endElementOpacityApplying(drawContext);
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FLUSH_ON_DRAW))) {
            xObject.flush();
        }

        if (isTagged) {
            canvas.closeTag();
        }

        if (isRelativePosition) {
            applyRelativePositioningTranslation(true);
        }
        applyBorderBox(occupiedArea.getBBox(), getBorders(), true);
        applyMargins(occupiedArea.getBBox(), true);

        if (modelElementIsAccessible) {
            tagPointer.moveToParent();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }

    @Override
    public Rectangle getBorderAreaBBox() {
        applyMargins(initialOccupiedAreaBBox, false);
        applyBorderBox(initialOccupiedAreaBBox, getBorders(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }
        applyMargins(initialOccupiedAreaBBox, true);
        applyBorderBox(initialOccupiedAreaBBox, true);
        return initialOccupiedAreaBBox;
    }

    @Override
    public void move(float dxRight, float dyUp) {
        super.move(dxRight, dyUp);
        if (initialOccupiedAreaBBox != null) {
            initialOccupiedAreaBBox.moveRight(dxRight);
            initialOccupiedAreaBBox.moveUp(dyUp);
        }
        if (fixedXPosition != null) {
            fixedXPosition += dxRight;
        }
        if (fixedYPosition != null) {
            fixedYPosition += dyUp;
        }
    }

    @Override
    protected MinMaxWidth getMinMaxWidth(float availableWidth) {
        return ((MinMaxWidthLayoutResult) layout(new LayoutContext(new LayoutArea(1, new Rectangle(availableWidth, AbstractRenderer.INF))))).getMinMaxWidth();
    }

    protected ImageRenderer autoScale(LayoutArea layoutArea) {
        Rectangle area = layoutArea.getBBox().clone();
        applyMargins(area, false);
        applyBorderBox(area, false);
        // if rotation was applied, width would be equal to the width of rectangle bounding the rotated image
        float angleScaleCoef = imageWidth / (float) width;
        if (width > angleScaleCoef * area.getWidth()) {
            setProperty(Property.HEIGHT, area.getWidth() / width * imageHeight);
            setProperty(Property.WIDTH, UnitValue.createPointValue(angleScaleCoef * area.getWidth()));
        }

        return this;
    }

    private void getMatrix(AffineTransform t, float imageItselfScaledWidth, float imageItselfScaledHeight) {
        t.getMatrix(matrix);
        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        if (xObject instanceof PdfImageXObject) {
            matrix[0] *= imageItselfScaledWidth;
            matrix[1] *= imageItselfScaledWidth;
            matrix[2] *= imageItselfScaledHeight;
            matrix[3] *= imageItselfScaledHeight;
        }
    }

    private float adjustPositionAfterRotation(float angle, float maxWidth, float maxHeight) {
        if (angle != 0) {
            AffineTransform t = AffineTransform.getRotateInstance(angle);
            Point p00 = t.transform(new Point(0, 0), new Point());
            Point p01 = t.transform(new Point(0, (float) height), new Point());
            Point p10 = t.transform(new Point((float) width, 0), new Point());
            Point p11 = t.transform(new Point((float) width, (float) height), new Point());

            double[] xValues = {p01.getX(), p10.getX(), p11.getX()};
            double[] yValues = {p01.getY(), p10.getY(), p11.getY()};

            double minX = p00.getX();
            double minY = p00.getY();
            double maxX = minX;
            double maxY = minY;

            for (double x : xValues) {
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
            for (double y : yValues) {
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            height = (float) (maxY - minY);
            width = (float) (maxX - minX);
            pivotY = (float) (p00.getY() - minY);

            deltaX = -(float) minX;
        }
        // Rotating image can cause fitting into area problems.
        // So let's find scaling coefficient
        float scaleCoeff = 1;
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.AUTO_SCALE))) {
            scaleCoeff = Math.min(maxWidth / (float) width, maxHeight / (float) height);
            height *= scaleCoeff;
            width *= scaleCoeff;
        } else if (null != getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH) && (boolean) getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH)) {
            scaleCoeff = maxWidth / (float) width;
            height *= scaleCoeff;
            width = maxWidth;
        } else if (null != getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT) && (boolean) getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT)) {
            scaleCoeff = maxHeight / (float) height;
            height = maxHeight;
            width *= scaleCoeff;
        }
        pivotY *= scaleCoeff;
        deltaX *= scaleCoeff;
        return scaleCoeff;
    }

    private void translateImage(float xDistance, float yDistance, AffineTransform t) {
        t.translate(xDistance, yDistance);
        t.getMatrix(matrix);
        if (fixedXPosition != null) {
            fixedXPosition += (float) t.getTranslateX();
        }
        if (fixedYPosition != null) {
            fixedYPosition += (float) t.getTranslateY();
        }
    }

    private void applyConcatMatrix(DrawContext drawContext, Float angle) {
        AffineTransform rotationTransform = AffineTransform.getRotateInstance((float) angle);
        Rectangle rect = getBorderAreaBBox();

        List<Point> rotatedPoints = transformPoints(rectangleToPointsList(rect), rotationTransform);

        float[] shift = calculateShiftToPositionBBoxOfPointsAt(rect.getX(), rect.getY() + rect.getHeight(), rotatedPoints);

        double[] matrix = new double[6];
        rotationTransform.getMatrix(matrix);

        drawContext.getCanvas().concatMatrix(matrix[0], matrix[1], matrix[2], matrix[3], shift[0], shift[1]);
    }

    private void applyRotationLayout(float angle) {
        Border[] borders = getBorders();
        Rectangle rect = getBorderAreaBBox();

        float leftBorderWidth = borders[3] == null ? 0 : borders[3].getWidth();
        float rightBorderWidth = borders[1] == null ? 0 : borders[1].getWidth();
        float topBorderWidth = borders[0] == null ? 0 : borders[0].getWidth();
        if (leftBorderWidth != 0) {
            float gip = (float) Math.sqrt(Math.pow(topBorderWidth, 2) + Math.pow(leftBorderWidth, 2));
            double atan = Math.atan(topBorderWidth / leftBorderWidth);
            if (angle < 0) {
                atan = -atan;
            }
            rotatedDeltaX = Math.abs((float) (gip * Math.cos(angle - atan) - leftBorderWidth));
        } else {
            rotatedDeltaX = 0;
        }

        rect.moveRight(rotatedDeltaX);
        occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() + rotatedDeltaX);

        if (rightBorderWidth != 0) {
            float gip = (float) Math.sqrt(Math.pow(topBorderWidth, 2) + Math.pow(leftBorderWidth, 2));
            double atan = Math.atan(rightBorderWidth / topBorderWidth);
            if (angle < 0) {
                atan = -atan;
            }
            rotatedDeltaY = Math.abs((float) (gip * Math.cos(angle - atan) - topBorderWidth));
        } else {
            rotatedDeltaY = 0;
        }

        rect.moveDown(rotatedDeltaY);
        if (angle < 0) {
            rotatedDeltaY += rightBorderWidth;
        }
        occupiedArea.getBBox().increaseHeight(rotatedDeltaY);
    }

    @Override
    public float getAscent() {
        return occupiedArea.getBBox().getHeight();
    }

    @Override
    public float getDescent() {
        return 0;
    }
}
