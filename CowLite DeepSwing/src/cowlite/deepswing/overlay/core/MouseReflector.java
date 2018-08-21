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
package cowlite.deepswing.overlay.core;

import java.util.List;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

/**
 *
 * @author Wessel
 */
public class MouseReflector implements ActionListener
{
    private Timer timer;
    private Robot robot;
    private OverlayManager root;
    boolean supress = false;
    
    public MouseReflector(OverlayManager root) throws Exception
    {
        this.root = root;
        robot = new Robot();
        timer = new Timer(1, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //if(!supress)
           // System.out.println("dafuq");
    }
    
    public void process(List<MouseEvent> mouseclicks)
    {
        System.out.println("processing");
        supress = true;
        for(MouseEvent e : mouseclicks)
        {
            robot.mouseMove(e.getLocationOnScreen().x, e.getLocationOnScreen().y);
            
            if(e.getID() == MouseEvent.MOUSE_PRESSED)
                robot.mousePress(getButton(e.getButton()));
        }
        root.repaint();
        supress = false;
    }
    
    private int getButton(int button)
    {
        System.out.println(button);
        if(button == MouseEvent.BUTTON1)
            return InputEvent.BUTTON1_MASK;
        else
            return 1234;
    }
}
