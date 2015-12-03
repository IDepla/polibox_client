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
package it.polito.ai.polibox.client.notification.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.springframework.stereotype.Component;


import it.polito.ai.polibox.client.notification.NotificationsInterface;
import it.polito.ai.polibox.client.persistency.Notification;

@Component
public class Notifications extends Observable implements NotificationsInterface {

	private List<Notification> notifications;
	
		
	public Notifications() {
		notifications=Collections.synchronizedList(new ArrayList<Notification>());
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotification(List<Notification> notifications){
		this.notifications=notifications;
	}
	public void appendNotification(Notification notification) {
		synchronized (notifications) {
//			System.out.println("notifica aggiunta code:"+notification.getCode()+"|id:"+notification.getId()+"|descr:"+notification.getDescription()+"|");
			notifications.add(notification);
			
//			System.out.println("dimensione notifica in notifications:"+notifications.size()+"|");
			setChanged();
			notifyObservers();
			clearChanged();
		}
	}
	
	public Notification getLast(){
		synchronized (notifications) {
			if(notifications.size()>0){
				return notifications.get(notifications.size()-1);
			}
			return null;
		}
	}
}
