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

import it.polito.ai.polibox.client.persistency.Resource;

public interface FileAction {

	public void createDirectoryHttp(Resource r);
	public void createFileHttp(Resource r);
	public void renameHttp(Resource old,Resource nuova);
	public void deleteHttp(Resource r);
	public void ripristinaHttp(Resource r);
	
	public void createDirectoryFs(Resource r);
	public void createFileFs(Resource r);
	public void renameFs(Resource old,Resource nuova);
	public void deleteFs(Resource r);
	public void ripristinaFs(Resource r);
	
//	public void fileModified(Resource r);
	public void upload(Resource r);
	public void download(Resource r);
	
	public String getTargetFolderCanonicalPath() throws IOException;
}
