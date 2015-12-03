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
package it.polito.ai.polibox.client.swing.panel;

import it.polito.ai.polibox.client.notification.NotificationsInterface;
import it.polito.ai.polibox.client.persistency.Notification;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationsPanel extends JPanel  implements InitializingBean,DisposableBean,Observer{

	@Autowired
	private XmlLoaderInterface<NotificationsInterface> notificationManager;
	
	JScrollPane scrollPane;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1136948707626518619L;

	public NotificationsPanel() {
		super(true);
	}

	public NotificationsPanel(LayoutManager layout) {
		super(layout);
	}

	public NotificationsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public NotificationsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}


	
	public XmlLoaderInterface<NotificationsInterface> getNotificationManager() {
		return notificationManager;
	}

	public void setNotificationManager(
			XmlLoaderInterface<NotificationsInterface> notificationManager) {
		this.notificationManager = notificationManager;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void destroy() throws Exception {
		((Observable) notificationManager.load()).deleteObserver(this);
		
	}
	
	public void afterPropertiesSet() throws Exception {
		((Observable) notificationManager.load()).addObserver(this);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	}

	public void update(Observable o, Object arg) {
//		notificationManager.load()
		
		NotificationsInterface ni = (NotificationsInterface) o;
//		System.out.println("dimensione notifica in update:"+ni.getNotifications().size()+"|");
		Notification n=ni.getLast();
		if(n!=null){
//			System.out.println("code:"+n.getCode()+"|id:"+n.getId()+"|descr:"+n.getDescription()+"|");
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
			add(p);
			repaint();
		}
	}

	
	
	
}
