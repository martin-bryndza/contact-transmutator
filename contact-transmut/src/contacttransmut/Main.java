/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ovečka
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, TransformerConfigurationException, TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, Exception {

        { //this is first basic test - load CSV into InternalDoc
            //This very simple code loads CSV into DOM tree and prints out the DOM tree along with some other information
            
             System.out.println("Ukazka vstupniho filtru:");
           
            String fileName = "csv.csv";
            String encoding = "UTF-8";
            InputFilter test = new ReadCSV(fileName, encoding, ",", "\"");
            
            // pre test ods xml odkomentuj tieto riadky   --Martina
            //String fileName = "content.xml";
            //InputFilter test = new ODSInput(fileName);
            
				// Test nacitani VCF
				// String fileName = "test.vcf";
				// InputFilter test = new ReadVCF(fileName, "UTF-8");
            
            Document loadedContacts = test.read();

            //print out result
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = null;
            trans = tf.newTransformer();
            trans.transform(new DOMSource(loadedContacts), new StreamResult(System.out));

            System.out.println("");
            System.out.println("Ukazka vystupniho filtru (otevri vystupni soubor):");
                DocumentBuilderFactory factoryWC = DocumentBuilderFactory.newInstance();

 //   factoryWC.setNamespaceAware(true);
    DocumentBuilder builderWC = factoryWC.newDocumentBuilder();

    Document docWC = builderWC.parse(new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root><contacts><contact number=\"0\"><Formatted_Name>?\"totojeA1\"</Formatted_Name><Delivery_Address>tptpjeC1, totojeB1</Delivery_Address></contact><contact number=\"1\"><Formatted_Name>totojeA2</Formatted_Name><Delivery_Address>toto je \"pokus\" hehe, tootojeB2</Delivery_Address></contact><contact number=\"2\"><Delivery_Address>zpetne \\ lomitko se pouziva jako \\\" pro \"fffuu\"</Delivery_Address></contact><contact number=\"3\"><Formatted_Name>\"</Formatted_Name><Delivery_Address>ja jsem carka \",\" troll to je jedno pole</Delivery_Address></contact><contact number=\"5\"><Formatted_Name>Display name</Formatted_Name><Email>email</Email><Telephone>telephone</Telephone><Telephone>telephone</Telephone><Organization_Name_or_Organizational_unit>company</Organization_Name_or_Organizational_unit><Note>note</Note><Delivery_Address>City, Address</Delivery_Address></contact><contact number=\"6\"><Formatted_Name>Novák Franta</Formatted_Name><Email>mail@mail.com</Email><Telephone>98479484</Telephone><Telephone>55437433</Telephone><Delivery_Address>Kutnovice, U Stodoly 23</Delivery_Address></contact><contact number=\"7\"><Formatted_Name>Bolá Pepka</Formatted_Name><Email>asdf@omg.bg</Email><Telephone>97974472</Telephone><Telephone>74684847</Telephone><Delivery_Address>Hechtorov, Hraničky 77</Delivery_Address></contact><contact number=\"8\"><Formatted_Name>ředitel Maw Gawd</Formatted_Name><Email>mawgawd@herpderp.derp</Email><Telephone>98764643</Telephone><Organization_Name_or_Organizational_unit>HURR DURR ELECTRIC</Organization_Name_or_Organizational_unit><Delivery_Address>Pozorov, Křeslová 123</Delivery_Address></contact><contact number=\"9\"><Formatted_Name>sekretářka Ow Data</Formatted_Name><Email>owdata@herpderp.derp</Email><Organization_Name_or_Organizational_unit>HURR DURR ELECTRIC</Organization_Name_or_Organizational_unit><Delivery_Address>Pozorov, Křeslová 123</Delivery_Address></contact><contact number=\"10\"><Formatted_Name> technik Tee Hee</Formatted_Name><Telephone>897736363</Telephone><Organization_Name_or_Organizational_unit>HURR DURR ELECTRIC</Organization_Name_or_Organizational_unit><Delivery_Address>Pozorov, Křeslová 123</Delivery_Address></contact><contact number=\"11\"><Formatted_Name>HURR DURR ELECTRIC</Formatted_Name><Email>hurrdurr@herpderp.derp</Email><Note>ředitel Maw Gawd 98764643, mawgawd@herpderp.derp, sekretářka Ow Data owdata@herpderp.derp, technik Tee Hee 897736363</Note><Delivery_Address>Pozorov, Křeslová 123</Delivery_Address></contact></contacts></root>")));

            //print out docWC
            TransformerFactory tfdocWC = TransformerFactory.newInstance();
            Transformer transdocWC = null;
            transdocWC = tfdocWC.newTransformer();
            transdocWC.transform(new DOMSource(docWC), new StreamResult(System.out));
            System.out.println("");

            System.out.println("printing csvout.csv:");
        OutputFilter testWriteCSV = new WriteCSV("csvout.csv", "UTF-8", ",", "\"", docWC);
        testWriteCSV.write();
        }


    }
}
