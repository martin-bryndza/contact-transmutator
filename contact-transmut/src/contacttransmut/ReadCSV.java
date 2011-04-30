/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ovečka
 */
public class ReadCSV implements InputFilter {

    private String fileEncoding;
    private String fileFileName;
    private String delimiter;
    private String quote;
    private final String FIXED_TEXT = "But soft! what code in yonder program breaks?";
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    private Document doc;
    /* document note:
     * document should be in the same format as from readCSV() even for other methods (like readVCF())
     * other methods (eg. readVCF()) should return ColumnSchema to identify the columns
     */

    ReadCSV(String aFileName, String aEncoding, String aDelimiter, String aQuote) {
        fileEncoding = aEncoding;
        fileFileName = aFileName;
        delimiter = aDelimiter;
        quote = aQuote;

        // create new internal XML DOM for processing the data
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

    /** Write fixed content to the given file. */
    void write_deletethis() throws IOException {
        log_deletethislater("Writing to file named " + fileFileName + ". Encoding: " + fileEncoding);
        Writer out = new OutputStreamWriter(new FileOutputStream(fileFileName), fileEncoding);
        try {
            out.write(FIXED_TEXT);
        } finally {
            out.close();
        }
    }

    StringBuilder readEntireFile_deletethislater() throws IOException {
        log_deletethislater("Reading from entire file.");
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(fileFileName), fileEncoding);
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append(NL);
            }
        } finally {
            scanner.close();
        }
        //log_deletethislater("Text read in: " + text);
        return text;
    }

    //Todo: write javadoc
    //this method’s implementation shall be final
    //this is CSV-to-XML parser
    public Document read() {

        Integer maxI = 0;
        Integer currentI = 0;

        //log_deletethislater("Reading from CSV file.");
        //log_deletethislater("delimiter, quote" + delimiter + quote);
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(fileFileName), fileEncoding);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }

        Element root = doc.getDocumentElement(); //get doc root element


        //Pattern patternRplcDelims = Pattern.compile("", Pattern.MULTILINE);
