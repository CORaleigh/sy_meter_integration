package gov.raleighnc.switchyard.integration.service.cwmeter.processor;

import gov.raleighnc.switchyard.integration.domain.Result;

import java.io.StringReader;

import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Switchyard Camel processor for handling SOAP responses from CCB.
 * 
 * @author mikev
 *
 */
@Named("ccbServiceSoapProcessor")
public class CcbServiceSoapProcessor implements Processor {
	private static final String FALSE_STATUS = "<ResponseStatus>F</ResponseStatus>";
	private static final String RESPONSE_TEXT_START = "<ResponseText>";
	private static final String RESPONSE_TEXT_END = "</ResponseText>";
	private static final String TEXT = "Text:";
	private static final String DESCRIPTION = "Description:";

    @Override
    public void process(Exchange exchange) throws Exception {   	
    	String sr = exchange.getIn().getBody(String.class);
        
        Document doc = getDom(sr);
        Result result = new Result();
    	result.setSuccess(true);
    	
        if (sr.contains("CMCompleteFieldActivity")) {

        	NodeList nl = doc.getElementsByTagName("CMCompleteFieldActivity");
        	for(int i = 0; i < nl.getLength(); i++) {
                Element element = (Element)nl.item(i);
                if (element.getTagName().equals("fieldActivityId")) {
                	result.setMessage(element.getNodeValue());
                	break;
                }
            }
        } else if (sr.contains(FALSE_STATUS) && sr.contains(RESPONSE_TEXT_START)) {
        	result.setSuccess(false);
        	
        	int startMsgIndex = sr.indexOf(RESPONSE_TEXT_START) + RESPONSE_TEXT_START.length();
        	int endMsgIndex = sr.indexOf(RESPONSE_TEXT_END);
        	String newMsg = sr.substring(startMsgIndex, endMsgIndex).trim();
        	
        	 if (newMsg.contains(TEXT) && newMsg.contains(DESCRIPTION)) {
       		 	int startIndex = newMsg.indexOf(TEXT) + TEXT.length();
                int endIndex = newMsg.indexOf(DESCRIPTION);
                String errorMsg = newMsg.substring(startIndex, endIndex).trim();
                result.setException(errorMsg);
        	 }
        }
        
        exchange.getOut().setBody(result);
    }
    
    protected Document getDom(String xml) throws Exception {
    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        return documentBuilder.parse(is);
    }
}