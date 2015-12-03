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

import it.polito.ai.polibox.client.swing.panel.tab.CacheTabPanel;
import it.polito.ai.polibox.client.swing.panel.tab.FolderTabPanel;
import it.polito.ai.polibox.client.swing.panel.tab.NotificationTabPanel;
import it.polito.ai.polibox.client.swing.panel.tab.ServerTabPanel;

import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * questo pannello contiene dati di configurazione un tabpane che ha al suo
 * interno - server - opzioni notifica - cache - directory
 * 
 * @author "Igor Deplano"
 * 
 */
@Component
public class OptionsPanel extends JPanel implements InitializingBean,
		DisposableBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5149896161261438604L;

	@Autowired
	private ServerTabPanel serverTab;

	@Autowired
	private FolderTabPanel folderTab;

	@Autowired
	private NotificationTabPanel notifyTab;

	@Autowired
	private CacheTabPanel cacheTab;

	@Autowired
	private ResourceBundle resourceBundle;

	public ServerTabPanel getServerTab() {
		return serverTab;
	}

	public void setServerTab(ServerTabPanel serverTab) {
		this.serverTab = serverTab;
	}

	public FolderTabPanel getFolderTab() {
		return folderTab;
	}

	public void setFolderTab(FolderTabPanel folderTab) {
		this.folderTab = folderTab;
	}

	public NotificationTabPanel getNotifyTab() {
		return notifyTab;
	}

	public void setNotifyTab(NotificationTabPanel notifyTab) {
		this.notifyTab = notifyTab;
	}

	public CacheTabPanel getCacheTab() {
		return cacheTab;
	}

	public void setCacheTab(CacheTabPanel cacheTab) {
		this.cacheTab = cacheTab;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public OptionsPanel() {
		super(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public OptionsPanel(LayoutManager layout) {
		super(layout);
	}

	public OptionsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public OptionsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public void destroy() throws Exception {

	}

	public void afterPropertiesSet() throws Exception {
		JTabbedPane jtb = new JTabbedPane();
		// ci sono 3 panel, dati del server, dati della cartella, dati delle
		// notifiche, cache notifiche
	/*
		jtb.addTab(resourceBundle
				.getString("OptionsPanel.afterPropertiesSet.tabOptionServer"),
				serverTab);
		jtb.setMnemonicAt(0, KeyEvent.VK_1);
*/
		jtb.addTab(resourceBundle
				.getString("OptionsPanel.afterPropertiesSet.tabOptionFolder"),
				folderTab);
		jtb.setMnemonicAt(0, KeyEvent.VK_2);

		JScrollPane pane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVisible(true);
		pane.setViewportView(notifyTab);

		jtb.addTab(
				resourceBundle
						.getString("OptionsPanel.afterPropertiesSet.tabOptionNotification"),
				pane);
		jtb.setMnemonicAt(1, KeyEvent.VK_3);

/*
		jtb.addTab(resourceBundle
				.getString("OptionsPanel.afterPropertiesSet.tabOptionCache"),
				cacheTab);
		jtb.setMnemonicAt(3, KeyEvent.VK_4);
*/
		add(jtb);

	}

}
