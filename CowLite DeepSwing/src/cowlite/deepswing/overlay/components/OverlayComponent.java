/* 
 * Copyright 2017 CowLite (http://www.cowlite.nl).
 *
 * This is not free software and you may not make copies of this
 * software without explicit permission of the copyright holder.
 * All rights reserved.
 */
package cowlite.deepswing.overlay.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * This class is the default template for all OverlayComponents.It handles the
 * drawing of the background and the border on the correct position.
 * 
 * <h2>Positioning</h2>
 * Positioning of OverlayComponents happens through two variables; the offset variables
 * (offsetX and offsetY) and the location variables (x and y). A component has got
 * an offset larger than 0 when it's parent container has either a x or an offsetX larger than 0. The only
 * parent containers provided with COverlay are <code>InvisiblePixelPainter</code> and
 * <code>OverlayPanel</code>. The component's origin is positioned at [x + offsetX, y + offsetY].
 * offsetX and offsetY should be set and maintained by the parent component. 
 * 
 * <h2>Drawing</h2>
 * For correct drawing of objects <code>super.paintComponent(Graphics)</code> should always
 * be called when <code>paintComponent(Graphics)</code> is overridden. This will
 * make sure that the border and background get painted and that the correct
 * clip gets set for the <code>Graphics</code> object.
 * 
 * @author Wessel Jongkind
 */
