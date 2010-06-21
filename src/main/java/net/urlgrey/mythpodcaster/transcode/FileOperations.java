/*
 * FileOperations.java
 *
 * Created: Apr 9, 2010
 *
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.transcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.UID;

/**
 * @author scottkidder
 *
 */
public class FileOperations {

	static final int BUFF_SIZE = 100000;
	static final byte[] buffer = new byte[BUFF_SIZE];

	/**
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	public static void copy(File from, File to) throws IOException{
		InputStream in = null;
		OutputStream out = null; 
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead); 
				}
			} 
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}


	/**
	 * @param directory
	 */
	public static void deleteDir(File directory) {
		try {
			// delete files in the directory
			File[] files = directory.listFiles();
			for(int i=0;i<files.length;i++) {
				File file = files[i];
				file.delete();
			}
			directory.delete();
		} catch (Exception e) {
			// ignore
		}
	}


	/**
	 * Creates a folder in "java.io.tmpdir" with a unique name.
	 */
	public static File createTempDir() throws IOException {
		UID uid = new UID();
		File dir = new File(System.getProperty("java.io.tmpdir"));
		File tmp = new File(dir, uid.toString());
		if(!tmp.mkdirs()) {
			throw new IOException("Cannot create tmp dir ["+tmp.toString()+"]");
		}
		return tmp;
	}
}
