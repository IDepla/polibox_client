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

import javax.ws.rs.client.WebTarget;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



import it.polito.ai.polibox.client.http.HttpClientInterface;
import it.polito.ai.polibox.client.http.Resource;
import it.polito.ai.polibox.client.http.config.ServerConfigInterface;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

@Component
@Scope(value="prototype")
public class BaseResource implements Resource,InitializingBean {

	private WebTarget webTarget;
	
	@Autowired
	private HttpClientInterface client;
	
	@Autowired
	private XmlLoaderInterface<ServerConfigInterface> settingManager;
	
	
	public XmlLoaderInterface<ServerConfigInterface> getSettingManager() {
		return settingManager;
	}

	public void setSettingManager(XmlLoaderInterface<ServerConfigInterface> settingManager) {
		this.settingManager = settingManager;
	}
	
	


	public WebTarget getWebTarget() {
		return webTarget;
	}

	public void setWebTarget(WebTarget webTarget) {
		this.webTarget = webTarget;
	}

	public HttpClientInterface getClient() {
		return client;
	}


	public void setClient(HttpClientInterface client) {
		this.client = client;
	}


	public BaseResource() {
		
	}
	
	public void afterPropertiesSet() throws Exception {
		webTarget = client.getClient().target(settingManager.load().getHost());
		
	}
	
}
