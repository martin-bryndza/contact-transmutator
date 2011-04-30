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
public interface InternalDoc2CompiledDoc {

    public void compile();

    public Document getCompiledValidContacts();

    public Document getCompiledInvalidContacts();

    public boolean compileErrorsDetected();
}
