package org.osgi.service.indexer.osgi;

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.indexer.IndexWriterFactory;
import org.osgi.service.indexer.ResourceIndexer;
import org.osgi.service.indexer.impl.IndexWriterFactoryImpl;
import org.osgi.service.indexer.impl.RepoIndex;

public class Activator implements BundleActivator {

	private LogTracker logTracker;
	private AnalyzerTracker analyzerTracker;

	private List<ServiceRegistration> registrations = new LinkedList<ServiceRegistration>();

	public void start(BundleContext context) throws Exception {
		logTracker = new LogTracker(context);
		logTracker.open();

		RepoIndex indexer = new RepoIndex(logTracker);

		analyzerTracker = new AnalyzerTracker(context, indexer, logTracker);
		analyzerTracker.open();

		registrations.add(context.registerService(ResourceIndexer.class.getName(), indexer, null));

		IndexWriterFactory writerFactory = new IndexWriterFactoryImpl(logTracker);
		registrations.add(context.registerService(IndexWriterFactory.class.getName(), writerFactory, null));
	}

	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration registration : registrations)
			registration.unregister();
		analyzerTracker.close();
		logTracker.close();
	}

}
