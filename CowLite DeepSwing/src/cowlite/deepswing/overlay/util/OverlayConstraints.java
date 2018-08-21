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

import java.awt.Insets;

/**
 *
 * @author Wessel
 */
public class OverlayConstraints
{
    private int gridx = 0, gridy = 0, gridwidth = 1, gridheight = 1;
    private double weightx = 1, weighty = 1;
    private Insets insets = new Insets(0, 0, 0, 0);
    private boolean resizing = false;
    
    
    public void gridx(int gridx)
    {
        this.gridx = gridx;
    }
    
    public void gridy(int gridy)
    {
        this.gridy = gridy;
    }
    
    public void weightx(double weightx)
    {
        this.weightx = Math.max(weightx, 0);
    }
    
    public void weighty(double weighty)
    {
        this.weighty = Math.max(weighty, 0);
    }
    
    public void gridwidth(int gridwidth)
    {
        this.gridwidth = Math.max(gridwidth, 1);
    }
    
    public void gridheight(int gridheight)
    {
        this.gridheight = Math.max(gridheight, 1);
    }
    
    public void insets(Insets insets)
    {
        this.insets = insets;
        if(this.insets == null)
            this.insets = new Insets(0, 0, 0, 0);
    }
    
    public void resizing(boolean resizing)
    {
        this.resizing = resizing;
    }
    
    public int gridx()
    {
        return gridx;
    }
    
    public int gridy()
    {
        return gridy;
    }
    
    public double weightx()
    {
        return weightx;
    }
    
    public double weighty()
    {
        return weighty;
    }
    
    public int gridwidth()
    {
        return gridwidth;
    }
    
    public int gridheight()
    {
        return gridheight;
    }
    
    public Insets insets()
    {
        return insets;
    }
    
    public boolean resizing()
    {
        return resizing;
    }
    
    public OverlayConstraints copy()
    {
        OverlayConstraints c = new OverlayConstraints();
        c.gridx(gridx);
        c.gridy(gridy);
        c.weightx(weightx);
        c.weighty(weighty);
        c.insets(insets);
        c.resizing(resizing);
        c.gridwidth(gridwidth);
        c.gridheight(gridheight);
        return c;
    }
    
    public static final String GRIDX = "GRIDX", GRIDY = "GRIDY", GRIDWIDTH = "GRIDWIDTH", GRIDHEIGHT = "GRIDHEIGHT", WEIGHTX = "WEIGHTX", WEIGHTY = "WEIGHTY",
            INSETS = "INSETS", RESIZING = "RESIZING";
    
    public Object getValue(String name) throws Exception
    {
        if(name.equals(GRIDX))
            return gridx();
        if(name.equals(GRIDY))
            return gridy();
        if(name.equals(GRIDWIDTH))
            return gridwidth();
        if(name.equals(GRIDHEIGHT))
            return gridheight();
        if(name.equals(WEIGHTX))
            return weightx();
        if(name.equals(WEIGHTY))
            return weighty();
        if(name.equals(INSETS))
            return insets();
        if(name.equals(RESIZING))
            return resizing();
        
        throw new Exception("Value " + name + " has not been found.");
    }
    
    public void setValue(String name, Object value) throws Exception
    {
        if(name.equals(GRIDX))
        {
            gridx((int) value);
            return;
        }
        if(name.equals(GRIDY))
        {
            gridy((int) value);
            return;
        }
        if(name.equals(GRIDWIDTH))
        {
            gridwidth((int) value);
            return;
        }
        if(name.equals(GRIDHEIGHT))
        {
            gridheight((int) value);
            return;
        }
        if(name.equals(WEIGHTX))
        {
            weightx((double) value);
            return;
        }
        if(name.equals(WEIGHTY))
        {
            weighty((double) value);
            return;
        }
        if(name.equals(INSETS))
        {
            insets((Insets) value);
            return;
        }
        if(name.equals(RESIZING))
        {
            resizing((Boolean) value);
            return;
        }
        
        throw new Exception("Value " + name + " has not been found.");
    }
}
