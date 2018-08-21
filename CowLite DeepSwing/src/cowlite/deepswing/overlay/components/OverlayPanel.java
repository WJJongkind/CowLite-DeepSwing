/* 
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.components;

import cowlite.deepswing.overlay.components.layout.GridBagLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import cowlite.deepswing.overlay.util.OverlayConstraints;

/**
 * This is a container class to which other <code>OverlayComponent</code>s can be added.
 * 
 * <h2>Painting</h2>
 * Components that have been added first will be drawn below more recently added components.
 * Currently indexed placement of components is not yet supported. 
 * 
 * @author Wessel Jongkind
 */
public class OverlayPanel extends OverlayComponent
{
    /**
     * The children-components of the <code>OverlayComponent</code>.
     */
    private final ArrayList<OverlayComponent> components = new ArrayList<>();
    
    private final HashMap<OverlayComponent, Double> relativeWidths = new HashMap<>();
    
    private final HashMap<OverlayComponent, Double> relativeHeights = new HashMap<>();
    
    private final HashMap<OverlayComponent, Double> relativeX = new HashMap<>();
    
    private final HashMap<OverlayComponent, Double> relativeY = new HashMap<>();
    
    private final HashMap<OverlayComponent, OverlayConstraints> constraints = new HashMap<>();
    
    private boolean useConstraints = false;
    
    private boolean paintedOnce = false;
    
    private final GridBagLayout gbc = new GridBagLayout(components, constraints);

    /**
     * Paints all children added to the panel.
     * @param g The graphics-device to be painted with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        
        Composite composite = g2.getComposite();
        
        Shape clip = g2.getClip();
        for(OverlayComponent comp : components)
        {
            g2.setClip(clip);
            comp.setOffsetX(getX() + getOffsetX());
            comp.setOffsetY(getY() + getOffsetY());
            comp.paintComponent(g);
        }
        
        g2.setComposite(composite);
        
        if(!paintedOnce && useConstraints)
        {
            paintedOnce = true;
            gridComponents();
            paintComponent(g);
        }
    }
    
    /**
     * Adds a <code>OverlayComponent</code> as a child to the panel.
     * @param comp The component to be added.
     */
    public void add(OverlayComponent comp)
    {
        if(sizeSet())
            setPositioningData(comp);
        
        components.add(comp);
    }
    
    public void add(OverlayComponent comp, OverlayConstraints c)
    {
        components.add(comp);
        constraints.put(comp, c.copy());
        
        if(useConstraints)
            gridComponents();
        
        paintedOnce = false;
    }
    
    /**
     * Removes a <code>OverlayComponent</code> from the panel.
     * @param comp The component to be removed.
     */
    public void remove(OverlayComponent comp)
    {
        components.remove(comp);
        if(constraints.containsKey(comp))
            constraints.remove(comp);
        
        if(useConstraints)
            gridComponents();
    }
    
    @Override
    public void setSize(Dimension size)
    {
        this.setSize((int) size.getWidth(), (int) size.getHeight());
    }
    
    @Override
    public void setSize(int width, int height)
    {
        if(sizeSet() && !useConstraints)
        {
            sizeComponents();
            positionComponents();
        }
        else if(!useConstraints)
            setPositioningData();
        
        super.setSize(width, height);
        
        if(useConstraints)
            gridComponents();
    }
    
    private void gridComponents()
    {
        gbc.gridComponents(this.getCorrectedWidth(), this.getCorrectedHeight());
    } 
    
    private void sizeComponents()
    {
        for(OverlayComponent c : components)
            if(c.getRelativeSizing())
                c.setSize(
                        (int) Math.round(relativeWidths.get(c) * this.getCorrectedWidth()), 
                        (int) Math.round(relativeHeights.get(c) * this.getCorrectedHeight()));
    }
    
    private void positionComponents()
    {
        for(OverlayComponent c : components)
            if(c.getRelativePositioning())
                c.setLocation(
                        (int) Math.round(relativeX.get(c) * this.getCorrectedWidth()), 
                        (int) Math.round(relativeY.get(c) * this.getCorrectedHeight()));
    }
    
    private void setPositioningData()
    {
        for(OverlayComponent c : components)
            setPositioningData(c);
    }
    
