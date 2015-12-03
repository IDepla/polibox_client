/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Igor Deplano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package it.polito.ai.polibox.client.swing.frames;

import it.polito.ai.polibox.client.notification.NotificationsInterface;
import it.polito.ai.polibox.client.persistency.Notification;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslucentNotificationFrame extends JFrame implements ActionListener,InitializingBean,Observer {
 
	private static final long serialVersionUID = 7896341672746821233L;

	private Timer timer;
    
    private Point location;
    
    @Autowired
	private ResourceBundle resourceBundle;
    
	@Autowired
	private XmlLoaderInterface<NotificationsInterface> notificationManager;
	
    public XmlLoaderInterface<NotificationsInterface> getNotificationManager() {
		return notificationManager;
	}


	public void setNotificationManager(
			XmlLoaderInterface<NotificationsInterface> notificationManager) {
		this.notificationManager = notificationManager;
	}


	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}


	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}


	public Point getLocation() {
		return location;
	}


	public void setLocation(Point location) {
		super.setLocation(location);
		this.location = location;
	}
	
	public void afterPropertiesSet() throws Exception {
		setTitle(resourceBundle.getString("TranslucentNotificationFrame.afterPropertiesSet.title"));
		((Observable)notificationManager.load()).addObserver(this);
	}

	public TranslucentNotificationFrame() {
    	  super();
          setLayout(new FlowLayout());

          setSize(300,200);
          
          
          setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

       // Determine if the GraphicsDevice supports translucency.
          GraphicsEnvironment ge = 
              GraphicsEnvironment.getLocalGraphicsEnvironment();
          GraphicsDevice gd = ge.getDefaultScreenDevice();

          //If translucent windows aren't supported, exit.
          if (!gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
              System.err.println("Translucency is not supported");
              System.exit(0);
          }
          
          setUndecorated(true);
          
          setOpacity(0.55f);
          // Display the window.
          setVisible(false);

        
    }


    public void actionPerformed(ActionEvent e) {
    	dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    	getContentPane().removeAll();
    	setVisible(false);
    	timer.stop();
    	timer=null;
    }


	public void update(Observable o, Object arg) {
		getContentPane().add(buildNotification(((NotificationsInterface)o).getLast()));
		timer = new Timer(3000, this);
		timer.start();
		setVisible(true);
	}

	private JPanel buildNotification(Notification n){
		JPanel p=new JPanel(true);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel jtf=new JLabel(n.getCreationTime().toString());
		jtf.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		jtf.setForeground(new Color(88,88,88));
		jtf.enableInputMethods(false);
		JLabel jta=new JLabel(n.getDescription());
		jta.setForeground(new Color(11,11,11));
		jta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		jta.enableInputMethods(false);
		p.add(jtf);
		p.add(jta);
		p.setVisible(true);
		return p;
	}
}
