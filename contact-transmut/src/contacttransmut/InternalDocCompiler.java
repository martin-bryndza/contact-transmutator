/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author jakub svoboda
 */
public class InternalDocCompiler implements InternalDoc2CompiledDoc {

    private Document docCompiled; //the output internal XML with fields compatible with VCF
    private Document docRawReadTextDoc; //the internal XML produced by ReadCSV
    private Document detectedErrors; //not-valid contacts should be redirected here, details will be available later, //TODO!
    private InternalDocColumnSchema docColumnSchema; //InternalDocColumnSchemaImpl for the ReadCSV-produced document (must be valid)
    private Element rootDocCompiled;
    private Element rootDocRawReadTextDoc;
    private Element rootDetectedErrors;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private VCFHelper outFormValidtr;
    private boolean errorsDetected;

    public InternalDocCompiler(Document newDocRawReadTextDoc, InternalDocColumnSchema newDocColumnSchema) {
        System.err.println("blabla5: called");
        docRawReadTextDoc = newDocRawReadTextDoc;
        docColumnSchema = newDocColumnSchema;
        outFormValidtr = new VCFHelperImpl(docColumnSchema);

        // create new internal XML DOM for processing the data
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        docCompiled = db.newDocument(); //this is internal XML DOM we use to process the data
        detectedErrors = db.newDocument(); //this should have the same structure as docCompiled, //TODO!
        //temporary note: detectedErrors will not be used, check and clean up later
        errorsDetected = false;
        Element root0 = docCompiled.createElement("root");
        docCompiled.appendChild(root0);
        rootDocCompiled = docCompiled.createElement("contacts");
        root0.appendChild(rootDocCompiled);

        Element rootERR0 = detectedErrors.createElement("root");
        detectedErrors.appendChild(rootERR0);
        rootDetectedErrors = detectedErrors.createElement("contacts");
        rootERR0.appendChild(rootDetectedErrors);

        NodeList docRawReadTextDocNodeList = returnXPathNodeList(docRawReadTextDoc, "/root");
        rootDocRawReadTextDoc = returnFirstElement(docRawReadTextDocNodeList);
    }