//           Pattern pattern = Pattern.compile(str_pattern);
//            Matcher matcher = pattern.matcher(sb);

        boolean iAmInQuote = false;
        boolean thereWasDelimiter = true; //newline is a delimiter too, new file too in fact :-)
        boolean waitForNextQuote = false;
        Element thisContact = null;
        Element thisLineElement = null;
        String stringThisElement = "";
        String stringThisLine = "";
        Scanner scannerThisLine = null;

        try {
            //log_deletethislater("trying1");
            boolean readingCSV = true;
            while (readingCSV) {
                readingCSV = scanner.hasNextLine();
                //process line there:

                if ((iAmInQuote) || (!readingCSV)) {
                    if ((waitForNextQuote) || (!readingCSV)) { //there was single quote and then \n
                        iAmInQuote = false; //not in quote anymore, that ended with the \n
                        //let next if clause create new contact record for us, so set iaminquote = false

                        //  save current element!
                        Element data = doc.createElement("data"); //create "data" element
                        data.setAttribute("counter", currentI.toString()); //create number comment
                        data.setTextContent(stringThisElement); //save read data into the element
                        thisLineElement.appendChild(data); //append to "uncategorized"
                        stringThisElement = ""; //data are saved, purge this string for new cell
                        if (maxI < currentI) {
                            maxI = currentI;
                        }
                        currentI = 0; //we ended the old line!
                        thereWasDelimiter = true; //yeah, there was a delimiter (newline)
                        waitForNextQuote = false; //there is nothing to wait for
                    } else {
                        stringThisElement += "\n";
                    }
                }
                if ((!iAmInQuote) && (readingCSV)) {
                    thisContact = doc.createElement("contact"); //create new contact
                    root.appendChild(thisContact);
                    thisLineElement = doc.createElement("uncategorized"); //yet-unknown data
                    thisContact.appendChild(thisLineElement);
                    currentI = 0; //we ended the old line!
                    stringThisElement = "";
                    thereWasDelimiter = true; //yeah, there was a delimiter (newline)
                }

                if (readingCSV) {
                    stringThisLine = scanner.nextLine();
                    scannerThisLine = new Scanner(stringThisLine);
                }

                if (readingCSV) {
                    //log_deletethislater("readingcsvline");
                    for (int ch = 0; ch < stringThisLine.length(); ch++) { //scan the string char by char
                        String thisOneChar = ("" + stringThisLine.charAt(ch));
                        Boolean Q = thisOneChar.equals(quote);
                        Boolean D = thisOneChar.equals(delimiter);
                        Boolean E = thereWasDelimiter;
                        Boolean W = waitForNextQuote;
                        Boolean I = iAmInQuote;
                        // log_deletethislater("char:" + thisOneChar + " flags:" + (Q?"Q":"")+ (D?"D":"")+ (E?"E":"")+ (W?"W":"")+ (I?"I":""));

                        if (!I) { //we are not in quotes
                            if (Q) { //not in quote, there is quote
                                if (E) { //was there a delimiter,
                                    iAmInQuote = true; //we are in quote from now on
                                    thereWasDelimiter = false;
                                } else { //so there was no delimiter and there is a single quote?
                                    //we’ll pass the quote to the text as text
                                    //this is already broken CSV
                                    //if there are more quotes outside of delimiter, better pass it through as text
                                    stringThisElement += quote;
                                }
                            } else {
                                if (D) { //not in quote, delimiter here
                                    //  save current element and begin creating new!
                                    Element data = doc.createElement("data"); //create "data" element
                                    data.setAttribute("counter", currentI.toString()); //create number comment
                                    data.setTextContent(stringThisElement); //save read data into the element
                                    thisLineElement.appendChild(data); //append to "uncategorized"
                                    stringThisElement = ""; //data are saved, purge this string for new cell
                                    if (maxI < currentI) {
                                        maxI = currentI;
                                    }
                                    currentI++; //we are in next element from now on
                                    thereWasDelimiter = true; //yeah, there was a delimiter
                                } else { //no control chars there
                                    stringThisElement += thisOneChar; //add char as text
                                    thereWasDelimiter = false;
                                }
                            }

                        }
                        if (I) { //in quote...
                            if (W) { //waiting already
                                if (Q) { //great! two quotes are a quote in text
                                    waitForNextQuote = false;
                                    stringThisElement += quote;
                                    thereWasDelimiter = false;
                                } else {
                                    if (D) { //so there was " and then ,
                                        //that means we are in quote no more and shall save the element and start new one
                                        waitForNextQuote = false;
                                        //  save current element and begin creating new!
                                        Element data = doc.createElement("data"); //create "data" element
                                        data.setAttribute("counter", currentI.toString()); //create number comment
                                        data.setTextContent(stringThisElement); //save read data into the element
                                        thisLineElement.appendChild(data); //append to "uncategorized"
                                        stringThisElement = ""; //data are saved, purge this string for new cell
                                        if (maxI < currentI) {
                                            maxI = currentI;
                                        }
                                        currentI++; //we are in next element from now on
                                        thereWasDelimiter = true; //there was a delimiter...
                                        iAmInQuote = false; //not inside quotes anymore!
                                    } else {
                                        //what the fuck at this point, seriously
                                        //we had an ending quote and there is no delimiter?!
                                        //okay, we have to cope even with broken files
                                        //abc,"def"ghi,xyz -> abcDELIMdef"ghiDELIMxyz
                                        //we’ll just pass the quote in the text and stay in the same element
                                        waitForNextQuote = false;
                                        stringThisElement += quote + thisOneChar;
                                    }
                                }
                            } else { //we are in quote and not waiting for another quote
                                thereWasDelimiter = false; //there was definitely no delimiter
                                if (Q) { //quote here - is it single char or two quotes?
                                    waitForNextQuote = true;
                                } else {
                                    stringThisElement += thisOneChar;
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            scanner.close();
        }

//make this so that it returns doc and not text
        root.setAttribute("maxColumnNumber", maxI.toString());

        return doc;
    }

    public InternalDocColumnSchema getColumnSchema() {
        return null;
    }

    void read_deletethislater() throws IOException {
        log_deletethislater("Reading from file.");
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(fileFileName), fileEncoding);




        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append(NL);




            }
        } finally {
            scanner.close();




        }
        log_deletethislater("Text read in: " + text);




    }

    private void log_deletethislater(String messageOut) {
        System.out.println(messageOut);
    }
}
