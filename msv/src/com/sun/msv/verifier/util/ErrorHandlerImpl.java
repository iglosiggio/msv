/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * default implementation of ErrorHandler.
 * 
 * If an error is found, throw it to stop further validation.
 * warnings are ignored.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ErrorHandlerImpl implements ErrorHandler
{
	public void fatalError( SAXParseException error ) throws SAXParseException {
		throw error; }
	public void error( SAXParseException error ) throws SAXParseException {
		throw error; }
	public void warning( SAXParseException warning ) {}
}
