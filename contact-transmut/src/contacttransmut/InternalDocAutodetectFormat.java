package contacttransmut;

/**
 *
 * @author ovečka
 */

/*
 * This class should take "InternalDoc" Document and produce InternalDocColumnSchema with CandidateTypes autodetected
 */
public interface InternalDocAutodetectFormat {

    public InternalDocColumnSchema autodetect();

    public String autodetectColumn(Integer columnNumber);

    public String autodetectString(String string);

}
