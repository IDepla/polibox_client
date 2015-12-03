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
package it.polito.ai.polibox.client.http.impl;

import it.polito.ai.polibox.client.http.HttpSSEClientInterface;
import it.polito.ai.polibox.client.http.ResourceSSE;
import it.polito.ai.polibox.client.http.RestAuthenticationFeature;
import it.polito.ai.polibox.client.http.config.ServerConfigInterface;
import it.polito.ai.polibox.client.http.sse.EventListenerInterface;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="singleton")
public class BaseResourceSSE implements ResourceSSE {
	private WebTarget target;
	@Autowired
	private HttpSSEClientInterface client;
	private EventSource eventSource;
	@Autowired
	protected XmlLoaderInterface<ServerConfigInterface> settingManager;
	
	public BaseResourceSSE() {
	}
	
	public WebTarget getTarget() {
		return target;
	}

	public void setTarget(WebTarget target) {
		this.target = target;
	}

	public HttpSSEClientInterface getClient() {
		return client;
	}

	public void setClient(HttpSSEClientInterface client) {
		this.client = client;
	}

	public EventSource getEventSource() {
		return eventSource;
	}

	public void setEventSource(EventSource eventSource) {
		this.eventSource = eventSource;
	}

	public void registerEventListener(EventListenerInterface el) {
		/*
		EventListener listener = new EventListener() {
	        @Override
	        public void onEvent(InboundEvent inboundEvent) {
	            System.out.println(inboundEvent.getName() + "; "
	                    + inboundEvent.readData(String.class));
	        }
	    };
	    */
	    eventSource.register(el, el.getEventName());
	}
	
	public void init(String targetURL) {
		ServerConfigInterface sc=settingManager.load();
		RestAuthenticationFeature raf=new RestAuthenticationFeature();
		target = client.getClient()
				.register(raf)
				.target(targetURL)
				.property("user", sc.getUser())
				.property("device", sc.getDevice())
				.property("password", sc.getPasscode());
		
		eventSource = EventSource.target(target).build();
	}
	public void start() {
		eventSource.open();
	}

	public void stop() {
		eventSource.close();
	}

	public XmlLoaderInterface<ServerConfigInterface> getSettingManager() {
		return settingManager;
	}

	public void setSettingManager(
			XmlLoaderInterface<ServerConfigInterface> settingManager) {
		this.settingManager = settingManager;
	}

}
