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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.springframework.stereotype.Component;

@Component
public class FileMap extends Observable implements FileMapInterface {

	private List<Resource> myResources;
	private List<Resource> sharedWithMeResources;

	public FileMap() {

		myResources = Collections.synchronizedList(new ArrayList<Resource>());
		
		sharedWithMeResources = Collections.synchronizedList(new ArrayList<Resource>());
		
	}


	
	private boolean elementExists(List<Resource> l,Resource r){
//		System.out.println("la resource list exists "+(l == sharedWithMeResources || l==myResources));
		synchronized (l) {
			if(l == sharedWithMeResources || l==myResources){
				for (int i = 0; i < l.size(); i++) {
					if(
					   (l.get(i).getId()!=0 && l.get(i).getId()==r.getId()) ||
					   (l.get(i).getInode()!=null && !l.get(i).getInode().isEmpty() && l.get(i).getInode()==r.getInode())
					   ){
/*						System.out.println("la resource list exists: " +
								"id->"+l.get(i).getId()+"="+r.getId()+
								"inode->"+l.get(i).getInode()+"="+r.getInode()
							);
*/
						return true;
					}
				}
			}
//			System.out.println("la resource list exists false");
			return false;
		}
	}
	
	public void appendToList(List<Resource> l,Resource r){
//		System.out.println("la resource list append "+(l == sharedWithMeResources || l==myResources));
		synchronized (l) {
			if(l == sharedWithMeResources || l==myResources){
				if(elementExists(l, r)){
					changeElementToList(l, r);
					return;
				}
//				System.out.println("la lista esiste nella mappa procedo ad inserimento");
				l.add(r);
//				System.out.println("append size:"+l.size());
				setChanged();
				notifyObservers();
				clearChanged();
			}
		}
	}
	public void removeToList(List<Resource> l,Resource r){
//		System.out.println("la resource list remove "+(l == sharedWithMeResources || l==myResources));
		synchronized (l) {
			if(l == sharedWithMeResources || l==myResources){
//				System.out.println("la lista esiste nella mappa procedo ad rimozione");
				l.remove(r);
				setChanged();
				notifyObservers();
				clearChanged();
			}
		}
	}
	
	public void changeElementToList(List<Resource> l,Resource r){
//		System.out.println("la resource list change "+(l == sharedWithMeResources || l==myResources));
		synchronized (l) {
			for (int i = 0; i < l.size(); i++) {
				if( (l.get(i).getId()!=0 && l.get(i).getId()==r.getId()) ||
					(l.get(i).getInode()!=null && !l.get(i).getInode().isEmpty() && l.get(i).getInode()==r.getInode())){
					l.get(i).updateWithoutName(r);
				}
			}
			setChanged();
			notifyObservers();
			clearChanged();
		}
	}
	
	public List<Resource> getMyResources() {
		return myResources;
	}

	public List<Resource> getSharedWithMeResources() {
		return sharedWithMeResources;
	}
	
	public void setSharedWithMeResources(List<Resource> sharedWithMeResources) {
		this.sharedWithMeResources = sharedWithMeResources;
	}

	public void setMyResources(List<Resource> myResources) {
		this.myResources = myResources;
	}

}
