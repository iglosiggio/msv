/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.generator;

import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.compiler.Controller;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.reader.NameUtil;
import com.sun.tahiti.util.text.Formatter;
import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

/**
 * produces Java source codes of the object model.
 */
class JavaGenerator
{
	JavaGenerator( AnnotatedGrammar grammar, Symbolizer symbolizer, Controller controller ) {
		this.grammar = grammar;
		this.symbolizer = symbolizer;
		this.controller = controller;
		this.grammarClassName = grammar.grammarName;
		
		int idx = grammarClassName.lastIndexOf('.');
		if(idx<0)	grammarShortClassName = grammarClassName;
		else		grammarShortClassName = grammarClassName.substring(idx+1);
	}
	
	private final AnnotatedGrammar grammar;
	private final Symbolizer symbolizer;
	private final Controller controller;
	private final String grammarClassName;
	private final String grammarShortClassName;
		
	void generate() throws IOException {
		// collect all ClassItems.
		
		ClassItem[] types = grammar.getClasses();
		for( int i=0; i<types.length; i++ )
			writeClass( types[i], new PrintWriter(controller.getOutput(types[i])) );
		
		InterfaceItem[] itfs = grammar.getInterfaces();
		for( int i=0; i<itfs.length; i++ )
			writeClass( itfs[i], new PrintWriter(controller.getOutput(itfs[i])) );
	}
	
	
	private static String format( String fmt, Object[] args ) {
		return MessageFormat.format(fmt,args);
	}
	private static String format( String fmt, Object arg1 ) {
		return MessageFormat.format(fmt,new Object[]{arg1});
	}
	private static String format( String fmt, Object arg1, Object arg2 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2});
	}
	private static String format( String fmt, Object arg1, Object arg2, Object arg3 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2,arg3});
	}
	
	/**
	 * computes the type name that will can be placed into the source code.
	 * 
	 * If the given type obejct is in the current package, then only the local type
	 * name is returned. Otherwise fully qualified name is returned.
	 */
	private String toPrintName( Type type ) {
		if( packageName==type.getPackageName()
		|| 	(packageName!=null && packageName.equals( type.getPackageName() ))
		||  "java.lang".equals( type.getPackageName() ) )
			return type.getBareName();
		else
			return type.getTypeName();
	}
	
	/**
	 * the name of the package into which this class is placed.
	 * This field is set to null to indicate the root package.
	 */
	private String packageName;
	
	/**
	 * writes the body of a ClassItem.
	 */
	private void writeClass( TypeItem type, PrintWriter out ) {

		// one 
		ClassItem citm = null;
		InterfaceItem iitm = null;
		
		if( type instanceof ClassItem )	citm = (ClassItem)type;
		else							iitm = (InterfaceItem)type;
	
		
		packageName = type.getPackageName();
		if( packageName!=null )
			out.println(format("package {0};\n",packageName));
		
		out.println("import com.sun.tahiti.runtime.ll.NamedSymbol;");
		out.println(format("import {0};",grammarClassName));
		out.println();
		
		out.print(format("public {0} {1}",
			citm!=null?"class":"interface",	type.getBareName() ));
		
		if( citm!=null && citm.getSuperType()!=null )
			out.print(format(" extends {0}",
				toPrintName( type.getSuperType() ) ));
		
		Type[] itfs = type.getInterfaces();
		if(itfs.length!=0) {
			out.print(format(" {0} {1}",
				citm!=null?"implements":"extends",
				toPrintName(itfs[0])));
			
			for( int i=1;i<itfs.length;i++ )
				out.print(format(", {0}",toPrintName(itfs[i])));
		}
		
		out.println(" {");
		out.println();
		
	// generate fields
	//----------------------------------------
		Iterator itr = type.fields.keySet().iterator();
		while( itr.hasNext() ) {
			String fieldName = (String)itr.next();
			FieldUse fu = (FieldUse)type.fields.get(fieldName);
			
			Container cont = getContainer(fu);
			
			
/*		// simple version: public field
			out.println(format("\tpublic {0} {1} = {2};\n",
				new Object[]{
					cont.getTypeStr(),
					fu.name,
					cont.getInitializer() }));
*/			
		// complex version: protected field with accessor method
			out.println(Formatter.format(
				"\n"+
				"//\n"+
				"// <%2>\n"+
				"//\n"+
				"	protected <%1> <%2> = <%3>;\n"+
				"	public void set<%0>( <%1> newVal ) {\n"+
				"		this.<%2> = newVal;\n"+
				"	}\n"+
				"	\n"+
				"	public <%1> get<%0>() {\n"+
				"		return <%2>;\n"+
				"	}\n",
				new Object[]{
					NameUtil.xmlNameToJavaName( "class", fu.name ),
					cont.getTypeStr(),
					fu.name,
					cont.getInitializer(),
				}));
		}
		
	// generate the setField method
	//------------------------------------------
		if( citm!=null ) {
			out.println("\n\n");
			out.println(
				"\t/**\n"+
				"\t * unmarshalling handler.\n"+
				"\t * This method is called to unmarshall objects from XML.\n"+
				"\t */");
			out.println("\tpublic void setField( NamedSymbol name, Object item ) throws Exception {");
			itr = type.fields.keySet().iterator();
			while( itr.hasNext() ) {
				String fieldName = (String)itr.next();
				FieldUse fu = (FieldUse)type.fields.get(fieldName);

				Container cont = getContainer(fu);
				
				out.print("\t\tif( ");
				FieldItem[] fi = fu.getItems();
				for( int i=0; i<fi.length; i++ ) {
					if(i!=0)
						out.print(" || ");
					out.print(format("name=={0}.{1}",
						grammarShortClassName,
						symbolizer.getId(fi[i])));
				}
				out.println(" ) {");
				out.println("\t\t\t"+cont.setField(fu.name,"item"));
				out.println("\t\t\treturn;");
				out.println("\t\t}");
			}
			if( type.getSuperType()!=null )
				out.println("\t\tsuper.setField(name,item);");
			else
				out.println("\t\tthrow new Error();//assertion failed.this is not possible");
			out.println("\t}");
		}
		
		out.println("}");
		out.flush();
		out.close();
	}
		
	private interface Container {
		String getTypeStr();
		String getInitializer();
		String setField( String fieldName, String objName );
	}
	
	private Container getContainer( final FieldUse fu ) {
		if( fu.multiplicity.isAtMostOnce() )
			// use item type itself.
			return new Container(){
				public String getTypeStr() {
					return toPrintName(fu.type);
				}
				public String getInitializer() {
					return "null";
				}
				public String setField( String fieldName, String objName ) {
					return format("this.{0}=({1}){2};",fieldName,getTypeStr(),objName);
				}
			};
/*
		if( fu.multiplicity.max!=null ) {
			if( fu.multiplicity.max.intValue()==fu.multiplicity.min )
				// use array.
				return new Container(){
					public String getTypeStr() {
						return fu.type.getTypeName()+"[]";
					}
					public String getInitializer() {
						return format("new {0}[{1}]",
							fu.type.getTypeName(),
							new Integer(fu.multiplicity.min));
					}
				};
		}
*/		
		// otherwise use Vector
		return new Container(){
			public String getTypeStr() {
				return "java.util.Vector";
			}
			public String getInitializer() {
				return "new java.util.Vector()";
			}
			public String setField( String fieldName, String objName ) {
				return format("this.{0}.add({1});",fieldName,objName);
			}
		};
	}
}
