/* Copyright 2009 University of Cambridge
 * Licensed under the Educational Community License (ECL), Version 2.0. You may not use this file except in 
 * compliance with this License.
 *
 * You may obtain a copy of the ECL 2.0 License at https://source.collectionspace.org/collection-space/LICENSE.txt
 */
package org.collectionspace.chain.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Hides the answers to configuration questions from the source of those answers. The interface to this will
 * change soon, as we can't go on supporting an additional method for each question.
 */
public class Config {
	// XXX refactor to give saner names
	private static final String CHAIN_PROPERTIES="chain.properties";
	private static final String STORE_PROPERTY="cspace.chain.store.dir";
	private static final String SCHEMA_PROPERTY="cspace.chain.schema.dir";
	private static final String STORAGE_PROPERTY="cspace.chain.store";
	private static final String STORE_URL="cspace.chain.store.url";
	
	private ServletContext ctx;
	private Properties props;
	private static final Logger log=LoggerFactory.getLogger(Config.class);

	public Config(ServletContext ctx) throws IOException {
		// Load properties file, if present
		log.info("Config loading");
		InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(CHAIN_PROPERTIES);
		this.ctx=ctx;
		props=new Properties();
		if(is==null) {
			log.warn("Warning: no configuration found");
			return;  // Missing is as blank
		}
		props.load(is);
		is.close();
	}

	private synchronized String testStore(String suffix) {
		try {
			String dir=(String)ctx.getAttribute("test-store");
			if(dir==null)
				return null;
			File base=new File(dir,suffix);
			if(!base.exists())
				base.mkdir();
			log.info("Using testing path "+base);
			return base.getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	private String getPath(String test_path,String prop_path,String name) {
		// Check if defined test store
		String out=testStore(test_path);
		if(out!=null)
			return out;
		// Check in properties file
		out=props.getProperty(prop_path);
		if(out!=null)
			return out;
		// Check for system property
		out=System.getProperty(prop_path);
		if(out!=null)
			return out;
		// Use temporary directory
		out=System.getProperty("java.io.tmpdir");
		log.warn("Warning: Defaulting to tmpdir for "+name);
		return out;
	}
	
	/** Get path to the store for data. We first look for the system property cspace.chain.store.dir. If that's 
	 * missing, we look for a properties file called chain.properties on the classpath and look in there. 
	 * Failing that, we just use the defined java temporary directory.
	 * 
	 * @return The path as a string. Should be a file.
	 */
	public String getPathToStore() {
		return getPath("store",STORE_PROPERTY,"store");
	}

	public String getPathToSchemaDocs() {
		return getPath("schema",SCHEMA_PROPERTY,"schema");
	}
	
	// XXX refactor
	public String getStorageType() {
		String out=(String)ctx.getAttribute("storage");
		if(out!=null)
			return out;
		out=props.getProperty(STORAGE_PROPERTY);
		if(out!=null)
			return out;
		return "file";
	}
	
	public String getServicesBaseURL() {
		String out=(String)ctx.getAttribute("store-url");
		if(out!=null)
			return out;
		out=props.getProperty(STORE_URL);
		if(out!=null)
			return out;
		return "file";		
	}
}
