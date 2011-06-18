package contacttransmut;

/*
 * This class should take "InternalDoc" Document and produce InternalDocColumnSchema with CandidateTypes autodetected
 */
/**
 * This class should take "InternalDoc" Document and produce InternalDocColumnSchema with CandidateTypes autodetected
 *
 * @author Jakub Svoboda
 */
public interface InternalDocAutodetectFormat {

    /**
     * Automatically detects probable types for columns and produces InternalDocColumnSchema
     *
     * @return InternalDocColumnSchema autodeteced
     */
    public InternalDocColumnSchema autodetect();

    /**
     * Detects probable type of given column
     *
     * @param columnNumber column to autodetect
     * @return String probable type
     */
    public String autodetectColumn(Integer columnNumber);

    /**
     * Detects probable type of given string
     *
     * @param string data to autodetect
     * @return String probable type
     */
    public String autodetectString(String string);

}