    private NodeList returnXPathNodeList(Document document, String newXpath) {
        XPath xpath = XPathFactory.newInstance().newXPath(); //new xpath
        NodeList dataNodeList = null;
        try {
            dataNodeList = (NodeList) xpath.evaluate(newXpath, document, XPathConstants.NODESET); //select the nodes
        } catch (XPathExpressionException ex) {
            Logger.getLogger(InternalDocColumnSchemaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataNodeList;
    }

    private Element returnFirstElement(NodeList dataNodeList) {
        Element retElement = null;
        for (int i = 0; i < dataNodeList.getLength(); i++) {
            if (dataNodeList.item(i) instanceof Element) {
                retElement = (Element) dataNodeList.item(i);
                break;
            }
        }
        return retElement;
    }

    /*
     * CompiledDoc format is as follows:
     * <root>
     * <contacts>
     * <contact number=0>
     * <TYPE1>data</TYPE1> //firstname, surname, ... whatever VCF accepts
     * <TYPE2>data</TYPE2> //e-mail, phone... whatever that can have multiple records of the same type
     * <TYPE2>data</TYPE2>
     * </contact>
     * </contacts>
     * </root>
     */
    private void writeField(Integer contactNumber, String dataType, String contents, boolean canHaveMultipleRecordsOfSameDataType) throws DOMException {
        System.err.println("short writeField called: Integer contactNumber="+contactNumber+", String dataType="+dataType+", String contents="+contents+", boolean canHaveMultipleRecordsOfSameDataType="+(canHaveMultipleRecordsOfSameDataType?"true":"false"));
        writeField(contactNumber, dataType, contents, canHaveMultipleRecordsOfSameDataType, false);
    }

    private void writeField(Integer contactNumber, String dataType, String contents, boolean canHaveMultipleRecordsOfSameDataType, boolean thisIsOriginalNoteEmbedding) throws DOMException {
       System.err.println("long writeField called: Integer contactNumber="+contactNumber+", String dataType="+dataType+", String contents="+contents+", boolean canHaveMultipleRecordsOfSameDataType="+(canHaveMultipleRecordsOfSameDataType?"true":"false")+", boolean thisIsOriginalNoteEmbedding="+(thisIsOriginalNoteEmbedding?"true":"false"));
         Element field = null;
        NodeList nodes = returnXPathNodeList(docCompiled, "//contact[@number=\"" + contactNumber + "\"]");
        String writeContents = contents;
        if (nodes.getLength() == 0) { //create
            System.err.println("create hive");
            Element newContact = docCompiled.createElement("contact");
            newContact.setAttribute("number", contactNumber.toString());
            field = docCompiled.createElement(dataType);
            newContact.appendChild(field);
            rootDocCompiled.appendChild(newContact);
            
        } else { //update
            System.err.println("update hive");
            Element contact = returnFirstElement(nodes);
            NodeList checkNodes = contact.getElementsByTagName(dataType);
            if (thisIsOriginalNoteEmbedding) {
                if (checkNodes.getLength() == 0) {
                    field = docCompiled.createElement(dataType);
                } else {
                    field = returnFirstElement(checkNodes);
                    writeContents = field.getTextContent() + "; " + writeContents;
                }
                contact.appendChild(field);
            } else {
                if (!canHaveMultipleRecordsOfSameDataType) {
                    if (checkNodes.getLength() != 0) {
                        throw new DOMException(DOMException.NO_DATA_ALLOWED_ERR, "canHaveMultipleRecordsOfSameDataType inconsistency");
                    }
                    field = docCompiled.createElement(dataType);
                } else {
                    field = docCompiled.createElement(dataType);
                }
                contact.appendChild(field);
            }
        }

        field.setTextContent(writeContents);
    }

    public void compile() {
        // docColumnSchema;
        //rootDocRawReadTextDoc.getChildNodes();
        NodeList rawContacts = returnXPathNodeList(docRawReadTextDoc, "//contact/uncategorized");

     System.err.println("blabla4: " + docColumnSchema.toString());
     System.err.println("blabla6: " + docRawReadTextDoc.toString());
//TODO jakub svoboda tidy up



                 System.err.println("blabla2: " + rawContacts.toString());
                 System.err.println("blabla3: " + (rawContacts.getLength()));

        for (int rawContCount = 0; rawContCount < rawContacts.getLength(); rawContCount++) {
            Element currentContactElement;
            if (rawContacts.item(rawContCount) instanceof Element) {
                 System.err.println("blabla0: " + rawContacts.item(rawContCount).toString());
                Element thisRawContact0 = (Element) rawContacts.item(rawContCount);
                 System.err.println("blabla1: " + thisRawContact0.toString());
               //Element thisRawContact = returnFirstElement(thisRawContact0.getElementsByTagName("uncategorized"));
                Element thisRawContact = thisRawContact0; //todo jakub svoboda: check and tidy up
                 currentContactElement = thisRawContact;
                NodeList thisRawContactContents = thisRawContact.getChildNodes();
                boolean processMergeSets = false;
                ArrayList<Integer> columnsInMergeset = new ArrayList<Integer>();
                ArrayList<Integer> usedMergesets = new ArrayList<Integer>();
                for (int contentsCount = 0; contentsCount < thisRawContactContents.getLength(); contentsCount++) {
                    Element contentsElement = (Element) thisRawContactContents.item(contentsCount);
                    Integer counter = Integer.valueOf(contentsElement.getAttribute("counter"));
                    String contents = contentsElement.getTextContent();
                    //current row number is $rawContCount
                    //current field number is $counter
                    //current contents is $contents
                    //current element is $contentsElement

                    if (!(contents.trim().equals(""))) {
                        if (docColumnSchema.isColumnAggregated(counter)) { //aggregated column...
                            // ============== AGGREGATED COLUMN =============
                            Integer numOfColumns = docColumnSchema.queryAggregateSettingNumberofcolumns(counter);
                            if (numOfColumns == null) {
                                System.err.println("InternalDocCompiler-err001: AGGREGATED: numOfColumns == null");
                                //log event
                            }
                            if (docColumnSchema.queryAggregateSettingIntoseparatecontacts(counter)) { //generate separate contacts
                                if (docColumnSchema.queryAggregateSettingAutodetectswaps(counter)) { //auto-detecting
                                    // ******* AGGREGATED, INTO SEPARATE, AUTO-DETECTION *******
                                System.err.println("InternalDocCompiler-err002: NOT IMPLEMENTED");
                                //log event
                                    //TODO!
                                    //Not implemented!
                                } else { //no auto-detection!
                                    // ******* AGGREGATED, INTO SEPARATE, RIGID (without a-d) ******
                                    Scanner contentsScanner = new Scanner(contents);
                                    String delimiter1 = docColumnSchema.queryAggregateSettingDelimiter(counter);
                                    String delimiter2 = docColumnSchema.queryAggregateSettingSeparatecontactsdelimiter(counter);
                                    if (delimiter1 == null) {
                                System.err.println("InternalDocCompiler-err003: AGGREGATED: delimiter1 == null");
                                //log event
                                    }
                                    if (delimiter2 == null) {
                                System.err.println("InternalDocCompiler-err004: AGGREGATED: delimiter2 == null");
                                //log event
                                    }
                                    contentsScanner.useDelimiter(delimiter2);
                                    if (delimiter1.equals(delimiter2)) { //the same delimiters, we’ll be COUNTING based on number of columns here
                                        // ******* AGGREGATED, INTO SEPARATE, RIGID (without a-d), SIMILAR DELIMITERS ******
                                        Integer columnCounter = 0;
                                        ArrayList<HashMap<String, String>> arrayListOfHashmapsSeparateContacts = new ArrayList<HashMap<String, String>>();
                                        HashMap<String, String> attributesHashmap = new HashMap<String, String>();

                                        while (contentsScanner.hasNext()) {

                                            String thisAggrFieldType = docColumnSchema.queryAggregatedCandidateType(counter, columnCounter);
                                            if (thisAggrFieldType == null) {
                                                thisAggrFieldType = docColumnSchema.queryAggregatedSelectedtypeType(counter, columnCounter);
                                            }
                                            if (thisAggrFieldType == null) {
                                 System.err.println("InternalDocCompiler-err005: AGGREGATED: thisAggrFieldType == null");
                                //log event
                                           }
                                            String thisAggrContents = contentsScanner.next();

                                            attributesHashmap.put(thisAggrFieldType, thisAggrContents);

                                            if (columnCounter >= numOfColumns) {
                                                columnCounter = 0;
                                                arrayListOfHashmapsSeparateContacts.add(attributesHashmap);
                                                attributesHashmap = new HashMap<String, String>();
                                            } else {
                                                columnCounter++;
                                            }
                                        }

                                        if (columnCounter != 0) {
                                            arrayListOfHashmapsSeparateContacts.add(attributesHashmap);
                                            attributesHashmap = new HashMap<String, String>();
                                        }

                                        //and now, create all the contacts
                                        //1) the original contact
                                        if (docColumnSchema.queryAggregateSettingOriginalsourcenote(counter)) {
                                            try {
                                                String vcfNoteType = VCFTypesEnum.Note.toString(); 
                                                writeField(rawContCount, vcfNoteType, contents, false, true);
                                            } catch (DOMException e) {
                                  System.err.println("InternalDocCompiler-err006: creating: writeField(rawContCount, vcfNoteType, contents, false, true) failed");
                                //log event
                                           }
                                        }
                                        //2) write out all the new contacts
                                        Iterator<HashMap<String, String>> allcontactsiterator = arrayListOfHashmapsSeparateContacts.iterator();
                                        while (allcontactsiterator.hasNext()) {
                                            attributesHashmap = allcontactsiterator.next();
                                            Iterator attribIter = attributesHashmap.entrySet().iterator();
                                            Element newContact = docCompiled.createElement("contact");
                                            while (attribIter.hasNext()) {
                                                Map.Entry attribPair = (Map.Entry) attribIter.next();
                                                String fieldType = (String) attribPair.getKey();
                                                String fieldContents = (String) attribPair.getValue();
                                                if (!(outFormValidtr.vcfCanHaveMultipleInstances(fieldType))) {
                                                    if (newContact.getElementsByTagName(fieldType).getLength() != 0) {
                                   System.err.println("InternalDocCompiler-err007: creating: (newContact.getElementsByTagName(fieldType).getLength() != 0)");
                                //log event
                                                   }
                                                }
                                                Element field = docCompiled.createElement(fieldType);
                                                field.setTextContent(fieldContents);
                                                newContact.appendChild(field);
                                            }
                                            if (docColumnSchema.queryAggregateSettingOriginaltargetnote(counter)) {
                                                String vcfNoteType = VCFTypesEnum.Note.toString();
                                                if (newContact.getElementsByTagName(vcfNoteType).getLength() != 0) {
                                                    newContact.getElementsByTagName(vcfNoteType).item(0).setTextContent(newContact.getElementsByTagName(vcfNoteType).item(0).getTextContent() + "; " + contents);
                                                } else {
                                                    newContact.getElementsByTagName(vcfNoteType).item(0).setTextContent(contents);
                                                }
                                            }
                                            rootDocCompiled.appendChild(newContact);
                                        }

                                    } else { //different delimiters: there can be less columns than the "number of columns" attribute because contacts are delimited with different delimiter
                                        // ******* AGGREGATED, INTO SEPARATE, RIGID (without a-d), DIFFERENT DELIMITERS ******

                                        Integer columnCounter = 0;
                                        ArrayList<HashMap<String, String>> arrayListOfHashmapsSeparateContacts = new ArrayList<HashMap<String, String>>();
                                        HashMap<String, String> attributesHashmap = new HashMap<String, String>();

                                        while (contentsScanner.hasNext()) {

                                            String thisContactContents = contentsScanner.next();

                                            Scanner contentsScanner2 = new Scanner(thisContactContents);

                                            boolean moreThanOne = false;
                                            String writeContents = null;
                                            String writeType = null;
                                            columnCounter = 0;
                                            while (contentsScanner2.hasNext()) {

                                                String thisAggrFieldType = docColumnSchema.queryAggregatedCandidateType(counter, columnCounter);
                                                if (thisAggrFieldType == null) {
                                                    thisAggrFieldType = docColumnSchema.queryAggregatedSelectedtypeType(counter, columnCounter);
                                                }
                                                if (thisAggrFieldType == null) {
                                   System.err.println("InternalDocCompiler-err008: AGGREGATED2: thisAggrFieldType == null");
                                //log event
                                               }
                                                String thisAggrContents = contentsScanner2.next();

                                                if (columnCounter < numOfColumns - 1) {

                                                    attributesHashmap.put(thisAggrFieldType, thisAggrContents);

                                                    columnCounter++;
                                                } else {
                                                    writeType = thisAggrFieldType;
                                                    if (!moreThanOne) {
                                                        writeContents = thisAggrContents;
                                                        moreThanOne = true;
                                                    } else {
                                                        writeContents = writeContents + delimiter1 + thisAggrContents;
                                                    }
                                                }
                                            }
                                            if (writeContents != null) {
                                                attributesHashmap.put(writeType, writeContents);
                                            }
                                            if (columnCounter != 0) {
                                                arrayListOfHashmapsSeparateContacts.add(attributesHashmap);
                                                attributesHashmap = new HashMap<String, String>();
                                            }
                                        }



                                        //and now, create all the contacts
                                        //1) the original contact
                                        if (docColumnSchema.queryAggregateSettingOriginalsourcenote(counter)) {
                                            try {
                                                String vcfNoteType = VCFTypesEnum.Note.toString();
                                                writeField(rawContCount, vcfNoteType, contents, false, true);
                                            } catch (DOMException e) {
                                    System.err.println("InternalDocCompiler-err009: creating2: writeField(rawContCount, vcfNoteType, contents, false, true) failed");
                                //log event
                                            }
                                        }
                                        //2) write out all the new contacts
                                        Iterator<HashMap<String, String>> allcontactsiterator = arrayListOfHashmapsSeparateContacts.iterator();
                                        while (allcontactsiterator.hasNext()) {
                                            attributesHashmap = allcontactsiterator.next();
                                            Iterator attribIter = attributesHashmap.entrySet().iterator();
                                            Element newContact = docCompiled.createElement("contact");
                                            while (attribIter.hasNext()) {
                                                Map.Entry attribPair = (Map.Entry) attribIter.next();
                                                String fieldType = (String) attribPair.getKey();
                                                String fieldContents = (String) attribPair.getValue();
                                                if (!(outFormValidtr.vcfCanHaveMultipleInstances(fieldType))) {
                                                    if (newContact.getElementsByTagName(fieldType).getLength() != 0) {
                                     System.err.println("InternalDocCompiler-err010: creating2: (newContact.getElementsByTagName(fieldType).getLength() != 0)");
                                //log event
                                                   }
                                                }
                                                Element field = docCompiled.createElement(fieldType);
                                                field.setTextContent(fieldContents);
                                                newContact.appendChild(field);
                                            }
                                            if (docColumnSchema.queryAggregateSettingOriginaltargetnote(counter)) {
                                                String vcfNoteType = VCFTypesEnum.Note.toString();
                                                if (newContact.getElementsByTagName(vcfNoteType).getLength() != 0) {
                                                    newContact.getElementsByTagName(vcfNoteType).item(0).setTextContent(newContact.getElementsByTagName(vcfNoteType).item(0).getTextContent() + "; " + contents);
                                                } else {
                                                    newContact.getElementsByTagName(vcfNoteType).item(0).setTextContent(contents);
                                                }
                                            }
                                            rootDocCompiled.appendChild(newContact);
                                        }
                                    }

                                }
                            } else { //only make new columns
                                // ******** AGGREGATED, ONLY SPLIT COLUMNS *********
                                Scanner contentsScanner = new Scanner(contents);
                                String delimiter1 = docColumnSchema.queryAggregateSettingDelimiter(counter);
                                if (delimiter1 == null) {
                                    System.err.println("InternalDocCompiler-err011: AGGREGATED3: delimiter1 == null");
                                //log event
                                  }
                                contentsScanner.useDelimiter(delimiter1);
                                String writeContents = null;
                                Integer columnAggrCounter = 0;
                                boolean moreThanOne = false;
                                while (contentsScanner.hasNext()) {
                                    if (columnAggrCounter < (numOfColumns - 1)) { //write directly to a new field
                                        String thisAggrFieldType = docColumnSchema.queryAggregatedCandidateType(counter, columnAggrCounter);
                                        if (thisAggrFieldType == null) {
                                            thisAggrFieldType = docColumnSchema.queryAggregatedSelectedtypeType(counter, columnAggrCounter);
                                        }
                                        if (thisAggrFieldType == null) {
                                    System.err.println("InternalDocCompiler-err012: thisAggrFieldType == null");
                                //log event
                                        }
                                        String thisAggrContents = contentsScanner.next();
                                        try {
                                            writeField(rawContCount, thisAggrFieldType, thisAggrContents, outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                        } catch (DOMException e) {
                                    System.err.println("InternalDocCompiler-err013: writeField(rawContCount, thisAggrFieldType, thisAggrContents, outFormValidtr.vcfCanHaveMultipleInstances(counter)) failed");
                                //log event
                                        }
                                        columnAggrCounter++;
                                    } else { //commit to writeContents
                                        writeContents += contentsScanner.next();
                                        if (moreThanOne) { //if there are any overflown data
                                            writeContents += delimiter1;
                                        }
                                        moreThanOne = true;
                                    }
                                }
                                if (writeContents != null) { //commit overflown data if any
                                    String thisAggrFieldType = docColumnSchema.queryAggregatedCandidateType(counter, columnAggrCounter);
                                    if (thisAggrFieldType == null) {
                                        thisAggrFieldType = docColumnSchema.queryAggregatedSelectedtypeType(counter, columnAggrCounter);
                                    }
                                    if (thisAggrFieldType == null) {
                                     System.err.println("InternalDocCompiler-err014: thisAggrFieldType == null");
                                //log event
                                   }
                                    try {
                                        writeField(rawContCount, thisAggrFieldType, writeContents, outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                    } catch (DOMException e) {
                                      System.err.println("InternalDocCompiler-err015: writeField(rawContCount, thisAggrFieldType, writeContents, outFormValidtr.vcfCanHaveMultipleInstances(counter)) failed");
                                //log event
                                    }
                                }
                            }
                        } else {
                            if (docColumnSchema.isColumnMergedInOther(counter)) { //belongs in mergeset...
                                // ============= COLUMN IN MERGESET ===============
                                Integer belongsInMergeset = docColumnSchema.queryMergeSet(counter);
                                if (belongsInMergeset == null) {
                                       System.err.println("InternalDocCompiler-err016: MERGESET: belongsInMergeset == null");
                                //log event
                               }
                                if (!(docColumnSchema.getAllMergesets().contains(belongsInMergeset))) {
                                       System.err.println("InternalDocCompiler-err017: MERGESET: (!(docColumnSchema.getAllMergesets().contains(belongsInMergeset)))");
                                //log event
                                }
                                System.err.println("compiler info: mergeset added");
                                processMergeSets = true;
                                columnsInMergeset.add(counter); //note the column in a mergeset
                                if (!(usedMergesets.contains(belongsInMergeset))) { //if not already noted...
                                    usedMergesets.add(belongsInMergeset); //note the parent mergeset
                                }
                                //OKAY, we have tested the column that it belongs in an existing mergeset
                                //will go through all mergesets later, let the column be at this moment
                                //TODO: jakub svoboda check corectness and tidy up
                            } else { //normal column...                                //normal column...
                                // =========== NORMAL COLUMN ===========
                                String type = docColumnSchema.querySelectedtypeType(counter);
                                if (type == null) {
                                    type = docColumnSchema.queryCandidateType(counter);
                                }
                                if (type == null) {
                                      System.err.println("InternalDocCompiler-err018: MERGESET: type == null");
                                //log event
                                 }

                                try {
                                    writeField(rawContCount, type, contents, outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                } catch (DOMException e) {
                                      System.err.println("InternalDocCompiler-err019: MERGESET: writeField(rawContCount, type, contents, outFormValidtr.vcfCanHaveMultipleInstances(counter)) failed");
                                       System.err.println("exception=" + e);
                                      System.err.println("rawContCount=" + rawContCount);
                                       System.err.println("type="+ type);
                                       System.err.println("contents="+contents);
                                       System.err.println("outFormValidtr.vcfCanHaveMultipleInstances(counter)="+outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                               //log event
                                 }
                            }
                        }
                    } else { //blank field
                        //dont want to do anything here
                    }

                }
                // ********** PROCESS MERGESETS HERE ***********
                System.err.println("InternalDocCompiler-info031: process mergesets here");
                if (processMergeSets) { //there are mergesets to process
                    processMergeSets = false;
               System.err.println("InternalDocCompiler-info032: there are mergesets to process");

                    ArrayList<Integer> allMergesets = docColumnSchema.getAllMergesets();
                    Iterator<Integer> usedMergesetsIter = usedMergesets.iterator();
                    while (usedMergesetsIter.hasNext()) {
            System.err.println("InternalDocCompiler-info034: while (usedMergesetsIter.hasNext())");

                        Integer thisMergeset = usedMergesetsIter.next();
                        HashMap<Integer, Integer> allMergesetMembers = docColumnSchema.getAllMergesetMembers(thisMergeset);
                        SortedSet<Integer> sortedMembersSet = new TreeSet<Integer>(allMergesetMembers.keySet());
                        Iterator<Integer> membersIter = sortedMembersSet.iterator();
                        String mergesetValue = "";
                        boolean firstEntry = true;
                        while (membersIter.hasNext()) {
                   System.err.println("InternalDocCompiler-info035: while (membersIter.hasNext())");

                            Integer attribPairKey = membersIter.next(); //order of column in mergeset
                            Integer attribPairValue = allMergesetMembers.get(attribPairKey); //number of the column
                            //Map.Entry attribPair = (Map.Entry) membersIter.next();
                     System.err.println("attribPairKey="+ attribPairKey + " attribPairValue="+attribPairValue);

                            //attribPair.getKey();
                            //attribPair.getValue();
                            if (columnsInMergeset.contains((Integer) attribPairValue)) {
                   System.err.println("if (columnsInMergeset.contains((Integer) attribPairValue="+attribPairValue);

                                String contentsOfField = null;
                                NodeList childsOfContact = currentContactElement.getChildNodes();
                                for (Integer i = 0; i < childsOfContact.getLength(); i++) {
                                    if (childsOfContact.item(i) instanceof Element) {
                                        Element thisField = (Element) childsOfContact.item(i);
                                        if (Integer.parseInt(thisField.getAttribute("counter")) == attribPairValue) {
                                            contentsOfField = thisField.getTextContent();
                                            break;
                                        }
                                    }
                                }
                  System.err.println("contentsOfField="+contentsOfField+", firstentry:"+(firstEntry?"true":"false"));

                                if (contentsOfField != null) {

                                    if (firstEntry) {
                                        mergesetValue = contentsOfField;
                                        firstEntry = false;
                                    } else {
                                        mergesetValue = mergesetValue + docColumnSchema.queryMergesetDelimiter(thisMergeset) + " " + contentsOfField;
                                    }
                                }
                  System.err.println("mergesetValue="+mergesetValue);

                            }
                        }

                        //write mergeset into contact
                   System.err.println("InternalDocCompiler-info036: write mergeset into contact, thisMergeset="+thisMergeset);
                        String type = docColumnSchema.queryMergesetSelectedType(thisMergeset);
                        if (type == null) {
                            type = docColumnSchema.queryMergesetCandidateType(thisMergeset);
                        }
                        if (type == null) {
                                      System.err.println("InternalDocCompiler-err020: WRITE MERGESET: type == null");
                                //log event
                        }

                        try {
                            writeField(rawContCount, type, mergesetValue, outFormValidtr.vcfCanHaveMultipleInstances(type));
                        } catch (DOMException e) {
                                        System.err.println("InternalDocCompiler-err021: WRITE MERGESET: writeField(rawContCount, type, mergesetValue, outFormValidtr.vcfCanHaveMultipleInstances(type)) failed");
                                //log event
                      }
                    }
                }

                //check if FN is present
                   System.err.println("InternalDocCompiler-info041: check if FN is present");
               NodeList nodes = currentContactElement.getElementsByTagName(VCFTypesEnum.Formatted_Name.toString());
                if (nodes.getLength() == 0) {
                    nodes = currentContactElement.getElementsByTagName(VCFTypesEnum.Name.toString());
                    if (nodes.getLength() == 0) {
                        nodes = currentContactElement.getElementsByTagName(VCFTypesEnum.Nickname.toString());
                        if (nodes.getLength() == 0) {
                            nodes = currentContactElement.getElementsByTagName(VCFTypesEnum.Organization_Name_or_Organizational_unit.toString());
                            if (nodes.getLength() == 0) {
                                nodes = currentContactElement.getElementsByTagName(VCFTypesEnum.Organization_Name_or_Organizational_unit_work.toString());
                                if (nodes.getLength() == 0) {
                                    nodes = currentContactElement.getElementsByTagName(VCFTypesEnum.Organization_Name_or_Organizational_unit_home.toString());
                                }
                            }
                        }
                    }
                    if (((nodes.getLength() != 0) && ((nodes.item(0)) instanceof Element)) && (((Element) nodes.item(0)).getTextContent().trim() == null ? "" != null : !((Element) nodes.item(0)).getTextContent().trim().equals(""))) {
                   System.err.println("InternalDocCompiler-info042: create the FN, otherwise ignore it...");
                        //create the FN, otherwise ignore it...
                        Element fn = docCompiled.createElement(VCFTypesEnum.Formatted_Name.toString());
                        fn.setTextContent(((Element) nodes.item(0)).getTextContent().trim());
                        currentContactElement.appendChild(fn);
                    }
                }

            }
        }


    }

    public Document getCompiledValidContacts() {
                                        System.err.println("InternalDocCompiler-info022: getCompiledValidContacts called, returning docCompiled");
                                //log event

                                        TransformerFactory tfx = TransformerFactory.newInstance();
            Transformer transx = null;
        try {
            transx = tfx.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(InternalDocCompiler.class.getName()).log(Level.SEVERE, null, ex);
             System.err.println("errx1");
        }
        try {
            transx.transform(new DOMSource(docCompiled), new StreamResult(System.err));
        } catch (TransformerException ex) {
            Logger.getLogger(InternalDocCompiler.class.getName()).log(Level.SEVERE, null, ex);
                         System.err.println("errx2");
        }

                                      
                                        return docCompiled;
    }

    public Document getCompiledInvalidContacts() {
                                         System.err.println("InternalDocCompiler-err023: DO NOT USE THIS METHOD! - getCompiledInvalidContacts called");
                                //log event
      return null;
    }

    public boolean compileErrorsDetected() {
                                          System.err.println("InternalDocCompiler-err024: DO NOT USE THIS METHOD! - compileErrorsDetected called");
                               //log event
       return true;
    }
}
