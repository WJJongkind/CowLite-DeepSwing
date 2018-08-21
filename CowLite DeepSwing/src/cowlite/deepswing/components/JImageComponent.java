package cowlite.deepswing.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wessel
 */
public class JImageComponent extends JComponent
{
    private BufferedImage image;
    private Image img;
    private int rotation = 0;
    private Dimension forcedSize = null;
    private Dimension d;
    private String path;
    private boolean painted = false;
    
    public JImageComponent(String path)
    {
        this.path = path;
        makeImage();
    }
    
    public JImageComponent(Image img, Dimension size)
    {
        painted = true;
        this.img = img;
        setSize(size);
        setPreferredSize(size);
    }
    
    public JImageComponent(String path, int theRotation)
    {
        this.path = path;
        rotation = theRotation;
        makeImage();
    }
    
    public JImageComponent(String path, Dimension forceSize)
    {
        this.path = path;
        forcedSize = forceSize;
        setPreferredSize(new Dimension(forcedSize));
        setSize(new Dimension(forcedSize));
        makeImageScaled();
    }
    
    public JImageComponent(String path, int theRotation, Dimension forceSize)
    {
        this.path = path;
        rotation = theRotation;
        forcedSize = forceSize;
        makeImageScaled();
    }
    
    private void makeImage()
    {
        try{
            image = ImageIO.read(new File(path));
        }catch(Exception e){ System.out.println(e + "=-==-==-==-=--makeImage JImageComponent");}
    }
    
    private void makeImageScaled()
    {
        try{
            image = ImageIO.read(new File(path));
            Dimension d = calcSize(getWidth(), getHeight());
            setSize(d);
            setPreferredSize(d);
            img = image.getScaledInstance(d.width,d.height,Image.SCALE_SMOOTH);
            painted = true;
        }catch(Exception e){ System.out.println(e + "=-==-==-==-=--makeImage JImageComponent");}
    }
            
    public void setSafeSize(Dimension d)
    {
        Dimension nd = calcSize(d.getWidth(), d.getHeight());
        this.setSize(nd);
        this.setPreferredSize(nd);
        img = image.getScaledInstance(nd.width, nd.height, Image.SCALE_SMOOTH);
        painted = true;
    }
    
    public void paintComponent(Graphics g)
    {
        try
        {
            Graphics2D g2 = (Graphics2D) g;
            if(!painted)
            {
                Dimension d2 = calcSize(getWidth(), getHeight());
                setSize(d2);
                setPreferredSize(d2);
                d = d2;
                img = image.getScaledInstance(d2.width,d2.height,Image.SCALE_SMOOTH);
                g2.drawImage(img,0, 0 , null);
                painted = true;
                return;
            }
            g2.drawImage(img, 0, 0, null);
        }catch(Exception e){System.out.println(e + "=-=-=-=-=-=-=-paintComponent JImageComponent" );}
    }
    
    private Dimension calcSize(double w, double h)
    {
        if(forcedSize == null)
        {
            double xRatio = w / image.getWidth();
            double yRatio = h / image.getHeight();
            if(xRatio < yRatio)
                return new Dimension((int)w, (int)(h*image.getHeight()));
            else
                return new Dimension((int)(image.getWidth()*yRatio),(int)h);
        }
        else
            return forcedSize;
    }
    
    public static Dimension getSafeSize(double w, double h, BufferedImage image)
    {
            double xRatio = w / image.getWidth();
            double yRatio = h / image.getHeight();
            if(xRatio < yRatio)
                return new Dimension((int)w, (int)(h*image.getHeight()));
            else
                return new Dimension((int)(image.getWidth()*yRatio),(int)h);
    }
}
