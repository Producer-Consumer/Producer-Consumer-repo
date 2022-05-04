package org.consumeexpose.endpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocumentationServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException  {
		
		File documentation = new File(DocumentationBuilder.documentPath);
		
		FileInputStream inputStream = new FileInputStream(documentation);
		byte[] content = inputStream.readAllBytes();
		inputStream.close();
		response.setContentType("text/html");
		response.setStatus(200);
		response.getOutputStream().write(content);
		
	}

}
