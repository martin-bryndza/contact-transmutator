
package contacttransmut;

import org.w3c.dom.Document;

/**
 *
 * @author jakub svoboda
 */

/*
 * This class should take "InternalDoc" Document and InternalDocColumnSchema in the constructor
 * Then (on compile() invokation) it creates "CompiledDoc" Documents
 *  * getCompiledValidContacts() returns valid document (it conforms to VCF and is easily convertable to VCF)
 *  * getCompiledInvalidContacts() returns the same document format, but it only contains contacts with some error
 *    (for instance with two XXXX fields whereas VCF allows only one XXXX field per contact)
 * 
 */
public interface InternalDoc2CompiledDoc {

    public void compile();

    public Document getCompiledValidContacts();

    public Document getCompiledInvalidContacts();

    public boolean compileErrorsDetected();
}
