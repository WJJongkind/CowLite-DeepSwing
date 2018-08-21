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
public class Grid
{
    private ArrayList<Cell> hcells = new ArrayList<>();
    private ArrayList<Cell> vcells = new ArrayList<>();
    private int width, height;
    private double totalWeightX = 0;
    private double totalWeightY = 0;
    
    public Grid(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    public void calculateCells(ArrayList<ArrayList<Cell>> cells, int totalWidth, int totalHeight)
    {
        totalWeightX = 0;
        totalWeightY = 0;
        
        for(int i = 0; i < totalWidth; i++)
        {
            double maxWeight = 0;
            //int minSize = Integer.MIN_VALUE;
            //int maxSize = Integer.MIN_VALUE;
            for(int j = 0; j < totalHeight; j++)
            {
                Cell cell = cells.get(i).get(j);
                maxWeight = Math.max(maxWeight, cell.getWeightX());
                //minSize = Math.max(cell.getMinimumWidth(), minSize);
                //maxSize = Math.max(cell.getMaximumWidth(), maxSize);
            }
            Cell cell = new Cell(i, 0);
            cell.setWeightX(maxWeight);
            //cell.setMinimumSize(minSize, Integer.MIN_VALUE);
            //cell.setMaximumSize(maxSize, Integer.MIN_VALUE);
            hcells.add(cell);
            totalWeightX += maxWeight;
        }
        
        for(int i = 0; i < totalHeight; i++)
        {
            double maxWeight = 0;
            //int minSize = Integer.MIN_VALUE;
            //int maxSize = Integer.MIN_VALUE;
            for(int j = 0; j < totalWidth; j++)
            {
                Cell cell = cells.get(j).get(i);
                maxWeight = Math.max(maxWeight, cell.getWeightY());
                //minSize = Math.max(cell.getMinimumHeight(), minSize);
                //maxSize = Math.max(cell.getMaximumHeight(), maxSize);
            }
            Cell cell = new Cell(0, i);
            cell.setWeightY(maxWeight);
            //cell.setMinimumSize(Integer.MIN_VALUE, minSize);
            //cell.setMaximumSize(Integer.MIN_VALUE, maxSize);
            vcells.add(cell);
            totalWeightY += maxWeight;
        }
        
        calculateSizes();
    }
    
    private void calculateSizes()
    {
        for(int i = 0; i < hcells.size(); i++)
        {
            double correctedWeight = hcells.get(i).getWeightX() / totalWeightX;
            hcells.get(i).setWidth((int)Math.round(width * correctedWeight));
        }
        
        for(int i = 0; i < vcells.size(); i++)
        {
            double correctedWeight = vcells.get(i).getWeightY() / totalWeightY;
            vcells.get(i).setHeight((int)Math.round(height * correctedWeight));
        }
    }
    
    public void ensureMinimumWidth(int x1, int x2, int width)
    {
        try{
            ensureMinimumSize(x1, x2, width, hcells, Cell.WIDTH, Cell.MIN_WIDTH, Cell.WEIGHTX);
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void ensureMinimumHeight(int y1, int y2, int height)
    {
        try{
            ensureMinimumSize(y1, y2, height, vcells, Cell.HEIGHT, Cell.MIN_HEIGHT, Cell.WEIGHTY);
        }catch(Exception e){e.printStackTrace();}
    }
    
    private void ensureMinimumSize(int c1, int c2, int size, ArrayList<Cell> cells, String dimension, String minimumDimension, String dimensionWeight) throws Exception
    {
        int presentSize = 0;
        do{
            for(int i = c1; i < c2; i++)
                presentSize += (int) cells.get(i).getValue(dimension);
            
            if(size <= presentSize)
                return;

            int availableSize = 0;
            int additionalSize = size - presentSize;
            double availableWeight = 0;
            

            ArrayList<Cell> availableCells = new ArrayList<>();
            for(int i = 0; i < cells.size(); i++)
            {
                int cellFree = (int) cells.get(i).getValue(dimension) - (int) cells.get(i).getValue(minimumDimension);
                if(cellFree > 0 && (i < c1 || i >= c2))
                {
                    availableSize = availableSize + cellFree;
                    availableWeight = availableWeight + (double) cells.get(i).getValue(dimensionWeight);
                    availableCells.add(cells.get(i));
                }
            }

            if(availableSize <= 0)
                return;

            for(int i = 0; i < availableCells.size(); i++)
            {
                Cell c = availableCells.get(i);
                int freeSpace = (int) c.getValue(dimension) - (int) c.getValue(minimumDimension);
                int freeWeightedSpace = (int) Math.round(((double) c.getValue(dimensionWeight) / availableWeight) * additionalSize);
                int takeAway;
                if((double) c.getValue(dimensionWeight) <= 0)
                    takeAway = freeSpace;
                else
                    takeAway = Math.min(freeSpace, freeWeightedSpace);
                
                cells.get(i).setValue(dimension, (int) c.getValue(dimension) - takeAway);

                for(int j = c1; j < c2; j++)
                {
                    int add = (int) Math.round(takeAway / (double) (c2-j));
                    cells.get(j).setValue(dimension, (int) cells.get(j).getValue(dimension) + add);
                    cells.get(j).setValue(minimumDimension, (int) cells.get(j).getValue(dimension) + add);
                    takeAway -= add;
                }

            }
        }while(presentSize < size);
        
        //calculateWeights();
    }
    
    private void calculateWeights()
    {
        for(int i = 0; i < hcells.size(); i++)
        {
            double correctedWeight = hcells.get(i).getWidth() / (double) width;
            hcells.get(i).setWeightX(correctedWeight);
        }
        
        for(int i = 0; i < vcells.size(); i++)
        {
            double correctedWeight = vcells.get(i).getWidth() / (double) height;
            vcells.get(i).setWeightY(correctedWeight);
        }
    }
    
    public double getTotalWeightX()
    {
        return totalWeightX;
    }
    
    public double getTotalWeightY()
    {
        return totalWeightY;
    }
    
    public ArrayList<ArrayList<Cell>> getCells()
    {
        ArrayList<ArrayList<Cell>> cells = new ArrayList<>();
        for(int i = 0; i < hcells.size(); i++)
        {
            cells.add(new ArrayList<>());
            for(int j = 0; j < vcells.size(); j++)
            {
                Cell cell = new Cell(i, j);
                cell.setWeightX(hcells.get(i).getWeightX());
                cell.setWidth(hcells.get(i).getWidth());
                cell.setWeightY(vcells.get(j).getWeightY());
                cell.setHeight(vcells.get(j).getHeight());
                cells.get(i).add(cell);
            }
        }
        
        return cells;
    }
    
    /*public ArrayList<Double> getMaxWeightsX()
    {
        return weightsx;
    }
    
    public ArrayList<Double> getMaxWeightY()
    {
        return weightsy;
    }
    
    public ArrayList<Integer> getWidths()
    {
        return widths;
    }
    
    public ArrayList<Integer> getHeights()
    {
        return heights;
    }*/
}
