package org.osgi.service.indexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.List;
import java.util.jar.Manifest;

public interface IndexableResource {
	
	static String NAME = "name";
	static String LOCATION = "location";
	static String SIZE = "size";
	static String LAST_MODIFIED = "lastmodified";
	
	Builder newBuilder();
	
	String getLocation();
	
	Dictionary<String, Object> getProperties();
	
	long getSize();
	
	InputStream getStream() throws IOException;
	
	Manifest getManifest() throws IOException;
	
	List<String> listChildren(String prefix) throws IOException;
	
	IndexableResource getChild(String path) throws IOException;
	
	void close();
}
