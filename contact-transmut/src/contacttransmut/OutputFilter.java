package contacttransmut;

/**
 *
 * @author jakub svoboda
 */

/*
 * this is output filter
 * constructor should have parameters to allow choosing some parameters (see below)
 * there is only write() method that returns nothing
 * OutputFilter doesn’t detect file writing problems, this should be checked once in GUI before calling any OutputFilter
 */
public interface OutputFilter {
    //CONSTRUCTOR: should allow to choose filename, encoding and filetype-specific parameters
    //constructor should accept "CompiledDoc" Document (as produced by InternalDoc2CompiledDoc) and filePath

    //writes the CompiledDoc in appropriate format to the file got in constructor
    public void write();
}
