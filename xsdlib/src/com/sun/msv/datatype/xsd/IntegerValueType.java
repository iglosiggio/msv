package com.sun.tranquilo.datatype;

/**
 * Value object of "integer" type
 */
public class IntegerValueType implements Comparable
{
	/** canonical representation of absolute value of integer
	 * 
	 * BNF of canonical representation
	 * 
	 * canonical_integer	:=  "0" | nonzero
	 * nonzero				:= '-'? ["1"-"9"] ["0"-"9"]*
	 */
	private final String value;
	
	/**
	 * translates non-canonicalized representation of an integer into
	 * an IntegerValueType.
	 * 
	 * @exception	ConvertionException
	 *		when the parameter is not even valid with respect to
	 *		the lexical space of "integer" type specified in
	 *		XML Schema datatype spec.
	 */
	public IntegerValueType( String nonCanonicalizedValue )
		throws ConvertionException
	{
		int idx=0; String v="";
		final int len = nonCanonicalizedValue.length();
		
		if(len==0)	throw new ConvertionException();
		switch(nonCanonicalizedValue.charAt(idx))
		{
		case '+':
			idx++;
			break;	// ignore the sign
		case '-':
			v += "-";
			idx++;
			break;
		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
			break;
		default:
			throw new ConvertionException();
		}
		
		// skip leading '0'
		while(idx<len && nonCanonicalizedValue.charAt(idx)=='0')
			idx++;
		
		if( idx==len )
		{// all the digits are skipped : that means this value is 0
			value = "0";
			return;
		}
		
		// adding digits
		while( idx<len )
		{
			final char ch = nonCanonicalizedValue.charAt(idx++);
			if( '0'<=ch && ch<='9' )	v += ch;
			else						throw new ConvertionException();	// illegal char
		}
		
		value = v;
	}
	
	/**
	 * @return		1	if this value is bigger than rhs
	 *				0	if the values are the same
	 *				-1	if rhs is bigger than this.
	 */
	public int compareTo( Object o )
	{
		IntegerValueType rhs = (IntegerValueType)o;

		boolean lhsIsNegative = value.charAt(0)=='-';
		boolean rhsIsNegative = rhs.value.charAt(0)=='-';
		
		if( lhsIsNegative && !rhsIsNegative )	return -1;
		if( rhsIsNegative && !lhsIsNegative )	return 1;
		
		// now both number have the same sign.
		
		int lp,rp,llen,rlen;
		
		if( lhsIsNegative && rhsIsNegative )
			lp=rp=1;
		else
			lp=rp=0;
		
		llen = value.length()-lp;
		rlen = rhs.value.length()-rp;
		
		if( llen > rlen )		return lhsIsNegative?-1:1;
		if( llen < rlen )		return lhsIsNegative?1:-1;
		
		// now we have the same length. compare left to right
		while( llen>0 )
		{
			final char lch = value.charAt(lp++);
			final char rch = value.charAt(rp++);
			
			if( lch > rch )		return lhsIsNegative?-1:1;
			if( lch < rch )		return lhsIsNegative?1:-1;
			
			llen--;
		}
		
		return 0;	// they are the same value
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof IntegerValueType )
			return value.equals( ((IntegerValueType)o).value );
		else
			return false;
	}
	
	public int hashCode() { return value.hashCode(); }
	
	public int precision()
	{
		// TODO : what is the exact definition of "precision"?
		// What is the precision of "100"? 1, or 3?
		final int len = value.length();
		if( value.charAt(0)=='-' )	return len-1;
		else						return len;
	}
	
	
	/** returns true if the value if non-positive (less than or equal to zero) */
	public boolean isNonPositive()
	{
		final char ch = value.charAt(0);
		if( ch=='-' || ch=='0' )	return true;
		return false;
	}
    
	/** returns true if the value if positive (greater than zero) */
	public boolean isPositive()
	{
		final char ch = value.charAt(0);
		if( ch=='-' || ch=='0' )	return false;
		return true;
	}
    
	/** returns true if the value if negative (less than zero) */
	public boolean isNegative()
	{
		return value.charAt(0)=='-';
	}
    
	/** returns true if the value if non-negative (greater than or equal to zero) */
	public boolean isNonNegative()
	{
		return value.charAt(0)!='-';
	}
}
