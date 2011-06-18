/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Martin
 */
public class ReadCompiledDoc implements InputFilter{

    private Document compiledDoc;
    private InternalDocColumnSchema columnSchema;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;  //internalDoc
    private int numberOfColumns;
    private HashMap<Integer,ArrayList<Integer>> indexesTable;
    private boolean stateIsOK = true;

    public ReadCompiledDoc(Document compiledDoc){
        stateIsOK = true;
        this.compiledDoc = compiledDoc;
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc = db.newDocument(); //this is internal XML DOM we use to process the data

        //find out the number of columns and create hash table <index of column in CompiledDoc, index in output>
        //index in output must be a list, because aggregated columns have the same index in CompiledDoc
        indexesTable = new HashMap<Integer, ArrayList<Integer>>();
        NodeList contactList = compiledDoc.getElementsByTagName("contact");
        for (int i=0; i<contactList.getLength(); i++){
            Element contactElement = (Element) contactList.item(i);
            //a List to help detecting aggregations
            ArrayList<Integer> indexesUsedInThisContact = new ArrayList<Integer>();
            NodeList dataList = contactElement.getChildNodes();
            for (int j=0; j<dataList.getLength(); j++){
                Element dataElement = (Element) dataList.item(j);

                //get the index in CompiledDoc
                int indexCD;
                try {
                    indexCD = Integer.parseInt(dataElement.getAttribute("columnCounter"));
                } catch (NumberFormatException numberFormatException) {
                    indexCD = 10000;
                }

                //count the next free column -> it is the SUM of all Arrays in indexesTable
                int nextFreeColumn = 0;
                for (ArrayList<Integer> array : indexesTable.values()){
                    nextFreeColumn += array.size();
                }

                //if there is no mapping for this index, create new one and use next column
                if (indexesTable.get(indexCD) == null){
                    indexesTable.put(indexCD, new ArrayList<Integer>());
                    indexesTable.get(indexCD).add(nextFreeColumn);
                }
                // else if there is such mapping, but this data item is from aggregation (has been used in this contact)
                else if (indexesTable.get(indexCD) != null && indexesUsedInThisContact.contains(indexCD)){
                    //get how many times it has been used in this contact to find out, if we need to add a column into the array
                    //it should be grater by one
                    int numberOfSameColumnsUsed = getNumberOfDuplicates(indexesUsedInThisContact, indexCD);
                    int numberOfColumnsIndexed = indexesTable.get(indexCD).size();
                    //if the number of indexed columns and number of duplicates are the same and we are going to use another duplicate, we need to assign nex column
                    if (numberOfSameColumnsUsed == numberOfColumnsIndexed){
                        indexesTable.get(indexCD).add(nextFreeColumn);
                    }
                    //else if we have less duplicates then columns assigned, we just use one of the assigned
                    //else it is a bug
                    else if (numberOfSameColumnsUsed > numberOfColumnsIndexed) {
                        stateIsOK = false;
                        System.err.println("ReadCompiledDoc ERROR while creating indexesTable - errNo: 1");
                        return;
                    }
                }
                //else the column is allready in the hash table -> do nothing


                //add the index to used
                indexesUsedInThisContact.add(indexCD);
            }
        }

        //count the number of columns -> it is the SUM of all Arrays in indexesTable
        numberOfColumns = 0;
        for (ArrayList<Integer> array : indexesTable.values()) {
            numberOfColumns += array.size();
        }
    }

    public Document read() {
        //check state
        if (!stateIsOK){
            System.err.println("ReadCompiledDoc ERROR: tried to read not with not correctly instantiazed entity - errNo: 2");
            return null;
        }

        //create root element
        Element root = doc.createElement("root");
        root.setAttribute("maxColumnNumber", String.valueOf(numberOfColumns));
        doc.appendChild(root);

        //for each contact node
        NodeList contactList = compiledDoc.getElementsByTagName("contact");
        for (int i=0; i<contactList.getLength(); i++){
            //create contact element and uncategorized element
            Element contact = root.getOwnerDocument().createElement("contact");
            root.appendChild(contact);
            Element uncategorized = contact.getOwnerDocument().createElement("uncategorized");
            contact.appendChild(uncategorized);

            //a List to help detecting aggregations
            ArrayList<Integer> indexesUsedInThisContact = new ArrayList<Integer>();

            //a List to help detecting unused columns
            ArrayList<Integer> columnsUsedInThisContact = new ArrayList<Integer>();

            //for each data node of the contact
            Element contactElement = (Element) contactList.item(i);
            NodeList dataList = contactElement.getChildNodes();
            //first, go through data and put them into correct columns
            for (int j=0; j<dataList.getLength(); j++){
                Element dataElement = (Element) dataList.item(j);

                //get the index in CompiledDoc
                int indexCD;
                try {
                    indexCD = Integer.parseInt(dataElement.getAttribute("columnCounter"));
                } catch (NumberFormatException numberFormatException) {
                    indexCD = 10000;
                }

                //add the index to used
                indexesUsedInThisContact.add(indexCD);

                //get the correct column for the field -> corresponding array int the indexesTable and corresponding index in the array
                int column = indexesTable.get(indexCD).get(getNumberOfDuplicates(indexesUsedInThisContact, indexCD)-1);

                if (columnsUsedInThisContact.contains(column)){
                    System.err.println("ReadCompiledDoc ERROR: tried to use allready used column - errNo: 3");
                    return null;
                }

                //create data element and fill it with column index
                Element data = uncategorized.getOwnerDocument().createElement("data");
                data.setAttribute("counter", String.valueOf(column));
                data.setTextContent(dataElement.getTextContent());
                uncategorized.appendChild(data);
                
                //add the column to used
                columnsUsedInThisContact.add(column);
            }

            //go through all columns and create the empty ones
            for (int j=0; j<numberOfColumns; j++){
                //if the column has not been used -> it is empty
                if (!columnsUsedInThisContact.contains(j)) {
                    Element data = uncategorized.getOwnerDocument().createElement("data");
                    data.setAttribute("counter", String.valueOf(j));
                    uncategorized.appendChild(data);
                }
            }
        }

        // <editor-fold defaultstate="collapsed" desc="print to System.err">
        System.err.println("\n Returning InternalDoc:");
        if (doc == null) {
            System.err.println("Document is null!!!");
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = null;
            try {
                trans = tf.newTransformer();
                trans.transform(new DOMSource(doc), new StreamResult(stream));
            } catch (TransformerException ex) {
                Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
            }
            String rawIntDoc = stream.toString();
            rawIntDoc = rawIntDoc.replaceAll("/>", "/>\n");
            rawIntDoc = rawIntDoc.replaceAll("</data>", "</data>\n");
            rawIntDoc = rawIntDoc.replaceAll("<contact>", "<contact>\n");
            rawIntDoc = rawIntDoc.replaceAll("</contact>", "</contact>\n\n");
            rawIntDoc = rawIntDoc.replaceAll("uncategorized>", "uncategorized>\n");
            System.err.println(rawIntDoc);
        }
        //</editor-fold>

            return doc;
    }

    public InternalDocColumnSchema getColumnSchema() {
        columnSchema = new InternalDocColumnSchemaImpl(numberOfColumns);
        return columnSchema;
    }

    private int getNumberOfDuplicates(ArrayList<Integer> list, Integer item){
        int result = 0;
        if (list.contains(item)){
            for (Integer i : list){
                if (i==item)
                    result++;
            }
        }
        return result;
    }

}
