package regalowl.hyperconomy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileTools {
	public ArrayList<String> getFolderContents(String folderpath) {
		File dir = new File(folderpath);
		File[] contents = dir.listFiles();
		ArrayList<String> libContents = new ArrayList<String>();
		if (contents != null) {
			for (int i = 0; i < contents.length; i++) {
				String cpath = contents[i].toString();
				cpath = cpath.substring(cpath.lastIndexOf(File.separator) + 1, cpath.length());
				libContents.add(cpath.toString());
			}
		}
		return libContents;
	}

	public String getJarPath() {
		String path = HyperConomy.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String serverpath = "";
		try {
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			serverpath = decodedPath.substring(0, decodedPath.lastIndexOf("plugins"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return serverpath;
	}

	public void copyFileFromJar(String resource, String destination) {
		InputStream resStreamIn = HyperConomy.class.getClassLoader().getResourceAsStream(resource);
		if (resStreamIn == null) {
			Logger log = Logger.getLogger("Minecraft");
			if (resource.equalsIgnoreCase("Languages/en_US.txt")) {
				log.severe("[HyperConomy] Failed to copy language file backup.  Do not use /reload to update HyperConomy.");
			} else {
				log.severe("[HyperConomy] Failed to copy file.  Restart your server to fix this.  Do not use /reload.");
			}
			return;
		}
		File newFile = new File(destination);
		try {
			OutputStream ostream = new FileOutputStream(newFile);
			int l;
			byte[] buffer = new byte[4096];
			while ((l = resStreamIn.read(buffer)) > 0) {
				ostream.write(buffer, 0, l);
			}
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void makeFolder(String path) {
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public void deleteFile(String path) {
		try {
			File file = new File(path);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unZipFile(String zipFile, String outputFolder) {
		try {
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				byte[] buffer = new byte[1024];
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// There is a better way in Java 7
	public void copyFile(String sourcePath, String destPath) {
		try {
			File sfile = new File(sourcePath);
			File dfile = new File(destPath);
			FileInputStream iStream = new FileInputStream(sfile);
			FileOutputStream oStream = new FileOutputStream(dfile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = iStream.read(buffer)) > 0) {
				oStream.write(buffer, 0, length);
			}
			iStream.close();
			oStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeStringToFile(String text, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void makeFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public String getStringFromFile(String path) {
		try {
			//BufferedReader input = new BufferedReader(new FileReader(path));
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
			String text = "";
			String string;
			while ((string = input.readLine()) != null) {
				text += string;
			}
			input.close();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

		
	}
	
	
	
	
	
	
	
	
	
	
	
	public ArrayList<String> getStringArrayFromFile(String path) {
		ArrayList<String> text = new ArrayList<String>();
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
			//BufferedReader input = new BufferedReader(new FileReader(path));
			String string;
			while ((string = input.readLine()) != null) {
				text.add(string);
			}
			input.close();
			return text;
		} catch (IOException e) {
			e.printStackTrace();
			text.add("error");
			return text;
		}
	}

	public boolean fileExists(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getTimeStamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}
}
