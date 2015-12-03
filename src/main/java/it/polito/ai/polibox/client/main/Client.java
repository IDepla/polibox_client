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
package it.polito.ai.polibox.client.main;

import java.io.IOException;
import java.security.Security;

import it.polito.ai.polibox.client.filesystem.FolderMonitorInterface;
import it.polito.ai.polibox.client.filesystem.impl.FolderMonitor;
import it.polito.ai.polibox.client.http.action.FileMapSynchronizer;
import it.polito.ai.polibox.client.http.sse.MonitorSSE;
import it.polito.ai.polibox.client.spring.SpringContext;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
/**
 * questo è il punto di ingresso reale dell'applicazione.
 * per come è costruita è facile gestire anche la possibilità che nel sistema operativo non sia presente la trayIcon.
 * @author "Igor Deplano"
 *
 */
@Component
public class Client  implements InitializingBean,DisposableBean{
	
	
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	    
	    Security.addProvider(new BouncyCastleProvider());
	}
	
	
	@Autowired
	private ProgramContainerInterface program;
	
	
	public ProgramContainerInterface getProgram() {
		return program;
	}

	public void setProgram(ProgramContainerInterface program) {
		this.program = program;
	}

	public Client(){
		
    }	
	
	
/**
 * l'applicazione inizialmente crea e carica il contesto di spring.
 * @param args
 */
	 public static void main (String[] args){
		 ClassPathXmlApplicationContext ctx = SpringContext.getInstance();
		 @SuppressWarnings("unused")
		Client client=ctx.getBean(Client.class);
		
		/*  avvio il monitor:
		 * il monitor si occupa di caricare l'albero dei file e confrontarlo con l'albero virtuale.  
		 */
		FolderMonitorInterface folderMonitor=ctx.getBean(FolderMonitor.class);
		try {
			/**
			 * riallineo file system con mappa
			 */
			folderMonitor.init();
		} catch (IOException e) {
			/**
			 * terminare il programma?
			 */
			throw new RuntimeException(e);
		}
		/* 
		 * avvio il file syncronizer che si occupa di sincronizzare la mappa locale con quella del server
		 * effettua realmente i cambiamenti attraverso il FileAction.  
		 */
		FileMapSynchronizer serverSynchronizer=ctx.getBean(FileMapSynchronizer.class);
		//System.out.println("login effettuato");
		//connection.perform();
		serverSynchronizer.init();
		
		
		MonitorSSE monitorSSE=ctx.getBean(MonitorSSE.class);
//		//dopo il login.
		monitorSSE.startMonitor();
		
		
		
		
		try {
			/**
			 * monitor del file system attivo.
			 */
			folderMonitor.start();
			serverSynchronizer.start();
		} catch (IOException e) {
			/**
			 * terminare il programma?
			 */
			throw new RuntimeException(e);
		}
	
		
		
		
		
		
		
		
		 /*
		 Settings s=client.getSettingManager().forceReload();
		 
		 NotificationOptionInterface[] n=new NotificationOptionImpl[2];
		 n[0]=new NotificationOptionImpl();
		 n[0].setDescription("prova descrizione");
		 n[0].setId("4521");
		 n[0].setOption(true);
		 
		 n[1]=new NotificationOptionImpl();
		 n[1].setDescription("prova descrizione 2");
		 n[1].setId("4522");
		 n[1].setOption(false);
		 s.setNotifications(n);
		 
		 client.getSettingManager().saveSettings();
		 */
	 }

	public void destroy() throws Exception {
		
	}

	public void afterPropertiesSet() throws Exception {
		
		 
	}
}
