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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class allows the user to type multi-line text towards the overlay.
 * 
 * @author Wessel Jongkind
 */
public class OverlayTextField extends OverlayComponent implements MouseListener, KeyListener
{
    /**
     * The line which should currently be focussed by the cursor.
     */
    private int cursorY = 0;
    
    /**
     * The character after which the cursor should be placed.
     */
    private int characterIndex = 0;
    
    /**
     * The amount of horizontal offset needed to display the area of text with
     * the cursor.
     */
    private int horizontalOffset = 0;
    
    /**
     * The amount of vertical offset needed to display the area of text with the
     * cursor.
     */
    private int verticalOffset = 0;
    
    /**
     * The font that should be used to paint the text.
     */
    private Font font;
    
    /**
     * The ArrayList that is used to contain all the lines of text.
     */
    private ArrayList<String> lines = new ArrayList<>();
    
    /**
     * If the cursor is moving to the right (either when new text is being typed
     * or the right arrow-key is being pressed) this is true. Otherwise false.
     * It is needed for the correct placement of the text.
     */
    private boolean goingRight = true;
    
    /**
     * This an object that is continuously updated whenever a repaint occurs. It
     * is needed to calculate where the cursor should be if a mouseclick occurs.
     */
    private FontMetrics metrics;
    
    /**
     * The margin that the cursor should keep to the characters around it.
     */
    public static final int CURSOR_MARGIN = 2;
    
    /**
     * The default font that is being used is the same as that is being used by
     * OverlayLabel.
     */
    public static final Font DEFAULT_FONT = OverlayLabel.DEFAULT_FONT;
    
    /**
     * Constructs a new textfield with the default font and an invisible background.
     */
    public OverlayTextField()
    {
        this.font = DEFAULT_FONT;
    }
    
   /**
     * Constructs a new textfield with the default font, an invisible background and
     * it will initially display the given text.
     * @see #DEFAULT_FONT
     * @see #INVISIBLE
     * @param text The text to initially be displayed by the textfield.
     */
    public OverlayTextField(String text)
    {
        this.font = DEFAULT_FONT;
        lines.add(text);
    }
    
    /**
     * Constructs a new textfield with a given text and font. The background will
     * automatically be set to invisible.
     * @see #INVISIBLE
     * @param text The text to initially be displayed by the textfield.
     * @param font The font to be used to display the text with.
     */
    public OverlayTextField(String text, Font font)
    {
        this.font = font;
        lines.add(text);
    }

    /**
     * Paints the textfield and all the text. It draws all the lines, but only
     * a part will be visible due to the clip that is being calculated for the
     * <code>Graphics</code> object. In combination with <code>horizontalOffset</code>,
     * <code>verticalOffset</code>, <code>getCorrectedWidth()</code> and <code>getCorrectedHeight()</code>
     * it will calculate what the clip for the textfield should be. Only the text within the bounds
     * of the clip is visible.
     * @param g The graphics to be painting with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        //Painting of the background & border
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        if(lines.size() < 1)
            return;
        
        Composite composite = g2.getComposite();
        
        g2.setComposite(AlphaComposite.Src);
        
        //Preparing to draw the text. The clip is the visible area of the text.
        g2.setColor(this.getForeground());
        g2.setFont(font);
        g2.setClip(getCorrectedX(), getCorrectedY(), getCorrectedWidth(), getCorrectedHeight());
        
        this.metrics = g2.getFontMetrics();
        
        positionText(g2);
        
        //The drawing of the text
        for(int i = 0; i < lines.size(); i++)
            g2.drawString(lines.get(i), getCorrectedX() + 1 - horizontalOffset, getCorrectedY() + verticalOffset + (g2.getFont().getSize() * (i + 1)));
        
        //The drawing of the cursor
        int width = g2.getFontMetrics().stringWidth(lines.get(cursorY).substring(0, characterIndex));
        
        if(isFocussed())
            g2.drawLine(getCorrectedX() - horizontalOffset + width, 
                        getCorrectedY() + verticalOffset + (g2.getFont().getSize() * cursorY), 
                        getCorrectedX() - horizontalOffset + width,
                        getCorrectedY() + verticalOffset + (g2.getFont().getSize() * (cursorY + 1)));
        
        g2.setComposite(composite);
    }
    
    /**
     * Positions the text in such a way that the cursor is always visible while
     * trying to move the text as little as possible. It does this by looking at
     * to which side the cursor is moving and the width of the text to the left and
     * to the right of the cursor. If the cursor is moving to the right then the cursor
     * is preferred to be placed at the left side of the screen. The offset of
     * the text is calculated by taking the width of the string from index 0 to 
     * the index of the cursor and subtracting the width of the textfield from 
     * that. If the cursor is moving to the left then the horizontal offset is
     * equal to the width of the string from index 0 to the index of the cursor.
     * <br>
     * The y-offset is not being calculated yet. This will be implemented later.
     * @see #horizontalOffset
     * @see #verticalOffset
     * @see #characterIndex
     * @see #cursorY
     * @param g2 The graphics object to calculate the placement with.
     */
    private void positionText(Graphics2D g2)
    {
        String line = lines.get(cursorY);
        String trimmed = line.substring(0, characterIndex);

        int width = g2.getFontMetrics().stringWidth(lines.get(cursorY).substring(0, characterIndex));

        double cursorPosition = getCorrectedX() + 1 - horizontalOffset + width;

        if(cursorPosition < getCorrectedX() + getCorrectedWidth() && cursorPosition > getCorrectedX() + 1)
            return;

        if(goingRight)
            horizontalOffset = g2.getFontMetrics().stringWidth(trimmed) - getCorrectedWidth() + 1;
        else
            horizontalOffset = g2.getFontMetrics().stringWidth(trimmed);
            
    }
    
