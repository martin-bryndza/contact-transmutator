/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/*import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import net.sf.saxon.Configuration;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xpath;

 */
/**
 *
 * @author ovečka
 */
public class InternalDocAutodetectFormatImpl /*implements InternalDocAutodetectFormat */ {

    private Document doc;
    private Element root;

    InternalDocAutodetectFormatImpl(Document newDoc) {
        doc = newDoc;
        root = doc.getDocumentElement(); //get doc root element

    }

    ;

    //use xpath.evaulate or sth like that
    //count percentual share of data types and guess columns
    void test_deletethislater() throws XPathExpressionException {
        XPath xpath;
        xpath = XPathFactory.newInstance().newXPath();

        //count 0th columns
        Integer columnI = 0;
        System.out.println("\n!!:" + xpath.evaluate("count(//*[@counter=\"" + columnI.toString() + "\"])", doc, XPathConstants.NUMBER));

        /*   xpath = XPathFactory.newInstance().newXPath();
        //count 0th columns that match regexp
        columnI = 0;
        System.out.println("\n!name!:" + xpath.evaluate("//data[matches(., '[a-z]')]", doc));
         * //regexps are broken like hell ([a-z] matches totojeA1, wtf?)
         *
         */

        //try through classic DOM

        System.out.println("\n==Regexp analysis!\n");
        columnI = 1; //0th column
         xpath = XPathFactory.newInstance().newXPath(); //new xpath
       NodeList dataNodeList = (NodeList) xpath.evaluate("//data[@counter=\"" + columnI.toString() + "\"]", root, XPathConstants.NODESET); //select the nodes

       for (int i = 0; i < dataNodeList.getLength(); i++) {
            if (dataNodeList.item(i) instanceof Element) {
                System.out.println(dataNodeList.item(i).getTextContent());
                        //Pattern patternRplcDelims = Pattern.compile("", Pattern.MULTILINE);
//           Pattern pattern = Pattern.compile(str_pattern);
//            Matcher matcher = pattern.matcher(sb);
                                System.out.println("Name:");
                Pattern patternName = Pattern.compile("[\\p{L}\\., '’[^@0-9]]+");
                Matcher matcherName = patternName.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherName.matches()?"Match!":"Not match");

                System.out.println("Email:");
                Pattern patternEmail = Pattern.compile(".+@.+");
                Matcher matcherEmail = patternEmail.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherEmail.matches()?"Match!":"Not match");


                                System.out.println("Number:");
                Pattern patternNumber = Pattern.compile("[+0-9\\(\\)/ [^@]]+\\-");
                Matcher matcherNumber = patternNumber.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherNumber.matches()?"Match!":"Not match");

                                                System.out.println("Address:");
                Pattern patternAddress = Pattern.compile("[\\p{L}\\., '’0-9\\(\\)/ [^@]]+");
                Matcher matcherAddress = patternAddress.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherAddress.matches()?"Match!":"Not match");
            }
        }
        System.out.println("\n==Regexp analysis END!\n");

        /*XPathExpression expr =  xpath.compile("//*[count(BBB)=2]");
        //Select elements which have two children BBB
        System.out.println("Select elements which have two children BBB");
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); i++) {
        System.out.println(nodes.item(i).getNodeName());
        }
         *
         */
    }
    

}