    private void setPositioningData(OverlayComponent comp)
    {
        relativeWidths.put(comp, (double)comp.getWidth() / (double)getCorrectedWidth());
        relativeHeights.put(comp, (double)comp.getHeight() / (double)getCorrectedHeight());
        relativeX.put(comp, (double)comp.getX() / (double)getCorrectedWidth());
        relativeY.put(comp, (double)comp.getY() / (double)getCorrectedHeight());
    }
    
    public void useComponentConstraints(boolean use)
    {
        this.useConstraints = use;
        if(use)
            gridComponents();
        else
            setPositioningData();
    }
    
    public ArrayList<OverlayComponent> getComponents()
    {
        return components;
    }
    
    public HashMap<OverlayComponent, OverlayConstraints> getConstrainedComponents()
    {
        return constraints;
    }
    
    public boolean isConstraintsEnabled()
    {
        return useConstraints;
    }
    
    /**
     * Notifies all children that are an instance of MouseMotionListener that the
     * mouse has been dragged.
     * @param e The <code>MouseEvent</code> corresponding to the movement of the mouse.
     */
    @Override
    public void mouseDragged(MouseEvent e) 
    { 
        super.mouseDragged(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseMotionListener)
                ((MouseMotionListener)comp).mouseDragged(e);
    }

    /**
     * Notifies all children that are an instance of MouseMotionListener that the
     * mouse has been moved.
     * @param e The <code>MouseEvent</code> corresponding to the movement of the mouse.
     */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        super.mouseMoved(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseMotionListener)
                ((MouseMotionListener)comp).mouseMoved(e);
    }

    /**
     * Notifies all children that are an instance of MouseListener that the
     * mouse has been clicked.
     * @param e The <code>MouseEvent</code> corresponding to the action of the mouse.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        super.mouseClicked(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseClicked(e);
    }

    /**
     * Notifies all children that are an instance of MouseListener that the
     * mouse is pressed down.
     * @param e The <code>MouseEvent</code> corresponding to the action of the mouse.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        super.mousePressed(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mousePressed(e);
        
    }

    /**
     * Notifies all children that are an instance of MouseListener that the
     * mouse has been released.
     * @param e The <code>MouseEvent</code> corresponding to the action of the mouse.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseReleased(e);
    }

    /**
     * Notifies all children that are an instance of MouseListener that the
     * mouse has entered a visible area of the overlay.
     * @param e The <code>MouseEvent</code> corresponding to the action of the mouse.
     */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        super.mouseEntered(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseEntered(e);
    }

    /**
     * Notifies all children that are an instance of MouseListener that the
     * mouse has exited any visible area of the overlay.
     * @param e The <code>MouseEvent</code> corresponding to the action of the mouse.
     */
    @Override
    public void mouseExited(MouseEvent e)
    {
        super.mouseExited(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseExited(e);
    }

    /**
     * Notifies all children that are a key has been typed. This can be in any window
     * and it also gets triggered when the overlay is not focussed.
     * @param e The <code>KeyEvent</code> corresponding to the key being typed.
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
        super.keyTyped(e);
        for(OverlayComponent comp : components)
            if(comp instanceof KeyListener)
                ((KeyListener)comp).keyTyped(e);
    }

    /**
     * Notifies all children that are a key is being pressed. This can be in any window
     * and it also gets triggered when the overlay is not focussed.
     * @param e The <code>KeyEvent</code> corresponding to the key being pressed.
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        super.keyPressed(e);
        for(OverlayComponent comp : components)
            if(comp instanceof KeyListener)
                ((KeyListener)comp).keyPressed(e);
    }

    /**
     * Notifies all children that are a key has been released. This can be in any window
     * and it also gets triggered when the overlay is not focussed.
     * @param e The <code>KeyEvent</code> corresponding to the key being released.
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        super.keyReleased(e);
        for(OverlayComponent comp : components)
            if(comp instanceof KeyListener)
                ((KeyListener)comp).keyReleased(e);
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        super.mouseWheelMoved(e);
        for(OverlayComponent comp : components)
            if(comp instanceof MouseWheelListener)
                ((MouseWheelListener)comp).mouseWheelMoved(e);
    }
}
