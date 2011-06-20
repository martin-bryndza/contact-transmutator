
package contacttransmut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 *  Trieda na nacitavanie informacii zo zuboru typu ODS
 * 
 * @author MartinaHlinková
 */
public class ODSInput implements InputFilter {

 
    private String nameOfFile;
    private DocumentBuilderFactory factory;
    private DocumentBuilderFactory factoryODS;
    private DocumentBuilder builder;
    private DocumentBuilder builderODS;
    private Document document;
    private Document documentODS;
    private Integer maxColumns;
    private Element outputRoot;
    
    public ODSInput(String nameFile) throws SAXException, ParserConfigurationException, IOException, Exception{

        nameOfFile = nameFile;  // zadanie cesty k ODS dokumnetu
        
        //inicializovanie vnutorneho XLM DOMu s korenovym elementom "root"
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        document = builder.newDocument();
        Element root = document.createElement("root");
        outputRoot = root;
        document.appendChild(root);


       //nacitanie ODS do pamate; Ak sa nepodari, vyhodi vynimku 
        try{
            factoryODS = DocumentBuilderFactory.newInstance();
            builderODS = factoryODS.newDocumentBuilder();
            documentODS = builderODS.parse(new InputSource(new StringReader(openZIPFile(nameOfFile).toString())));
            
        }catch(Exception ex){
            throw ex;
        }
       
    }

    /**
     * Private helper method for opening the .ods (ZIP) file and reading content.xml.
     * @param nameFile path to file
     * @return StringBuilder text contents of contents.xml
     */
    private StringBuilder openZIPFile(String nameFile) {
        ZipFile inputods = null;
        try {
             inputods = new ZipFile(nameFile);
        } catch (IOException ex) {
            Logger.getLogger(ODSInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        ZipEntry entry = inputods.getEntry("content.xml");
                   BufferedReader buffread = null;
        try {
            buffread = new BufferedReader(new InputStreamReader(inputods.getInputStream(entry)));
        } catch (IOException ex) {
            Logger.getLogger(ODSInput.class.getName()).log(Level.SEVERE, null, ex);
        }
            String line;
            StringBuilder stringbuild = new StringBuilder();

        try {
            while ((line = buffread.readLine()) != null) {
                //System.err.println(line);
                stringbuild.append(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(ODSInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            buffread.close();
        } catch (IOException ex) {
            Logger.getLogger(ODSInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            inputods.close();
        } catch (IOException ex) {
            Logger.getLogger(ODSInput.class.getName()).log(Level.SEVERE, null, ex);
        }
            return stringbuild;
    }
    /**
     * Pomocna metoda, ktora prechadza jednotline listy v dokumente ODS a hlada navyssi pocet stlpcov
     * ODS ma formu: XML => viac listov; jeden list => viac tabuliek
     * 
     * @param doc - nacitane ODS v pamati
     * @return maximalny pocet stlpcov
     */
    private Integer maxPocetStlpcov(Document doc){
        int number = 0; // maximalny pocet stlpcov
        
        // vytvorenie zoznamu tabuliek z listov ODS
        NodeList listOfTables = doc.getElementsByTagName("table:table");
        
        //prehladavanie jednotlivych tabuliek  
        for(int i = 0; i < listOfTables.getLength(); i++){
            Node elementTable = listOfTables.item(i);
            if(elementTable.getNodeType() == Node.ELEMENT_NODE){
                
                //vytvorenie zoznamu tabuliek v liste
               NodeList listOfColumns = elementTable.getChildNodes();
               
               //hladanie tagu s informaciami o formatovani stlpcov
               for(int j = 0; j < listOfColumns.getLength(); j++){
                   Node elementColumns = listOfColumns.item(j);
                   if(elementColumns.getNodeName().equals("table:table-column") == true){
                       
                       // Vytvorenie mapy uzlov s atributmi tagu o formatovani stlpcov
                       NamedNodeMap mapaAtributov = elementColumns.getAttributes();
                       
                       //hladanie informacie o pocte stlpcov v tabulke a nasledne porovnanie
                       int hodnota = 0;
                       hodnota = Integer.parseInt(mapaAtributov.getNamedItem("table:number-columns-repeated").getNodeValue());

                       if(hodnota > number){number = hodnota;}
                   }
               }
            }
        }
         return (Integer)number;
    }
    /**
     * Metoda, ktora ziskava informacie z ODS a zadeluje ich do vnutornedo XML DOM
     * 
     * @return vnutorne XML DOM
     */
    public Document read(){
        Integer maxColumnInRead = 0;
        //najdenie korenoveho tagu
        Element koren = document.getDocumentElement();
        
        // zoznam tabuliek
        NodeList listOfTables = documentODS.getElementsByTagName("table:table");
        
        for(int i = 0; i < listOfTables.getLength(); i++){
            
            //momentalna tabulka, s ktorou pracujeme
            Node tab = listOfTables.item(i);
            
            //zoznam potencialnych riadkov tabulky
            NodeList listOfRows = tab.getChildNodes();
            for(int j = 0; j < listOfRows.getLength(); j++){
                Node row = listOfRows.item(j);
                
                // kontrola, ci ide o riadok tabulky (inak riadiaca informacia)
                if(row.getNodeName().equals("table:table-row") == true){
                    //vlozenie noveho kontaktu do XML DOM
                    Element novyKontakt0 = document.createElement("contact");
                    Element novyKontakt = document.createElement("uncategorized");
                    koren.appendChild(novyKontakt0);
                    novyKontakt0.appendChild(novyKontakt);

                    Integer cislovani = -1;

                    //zoznam potencialnych buniek v riadku
                    NodeList listOfCells = row.getChildNodes();
                    for(int k = 0; k < listOfCells.getLength(); k++){
                        Node cell = listOfCells.item(k);

                       // Kontrola, ci ide o bunku tabulky
                        if(cell.getNodeName().equals("table:table-cell") == true){
                            cislovani++; //i prazdna bunka data musi byt zapisana a ocislovana
                            if (cislovani>maxColumnInRead) maxColumnInRead = cislovani;
                            Element zaznam = document.createElement("data");
                            zaznam.setAttribute("counter", cislovani.toString());
                            novyKontakt.appendChild(zaznam);
                            
                            if(cell.hasChildNodes() == true){ // pokial ma bunka data
                                
                                // zapis obsahu bunky do zaznamu kontaktu
                                String text = cell.getTextContent().trim();
                                
                                zaznam.setTextContent(text);                              
                            }
                            
                        }
                    }
                    // pokial kontakt nema ziadne zaznamy, vymaze sa
                    if(novyKontakt.hasChildNodes() == false){
                        koren.removeChild(novyKontakt);
                    }
                }
            }
        }
        maxColumns = maxColumnInRead;
        outputRoot.setAttribute("maxColumnNumber", maxColumnInRead.toString());
         return document;
    }
    
    /**
     * Metoda, ktora vytvara nove stplcove schema podla maximalneho poctu stlpcov v ODS 
     * 
     * @return pozadovane prazdne schema
     */
    public InternalDocColumnSchema getColumnSchema() {
        // vypocet maximalneho poctu stlpcov
//        Integer maxStlpcov = maxPocetStlpcov(documentODS);

        //Vytvorenie schemy
//        InternalDocColumnSchema novaSchema = new InternalDocColumnSchemaImpl(maxStlpcov);
        InternalDocColumnSchema novaSchema = new InternalDocColumnSchemaImpl(maxColumns+1);
        
        return novaSchema;
    }
    
}
