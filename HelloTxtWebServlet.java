package com.txtweb.examples.hellotxtweb;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HellotxtWebServlet extends HttpServlet {

	private static final long serialVersionUID = -6194111034650922L;
	private static final Logger logger = Logger.getLogger(HellotxtWebServlet.class.getName());

	private static final String APPKEY_NAME = "txtweb-appkey";
	private static final String APPKEY_CONTENT = "ad8b852a-9dcf-4ef7-96ed-52a24be70355";

	private static final String HTTP_PARAM_TXTWEB_MESSAGE = "txtweb-message";
	private static final String HTTP_PARAM_TXTWEB_PROTOCOL = "txtweb-protocol";
	private static final String HTTP_PARAM_TXTWEB_MOBILE = "txtweb-mobile";
	private static final String HTTP_PARAM_TXTWEB_UNIQUE_ID = "txtweb-id";
	private static final String HTTP_PARAM_TXTWEB_VERIFYID = "txtweb-verifyid";

	@Override
	public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {

		String txtWebMessage = httpRequest.getParameter(HTTP_PARAM_TXTWEB_MESSAGE); 		// Message sent by the end user
		String txtWebMobileHash = httpRequest.getParameter(HTTP_PARAM_TXTWEB_MOBILE); 		// End user's mobile number in hash format
		String txtWebUniqueId = httpRequest.getParameter(HTTP_PARAM_TXTWEB_UNIQUE_ID); 		// Unique id for the message
		String txtWebVerifyId = httpRequest.getParameter(HTTP_PARAM_TXTWEB_VERIFYID);		// verify-id param from txtWeb
		String txtWebProtocol = httpRequest.getParameter(HTTP_PARAM_TXTWEB_PROTOCOL); 		// Protocol through which your application has been accessed.
		/*
		 * txtweb-protocol values:
		 * 	SMS: 1000
		 * 	WEB: 200x
		 * 	EMULATOR: 2100
		 * 	INSTANT MESSENGER: 220x
		 */

		
		/* Construct the response message to be returned as a response from the application
		 * A simple Welcome message, with all the above captured parameters echoed to the end user
		 */
		String response =  "Welcome to txtWeb!"
			+"<br/> You have successfully written your first txtWeb app :-)";

		//if-else block to test for null or empty values for each of the above parameters

		if (txtWebMessage != null && !txtWebMessage.isEmpty()){
			response += "<br/><br/>txtweb-message : " + txtWebMessage; // will be null if no message was sent to the application
		}else{
			response += "<br/>txtweb-message param is null or empty";
		}

		if (txtWebMobileHash != null && !txtWebMobileHash.isEmpty()){
			response += "<br/><br/>txtweb-mobile : " + txtWebMobileHash; 
		}else{
			response += "<br/>txtweb-mobile parameter is null or empty";
		}

		if (txtWebProtocol != null && !txtWebProtocol.isEmpty()){
			response += "<br/><br/>txtweb-protocol : " + txtWebProtocol; 
		}else{
			response += "<br/>txtweb-protocol parameter is null or empty";
		}

		if (txtWebUniqueId != null && !txtWebUniqueId.isEmpty()){
			response += "<br/><br/>txtweb-id : " + txtWebUniqueId; 
		}else{
			response += "<br/>txtweb-id parameter is null or empty";
		}

		if (txtWebVerifyId != null && !txtWebVerifyId.isEmpty()){
			response += "<br/><br/>txtweb-verifyid : " + txtWebVerifyId; 
		}else{
			response += "<br/>txtweb-verifyid parameter is null or empty";
		}

		sendResponse(httpResponse, response);

	}

	private static void sendResponse(HttpServletResponse httpResponse, String response) {
		try{
			httpResponse.setContentType("text/html; charset=UTF-8");
			PrintWriter out = httpResponse.getWriter();

			// Add all the surrounding HTML to the response string
			String htmlResponse = "<html><head><title>Hello txtWeb</title>"
				+ "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"
				+ "<meta name='" + APPKEY_NAME + "' content='" + APPKEY_CONTENT + "' />"
				+ "</head><body>" + response + "</body></html>";

			out.println(htmlResponse);
		} catch (Exception e) {
			//Exception handling
			logger.log(Level.SEVERE, "Exception caught", e);
		}		
	}
}

