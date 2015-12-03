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

import java.util.Observable;
import java.util.Observer;

import org.springframework.stereotype.Component;

import it.polito.ai.polibox.client.notification.config.NotificationOptionInterface;

@Component
public class NotificationOption extends Observable implements NotificationOptionInterface {
	private String id;
	private String description;
	private boolean option;
	
	public NotificationOption() {
	 super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		setChanged();
		notifyObservers();
		clearChanged();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
		setChanged();
		notifyObservers();
		clearChanged();
	}
	public boolean isOption() {
		return option;
	}
	public void setOption(boolean option) {
		this.option = option;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
	//	System.out.println(this.getClass().getName()+" has added a new observer: "+ o.getClass().getName()+" observer count now:"+this.countObservers());
	}
}
