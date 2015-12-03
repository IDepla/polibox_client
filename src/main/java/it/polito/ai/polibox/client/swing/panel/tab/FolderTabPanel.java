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
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import it.polito.ai.polibox.client.filesystem.config.FolderMonitorConfigInterface;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FolderTabPanel extends JPanel implements ActionListener,
		InitializingBean, DisposableBean, Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6386940619580555686L;

	@Autowired
	private ResourceBundle resourceBoundle;

	@Autowired
	private XmlLoaderInterface<FolderMonitorConfigInterface> settingManager;

	private JFileChooser jfc;
	private JLabel jl, jl2;
	private JButton choose;

	public ResourceBundle getResourceBoundle() {
		return resourceBoundle;
	}

	public void setResourceBoundle(ResourceBundle resourceBoundle) {
		this.resourceBoundle = resourceBoundle;
	}

	public XmlLoaderInterface<FolderMonitorConfigInterface> getSettingManager() {
		return settingManager;
	}

	public void setSettingManager(
			XmlLoaderInterface<FolderMonitorConfigInterface> settingManager) {
		this.settingManager = settingManager;
	}

	public FolderTabPanel() {
		super(true);

		setLayout(new java.awt.FlowLayout());

		jl = new JLabel();// spiegazione directory
		jl2 = new JLabel();// contenuto directory, viene settato quanto
							// visualizzo il componente
		choose = new JButton(); // bottone per selezionare nuovo file.
		choose.addActionListener(this); // l'action listesner è la classe
										// stessa, in questo modo condivido il
										// settingManager.
		add(jl);
		add(jl2);
		add(choose);

	}

	public void actionPerformed(ActionEvent e) {
		jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(this.settingManager.load()
				.getFolder()));
		jfc.setDialogTitle(resourceBoundle.getString("FolderTabPanel.actionPerformed.labelFolder"));
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		jfc.setAcceptAllFileFilterUsed(false);
		//
		if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			settingManager.load().setFolder(
					jfc.getSelectedFile().getAbsolutePath());
		}// altrimenti se non selezioni niente non cambia niente.
	}

	public void update(Observable o, Object arg) {// quando il cambia ricarico
													// il folder attuale.
		FolderMonitorConfigInterface fmci = (FolderMonitorConfigInterface) o;
		jl2.setText(fmci.getFolder());
	}

	public void destroy() throws Exception {
		((Observable) settingManager.load()).deleteObserver(this);
	}

	public void afterPropertiesSet() throws Exception {
		jl.setText(resourceBoundle.getString("FolderTabPanel.afterPropertiesSet.labelFolderPanel"));
		choose.setText(resourceBoundle.getString("FolderTabPanel.afterPropertiesSet.buttonFolder"));
		((Observable) settingManager.load()).addObserver(this);
		jl2.setText(settingManager.load().getFolder());
	}
}
