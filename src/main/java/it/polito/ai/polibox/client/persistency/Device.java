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
import java.util.Date;



public class Device implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 570321607093342706L;

	private int id;
	
	private String device_name;
	
	private String fileListDigest;
	
	private Date lastPing;
	
	private Date lastLogin;
	
	private Date lastCompleteSync;
	
	private Date devicetimeSync;
	
	private boolean deviceDeletable;
	
	private String randomSalt;
	
	private String autoAuthenticationKey;
	
	private Date lastAutoAuthenticationTimestamp;
	
	private int deviceMaxSpace;
	


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}



	public String getFileListDigest() {
		return fileListDigest;
	}

	public void setFileListDigest(String fileListDigest) {
		this.fileListDigest = fileListDigest;
	}

	public Date getLastPing() {
		return lastPing;
	}

	public void setLastPing(Date lastPing) {
		this.lastPing = lastPing;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastCompleteSync() {
		return lastCompleteSync;
	}

	public void setLastCompleteSync(Date lastCompleteSync) {
		this.lastCompleteSync = lastCompleteSync;
	}

	public Date getDevicetimeSync() {
		return devicetimeSync;
	}

	public void setDevicetimeSync(Date devicetimeSync) {
		this.devicetimeSync = devicetimeSync;
	}

	public boolean isDeviceDeletable() {
		return deviceDeletable;
	}

	public void setDeviceDeletable(boolean deviceDeletable) {
		this.deviceDeletable = deviceDeletable;
	}


	public String getRandomSalt() {
		return randomSalt;
	}

	public void setRandomSalt(String randomSalt) {
		this.randomSalt = randomSalt;
	}

	public String getAutoAuthenticationKey() {
		return autoAuthenticationKey;
	}

	public void setAutoAuthenticationKey(String autoAuthenticationKey) {
		this.autoAuthenticationKey = autoAuthenticationKey;
	}

	public Date getLastAutoAuthenticationTimestamp() {
		return lastAutoAuthenticationTimestamp;
	}

	public void setLastAutoAuthenticationTimestamp(
			Date lastAutoAuthenticationTimestamp) {
		this.lastAutoAuthenticationTimestamp = lastAutoAuthenticationTimestamp;
	}

	public int getDeviceMaxSpace() {
		return deviceMaxSpace;
	}

	public void setDeviceMaxSpace(int deviceMaxSpace) {
		this.deviceMaxSpace = deviceMaxSpace;
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null && 
			obj instanceof Device && 
			((Device) obj).getId()==this.getId()){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if(id != 0)
			return id;
		return 0;
	}
}