public class OverlayComponent implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
    /**
     * The width of the component. By default the width is 0 so it will be
     * invisible if the width does not get changed.
     */
    private int width = 0; 
    
    /**
     * The height of the component. By default the height is 0 so it will be
     * invisible if the width does not get changed.
     */
    private int height = 0;
    
    private int minimumWidth = Integer.MIN_VALUE;
    
    private int minimumHeight = Integer.MIN_VALUE;
    
    private int maximumWidth = Integer.MAX_VALUE;
    
    private int maximumHeight = Integer.MAX_VALUE;
    
    /**
     * The x-coordinate of the component relative to it's parent.
     */
    private int x = 0;
    
    /**
     * The y-coordinate of the  component relative to it's parent.
     */
    private int y = 0;
    
    /**
     * The offset on the x-axis of the component relative to the overlay's origin.
     * This is generally used to position components onto secondary containers.
     */
    private int offsetX = 0;
    
    /**
     * The offset on the y-axis of the component relative to the overlay's origin.
     * This is generally used to position components onto secondary containers.
     */
    private int offsetY = 0;
    
    /**
     * The width of the border around this component. By default the border
     * is 1 pixel thick.
     */
    private int borderWidth = 1;
    
    /**
     * Initially no size has been  set for the component.
     */
    private boolean sizeSet = false;
    
    /**
     * Shows wether or not the component is allowed to be resized automatically.
     * It is set to true by default because components that don't support automatic
     * resizing are not influenced by this field.
     */
    private boolean automaticResizing = false;
    
    /**
     * Initially the component is not focussed.
     */
    private boolean focussed = false;
    
    /**
     * The color of the foreground color of the component. Usually the foreground
     * are things such as text or the filling of a slider.
     */
    private Color foreground = DEFAULT_FOREGROUND; 
    
    /**
     * The background color of the component.
     */
    private Color background = DEFAULT_BACKGROUND;
    
    /**
     * The color of the border of the component.
     */
    private Color border = DEFAULT_BORDER;
    
    /**
     * The default background color of all OverlayComponents is black with an opacity
     * of 0.3f.
     */
    public static final Color DEFAULT_BACKGROUND = new Color(0.0f, 0.0f, 0.0f, 0.3f);
    
    /**
     * The default foreground color of all OverlayComponents is white.
     */
    public static final Color DEFAULT_FOREGROUND = Color.white;
    
    /**
     * The default color of the border of all OverlayComponents is white with an opacity 
     * of 0.3f.
     */
    public static final Color DEFAULT_BORDER = new Color(1.0f, 1.0f, 1.0f, 0.3f);
    
    /**
     * If you want a border, background or foreground to not be seen then you can
     * use this. It is simply a color with an opacity of 0.03f. Do note that if
     * there is a component below an invisible area, then that area will have
     * the color of the component below.
     */
    public static final Color INVISIBLE = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    
    private final ArrayList<EventListener> listeners = new ArrayList<>();
    
    private boolean relativeSizing = false, relativePositioning = false;
    
    /**
     * Default painting of this overlay component. Call for this method (generally
     * super.paintComponent(g);) to draw a correct background and border.
     * @param g Graphics to be painting with.
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setClip(calculateClip(g2));
        
        drawBackground(g2);
        
        if(getBorderWidth() > 0)
            drawBorder(g2);
        
    }
    
    /**
     * Draws the background of the component. The background is the area between
     * the borders of the component.
     * @param g2 The graphics object to be painted with.
     */
    private void drawBackground(Graphics2D g2)
    {
        g2.setColor(getBackground());
        g2.fillRect(getCorrectedX(), getCorrectedY(), getCorrectedWidth(), getCorrectedHeight());
    }
    
    /**
     * Draws the border of the component. To set the color of the border use
     * setBorder(Color c) and to set the thickness of the border use setBorderWidth(int width).
     * @see #setBorder(java.awt.Color) 
     * @see #setBorderWidth(int) 
     * @param g2 The graphics object to be painted with.
     */
    private void drawBorder(Graphics2D g2)
    {
        Stroke defaultStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(getBorderWidth()));
        
        g2.setColor(getBorder());
        int correctionOffset = -1*(int)Math.round(getBorderWidth()/2.0);
        int correctionSizes = -1*(int)Math.round(getBorderWidth() * 1);
        
        g2.drawRect(getCorrectedX() + correctionOffset, getCorrectedY() + correctionOffset, getWidth() + correctionSizes, getHeight() + correctionSizes);
        
        g2.setStroke(defaultStroke);
    }
    
    /**
     * This method calculates the clip (visible area) of the component. In order
     * to do this we retreive the x and y coordinate of the already present clip
     * of the graphics object. In COverlay these are the coordinates of the origin of
     * the component's parent. Then we calculate what x-coordinate and y-coordinate
     * the clip of this component should be. If the component falls outside of the
     * parent's painting area then it will be invisible due to a non-dimensional clip.
     * @param g2
     * @return 
     */
    private Shape calculateClip(Graphics2D g2)
    {
        Rectangle clip = g2.getClip().getBounds();
        int minX = (int) clip.getX();
        int minY = (int) clip.getY();
        int maxX = (int) (clip.getX() + clip.getWidth());
        int maxY = (int) (clip.getY() + clip.getHeight());
        
        //Calculate the x-coordinate of the origin
        int x1 = Math.max(getCorrectedX() - getBorderWidth(), minX);
        
        /*
            Calculate the maximum possible x-coordinate of the component 
            taking the parent's drawing area into account.
        */
        int x2 = (int) Math.min(getCorrectedX() + getWidth() - getBorderWidth(), maxX);
        
        //Calculate the y-coordinate of the origin of the object
        int y1 = Math.max(getCorrectedY() - getBorderWidth(), minY);
        
        /*
            Calculate the maximum possible y-coordinate of the component 
            taking the parent's drawing area into account.
        */
        int y2 = (int) Math.min(getCorrectedY() + getHeight() - getBorderWidth(), maxY);
        
        if(x2 - x1 < 0 || y2 - y1 < 0)
            return new Rectangle2D.Double(0, 0, 0, 0);
        else
            return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
    }
    
    /**
     * Sets the size of the component, including border.
     * @param size The size for the overlay component.
     * @see #setSize(int, int) 
     */
    public void setSize(Dimension size)
    {
        setSize((int)size.getWidth(), (int)size.getHeight());
    }
    
    /**
     * Sets the size for the component, including border.
     * @param width The width of the component.
     * @param height The height of the component.
     * @see #setSize(java.awt.Dimension) 
     */
    public void setSize(int width, int height)
    {
        //if(this instanceof OverlayButton)
            //System.out.println("Requesting " + width + "   " + height);
        if(width > getMaximumWidth())
            width = getMaximumWidth();
        if(height > getMaximumHeight())
            height = getMaximumHeight();
        if(width < getMinimumWidth())
            width = getMinimumWidth();
        if(height < getMinimumHeight())
            height = getMinimumHeight();
        
        this.width = width;
        this.height = height;
        //if(this instanceof OverlayButton)
            //System.out.println("setting " + width + "   " + height);
        sizeSet = true;
    }
    
    public void setMinimumSize(Dimension size)
    {
        this.setMinimumSize((int)size.getWidth(), (int)size.getHeight());
    }
    
    public void setMinimumSize(int width, int height)
    {
        this.minimumWidth = width;
        this.minimumHeight = height;
        
        if(this.width < width)
            this.width = width;
        if(this.height < height)
            this.height = height;
    }
    
    public void setMaximumSize(Dimension size)
    {
        this.setMaximumSize((int) size.getWidth(), (int) size.getHeight());
    }
    
    public void setMaximumSize(int width, int height)
    {
        this.maximumWidth = width;
        this.maximumHeight = height;
        if(this.width > width)
            this.width = width;
        if(this.height > height)
            this.height = height;
    }
    
    /**
     * Sets the location of the  component, relative to it's parent. The parent
     * can be one of the following object types:
     * <ul>
     * <li><code>CowliteOverlay</code></li>
     * <li><code>OverlayPanel</code></li>
     * <li>custom overlay components</li>
     * </ul>
     * @param x The x-coordinate of the component.
     * @param y The y-coordinate of the component.
     */
    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Sets the location of the  component, relative to it's parent. The parent
     * can be one of the following object types:
     * <ul>
     * <li><code>CowliteOverlay</code></li>
     * <li><code>OverlayPanel</code></li>
     * <li>custom overlay components</li>
     * </ul>
     * @see #setLocation(int, int) 
     * @param p The location of the component.
     */
    public void setLocation(Point p)
    {
        setLocation(p.x, p.y);
    }
    
    /**
     * Sets the foreground color of the component. By default the foreground color is
     * <code>DEFAULT_FOREGROUND</code>. Some objects may not draw a foreground
     * and for those objects this method won't have any effect. Some objects
     * that are not influenced by this method are:
     * <ul>
     * <li><code>OverlayPanel</code></li>
     * <li><code>Overlayimage</code></li>
     * </ul>
     * @see #DEFAULT_FOREGROUND
     * @param c The color that the foreground of the component should be.
     */
    public void setForeground(Color c)
    {
        this.foreground = c;
    }
    
    /**
     * Sets the background color of the component. By default the background color is
     * <code>DEFAULT_BACKGROUND</code>. To maintain the overlay's purpose it is
     * recommended to set the background exclusively to transparent colours.
     * @param c The color that the background of the component should be.
     */
    public void setBackground(Color c)
    {
        this.background = c;
    }
    
    /**
     * Sets the color that the border of the  component should be. By default
     * the border is <code>DEFAULT_BORDER</code>. Setting the border to <code>INVISIBLE</code>
     * makes the border invisible. However, for rendering and placement reasons it
     * is strongly recommended that you set the size of the border to 0. 
     * @see #setBorderWidth(int)
     * @see #DEFAULT_BORDER
     * @see #INVISIBLE
     * @param c The color that the foreground of the component should be.
     */
    public void setBorder(Color c)
    {
        this.border = c;
    }
    
    /**
     * Sets the thickness of the border. This method can also be used to correctly
     * make the border invisible. To do this, set the border width to 0. To
     * @see #setBorder(java.awt.Color)
     * @param width The thickness of the border.
     */
    public void setBorderWidth(int width)
    {
        this.borderWidth = width;
    }
    
    /**
     * This method sets the offset on the x-axis for the component (so it moves the origin). This
     * method is mainly used by overlay-containers to correctly position an object. 
     * It is strongly recommended that you do not use this method for the placement
     * of a component on normal overlays, unless you are creating a new type of
     * container.
     * @see #setLocation(java.awt.Point) setLocation(Point location)
     * @see #setLocation(int, int) setLocation(int x, int y)
     * @param offset The amount of offset that this object should have on the x-axis. 
     *               Values lower than 0 will make it move to the left, values higher than 0 will make it move to the right.
     */
    public void setOffsetX(int offset)
    {
        this.offsetX = offset;
    }
    
    /**
     * This method sets the offset on the y-axis for the component (so it moves the origin). This
     * method is mainly used by overlay-containers to correctly position an object. 
     * It is strongly recommended that you do not use this method for the placement
     * of a component on normal overlays, unless you are creating a new type of
     * container.
     * @see #setLocation(java.awt.Point) setLocation(Point location)
     * @see #setLocation(int, int) setLocation(int x, int y)
     * @param offset The amount of offset that this object should have on the y-axis.
     *               Values lower than 0 will make it move up. Values higher than 0 will make it move down.
     */
    public void setOffsetY(int offset)
    {
        this.offsetY = offset;
    }
    
    public void setRelativeSizing(boolean relativeSizing)
    {
        this.relativeSizing = relativeSizing;
    }
    
    public void setRelativePositioning(boolean relativePositioning)
    {
        this.relativePositioning = relativePositioning;
    }
    
    /**
     * This method is used to notify a component that it is focussed. This will
     * allow the component to start listening for key and mouse events. This
     * method generally gets called by the components themselves once they
     * register that a click has landed on it's surface area. It is not recommended
     * that you call this method yourself if you only want components
     * to be focussed once they get clicked upon.
     * @see #confirmHit(java.awt.event.MouseEvent) 
     * @see #getHit(java.awt.event.MouseEvent) 
     * @param focussed True if the component has to be focussed, false if the component shouldn't be focussed.
     */
    public void setFocussed(boolean focussed)
    {
        this.focussed = focussed;
    }
    
    /**
     * Either allows or disallows the component to size itself automatically. This only
     * works for components that support automatic resizing.
     * @param isAllowed True if the component should be able to size itself, false if you want to exclusively use custom sizes.
     */
    public void allowAutomaticResizing(boolean isAllowed)
    {
        this.automaticResizing = isAllowed;
    }

    public void addMouseListener(MouseListener listener)
    {
        this.addListener(listener);
    }

    public void addMouseMotionListener(MouseMotionListener listener)
    {
        this.addListener(listener);
    }

    public void addMouseWheelListener(MouseWheelListener listener)
    {
        this.addListener(listener);
    }
    
    public void addKeyListener(KeyListener listener)
    {
        this.addListener(listener);
    }
    
    private void addListener(EventListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Returns the total size of the  component, including border. If you want
     * to get the size of the content surface area of the  component then use
     * getCorrectedSize().
     * @see #getCorrectedSize()
     * @return The size of the  component including border.
     */
    public Dimension getSize()
    {
        return new Dimension(getWidth(), getHeight());
    }
    
    /**
     * Returns the size of the content area of the  component. That is the total size of the component
     * minus the amount of space that the border takes up. If you want to get the total size
     * of the  component then use getSize().
     * @see #getSize() 
     * @return The size of the  component corrected for the border.
     */
    public Dimension getCorrectedSize()
    {
        return new Dimension(getCorrectedWidth(), getCorrectedHeight());
    }
    
    /**
     * Returns the width of the  component, including border. If you want to get
     * the width of the content area then use getCorrectedWidth().
     * @see #getCorrectedWidth()
     * @return The total width of the  component.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Returns the height of the  component, including border. If you want to get
     * the height of the content area then use getCorrectedHeight().
     * @see #getCorrectedHeight()
     * @return The total height of the  component.
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * Returns the width of the  component's content area. That is the total
     * width of the  component minus the area that the border takes up. If you
     * want to get the total width of the  component then use getWidth().
     * @see #getWidth()
     * @return The width of the content area of the  component.
     */
    public int getCorrectedWidth()
    {
        return getWidth() - getBorderWidth() * 2;
    }
    
    /**
     * Returns the height of the  component's content area. That is the total
     * height of the  component minus the area that the border takes up. If you
     * want to get the total height of the  component then use getHeight().
     * @see #getHeight()
     * @return The height of the content area of the  component.
     */
    public int getCorrectedHeight()
    {
        return getHeight() - getBorderWidth() * 2;
    }
    
    public Dimension getMinimumSize()
    {
        return new Dimension(this.getMinimumWidth(), this.getMinimumHeight());
    }
    
    public int getMinimumWidth()
    {
        return minimumWidth;
    }
    
    public int getMinimumHeight()
    {
        return minimumHeight;
    }
    
    public Dimension getMaximumSize()
    {
        return new Dimension(this.getMaximumWidth(), this.getMaximumHeight());
    }
    
    public int getMaximumWidth()
    {
        return maximumWidth;
    }
    
    public int getMaximumHeight()
    {
        return maximumHeight;
    }
    
    /**
     * Returns the location of the  component relative to it's parent.
     * If you want to get the location of the content area of the  component relative
     * to the overlay's location then use getCorrectedLocation(). 
     * @see #getCorrectedLocation()
     * @return The location of the  component.
     */
    public Point getLocation()
    {
        return new Point(x,y);
    }
    
    /**
     * Returns the x-coordinate of the  component.
     * If you want to get the y-coordinate of the content area relative to the
     * overlay's location then use getCorrectedX().
     * @see #getCorrectedX()
     * @return The x-coordinate of the  component.
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * Returns the y-coordinate of the  component relative to it's parent. 
     * If you want to get the y-coordinate of the content area relative to
     * the overlay's location then use getCorrectedX().
     * @see #getCorrectedY()
     * @return The y-coordinate of the  component.
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * Returns the location of the content area of the  component relative to
     * the overlay's location. If you want to get the location of the  component
     * relative to it's parent then use getLocation().
     * @see #getLocation()
     * @return The location of the content area of the  component.
     */
    public Point getCorrectedLocation()
    {
        return new Point(getCorrectedX(), getCorrectedY());
    }
    
    /**
     * Returns the x-coordinate of the content area of the  component relative to
     * the overlay's location. If you want to get the location of the  component
     * relative to it's parent then use getX().
     * @see #getX() 
     * @return The x-coordinate of the content area of the  component.
     */
    public int getCorrectedX()
    {
        return getOffsetX() + getX() + getBorderWidth();
    }
    
    /**
     * Returns the y-coordinate of the content area of the  component relative to
     * the overlay's location. If you want to get the location of the  component
     * relative to it's parent then use getY().
     * @see #getY()
     * @return The y-coordinate of the content area of the  component.
     */
    public int getCorrectedY()
    {
        return getOffsetY() + getY() + getBorderWidth();
    }
    
    /**
     * Returns the offset on the x-axis of the  component which is usually equal to it's parent's
     * x-coordinate. This method is mainly used to correctly place components onto a container
     * and it's purpose is solely ment for that. If you want to get the location of the component
     * then please consider using:
     * <ul>
     * <li>getLocation()</li>
     * <li>getCorrectedLocation()</li>
     * <li>getX()</li>
     * <li>getY()</li>
     * <li>getCorrectedX()</li>
     * <li>getCorrectedY()</li>
     * </ul>
     * @see #getLocation()
     * @see #getCorrectedLocation
     * @see #getX()
     * @see #getY()
     * @see #getCorrectedX()
     * @see #getCorrectedY()
     * @return The amount of offset on the x-axis of the  component
     */
    public int getOffsetX()
    {
        return offsetX;
    }
    
    /**
     * Returns the offset on the y-axis of the  component which is usually equal to it's parent's
     * y-coordinate. This method is mainly used to correctly place components onto a container
     * and it's purpose is solely ment for that. If you want to get the location of the component
     * then please consider using getLocation() and getCorrectedLocation().
     * <ul>
     * <li>getLocation()</li>
     * <li>getCorrectedLocation()</li>
     * <li>getX()</li>
     * <li>getY()</li>
     * <li>getCorrectedX()</li>
     * <li>getCorrectedY()</li>
     * </ul>
     * @see #getLocation()
     * @see #getCorrectedLocation
     * @see #getX()
     * @see #getY()
     * @see #getCorrectedX()
     * @see #getCorrectedY()
     * @return The amount of offset on the y-axis of the  component
     */
    public int getOffsetY()
    {
        return offsetY;
    }
    
    /**
     * This method returns the foreground color of the component. By default the foreground color is
     * <code>DEFAULT_FOREGROUND</code>. Some objects may not draw a foreground
     * and for those objects this method won't have any effect. Some objects
     * that are not influenced by this method are:
     * <ul>
     * <li><code>OverlayPanel</code></li>
     * <li><code>Overlayimage</code></li>
     * </ul>
     * @see #DEFAULT_FOREGROUND
     * @return The foreground color of the component.
     */
    public Color getForeground()
    {
        return foreground;
    }
    
    /**
     * This method returns the background color of the component. By default the
     * background color is <code>DEFAULT_BACKGROUND</code>.
     * @see #DEFAULT_BACKGROUND
     * @return The background color of the  component.
     */
    public Color getBackground()
    {
        return background;
    }
    
    /**
     * This method returns the border color of the component. By default the
     * border color is <code>DEFAULT_BORDER</code>. If you want to get the thickness
     * of the border then use getBorderWidth().
     * @see #DEFAULT_BORDER
     * @see #getBorderWidth()
     * @return The border color of this component.
     */
    public Color getBorder()
    {
        return border;
    }
    
    /**
     * This method returns the thickness of the border of the component. If you want
     * to get the color of the border then use getBorder().
     * @see #getBorder()
     * @return The thickness of the border of the component.
     */
    public int getBorderWidth()
    {
        return borderWidth;
    }
    
    public boolean getRelativeSizing()
    {
        return relativeSizing;
    }
    
    public boolean getRelativePositioning()
    {
        return relativePositioning;
    }
    
    /**
     * This method tells wether or not the component is focussed. When the component
     * is focussed it will respond to key and mouse events. You can  set this
     * component to focussed by using setFocussed(boolean focussed).
     * @see #setFocussed(boolean) 
     * @return True of the component is focussed, false if the component is not focussed.
     */
    public boolean isFocussed()
    {
        return this.focussed;
    }
    
    /**
     * This method returns the point of the mouse-cursor relative to it's
     * corrected location. If the mouse-cursor is not over the component
     * then it will return null. If you only want to know if the mouse
     * is above the component then use confirmHit(MouseEvent e).
     * @see #confirmHit(java.awt.event.MouseEvent) 
     * @param e The MouseEvent to be checked.
     * @return The coordinate of the mouse-cursor on the component.
     */
    public Point getHit(MouseEvent e)
    {
        Point p = e.getPoint();
        
        Rectangle2D.Double area = new Rectangle2D.Double(getCorrectedX(), getCorrectedY(), getCorrectedWidth(), getCorrectedHeight());
        
        if(area.contains(p))
            return new Point(p.x - getCorrectedX(), p.y - getCorrectedY());
        else
            return null;
    }
    
    /**
     * This method calculates if the mouse-cursor is above the component. 
     * If you want to know the relative location of the cursor on the component
     * then use getHit(MouseEvent e).
     * @see #getHit(java.awt.event.MouseEvent) 
     * @param e The MouseEvent to be checked.
     * @return Returns false if the mouse-cursor is not over the component, returns
     * true if the mouse-cursor is over the component.
     */
    public boolean confirmHit(MouseEvent e)
    {
        Point p = e.getPoint();
        
        Rectangle2D.Double area = new Rectangle2D.Double(getCorrectedX(), getCorrectedY(), getCorrectedWidth(), getCorrectedHeight());
        
        return area.contains(p);
    }
    
    /**
     * This method tells you wether or not a size has been set for the  component.
     * @return True of a size has been set, false if a size hasn't been set.
     */
    public boolean sizeSet()
    {
        if(automaticResizing)
            return sizeSet;
        else
            return true;
    }
    
    /**
     * Notifies the component that it's content has been changed. Components that
     * automatically size themselves require this method if their content has been changed
     * in order to be resized properly.
     */
    public void notifyChange()
    {
        if(automaticResizing)
            this.sizeSet = false;
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseListener)
                ((MouseListener)l).mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        this.setFocussed(confirmHit(e));
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseListener)
                ((MouseListener)l).mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseListener)
                ((MouseListener)l).mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseListener)
                ((MouseListener)l).mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseListener)
                ((MouseListener)l).mouseExited(e);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseMotionListener)
                ((MouseMotionListener)l).mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseMotionListener)
                ((MouseMotionListener)l).mouseMoved(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof MouseWheelListener)
                ((MouseWheelListener)l).mouseWheelMoved(e);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof KeyListener)
                ((KeyListener)l).keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof KeyListener)
                ((KeyListener)l).keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if(!isFocussed())
            return;
        for(EventListener l : listeners)
            if(l instanceof KeyListener)
                ((KeyListener)l).keyReleased(e);
    }
}
