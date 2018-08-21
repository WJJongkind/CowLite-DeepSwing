/*
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.core;

import cowlite.deepswing.overlay.components.OverlayComponent;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Wessel Jongkind
 */
public class COverlay
{
    private final OverlayManager manager;
    
    public COverlay() throws Exception {
        manager = new OverlayManager();
    }
    
    public void setFocusable(boolean focusable)
    {
        manager.setFocusable(focusable);
    }
    
    public void setVisible(boolean visible)
    {
        manager.setVisible(visible);
    }
    
    public void setDefaultCloseOperation(int operation)
    {
        manager.setDefaultCloseOperation(operation);
    }
    
    public void setBackground(Color c)
    {
        manager.getContent().setBackground(c);
    }
    
    public ArrayList<OverlayComponent> getContent()
    {
        return manager.getContent().getOverlayComponents();
    }
    
    public int getWidth()
    {
        return manager.getWidth();
    }
    
    public int getHeight()
    {
        return manager.getHeight();
    }
    
    public void add(OverlayComponent comp)
    {
        manager.getContent().add(comp);
    }
    
    public void add(ArrayList<OverlayComponent> components)
    {
        manager.getContent().setContent(components);
    }
    
    public void remove(OverlayComponent comp)
    {
        manager.getContent().remove(comp);
    }
    
    public void removeAll()
    {
        manager.getContent().removeAll();
    }
    
    public void repaint()
    {
        manager.repaint();
    }
    
}
