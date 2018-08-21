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
package cowlite.deepswing.overlay.components.layout;

import java.util.ArrayList;

/**
 *
 * @author Wessel
 */
public class Cell
{
    private final int x, y;
    private int width, height;
    private int cellWidth, cellHeight;
    private double weightx, weighty;
    private int minwidth = 0, minheight = 0;
    private int maxwidth = Integer.MAX_VALUE, maxheight = Integer.MAX_VALUE;
    private ArrayList<Cell> subcells = new ArrayList<>();
    
    public static final String WIDTH = "WIDTH", HEIGHT = "HEIGHT", MIN_WIDTH = "MINWIDTH", MIN_HEIGHT = "MINHEIGHT",
                               WEIGHTX = "WEIGHTX", WEIGHTY = "WEIGHTY";
    
    public Cell(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }
    
    public Cell(int x, int y, ArrayList<Cell> subcells)
    {
        this(x, y);
        this.subcells = subcells;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public void setWeightX(double weight)
    {
        this.weightx = weight;
    }
    
    public void setWeightY(double weight)
    {
        this.weighty = weight;
    }
    
    public void setMinimumSize(int width, int height)
    {
        this.minwidth = width;
        this.minheight = height;
    }
    
    public void setMinimumWidth(int width)
    {
        this.minwidth = width;
    }
    
    public void setMinimumHeight(int height)
    {
        this.minheight = height;
    }
    
    public void setMaximumSize(int width, int height)
    {
        this.maxwidth = width;
        this.maxheight = height;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public double getWeightX()
    {
        return weightx;
    }
    
    public double getWeightY()
    {
        return weighty;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public int getMinimumWidth()
    {
        return minwidth;
    }
    
    public int getMinimumHeight()
    {
        return minheight;
    }
    
    public int getMaximumWidth()
    {
        return  maxwidth;
    }
    
    public int getMaximumHeight()
    {
        return maxheight;
    }
    
    public Object getValue(String value) throws Exception
    {
        if(value.equals(HEIGHT))
            return getHeight();
        if(value.equals(WIDTH))
            return getWidth();
        if(value.equals(MIN_HEIGHT))
            return getMinimumHeight();
        if(value.equals(MIN_WIDTH))
            return getMinimumWidth();
        if(value.equals(WEIGHTX))
            return getWeightX();
        if(value.equals(WEIGHTY))
            return getWeightY();
        throw new Exception("The requested variable has not been  found.");
    }
    
    public void setValue(String target, Object value) throws Exception
    {
        if(target.equals(HEIGHT))
        {
            this.height = (int) value;
            return;
        }
        if(target.equals(WIDTH))
        {
            this.width = (int) value;
            return;
        }
        if(target.equals(MIN_HEIGHT))
        {
            this.minheight = (int) value;
            return;
        }
        if(target.equals(MIN_WIDTH))
        {
            this.minwidth = (int) value;
            return;
        }
        if(target.equals(WEIGHTX))
        {
            this.weightx = (double) value;
            return;
        }
        if(target.equals(WEIGHTY))
        {
            this.weighty = (double) value;
            return;
        }
        throw new Exception("The requested variable has not been  found.");
    }
}
