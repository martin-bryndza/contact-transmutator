/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

import java.io.ByteArrayOutputStream;
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

    public ReadCompiledDoc(Document compiledDoc){
        this.compiledDoc = compiledDoc;
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc = db.newDocument(); //this is internal XML DOM we use to process the data
    }

    public Document read() {
        //create root element
        int numberOfColumns = getNumberOfColumns(compiledDoc);
        Element root = doc.createElement("root");
        root.setAttribute("maxColumnNumber", String.valueOf(numberOfColumns));
        doc.appendChild(root);

        //for each contact node
        NodeList contactList = compiledDoc.getElementsByTagName("contact");
        int temp1 = contactList.getLength();
        for (int i=0; i<contactList.getLength(); i++){
            //create contact element and uncategorized element
            Element contact = root.getOwnerDocument().createElement("contact");
            root.appendChild(contact);
            Element uncategorized = contact.getOwnerDocument().createElement("uncategorized");
            contact.appendChild(uncategorized);

            //for each data node of the contact
            Element cont = (Element) contactList.item(i);
            NodeList dataList = cont.getChildNodes();
            for (int j=0; j<dataList.getLength(); j++){
                int temp = dataList.getLength();
                //create data element and fill it with value
                Element data = uncategorized.getOwnerDocument().createElement("data");
                data.setAttribute("counter", String.valueOf(j));
                data.setTextContent(dataList.item(j).getTextContent());
                uncategorized.appendChild(data);
            }

            //the rest will be empty data nodes
            for (int j=dataList.getLength(); j<numberOfColumns; j++){
                Element data = uncategorized.getOwnerDocument().createElement("data");
                data.setAttribute("counter", String.valueOf(j));
                uncategorized.appendChild(data);
            }
        }

        // <editor-fold defaultstate="collapsed" desc="print to System.err">
        System.err.println("");
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
        columnSchema = new InternalDocColumnSchemaImpl(getNumberOfColumns(compiledDoc));
        return columnSchema;
    }

    private int getNumberOfColumns(Document compiledDoc){
        int result = 0;
        NodeList contactList = compiledDoc.getElementsByTagName("contact");
        for (int i=0; i<contactList.getLength(); i++){
            int numberOfItems = contactList.item(i).getChildNodes().getLength();
            if (numberOfItems > result){
                result = numberOfItems;
            }
        }
        return result;
    }

}
