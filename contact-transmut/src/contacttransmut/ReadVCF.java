package contacttransmut;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Molnar
 */
public class ReadVCF implements InputFilter {

    private String fileEncoding;
    private String fileName;
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;
    private ArrayList detectedTypes;

    public ReadVCF(String pFileName, String pEncoding) {
        this.fileName = pFileName;
        this.fileEncoding = pEncoding;
        this.detectedTypes = new ArrayList();
        
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadVCF.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.doc = db.newDocument();
        Element root = this.doc.createElement("root");
        this.doc.appendChild(root);
    }

    public Document read() {

        String line = "";
        Scanner scanner = null;
        boolean parsingContact = false;
        Element thisContact = null;
        Element thisContactUncat = null;
        Element thisProperty = null;
        VCFConverter converter = new VCFConverter();
        VCFHelper helper = new VCFHelperImpl(new InternalDocColumnSchemaImpl(1));
        int maxCounter = 0;
        int indexLastModified = -1;

        try {
            scanner = new Scanner(new FileInputStream(this.fileName), this.fileEncoding);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadVCF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();

                if (!parsingContact && line.equals("BEGIN:VCARD")) {
                    thisContact = this.doc.createElement("contact");
                    thisContactUncat = this.doc.createElement("uncategorized");
                    thisContact.appendChild(thisContactUncat);

                    // Prepare empty data elements for all detected types
                    for (int i = 0; i < maxCounter; i++) {
                        thisProperty = doc.createElement("data");
                        thisProperty.setAttribute("counter", Integer.toString(i));
                        thisContactUncat.appendChild(thisProperty);
                    }

                    parsingContact = true;

                } else if (parsingContact) {
                    if (line.equals("END:VCARD")) {
                        this.doc.getFirstChild().appendChild(thisContact);
                        parsingContact = false;
                    } else {
                        String[] contactLine = line.split(":");
                        // New property detected
                        if (contactLine.length > 1) {
                            if (!contactLine[0].equals("VERSION")) {
                                String naturalName = converter.VCFNameToNaturalName(contactLine[0]);
                                boolean mpInstAllowed = helper.vcfCanHaveMultipleInstances(naturalName);
                                boolean newType = true;

                                // We will go through all columns and try to fill the first empty column with the type of our property
                                for (int i = 0; i < detectedTypes.size(); i++) {
                                    if (detectedTypes.get(i).equals(naturalName)) {
                                        Node property = thisContactUncat.getChildNodes().item(i);
                                        
                                        // If column is empty
                                        if (property.getTextContent().equals("")) {
                                            property.setTextContent(contactLine[1]);
                                            indexLastModified = i;
                                            newType = false;
                                            break;
                                        // Column isn't empty, but there is allowed only one instance of this type
                                        } else if (!mpInstAllowed) {
                                            property.setTextContent(property.getTextContent() + ";" + contactLine[1]);
                                        }
                                    }
                                }

                                // If all columns with the type of our property are filled or there is no such column
                                // make new one and fill it with data
                                if (newType) {
                                    detectedTypes.add(naturalName);
                                    thisProperty = this.doc.createElement("data");
                                    thisProperty.setAttribute("counter", Integer.toString(maxCounter));
                                    thisProperty.setTextContent(contactLine[1]);
                                    thisContactUncat.appendChild(thisProperty);
                                    indexLastModified = maxCounter;
                                    maxCounter++;
                                }
                            }
                        // Multi-line value or broken file
                        } else {
                            if (indexLastModified > -1 && !contactLine[0].equals("")) {
                                String textContent = thisContactUncat.getChildNodes().item(indexLastModified).getTextContent();
                                thisContactUncat.getChildNodes().item(indexLastModified).setTextContent(textContent + ";" + contactLine[0]);
                            }
                        }
                    }
                }
            }
            
            this.doc.getDocumentElement().setAttribute("maxColumnNumber", Integer.toString(maxCounter));

            // For every contact
            for (int i = 0; i < this.doc.getDocumentElement().getChildNodes().getLength(); i++) {
                Node contact = this.doc.getDocumentElement().getChildNodes().item(i);
                int length = contact.getFirstChild().getChildNodes().getLength();
                // Add missing data elements. Every contact.uncategorized should have exactly maxCounter data elements
                for (int j = length; j < maxCounter; j++) {
                    thisProperty = doc.createElement("data");
                    thisProperty.setAttribute("counter", Integer.toString(j));
                    contact.getFirstChild().appendChild(thisProperty);
                }
            }
         } finally {
             scanner.close();
         }

        return doc;
    }

    public InternalDocColumnSchema getColumnSchema() {
        InternalDocColumnSchema schema = new InternalDocColumnSchemaImpl(detectedTypes.size());
        for (int i = 0; i < detectedTypes.size(); i++) {
            schema.setCandidateType(i, detectedTypes.get(i).toString());
        }
        return schema;
    }
    
}