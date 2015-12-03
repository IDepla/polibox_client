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

import it.polito.ai.polibox.client.filesystem.FileMapInterface;
import it.polito.ai.polibox.client.filesystem.config.FolderMonitorConfigInterface;
import it.polito.ai.polibox.client.filesystem.impl.FilesystemTools;
import it.polito.ai.polibox.client.http.response.Response;
import it.polito.ai.polibox.client.persistency.Resource;
import it.polito.ai.polibox.client.xml.XmlLoaderInterface;
import it.polito.ai.polibox.client.xml.impl.GenericXmlAutoLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractFileAction implements FileAction {

	@Autowired
	protected HttpHelper helper;
	
	@Autowired
	protected XmlLoaderInterface<FolderMonitorConfigInterface> folderConfigManager;
	
	@Autowired
	protected GenericXmlAutoLoader<FileMapInterface> fileMapLoader;
	
	protected AbstractFileAction() {}

	public abstract void createDirectoryHttp(Resource r);

	public abstract void createFileHttp(Resource r);

	public abstract void renameHttp(Resource old, Resource nuova);

	public abstract void deleteHttp(Resource r);

	public abstract void ripristinaHttp(Resource r);

	
	public void createDirectoryFs(Resource r) {
		String s=r.getName();
		try {
			if(!s.startsWith(getTargetFolderCanonicalPath())){
				s=getTargetFolderCanonicalPath()+r.getName();
			}
			File f=new File(s);
			f.mkdir();
			r.setName(s);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void createFileFs(Resource r) {
		String s=r.getName();
//		System.out.println(r.getName()+" file creation in filesystem: ");
		try {
			if(!s.startsWith(getTargetFolderCanonicalPath())){
				s=getTargetFolderCanonicalPath()+r.getName();
			}
			Path p=Paths.get(s);
			if(!Files.exists(p.getParent()))
				Files.createDirectories(p.getParent());
			r.setName(s);
			Files.createFile(p);
		} catch (FileAlreadyExistsException e){
//			System.out.println(r.getName()+" file esisteva già");
		} catch (IOException e) {
//			System.out.println(r.getName()+" ha lanciato eccezione.");
			throw new RuntimeException(e);
		}
	}

	public void renameFs(Resource old, Resource nuova) {
		File oldF,newF;
		String s,w;
		s=old.getName();
		w=nuova.getName();
		try {
			if(!s.startsWith(getTargetFolderCanonicalPath()))
				s=getTargetFolderCanonicalPath()+old.getName();
			if(!w.startsWith(getTargetFolderCanonicalPath()))
				w=getTargetFolderCanonicalPath()+nuova.getName();
			
			oldF=new File(s);
			newF=new File(w);
			oldF.renameTo(newF);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteFs(Resource r) {
		String s=r.getName();
		try {
			if(!s.startsWith(getTargetFolderCanonicalPath()))
				s=getTargetFolderCanonicalPath()+r.getName();

			File o=new File(s);
			FilesystemTools.deleteFolder(o);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * lo implemento come nuovo download.
	 */
	public void ripristinaFs(Resource r) {
//		System.out.println("ripristina file system --> download");
		
		
		download(r);
		r.setDeleted(false);
	}

	public int searchParent(Resource r){
		List<Resource> rList=getTargetList();
		String name=r.getName().substring(0, r.getName().lastIndexOf("/"));
//		System.out.println("stampa: "+name);
		for(Resource w : rList){
			if(w.getName().compareTo(name)==0){
				return w.getId();
			}
		}
		return -1;
	}
	
	/**
	 * upload
	 */
	public void upload(Resource r) {
		FileMapInterface f=fileMapLoader.load();
		if(r.isDirectory()){
			createDirectoryHttp(r);
			f.changeElementToList(getTargetList(), r);
			return;
		}else if(!r.isDirectory()){
			createFileHttp(r);
			f.changeElementToList(getTargetList(), r);	
			// continua con upload
//			System.out.println("id:"+r.getId()+"|version:"+r.getVersion()+"|");
			File file;
			FileInputStream fis = null;
			try {
				file=new File(r.getName());
				fis=new FileInputStream(file);
				byte[] buffer = new byte[FilesystemTools.CHUNK_SIZE];
	            int bytesRead;
	            int index,i;
	            String s,d;
	            WebTarget target=helper.getWebTarget().path("rest/file/"+r.getId()+"/"+r.getVersion());
	            Form form;
	    		index=0;
	    		byte[] b2,bite;
	    		MessageDigest md = MessageDigest.getInstance("SHA3-512");
	            while ((bytesRead = fis.read(buffer)) != -1) {
	            	if(bytesRead<buffer.length){
	            		b2=new byte[bytesRead];
	            		for(i=0;i<b2.length;i++){
	            			b2[i]=buffer[i];
	            		}
	            		buffer=b2;
	            	}
	            	if(index==r.getChunkNumber()){
	            		/**
	            		 * questo mi serve per i file molto grossi. il so mi splitta il file in versioni differenti, 
	            		 * nel server c'è il merge quindi non troppi problemi arrivano.
	            		 */
	            		createFileHttp(r);
	    				f.changeElementToList(getTargetList(), r);	
	            	}
	            	s=Base64.encodeBase64String(buffer);
//	            	System.out.println("ho letto:"+bytesRead+" bytes |"+buffer.length+"|"+md+"|");
	                md.reset();
	                md.update(s.getBytes("UTF-8"));//calcolo digest
	                bite=md.digest();
	    		  	d=org.bouncycastle.util.encoders.Hex.toHexString(bite);
//	    		  	System.out.println("digest:"+d);
	    		  	form=new Form();
		            form.param("version", ""+r.getVersion());
		            form.param("resource", ""+r.getId());
		            form.param("chunkNumber", ""+index);
		            form.param("digest", d);
		            form.param("data", s);
//		            System.out.println("version:"+r.getVersion()+"|resource:"+r.getId()+"|chunk:"+index+"|of:"+r.getChunkNumber()+"|");
	                target.request(MediaType.APPLICATION_JSON_TYPE)
					.headers(helper.getHeaders())
				    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),new GenericType<Response<Resource>>(){});
	                index++;
	            }
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}finally{
				if(fis!=null){
					try {
						fis.close();
						fis=null;
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	/**
	 * download
	 */
	public void download(Resource r) {
		FileMapInterface f=fileMapLoader.load();
		if(r.isDirectory()){
			createDirectoryFs(r);
			return;
		}else{
			createFileFs(r);
			System.out.println(r.getName() +" download");
			/***
			 * recupero ultima versione e aggiorno mappa
			 */
//			System.out.println(r.getName() +" è stata spedita per il recupero ultima versione prima di download");
			WebTarget target=helper.getWebTarget().path("rest/your/file/"+r.getId()+"/last");
			Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
					.headers(helper.getHeaders())
				    .get(new GenericType<Response<Resource>>(){});
			for (Resource e : response.getResult()) {
				r.updateWithoutName(e);
				f.appendToList(getTargetList(), r);
			}
			
//			System.out.println(r.getName() +" è stata spedita per il  download");
			target=helper.getWebTarget().path("rest/file/"+r.getId()+"/"+r.getVersion());
			javax.ws.rs.core.Response downloadResponse=target.request(MediaType.APPLICATION_JSON_TYPE)
					.headers(helper.getHeaders()).get();
			 // read response string
			InputStream inputStream = downloadResponse.readEntity(InputStream.class);
            
			FileOutputStream outputStream=null;
			try {
				outputStream = new FileOutputStream(r.getName());
			
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
			} catch (FileNotFoundException e1) {
				throw new RuntimeException(e1);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}finally{
				try {
					if(outputStream!=null)
						outputStream.close();
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
			
		}
	}

	public abstract String getTargetFolderCanonicalPath() throws IOException;

	public abstract List<Resource> getTargetList();
	
	public HttpHelper getHelper() {
		return helper;
	}

	public void setHelper(HttpHelper helper) {
		this.helper = helper;
	}

	public XmlLoaderInterface<FolderMonitorConfigInterface> getFolderConfigManager() {
		return folderConfigManager;
	}

	public void setFolderConfigManager(
			XmlLoaderInterface<FolderMonitorConfigInterface> folderConfigManager) {
		this.folderConfigManager = folderConfigManager;
	}

	public GenericXmlAutoLoader<FileMapInterface> getFileMapLoader() {
		return fileMapLoader;
	}

	public void setFileMapLoader(
			GenericXmlAutoLoader<FileMapInterface> fileMapLoader) {
		this.fileMapLoader = fileMapLoader;
	}
	
	
}
