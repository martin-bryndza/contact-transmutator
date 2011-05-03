/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author jakub svoboda
 */
public class InternalDocAutodetectFormatImpl /*implements InternalDocAutodetectFormat */ {

    private Document doc;
    private Element root;

    InternalDocAutodetectFormatImpl(Document newDoc) {
        doc = newDoc;
        root = doc.getDocumentElement(); //get doc root element

    }

    public InternalDocColumnSchema autodetect() {
//TODO
        return null;
    }

//this returns null if column type cannot be detected
//or returns one of:
//VCFTypesEnum.Formatted_Name.toString()
//VCFTypesEnum.Label_Address.toString()
//VCFTypesEnum.Telephone.toString()
//VCFTypesEnum.Email.toString()
    public String autodetectColumn(Integer columnNumber) {

        XPath xpath;
        xpath = XPathFactory.newInstance().newXPath();
        Integer numOfCellsInColumn = 0;

        Integer names = 0;
        Integer emails = 0;
        Integer addresses = 0;
        Integer phones = 0;

        String returnType = null;


        Integer columnI = columnNumber;
        columnI = columnNumber; //0th column
        xpath = XPathFactory.newInstance().newXPath(); //new xpath
        NodeList dataNodeList = null;
        try {
            dataNodeList = (NodeList) xpath.evaluate("//data[@counter=\"" + columnI.toString() + "\"]", root, XPathConstants.NODESET); //select the nodes
            numOfCellsInColumn = dataNodeList.getLength();
            for (int i = 0; i < numOfCellsInColumn; i++) {
                if (dataNodeList.item(i) instanceof Element) {

                    String contents = dataNodeList.item(i).getTextContent();

                    String detection = autodetectString(contents);

                    if (detection != null) {
                        if (detection.equals(VCFTypesEnum.Formatted_Name.toString())) {
                            names++;
                        }
                        if (detection.equals(VCFTypesEnum.Label_Address.toString())) {
                            addresses++;
                        }
                        if (detection.equals(VCFTypesEnum.Telephone.toString())) {
                            phones++;
                        }
                        if (detection.equals(VCFTypesEnum.Email.toString())) {
                            emails++;
                        }

                    }
                }
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(InternalDocAutodetectFormatImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        if ((names + emails + phones + addresses) != 0) {
            HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
            hashmap.put(VCFTypesEnum.Formatted_Name.toString(), names);
            hashmap.put(VCFTypesEnum.Email.toString(), emails);
            hashmap.put(VCFTypesEnum.Label_Address.toString(), addresses);
            hashmap.put(VCFTypesEnum.Telephone.toString(), phones);

            SortedSet<Integer> sortedints = new TreeSet<Integer>(hashmap.values());
            Iterator iter = sortedints.iterator();
            while (iter.hasNext()) { //iterates all the way to the end and returns the key with the highest value
                returnType = (String) ((Map.Entry) iter.next()).getKey();
            }
        }

        return returnType;
    }

    public String autodetectString(String string) {
        String returnString = null;

        Pattern patternName = Pattern.compile("[\\p{L}\\., '’[^@0-9]]+");
        Matcher matcherName = patternName.matcher(string);

        Pattern patternEmail = Pattern.compile(".+@.+");
        Matcher matcherEmail = patternEmail.matcher(string);

        Pattern patternNumber = Pattern.compile("[\\+0-9\\(\\)/ [^@]]+\\-");
        Matcher matcherNumber = patternNumber.matcher(string);

        Pattern patternAddress = Pattern.compile("[\\p{L}\\., '’0-9\\(\\)/ [^@]]+", Pattern.MULTILINE);
        Matcher matcherAddress = patternAddress.matcher(string);

        // name > address > number > email
        if (matcherName.matches()) {
            returnString = VCFTypesEnum.Formatted_Name.toString();
        }
        if (matcherAddress.matches()) {
            returnString = VCFTypesEnum.Label_Address.toString();
        }
        if (matcherNumber.matches()) {
            returnString = VCFTypesEnum.Telephone.toString();
        }
        if (matcherEmail.matches()) {
            returnString = VCFTypesEnum.Email.toString();
        }

        return returnString;
    }

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
                System.out.println(matcherName.matches() ? "Match!" : "Not match");

                System.out.println("Email:");
                Pattern patternEmail = Pattern.compile(".+@.+");
                Matcher matcherEmail = patternEmail.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherEmail.matches() ? "Match!" : "Not match");


                System.out.println("Number:");
                Pattern patternNumber = Pattern.compile("[+0-9\\(\\)/ [^@]]+\\-");
                Matcher matcherNumber = patternNumber.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherNumber.matches() ? "Match!" : "Not match");

                System.out.println("Address:");
                Pattern patternAddress = Pattern.compile("[\\p{L}\\., '’0-9\\(\\)/ [^@]]+");
                Matcher matcherAddress = patternAddress.matcher(dataNodeList.item(i).getTextContent());
                System.out.println(matcherAddress.matches() ? "Match!" : "Not match");
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
