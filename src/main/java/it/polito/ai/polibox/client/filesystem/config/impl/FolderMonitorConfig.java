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
package it.polito.ai.polibox.client.filesystem.config.impl;

import it.polito.ai.polibox.client.filesystem.config.FolderMonitorConfigInterface;

import java.io.File;
import java.util.Observable;

import org.springframework.stereotype.Component;

@Component
public class FolderMonitorConfig extends Observable implements FolderMonitorConfigInterface{

	String folder;
	String folderMe=File.separator+"my";
	String folderWithMe=File.separator+"withMe";

	private long schedulePeriod;
	
	public FolderMonitorConfig() {
		super();
	}
	public String getFolder() {
		return folder;
	}


	public long getSchedulePeriod() {
		return schedulePeriod;
	}
	public void setSchedulePeriod(long schedulePeriod) {
		this.schedulePeriod = schedulePeriod;
	}
	public synchronized void setFolder(String folder) {
		this.folder = folder;
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public File getFolderMy() {
		return new File(folder+folderMe);
	}


	public File getFolderWithMe() {
		return new File(folder+folderWithMe);
	}
}
