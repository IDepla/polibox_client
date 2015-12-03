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
package it.polito.ai.polibox.client.swing;


import it.polito.ai.polibox.client.filesystem.config.FolderMonitorConfigInterface;
import it.polito.ai.polibox.client.main.ProgramContainerInterface;
import it.polito.ai.polibox.client.swing.frames.NotificationFrame;
import it.polito.ai.polibox.client.swing.frames.OptionFrame;
import it.polito.ai.polibox.client.swing.frames.TranslucentNotificationFrame;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * il tray program gestisce il programma della trayIcon, crea i pane, i frame e
 * li lega nel menù della tray.
 * 
 * @author "Igor Deplano"
 * 
 */
@Component
public class TrayProgram implements ProgramContainerInterface,
		InitializingBean, DisposableBean {

	@Autowired
	private XmlLoaderInterface<FolderMonitorConfigInterface> settingManager;

	@Autowired
	private OptionFrame optionFrame;

	@Autowired
	private NotificationFrame notificationFrame;
	
	@Autowired
	private TranslucentNotificationFrame translucentNotificationFrame;

	@Autowired
	private ResourceBundle resourceBundle;

	private TrayIcon trayIcon;
	private SystemTray systemTray;

	public TrayProgram() {
		optionFrame = null;
		notificationFrame = null;
		translucentNotificationFrame=null;
	}

	public TranslucentNotificationFrame getTranslucentNotificationFrame() {
		return translucentNotificationFrame;
	}

	public void setTranslucentNotificationFrame(
			TranslucentNotificationFrame translucentNotificationFrame) {
		this.translucentNotificationFrame = translucentNotificationFrame;
	}

	public XmlLoaderInterface<FolderMonitorConfigInterface> getSettingManager() {
		return settingManager;
	}

	public void setSettingManager(
			XmlLoaderInterface<FolderMonitorConfigInterface> settingManager) {
		this.settingManager = settingManager;
	}

	private PopupMenu initPopupMenu() {
		PopupMenu p = new PopupMenu();

		MenuItem m;

		m = new MenuItem(
				resourceBundle
						.getString("TrayProgram.initPopupMenu.syncFolderOpen"));
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = new File(settingManager.load().getFolder());
				try {
					Desktop.getDesktop().open(f);
				} catch (IOException e1) {
					throw new RuntimeException();
				}

			}
		});
		p.add(m);

		p.addSeparator();

		m = new MenuItem(
				resourceBundle.getString("TrayProgram.initPopupMenu.options"));
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				optionFrame.setVisible(true);

			}
		});
		p.add(m);

		m = new MenuItem(
				resourceBundle
						.getString("TrayProgram.initPopupMenu.notifications"));
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				notificationFrame.setVisible(true);
			}
		});
		p.add(m);

		p.addSeparator();

		m = new MenuItem(
				resourceBundle.getString("TrayProgram.initPopupMenu.exit"));
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		p.add(m);

		return p;
	}

	public void destroy() {

	}

	public OptionFrame getOptionFrame() {
		return optionFrame;
	}

	public void setOptionFrame(OptionFrame optionFrame) {
		this.optionFrame = optionFrame;
	}

	public NotificationFrame getNotificationFrame() {
		return notificationFrame;
	}

	public void setNotificationFrame(NotificationFrame notificationFrame) {
		this.notificationFrame = notificationFrame;
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public void afterPropertiesSet() throws Exception {
		if (SystemTray.isSupported()) {
			systemTray = SystemTray.getSystemTray();
			PopupMenu popup = initPopupMenu();

			Image image = Toolkit
					.getDefaultToolkit()
					.getImage(
							resourceBundle
									.getString("TrayProgram.afterPropertiesSet.imageLogo"));
			System.out.println(new File(resourceBundle
					.getString("TrayProgram.afterPropertiesSet.imageLogo"))
					.exists());
			trayIcon = new TrayIcon(
					image,
					resourceBundle
							.getString("TrayProgram.afterPropertiesSet.trayIconTooltip"),
					popup);
			trayIcon.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Point pos = e.getLocationOnScreen();
                    pos.y+=20;
                    translucentNotificationFrame.setLocation(pos);
                    e.consume();
                }
             });
			try {
				systemTray.add(trayIcon);
			} catch (AWTException e) {
				throw new RuntimeException();
			}
		} else {
			throw new RuntimeException();
		}
	}
}
