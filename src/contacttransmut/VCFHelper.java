/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

/**
 *
 * @author ovečka
 */
public interface VCFHelper {

    public boolean vcfCanHaveMultipleInstances(Integer columnNumber);

    public boolean vcfCanHaveMultipleInstances(String type); //could be used as static, Java doesn’t allow static in interface, though
}
