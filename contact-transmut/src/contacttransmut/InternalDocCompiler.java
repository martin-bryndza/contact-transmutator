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

    InternalDocCompiler(Document newDocRawReadTextDoc, InternalDocColumnSchema newDocColumnSchema) {
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
        writeField(contactNumber, dataType, contents, canHaveMultipleRecordsOfSameDataType, false);
    }

    private void writeField(Integer contactNumber, String dataType, String contents, boolean canHaveMultipleRecordsOfSameDataType, boolean thisIsOriginalNoteEmbedding) throws DOMException {
        Element field = null;
        NodeList nodes = returnXPathNodeList(docCompiled, "//contact[@number=\"" + contactNumber + "\"]");
        String writeContents = contents;
        if (nodes.getLength() == 0) { //create
            Element newContact = docCompiled.createElement("contact");
            newContact.setAttribute("number", contactNumber.toString());

            newContact.appendChild(field);
            docCompiled.appendChild(newContact);
            field = docCompiled.createElement(dataType);
        } else { //update
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

        for (int rawContCount = 0; rawContCount < rawContacts.getLength(); rawContCount++) {
            Element currentContactElement;
            if (rawContacts.item(rawContCount) instanceof Element) {
                Element thisRawContact0 = (Element) rawContacts.item(rawContCount);
                Element thisRawContact = returnFirstElement(thisRawContact0.getElementsByTagName("uncategorized"));
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
                                //TODO: run shitstorm
                            }
                            if (docColumnSchema.queryAggregateSettingIntoseparatecontacts(counter)) { //generate separate contacts
                                if (docColumnSchema.queryAggregateSettingAutodetectswaps(counter)) { //auto-detecting
                                    // ******* AGGREGATED, INTO SEPARATE, AUTO-DETECTION *******
                                    //TODO!
                                    //Not implemented!
                                } else { //no auto-detection!
                                    // ******* AGGREGATED, INTO SEPARATE, RIGID (without a-d) ******
                                    Scanner contentsScanner = new Scanner(contents);
                                    String delimiter1 = docColumnSchema.queryAggregateSettingDelimiter(counter);
                                    String delimiter2 = docColumnSchema.queryAggregateSettingSeparatecontactsdelimiter(counter);
                                    if (delimiter1 == null) {
                                        //TODO: run shitstorm
                                    }
                                    if (delimiter2 == null) {
                                        //TODO: run shitstorm
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
                                                //TODO: run shitstorm
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
                                                //TODO: run shitstorm
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
                                                        //TODO: run shitstorm
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
                                                    //TODO: run shitstorm
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
                                                //TODO: run shitstorm
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
                                                        //TODO: run shitstorm
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
                                    //TODO: run shitstorm
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
                                            //TODO: run shitstorm
                                        }
                                        String thisAggrContents = contentsScanner.next();
                                        try {
                                            writeField(rawContCount, thisAggrFieldType, thisAggrContents, outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                        } catch (DOMException e) {
                                            //TODO: run shitstorm
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
                                        //TODO: run shitstorm
                                    }
                                    try {
                                        writeField(rawContCount, thisAggrFieldType, writeContents, outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                    } catch (DOMException e) {
                                        //TODO: run shitstorm
                                    }
                                }
                            }
                        } else {
                            if (docColumnSchema.isColumnMergedInOther(counter)) { //belongs in mergeset...
                                // ============= COLUMN IN MERGESET ===============
                                Integer belongsInMergeset = docColumnSchema.queryMergeSet(counter);
                                if (belongsInMergeset == null) {
                                    //TODO: run shitstorm
                                }
                                if (!(docColumnSchema.getAllMergesets().contains(belongsInMergeset))) {
                                    //TODO: run shitstorm
                                }
                                columnsInMergeset.add(counter); //note the column in a mergeset

                                //OKAY, we have tested the column that it belongs in an existing mergeset
                                //will go through all mergesets later, let the column be at this moment
                                //TODO: go through all mergesets
                            } else { //normal column...                                //normal column...
                                // =========== NORMAL COLUMN ===========
                                String type = docColumnSchema.querySelectedtypeType(counter);
                                if (type == null) {
                                    type = docColumnSchema.queryCandidateType(counter);
                                }
                                if (type == null) {
                                    //TODO: run shitstorm
                                }

                                try {
                                    writeField(rawContCount, type, contents, outFormValidtr.vcfCanHaveMultipleInstances(counter));
                                } catch (DOMException e) {
                                    //TODO: run shitstorm
                                }
                            }
                        }
                    } else { //blank field
                        //dont want to do anything here
                    }

                }
                // ********** PROCESS MERGESETS HERE ***********
                if (processMergeSets) { //there are mergesets to process
                    ArrayList<Integer> allMergesets = docColumnSchema.getAllMergesets();
                    Iterator<Integer> usedMergesetsIter = usedMergesets.iterator();
                    while (usedMergesetsIter.hasNext()) {
                        Integer thisMergeset = usedMergesetsIter.next();
                        HashMap<Integer, Integer> allMergesetMembers = docColumnSchema.getAllMergesetMembers(thisMergeset);
                        SortedSet<Integer> sortedMembersSet = new TreeSet<Integer>(allMergesetMembers.keySet());
                        Iterator membersIter = sortedMembersSet.iterator();
                        String mergesetValue = "";
                        boolean firstEntry = true;
                        while (membersIter.hasNext()) {
                            Map.Entry attribPair = (Map.Entry) membersIter.next();
                            attribPair.getKey();
                            attribPair.getValue();
                            if (columnsInMergeset.contains((Integer) attribPair.getValue())) {
                                String contentsOfField = null;
                                NodeList childsOfContact = currentContactElement.getChildNodes();
                                for (Integer i = 0; i < childsOfContact.getLength(); i++) {
                                    if (childsOfContact.item(i) instanceof Element) {
                                        Element thisField = (Element) childsOfContact.item(i);
                                        if (Integer.parseInt(thisField.getAttribute("counter")) == attribPair.getValue()) {
                                            contentsOfField = thisField.getTextContent();
                                            break;
                                        }
                                    }
                                }

                                if (contentsOfField != null) {
                                    if (firstEntry) {
                                        mergesetValue = contentsOfField;
                                        firstEntry = false;
                                    } else {
                                        mergesetValue = mergesetValue + docColumnSchema.queryMergesetDelimiter(thisMergeset) + " ";
                                    }
                                }
                            }
                        }

                        //write mergeset into contact
                        String type = docColumnSchema.queryMergesetSelectedType(thisMergeset);
                        if (type == null) {
                            type = docColumnSchema.queryMergesetCandidateType(thisMergeset);
                        }
                        if (type == null) {
                            //TODO: run shitstorm
                        }

                        try {
                            writeField(rawContCount, type, mergesetValue, outFormValidtr.vcfCanHaveMultipleInstances(type));
                        } catch (DOMException e) {
                            //TODO: run shitstorm
                        }
                    }
                }

                //check if FN is present
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
        //TODO!
        return null;
    }

    public Document getCompiledInvalidContacts() {
        //TODO!
        return null;
    }

    public boolean compileErrorsDetected() {
        //TODO!
        return true;
    }
}
