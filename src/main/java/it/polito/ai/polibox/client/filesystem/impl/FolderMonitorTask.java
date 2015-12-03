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
package it.polito.ai.polibox.client.filesystem.impl;

import it.polito.ai.polibox.client.filesystem.FileMapInterface;
import it.polito.ai.polibox.client.persistency.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TimerTask;

public class FolderMonitorTask extends TimerTask implements Observer{

	private String[] monitorCommand = { "/bin/sh", "-c",
			"find pathToCHANGE -printf '%i|%M|%T@|%p\\n' | sed 1d | sed '/~$/ d'" };

	private String baseDir;

	private List<Resource> resourceList;
	
	private FileMapInterface map;
	
//	private boolean dirtyMap;
	
	/**
	 * evitare di fare la scansione della mappa  sincronizzata: mi serve una lista
	 */
	private List<String> oldOutput;
	
	@SuppressWarnings("unused")
	private boolean init;

	public FolderMonitorTask(String dir, List<Resource> list, FileMapInterface fmi, boolean init) {
		baseDir = dir;
		resourceList = list;
		monitorCommand[2] = monitorCommand[2].replace("pathToCHANGE", baseDir);
		map=fmi;
//		dirtyMap=true;
		this.init=init;
		oldOutput=new ArrayList<String>();
		//System.out.println("sto istanziando il task init:"+init);
	}
	
	public FolderMonitorTask(String dir, List<Resource> list, FileMapInterface fmi) {
		this(dir,list,fmi,false);
	}

	@Override
	public void run() {
		Process script_exec;
		try {
			
			script_exec = Runtime.getRuntime().exec(monitorCommand);
			script_exec.waitFor();
			BufferedReader stdInput;
			if (script_exec.exitValue() != 0) { //errore viene spedito in console.
				stdInput = new BufferedReader(new InputStreamReader(
						script_exec.getErrorStream()));
				String s;
				while ((s = stdInput.readLine()) != null) {
					System.err.println(s);
				}
			} else {//devo capire che è successo.
				stdInput = new BufferedReader(new InputStreamReader(
						script_exec.getInputStream()));
				String s;
				/**
				 * se la mappa è dirty allora vuol dire che ci sono state modifiche
				 */
//				if(dirtyMap==true){
					synchronized (resourceList) {
						for (Resource r : resourceList) {
//							System.out.println("resource "+r.getInode());
							r.setSyncronizedWithHardDisk(false);
						}
						oldOutput=new ArrayList<String>();
						while ((s = stdInput.readLine()) != null) {
							oldOutput.add(s);
							/** 
							 * prendo in carico una riga: questa mi rappresenta:
							 * inode permessi ultimamodifica pathcompleto
							 * passo questa riga al costruttore di resource.
							 */
							parse(new Resource(s, "|"));
						}
						for (Resource r : resourceList) {
//							System.out.println("resource "+r.getInode()+" syncronized:"+r.isSyncronizedWithHardDisk());
							if(r.isSyncronizedWithHardDisk()==false){
								/**
								 * se è attivo vuol dire che sta sincronizzando quindi fa niente se non c'è
								 */
								if(r.isWritingLock() || r.isDeleted()==true){
									continue;
								}else{
//									System.out.println("set deleted and write resource:"+r.getInode());
									r.setDeleted(true);
									map.changeElementToList(resourceList, r);
								}
							}
						}
//						dirtyMap=false;
					}
//				}else{
//					/**
//					 * se la mappa non è stata modificata allora controllo se ho modifiche nella sequenza di output 
//					 */
//					Iterator<String> it=oldOutput.iterator();
//					while ((s = stdInput.readLine()) != null) {
//						try{
//							if(it.next().contentEquals(s))
//								continue;
//							parse(new Resource(s, "|"));
//						}catch(java.util.NoSuchElementException e){//ho inserito un nuovo file nell'hd
//							parse(new Resource(s, "|"));
//						}
//					}
//				}
				
			}
		} catch (IOException e) {
			//non ho niente da fare;
		} catch (InterruptedException e) {
			//non ho niente da fare;
		}

	}

	/**
	 * in questa funzione faccio il controllo tra la resource presa dal filesystem e la mia mappa virtuale.
	 */
	private void parse(Resource r){
		synchronized (resourceList) {
			int compareResult;
			for (Resource fsRes : resourceList) {
				
				
				compareResult = r.getName().compareTo(fsRes.getName());
				if(compareResult==0){
					if(fsRes.getInode()!=null && 
						fsRes.getInode().isEmpty() && 
						!r.getInode().isEmpty()){
//						System.out.println("id:"+fsRes.getId());
//						System.out.println("id:"+r.getId());
//						System.out.println("inode:"+fsRes.getInode());
//						System.out.println("inode:"+r.getInode());
//						System.out.println("update");
						fsRes.setInode(r.getInode());
						fsRes.setHdLastModify(r.getHdLastModify());
					}else if(fsRes.getInode().compareTo(r.getInode())!=0){
						/*se il nome è uguale e gli inode son diversi
						 * siamo nella situazione dove ho cancellato e rifatto con lo stesso nome un file,
						 * devo cancellare il vecchio dalla mappa.
						 */
						map.removeToList(resourceList, fsRes);
						break;//esco dal ciclo ed inserisco il nuovo elemento
					}
				}
				
				
				if(r.getInode().equalsIgnoreCase(fsRes.getInode())){
					/** 
					 * se ho trovato inode significa che il file era già presente nella mappa, 
					 * ora devo vedere se ci sono stati cambiamenti:
					 * - nel nome: --> implica rinominazione e spostamento --> spedire comando opportuno
					 * - nell'ultima modifica --> implica modifiche dall'esterno --> nuovo upload
					 */
					if(r.getName().contentEquals(fsRes.getName())){
						/**
						 * se i contenuti sono uguali allora non c'è stato una rinomina, dunque
						 * controllo per la data.
						 */
						if(r.getHdLastModify().compareTo(fsRes.getHdLastModify())==0){
							/**
							 * se anche questa è uguale allora vuol dire che non c'è modifica dall'ultima volta.
							 */
							fsRes.setSyncronizedWithHardDisk(true);
							return;
						}else{
							/**
							 * se c'è stata una modifica in questa mappa vuol dire che devo 
							 * fare l'upload, quindi metto la versione a 0. 
							 */
							fsRes.setVersion(0);
							fsRes.setHdLastModify(r.getHdLastModify());
							map.changeElementToList(resourceList, fsRes);
						}
					}else{
						/**
						 * se c'è stata una rinomina, che è anche equivalente ad uno spostamento, allora devo 
						 * applicare la rinomina sulla mappa.
						 */
						fsRes.setName(r.getName());
						map.changeElementToList(resourceList, fsRes);
					}
					fsRes.setSyncronizedWithHardDisk(true);
					return;
				}
			}
		
			/**
			 * se non è stato trovato in tutta la mappa allora significa che il file è stato creato dall'esterno dell'ambiente
			 * quindi devo inserire la mia resource nella mappa.
			 * inserisco con id e version == 0, implicherà upload.
			 */
			map.appendToList(resourceList, r);
//			System.out.println(resourceList+" resource list dimension:"+resourceList.size());
		}
	}

	public void update(Observable o, Object arg) {
		synchronized (resourceList) {
//			dirtyMap=true;
		}
	}
}
