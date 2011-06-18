/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *
 * @author MartinaHlinková
 */
public class ODSWrite implements OutputFilter {
    private String outputFileName;
    private DocumentBuilderFactory factoryODS;
    private DocumentBuilder builderODS;
    private Document documentODS;
    private Document compiled;
    private Element korenCompiled;
    private Element korenODS;
    
    public ODSWrite(String fileName, Document compiled){
        //cesta, kam sa bude ukladat vysledne xml
        outputFileName = fileName;
        this.compiled = compiled;
        korenCompiled = (Element) compiled.getDocumentElement().getElementsByTagName("contacts").item(0);
        
        
        //novy XML DOM na vytvaranie ODS
        factoryODS = DocumentBuilderFactory.newInstance();
        try {
            builderODS = factoryODS.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OutputFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
          
        //nacitanie korenoveho elementu "office:document-content"
        documentODS = builderODS.newDocument();
        korenODS = documentODS.createElement("office:document-content");
        korenODS.setAttribute("office:version","1.2");
        documentODS.appendChild(korenODS);
        
        
        
    }
    
     public void write() {
        
        
         System.out.println(korenODS.getNodeName());
       
         napisHlavicku();
        /* 
         TransformerFactory tran = TransformerFactory.newInstance();
         Transformer tr = null;
         try{
             tr = tran.newTransformer();
             tr.transform(new DOMSource(documentODS), new StreamResult(System.out));
         }catch(Exception e){
             System.err.println(e);
         }
         
         
         
         
         //ziskanie korena CompiledDoc
         Node koren = compiled.getDocumentElement().getFirstChild();
         //ziskanie vsetkych kontaktov z COmpiledDoc
         NodeList kontakty = koren.getChildNodes();
         // pocitanie maximalneho poctu stlpcov
         int maxStlpcov = 0;
        /* 
         // tag novej tabulky a jeho nastavenie
         Element tagTabulky = documentODS.createElement("table:table");
         tagTabulky.setAttribute("table:name", "List1");
         tagTabulky.setAttribute("table:print", "false");
         tagTabulky.setAttribute("table:style-name", "ta1");
         
         //Prechadzanie zoznamu kontaktov po jednom
         for(int i= 0; i< kontakty.getLength(); i++){
             Node jedenKontakt = kontakty.item(i);
             
             // vytvorenie tagu pre jeden kontakt - jeden riadok
             Element riadokKontaktu = documentODS.createElement("table:table-row");
             riadokKontaktu.setAttribute("table:style-name", "ro1");
             
             // zoznam informacii, ktore sa nachadzaju v kontakte
             Node kontaktNumber = jedenKontakt.getFirstChild();
             NodeList kontaktInfo = kontaktNumber.getChildNodes();
             
             //pocitanie maximlneho poctu stlpcov
             if(maxStlpcov < kontaktInfo.getLength()){
                 maxStlpcov = kontaktInfo.getLength();
             }
             
             // zadavanie informacii z kontaktu do buniek riadku
             for(int j = 0; j< kontaktInfo.getLength(); j++){
                 Node info = kontaktInfo.item(j);
                 String infoContent = info.getNodeValue();
                 
                 Element bunka = documentODS.createElement("table:table-cell");
                 bunka.setAttribute("office:value-type", "string");
                 bunka.setNodeValue(infoContent);
                 riadokKontaktu.appendChild(bunka);
             }
             
             tagTabulky.appendChild(riadokKontaktu);
             
         }
         Element korenODS = documentODS.getDocumentElement();
         Node spreadsheet = korenODS.getElementsByTagName(outputFileName).item(0);
         spreadsheet.appendChild(tagTabulky);
         */
         FileOutputStream outputStream = null;
         try {
            outputStream = new FileOutputStream(outputFileName);
            TransformerFactory vypisFactory = TransformerFactory.newInstance();
            Transformer vypis = null;
            vypis = vypisFactory.newTransformer();
            vypis.transform(new DOMSource(documentODS), new StreamResult(outputStream));
            
        } catch (Exception e) {
            System.err.println(e);
            
        }
        
        
     }
    
        public void napisHlavicku(){

         System.out.println(korenODS.getNodeName());
       
        // --------vypis hlavicky ODS do XML DOM------------
        
        //znacka scriptov
        Element castHlavicky = documentODS.createElement("office:scripts");
        korenODS.appendChild(castHlavicky);
        
        //znacka zoznamu fontov
        castHlavicky = documentODS.createElement("office:font-face-decls");
        Element font = documentODS.createElement("style:font-face");
            font.setAttribute("style:font-family-generic", "swiss");
            font.setAttribute("style:font-pitch", "variable");
            font.setAttribute("style:name", "Arial");
            font.setAttribute("svg:font-family", "Arial");
        castHlavicky.appendChild(font);
        font = documentODS.createElement("style:font-face");
            font.setAttribute("style:font-family-generic", "system");
            font.setAttribute("style:font-pitch", "variable");
            font.setAttribute("style:name", "Lucinda Sans Unicode");
            font.setAttribute("svg:font-family", "Lucinda Sans Unicode");
        castHlavicky.appendChild(font);
        font = documentODS.createElement("style:font-face");
            font.setAttribute("style:font-family-generic", "system");
            font.setAttribute("style:font-pitch", "variable");
            font.setAttribute("style:name", "Tahoma");
            font.setAttribute("svg:font-family", "Tahoma");
        castHlavicky.appendChild(font);
        korenODS.appendChild(castHlavicky);
        /*
        //znacka stylov
        castHlavicky = documentODS.createElement("office:automatic-styles");
        
        Element styl = documentODS.createElement("style:style");
             styl.setAttribute("style:family", "table-column");
             styl.setAttribute("style:name", "co1");
             Element stylFace = documentODS.createElement("style:table-column-properties");
                stylFace.setAttribute("style:column-width", "5.000cm");
                stylFace.setAttribute("fo:break-before", "auto");
             styl.appendChild(stylFace);
        castHlavicky.appendChild(styl);
        
         styl = documentODS.createElement("style:style");
             styl.setAttribute("style:family", "table-row");
             styl.setAttribute("style:name", "ro1");
             stylFace = documentODS.createElement("style:table-row-properties");
                stylFace.setAttribute("style:row-height", "2.500cm");
                stylFace.setAttribute("style:use-optimal-row-height", "false");
                stylFace.setAttribute("fo:break-before", "auto");
             styl.appendChild(stylFace);
        castHlavicky.appendChild(styl);
        
         styl = documentODS.createElement("style:style");
             styl.setAttribute("style:family", "table-row");
             styl.setAttribute("style:name", "ro2");
             stylFace = documentODS.createElement("style:table-row-properties");
                stylFace.setAttribute("style:row-height", "0.500cm");
                stylFace.setAttribute("style:use-optimal-row-height", "false");
                stylFace.setAttribute("fo:break-before", "auto");
             styl.appendChild(stylFace);
        castHlavicky.appendChild(styl);
        
        styl = documentODS.createElement("style:style");
             styl.setAttribute("style:family", "table");
             styl.setAttribute("style:master-page-name", "Default");
             styl.setAttribute("style:name", "ta1");
             stylFace = documentODS.createElement("style:table-properties");
                stylFace.setAttribute("style:writing-mode", "lr-tb");
                stylFace.setAttribute("table:display", "true");
             styl.appendChild(stylFace);
        castHlavicky.appendChild(styl);
        
        styl = documentODS.createElement("style:style");
             styl.setAttribute("style:family", "table");
             styl.setAttribute("style:name", "ta_extref");
             stylFace = documentODS.createElement("style:table-properties");
                stylFace.setAttribute("table:display", "false");
             styl.appendChild(stylFace);
        castHlavicky.appendChild(styl);
        
        koren.appendChild(castHlavicky);
        
        //znacka pre telo XML a pre tabulkovy dokument
        
        Element telo = documentODS.createElement("office:body");
        Element tabDok = documentODS.createElement("office:spreadsheet");
        telo.appendChild(tabDok);
        koren.appendChild(telo);
        
        //   ------Koniec vypisu hlavicky--------
        */
    }

}
