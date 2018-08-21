/* 
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.core;

import cowlite.deepswing.overlay.components.OverlayComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * This class paints all components and makes sure the mouse can click through
 * the overlay. It serves as a root container.
 * 
 * <h2>Clicking through the overlay</h2>
 * This class will make sure that all components are painted on the overlay
 * and that the mouse is able to click through the overlay if it is not focussed.
 * It does this by painting a fully transparent pixel below the cursor. This will
 * allow the mouse to click behind the overlay without the user being able to
 * graphically see that the pixel is invisible. 
 * 
 * @author Wessel Jongkind
 */
class OverlayRoot extends JPanel implements MouseMotionListener, MouseListener, KeyListener
{
    /**
     * The overlay attached to this root container.
     */
    private final OverlayManager overlay;
    
    /**
     * All the components on the overlay.
     */
    private ArrayList<OverlayComponent> components = new ArrayList<>();
    
    /**
     * True if the overlay has got focus, false if it doesn't. 
     */
    private boolean focussed = false;
    
    /**
     * If the overlay should be repainted then it is true. Else it is false.
     */
    private boolean repaint = false;
    
    private ArrayList<MouseEvent> mouseclicks = new ArrayList<>();
    
    /**
     * If the overlay is still painting, or a paint-request has been done
     * but it hasn't painted since then it is true. Else it is false.
     */
    private boolean painting = false;
    
    private MouseReflector reflector;
    
    /**
     * Constructs a new root container for the overlay.
     * @param overlay The overlay that the object will be the root for.
     */
    public OverlayRoot(OverlayManager overlay) throws Exception
    {
        this.overlay = overlay;
        reflector = new MouseReflector(overlay);
    }
    
    /**
     * First it paints all components on the overlay. After this is finished it will
     * paint an invisible pixel at the mouse's last known location if the overlay
     * is not focussed. This will allow the user to click through the overlay.
     * @param g The graphics to be painting with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        //System.out.println(focussed);
        
        Graphics2D g2 = (Graphics2D) g;
        
        System.out.println(mouseclicks.isEmpty());
        if(!mouseclicks.isEmpty())
        {
            //get click events
            reflector.process(mouseclicks);
            mouseclicks.clear();
            return;
        }
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Shape parentClip = g2.getClip();
        
        for(OverlayComponent comp : components)
        {
            comp.paintComponent(g);
            g.setClip(parentClip);
        }
    }
    
    /**
     * Changes the focus-state of the overlay. If you set it to true then
     * the overlay will be focussed and all children will be notified of this.
     * @param focussed True if you want the overlay to be focussed, false if you don't want the overlay to be focussed.
     */
    public void setFocussed(boolean focussed)
    {
        this.focussed = focussed;
        
        if(!focussed)
            for(OverlayComponent comp : components)
                comp.setFocussed(false);
    }
    
    public void setContent(ArrayList<OverlayComponent> content)
    {
        this.removeAll();
        this.components.addAll(content);
    }
    
    public ArrayList<OverlayComponent> getOverlayComponents()
    {
        return components;
    }
    
    /**
     * With this method you can add <code>OverlayComponent</code>s to the overlay.
     * These components their offset will automatically be set to the location
     * of the overlay.
     * @param comp The component to be added.
     */
    public void add(OverlayComponent comp)
    {
        components.add(comp); 
        comp.setOffsetX(getX());
        comp.setOffsetY(getY());
    }
    
    /**
     * Removes an <code>OverlayComponent</code> from the overlay. All the component's
     * children will also be removed from the overlay.
     * @param comp The component to be removed. 
     */
    public void remove(OverlayComponent comp)
    {
        components.remove(comp);
    }
    
    @Override
    public void removeAll()
    {
        this.components.removeAll(this.getOverlayComponents());
    }

    /**
     * When the mouse is dragged on the overlay while the overlay is focussed
     * all overlay components that are an instance of <code>MouseMotionListener</code> 
     * will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseDragged(MouseEvent e) 
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseMotionListener)
                ((MouseMotionListener)comp).mouseDragged(e);
    }

    /**
     * When the mouse is moved above the overlay all overlay then a repaint will
     * be requested so that the pixel below the cursor remains invisible. If 
     * the overlay is focussed then all overlay components that are an instance 
     * of <code>MouseMotionListener</code> will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseMotionListener)
                ((MouseMotionListener)comp).mouseMoved(e);
    }
    
    /**
     * When the mouse is clicked on the overlay while the overlay is focussed
     * all overlay components that are an instance of <code>MouseListener</code> 
     * will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseClicked(e);
    }
    
    /**
     * When the mouse is pressed above the overlay while the overlay is focussed
     * all overlay components that are an instance of <code>MouseListener</code> 
     * will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        System.out.println("press");
        if(!focussed)
        {
            mouseclicks.add(e);
            overlay.repaint();
            //mouseclicks.
            return;
        }
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mousePressed(e);
        overlay.repaint();
        
    }
    
    /**
     * When the mouse is released above the overlay while the overlay is focussed
     * all overlay components that are an instance of <code>MouseListener</code> 
     * will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(!focussed)
        {
            mouseclicks.add(e);
            overlay.repaint();
            return;
        }
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseReleased(e);
    }
    
    /**
     * When the mouse enters the surface of visible parts of the overlay while
     * the overlay is focussed all overlay components that are an instance of
     * <code>MouseListener</code> will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseEntered(e);
        overlay.repaint();
    }
    
    /**
     * When the mouse exits the surface of visible parts of the overlay while
     * the overlay is focussed all overlay components that are an instance of 
     * <code>MouseListener</code> will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseExited(MouseEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof MouseListener)
                ((MouseListener)comp).mouseExited(e);
        overlay.repaint();
    }
    
    /**
     * When a key is typed while the overlay is focussed all overlay components 
     * that are an instance of <code>KeyListener</code> will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof KeyListener)
                ((KeyListener)comp).keyTyped(e);
        overlay.repaint();
    }
    
    /**
     * When a key is pressed while the overlay is focussed all overlay components 
     * that are an instance of <code>KeyListener</code> will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof KeyListener)
                ((KeyListener)comp).keyPressed(e);
        overlay.repaint();
    }

    /**
     * When a key is released while the overlay is focussed all overlay components 
     * that are an instance of <code>KeyListener</code> will be notified of this.
     * @see cowlite.deepswing.overlay.components.OverlayComponent
     * @param e The latest information about the mouse.
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if(!focussed)
            return;
        
        for(OverlayComponent comp : components)
            if(comp instanceof KeyListener)
                ((KeyListener)comp).keyReleased(e);
        overlay.repaint();
    }
}
