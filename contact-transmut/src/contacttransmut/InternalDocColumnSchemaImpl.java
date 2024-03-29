/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * ColumnSchema implementation.
 *
 * Make W3C XML DOM for managing column schema.
 * This column schema will be used by:
 * 1) auto-detection
 * 2) graphical user interface
 * 3) output compiler
 *
 * This schema should facilitate all possible and morbid combinations of data, aggregation and merging.
 * It doesn’t look quite simple, but it should do the task well
 * XML DOM is also quite handy for this task because it is rather flexible data structure with robust query options
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!! read this document VERY carefully before programming this class !!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!! no changes shall be made here without consent of cooperating parties !!!!!
 * !!!!! (this data format is used throughout the application                 !!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * TO-DO list for future:
 * implement multiple-delimiter choice throughout the program (not implemented ATM)
 *   - requires changes in InternalDocColumnSchemaImpl, InternalDocCompiler, GUI
 * 
 * bear in mind the result should be VCF-compatible
 *
 * some of the features may not be currently implemented, refer to the source code
 *
 * notes:
 * column
 *   mergedinother - merged in set?
 *   aggregated - is this multiple aggregated columns that will be split into separate columns?
 * aggregatedcolumns
 *   delimiter - delimiter used in text to delimit the columns
 *   intoseparatecontacts - no->simply split into more columns
 *                          yes->make new contact for each contact in here (list of employees...)
 *   employees - yes->company/displayname of original contact into Company field of new contact
 *               no->displayname of original contact into note of new contact
 *   originalintargetnote - yes->original of the entire original field into note of new contact
 *                          no->do nothing
 *   originalinsourcenote - yes->original of the entire original field int note of the original contact
 *                          no->do nothing
 *   separatecontacts - present only when intoseparatecontacts="yes"
 *     delimiter - delimiter used in text to delimit contacts from each other (example: aggregatedcolumns delimiter is "," and separatecontacts delimiter is ";" here: john, 123, a@b.com; jake, 342; carl, carl@b.com)
 *     autodetectswaps - no->contacts MUST be of selected format and number of columns EVERY TIME
 *                       yes->some columns can be ommited and swaps are automatically detected
 *     type - present only when autodetectswaps="yes"
 *       number - corresponding //aggregatedcolumns/column@number
 *         regexp - redefine regular expression to detect this column type - currently not implemented
 * candidatetype - type of column based on auto-detection (values from VCFHelperEnum.toString())
 * selectedtype - type of column selected by user (values from VCFHelperEnum.toString())
 * mergein
 *   set - which set the column belongs to (starting from 1)
 *   order - order of the column in the set (starting from 1)
 * mergeset
 *   number - which set this is (there can be more sets)
 *   delimiter - delimiting character to use in the set
 *
 * example of column schema XML document:
 * <root>
 *   <columnschema>
 *     <column number="0" mergedinother="no" aggregated="no">
 *       <candidatetype value="Name"/>
 *       <selectedtype value="First name"/>
 *     </column>
 *     <column number="1" mergedinother="no" aggregated="no">
 *       <candidatetype value="Name"/>
 *       <selectedtype value="Surname"/>
 *     </column>
 *     <column number="2" mergedinother="no" aggregated="yes">
 *        <aggregatedcolumns delimiter="," numberofcolumns="3" intoseparatecontacts="yes" employees="yes" originalinsourcenote="yes" originalintargetnote="yes">
 *          <separatecontacts delimiter=";" autodetectswaps="yes">
 *            <type number="1">
 *              <regexp>[0-9#*]*</regexp>
 *            </type>
 *          </separatecontacts>
 *          <column number="0">
 *            <candidatetype value="Name"/>
 *            <selectedtype value="Display Name"/>
 *          </column>
 *          <column number="1">
 *            <candidatetype value="Phone number"/>
 *            <selectedtype value="Phone number"/>
 *          </column>
 *          <column number="2">
 *            <candidatetype value="Email"/>
 *            <selectedtype value="Email"/>
 *          </column>
 *        </aggregatedcolumns>
 *     </column>
 *     <column number="3" mergedinother="no" aggregated="yes">
 *        <aggregatedcolumns delimiter=";" numberofcolumns="2" intoseparatecontacts="no" employees="no">
 *          <column number="0">
 *            <candidatetype value="Email"/>
 *            <selectedtype value="Email"/>
 *          </column>
 *          <column number="1">
 *            <candidatetype value="Email"/>
 *            <selectedtype value="Email"/>
 *          </column>
 *        </aggregatedcolumns>
 *     </column>
 *     <column number="4" mergedinother="yes" aggregated="no">
 *       <mergein set="1" order="2"/>
 *     </column>
 *     <column number="5" mergedinother="yes" aggregated="no">
 *       <mergein set="1" order="1"/>
 *     </column>
 *     <mergeset number="1">
 *       <delimiter value=","/>
 *       <selectedtype value="Note"/>
 *     </mergeset>
 *   </columnschema>
 * </root>
 *
 * JavaDoc comments from Interface are applicable.
 * 
 * @author Jakub Svoboda
 */
public class InternalDocColumnSchemaImpl implements InternalDocColumnSchema {

    private Document doc; //sorry, the doc should be private anyways... it’s just an internal structure; use the methods!
    private Element root;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;

    /**
     * Constructor. Only number of columns is specified - this only parameter cannot be changed for existing columnschema.
     * @param columns number of columns
     */
    public InternalDocColumnSchemaImpl(Integer columns) {
        // create new internal XML DOM for processing the data
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc = db.newDocument(); //this is internal XML DOM we use to process the data
        Element root0 = doc.createElement("root");
        doc.appendChild(root0);
        root = root0.getOwnerDocument().createElement("columnschema");
        root0.appendChild(root);

        addColumns(columns);
    }

    /**
     * Contructor used for cloning
     * @param newDoc internal InternalDocColumnSchemaImpl’s Document that will be parsed and duplicated (providing deep copy of InternalDocColumnSchemaImpl)
     */
        private InternalDocColumnSchemaImpl(Document newDoc) {

           TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = null;
        try {
            trans = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(InternalDocColumnSchemaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
           // String result = null;

            DOMResult result = new DOMResult();
        try {
            trans.transform(new DOMSource(newDoc), result);
        } catch (TransformerException ex) {
            Logger.getLogger(InternalDocColumnSchemaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }


        // create new internal XML DOM for processing the data
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc = (Document)result.getNode();

        //doc = db.parse(result);


        findRoot();

    }



        /**
         * Returns cloned column schema.
         * Java operates with pointers. Implementing deep copy constructor for this class would be troublesome. This is the best way to make an independent copy of columnschema.
         * @return new InternalDocColumnSchema
         */
    public InternalDocColumnSchema returnClonedColumnSchema() {
        // ignore // System.err.println("new column schema created!");
        InternalDocColumnSchema newSchema = new InternalDocColumnSchemaImpl(doc);
        return newSchema;
    }

    /**
     * Internal helper method. Returns NodeList for given XPath expression.
     * Handy for selecting multiple nodes based on some criteria.
     * @param newXpath XPath expression to select nodes
     * @return NodeList of selected nodes
     */
    private NodeList returnXPathNodeList(String newXpath) {
        XPath xpath = XPathFactory.newInstance().newXPath(); //new xpath
        NodeList dataNodeList = null;
        try {
            dataNodeList = (NodeList) xpath.evaluate(newXpath, doc, XPathConstants.NODESET); //select the nodes
        } catch (XPathExpressionException ex) {
            Logger.getLogger(InternalDocColumnSchemaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataNodeList;
    }

    /**
     * Helper method that returns first element of a NodeList
     * @param dataNodeList input NodeList
     * @return first Element in NodeList (=not first Node)
     */
    private Element returnFirstElement(NodeList dataNodeList) {
        if (dataNodeList.getLength() <= 0){
            return new ElementImpl(new DocumentImpl(), "null");
        }

        Element retElement = null;
        for (int i = 0; i < dataNodeList.getLength(); i++) {
            if (dataNodeList.item(i) instanceof Element) {
                retElement = (Element) dataNodeList.item(i);
                break;
            }
        }
        return retElement;
    }

 
    /**
     * Helper method that locates root of the ColumnSchema Document. Handy in case the document changed before.
     * each InternalDocColumnSchemaImpl method should locate columnschema element again (document could have been replaced and the root variable voided)
     */
    private void findRoot() {
        NodeList dataNodeList = returnXPathNodeList("//columnschema");

        root = returnFirstElement(dataNodeList);
    }

    //say I have read document with maxColumnNumber="7"
    //so there are 8 columns in there
    //so I’ll just call addColumns(8)
    /**
     * Private helper method to initialize ColumnSchema with given number of columns.
     *
     * @param numOfColumns number of columns
     */
    private void addColumns(Integer numOfColumns) {
        findRoot();

        for (Integer i = 0; i < numOfColumns; i++) {
            Element column = root.getOwnerDocument().createElement("column");
            column.setAttribute("number", i.toString());
            column.setAttribute("mergedinother", "no");
            column.setAttribute("aggregated", "no");
            root.appendChild(column);
        }
    }

    public boolean isColumnMergedInOther(Integer colNumber) {
        return queryNumberedElementAttributeYesNo(colNumber, "//columnschema/column", "mergedinother");
    }

    public boolean isColumnAggregated(Integer colNumber) {
        return queryNumberedElementAttributeYesNo(colNumber, "//columnschema/column", "aggregated");
    }

    /**
     * Helper method that queries whether given column has given attribute set.
     * Please read and understand the source code before using this method.
     * @param colNumber number of column
     * @param newXpath XPath to locate certain column’s Element
     * @param attribute name of attribute
     * @return true if set, false if not set or not meaningful
     */
    private boolean queryNumberedElementAttributeYesNo(Integer colNumber, String newXpath, String attribute) {
        NodeList dataNodeList = returnXPathNodeList(newXpath + "[@number=\"" + colNumber.toString() + "\"]");

        boolean answer = false;
        for (int i = 0; i < dataNodeList.getLength(); i++) {
            if (dataNodeList.item(i) instanceof Element) {
                Element retrieved = (Element) dataNodeList.item(i);
                if (retrieved.getAttribute(attribute) == null ? "yes" == null : retrieved.getAttribute(attribute).equals("yes")) {
                    answer = true;
                }
                break;
            }
        }
        return answer;
    }

    /*
     *  *     <column number="2" mergedinother="no" aggregated="yes">
     *        <aggregatedcolumns delimiter="," numberofcolumns="3" intoseparatecontacts="yes" employees="yes" originalinsourcenote="yes" originalintargetnote="yes">
     *          <separatecontacts delimiter=";" autodetectswaps="yes">
     *            <type number="1">
     *              <regexp>[0-9#*]*</regexp>
     *            </type>
     *          </separatecontacts>
     *          <column number="0">
     *            <candidatetype value="Name"/>
     *            <selectedtype value="Display Name"/>
     *          </column>
     *          <column number="1">
     *            <candidatetype value="Phone number"/>
     *            <selectedtype value="Phone number"/>
     *          </column>
     *          <column number="2">
     *            <candidatetype value="Email"/>
     *            <selectedtype value="Email"/>
     *          </column>
     *        </aggregatedcolumns>

     */
    public boolean columnAggregateOn(Integer colNum, String delimiter, Integer numberofcolumns,
            boolean intoseparatecontacts, boolean employees, boolean originalsourcenote,
            boolean originaltargetnote, String separatecontactsdelimiter, boolean autodetectswaps) {
        findRoot();
        if ((!isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when !isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]");

            Element theColumnElement = returnFirstElement(dataNodeList);
            
            Element aggregatedcolumns = theColumnElement.getOwnerDocument().createElement("aggregatedcolumns");
            aggregatedcolumns.setAttribute("delimiter", delimiter);
            aggregatedcolumns.setAttribute("numberofcolumns", numberofcolumns.toString());
            aggregatedcolumns.setAttribute("intoseparatecontacts", (intoseparatecontacts ? "yes" : "no"));
            aggregatedcolumns.setAttribute("employees", (employees ? "yes" : "no"));
            aggregatedcolumns.setAttribute("originalsourcenote", (originalsourcenote ? "yes" : "no"));
            aggregatedcolumns.setAttribute("originaltargetnote", (originaltargetnote ? "yes" : "no"));

            if (intoseparatecontacts) {
                Element separatecontacts = aggregatedcolumns.getOwnerDocument().createElement("separatecontacts");
                separatecontacts.setAttribute("delimiter", delimiter);
                separatecontacts.setAttribute("autodetectswaps", (autodetectswaps ? "yes" : "no"));
                aggregatedcolumns.appendChild(separatecontacts);
            }

            for (Integer i = 0; i < numberofcolumns; i++) {
                Element column = aggregatedcolumns.getOwnerDocument().createElement("column");
                column.setAttribute("number", i.toString());
                aggregatedcolumns.appendChild(column);
            }

            theColumnElement.appendChild(aggregatedcolumns);

            theColumnElement.setAttribute("aggregated", "yes");

            return true;
        } else {
            return false;
        }
    }

    public boolean columnAggregateOff(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element theAggregatedcolumnsElement = returnFirstElement(dataNodeList);

            Element theColumnElement = (Element) theAggregatedcolumnsElement.getParentNode();

            theColumnElement.removeChild(theAggregatedcolumnsElement);

            theColumnElement.setAttribute("aggregated", "no");

            return true;
        } else {
            return false;
        }
    }

    public String queryAggregateSettingDelimiter(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            return element.getAttribute("delimiter");

        } else {
            return null;
        }
    }

    public Integer queryAggregateSettingNumberofcolumns(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("numberofcolumns");
            Integer intReturn = Integer.parseInt(stringReturn);
            return intReturn;
        } else {
            return null;
        }

    }

    public boolean queryAggregateSettingIntoseparatecontacts(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("intoseparatecontacts");

            return (stringReturn == null ? "yes" == null : stringReturn.equals("yes"));
        } else {
            return false;
        }
    }

    public boolean queryAggregateSettingEmployees(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("employees");

            return (stringReturn == null ? "yes" == null : stringReturn.equals("yes"));
        } else {
            return false;
        }
    }

    public boolean queryAggregateSettingOriginalsourcenote(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("originalinsourcenote");

            return (stringReturn == null ? "yes" == null : stringReturn.equals("yes"));
        } else {
            return false;
        }
    }

    public boolean queryAggregateSettingOriginaltargetnote(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("originalintargetnote");

            return (stringReturn == null ? "yes" == null : stringReturn.equals("yes"));
        } else {
            return false;
        }
    }

    //only meaningful if queryAggregateSettingIntoseparatecontacts
    public String queryAggregateSettingSeparatecontactsdelimiter(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            if (queryAggregateSettingIntoseparatecontacts(colNum)) {
                NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/separatecontacts");

                Element element = returnFirstElement(dataNodeList);

                return element.getAttribute("delimiter");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //only meaningful if queryAggregateSettingIntoseparatecontacts
    public boolean queryAggregateSettingAutodetectswaps(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            if (queryAggregateSettingIntoseparatecontacts(colNum)) {
                NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/separatecontacts");

                Element element = returnFirstElement(dataNodeList);

                String stringReturn = element.getAttribute("autodetectswaps");

                return (stringReturn == null ? "yes" == null : stringReturn.equals("yes"));
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean changeAggregateSettingDelimiter(Integer colNum, String delimiter) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            element.setAttribute("delimiter", delimiter);

            return true;
        } else {
            return false;
        }

    }

    public boolean changeAggregateSettingNumberofcolumns(Integer colNum, Integer numberofcolumns) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            element.setAttribute("numberofcolumns", numberofcolumns.toString());
            return true;
        } else {
            return false;
        }
    }

    //This will delete corresponding "separatecontacts" subelement!
    public boolean changeAggregateSettingIntoseparatecontactsOff(Integer colNum) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            boolean intoseparatecontacts = false;

            element.setAttribute("intoseparatecontacts", (intoseparatecontacts ? "yes" : "no"));

            dataNodeList = returnXPathNodeList("//columnschema/aggregatedcolumns/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/separatecontacts");

            element.removeChild(returnFirstElement(dataNodeList));
            return true;
        } else {
            return false;
        }
    }

    //This will create corresponding "separatecontacts" subelement
    public boolean changeAggregateSettingIntoseparatecontactsOn(Integer colNum, String delimiter, boolean autodetectswaps) {
        findRoot();
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated
            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            boolean intoseparatecontacts = true;

            element.setAttribute("intoseparatecontacts", (intoseparatecontacts ? "yes" : "no"));

            Element separatecontacts = element.getOwnerDocument().createElement("separatecontacts");
            separatecontacts.setAttribute("delimiter", delimiter);
            separatecontacts.setAttribute("autodetectswaps", (autodetectswaps ? "yes" : "no"));
            element.appendChild(separatecontacts);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeAggregateSettingEmployees(Integer colNum, boolean employees) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            element.setAttribute("employees", (employees ? "yes" : "no"));
            return true;
        } else {
            return false;
        }
    }

    public boolean changeAggregateSettingOriginalsourcenote(Integer colNum, boolean originalinsourcenote) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            element.setAttribute("originalinsourcenote", (originalinsourcenote ? "yes" : "no"));
            return true;
        } else {
            return false;
        }
    }

    public boolean changeAggregateSettingOriginaltargetnote(Integer colNum, boolean originalintargetnote) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns");

            Element element = returnFirstElement(dataNodeList);

            element.setAttribute("originalintargetnote", (originalintargetnote ? "yes" : "no"));
            return true;
        } else {
            return false;
        }
    }

    public boolean changeAggregateSettingSeparatecontactsdelimiter(Integer colNum, String delimiter) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated
            if (queryAggregateSettingIntoseparatecontacts(colNum)) { //only meaningful if queryAggregateSettingIntoseparatecontacts
                NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/separatecontacts");

                Element element = returnFirstElement(dataNodeList);

                element.setAttribute("delimiter", delimiter);

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean changeAggregateSettingAutodetectswaps(Integer colNum, boolean autodetectswaps) {
        if ((isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when isColumnAggregated

            if (queryAggregateSettingIntoseparatecontacts(colNum)) { //only meaningful if queryAggregateSettingIntoseparatecontacts
                NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/separatecontacts");

                Element element = returnFirstElement(dataNodeList);

                element.setAttribute("autodetectswaps", (autodetectswaps ? "yes" : "no"));

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
     *  *     <column number="4" mergedinother="yes" aggregated="no">
     *       <mergein set="1" order="2"/>
     *     </column>
     *     <column number="5" mergedinother="yes" aggregated="no">
     *       <mergein set="1" order="1"/>
     *     </column>
     *     <mergeset number="1">
     *       <delimiter value=","/>
     *       <selectedtype value="Note"/>
     *     </mergeset>
     */
    public boolean columnMergeOn(Integer colNum, Integer mergeiniset, Integer order) {
        findRoot();
        if ((!isColumnAggregated(colNum)) && (!isColumnMergedInOther(colNum))) {    //only meaningful when !isColumnMergedInOther

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]");

            Element theColumnElement = returnFirstElement(dataNodeList);

            Element mergein = theColumnElement.getOwnerDocument().createElement("mergein");
            mergein.setAttribute("set", mergeiniset.toString());
            mergein.setAttribute("order", order.toString());

            theColumnElement.appendChild(mergein);

            theColumnElement.setAttribute("mergedinother", "yes");

            return true;
        } else {
            return false;
        }
    }

    public boolean columnMergeOff(Integer colNum) {
        if ((!isColumnAggregated(colNum)) && (isColumnMergedInOther(colNum))) {    //only meaningful when isColumnMergedInOther

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/mergein");

            Element theMergeinElement = returnFirstElement(dataNodeList);

            Element theColumnElement = (Element) theMergeinElement.getParentNode();

            theColumnElement.removeChild(theMergeinElement);

            theColumnElement.setAttribute("mergedinother", "no");

            return true;
        } else {
            return false;
        }
    }

    public Integer queryMergeSet(Integer colNum) {
        if ((!isColumnAggregated(colNum)) && (isColumnMergedInOther(colNum))) {    //only meaningful when isColumnMergedInOther

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/mergein");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("set");
            Integer intReturn = Integer.parseInt(stringReturn);
            return intReturn;
        } else {
            return null;
        }
    }

    public Integer queryMergeOrder(Integer colNum) {
        if ((!isColumnAggregated(colNum)) && (isColumnMergedInOther(colNum))) {    //only meaningful when isColumnMergedInOther

            NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/mergein");

            Element element = returnFirstElement(dataNodeList);

            String stringReturn = element.getAttribute("order");
            Integer intReturn = Integer.parseInt(stringReturn);
            return intReturn;
        } else {
            return null;
        }
    }

    public boolean createMergeset(Integer num, String newDelimiter) {
        findRoot();
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + num.toString() + "\"]");

        Element mergeset = returnFirstElement(dataNodeList);

        if (mergeset.getNodeName().equals("null")) {
            mergeset = root.getOwnerDocument().createElement("mergeset");
            mergeset.setAttribute("number", num.toString());

            Element delimiter = mergeset.getOwnerDocument().createElement("delimiter");
            delimiter.setAttribute("value", newDelimiter);

            mergeset.appendChild(delimiter);

            root.appendChild(mergeset);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMergeset(Integer num) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + num.toString() + "\"]");

        Element mergeset = returnFirstElement(dataNodeList);
        if (!mergeset.getTagName().equals("null")) {
            HashMap<Integer,Integer> members = getAllMergesetMembers(num);
            for (int i = 1; i<=members.size(); i++){
                columnMergeOff(members.get(i));
            }
            Element mergesetParent = (Element) mergeset.getParentNode();
            mergesetParent.removeChild(mergeset);
            return true;
        } else {
            return false;
        }
    }

    public void setMergesetCandidateType(Integer mergesetNum, String type) {
        findRoot();
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]");

        Element mergeset = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]/candidatetype");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            mergeset.removeChild(deleteElement);
        }

        Element candidatetype = mergeset.getOwnerDocument().createElement("candidatetype");
        candidatetype.setAttribute("value", type);
        
        mergeset.appendChild(candidatetype);
    }

    public void setMergesetSelectedType(Integer mergesetNum, String type) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]");

        Element mergeset = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]/selectedtype");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            mergeset.removeChild(deleteElement);
        }

        Element selectedtype = mergeset.getOwnerDocument().createElement("selectedtype");
        selectedtype.setAttribute("value", type);
        
        mergeset.appendChild(selectedtype);
    }

    public void changeMergesetDelimiter(Integer mergesetNum, String delim) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]");

        Element mergeset = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]/delimiter");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            mergeset.removeChild(deleteElement);
        }

        Element selectedtype = mergeset.getOwnerDocument().createElement("delimiter");
        selectedtype.setAttribute("value", delim);

        mergeset.appendChild(selectedtype);
    }

    public String queryMergesetCandidateType(Integer mergesetNum) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]/candidatetype");

        Element candidatetype = returnFirstElement(dataNodeList);

        return candidatetype.getAttribute("value");

    }

    public String queryMergesetSelectedType(Integer mergesetNum) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]/selectedtype");

        Element candidatetype = returnFirstElement(dataNodeList);

        return candidatetype.getAttribute("value");
    }

    public String queryMergesetDelimiter(Integer mergesetNum) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset[@number=\"" + mergesetNum.toString() + "\"]/delimiter");

        Element delimiter = returnFirstElement(dataNodeList);

        return delimiter.getAttribute("value");
    }

    public void setCandidateType(Integer colNum, String type) {
        findRoot();
        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]");

        Element columnElement = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/candidatetype");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            columnElement.removeChild(deleteElement);
        }

        Element candidatetypeElement = columnElement.getOwnerDocument().createElement("candidatetype");
        candidatetypeElement.setAttribute("value", type);

        columnElement.appendChild(candidatetypeElement);
    }

    public void setSelectedtypeType(Integer colNum, String type) {
        findRoot();
        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]");

        Element columnElement = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/selectedtype");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            columnElement.removeChild(deleteElement);
        }

        Element selectedtypeElement = columnElement.getOwnerDocument().createElement("selectedtype");
        selectedtypeElement.setAttribute("value", type);

        columnElement.appendChild(selectedtypeElement);
    }

    public String queryCandidateType(Integer colNum) {

        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/candidatetype");

        Element columnElement = returnFirstElement(dataNodeList);

        return columnElement.getAttribute("value");
    }

    public String querySelectedtypeType(Integer colNum) {

        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/selectedtype");

        Element columnElement = returnFirstElement(dataNodeList);

        return columnElement.getAttribute("value");
    }

    //colNum - number of column with attribute "aggregated"
    //colNum2 - number of the //columnschema/column/aggregatedcolumns/column column
    public void setAggregatedCandidateType(Integer colNum, Integer colNum2, String type) {
        findRoot();
        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/column[@number=\"" + colNum2.toString() + "\"]");

        Element aggregatedColumnElement = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/column[@number=\"" + colNum2.toString() + "\"]/candidatetype");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            aggregatedColumnElement.removeChild(deleteElement);
        }

        Element candidatetypeElement = aggregatedColumnElement.getOwnerDocument().createElement("candidatetype");
        candidatetypeElement.setAttribute("value", type);

        aggregatedColumnElement.appendChild(candidatetypeElement);
    }

    public void setAggregatedSelectedtypeType(Integer colNum, Integer colNum2, String type) {
        findRoot();
        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/column[@number=\"" + colNum2.toString() + "\"]");

        Element aggregatedColumnElement = returnFirstElement(dataNodeList);

        NodeList deleteNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/column[@number=\"" + colNum2.toString() + "\"]/selectedtype");

        Element deleteElement = returnFirstElement(deleteNodeList);

        if (!deleteElement.getTagName().equals("null")) {
            aggregatedColumnElement.removeChild(deleteElement);
        }

        Element selectedtypeElement = aggregatedColumnElement.getOwnerDocument().createElement("selectedtype");
        selectedtypeElement.setAttribute("value", type);

        aggregatedColumnElement.appendChild(selectedtypeElement);
    }

    public String queryAggregatedCandidateType(Integer colNum, Integer colNum2) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/column[@number=\"" + colNum2.toString() + "\"]/candidatetype");

        Element element = returnFirstElement(dataNodeList);

        return element.getAttribute("value");
    }

    public String queryAggregatedSelectedtypeType(Integer colNum, Integer colNum2) {
        NodeList dataNodeList = returnXPathNodeList("//columnschema/column[@number=\"" + colNum.toString() + "\"]/aggregatedcolumns/column[@number=\"" + colNum2.toString() + "\"]/selectedtype");

        Element element = returnFirstElement(dataNodeList);

        return element.getAttribute("value");
    }
