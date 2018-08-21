/* 
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.core;

import java.awt.Color;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * This class manages the overlay and all of it's components.
 * 
 * @author Wessel Jongkind
 */
class OverlayManager extends JFrame
{
    /**
     * The root component of the overlay to which all new components get
     * added.
     */
    private OverlayRoot content;
    
    /**
     * Is false when the manager has not fully been initialized yet (or something
     * went wrong in the meantime).
     */
    private boolean initialized;
    
    /**
     * Constructs a new <code>OverlayManager</code>. It automatically registers
     * all mouse, mousemotion and keylisteners, it automatically sets the size,
     * it makes sure all keyboard events anywhere on the computer will be registered
     * and it will  make sure the JFrame to which the overlay gets added is invisible.
     */
    public OverlayManager() throws Exception
    {
        content = new OverlayRoot(this);
        setFocusable(false);
        
        //Due to the getContentPane() method
        initialized = false;
        
        //Undecorated as it's an overlay and for pixel-tracking
        super.setUndecorated(true);
        
        //Is used to click through the overlay. Is also the contentPane. Manages MouseEvents.
        super.add(content);
        this.content.addMouseMotionListener(content);
        this.content.addMouseListener(content);
        this.content.addKeyListener(content);
        
        //100% pixel transparency is required to click through a JFrame
        super.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        this.content.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        
        //We initially don't want the overlay to be focusable
        super.setFocusable(false);
        super.setAlwaysOnTop(true);
        
        //Overlay can be the size of the monitor it's on. custom sizing is allowed.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        super.setSize(toolkit.getScreenSize());
        
        //Initialized
        initialized = true;
    }
    
    /**
     * Sets the overlay focussed so that it can be interacted with.
     * @param focusable True if the overlay should be interactable, otherwise false.
     */
    @Override
    public void setFocusable(boolean focusable)
    {
        super.setFocusable(focusable);
        if(focusable)
            this.content.requestFocus();
        this.content.setFocussed(focusable);
    }
    
    public void setContent(OverlayRoot content)
    {
        this.content = content;
    }
    
    public OverlayRoot getContent()
    {
        return  this.content;
    }
}
