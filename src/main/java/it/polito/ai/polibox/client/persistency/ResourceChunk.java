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

import java.io.Serializable;
import java.sql.Blob;

public class ResourceChunk implements Serializable{

	
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3022993641942784028L;

	private ResourceChunkId pk;
	
	private String digest;
	
	private Blob data;
	
	private int size;
	
	public ResourceChunk() {
		pk= new ResourceChunkId();
	}
	
	public ResourceChunk(Resource r, int version, int chunkNumber){
		pk.getResourceVersion().getPk().setResource(r);
		pk.getResourceVersion().getPk().setVersion(version);
		pk.setNumber(chunkNumber);
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	public ResourceChunkId getPk() {
		return pk;
	}

	public void setPk(ResourceChunkId pk) {
		this.pk = pk;
	}
	

	@Override
	public boolean equals(Object obj) {
		return this.getPk().equals(obj);
	}
}
