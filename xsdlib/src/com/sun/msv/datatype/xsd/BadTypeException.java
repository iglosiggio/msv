/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.datatype;

/**
 * Exception
 *
 * This exception is thrown when there is an error
 * regarding type definition (mainly derivation).
 */
public class BadTypeException extends Exception
{
	public BadTypeException(String resourcePropertyName,Object[] args)
	{
		super( java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.datatype.Messages").getString(resourcePropertyName),
			args ) );
	}

	public BadTypeException(String resourcePropertyName,Object arg1,Object arg2)
	{
		this( resourcePropertyName, new Object[]{arg1,arg2} );
	}
	
	public BadTypeException(String resourcePropertyName,Object arg1)
	{
		this( resourcePropertyName, new Object[]{arg1} );
	}

	public BadTypeException(String resourcePropertyName)
	{
		this( resourcePropertyName, null );
	}
	
	public static final String ERR_INVALID_ITEMTYPE = null;
	// Type "{0}" must be an atom type in order to be used as an item type
	// of list.
	
	public static final String ERR_INVALID_WHITESPACE_VALUE =
		"WhiteSpaceProcessor.InvalidWhiteSpaceValue";
	public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
	
	public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE =
		"EnumerationFacet.InvalidValueForThisType";
	public final static String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER
		= null; // facet "{0}" must be a non-negative integer value
	public final static String ERR_FACET_MUST_BE_POSITIVE_INTEGER
		= null; // facet "{0}" must be a positive integer value
	public final static String ERR_OVERRIDING_FIXED_FACET
		= null;	// facet "{0}" is specified as fixed in base type.
	public final static String ERR_INCONSISTENT_MINMAX_LENGTH	= "LengthFacet.InconsistentMinMaxLength";
	public final static String ERR_LENGTH_AND_X_IS_EXCLUSIVE	= "LengthFacet.LengthAndXIsExclusive";
	public final static String ERR_SCALE_IS_GREATER_THAN_PRECISION =
		"PrecisionScaleFacet.ScaleIsGraterThanPrecision";
	public final static String ERR_EXCLUSIVE_FACETS_SPECIFIED =
		"RangeFacet.ExclusiveFacetsSpecified";
	public final static String ERR_ILLEGAL_MAX_MIN_ORDER =
		"RangeFacet.IllegalMaxMinOrder";
	public final static String ERR_INAPPROPRIATE_VALUE_FOR_X =
		"RangeFacet.IllegalValueForX";
	public static final String ERR_DUPLICATE_FACET
		= null; // facet {0} is specified more than once.
}
