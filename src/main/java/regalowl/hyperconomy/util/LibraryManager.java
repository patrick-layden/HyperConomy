package regalowl.hyperconomy.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.ServerConnectionType;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.file.FileTools;


public class LibraryManager implements HyperEventListener {
	private String libFolder;
	private HyperConomy hc;
	private HyperEventHandler heh;
	private LibraryLoadEventTask libraryLoadEventTask;
	private Timer t = new Timer();
	private boolean librariesLoaded;
	private ArrayList<String> dependencyLoadErrors = new ArrayList<String>();
	private boolean dependencyError = false;

	public LibraryManager(HyperConomy hc, HyperEventHandler heh) {
		this.hc = hc;
		this.heh = heh;
		librariesLoaded = false;
		new Thread(new LibraryLoader()).start();
		libraryLoadEventTask = new LibraryLoadEventTask();
		t.schedule(libraryLoadEventTask, 500, 500);
		heh.registerListener(this);
	}
	
	
	
    private class LibraryLoader implements Runnable {

		public void run() {
			FileTools ft = hc.getSimpleDataLib().getFileTools();
			libFolder = hc.getSimpleDataLib().getStoragePath() + File.separator + "lib";
			ft.makeFolder(libFolder);
			
			ArrayList<Dependency> dependencies = new ArrayList<Dependency>();

			dependencies.add(new Dependency(libFolder + File.separator + "sqlite-jdbc-3.7.2.jar", "https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.20.0.jar", true));
			dependencies.add(new Dependency(libFolder + File.separator + "mysql-connector-java-5.1.44.jar", "http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.44/mysql-connector-java-5.1.44.jar", true));
			dependencies.add(new Dependency(libFolder + File.separator + "json-simple-1.1.1.jar", "http://central.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar", true));
			
			//include in jar so that config can load quicker
			//dependencies.add(new Dependency(libFolder + File.separator + "snakeyaml-1.15.jar", "https://oss.sonatype.org/content/groups/public/org/yaml/snakeyaml/1.15/snakeyaml-1.15.jar", true));
			
			
			
			//dependencies.add(new Dependency(libFolder + File.separator + "c3p0-0.9.1.2.jar", "http://central.maven.org/maven2/c3p0/c3p0/0.9.1.2/c3p0-0.9.1.2.jar", true));
			//dependencies.add(new Dependency(libFolder + File.separator + "slf4j-api-1.6.1.jar", "http://central.maven.org/maven2/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar", true));
			
			
			
			dependencies.add(new Dependency(libFolder + File.separator + "javax.servlet-api-3.0.1.jar", "http://central.maven.org/maven2/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-servlet-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-servlet/8.1.9.v20130131/jetty-servlet-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-continuation-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-continuation/8.1.9.v20130131/jetty-continuation-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-http-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-http/8.1.9.v20130131/jetty-http-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-io-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-io/8.1.9.v20130131/jetty-io-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-server-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-server/8.1.9.v20130131/jetty-server-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-util-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-util/8.1.9.v20130131/jetty-util-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-security-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-security/8.1.9.v20130131/jetty-security-8.1.9.v20130131.jar", false));
			dependencies.add(new Dependency(libFolder + File.separator + "jetty-jmx-8.1.9.v20130131.jar", "http://central.maven.org/maven2/org/eclipse/jetty/jetty-jmx/8.1.9.v20130131/jetty-jmx-8.1.9.v20130131.jar", false));
			
			
			dependencies.add(new Dependency(libFolder + File.separator + "opencsv-2.3.jar", "http://central.maven.org/maven2/net/sf/opencsv/opencsv/2.3/opencsv-2.3.jar", false));

			//download missing dependencies	
			for (Dependency d:dependencies) {
				if (ft.fileExists(d.filePath)) continue;
				if (hc.getMC().getServerConnectionType() != ServerConnectionType.GUI && d.guiOnly) continue; //skip dependencies only needed for the GUI
				try {
					URL link = new URL(d.url);
					InputStream in = new BufferedInputStream(link.openStream());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int n = 0;
					while (-1 != (n = in.read(buf))) {
						out.write(buf, 0, n);
					}
					out.close();
					in.close();
					byte[] response = out.toByteArray();
					FileOutputStream fos = new FileOutputStream(d.filePath);
					fos.write(response);
					fos.close();
				} catch (IOException e) {
					hc.getMC().logSevere("[HyperConomy]Failed to download dependency: "+d.getFileName());
					hc.getMC().logSevere("[HyperConomy]Check your internet connection or manually install libraries.  Cannot run with missing dependencies.");
					e.printStackTrace();
					dependencyError = true;
				} catch (Exception e) {
					hc.getMC().logSevere("[HyperConomy]Error while downloading dependency: "+d.getFileName());
					e.printStackTrace();
					dependencyError = true;
				}
			}
			
			if (dependencyError) {
				librariesLoaded = true;
				return;
			}
			
			//generate list of classes to load and add to classpath
			ArrayList<ClassToLoad> classesToLoad = new ArrayList<ClassToLoad>();
			for (Dependency d:dependencies) {
				if (hc.getMC().getServerConnectionType() != ServerConnectionType.GUI && d.guiOnly) continue;
				File f = new File(d.filePath);
				try {
					addURL(f.toURI().toURL());
					JarFile jar = new JarFile(f);
					Enumeration<JarEntry> j = jar.entries();
					while (j.hasMoreElements()) {
						JarEntry e = j.nextElement();
						if (e.isDirectory() || !e.getName().endsWith(".class")) continue;
						String className = e.getName().substring(0, e.getName().length() - 6);
						className = className.replace('/', '.');
						classesToLoad.add(new ClassToLoad(className));
					}
					jar.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			

			//load classes in dependencies
			int count = 0;
			boolean complete = false;
			while (!complete) {
				complete = true;
				Iterator<ClassToLoad> itr = classesToLoad.iterator();
				while (itr.hasNext()) {
					ClassToLoad c = itr.next();
					boolean success = c.load();
					if (success) {
						itr.remove();
					} else {
						complete = false;
					}
				}
				count++;
				if (count > 20) {
					for (ClassToLoad c:classesToLoad) {
						dependencyLoadErrors.add("["+c.className+"]"+c.lastError);
					}
					break;
				}
			}

			librariesLoaded = true;
		}
    }
    
    private class ClassToLoad {
    	String className;
    	String lastError;
    	
    	ClassToLoad(String className) {
    		this.className = className;
    	}
    	
    	boolean load() {
    		try {
    			Class.forName(className);
    			return true;
    		} catch (ClassNotFoundException e) {
    			lastError = CommonFunctions.getErrorString(e);
    			return false;
    		} catch (NoClassDefFoundError e) {
    			lastError = CommonFunctions.getErrorString(e);
    			return false;
    		} catch (Exception e) {
    			lastError = CommonFunctions.getErrorString(e);
    			return false;
    		}
    	}
    }
    
    private class Dependency {
    	String filePath;
    	String url;
    	boolean guiOnly;
    	Dependency(String filePath, String url, boolean guiOnly) {
    		this.url = url;
    		this.filePath = filePath;
    		this.guiOnly = guiOnly;
    	}
    	String getFileName() {
    		return url.substring(url.lastIndexOf("/") + 1, url.length());
    	}
    }
     
    private class LibraryLoadEventTask extends TimerTask {
    	@Override
		public synchronized void run() {
    		if (librariesLoaded && hc.enabled()) {
    			heh.fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.LIBRARIES));
    			libraryLoadEventTask.cancel();
    		}
		}

    }

	private void addURL(URL url) {
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URLClassLoader loader = (URLClassLoader) cl;
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(loader, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof DataLoadEvent) {
			DataLoadEvent devent = (DataLoadEvent)event;
			if (devent.loadType == DataLoadType.COMPLETE) {
				for (String e:dependencyLoadErrors) {
					hc.getDebugMode().debugWriteMessage("[LibraryManager: failed to load class]"+e);
				}
			}
		}
	}
	
	public boolean dependencyError() {
		return dependencyError;
	}

}
