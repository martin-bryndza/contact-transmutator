/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
        Element root = doc.createElement("root");
        doc.appendChild(root);
    }

    public Document read() {

        return doc;
    }

    public InternalDocColumnSchema getColumnSchema() {

        return columnSchema;
    }

    private int getNumberOfColumns(Document compiledDoc){
        
        return 1;
    }

}
