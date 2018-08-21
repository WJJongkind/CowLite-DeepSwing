/* 
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.components;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 * This class handles the action of overlay buttons.
 * 
 * <h2>Text and imaging</h2>
 * The <code>OverlayButton</code> is automatically sized when it is constructed
 * with a <code>String</code> parameter. The size will be set to the width and
 * height of the label displayed on the button. If the button has an icon rather
 * than text then the size has to be set manually.
 * 
 * <h2>Event handling</h2>
 * When a click happens on the overlay objects of this class will be notified.
 * Objects of this class will first check if the click landed on their surface
 * area and they will then perform the action that they have been given. To give a
 * <code>OverlayButton</code> an action use the method <code>addActionListener(ActionListener)</code>.
 * 
 * @author Wessel Jongkind
 */
public class OverlayButton extends OverlayComponent implements MouseListener
{
    /**
     * The <code>ActionListener</code> containing the action to be performed
     * when the <code>OverlayButton</code> gets clicked.
     */
    private ActionListener listener;
    
    /**
     * The label that displays the text that has been set for the button. If no 
     * text has been set, or an icon has been set later, then the label will be null.
     */
    private OverlayLabel label;
    
    /**
     * The icon to be displayed on the button. If no icon has been  set or a
     * label has been set later, then the icon will be null.
     */
    private OverlayImage icon;
    
    /**
     * The default text to be displayed on the button.
     */
    private static final String DEFAULT_TEXT = "PRESS ME";
    
    /**
     * The EventID when the button gets pressed. It is used to create the
     * <code>ActionEvent</code> object that gets passed on to the <code>actionPerformed(ActionEvent)</code>
     * method of the <code>ActionListener</code>.
     */
    public static final String BUTTON_PRESS = "Button pressed";
    
    /**
     * Constructs a new <code>OverlayButton</code> with the label set to <code>DEFAULT_TEXT</code>
     * @see #DEFAULT_TEXT
     */
    public OverlayButton()
    {
        createLabel(DEFAULT_TEXT);
    }
    
    /**
     * Constructs a new <code>OverlayButton</code> with the label displaying the text
     * of the <code>String</code> parameter.
     * @param text The text to be displayed on the button.
     */
    public OverlayButton(String text)
    {
        createLabel(text);
    }
    
    /**
     * Constructs a new <code>OverlayButton</code> with the button displaying
     * an icon of the provided file.
     * 
     * @param file The file containing the image to be displayed on the button.
     * @throws java.lang.Exception when the image can not be found or it is not a image-file.
     */
    public OverlayButton(File file) throws Exception
    {
        createIcon(file);
        
    }
    
    /**
     * This method creates a <code>OverlayLabel</code> object that should be
     * displayed on the button. It disables the border of the label and it
     * sets the background to <code>INVISIBLE</code> in order to display it
     * correctly. The foreground (text color) of the label is equal to the foreground
     * of the button.
     * @see #INVISIBLE
     * @param text The text that the label should be displaying.
     */
    private void createLabel(String text)
    {
        this.label = new OverlayLabel(text);
        label.setBackground(INVISIBLE);
        label.setBorderWidth(0);
        label.setForeground(getForeground());
        label.allowAutomaticResizing(true);
    }
    
    /**
     * This method creates a <code>BufferedImage</code> which will serve as
     * an icon that should be displayed on the button. The image gets stretched to
     * the size of the JButon. 
     * @param file The file containing the image to be displayedon the button.
     * @throws Exception If the image file has not been  found.
     */
    private void createIcon(File file) throws Exception
    {
        this.icon = new OverlayImage(file);
        this.icon.setBorderWidth(0);
        this.icon.setBackground(INVISIBLE);
        super.allowAutomaticResizing(true);
    }

    /**
     * This method handles the painting of the button at the correct position.
     * First it checks if a size for the component has been set. If not it will
     * automatically set the size of the component unless the component should
     * display an icon. 
     * @param g The <code>Graphics</code> object to be painted with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        if(!sizeSet() && label != null)
        {
            this.setSize(label.getStringWidth(g2), label.getStringHeight(g2));
            this.setMinimumSize(this.getSize());
        }
        
        super.paintComponent(g);
        
        Composite composite = g2.getComposite();
        
        if(label != null)
            drawLabel(g);
        else
            drawIcon(g);
        
      
        g2.setComposite(composite);
    }
    
    /**
     * Draws the label to be displayed on this button with the given <code>Graphics</code> object.
     * It draws the label in the center of the button. This method should not be
     * called when the label is null.
     * @see #OverlayLabel
     * @param g The <code>Graphics</code> object to be painted with.
     */
    private void drawLabel(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        positionLabel(g2);
        
        label.paintComponent(g);
    }
    
