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
package it.polito.ai.polibox.client.http.action;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import it.polito.ai.polibox.client.filesystem.FileMapInterface;
import it.polito.ai.polibox.client.filesystem.config.FolderMonitorConfigInterface;
import it.polito.ai.polibox.client.http.response.Response;
import it.polito.ai.polibox.client.persistency.Resource;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;
import it.polito.ai.polibox.client.xml.impl.GenericXmlAutoLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileMapSynchronizer{

	@Autowired
	private HttpHelper helper;
	
	@Autowired
	private GenericXmlAutoLoader<FileMapInterface> fileMapLoader;
	
	@Autowired
	private XmlLoaderInterface<FolderMonitorConfigInterface> folderConfigManager;
	
	@Autowired
	private FileSharedAction fileSharedAction;
	
	@Autowired
	private FileOwnedAction fileOwnedAction;
	
	private Timer schedulerMy, schedulerWithMe;

	private boolean running;

	private Object o;
	/**
	 * devo fare il login
	 * recuperare la lista dei file?
	 * confrontare con la lista che ho sulla cartella di sharing
	 * recuperare i file che mancano o spedire i file aggiornati in locale.
	 */
	
	public FileMapSynchronizer() {
		running=false;
		o=new Object();
	}
	
	/**
	 * fa la prima sincronizzazione.
	 */
	public void init(){
		/**
		 * prendere la filemap
		 * contiene sia shared che my
		 */
		FileMapInterface fileMap=fileMapLoader.load();
		
		/***
		 * recuperare la maplist dal server dei tuoi file
		 */
		hdMapToHttpMapSync("rest/files/all",fileMap.getMyResources(),fileOwnedAction);
		
		/***
		 * recuperare la maplist dal server dei file condivisi con te
		 */
		hdMapToHttpMapSync("rest/sharedwithyou/files/all",fileMap.getSharedWithMeResources(),fileSharedAction);
		//fileMapLoader.save();
	}
	
	/**
	 * inizia il syncronizer
	 */
	public void start(){
		synchronized (o) {
			schedulerMy = new Timer(true);
			schedulerWithMe = new Timer(true);

			final FileMapInterface fileMap=fileMapLoader.load();
			FolderMonitorConfigInterface fmci = folderConfigManager.load();

			schedulerMy.schedule(new TimerTask() {
						@Override
						public void run() {
							hdMapToHttpMapSync("rest/files/all",fileMap.getMyResources(),fileOwnedAction);
							
						}
					}, new Date(), 
					fmci.getSchedulePeriod());

			schedulerWithMe.schedule(new TimerTask() {
						@Override
						public void run() {
							hdMapToHttpMapSync("rest/sharedwithyou/files/all",fileMap.getSharedWithMeResources(),fileSharedAction);
							
						}
					}, new Date(), 
					fmci.getSchedulePeriod());

			running = true;
		}
	}
	
	/**
	 * ferma il syncronizer
	 */
	public void stop(){
		synchronized (o) {
			schedulerMy.cancel();
			schedulerWithMe.cancel();
			running = false;
		}
		
	}
	
	public boolean isRunning(){
		return running;
	}
	
	private void hdMapToHttpMapSync(String link,List<Resource> hdres,FileAction action){
		WebTarget target=helper.getWebTarget().path(link);
		Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
				.headers(helper.getHeaders())
			    .get(new GenericType<Response<Resource>>(){});
		synchronized (hdres) {
			/**
			 * azzero i file che recupero dal filesystem, quelli che non trovo nel server devono essere spediti (upload)
			 */
			for (Resource rr : hdres) {
				rr.setSyncronizedWithHardDisk(false);
			}
			for (Resource e : response.getResult()) {
//				System.out.println("analizzo "+e.getId() +" "+e.getName()+" "+e.getParent()+" "+e.getOwner());
				parse(hdres,e,action);
			}
			for (int i = 0; i < hdres.size(); i++) {
				Resource rr=hdres.get(i);
//				System.out.println("resource "+rr.getInode()+" syncronized:"+rr.isSyncronizedWithHardDisk());
				if(rr.isSyncronizedWithHardDisk()==false){
					/**
					 * se non è stato trovato fino ad ora significa che è da fare upload
					 * se e solo se la risorsa non è marcata come cancellata.
					 */
					if(!rr.isDeleted()){
						/**
						 * se non è cancellata sul filesystem allora faccio upload sul server
						 */
//						System.out.println("rr non è cancellata inizio a fare upload:resource:"+rr.getId()+"|version:"+rr.getVersion()+"|inode:"+rr.getInode());
						action.upload(rr);
					}else{
						/**
						 * se è cancellata anche sul file system allora devo rimuovere da mappa.
						 */
						action.deleteFs(rr);
						fileMapLoader.load().removeToList(hdres, rr);
					}
				}
			}
		}
		
	}
	
	/**
	 * prende in carico lista dei file sul file system e una risorsa presa dal server.
	 * il compito è controllare se questa risorsa esiste e valutare il da farsi.
	 * notare che dentro la lista i path sono assoluti, quindi prima di poterlo controllare bisogna fare
	 * il trim del path.
	 * @param list  lista dei file sul filesystem
	 * @param r risorsa presa dal server
	 */
	private void parse(List<Resource> list, Resource r, FileAction action){
		int compareResult;
		boolean found=false;
		synchronized (list) {
			
			/*
			 * controllo le risorse sulla mappa
			 */
//			System.out.println("sto per entrare nel ciclo riga 126");
			for (Resource resource : list) {
				/**
				 * se trovo la risorsa che sto cercando: devo guardare id
				 * se ho una risorsa dal server questa sicuramente avrà l'id
				 * sull'hard disk se questa è già stata sincronizzata allora avrà un id
				 * se non è stata sincronizzata allora mi attendo che questa non ci sia.
				 * sto scartando la possibilità di una sincronizzazione esterna. IMPORTANT
				 */
				try {
					compareResult = resource.getName().compareTo(action.getTargetFolderCanonicalPath()+r.getName());
					if(compareResult==0){ 
//						System.out.println(action.getTargetFolderCanonicalPath()+r.getName());
//						System.out.println(resource.getName());
//						System.out.println("compare:"+compareResult);
						if(resource.getId()==0 && r.getId()!=0){
							/**
							 * caso in cui hai che il file c'è ma non hanno id
							 */
//							System.out.println(resource.getId());
//							System.out.println(r.getId());
//							System.out.println("update");
							resource.setId(r.getId());
							resource.setParent(r.getParent());
							/**
							 * caso di startup, le mappe non sono allineate le allineo anche di versione
							 */
							if(r.getVersion()!=0 && resource.getVersion()==0){
								resource.setVersion(r.getVersion());
							}
							fileMapLoader.load().changeElementToList(list, resource);
						}else{}
					}
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
				
				
				
				if(resource.getId()==r.getId()){
					resource.setSyncronizedWithHardDisk(true);
//					System.out.println("risorsa "+r.getId() +" trovata");
					/**
					 * controllo se è cancellata sul server
					 */
					resource.updateNullFields(r);
					found=true;
					if(r.isDeleted()){//se è cancellata sul server
						//allora cancello anche in locale.
						int cmpd=resource.getServerLastModify().compareTo(r.getServerLastModify());
						if(cmpd<0){//la data che ho sulla mappa dell'ultima modifica sul server è precedente all'ultima modifica reale
							/**
							 * il server è stato modificato dopo
							 * devo cancellare sul file system
							 */
							action.deleteFs(resource);
						}else{
							/**
							 * le date sono uguali perchè il file system non  può essere stato modificato dopo
							 * quindi vuol dire che se non è cancellato in locale allora vuol dire che è stato ripristinato in locale
							 */
							if(!resource.isDeleted()){
								action.ripristinaHttp(resource);
							}else{
								action.deleteFs(resource);
							}
						}
						return;
					}else if(resource.isDeleted()){ 
						/**
						 * controllo se invece è cancellata in locale
						 * situaazione: sul server non è cancellata ma è cancellata in locale
						 */
						int cmpd=resource.getServerLastModify().compareTo(r.getServerLastModify());
						if(cmpd<0){//la data che ho sulla mappa dell'ultima modifica sul server è precedente all'ultima modifica reale
							/**
							 * il server è stato modificato dopo
							 * devo ripristinare, rifaccio il download? oppure ripristino attraverso inode?
							 * risposta: demando all'action
							 */
							action.ripristinaFs(resource);
						}else{
							/**
							 * le date sono uguali perchè il file system non  può essere stato modificato dopo
							 * quindi vuol dire che se non è cancellato in locale allora vuol dire che è stato cancellato in locale
							 * e va cancellato sul server
							 */
							if(!r.isDeleted()){
								action.deleteHttp(r);
							}else{
								/**
								 * se è cancellato anche sul server lo cancello in locale.
								 */
								action.deleteFs(resource);
							}
						}
						return;
					}
					
					/**
					 * la risorsa è stata trovata
					 * controllare se ci sono differenze nel nome!!
					 */
					
					try {
//						System.out.println("la risorsa è stata trovata sulla mappa:");
//						System.out.println(resource.getName());
//						System.out.println(action.getTargetFolderCanonicalPath()+r.getName());
						compareResult = resource.getName().compareTo(action.getTargetFolderCanonicalPath()+r.getName());
						if(compareResult!=0){ 
							/**
							 * se son diversi si deve guardare chi ha modificato prima
							 */
							if(resource.getServerLastModify().equals(r.getServerLastModify())){
								/**
								 * se le date son uguali allora vuol dire che devo spedire la rinomina,
								 * vuol dire che l'ho fatta in locale ed è da sincronizzare
								 */
								action.renameHttp(r,resource);
							}else{
								/**
								 * se son diversi allora vuol dire che il server è avanti, 
								 * quindi devo cambiare in locale con quello del server  
								 */
								action.renameFs(resource,r);
							}
						}else{
							/**
							 * se i nomi sono uguali allora devo controllare le versioni se stiamo 
							 * parlando di file
							 */	
							if(!r.isDirectory()){
								if(resource.getVersion()==r.getVersion()){ 
									/**
									 * se non ci sono differenze allora va bene così
									 */
									return;
								}else{
//									System.out.println("controllo versione: fs:"+resource.getVersion()+"="+r.getVersion());
									/**
									 * se ci sono differenze nella versione non può che essere che il server è avanti nella versione, quindi 
									 * devo sincronizzare la risorsa con un download dell'ultima versione. 
									 */
									action.download(r);
								}
							}
							//se è una directory allora va bene così.
							return;
						}
						
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					
					return;
				}
			}
			/**
			 * se la risorsa non c'è nell'hard disk allora devo sincronizzarla, cioè devo farne il download
			 * 
			 * se e solo se non è cancellata sul server, altrimenti non faccio niente.
			 */
			if (found==false && !r.isDeleted())
				action.download(r);
		}
	}
	
	
	public HttpHelper getHelper() {
		return helper;
	}

	public void setHelper(HttpHelper helper) {
		this.helper = helper;
	}

	public GenericXmlAutoLoader<FileMapInterface> getFileMapLoader() {
		return fileMapLoader;
	}

	public void setFileMapLoader(
			GenericXmlAutoLoader<FileMapInterface> fileMapLoader) {
		this.fileMapLoader = fileMapLoader;
	}

	public XmlLoaderInterface<FolderMonitorConfigInterface> getFolderConfigManager() {
		return folderConfigManager;
	}

	public void setFolderConfigManager(
			XmlLoaderInterface<FolderMonitorConfigInterface> folderConfigManager) {
		this.folderConfigManager = folderConfigManager;
	}

	public FileSharedAction getFileSharedAction() {
		return fileSharedAction;
	}

	public void setFileSharedAction(FileSharedAction fileSharedAction) {
		this.fileSharedAction = fileSharedAction;
	}

	public FileOwnedAction getFileOwnedAction() {
		return fileOwnedAction;
	}

	public void setFileOwnedAction(FileOwnedAction fileOwnedAction) {
		this.fileOwnedAction = fileOwnedAction;
	}
	
	
}
