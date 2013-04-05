/*
 * Copyright (c) OSGi Alliance (2002, 2006, 2007). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.indexer;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * ResourceIndexer is an OSGi service that creates a Repository XML
 * representation by indexing resource capabilities and requirements.
 */
public interface ResourceIndexer {

	/**
	 * Template for the URLs in the OBR XML representation. It can contain the
	 * following symbols:
	 * <ul>
	 * <li>%s is the symbolic name</li>
	 * <li>%v is the version number</li>
	 * <li>%f is the filename</li>
	 * <li>%p is the directory path</li>
	 * </ul>
	 */
	public static final String URL_TEMPLATE = "url.template";

	/** the root (directory) URL of the OBR */
	public static final String ROOT_URL = "root.url";

	/** the license URL of the OBR XML representation */
	public static final String LICENSE_URL = "license.url";

	public static final String VERBOSE = "verbose";


	/**
	 * Index a set of input files and write the Repository XML representation to
	 * the given writer
	 * 
	 * @param files
	 *            a set of input files
	 * @param iw
	 *            the IndexWriter to write the OBR XML representation
	 * @param config
	 *            a set of optional parameters (use the interface constants as
	 *            keys)
	 * @throws Exception
	 */
	void index(Set<File> files, IndexWriter iw, Map<String, String> config) throws Exception;
}
