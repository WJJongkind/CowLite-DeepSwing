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

import cowlite.deepswing.overlay.components.OverlayButton;
import cowlite.deepswing.overlay.components.OverlayComponent;
import static java.awt.SystemColor.text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import cowlite.deepswing.overlay.util.OverlayConstraints;



/**
 *
 * @author Wessel
 */
public class GridBagLayout
{
    private final ArrayList<OverlayComponent> components;
    private final ArrayList<ArrayList<Cell>> cells = new ArrayList<>();
    private final ArrayList<OverlayComponent> requiredX = new ArrayList<>();
    private final ArrayList<OverlayComponent> requiredY = new ArrayList<>();
    private final HashMap<OverlayComponent, OverlayConstraints> constraints;
    private double totalWeightX, totalWeightY;
    
    
    public GridBagLayout(ArrayList<OverlayComponent> components, HashMap<OverlayComponent, OverlayConstraints> constraints)
    {
        this.components = components;
        this.constraints = constraints;
    }
    
    public void gridComponents(int areaWidth, int areaHeight)
    {
        if(areaWidth == 0 || areaHeight == 0)
            return;
        
        System.out.println("--------------------------------------");
        cells.clear();
        
        int totalWidth = 0;
        int totalHeight = 0;
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            
            int x = c.gridx();
            int y = c.gridy();
            int width = c.gridwidth();
            int height = c.gridheight();
            
            totalWidth = Math.max(totalWidth, x + width);
            totalHeight = Math.max(totalHeight, y + height);
        }
        
        if(totalWidth == 0 || totalHeight == 0)
            return;
        
        for(int i = 0; i < totalWidth; i++)
        {
            cells.add(new ArrayList<>());
            for(int j = 0; j < totalHeight; j++)
                cells.get(i).add(new Cell(i, j));
        }
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            int x = c.gridx();
            int y = c.gridy();
            int width = c.gridwidth();
            int height = c.gridheight();
            
