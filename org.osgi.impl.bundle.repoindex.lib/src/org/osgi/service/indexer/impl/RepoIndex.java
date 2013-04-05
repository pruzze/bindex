package org.osgi.service.indexer.impl;

import static org.osgi.framework.FrameworkUtil.createFilter;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.indexer.Capability;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.Requirement;
import org.osgi.service.indexer.Resource;
import org.osgi.service.indexer.ResourceAnalyzer;
import org.osgi.service.indexer.ResourceIndexer;
import org.osgi.service.indexer.impl.util.AddOnlyList;
import org.osgi.service.indexer.impl.util.Pair;
import org.osgi.service.log.LogService;

public class RepoIndex implements ResourceIndexer {
	
	private final BundleAnalyzer bundleAnalyzer;
	private final OSGiFrameworkAnalyzer frameworkAnalyzer;
	private final SCRAnalyzer scrAnalyzer;
	private final BlueprintAnalyzer blueprintAnalyzer;
	
	private final LogService log;
	
	private final List<Pair<ResourceAnalyzer, Filter>> analyzers = new LinkedList<Pair<ResourceAnalyzer,Filter>>();
	
	public RepoIndex() {
		this(new ConsoleLogSvc());
	}
	
	public RepoIndex(LogService log) {
		this.log = log;

		this.bundleAnalyzer = new BundleAnalyzer(log);
		this.frameworkAnalyzer = new OSGiFrameworkAnalyzer(log);
		this.scrAnalyzer = new SCRAnalyzer(log);
		this.blueprintAnalyzer = new BlueprintAnalyzer(log);
		
		try {
			Filter allFilter = createFilter("(name=*.jar)");
			
			addAnalyzer(bundleAnalyzer, allFilter);
			addAnalyzer(frameworkAnalyzer, allFilter);
			addAnalyzer(scrAnalyzer, allFilter);
			addAnalyzer(blueprintAnalyzer, allFilter);

		} catch (InvalidSyntaxException e) {
			// Can't happen...?
			throw new RuntimeException("Unexpected internal error compiling filter");
		}
	}
	
	public final void addAnalyzer(ResourceAnalyzer analyzer, Filter filter) {
		synchronized (analyzers) {
			analyzers.add(Pair.create(analyzer, filter));
		}
	}
	
	public final void removeAnalyzer(ResourceAnalyzer analyzer, Filter filter) {
		synchronized (analyzers) {
			analyzers.remove(Pair.create(analyzer, filter));
		}
	}

	public void index(Set<File> files, IndexWriter iw, Map<String, String> config) throws Exception {
		if (config == null)
			config = new HashMap<String, String>(0);
		
		for (File file : files) {
			Resource resource = generateResource(file, config);
			iw.write(resource);
		}
	}
	
	private Resource generateResource(File file, Map<String, String> config) throws Exception {
		
		JarResource resource = new JarResource(file);
		List<Capability> caps = new AddOnlyList<Capability>(new LinkedList<Capability>());
		List<Requirement> reqs = new AddOnlyList<Requirement>(new LinkedList<Requirement>());
		
		// Read config settings and save in thread local state
		if (config != null) {
			URL rootURL;
			String rootURLStr = config.get(ResourceIndexer.ROOT_URL);
			if (rootURLStr != null) {
				File rootDir = new File(rootURLStr);
				if (rootDir.isDirectory())
					rootURL = rootDir.toURI().toURL();
				else
					rootURL = new URL(rootURLStr);
			}
			else
				rootURL = new File(System.getProperty("user.dir")).toURI().toURL();
			
			String urlTemplate = config.get(ResourceIndexer.URL_TEMPLATE);
			bundleAnalyzer.setStateLocal(new GeneratorState(rootURL, urlTemplate));
		} else {
			bundleAnalyzer.setStateLocal(null);
		}
		
		// Iterate over the analyzers
		try {
			synchronized (analyzers) {
				for (Pair<ResourceAnalyzer, Filter> entry : analyzers) {
					ResourceAnalyzer analyzer = entry.getFirst();
					Filter filter = entry.getSecond();
					
					if (filter == null || filter.match(resource.getProperties())) {
						try {
							analyzer.analyzeResource(resource, caps, reqs);
						} catch (Exception e) {
							log(LogService.LOG_ERROR, MessageFormat.format("Error calling analyzer \"{0}\" on resource {1}.", analyzer.getClass().getName(), resource.getLocation()), e);
						}
					}
				}
			}
		} finally {
			bundleAnalyzer.setStateLocal(null);
		}
		
		return new Resource(reqs, caps);
	}

	private void log(int level, String message, Throwable t) {
		if (log != null)
			log.log(level, message, t);
	}
	
}
