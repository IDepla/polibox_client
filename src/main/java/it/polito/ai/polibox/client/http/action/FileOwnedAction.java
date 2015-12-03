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
import it.polito.ai.polibox.client.filesystem.impl.FilesystemTools;
import it.polito.ai.polibox.client.http.request.Directory;
import it.polito.ai.polibox.client.http.request.FileInput;
import it.polito.ai.polibox.client.http.request.ResourceInput;
import it.polito.ai.polibox.client.http.response.Response;
import it.polito.ai.polibox.client.persistency.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

@Component
public class FileOwnedAction extends AbstractFileAction  implements FileAction{


	
	public FileOwnedAction() {	
		super();
	}

	@Override
	public String getTargetFolderCanonicalPath() throws IOException {
		return folderConfigManager.load().getFolderMy().getCanonicalPath();
	}
	
	@Override
	public List<Resource> getTargetList() {
		FileMapInterface f=fileMapLoader.load();
		return f.getMyResources();
	}
	

	public void createDirectoryHttp(Resource r) {
		Directory request=new Directory();
//		System.out.println(r.getName() +" è stata spedita per la creazione,directory");
		try {
			request.setName(r.getName().replace(getTargetFolderCanonicalPath(), ""));
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}
		WebTarget target=helper.getWebTarget().path("rest/your/files/newdirectory");
		Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
				.headers(helper.getHeaders())
			    .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE),new GenericType<Response<Resource>>(){});
		for (Resource e : response.getResult()) {
			try {
				e.setName(getTargetFolderCanonicalPath()+e.getName());
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			r.updateWithoutName(e);
			return;
		}
		
	}

	public void createFileHttp(Resource r) {
		FileInput request=new FileInput();
		File f=new File(r.getName());
		request.setSize(f.length());
		request.setChunkNumber(FilesystemTools.getChunkNumber(f));
		request.setMime(FilesystemTools.getMime(f));
		request.setName(r.getName());
		request.setDigest(FilesystemTools.sha3Digest(f));
		
//		System.out.println(r.getName() +" è stata spedita per la creazione,file |" + r.getInode()+ "| " + r.getId() + "| "+ r.getParent() + "| " + r.getVersion() );
		try {
			request.setName(r.getName().replace(getTargetFolderCanonicalPath(), ""));
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}
		WebTarget target=helper.getWebTarget().path("rest/your/files");
		Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
				.headers(helper.getHeaders())
			    .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE),new GenericType<Response<Resource>>(){});
		for (Resource e : response.getResult()) {
			try {
				e.setName(getTargetFolderCanonicalPath()+e.getName());
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			r.updateWithoutName(e);
			return;
		}
		
	}
	
	/**
	 * rinomina
	 */
	public void renameHttp(Resource old, Resource nuova) {
		ResourceInput request=new ResourceInput();
//		System.out.println(nuova.getName() +" è stata spedita per la rinomina");
		try {
			request.setName(nuova.getName().replace(getTargetFolderCanonicalPath(), ""));
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}
		WebTarget target=helper.getWebTarget().path("rest/your/file/"+nuova.getId());
		Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
				.headers(helper.getHeaders())
			    .put(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE),new GenericType<Response<Resource>>(){});
		for (Resource e : response.getResult()) {
			try {
				e.setName(getTargetFolderCanonicalPath()+e.getName());
			} catch (IOException e1) {
				//no happens
			}
			nuova.updateWithoutName(e);
			return;
		}
	}

	/**
	 * nella cancellazione recupero l'id e lo spedisco
	 */
	public void deleteHttp(Resource r) {
		int[] request=new int[1];
//		System.out.println(r.getName() +" è stata spedita per la cancellazione");
		request[0]=r.getId();
		WebTarget target=helper.getWebTarget().path("rest/your/files/trash");
		Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
				.headers(helper.getHeaders())
			    .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE),new GenericType<Response<Resource>>(){});
		for (Resource e : response.getResult()) {
			r.setDeleted(true);
			r.updateWithoutName(e);
			fileMapLoader.load().changeElementToList(getTargetList(), r);
			return;
		}
	}
	
	/**
	 * ripristina da cestino
	 */
	public void ripristinaHttp(Resource r) {
		int[] request=new int[1];
//		System.out.println(r.getName() +" è stata spedita per la ripristinazione da cestino");
		request[0]=r.getId();
		WebTarget target=helper.getWebTarget().path("rest/your/files/trash/ripristina");
		Response<Resource> response=target.request(MediaType.APPLICATION_JSON_TYPE)
				.headers(helper.getHeaders())
			    .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE),new GenericType<Response<Resource>>(){});
		for (Resource e : response.getResult()) {
			r.setDeleted(false);
			r.updateWithoutName(e);
			fileMapLoader.load().changeElementToList(getTargetList(), r);
			return;
		}
	}


	
	
	

}
