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
package it.polito.ai.polibox.client.xml.impl;

import it.polito.ai.polibox.client.xml.XmlLoaderInterface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;


public abstract class GenericXmlAutoLoader<T> implements XmlLoaderInterface<T>,
		InitializingBean, DisposableBean,Observer {

	@Autowired
	private Marshaller marshaller;
	@Autowired
	private Unmarshaller unmarshaller;

	private T settings;

	private Object sync=new Object();
	

	@Autowired
	private ResourceBundle resourceBundle;
	
	public abstract String getFileName(); 

	protected GenericXmlAutoLoader() {
		settings = null;
	}

	public T load() {
		synchronized (sync) {
			if (settings == null)
				return forceReload();
			return settings;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	@SuppressWarnings("unchecked")
	protected T forceReload() {
		synchronized (sync) {
			FileInputStream is = null;
			try {
				is = new FileInputStream(getFileName());
				this.settings = (T) this.unmarshaller
						.unmarshal(new StreamSource(is));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(
						getFileName()
								+ resourceBundle
										.getString("SettingManager.forceReload.notfound"));
			} catch (XmlMappingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
//			((Observable)settings).addObserver(this);
			return settings;
		}
	}

	
	
	public void save() {
		synchronized (sync) {
			FileOutputStream os = null;
//			System.out.println(this.getClass().getName()+" settings start save");
			try {
				os = new FileOutputStream(getFileName());
				this.marshaller.marshal(settings, new StreamResult(os));
//				System.out.println(this.getClass().getName()+" settings saved "+settings);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(
						getFileName()
								+ resourceBundle
										.getString("SettingManager.forceReload.notfound"));
			} catch (XmlMappingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		
	}
	
	public void update(Observable o, Object arg) {
//		System.out.println(this.getClass().getName() +" ha catturato la modifica di "+o.getClass().getName());
		save();
	}

	public void destroy() throws Exception {
		save();
	}

	public void afterPropertiesSet() throws Exception {
		((Observable)load()).addObserver(this);
//		System.out.println(""+load().getClass().getName()+" è diventata osservata da "+this.getClass().getName());
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public T getSettings() {
		return settings;
	}

	public void setSettings(T settings) {
		this.settings = settings;
	}

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

}
