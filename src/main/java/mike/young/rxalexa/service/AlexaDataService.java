package mike.young.rxalexa.service;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import rx.Observable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AlexaDataService {
    private File file = new File(getClass().getClassLoader().getResource("top_sites_raw.xml").getFile());

    public Observable<String> findAllSites() {
        try{
            FileInputStream fileIS = new FileInputStream(file);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(fileIS);
            System.out.println(xmlDocument.getXmlVersion());

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "//*[local-name()='DataUrl']/text()";
//            System.out.println("Expression: "+ expression);
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
            System.out.println("Found nodes: " + nodeList.getLength());

            List<String> urlList = new ArrayList<String>();
            for(int i = 0; i < nodeList.getLength(); i++){
                urlList.add(nodeList.item(i).getNodeValue());
            }

            return Observable.from(urlList);
        }catch (Exception e){
            e.printStackTrace();
            return Observable.empty();
        }

    }
}
