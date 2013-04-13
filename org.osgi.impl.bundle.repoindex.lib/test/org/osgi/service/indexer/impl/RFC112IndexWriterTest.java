package org.osgi.service.indexer.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.mockito.Mockito;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.log.LogService;

import junit.framework.TestCase;

public class RFC112IndexWriterTest extends TestCase {

	public void testFragmentBsnVersion() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-01.txt", "testdata/01-bsn+version.jar");
	}
	
	public void testFragmentLocalization() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-02.txt", "testdata/02-localization.jar");
	}
	
	public void testFragmentExport() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-03.txt", "testdata/03-export.jar");
	}
	
	public void testFragmentExportWithUses() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-04.txt", "testdata/04-export+uses.jar");
	}
	
	public void testFragmentImport() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-05.txt", "testdata/05-import.jar");
	}
	
	public void testFragmentRequireBundle() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-06.txt", "testdata/06-requirebundle.jar");
	}
	
	public void testFragmentOptionalImport() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-07.txt", "testdata/07-optionalimport.jar");
	}
	
	public void testFragmentFragmentHost() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-08.txt", "testdata/08-fragmenthost.jar");
	}
	
	public void testFragmentSingletonBundle() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-09.txt", "testdata/09-singleton.jar");
	}	
	
	public void testFragmentExportService() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-10.txt", "testdata/10-exportservice.jar");
	}
	
	public void testFragmentImportService() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-11.txt", "testdata/11-importservice.jar");
	}
	
	public void testFragmentForbidFragments() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-12.txt", "testdata/12-nofragments.jar");
	}
	
	public void testFragmentBREE() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-13.txt", "testdata/13-bree.jar");
	}
	
	// Generic Requirements / Capabilites are not supported
	public void testFragmentProvideRequireCap() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-14.txt", "testdata/14-provide-require-cap.jar");
	}
	
	// osgi.extender namespace is not supported
	public void testFragmentRequireSCR() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-15.txt", "testdata/15-scr.jar");
	}
	
	public void testFragmentOptionalRequireBundle() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-16.txt", "testdata/16-optionalrequirebundle.jar");
	}
	
	// osgi.native namespace is not supported
	public void testFragmentBundleNativeCode() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-19.txt", "testdata/19-bundlenativecode.jar");
	}
	
	public void testFragmentPlainJar() throws Exception {
		LogService mockLog = Mockito.mock(LogService.class);
		RepoIndex indexer = new RepoIndex(mockLog);
		indexer.addAnalyzer(new KnownBundleAnalyzer(), FrameworkUtil.createFilter("(name=*)"));
		
		assertFragmentMatch(indexer, "testdata/rfc112/fragment-plainjar.txt", "testdata/jcip-annotations.jar");
		Mockito.verifyZeroInteractions(mockLog);
	}
	
	public void testFragmentPlainJarWithVersion() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-plainjar-versioned.txt", "testdata/jcip-annotations-2.5.6.wibble.jar");
	}
	
	public void testFragmenExtendedIdentity() throws Exception {
		assertFragmentMatch("testdata/rfc112/fragment-21.txt", "testdata/21-extinfo.jar");
	}
	
	private static String writeFragment(RepoIndex indexer, Set<File> files, Map<String, String> config) throws Exception {
		StringWriter sw = new StringWriter();
		IndexWriter iw = new RFC112FragmentWriter(sw);
		indexer.index(files, iw, config);
		iw.close();
		return sw.toString().trim();
	}

	private static void assertFragmentMatch(String expectedPath, String jarPath) throws Exception {
		RepoIndex indexer = new RepoIndex();
		assertFragmentMatch(indexer, expectedPath, jarPath);
	}

	private static void assertFragmentMatch(RepoIndex indexer, String expectedPath, String jarPath) throws Exception {
		String actual = writeFragment(indexer, Collections.singleton(new File(jarPath)), null);
		String expected = Utils.readStream(new FileInputStream(expectedPath));
		assertEquals(expected, actual);
	}

	public void testEmptyIndex() throws Exception {
		RepoIndex indexer = new RepoIndex();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Set<File> files = Collections.emptySet();
	
		Map<String, String> config = new HashMap<String, String>();
		config.put(IndexWriter.REPOSITORY_INCREMENT_OVERRIDE, "0");
		config.put(IndexWriter.REPOSITORY_NAME, "empty");
		config.put(IndexWriter.PRETTY, "true");
		IndexWriter iw = new RFC112IndexWriter(out, config);
		indexer.index(files, iw, config);
		iw.close();
		
		String expected = Utils.readStream(new FileInputStream("testdata/rfc112/empty.txt"));
		assertEquals(expected, out.toString());
	}

	public void testFullIndex() throws Exception {
		RepoIndex indexer = new RepoIndex();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Set<File> files = new LinkedHashSet<File>();
		files.add(new File("testdata/03-export.jar"));
		files.add(new File("testdata/06-requirebundle.jar"));
		
		Map<String, String> config = new HashMap<String, String>();
		config.put(IndexWriter.REPOSITORY_INCREMENT_OVERRIDE, "0");
		config.put(IndexWriter.REPOSITORY_NAME, "full-c+f");
		IndexWriter iw = new RFC112IndexWriter(out, config);
		indexer.index(files, iw, config);
		iw.close();
		
		String unpackedXML = Utils.readStream(new FileInputStream("testdata/rfc112/unpacked.xml"));
		String expected = unpackedXML.replaceAll("[\\n\\t]*", "");
		assertEquals(expected, Utils.decompress(out.toByteArray()));
	}

	public void testFullIndexPrettyPrint() throws Exception {
		RepoIndex indexer = new RepoIndex();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Set<File> files = new LinkedHashSet<File>();
		files.add(new File("testdata/03-export.jar"));
		files.add(new File("testdata/06-requirebundle.jar"));
		
		Map<String, String> config = new HashMap<String, String>();
		config.put(IndexWriter.REPOSITORY_INCREMENT_OVERRIDE, "0");
		config.put(IndexWriter.REPOSITORY_NAME, "full-c+f");
		config.put(IndexWriter.PRETTY, "true");
		IndexWriter iw = new RFC112IndexWriter(out, config);
		indexer.index(files, iw, config);
		iw.close();
		
		String expected = Utils.readStream(new FileInputStream("testdata/rfc112/full-03+06.txt"));
		assertEquals(expected, out.toString());
	}
}
