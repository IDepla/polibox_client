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
package it.polito.ai.polibox.client.http;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Configuration;

public class RestAuthenticationFilter implements ClientRequestFilter, ClientResponseFilter {

	public static final String USER="user";
	public static final String DEVICE="device";
	public static final String PASSWORD="password";
	private Configuration c;
	
	public RestAuthenticationFilter(Configuration configuration) {
		c=configuration;
	}

	public void filter(ClientRequestContext request,
			ClientResponseContext responseContext) throws IOException {
		  
		  if ( request.getHeaders().containsKey(USER) &&
			   request.getHeaders().containsKey(DEVICE) &&
			   request.getHeaders().containsKey(PASSWORD)
			  ) {
	            return;
	        }else{
	        	request.getHeaders().add(USER, c.getProperty(USER));
	        	request.getHeaders().add(DEVICE, c.getProperty(DEVICE));
	        	request.getHeaders().add(PASSWORD, c.getProperty(PASSWORD));
	        	
	        }

	}

	public void filter(ClientRequestContext request) throws IOException {
		  filter(request,null);
	}

}