//String queryCandidateType
//String querySelectedType
//Aggreg. var.
//colnum colnum
//2x
//createMergeset(num, del, typ
//setMergesetDelimiter
//setMergesetType
//deleteMergeset
//queryMergesetDelimiter
//queryMergesetType

    public ArrayList<Integer> getAllMergesets() {
        ArrayList<Integer> allMergesetsList = new ArrayList<Integer>();
                   NodeList dataNodeList = returnXPathNodeList("//columnschema/mergeset");
                   for (Integer i = 0; i<dataNodeList.getLength(); i++) {
                       if (dataNodeList.item(i) instanceof Element) {
                           Element thisMergeSet = (Element) dataNodeList.item(i);
                           allMergesetsList.add(Integer.parseInt(thisMergeSet.getAttribute("number")));
                       }
                   }
                   return allMergesetsList;
    }

    //<order, columnNumber>
   public HashMap<Integer, Integer> getAllMergesetMembers(Integer mergeset) {
        //ArrayList<Integer> allMembersList = new ArrayList<Integer>();
        //ArrayList<Integer> allOrderList = new ArrayList<Integer>();
                HashMap<Integer, Integer> sortKeyHashMap = new HashMap<Integer, Integer>();

                   NodeList mergeinNodeList = returnXPathNodeList("//columnschema/column/mergein[@set=\""+mergeset.toString()+"\"]");
                   for (Integer i = 0; i<mergeinNodeList.getLength(); i++) {
                       if (mergeinNodeList.item(i) instanceof Element) {
                           Element thisMergein = (Element) mergeinNodeList.item(i);
                           Element thisColumn = (Element) thisMergein.getParentNode();
                           Integer columnOrder = Integer.parseInt(thisMergein.getAttribute("order"));
                           Integer columnNumber = Integer.parseInt(thisColumn.getAttribute("number"));
                           //allMembersList.add(columnNumber);
                           //allOrderList.add(columnOrder);
                           sortKeyHashMap.put(columnOrder, columnNumber);
                       }
                   }

    //SortedSet<Integer> sortedOrdersSet= new TreeSet<Integer>(sortKeyHashMap.keySet());

    //Iterator<String> it = sortedset.iterator();

    //while (it.hasNext()) {
    //  System.out.println (it.next());
    //}

                   return sortKeyHashMap;
    }

    //<editor-fold defaultstate="collapsed" desc="added by Martin B.">
    public int getColumnCount(){
        NodeList columnList = returnXPathNodeList("//columnschema/column");
        return columnList.getLength();//columnCount;
    }

    public boolean isTypeInColumnSchema(VCFTypesEnum type){
        NodeList candidateTypeList = returnXPathNodeList("//candidatetype");
        for (int i= 0; i<candidateTypeList.getLength(); i++){
            Element candidateType = (Element) candidateTypeList.item(i);
            String value = candidateType.getAttribute("value");
            if (type.toString().equals(value)){
                return true;
            }
        }
         NodeList selectedTypeList = returnXPathNodeList("//selectedtype");
        for (int i= 0; i<selectedTypeList.getLength(); i++){
            Element selectedType = (Element) selectedTypeList.item(i);
            String value = selectedType.getAttribute("value");
            if (type.toString().equals(value)){
                return true;
            }
        }

        return false;
    }

    public void addColumn(){
        Element newColumn = root.getOwnerDocument().createElement("column");
        int newColumnNumber = getColumnCount();
        newColumn.setAttribute("number", String.valueOf(newColumnNumber));
        newColumn.setAttribute("mergedinother", "no");
        newColumn.setAttribute("aggregated", "no");
        root.appendChild(newColumn);
        setSelectedtypeType(newColumnNumber, VCFTypesEnum.Note.toString());
        setCandidateType(newColumnNumber, VCFTypesEnum.Note.toString());
    }

    @Override
    public String toString(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = null;
        try {
            trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(stream));
        } catch (TransformerException ex) {
            Logger.getLogger(InternalDocColumnSchemaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stream.toString();
    }

    //</editor-fold>

}


