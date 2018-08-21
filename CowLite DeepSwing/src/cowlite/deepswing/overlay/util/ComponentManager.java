/*
 * Copyright 2017 Wessel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cowlite.deepswing.overlay.util;

import cowlite.deepswing.overlay.components.OverlayComponent;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Wessel
 */
public class ComponentManager implements MouseMotionListener, MouseListener
{
    private final OverlayComponent component;
    
    private int change = 0;
    
    private int movementTreshold = DEFAULT_TRESHOLD;
    
    private int borderTreshold = DEFAULT_TRESHOLD;
    
    private static final int FIRST_BLOCK = 0, SECOND_BLOCK = 1, THIRD_BLOCK = 2;
    
    private boolean resizing = false, n = false, ne = false, east = false, se = false, s = false, sw = false, w = false, nw = false;
    
    private boolean resizable = false, movable = false;
    
    private Point previous;
    
    public static final int DEFAULT_TRESHOLD = 5;
    
    public ComponentManager(OverlayComponent component, boolean resizable, boolean movable)
    {
        this.component = component;
        this.resizable = resizable;
        this.movable = movable;
    }
    
    public void setMovable(boolean movable)
    {
        this.movable = movable;
    }
    
    public void setResizable(boolean resizable)
    {
        this.resizable = resizable;
    }
    
    public void setMovementTreshold(int treshold)
    {
        this.movementTreshold = treshold;
    }
    
    public void setResizeTreshold(int treshold)
    {
        this.borderTreshold = treshold;
    }
    
    public int getMovementTreshold()
    {
        return movementTreshold;
    }
    
    public int getResizeTreshold(int treshold)
    {
        return borderTreshold;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        decideResizeOrDrag(e);
        
        change++;
        if(change < movementTreshold)
            return;
        
        int dx = e.getPoint().x - previous.x;
        int dy = e.getPoint().y - previous.y;
        
        if(!resizing && movable)
            moveComponent(dx, dy);
        else if(resizable)
            resizeComponent(dx, dy);
        
        previous = e.getPoint();
    }
    
    private void decideResizeOrDrag(MouseEvent e)
    {
        if(change != 0)
            return;
        
        int x = component.getX() + component.getOffsetX();
        int y = component.getY() + component.getOffsetY();
        int width = component.getWidth();
        int height = component.getHeight();
        
        previous = e.getPoint();

        //checking with which row we are dealing
        int row = getRow(x, y, width, height);
        int column = getColumn(x, y, width, height);
        
        if(row == FIRST_BLOCK)
        {
            if(column == FIRST_BLOCK)
                nw = true;
            if(column == SECOND_BLOCK)
                n = true;
            if(column == THIRD_BLOCK)
                ne = true;
        }
        
        if(row == SECOND_BLOCK)
        {
            if(column == FIRST_BLOCK)
                w = true;
            if(column == THIRD_BLOCK)
                east = true;
        }
        
        if(row == THIRD_BLOCK)
        {
            if(column == FIRST_BLOCK)
                sw = true;
            if(column == SECOND_BLOCK)
                s = true;
            if(column == THIRD_BLOCK)
                se = true;
        }
        
        if(n || ne || east || se || s || sw || w || nw)
            resizing = true;
    }
    
    private int getRow(int x, int y, int width, int height)
    {
        if(previous.y >= y && previous.y <= y + borderTreshold)
            return FIRST_BLOCK;
        
        if(previous.y > y + borderTreshold && previous.y < y + height - borderTreshold)
            return SECOND_BLOCK;
        
        if(previous.y >= y + height - borderTreshold && previous.y < y + height)
            return THIRD_BLOCK;
        
        return -1;
    }
    
    private int getColumn(int x, int y, int width, int height)
    {
        if(previous.x > x && previous.x <= x + borderTreshold)
            return FIRST_BLOCK;
        
        if(previous.x > x + borderTreshold && previous.x < x + width - borderTreshold)
            return SECOND_BLOCK;
        
        if(previous.x >= x + width - borderTreshold && previous.x <= x + width)
            return THIRD_BLOCK;
        
        return -1;
    }
    
    private void moveComponent(int dx, int dy)
    {
        component.setLocation(component.getX() + dx, component.getY() + dy);
    }
    
    private void resizeComponent(int dx, int dy)
    {
        int x = component.getX() + component.getOffsetX();
        int y = component.getY() + component.getOffsetY();
        
        if(n)
        {
            component.setSize(component.getWidth(), component.getHeight() - dy);
            component.setLocation(x, y + dy);
        }
        if(ne)
        {
            component.setSize(component.getWidth() + dx, component.getHeight() - dy);
            component.setLocation(x, y + dy);
        }
        if(east)
            component.setSize(component.getWidth() + dx, component.getHeight());
        if(se)
            component.setSize(component.getWidth() + dx, component.getHeight() + dy);
        if(s)
            component.setSize(component.getWidth(), component.getHeight() + dy);
        if(sw)
        {
            component.setSize(component.getWidth() - dx, component.getHeight() + dy);
            component.setLocation(x + dx, y);
        }
        if(w)
        {
            component.setSize(component.getWidth() - dx, component.getHeight());
            component.setLocation(x + dx, y);
        }
        if(nw)
        {
            component.setSize(component.getWidth() - dx, component.getHeight() - dy);
            component.setLocation(x + dx, y + dy);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(component.isFocussed())
            change = 0;
        previous = null;
        resizing = false;
        n = ne = east = se = s = sw = w = nw = false;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
}