            for(int i = x; i < x + width; i++)
            {
                for(int j = y; j < y + height; j++)
                {
                    Cell cell = cells.get(i).get(j);
                    cell.setWeightX(Math.max(c.weightx() / width, cell.getWeightX()));
                    cell.setWeightY(Math.max(c.weighty() / height, cell.getWeightY()));
                }
            }
        }
        
       // System.out.println(totalWidth + "    " + cells.size());
       // System.out.println(totalHeight + "    " + cells.get(0).size());
        Grid grid = new Grid(areaWidth, areaHeight);
        grid.calculateCells(cells, totalWidth, totalHeight);
        
        Collections.sort(components, new Comparator<OverlayComponent>() {
            @Override
            public int compare(OverlayComponent comp1, OverlayComponent comp2)
            {
                OverlayConstraints c1 = constraints.get(comp1);
                OverlayConstraints c2 = constraints.get(comp2);
                Integer width1 = c1.gridwidth();
                Integer width2 = c2.gridwidth();
                return  width1.compareTo(width2);
            }
        });
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            
            int x = c.gridx();
            int width = c.gridwidth();
            int minimumWidth = comp.getMinimumWidth();
            
            grid.ensureMinimumWidth(x, x + width, minimumWidth);
        }
        
        Collections.sort(components, new Comparator<OverlayComponent>() {
            @Override
            public int compare(OverlayComponent comp1, OverlayComponent comp2)
            {
                OverlayConstraints c1 = constraints.get(comp1);
                OverlayConstraints c2 = constraints.get(comp2);
                Integer height1 = c1.gridheight();
                Integer height2 = c2.gridheight();
                return  height1.compareTo(height2);
            }
        });
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            
            int y = c.gridy();
            int height = c.gridheight();
            int minimumHeight = comp.getMinimumHeight();
            
            grid.ensureMinimumHeight(y, y + height, minimumHeight);
        }
        
        ArrayList<ArrayList<Cell>> newCells = grid.getCells();
        
        for(int i = 0; i < newCells.size(); i++)
        {
            String line = i + ": ";
            for(int j = 0; j < newCells.get(i).size(); j++)
            {
                line += "\t" + newCells.get(i).get(j).getWidth() + "|" + newCells.get(i).get(j).getHeight();
            }
           // System.out.println(line);
        }
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            int x = c.gridx();
            int y = c.gridy();
            int width = c.gridwidth();
            int height = c.gridheight();
            int compWidth = 0;
            int compHeight = 0;
            int compX = 0;
            int compY = 0;
            
            for(int i = 0; i < newCells.get(x).size(); i++)
            {
                Cell cell = newCells.get(x).get(i);
                if(i >= y && i < y + height)
                {
                    compHeight += cell.getHeight();
                }
                if(i < y)
                {
                    compY += cell.getHeight();
                }
            }
            
            for(int i = 0; i < newCells.size(); i++)
            {
                Cell cell = newCells.get(i).get(y);
                if(i >= x && i < x + width)
                {
                    compWidth += cell.getWidth();
                }
                if(i < x)
                {
                    compX += cell.getWidth();
                }
            }
            
            
            comp.setSize(compWidth, compHeight);
            comp.setLocation(compX + compWidth / 2 - comp.getWidth() / 2, compY + compHeight / 2 - comp.getHeight() / 2);
            
        }
        
        /*int totalWidth = 0, totalHeight = 0;
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            
            int x = c.gridx();
            int y = c.gridy();
            int width = c.gridwidth();
            int height = c.gridheight();
            
            totalWidth = Math.max(totalWidth, x + width);
            totalHeight = Math.max(totalHeight, y + height);
            requiredX.add(comp);
            requiredY.add(comp);
        }
        
        totalWeightY = getWeight(totalHeight, OverlayConstraints.GRIDY, OverlayConstraints.GRIDHEIGHT, OverlayConstraints.WEIGHTY);
        
        totalWeightX = getWeight(totalWidth, OverlayConstraints.GRIDX, OverlayConstraints.GRIDWIDTH, OverlayConstraints.WEIGHTX);
        
        //System.out.println("Initial weights: " + totalWeightY + "    " + totalWeightX);
        
        int implWidth = correctWeights(totalWidth, areaWidth, OverlayConstraints.GRIDX, OverlayConstraints.GRIDWIDTH, OverlayConstraints.WEIGHTX, finishedX);
        int implHeight = correctWeights(totalHeight, areaHeight, OverlayConstraints.GRIDY, OverlayConstraints.GRIDHEIGHT, OverlayConstraints.WEIGHTY, finishedY);
        
        //System.out.println("1st corrected weights: " + totalWeightY + "    " + totalWeightX);
    
        //System.out.println("2nd corrected weights: " + totalWeightY + "    " + totalWeightX);
        
        this.calculateGridDimensions(OverlayConstraints.GRIDY, 
                                    totalHeight, 
                                    OverlayConstraints.WEIGHTY, 
                                    totalWeightY, 
                                    OverlayConstraints.GRIDHEIGHT,
                                    finishedY,
                                    implHeight,
                                    requiredY);
        
        this.calculateGridDimensions(OverlayConstraints.GRIDX, 
                                    totalWidth, 
                                    OverlayConstraints.WEIGHTX, 
                                    totalWeightX, 
                                    OverlayConstraints.GRIDWIDTH,
                                    finishedX,
                                    implWidth,
                                    requiredX);
        
        setMinimumSizes(implWidth, totalWidth, finishedX, OverlayConstraints.GRIDX, OverlayConstraints.GRIDWIDTH, OverlayConstraints.WEIGHTX);
        setMinimumSizes(implHeight, totalHeight, finishedY, OverlayConstraints.GRIDY, OverlayConstraints.GRIDHEIGHT, OverlayConstraints.WEIGHTY);
        
        for(OverlayComponent comp : components)
        {
            OverlayConstraints c = constraints.get(comp);
            
            int width = 0, height = 0, x = 0, y = 0;

            for(int i = 0; i < c.gridwidth(); i++)
                if(finishedX.get(i + c.gridx()) != null)
                    width += finishedX.get(i + c.gridx());

            for(int i = 0; i < c.gridx(); i++)
                if(finishedX.get(i) != null)
                    x += finishedX.get(i);

            for(int i = 0; i < c.gridheight(); i++)
                if(finishedY.get(i + c.gridy()) != null)
                    height += finishedY.get(i + c.gridy());

            for(int i = 0; i < c.gridy(); i++)
                if(finishedY.get(i) != null)
                    y += finishedY.get(i);
            
            if(comp instanceof OverlayButton)
            {
                System.out.println("@@@@@@");
                System.out.println(width + "         " + height);
            }

            width = width - c.insets().left - c.insets().right;
            height = height - c.insets().top - c.insets().bottom;
            
            if(c.resizing())
                comp.setSize(width, height);

                comp.setLocation((int) Math.round(x + c.insets().left + width / 2.0 - comp.getWidth() / 2.0), 
                                 (int) Math.round(y + c.insets().top + height / 2.0 - comp.getHeight() / 2.0));
            
                
        }*/
    } 
    
    private double getWeight(int length, String direction, String dimension, String weight)
    {    
        try{
            double totalWeight = 0;
            for(int i = 0; i < length; i++)
            {
                double cellWeight = 0;

                for(OverlayComponent comp : components)
                {
                    OverlayConstraints c = constraints.get(comp);

                    int index = (int) c.getValue(direction);
                    int size = (int) c.getValue(dimension);

                    if(i >= index && i < index + size)
                        cellWeight = Math.max(cellWeight, (double) c.getValue(weight) / size);
                }

                totalWeight += cellWeight;
            }
            return totalWeight;
        }catch(Exception e){e.printStackTrace();return 0;}
    }
    
    private int correctWeights(int length, int dimensionSize, String direction, String dimension, String weight, HashMap<Integer, Integer> target)
    {
        int implSize = dimensionSize;
        double implWeight;
        if(direction.equals(OverlayConstraints.GRIDX))
             implWeight = totalWeightX;
        else
            implWeight = totalWeightY;
        try{
            for(int i = 0; i < length; i++)
            {
                for(OverlayComponent comp : components)
                {
                    OverlayConstraints c = constraints.get(comp);

                    int index = (int) c.getValue(direction);
                    int size = (int) c.getValue(dimension);
                    
                    int compSize;
                    
                    if(direction.equals(OverlayConstraints.GRIDX))
                        compSize = comp.getMaximumWidth();
                    else
                        compSize = comp.getMaximumHeight();

                    if(i >= index && i < index + size)
                    {
                        double correctedWeight = (double) c.getValue(weight) / implWeight;
                        if(compSize < correctedWeight * implSize)
                        {
                            if(target.containsKey(i))
                                target.replace(i, Math.max(target.get(i), compSize));
                            else
                                target.put(i, compSize);
                            implWeight -= (double) c.getValue(weight);
                            implSize -= compSize;
                            
                            if(direction.equals(OverlayConstraints.GRIDX))
                                requiredX.remove(comp);
                            else 
                                requiredY.remove(comp);
                        }
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
        
        if(direction.equals(OverlayConstraints.GRIDX))
             totalWeightX = implWeight;
        else
            totalWeightY = implWeight;
        return implSize;
    }
    
    private void setMinimumSizes(int totalSize, int totalCells, HashMap<Integer, Integer> target,
                                String dimension, String dimensionSize, String dimensionWeight)
    {
        try{
            for(OverlayComponent comp : components)
            {
                OverlayConstraints c = constraints.get(comp);

                int size;
                if(dimension.equals(OverlayConstraints.GRIDX))
                    size = comp.getMinimumWidth();
                else
                    size = comp.getMinimumHeight();

                if(size > 0)
                {
                    int index = (int) c.getValue(dimension);
                    int length = (int) c.getValue(dimensionSize);
                    int combinedCellSize = 0;
                    
                    for(int i = 0; i < length; i++)
                        combinedCellSize += target.get(i + index);
                    
                    if(combinedCellSize < size)
                    {
                    }
                }
            }
        }catch(Exception e){}
    }

    /*private int setMinimumSizes(double totalSize, int totalCells, HashMap<Integer, Integer> target,
                                String dimension, String dimensionSize, String dimensionWeight)
    {
        double implSize = totalSize;
        double implWeight;
        
        if(dimension.equals(OverlayConstraints.GRIDX))
            implWeight = totalWeightX;
        else
            implWeight = totalWeightY;
        
        try{
            for(int i = 0; i < totalCells; i++)
            {
                double cellWeight = 0;
                double requiredWeight = 0;
                double absoluteWeight = 0;
                int requiredSize = 0;

                for(OverlayComponent comp : components)
                {
                    OverlayConstraints c = constraints.get(comp);

                    int primIndex = (int) c.getValue(dimension);
                    int primSize = (int) c.getValue(dimensionSize);

                    if(i >= primIndex && i < primIndex + primSize)
                    {
                        if(cellWeight < (double) c.getValue(dimensionWeight) / (int)c.getValue(dimensionSize) / implWeight)
                        {
                            cellWeight = (double) c.getValue(dimensionWeight) / (int)c.getValue(dimensionSize) / implWeight;
                            absoluteWeight = (double) c.getValue(dimensionWeight) / (int)c.getValue(dimensionSize);
                        }
                        
                        int size;
                        if(dimension.equals(OverlayConstraints.GRIDX))
                            size = comp.getMinimumWidth();
                        else
                            size = comp.getMinimumHeight();
                            
                        requiredWeight = Math.max(requiredWeight, size / implSize);
                        requiredSize = Math.max(requiredSize, (int)Math.round(size / (double)(int) c.getValue(dimensionSize)));
                    }
                }

                if(requiredWeight > cellWeight)
                {
                    if(target.get(i) != null)
                        target.replace(i, requiredSize);
                    else
                        target.put(i, requiredSize);
                    implSize -= requiredSize;
                    implWeight -= absoluteWeight;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        if(dimension.equals(OverlayConstraints.GRIDX))
            totalWeightX = implWeight;
        else
            totalWeightY = implWeight;
        
        return (int) Math.round(implSize);
    }*/
    
    private void calculateGridDimensions(String firstDirection, int firstDirSize,
                                       String weight, double totalWeight, 
                                       String firstDimension, HashMap<Integer, Integer> target,
                                       int totalDimensionSize, ArrayList<OverlayComponent> required)
    {
        try{
            for(int i = 0; i < firstDirSize; i++)
            {
                double cellWeight = 0;

                for(OverlayComponent comp : required)
                {
                    OverlayConstraints c = constraints.get(comp);

                    int primIndex = (int) c.getValue(firstDirection);
                    int primSize = (int) c.getValue(firstDimension);
                    
                    if(i >= primIndex && i < primIndex + primSize)
                    {
                        cellWeight = Math.max(cellWeight, (double) c.getValue(weight) / (int) c.getValue(firstDimension));                          
                    }
                }

                double correctedWeight = cellWeight / totalWeight;

                if(target.get(i) != null)
                    target.replace(i,
                            Math.max((int) Math.round(totalDimensionSize * correctedWeight), target.get(i)));
                else
                    target.put(i, (int) Math.round(totalDimensionSize * correctedWeight));
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
}