    /**
     * Sets the correct size and location of the label so that the label
     * will draw the text in the center.
     * 
     * @param g2 The graphics object with which should be painted. 
     */
    private void positionLabel(Graphics2D g2)
    {
        label.setLocation(
                (int) Math.round(this.getCorrectedWidth() / 2.0 - label.getWidth() / 2.0 + getCorrectedX()), 
                (int) Math.round(this.getCorrectedHeight() / 2.0 - label.getHeight() / 2.0 + getCorrectedY()));
    }
    
    /**
     * Draws the icon to be displayed on this button with the given <code>Graphics</code> object.
     * It draws the label in the center of the button. This method should not be
     * called when the icon is null.
     * @see #OverlayImage
     * @param g The <code>Graphics</code> object to be painted with.
     */
    private void drawIcon(Graphics g)
    {
        this.icon.setLocation(this.getCorrectedLocation());
        this.icon.paintComponent(g);
    }
    
    /**
     * Sets the text to be displayed on the button. If previously a text was
     * already displayed then it will be replaced. If an icon was displayed on the
     * button then it will be removed and replaced with the text. If you want
     * to set an icon to be displayed then please use <code>setIcon(java.io.File)</code>
     * or <code>setIcon(java.lang.String)</code>.
     * @see #setIcon(java.io.File) 
     * @see #setIcon(java.lang.String) 
     * @see cowlite.deepswing.overlay.components.OverlayLabel
     * @param text The text to be displayed.
     */
    public void setText(String text)
    {
        this.icon = null;
        this.label = new OverlayLabel(text);
        this.label.setSize(getCorrectedWidth(), getCorrectedHeight());
    }
    
    /**
     * Sets the icon to be displayed on the button. If previously an icon was
     * already displayed then it will be replaced. If a <code>OverlayLabel</code> was displayed on the
     * button then it will be removed and replaced with the icon. If you want
     * to set a text to be displayed then please use <code>setLabel(java.lang.String)</code>.
     * @param path The filepath pointing to the image that should be displayed as an icon.
     * @throws java.lang.Exception When the image can not be found or the file is not an image.
     * @see #setIcon(java.io.File) 
     * @see #setText(java.lang.String)
     * @see cowlite.deepswing.overlay.components.OverlayImage
     */
    public void setIcon(String path) throws Exception
    {
        this.label = null;
        createIcon(new File(path));
        this.icon.setSize(getCorrectedSize());
    }
    
    /**
     * Sets the icon to be displayed on the button. If previously an icon was
     * already displayed then it will be replaced. If a <code>OverlayLabel</code> was displayed on the
     * button then it will be removed and replaced with the icon. If you want
     * to set a text to be displayed then please use <code>setLabel(java.lang.String)</code>.
     * @param file The file containing the image that should be displayed on the button as an icon.
     * @throws java.lang.Exception When the image can not be found or the file is not an image.
     * @see #setIcon(java.lang.String) 
     * @see #setText(java.lang.String)
     * @see cowlite.deepswing.overlay.components.OverlayImage
     */
    public void setIcon(File file) throws Exception
    {
        this.label = null;
        createIcon(file);
        this.icon.setSize(getCorrectedSize());
    }
    
    /**
     * Sets the foreground color of the button. If the button displays text
     * then the text will turn into the given color. If the button displays
     * an icon then this method will have no effect.
     * @see #setBackground(java.awt.Color) 
     * @see #setBorder(java.awt.Color) 
     * @param c The color that the text on the button should be.
     */
    @Override
    public void setForeground(Color c)
    {
        super.setForeground(c);
        if(label != null)
            label.setForeground(c);
    }
    
    /**
     * Adds an <code>ActionListener</code> to the button which contains the
     * action that should be performed once the button gets triggered. It is required
     * that this method gets used to set an <code>ActionListener</code> for the
     * button to have any functionality.
     * @param listener The listener containing the action that this button should perform.
     */
    public void addActionListener(ActionListener listener)
    {
        this.listener = listener;
    }
    
    public OverlayImage getImage()
    {
        return icon;
    }
    
    public OverlayLabel getText()
    {
        return label;
    }
    
    public ActionListener getActionListener()
    {
        return listener;
    }
    
    /**
     * Checks if the click is on the surface area of the button. If so then it will
     * trigger the <code>ActionListener</code>.
     * @param e The <code>MouseEvent</code> to be processed.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        super.mouseClicked(e);
        
        if(listener != null && confirmHit(e))
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, BUTTON_PRESS));
    }
}
