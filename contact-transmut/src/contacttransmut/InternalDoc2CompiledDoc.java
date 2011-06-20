
package contacttransmut;

import org.w3c.dom.Document;


/**
 * Interface for InternalDocCompiler
 *
 * Compiler should accept format from input file filters and produce Documents readable by output file filters.
 * There is only one compiler currently - InternalDocCompiler, but it may be more feasible to write a new compiler for entirely different input filter than adapt existing compiler.
 *
 * More info:
 * This class should take "InternalDoc" Document and InternalDocColumnSchema in the constructor
 * Then (on compile() invokation) it creates "CompiledDoc" Documents
 *  * getCompiledValidContacts() returns valid document (it conforms to VCF and is easily convertable to VCF)
 *  * getCompiledInvalidContacts() returns the same document format, but it only contains contacts with some error
 *    (for instance with two XXXX fields whereas VCF allows only one XXXX field per contact)
 *    (this is not currently used)
 *
 * @author Jakub Svoboda
 */
public interface InternalDoc2CompiledDoc {

    /**
     * Compile the data specified in constructor.
     *
     * Retrieval of the compiled data is done by the corresponding methods.
     *
     */
    public void compile();

    public Integer getCurrentStatus();

    public Integer getMaxContacts();

    /**
     * Get compiled valid contacts.
     *
     * Use this method to retrieve valid compiled contacts after calling compile() method.
     *
     * @return Document intended for use by output filters ("CompiledDoc")
     */
    public Document getCompiledValidContacts();

    /**
     * Get compiled invalid contacts.
     *
     * Use this method to retrieve invalid compiled contacts after calling compile() method.
     *
     * @return Document intended for use by output filters ("CompiledDoc"), but not necessarily valid - it contains errors
     */
    public Document getCompiledInvalidContacts();

    /**
     * Returns true if there were errors detected during compilation
     *
     * Handy for compilers that can produce errors.
     *
     * @return boolean true or false - true if errors detected
     */
    public boolean compileErrorsDetected();
}
