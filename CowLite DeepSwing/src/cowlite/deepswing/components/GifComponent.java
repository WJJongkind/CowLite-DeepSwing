/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cowlite.deepswing.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 *
 * @author Wessel
 */
public class GifComponent extends JComponent implements ActionListener
{
    private List<Image> imgArray;
    private AffineTransform tx;
    private int i = 0;
    private int framerate;
    private Timer timer;
    
    public GifComponent(Image[] images, int width, int height, int framerate) {
        imgArray = new ArrayList<>();
        Collections.addAll(imgArray, images);
        
        super.setSize(new Dimension(width, height));
        super.setPreferredSize(new Dimension(width, height));
        
        this.framerate = framerate;
        this.timer = new Timer((int)Math.round((double)1000 / framerate), this);
    }
    
    public GifComponent(String path, int framerate) {
        ImageReader ir = ImageIO.getImageReadersByFormatName("gif").next();
        
        try {
            super.setSize(new Dimension(ir.getWidth(0), ir.getHeight(0)));
            super.setPreferredSize(new Dimension(ir.getWidth(0), ir.getHeight(0)));
        }catch(Exception e) {
            super.setSize(new Dimension(0,0));
            super.setPreferredSize(new Dimension(0,0));
        }
        
        loadData(path, framerate);
    }
    
    public GifComponent(String path, int width, int height, int framerate) {
        super.setSize(new Dimension(width, height));
        super.setPreferredSize(new Dimension(width, height));
        
        loadData(path, framerate);
    }
    
    private void loadData(String path, int framerate) {
        imgArray = new ArrayList<>();
        
        ImageReader ir = ImageIO.getImageReadersByFormatName("gif").next();
        try {
            ir.setInput(ImageIO.createImageInputStream(new File(path)));
            for(int i = 0; i < ir.getNumImages(true); i++) {
                imgArray.add(ir.read(i).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH));
            }
            this.framerate = framerate;
        }catch(Exception e){System.out.println(e.getCause());}
        this.timer = new Timer((int)Math.round((double)1000 / framerate), this);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        try{
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(imgArray.get(i), 0, 0, getWidth(), getHeight(), null);
        }catch(Exception e){}
    }
    
    public void start() {
        if(!timer.isRunning()) {
            timer.start();
        }
    }
    
    public void pause() {
        if(timer.isRunning()) {
            timer.stop();
        }
    }
    
    public void stop() {
        pause();
        
        i = 0;
    }
    
    public void setFrameRate(int fps) {
        framerate = fps;
        timer.setDelay((int)Math.round((double) 1000 / fps));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        i++;
    }
}
