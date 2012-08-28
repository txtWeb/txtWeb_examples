package com.txtweb.examples.hellotxtweblocation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//A simple application which uses the mobile hash from where the request to txtWeb was made, 
//and calls location API to extract the location information for that mobile
//Send @hellotxtweblocation to see this app live

public class HellotxtWebLocationServlet extends HttpServlet {

	private static final long serialVersionUID = 2441651572495913280L;

	private static final Logger logger = Logger.getLogger(HellotxtWebLocationServlet.class.getName());

	private static final String APPKEY_NAME = "txtweb-appkey";
	private static final String APPKEY_CONTENT = "8798c973-bf99-49a8-a1c9-d2e49d53421b";

	final String LOCATION_API_URL = "http://api.txtweb.com/v1/location/get";
	final String SUCCESS_CODE = "0";

	private static final String HTTP_PARAM_TXTWEB_MOBILE = "txtweb-mobile";

	@Override
	public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
	throws IOException {

		try{
                        // Mobile number in hash format of the user whose location has to be extracted
			String txtWebMobileHash = httpRequest.getParameter(HTTP_PARAM_TXTWEB_MOBILE); 		
			
			if (txtWebMobileHash == null || txtWebMobileHash.isEmpty()){
				sendResponse(httpResponse, getWelcomeMessage());
			}
			String mobileHashParam = "txtweb-mobile=" + URLEncoder.encode(txtWebMobileHash,"UTF-8");

			//Using DOM parser to parse the XML response from the Location API
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(this.LOCATION_API_URL + "?" + mobileHashParam).openStream());

			/**
			 * Example of a sample success response from the Location Service API
			 * <txtWeb>
			 * <status>
			 * <code>0</code>
			 * <message>success</message>
			 * </status>
			 * <location mobile="1F5YF1B4-60QB-491F-A5C6-Q31KC4WD640B"> 
			 * <default>true</default>
			 * <userlocationtext>Bangalore</userlocationtext>
			 * <geolocation>
			 * <city>Bengaluru</city>
			 * <province>Karnataka</province>
			 * <country>IN</country>
			 * <postalcode>571111</postalcode>
			 * </geolocation>
			 * </location>
			 * </txtWeb>
			 */
			NodeList childNodes = doc.getChildNodes();
			String code = "-1";
			String isDefaultLocation = ""; 
                        /*
                        * If the above value(isDefaultLocation) is set to true, 
                        * it means the location has been set by the user, 
                        * else it is a value that has been guesses by the platform 
                        * from the end user's mobile number
                        */
			String userLocationText = "";
			String city = ""; 
			String province = "";
			String country = "";
			String postalCode = "";
			String response = "";
			for(int index = 0; index < childNodes.getLength(); index++){
				Node childNode = childNodes.item(index);
				if( childNode.getNodeType() == Node.ELEMENT_NODE ){ 
					Element element = (Element) childNode;
					code = getTagValue("code", element);
                                        // if success, then extract all location params
					if (this.SUCCESS_CODE.equals(code)){	
						isDefaultLocation = getTagValue("default", element);
						userLocationText = getTagValue("userlocationtext", element);
						// Note that City, province, country and postalcode can be null
						city = getTagValue("city", element);
						province = getTagValue("province", element);
						country = getTagValue("country", element);
						postalCode = getTagValue("postalcode", element);

					}else{
						//Echo error code
						sendResponse(httpResponse,"An error occurred! Error Code : " + code);
					}
					response += "The Location API response for your mobile is as follows: "
					+ "<br/> Default: " + isDefaultLocation
					+ "<br/> UserLocation: " + userLocationText
					+ "<br/> City: " + city
					+ "<br/> Province: " + province
					+ "<br/> Country: " + country
					+ "<br/> Postal Code: " + postalCode;
					sendResponse(httpResponse, response);
				}
			}
		}catch(Exception e){
			//Exception handling
			logger.log(Level.SEVERE,"Exception caught" + e);
		}
	}


	private String getWelcomeMessage() {
		return "Welcome to txtWeb! Try playing around with our location API..<br/>";
	}


	private static String getTagValue(String sTag, Element eElement) {
		try{
			NodeList nodeList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			Node node = nodeList.item(0);
			return node.getNodeValue();
		}catch(Exception e){//If sTag does not exist, return null
			return null;
		}
	}

	private static void sendResponse(HttpServletResponse httpResponse, String response) {
		try{
			httpResponse.setContentType("text/html; charset=UTF-8");
			PrintWriter out = httpResponse.getWriter();

			// Add all the surrounding HTML to the response string
			String htmlResponse = "<html><head><title>Hello txtWeb Location!</title>"
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
