package de.moonflower.jfritz.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

import de.moonflower.jfritz.autoupdate.Logger;
import de.moonflower.jfritz.autoupdate.MDGenerator;

public class CreateUpdateFile {
	
	private final static String className = "(CreateUpdateFile) ";

	transient private File targetDirectory;

	transient private String version = "";

	transient private File versionFile;
	
	transient private File updateFile; 

	transient private BufferedWriter versionFileWriter;
	
	transient private BufferedWriter updateFileWriter; 
	
	transient private Vector<Directory> dirs = new Vector<Directory>();

	public void setToDir(final String targetDir) {
		targetDirectory = new File(targetDir);
		versionFile = new File(targetDirectory.getAbsolutePath()
				+ System.getProperty("file.separator") + "current.txt");
		updateFile = new File(targetDirectory.getAbsolutePath()
				+ System.getProperty("file.separator") + "update.txt");
		try {
			versionFileWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(versionFile), "UTF8"));
		} catch (UnsupportedEncodingException e) {
			Logger.err(className + "Could not write file with UTF8 encoding");
		} catch (FileNotFoundException e) {
			Logger.err(className + "Could not find file " + versionFile);
		}

		try {
			updateFileWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(updateFile), "UTF8"));
		} catch (UnsupportedEncodingException e) {
			Logger.err(className + "Could not write file with UTF8 encoding");
		} catch (FileNotFoundException e) {
			Logger.err(className + "Could not find file " + updateFile);
		}
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Directory createAddDirectory() {
		Directory dir = new Directory();
		dirs.add(dir);
		return dir;
	}

	public class Directory {
		transient private String dir;

		public void setDirectory(final String file) {
			this.dir = file;
		}

		public String getDirectory() {
			return dir;
		}
	}

	private void setVersion() {
		versionFile.delete();
		try {
			versionFile.createNewFile();
		} catch (IOException e1) {
			Logger.err(className + "Could not create version file: "
					+ versionFile.getAbsolutePath());
		}

		try {
			versionFileWriter.write(version);
			versionFileWriter.close();
		} catch (IOException e) {
			Logger.err(className + "Could not write to file " + versionFile);
		}
	}

	private void setUpdateFiles() {
		updateFile.delete();
		try {
			updateFile.createNewFile();
		} catch (IOException e1) {
			Logger.err(className + "Could not create update file: "
					+ updateFile.getAbsolutePath());
		}
		try {
			Iterator<Directory> iterator;
			for (iterator = dirs.iterator(); iterator.hasNext();) {
				File[] entries = new File(iterator.next().getDirectory()).listFiles();
				for (int j = 0; j < entries.length; j++) {
					String fileDigest = "";
					try {
						byte[] digest = MDGenerator.messageDigest(entries[j]
								.getAbsolutePath(), "MD5");
						for (int i = 0; i < digest.length; i++) {
							fileDigest += Integer.toHexString(digest[i] & 0xFF);
						}
					} catch (Exception e) {
						Logger.err(className + "Could not create message digest for file \""
										+ entries[j].getName() + "\"");
					}
					Logger.msg(className + "File: " + entries[j].getName() + " ("
							+ fileDigest + ")");
					updateFileWriter.write(entries[j].getName() + ";" + fileDigest + ";"
							+ entries[j].length());
					updateFileWriter.newLine();

				}
			}
			updateFileWriter.close();
		} catch (UnsupportedEncodingException e1) {
			Logger.err(className + "Could not write file with UTF8 encoding");
		} catch (FileNotFoundException e1) {
			Logger.err(className + "Could not find file " + updateFile);
		} catch (IOException e) {
			Logger.err(className + "Could not write to file " + updateFile);
		}
	}

	public void execute() {
		Logger.msg(className + "Target directory: "
				+ targetDirectory.getAbsolutePath());
		setVersion();
		setUpdateFiles();
	}

}
