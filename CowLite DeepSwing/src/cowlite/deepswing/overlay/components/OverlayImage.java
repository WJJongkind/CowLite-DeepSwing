/* 
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.components;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * This class is used to display images on an overlay.
 * 
 * <h2>Painting</h2>
 * The image that should be painted by this component is stretched to the component's
 * borders. Currently there is no functionality yet to maintain aspect ratio of
 * the image. The component does not get sized automatically.
 * 
 * <h2>Transparency</h2>
 * When the alpa-value of an <code>OverlayImage</code> gets changed the image will
 * become transparent. If the background of the component or one of the ancestors
 * of the component has got a background color then this will also affect the
 * colors of the image. 
 * 
 * @author Wessel Jongkind
 */
public class OverlayImage extends OverlayComponent
{
    /**
     * The image to be displayed by the component. 
     */
    private BufferedImage image;
    
    /**
     * The transparency of the image. If one of the component's ancestors
     * is colored then it will show through the image. 
     */
    private float alpha = 1.0f;
    
    /**
     * The rotation of the image component (rotates clockwise).
     */
    private int rotation = 0;
    
    private boolean maintainRatio = false;
    
    /**
     * Constructs an image component using the image from the file where the
     * filepath is pointing. If the filepath points to a non-existent file, or the
     * file is not an image then it will throw an exception.
     * @param path The path pointing to the image-file that should be used.
     * @throws Exception Specified file does not exist or is not an image.
     */
    public OverlayImage(String path) throws Exception
    {
        image = ImageIO.read(new File(path));
    }
    
    /**
     * Constructs an image component using the image from the file where the
     * filepath is pointing. If the filepath points to a non-existent file, or the
     * file is not an image then it will throw an exception.
     * @param file The file that should be used.
     * @throws Exception Specified file does not exist or is not an image.
     */
    public OverlayImage(File file) throws Exception
    {
        image = ImageIO.read(file);
    }
    
    /**
     * Paints the image with the given rotation and transparency. 
     * @param g The <code>Graphics</code> object to be painted with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        
        Graphics2D g2 = (Graphics2D) g;
        
        AffineTransform originalTx = g2.getTransform();
        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(rotation), getWidth() / 2, getWidth() / 2);
        g2.setTransform(tx);
        
        super.paintComponent(g);
        
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        if(!maintainRatio)
            g2.drawImage(image, getCorrectedX(), getCorrectedY(), getCorrectedWidth(), getCorrectedHeight(), null);
        else
            drawMaintainAspect(g2);
        
        g2.setTransform(originalTx);
        g2.setComposite(composite);
    }
    
    private void drawMaintainAspect(Graphics2D g2)
    {
        double scaleWidth = (double) this.getCorrectedWidth() / image.getWidth();
        double scaleHeight = (double) this.getCorrectedHeight() / image.getHeight() ;
        
        double scale = Math.min(scaleWidth, scaleHeight);
        
        int x = (int) Math.round(this.getCorrectedWidth() / 2.0 - scale * image.getWidth() / 2.0);
        int y = (int) Math.round(this.getCorrectedHeight() / 2.0 - scale * image.getHeight() / 2.0);
        
        g2.drawImage(image, getCorrectedX() + x, getCorrectedY() + y, (int) Math.round(image.getWidth() * scale), (int) Math.round(image.getHeight() * scale), null);
    }
    
    /**
     * Sets the transparency of the image. Transparency should be between
     * 0.0f and 0.1f. 
     * @param alpha The transparency of the image (alpha-value).
     */
    public void setTransparency(float alpha)
    {
        this.alpha = alpha;
    }
    
    /**
     * Sets the rotation of the component. It rotates clockwise. Can be any value
     * but effectively it will be value % 360.
     * @param degrees The amount of degrees that the component should be rotated clockwise.
     */
    public void setRotation(int degrees)
    {
        this.rotation = degrees;
    }
    
    public void maintainAspectRatio(boolean maintain)
    {
        this.maintainRatio = maintain;
    }
    
    public BufferedImage getImage()
    {
        return image;
    }
    
    public float getTransparency()
    {
        return alpha;
    }
    
    public int getRotation()
    {
        return rotation;
    }
    
    public boolean isAspectRatioMaintained()
    {
        return this.maintainRatio;
    }
}
