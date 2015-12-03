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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import it.polito.ai.polibox.client.swing.panel.NotificationsPanel;


import javax.swing.JFrame;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NotificationFrame extends JFrame  implements InitializingBean,DisposableBean{

	@Autowired
	private NotificationsPanel notificationsPanel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7530042713470511504L;

	public NotificationFrame() {
		super();
		addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
					setVisible(false);
				}
			}
			
			public void keyReleased(KeyEvent e) {}
			
			public void keyPressed(KeyEvent e) {}
		});
		
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	
		setAlwaysOnTop(true);
	}

	public NotificationsPanel getNotificationsPanel() {
		return notificationsPanel;
	}

	public void setNotificationsPanel(NotificationsPanel notificationsPanel) {
		this.notificationsPanel = notificationsPanel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void destroy() throws Exception {
		
		
	}

	public void afterPropertiesSet() throws Exception {
		getContentPane().add(notificationsPanel);

		setSize(400, 600);
	}

	
}
