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
package it.polito.ai.polibox.client.http.config.impl;

import java.util.Observable;

import org.springframework.stereotype.Component;

import it.polito.ai.polibox.client.http.config.ServerConfigInterface;

@Component
public class ServerConfig extends Observable implements ServerConfigInterface {

	private String host;
	private String user;
	private String device;
	private String passcode;
	
	public String getDevice() {
		return device;
	}

	public synchronized void setDevice(String device) {
		this.device = device;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public ServerConfig() {
		super();
	}

	public String getHost() {
		return host;
	}


	public synchronized void setHost(String host) {
		this.host = host;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
		setChanged();
		notifyObservers();
		clearChanged();
	}




}
