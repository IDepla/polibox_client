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
package it.polito.ai.polibox.client.swing.panel.tab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import it.polito.ai.polibox.client.notification.config.NotificationOptionInterface;
import it.polito.ai.polibox.client.notification.config.NotificationOptionsInterface;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NotificationTabPanel  extends JPanel  implements InitializingBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8991771758853764655L;
	
	@Autowired
	private XmlLoaderInterface<NotificationOptionsInterface> settingManager;
	
	@Autowired
	private ResourceBundle resourceBoundle;
	
	public XmlLoaderInterface<NotificationOptionsInterface> getSettingManager() {
		return settingManager;
	}

	public void setSettingManager(XmlLoaderInterface<NotificationOptionsInterface> settingManager) {
		this.settingManager = settingManager;
	}

	public ResourceBundle getResourceBoundle() {
		return resourceBoundle;
	}

	public void setResourceBoundle(ResourceBundle resourceBoundle) {
		this.resourceBoundle = resourceBoundle;
	}

	public NotificationTabPanel() {
		super(true);
		
		
	}

	public class JNotificationButton extends JButton implements ActionListener,Observer,DisposableBean{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5282424398582114600L;
		
		NotificationOptionInterface noi;
		
		public JNotificationButton(NotificationOptionInterface noi) {
			super();
			this.noi=noi;
			addActionListener(this);
			setText(""+noi.isOption());
			repaint();
			((Observable) noi).addObserver(this);
//			System.out.println("JbuttonNotification N° osservatori: "+((Observable)noi).countObservers());
		}
		
		public void actionPerformed(ActionEvent e) {
			noi.setOption(!noi.isOption());
		}
		
		public void update(Observable o, Object arg) {
//			System.out.println(this.getClass().getName()+ " ha catturato la modifica di "+o.getClass().getName());
			setText(""+((NotificationOptionInterface)o).isOption());
		}
		
		public void destroy() throws Exception {
			((Observable) noi).deleteObserver(this);	
		}
	}


	public void afterPropertiesSet() throws Exception {
		NotificationOptionsInterface setting=settingManager.load();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		List<NotificationOptionInterface> nt=setting.getNotifications();
//		System.out.println(setting.getClass().getName() + " contiene "+ ((Observable)setting).countObservers()+" observers");
		int i;
		for (i=0;i<nt.size();i++) {
			JPanel p=new JPanel(true);
			JNotificationButton j=new JNotificationButton(nt.get(i));
			p.add(new JLabel(nt.get(i).getDescription()));
			p.add(j);
			add(p);
//			System.out.println("nt.get(i) N° osservatori: "+((Observable)nt.get(i)).countObservers());
		}
	}
	
}