    /**
     * the text to be displayed by the textfield. All currently present text
     * will be overwritten by this method.
     * @see #getText() 
     * @param text The text to be displayed.
     */
    public void setText(String text)
    {
        lines.clear();
        lines.add(text);
    }
    
    /**
     * Changes the font to be used to display the text. By default the font is
     * set to <code>DEFAULT_FONT</code>.
     * @see #DEFAULT_FONT
     * @param font The font to be used to display the text with.
     */
    public void setFont(Font font)
    {
        this.font = font;
    }
    
    /**
     * Returns all the lines of the textfield seperated in an ArrayList.
     * @return All the lines of the textfield.
     */
    public ArrayList<String> getText()
    {
        return lines;
    }
    
    /**
     * Returns the font used to draw the text of the textfield.
     * @return The font used to draw the text of the textfield.
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * If the mouse has been clicked then first this method will check if
     * the click was on the textfield. If so it will make sure that the cursor
     * gets set to the right position of the text.
     * @param e The latest information about the mouse.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point hit;
        if((hit = getHit(e)) != null)
        {
            setFocussed(true);
            recalculateCharacterIndex(hit);
        }
        else
            setFocussed(false);
    }
    
    /**
     * Recalculates <code>cursorY</code> and <code>characterIndex</code> by converting
     * the point's position to a position in the text. It does this by assigning a
     * rectangular area to each character. For each character is checked wether or not
     * the point is in that rectangle.
     * @param p The point to be converted.
     */
    private void recalculateCharacterIndex(Point p)
    {
        for(int i = 0; i < lines.size() && p != null; i++)
        {
            String line = lines.get(i);
            
            for(int j = 0; j < line.length(); j++)
            {
                int x = metrics.stringWidth(line.substring(0, j)) - horizontalOffset;
                int y = verticalOffset + (font.getSize() * (i));
                int height = font.getSize();
                int width = metrics.stringWidth(line.charAt(j) + "");
                
                Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, width, height);
                
                if(bounds.contains(p))
                {
                    characterIndex = j;
                    cursorY = i;
                    return;
                }
            }
        }
        
        cursorY = lines.size() - 1;
        
        if(cursorY == -1)
        {
            cursorY = 0;
            characterIndex = 0;
        }
    }

    /**
     * If a key has been pressed this method will make sure that it gets processed.
     * @param e Information about the key that was pressed.
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        super.keyPressed(e);
        this.processKeyEvent(e);
    }
    
    /**
     * If a key has been pressed this method will process it. It first determines
     * what kind of key has been pressed. The arrow-keys are used for navigation
     * through the text. Any symbol is added to the text. 
     * @param e Information about the key to be processed.
     */
    private void processKeyEvent(KeyEvent e)
    {
        if(!isFocussed())
            return;
        
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {   
            goingRight = false;
            if(characterIndex == 0 && cursorY == 0)
                return;
            if(characterIndex == 0 && cursorY > 0)
            {
                cursorY--;
                characterIndex = lines.get(cursorY).length();
            }
            lines.set(cursorY, lines.get(cursorY).substring(0, characterIndex - 1) + lines.get(cursorY).substring(characterIndex));
            characterIndex--;
        } 
        
        else if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            goingRight = false;
            cursorY++;
            characterIndex = 0;
            lines.add(cursorY, "");
        }
        
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            goingRight = false;
            characterIndex--;
        }
        
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            goingRight = true;
            characterIndex++;
        }
        
        else if(e.getKeyCode() == KeyEvent.VK_UP)
        {
            goingRight = false;
            cursorY--;
        }
        
        else if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            goingRight = false;
            cursorY++;
        }
        
        else if(isSymbol(e.getKeyChar()))
        {
            goingRight = true;
            if(lines.size() <= cursorY)
                lines.add(cursorY, "");
            lines.set(cursorY, lines.get(cursorY).substring(0, characterIndex) + e.getKeyChar() + lines.get(cursorY).substring(characterIndex));
            characterIndex++;
        }
        
        correctIndices();
    }
    
    /**
     * Checks wether <code>cursorY</code> and <code>characterIndex</code> are
     * within possible bounds.
     * @see #cursorY
     * @see #characterIndex
     */
    private void correctIndices()
    {
        if(cursorY >= lines.size())
            cursorY = lines.size() - 1;
        
        if(lines.size() > 0 && characterIndex > lines.get(cursorY).length())
            characterIndex = lines.get(cursorY).length();
        
        if(characterIndex < 0)
        {
            if(cursorY > 0)
                cursorY--;
            characterIndex = lines.get(cursorY).length();
        }
        
        if(cursorY < 0)
            cursorY = 0;
    }
    
    /**
     * Determines wether or not a character is a symbol. For example: the shift-key
     * can leave behind a character in KeyEvent which is not recognized by the system
     * as a character/symbol. In that case this method will return false.
     * @param character The character to be processed.
     * @return True if it is a symbol, false if it is not (or an unknown) symbol.
     */
    private boolean isSymbol(char character)
    {
        int type = Character.getType(character);
        return Character.UNASSIGNED != type;
    }
}
