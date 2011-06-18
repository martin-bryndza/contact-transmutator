package contacttransmut;


/*
 * Interface for output filter.
 * Output filter takes "CompiledDoc" Document from compiler and writes it into a file.
 * 
 * constructor should have parameters to allow choosing some parameters (see below)
 * there is only write() method that returns nothing
 * OutputFilter doesn’t detect file writing problems, this should be checked once in GUI before calling any OutputFilter
 *
 * CONSTRUCTOR: should allow to choose filename, encoding and filetype-specific parameters
 * constructor should accept "CompiledDoc" Document (as produced by InternalDoc2CompiledDoc) and filePath
 *
 * @author jakub svoboda
 */
public interface OutputFilter {

    /**
     * Writes the CompiledDoc in appropriate format to the file specified in constructor
     */
    public void write();
}
