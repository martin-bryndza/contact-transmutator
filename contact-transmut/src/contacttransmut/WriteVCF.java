package contacttransmut;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * OutputFilter implementation for VCF format.
 * 
 * @author Martin Molnar
 */
public class WriteVCF implements OutputFilter {
    
    private String fileEncoding;
    private String fileName;
    private Document compiledDoc;

    public WriteVCF(String pFileName, String pEncoding, Document pCompiledDoc) {
        this.fileEncoding = pEncoding;
        this.fileName = pFileName;
        this.compiledDoc = pCompiledDoc;
    }

    public void write() {

        String nLine = System.getProperty("line.separator");
        FileOutputStream fos = null;
        Writer out = null;
        
        try {
            fos = new FileOutputStream(this.fileName);
            out = new OutputStreamWriter(fos, this.fileEncoding);
        } catch (IOException e) {
            System.err.println(e);
        }

        NodeList contacts = this.compiledDoc.getDocumentElement().getFirstChild().getChildNodes();

        for (int i = 0; i < contacts.getLength(); i++) {
            Node contact = contacts.item(i);
            try {
                out.write("BEGIN:VCARD" + nLine);
                out.write("VERSION:3.0" + nLine);
                for (int j = 0; j < contact.getChildNodes().getLength(); j++) {
                    Element data = (Element) contact.getChildNodes().item(j);
                    String VCFName = VCFConverter.NaturalNameToVCFName(data.getTagName());
                    String value = data.getTextContent();
                    if (!value.equals("")) {
                        out.write(VCFName + ":" + value + nLine);
                    }
                }
                out.write("END:VCARD" + nLine);
            } catch (IOException ex) {
                System.err.println();
            }
        }

        try {
            out.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}