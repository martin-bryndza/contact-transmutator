/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ovečka
 */
public class WriteCSV implements OutputFilter {
    //CONSTRUCTOR: should allow to choose filename, encoding and filetype-specific parameters
    //constructor should accept "CompiledDoc" Document (as produced by InternalDoc2CompiledDoc) and filePath

    private String fileEncoding;
    private String fileFileName;
    private String delimiter;
    private String quote;
    private Document compiledDoc;
    Element rootCompiledDoc;
    HashMap<Integer, ArrayList<String>> outputTableContents; //contents of the table
    ArrayList<VCFTypesEnum> outputTableHeader; //headers of the table - data types are written here

    public WriteCSV(String aFileName, String aEncoding, String aDelimiter, String aQuote, Document aCompiledDoc) {
        // ignore this // System.err.println("WriteCSV");
        fileEncoding = aEncoding;
        fileFileName = aFileName;
        delimiter = aDelimiter;
        quote = aQuote;
        compiledDoc = aCompiledDoc;
        rootCompiledDoc = (Element) compiledDoc.getDocumentElement().getElementsByTagName("contacts").item(0);

        outputTableContents = new HashMap<Integer, ArrayList<String>>();
        outputTableHeader = new ArrayList<VCFTypesEnum>();


    }

    private void addCell(Integer contactNumber, VCFTypesEnum type, String contents) {
        //helper method to write single cell into the table
// ignore this // System.err.println("addcell(contactNumber="+contactNumber+", type="+type+", contents="+contents);
        //check if type already exists
        if (!(outputTableHeader.contains(type))) {
            //doesnt exist
            outputTableHeader.add(type);
        }
// ignore this // System.err.println("addcell checkA");
        //exists now
        //check if this type already exists for current contact

        boolean notWritten = true;
        Integer headerIter = -1; //begin before beginning so we can catch even the first column :)
        VCFTypesEnum tmpType = null;
        Integer indexOfType;
// ignore this // System.err.println("addcell checkB");
        while (notWritten) {
// ignore this // System.err.println("addcell checkC headerIter="+headerIter);
            tmpType = null;
            while (outputTableHeader.size() - 1 > headerIter) { //search for next column of the type
                // ignore this // System.err.println("search headerIter="+headerIter);
                headerIter++;
                tmpType = outputTableHeader.get(headerIter);
                if (tmpType.equals(type)) {
                    break; //found it!
                }
            }

            if ((tmpType == null) || (!(tmpType.equals(type)))) { //the search ended at the end of list, create new column
                // ignore this // System.err.println("search at end,add");

                //that means we have still not written and there is no more columns of the type
                //create new
                outputTableHeader.add(type);
                indexOfType = outputTableHeader.lastIndexOf(type); //same as the last index of the list...
            } else { //the search was successful, so use the result
                indexOfType = headerIter;
            }

            if (outputTableContents.containsKey(contactNumber)) { //there is a line for that (contact already exists)
                ArrayList<String> thisContactArray = outputTableContents.get(contactNumber);
                // ignore this // System.err.println("thisContactArray.size()-1="+(thisContactArray.size()-1)+", indexOfType="+indexOfType);
                if (thisContactArray.size() - 1 < indexOfType) { //there are fewer columns than needed
                    //add new columns
                    //sparse arrays could come handy...
                    Integer shouldbe = thisContactArray.size() - 1;
                    for (Integer i = 0; i < (indexOfType - (shouldbe)); i++) {
                        // ignore this // System.err.println("adding indexOfType = "+indexOfType+", thisContactArray.size()="+thisContactArray.size()+", shouldbe="+shouldbe+", i="+i);
                        thisContactArray.add(""); //fill the contact...
                    }
                    // ignore this // System.err.println("added, thisContactArray.size="+thisContactArray.size());
                }
                if ((thisContactArray.get(indexOfType) == null) || (thisContactArray.get(indexOfType).trim().equals(""))) {
                    // ignore this // System.err.println("empty");
                    //the corresponding field is empty
                    thisContactArray.set(indexOfType, contents);
                    notWritten = false; //WRITTEN!!
                } else {
                    // ignore this // System.err.println("nonempty");
                    //the corresponding field is already used, must create new one
                    //do nothing, the loop will run once again and tries to find the next column of the same type
                    //or creates new column
                }
            } else { //must create the contact first
                // ignore this // System.err.println("/must create the contact first");
                outputTableContents.put(contactNumber, new ArrayList<String>());
                headerIter = -1; //restart the run
                //haha, so much for optimization
                //todo: jakub svoboda: rework and optimize
            }
        }
    }

    //writes the CompiledDoc in appropriate format to the file got in constructor
    public void write() {

        NodeList contactsNL = rootCompiledDoc.getChildNodes(); //get all the contacts in nodelist

        for (int contCount = 0; contCount < contactsNL.getLength(); contCount++) { //go through ocntacts
            if (contactsNL.item(contCount) instanceof Element) { //check this is indeed an element - contact
                Element currentContactElement = (Element) contactsNL.item(contCount); //contact we will be working on = row in resulting table
                NodeList attribsOfThisContact = currentContactElement.getChildNodes(); //contact’s attributes = columns in the row
                for (int attribCount = 0; attribCount < attribsOfThisContact.getLength(); attribCount++) { //go through the attributes
                    if (attribsOfThisContact.item(attribCount) instanceof Element) { //if this is attribute...
                        Element currentAttribute = (Element) attribsOfThisContact.item(attribCount); //current attribute we will write to the table
// ignore this // System.err.println("currentAttribute name = "+currentAttribute.getNodeName());
                        VCFTypesEnum vcftype = VCFTypesEnum.valueOf(currentAttribute.getTagName()); //match the attribute to VCFTypesEnum

                        if (vcftype != null) { //if match successful
                            //add the attribute data of this particular type into the resulting table
                            addCell(Integer.valueOf(currentContactElement.getAttribute("number")), vcftype, currentAttribute.getTextContent().trim());
                        }
                    }
                }


            }
        } //internal table is created
        //write it to the file now:

        FileOutputStream fos = null; //output file
        Writer out = null;
        try {
            fos = new FileOutputStream(fileFileName); //open file based on path from constructor
            out = new OutputStreamWriter(fos, fileEncoding); //use the encoding specified in constructor (I would suggest using "UTF-8")
        } catch (IOException e) {
            System.err.println(e);
        }

        String NL = System.getProperty("line.separator");

        for (Integer i = 0; i < outputTableHeader.size(); i++) { //write used filetypes for the particular columns in the first row of resulting table
            try {
                out.write(quote); //one cell is encapsulated in quotes
                out.write(outputTableHeader.get(i).toDisplayString());
                out.write(quote);
                out.write(delimiter); //delimited by delimiters
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        try {
            out.write(NL); //the line ends with NL character :)
        } catch (IOException e) {
            System.err.println(e);
        }



        Collection tmpCol = outputTableContents.keySet(); //to make possible iterate through hashmap
        Iterator<Integer> rowsIter = tmpCol.iterator();

        while (rowsIter.hasNext()) { //go through all the rows (contacts)
            Integer thisRow = rowsIter.next();
            for (Integer jcol = 0; jcol < outputTableContents.get(thisRow).size(); jcol++) { //and through all the columns for each row (through all attributes for each contact)
                try {
                    out.write(quote);
                    out.write(outputTableContents.get(thisRow).get(jcol).replace(quote, quote + quote)); //if quote is used inside cell, it should be doubled (like quotes in Pascal)
                    out.write(quote);
                    out.write(delimiter);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
            try {
                out.write(NL);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        try {
            out.close(); //close the file
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
