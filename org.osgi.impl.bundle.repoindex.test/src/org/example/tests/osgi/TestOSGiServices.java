package org.example.tests.osgi;

import static org.example.tests.utils.Utils.copyToTempFile;
import static org.example.tests.utils.Utils.createTempDir;
import static org.example.tests.utils.Utils.deleteWithException;
import static org.example.tests.utils.Utils.readStream;
import static org.example.tests.utils.Utils.writeFragment;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.example.tests.utils.WibbleAnalyzer;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.indexer.IndexWriterFactory;
import org.osgi.service.indexer.IndexableResource;
import org.osgi.service.indexer.ResourceAnalyzer;
import org.osgi.service.indexer.ResourceIndexer;
import org.osgi.service.log.LogService;

public class TestOSGiServices extends TestCase {

	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	private File tempDir;

	@Override
	protected void setUp() throws Exception {
		tempDir = createTempDir();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteWithException(tempDir);
	}

	public void testBasicServiceInvocation() throws Exception {
		ServiceReference indexerRef = context.getServiceReference(ResourceIndexer.class.getName());
		ResourceIndexer indexer = (ResourceIndexer) context.getService(indexerRef);

		ServiceReference writerFactRef = context.getServiceReference(IndexWriterFactory.class.getName());
		IndexWriterFactory iwf = (IndexWriterFactory) context.getService(writerFactRef);

		Map<String, String> config = new HashMap<String, String>();
		config.put(ResourceIndexer.ROOT_URL, tempDir.getAbsoluteFile().toURL().toString());

		String actual = writeFragment(indexer, iwf, Collections.singleton(copyToTempFile(tempDir, "testdata/01-bsn+version.jar")), config);
		String expected = readStream(TestOSGiServices.class.getResourceAsStream("/testdata/fragment-basic.txt"));

		assertEquals(actual, expected);

		context.ungetService(indexerRef);
		context.ungetService(writerFactRef);
	}

	// Test whiteboard registration of Resource Analyzers.
	public void testWhiteboardAnalyzer() throws Exception {
		ServiceRegistration reg = context.registerService(ResourceAnalyzer.class.getName(), new WibbleAnalyzer(), null);

		ServiceReference indexeRef = context.getServiceReference(ResourceIndexer.class.getName());
		ResourceIndexer indexer = (ResourceIndexer) context.getService(indexeRef);

		ServiceReference writerFactRef = context.getServiceReference(IndexWriterFactory.class.getName());
		IndexWriterFactory iwf = (IndexWriterFactory) context.getService(writerFactRef);

		Map<String, String> config = new HashMap<String, String>();
		config.put(ResourceIndexer.ROOT_URL, tempDir.getAbsoluteFile().toURL().toString());

		String actual = writeFragment(indexer, iwf, Collections.singleton(copyToTempFile(tempDir, "testdata/01-bsn+version.jar")), config);
		String expected = readStream(TestOSGiServices.class.getResourceAsStream("/testdata/fragment-wibble.txt"));

		assertEquals(actual, expected);

		context.ungetService(indexeRef);
		context.ungetService(writerFactRef);
		reg.unregister();
	}

	// Test whiteboard registration of Resource Analyzers, with resource filter
	// property.
	public void testWhiteboardAnalyzerWithFilter() throws Exception {
		Dictionary<String, Object> analyzerProps = new Hashtable<String, Object>();
		analyzerProps.put(ResourceAnalyzer.FILTER, "(location=*sion.jar)");
		ServiceRegistration reg = context.registerService(ResourceAnalyzer.class.getName(), new WibbleAnalyzer(), analyzerProps);

		ServiceReference indexerRef = context.getServiceReference(ResourceIndexer.class.getName());
		ResourceIndexer indexer = (ResourceIndexer) context.getService(indexerRef);

		ServiceReference writerFactRef = context.getServiceReference(IndexWriterFactory.class.getName());
		IndexWriterFactory iwf = (IndexWriterFactory) context.getService(writerFactRef);

		Set<File> files = new LinkedHashSet<File>();
		files.add(copyToTempFile(tempDir, "testdata/01-bsn+version.jar"));
		files.add(copyToTempFile(tempDir, "testdata/02-localization.jar"));

		Map<String, String> config = new HashMap<String, String>();
		config.put(ResourceIndexer.ROOT_URL, tempDir.getAbsoluteFile().toURL().toString());

		String actual = writeFragment(indexer, iwf, files, config);
		String expected = readStream(TestOSGiServices.class.getResourceAsStream("/testdata/fragment-wibble-filtered.txt"));

		assertEquals(actual, expected);

		context.ungetService(indexerRef);
		context.ungetService(writerFactRef);
		reg.unregister();
	}

	// Test that exceptions thrown by analyzers are forwarded to the OSGi Log
	// Service
	public void testLogNotification() throws Exception {
		// Register mock LogService, to receive notifications
		LogService mockLog = mock(LogService.class);
		ServiceRegistration mockLogReg = context.registerService(LogService.class.getName(), mockLog, null);

		// Register a broken analyzer that throws exceptions
		ResourceAnalyzer brokenAnalyzer = new ResourceAnalyzer() {
			public void analyzeResource(IndexableResource resource, List<Capability> capabilities, List<Requirement> requirements) throws Exception {
				throw new Exception("Bang!");
			}
		};
		ServiceRegistration mockAnalyzerReg = context.registerService(ResourceAnalyzer.class.getName(), brokenAnalyzer, null);

		// Call the indexer
		ServiceReference indexerRef = context.getServiceReference(ResourceIndexer.class.getName());
		ResourceIndexer indexer = (ResourceIndexer) context.getService(indexerRef);

		ServiceReference writerFactRef = context.getServiceReference(IndexWriterFactory.class.getName());
		IndexWriterFactory iwf = (IndexWriterFactory) context.getService(writerFactRef);

		Set<File> files = Collections.singleton(copyToTempFile(tempDir, "testdata/01-bsn+version.jar"));
		Map<String, String> config = new HashMap<String, String>();
		config.put(ResourceIndexer.ROOT_URL, tempDir.getAbsoluteFile().toURL().toString());
		writeFragment(indexer, iwf, files, config);

		// Verify log output
		ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
		verify(mockLog).log(any(ServiceReference.class), eq(LogService.LOG_ERROR), anyString(), exceptionCaptor.capture());
		assertEquals("Bang!", exceptionCaptor.getValue().getMessage());

		context.ungetService(indexerRef);
		context.ungetService(writerFactRef);
		mockAnalyzerReg.unregister();
		mockLogReg.unregister();
	}

}
