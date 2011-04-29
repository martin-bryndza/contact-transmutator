/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

import org.w3c.dom.Document;

/**
 *
 * @author ovečka
 */
public interface InputFilter {
    //should allow to choose filename, encoding and filetype-specific parameters
    //Document format should be exactly the same as in ReadCSV implementation
    //If the loaded file has some schema information (what is phone, address, etc), then create non-blank InternalDocColumnSchema
     public Document read();

     //if no columnschema was read, should return null!
     public InternalDocColumnSchema getColumnSchema();

}
