package com.txtweb.server.examples.dictionary;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

public class Dictionary extends HttpServlet {

	private static final long serialVersionUID = 1869644027140419135L;

	String searchWord = "";
	String txtWebResponse = "";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)

	throws IOException {

		this.searchWord= req.getParameter("txtweb-message");

		if (this.searchWord ==null || this.searchWord.isEmpty())
			sendResponse(resp,"Welcome to Dictionary on txtWeb<br/><br/>"+getSearchForm()+"<br/><br/>For example: <br/>'@word alibi' or<br/>'@word complex' etc<br/><br/>");
		else
			findMeaning(resp,StringUtils.strip(this.searchWord).replace("\\s+"," "));
	}

	private void sendResponse(HttpServletResponse resp, String smsResponse) {
		try {
			resp.setContentType("text/html");
			resp.addHeader("Cache-Control", "max-age=2419200"); // Allow caching responses for up to 4 weeks. (Dictionary definitions just don't change that often)
			PrintWriter out = resp.getWriter();
			out.println("<html><head><title>Dictionary on txtWeb</title><meta name=\"txtweb-appkey\" content=\"B29672A4-97A8-44D0-9956-3347DA3934D3\" /></head><body>"
					+smsResponse 
					+"</body></html>"); 

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void findMeaning(HttpServletResponse resp,String word) throws MalformedURLException, IOException {
		this.searchWord = URLEncoder.encode(word, "UTF-8");
		String sourceUrlString="http://en.wiktionary.org/wiki/" + this.searchWord;
		Source source=new Source(new URL(sourceUrlString));
		Element bodyContent = source.getElementById("bodyContent");
		this.txtWebResponse = parseHtmlNode(bodyContent);
		if(!this.txtWebResponse.isEmpty())
			sendResponse(resp,"Word : "+word+"<br/>Meaning :<br/>"+this.txtWebResponse);
		else
			sendResponse(resp,"Nothing Found for the word "+word);
	}

	private String parseHtmlNode(Element theElement) {
		List<Element> children = theElement.getChildElements();
		this.txtWebResponse = "";
		outer : for (Element child: children) 
		{
			if (child.getName().equals("ol"))
			{
				List<Element> multipleAnswersChildren = child.getChildElements();
				this.txtWebResponse += (new TextExtractor(multipleAnswersChildren.get(0).getContent())).toString()+" ";
				break outer;
			}

			else if((child.getChildElements()).size() > 0)
			{
				this.txtWebResponse = parseHtmlNode(child);
				if(!this.txtWebResponse.isEmpty())
					break outer;					
			}
		}
		return this.txtWebResponse;
	}

	private static String getSearchForm() {
		return "<form action='./dictionary' method='get' class='" + "txtweb-form" + "' >"
		+ "word <input type='text' name='txtweb-message' />"
		+ "<input type='submit' value='Submit' />"
		+ "</form> to find the definition of a word";
	}
}


