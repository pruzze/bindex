package org.osgi.service.indexer.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.IndexWriterFactory;
import org.osgi.service.log.LogService;

public class IndexWriterFactoryImpl implements IndexWriterFactory {

	private LogService log;

	public IndexWriterFactoryImpl() {
		this(new ConsoleLogSvc());;
	}
	
	public IndexWriterFactoryImpl(LogService log) {
		this.log = log;
	}

	public IndexWriter newIndexWriter(OutputStream out, Map<String, String> config) throws IOException {
		return new DefaultIndexWriter(out, config);
	}
	
	public IndexWriter newFragmentIndexWriter(Writer out) {
		return new DefaultFragmentWriter(out);
	}

	public IndexWriter newConcurrentIndexWriter(IndexWriter target, ExecutorService executor) throws IOException {
		return new ConcurrentIndexWriter(target, executor, log);
	}
}
