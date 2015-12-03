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
package it.polito.ai.polibox.client.persistency;

import java.util.Date;
import java.util.Observable;
import java.util.StringTokenizer;


public class Resource extends Observable{

	private int id;
	
	private String name;
	
	private String inode;
	
	private boolean deleted;
	
	private boolean writingLock; 
	
	private boolean directory;
	
	private Date hdLastModify;
	
	private Date serverLastModify;
	
	private String digest;
	
	private boolean toSynchronize;
	
	private int chunkNumber;
	
	private int size;
	
	private String mime;
	
	private Date creationTime;

	private int version;
	
	private int owner;
	
	private boolean syncronizedWithHardDisk;
	
	private int parent;
	
	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public String getInode() {
		return inode;
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public Resource() {
		super();
		inode="";
		syncronizedWithHardDisk=false;
	}
	
	public Resource clone(){
		return (Resource)this.clone();
	}
	/**
	 * costruttore per una resource dal file system
	 * 
	 * @param s : inode permessi ultimamodifica pathcompleto
	 */
	public Resource(String s,String delimiter){
		this();
		StringTokenizer st=new StringTokenizer(s,delimiter);
		inode=st.nextToken();
		if(st.nextToken().startsWith("d"))
			directory=true;
		else
			directory=false;
		hdLastModify=new Date();
		hdLastModify.setTime(Math.round(Double.parseDouble(st.nextToken()))*1000);
		name=st.nextToken();
		id=0;
		version=0;
		deleted=false;
		syncronizedWithHardDisk=true;
	}

	public void updateWithoutName(Resource r){
		if(r.getInode()!=null && !r.getInode().isEmpty())
			inode=r.getInode();
		if(r.getId()!=0)
			id=r.getId();
		if(r.getOwner()!=0)
			owner=r.getOwner();
		if(r.getVersion()!=0){
			version=r.getVersion();
			chunkNumber=r.getChunkNumber();
			digest=r.getDigest();
			mime=r.getMime();
			size=r.getSize();
		}
		if(r.getServerLastModify()!=null)
			serverLastModify=r.getServerLastModify();
		
		deleted=r.isDeleted();
		directory=r.isDirectory();
		writingLock=r.isWritingLock();
		
		setChanged();
		notifyObservers();
		clearChanged();
	}
	
	public void updateNullFields(Resource r){
		if(r.getName()!=null && getName()==null)
			name=r.getName();
		if(r.getInode()!=null && getInode()==null)
			inode=r.getInode();
		if(r.getHdLastModify()!=null && getHdLastModify()==null)
			hdLastModify=r.getHdLastModify();
		if(r.getServerLastModify()!=null && getServerLastModify()==null)
			serverLastModify=r.getServerLastModify();
		if(r.getDigest()!=null && getDigest()==null)
			digest=r.getDigest();
		if(r.getMime()!=null && getMime()==null)
			mime=r.getMime();
		if(r.getCreationTime()!=null && getCreationTime()==null)
			creationTime=r.getCreationTime();
		
		setChanged();
		notifyObservers();
		clearChanged();
	}		
	
	public boolean isSyncronizedWithHardDisk() {
		return syncronizedWithHardDisk;
	}

	public void setSyncronizedWithHardDisk(boolean syncronizedWithHardDisk) {
		this.syncronizedWithHardDisk = syncronizedWithHardDisk;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public boolean isToSynchronize() {
		return toSynchronize;
	}

	public void setToSynchronize(boolean toSynchronize) {
		this.toSynchronize = toSynchronize;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public int getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public void setServerLastModify(Date serverLastModify) {
		this.serverLastModify = serverLastModify;
		setChanged();
		notifyObservers();
		clearChanged();
	}
	
	public void setHdLastModify(Date hdLastModify) {
		this.hdLastModify = hdLastModify;
		setChanged();
		notifyObservers();
		clearChanged();
	}
	
	public Date getServerLastModify() {
		return serverLastModify;
	}
	
	public Date getHdLastModify() {
		return hdLastModify;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public boolean isWritingLock() {
		return writingLock;
	}

	public void setWritingLock(boolean writingLock) {
		this.writingLock = writingLock;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean isDirectory) {
		this.directory = isDirectory;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	/*
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Resource){
			Resource r=(Resource)obj;
			if( r.getId()==getId() &&
				r.getVersion()==getVersion() &&
				r.getName().contentEquals(getName()))
				return true;
		}
		return super.equals(obj);
	}
	*/
}
