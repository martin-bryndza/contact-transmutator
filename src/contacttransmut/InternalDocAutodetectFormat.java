/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

/**
 *
 * @author ovečka
 */
public interface InternalDocAutodetectFormat {

    public InternalDocColumnSchema autodetect();

    public String autodetectColumn(Integer columnNumber);

    public String autodetectString(String string);

}
