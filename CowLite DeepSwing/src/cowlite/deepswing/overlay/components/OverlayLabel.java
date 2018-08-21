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

/**
 * This class allows simple text to be added to the overlay. A <code>OverlayLabel</code>
 * can only have one foreground color (so multi-colored text is not supported by this class).
 * If you want to display a textfield or a larger chunk of text, then please use
 * coverlay.components.OverlayTextField.
 * 
 * <h2>Sizing</h2>
 * This type of component gets sized automatically. It will make sure that the text
 * is fully visible, but in such a way that there is no excessive area being used.
 * Sizing the component smaller than the space that the text requires will cause
 * the text to not be fully visible.
 * 
 * @author Wessel Jongkind
 */
public class OverlayLabel extends OverlayComponent
{
    /**
     * The font to be used for the text on the component.
     */
    private Font font;
    
    /**
     * The text to be displayed by the component.
     */
    private String text;
    
    /**
     * The default font for labels.
     */
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
    
    private int horizontalOffset = 0;
    
    private int verticalOffset = 0;
    
    private int lastKnownWidth = 0;
    
    /**
     * Constructs a new <code>OverlayLabel</code> with no text. It will be invisible untill
     * a new text has been set for the first time.
     */
    public OverlayLabel()
    {
        text = "";
        this.font = DEFAULT_FONT;
        super.setBorderWidth(0);
    }
    
    /**
     * Constructs a new <code>OverlayLabel</code> with a given text.
     * @see #setText(java.lang.String) 
     * @param text The text to be displayed.
     */
    public OverlayLabel(String text)
    {
        this.text = text;
        this.font = DEFAULT_FONT;
        super.setBorderWidth(0);
    }
    
    /**
     * Constructs a new <code>OverlayLabel</code> with a given text and font.
     * @see #setText(java.lang.String) 
     * @see #setFont(java.awt.Font) 
     * @param text The text to be displayed.
     * @param font The font to be used to display the text with.
     */
    public OverlayLabel(String text, Font font)
    {
        this.text = text;
        this.font = font;
        super.setBorderWidth(0);
    }
    
    /**
     * Paints the label. If no size has been set and a text has been set then it
     * will automatically calculate the required size. To disable automatic sizing
     * please use <code>coverlay.components.OverlayComponent#setAutomaticResize(boolean)</code>.
     * @see #notifyChange() 
     * @see #allowAutomaticResizing(boolean) 
     * @param g The graphics object to be painted with. 
     */
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(font);
        
        if(!sizeSet() && !getText().equals(""))
        {
            setSize(getStringWidth(g2) + getBorderWidth() * 2, getStringHeight(g2) + getBorderWidth() * 2);
        }
        
        super.paintComponent(g);
        
        Composite composite = g2.getComposite();
        
        g2.setComposite(AlphaComposite.Src);
        
        g2.setColor(getForeground());
        
        positionLabel(g2);
        g2.drawString(text, getCorrectedX() + horizontalOffset, getCorrectedY() + verticalOffset);
        
        lastKnownWidth = getStringWidth(g2);
        
        g2.setComposite(composite);
    }
    
    /**
     * Positions the label in the center of the button. This method should
     * only be called if the label is not  null.
     * 
     * @param g2 The graphics object with which should be painted. 
     */
    private void positionLabel(Graphics2D g2)
    {
        FontMetrics metrics = g2.getFontMetrics(font);
        int middle = getCorrectedWidth() / 2;
        horizontalOffset = middle - getStringWidth(g2) / 2;
        
        middle = getCorrectedHeight() / 2;
        verticalOffset = middle - getStringHeight(g2) / 2 + metrics.getAscent();
    }
    
    /**
     * Changes the text of the label. If automatic resizing is enabled then the
     * label will resized to fit the new text.
     * @param text The text to be displayed.
     */
    public void setText(String text)
    {
        this.text = text;
        super.notifyChange();
    }
    
    /**
     * Changes the font used to display the text.  If automatic resizing is enabled then the
     * label will resized to fit the new font.
     * @see #DEFAULT_FONT
     * @param font The font to be displaying the text with.
     */
    public void setFont(Font font)
    {
        this.font = font;
        super.notifyChange();
    }
    
    /**
     * Changes the font-size of the font used to display the text. If automatic resizing
     * is enabled then the label will be resized to fit the text with the new font-size.
     * @param size The new font-size that should be used.
     */
    public void setFontSize(int size)
    {
        this.font = new Font(this.font.getFontName(), this.font.getStyle(), size);
        super.notifyChange();
    }
    
    /**
     * Returns the text that is being displayed by this component.
     * @return The text that is being displayed by this component.
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Returns the font that is being used by this component.
     * @return The font that is being used by this component.
     */
    public Font getFont()
    {
        return font;
    }
    
    /**
     * Calculates the width of the string that should be painted by this component.
     * @see #getStringHeight(java.awt.Graphics2D) 
     * @param g2 The graphics to calculate the width with.
     * @return The width of the string that has to be painted.
     */
    public int getStringWidth(Graphics2D g2)
    {
        int size = g2.getFontMetrics(font).stringWidth(text);
        return size;
    }
    
    /**
     * Calculates the height of the string that should be painted by this component.
     * @see #getStringWidth(java.awt.Graphics2D) 
     * @param g2 The graphics to calculate the width with.
     * @return The width of the string that has to be painted.
     */
    public int getStringHeight(Graphics2D g2)
    {
        int size = g2.getFontMetrics(font).getHeight();
        return size;
    }
    
    public int getLastKnownWidth()
    {
        return lastKnownWidth;
    }
}
