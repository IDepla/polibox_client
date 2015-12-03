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
package it.polito.ai.polibox.client.http.sse;

import it.polito.ai.polibox.client.http.ResourceSSE;
import it.polito.ai.polibox.client.http.config.ServerConfigInterface;
import it.polito.ai.polibox.client.http.sse.event.AccountCreato;
import it.polito.ai.polibox.client.http.sse.event.AccountEmailModificato;
import it.polito.ai.polibox.client.http.sse.event.AccountOpzioniNotificaEmail;
import it.polito.ai.polibox.client.http.sse.event.AccountPasswordModificato;
import it.polito.ai.polibox.client.http.sse.event.AccountPersonalDetailModificato;
import it.polito.ai.polibox.client.http.sse.event.AccountRecuperato;
import it.polito.ai.polibox.client.http.sse.event.CondivisioneAccettata;
import it.polito.ai.polibox.client.http.sse.event.CondivisioneCancellata;
import it.polito.ai.polibox.client.http.sse.event.CondivisioneModificata;
import it.polito.ai.polibox.client.http.sse.event.CondivisioneRichiesta;
import it.polito.ai.polibox.client.http.sse.event.ConflittoDiModifica;
import it.polito.ai.polibox.client.http.sse.event.DeviceCancellato;
import it.polito.ai.polibox.client.http.sse.event.DeviceCollegato;
import it.polito.ai.polibox.client.http.sse.event.DeviceCreato;
import it.polito.ai.polibox.client.http.sse.event.DeviceOpzioniNotificaModificate;
import it.polito.ai.polibox.client.http.sse.event.DeviceRinominato;
import it.polito.ai.polibox.client.http.sse.event.DeviceScollegato;
import it.polito.ai.polibox.client.http.sse.event.DirectoryCancellata;
import it.polito.ai.polibox.client.http.sse.event.DirectoryCreata;
import it.polito.ai.polibox.client.http.sse.event.DirectoryRinominata;
import it.polito.ai.polibox.client.http.sse.event.FileCancellato;
import it.polito.ai.polibox.client.http.sse.event.FileCreato;
import it.polito.ai.polibox.client.http.sse.event.FileModificato;
import it.polito.ai.polibox.client.http.sse.event.FileRinominato;
import it.polito.ai.polibox.client.http.sse.event.RisorsaRipristinataDaCestino;
import it.polito.ai.polibox.client.http.sse.event.RisorsaRipristinataDaHistory;
import it.polito.ai.polibox.client.http.sse.event.RisorsaSpostataInCestino;
import it.polito.ai.polibox.client.http.sse.event.RisorseSincronizzate;
import it.polito.ai.polibox.client.http.sse.event.SpazioInEsaurimento;
import it.polito.ai.polibox.client.http.sse.event.SpazioInsufficiente;
import it.polito.ai.polibox.client.main.ThreadPool;
import it.polito.ai.polibox.client.notification.NotificationsInterface;
import it.polito.ai.polibox.client.notification.config.NotificationOptionsInterface;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * si occupa di creare e gestire la comunicazione SSE con il server 
 * @author "Igor Deplano"
 *
 */
@Component
public class MonitorSSE implements InitializingBean,DisposableBean{

	@Autowired
	private ResourceSSE source;
	
	@Autowired
	private ThreadPool threadPool;
	
	@Autowired
	private XmlLoaderInterface<ServerConfigInterface> serverSetting;
	
	@Autowired
	private XmlLoaderInterface<NotificationOptionsInterface> notificationSetting;
	
	@Autowired
	private XmlLoaderInterface<NotificationsInterface> notificationManager;
	
	
	public MonitorSSE() {
		
	}
	
	public void afterPropertiesSet() throws Exception {
		ServerConfigInterface config=serverSetting.load();
		source.init(config.getHost()+"/rest/notifications/realtime");
		source.registerEventListener(new AccountCreato(notificationSetting, notificationManager));
		source.registerEventListener(new AccountEmailModificato(notificationSetting, notificationManager));
		source.registerEventListener(new AccountOpzioniNotificaEmail(notificationSetting, notificationManager));
		source.registerEventListener(new AccountPasswordModificato(notificationSetting, notificationManager));
		source.registerEventListener(new AccountPersonalDetailModificato(notificationSetting, notificationManager));
		source.registerEventListener(new AccountRecuperato(notificationSetting, notificationManager));
		source.registerEventListener(new CondivisioneAccettata(notificationSetting, notificationManager));
		source.registerEventListener(new CondivisioneCancellata(notificationSetting, notificationManager));
		source.registerEventListener(new CondivisioneModificata(notificationSetting, notificationManager));
		source.registerEventListener(new CondivisioneRichiesta(notificationSetting, notificationManager));
		source.registerEventListener(new DeviceCreato(notificationSetting, notificationManager));
		source.registerEventListener(new DeviceOpzioniNotificaModificate(notificationSetting, notificationManager));
		source.registerEventListener(new DeviceRinominato(notificationSetting, notificationManager));
		source.registerEventListener(new DeviceScollegato(notificationSetting, notificationManager));
		source.registerEventListener(new DeviceCollegato(notificationSetting, notificationManager));
		source.registerEventListener(new DeviceCancellato(notificationSetting, notificationManager));
		source.registerEventListener(new ConflittoDiModifica(notificationSetting, notificationManager));
		source.registerEventListener(new DirectoryCancellata(notificationSetting, notificationManager));
		source.registerEventListener(new DirectoryCreata(notificationSetting, notificationManager));
		source.registerEventListener(new DirectoryRinominata(notificationSetting, notificationManager));
		source.registerEventListener(new FileCancellato(notificationSetting, notificationManager));
		source.registerEventListener(new FileCreato(notificationSetting, notificationManager));
		source.registerEventListener(new FileModificato(notificationSetting, notificationManager));
		source.registerEventListener(new FileRinominato(notificationSetting, notificationManager));
		source.registerEventListener(new RisorsaRipristinataDaCestino(notificationSetting, notificationManager));
		source.registerEventListener(new RisorsaRipristinataDaHistory(notificationSetting, notificationManager));
		source.registerEventListener(new RisorsaSpostataInCestino(notificationSetting, notificationManager));
		source.registerEventListener(new RisorseSincronizzate(notificationSetting, notificationManager));
		source.registerEventListener(new SpazioInEsaurimento(notificationSetting, notificationManager));
		source.registerEventListener(new SpazioInsufficiente(notificationSetting, notificationManager));
	}
	
	public ResourceSSE getSource() {
		return source;
	}

	public void setSource(ResourceSSE source) {
		this.source = source;
	}

	public ThreadPool getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}

	public XmlLoaderInterface<ServerConfigInterface> getServerSetting() {
		return serverSetting;
	}

	public void setServerSetting(
			XmlLoaderInterface<ServerConfigInterface> serverSetting) {
		this.serverSetting = serverSetting;
	}

	public XmlLoaderInterface<NotificationOptionsInterface> getNotificationSetting() {
		return notificationSetting;
	}

	public void setNotificationSetting(
			XmlLoaderInterface<NotificationOptionsInterface> notificationSetting) {
		this.notificationSetting = notificationSetting;
	}

	public XmlLoaderInterface<NotificationsInterface> getNotificationManager() {
		return notificationManager;
	}

	public void setNotificationManager(
			XmlLoaderInterface<NotificationsInterface> notificationManager) {
		this.notificationManager = notificationManager;
	}

	public void destroy() throws Exception {
		source.stop();
	}
	
	public void startMonitor(){
		source.start();
	}
}
