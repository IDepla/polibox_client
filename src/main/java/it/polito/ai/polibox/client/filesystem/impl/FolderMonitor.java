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
package it.polito.ai.polibox.client.filesystem.impl;

import it.polito.ai.polibox.client.filesystem.FileMapInterface;
import it.polito.ai.polibox.client.filesystem.FolderMonitorInterface;
import it.polito.ai.polibox.client.filesystem.config.FolderMonitorConfigInterface;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Il folder monitor si occupa di monitorare le cartelle. Il monitoraggio
 * avviene attraverso polling su una find: si fa il find e si recupera la lista
 * dei files, si confronta la lista con la mappa virtuale e si identificano le
 * operazioni da fare.
 * 
 * 
 * @author "Igor Deplano"
 * 
 */

@Component
public class FolderMonitor implements FolderMonitorInterface, Observer {

	@Autowired
	private XmlLoaderInterface<FolderMonitorConfigInterface> settingManager;

	@Autowired
	private XmlLoaderInterface<FileMapInterface> virtualFileManager;

	private Timer schedulerMy, schedulerWithMe;

	private boolean running;

	private Object o;
	
	

	/**
	 * Creates il monitor
	 */
	public FolderMonitor() {
		running = false;
		o = new Object();
	}

	public void afterPropertiesSet() throws Exception {
//		System.out.format("configurazione folder monitor ...\n");

		/**
		 * in questo modo se cambiano i settings posso riavviare.
		 */
		FolderMonitorConfigInterface fmci = settingManager.load();
		((Observable) fmci).addObserver(this);
	}
	
	public void init() throws IOException{
		synchronized (o) {
			schedulerMy = new Timer(true);
			schedulerWithMe = new Timer(true);

			FolderMonitorConfigInterface fmci = settingManager.load();

			schedulerMy.schedule(new FolderMonitorTask(
						fmci.getFolderMy().getCanonicalPath(),
						virtualFileManager.load().getMyResources(),
						virtualFileManager.load(),
						true
					), new Date());

			schedulerWithMe.schedule(new FolderMonitorTask(
						fmci.getFolderWithMe().getCanonicalPath(),
						virtualFileManager.load().getSharedWithMeResources(),
						virtualFileManager.load(),
						true
					), new Date());
		}
	}

	public void start() throws IOException {
		synchronized (o) {
			schedulerMy = new Timer(true);
			schedulerWithMe = new Timer(true);

			FolderMonitorConfigInterface fmci = settingManager.load();

			schedulerMy.schedule(new FolderMonitorTask(
						fmci.getFolderMy().getCanonicalPath(),
						virtualFileManager.load().getMyResources(),
						virtualFileManager.load()
					), new Date(), 
					fmci.getSchedulePeriod());

			schedulerWithMe.schedule(new FolderMonitorTask(
						fmci.getFolderWithMe().getCanonicalPath(),
						virtualFileManager.load().getSharedWithMeResources(),
						virtualFileManager.load()
					), new Date(), 
					fmci.getSchedulePeriod());

			running = true;
		}
	}

	public void stop() {
		synchronized (o) {
			schedulerMy.cancel();
			schedulerWithMe.cancel();
			running = false;
		}

	}

	public boolean isRunning() {
		synchronized (o) {
			return running;
		}
	}

	public synchronized void update(Observable o, Object arg) {
		if (isRunning())
			stop();
		try {
			start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void destroy() throws Exception {
		FolderMonitorConfigInterface fmci = settingManager.load();
		((Observable) fmci).deleteObserver(this);

	}

	public XmlLoaderInterface<FolderMonitorConfigInterface> getSettingManager() {
		return settingManager;
	}

	public void setSettingManager(
			XmlLoaderInterface<FolderMonitorConfigInterface> settingManager) {
		this.settingManager = settingManager;
	}

}
