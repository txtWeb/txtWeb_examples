package com.intuit.txtweb.server.example.twitter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class TwitterStatus extends HttpServlet {

	private static final long serialVersionUID = -81152587263123048L;
	private static final Logger logger = Logger.getLogger(TwitterStatus.class);
	static final String TWITTER_USER = "user";
	/**
	 * Use http://<hostname>:<port>/twitter?user=<username> in applink
	 */
	
	@Override
	public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
		processRequest(httpRequest, httpResponse);
	}
	
	@Override
	public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
		processRequest(httpRequest, httpResponse);
	}
	
	public static void processRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		String response = null;
		
		String twitterUser = httpRequest.getParameter(TWITTER_USER);
		String message = httpRequest.getParameter("txtweb-message");
		
		if((twitterUser == null || twitterUser.isEmpty()) && StringUtils.isNotEmpty(message)) {
			message = StringUtils.strip(message).replaceAll("\\s+", " ");
			twitterUser = message;
		}

		if(twitterUser == null || twitterUser.isEmpty()) {
			// user is not specified. Send instructions to the user
			response = getWelcomeMessage();
			sendResponse(httpResponse, response);
			return;
		}
		
		try {
			response = "";
			
			URL url = new URL("http://twitter.com/users/show/" + twitterUser + ".xml");
			URLConnection conn = url.openConnection();
			TagNode node = new HtmlCleaner().clean(conn.getInputStream());
			TagNode responseNode = node.findElementByName("text", true);
			if(responseNode != null) {
				response = responseNode.getText().toString();
			}

			if(!response.isEmpty()) {
				response = "<html><head><title>Twitter</title>"
					+ "<meta name=\"" + "txtweb-appkey" + "\" content=\"179E074E-DC8B-47F2-8DD5-AD600D3D1009\" />"
					+ "</head><body>"
					+ response + "<br/><br/>"
					+ "To find another user's status: <br/>"
					+ "<form action=\"/twitter\" method=\"" + "get" + "\" class=\"" + "txtweb-form" + "\">"
					+ "user name<input type=\"text\" name=\"" + TWITTER_USER + "\" />"
					+ "<input type=\"submit\" value=\"Submit\" />"
					+ "</form><body></html>";
				sendResponse(httpResponse, response);			
				return;
			}
		} catch (MalformedURLException mue) {
			logger.error("Malformed URL exception cauught", mue);
		} catch (IOException ioe) {
			logger.error("IO exception cauught", ioe);
		}	
		
		// Unknown error or no results. Respond with a 'welcome' message 
		// and instructions on how to use the service
		response = getNothingFoundMessage(twitterUser);
		sendResponse(httpResponse, response);
		return;

	}
	
	private static void sendResponse(HttpServletResponse httpResponse, String response) {
		try{
			httpResponse.addHeader("Cache-Control", "max-age=60"); // Allow caching responses for up to 1 minute.			
			httpResponse.setContentType("text/html");
			PrintWriter out = httpResponse.getWriter();
			out.println(response);
		} catch (IOException ioe) {
			logger.error("IO exception cauught", ioe);
		}
	}
	
	private static String getWelcomeMessage() {
		return "<html><head><title>Twitter</title>"
		+ "<meta name=\"" + "txtweb-appkey" + "\" content=\"179E074E-DC8B-47F2-8DD5-AD600D3D1009\" />"
		+ "</head><body>"
		+ "Welcome to Twitter<br/><br/>"
		+ "To find a user's status: <br/>"
		+ "<form action=\"/twitter\" method=\"" + "get" + "\" class=\"" + "txtweb-form" + "\">"
		+ "user name<input type=\"text\" name=\"" + "txtweb-message" + "\" />"
		+ "<input type=\"submit\" value=\"Submit\" />"
		+ "</form></body></html>";
	}
	
	private static String getNothingFoundMessage(String twitterUser) {
		return "<html><head><title>Twitter</title>"
		+ "<meta name=\"" + "txtweb-appkey" + "\" content=\"179E074E-DC8B-47F2-8DD5-AD600D3D1009\" />"
		+ "</head><body>"
		+ "No twitter user or activity found for user: " + twitterUser + "<br/><br/>"
		+ "To find a user's status: <br/>"
		+ "<form action=\"/twitter\" method=\"" + "get" + "\" class=\"" + "txtweb-form" + "\">"
		+ "user name<input type=\"text\" name=\"" + "txtweb-message" + "\" />"
		+ "<input type=\"submit\" value=\"Submit\" />"
		+ "</form></body></html>";
	}
	
}

