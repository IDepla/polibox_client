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
package it.polito.ai.polibox.client.notification.config.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.springframework.stereotype.Component;

import it.polito.ai.polibox.client.notification.config.NotificationOptionInterface;
import it.polito.ai.polibox.client.notification.config.NotificationOptionsInterface;

@Component
public class NotificationOptions extends Observable implements NotificationOptionsInterface,Observer {

	private List<NotificationOptionInterface> notifications;
	private int notificationNumber;
	
	public int getNotificationNumber() {
		return notificationNumber;
	}

	public void setNotificationNumber(int notificationNumber) {
		this.notificationNumber = notificationNumber;
	}

	public NotificationOptions() {
		super();
		notifications=new ArrayList<NotificationOptionInterface>();
	}

	public synchronized void setNotifications(List<NotificationOptionInterface> notifications) {
		this.notifications = notifications;
	}


	public List<NotificationOptionInterface> getNotifications() {
		return notifications;
	}

	public NotificationOptionInterface getNotificationByCode(String code) throws Exception{
		int i=0;
		while (notifications.size()>i){
			if(notifications.get(i).getId().equals(code))
				return notifications.get(i);
			i++;
		}
		throw new Exception();
	}

	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers();
		clearChanged();
	}

}
