package com.tnt.gestionproj.common.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


import flex.messaging.MessageException;
/**
 * @author abderrahmen
 *
 */
public class FlexExceptionTranslator //implements ExceptionTranslator 
{

	public boolean handles(Class<?> clazz) {  
		 return true;   
		 
	}   
		public MessageException translate(Throwable t) { 
			//t.get
		Exception exc =   (Exception) t;
		exc.printStackTrace();
		 MessageException ex = new MessageException();    
		 ex.setCode("Application.Service");   
		  ex.setMessage(exc.getMessage());   
		  ex.setRootCause(t);   
		  return ex;   } 

		private  String getStackTraceString(Throwable aThrowable) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
		  }
}

