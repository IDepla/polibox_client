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
package it.polito.ai.polibox.client.http.sse.event;

import org.glassfish.jersey.media.sse.InboundEvent;

import it.polito.ai.polibox.client.http.sse.EventListenerInterface;
import it.polito.ai.polibox.client.notification.NotificationsInterface;
import it.polito.ai.polibox.client.notification.config.NotificationOptionsInterface;
import it.polito.ai.polibox.client.persistency.Notification;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

public class Message implements EventListenerInterface{

	protected int code;
	protected boolean enabled;
	protected XmlLoaderInterface<NotificationsInterface> notificationManager;
	protected XmlLoaderInterface<NotificationOptionsInterface> notificationSetting;
	
	protected Message(int code,
			XmlLoaderInterface<NotificationOptionsInterface> notificationSetting,
			XmlLoaderInterface<NotificationsInterface> notificationManager) {
		this.code=code;
		this.notificationManager=notificationManager;
		this.notificationSetting=notificationSetting;
	}

	public void onEvent(InboundEvent inboundEvent) {
		NotificationOptionsInterface options=notificationSetting.load();
		Notification n=inboundEvent.readData(Notification.class);
		try {
			//se è abilitato aggiungo la notifica alla mia cache
			if(n.getCode()==code &&
			   options.getNotificationByCode(""+code).isOption() &&
			   inboundEvent.getName().equals("message")){
//				System.out.println("è accaduto un evento:"+inboundEvent.getName()+"|"+inboundEvent.readData(String.class));
				notificationManager.load().appendNotification(n);
			}
		} catch (Exception e) {
			
		}
	}
	

	public String getEventName() {
		return "message";//""+code;
	}

}
