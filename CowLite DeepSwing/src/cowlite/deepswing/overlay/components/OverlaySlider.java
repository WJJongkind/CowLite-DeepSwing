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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class resembled a slider that can be used for multiple purposes.
 * 
 * @author Wessel Jongkind
 */
public class OverlaySlider extends OverlayComponent implements MouseMotionListener, MouseListener
{
    /**
     * The percentage of the slider that should be filled.
     */
    private double percentage = 0;
    
    /**
     * The changelistener that should be triggered once the slider's value gets changed.
     */
    private final ArrayList<ChangeListener> listeners = new ArrayList<>();
    
    /**
     * Wether or not the slider has been hit by the last click registered on the overlay.
     * If so the slider will follow the mouse's movement and change the fill correspondingly.
     */
    private boolean hit = false;
    
    private boolean horizontal = true;
    
    /**
     * Paints the filling of the slider.
     * @param g The graphics object to be painting with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
 
        g2.setColor(getForeground());
        
        Composite composite = g2.getComposite();
        
        g2.setComposite(AlphaComposite.Src);
        
        if(horizontal)
        {
            int fill = (int)(percentage * getCorrectedWidth());
            g2.fillRect(getCorrectedX(), getCorrectedY(), fill, getCorrectedHeight());
        }
        else
        {
            int fill = (int)((1-percentage) * getCorrectedHeight());
            g2.fillRect(getCorrectedX(), getCorrectedY() + fill, getCorrectedWidth(), getCorrectedHeight() - fill);
        }
        
        g2.setComposite(composite);
    }
    
    /**
     * Sets the percentage of fill that the slider should have. The value should be
     * between 0 and 1. If it is higher or lower then it will be set to either
     * 0 or 1, depending on which number is closer.
     * @see #getPercentage() 
     * @param percentage The percentage of fill that the slider should have.
     */
    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
        if(percentage < 0)
            this.percentage = 0;
        if(percentage > 1)
            this.percentage = 1;
    }
    
    public void setOrientation(boolean horizontal)
    {
        this.horizontal = horizontal;
    }
    
    /**
     * Sets the percentage of the slider to the value corresponding with the
     * cursor of the mouse.
     * @param e The MouseEvent containing the latest position of the mouse.
     */
    private void setPercentage(MouseEvent e)
    {
        if(horizontal)
            setPercentage((double)(e.getX() - getCorrectedX()) / (double)getWidth());
        else
            setPercentage(1-((double)(e.getY() - getCorrectedY()) / (double)getHeight()));
    }
    
    /**
     * Adds a <code>ChangeListener</code> to the slider. Whenever the slider's value
     * changes the listener will be notified.
     * @param listener The listener to be added.
     */
    public void addChangeListener(ChangeListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Notifies all listeners that the slider's value has been changed.
     */
    private void fireStateChanged()
    {
        for(int i = 0; i < listeners.size(); i++)
            listeners.get(i).stateChanged(new ChangeEvent(this));
    }
    
    /**
     * Returns the percentage of fill that the slider has.
     * @see #setPercentage(double) 
     * @return The percentage of fill that the slider has.
     */
    public double getPercentage()
    {
        return percentage;
    }
    
    public boolean getOrientation()
    {
        return horizontal;
    }
    
    public ArrayList<ChangeListener> getChangeListeners()
    {
        return listeners;
    }
    
    /**
     * If the slider is focussed then it will track the mouse's movement and
     * change the fill and value of the slider corresponding to the mouse's movement.
     * @param e The MouseEvent with the lastest information about the mouse.
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        super.mouseDragged(e);
        if(!hit)
            return;
        setPercentage(e);
        fireStateChanged();
    }

    /**
     * If the slider is focussed then it will change the fill and value of the slider 
     * corresponding to the mouse's position.
     * @param e The MouseEvent with the lastest information about the mouse.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        super.mousePressed(e);
        hit = confirmHit(e);
        if(!hit)
            return;
        setPercentage(e);
        fireStateChanged();
    }

    /**
     * If the mouse is released then the slider loses focus.
     * @param e The MouseEvent with the lastest information about the mouse.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        hit = false;
    }
}
    

