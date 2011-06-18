
package contacttransmut;

/**
 * VCF Helper that helps with knowledge of what type can have multiple instances in VCF (cardinality)
 *
 * VCFHelper is queried whether there can be more fields of XXXXX type per contact
 *  - returns true/false
 *
 *
 * @author jakub svoboda
 */
public interface VCFHelper {
    //constructor should take InternalDocColumnSchema

    public boolean vcfCanHaveMultipleInstances(Integer columnNumber);

    /*
     * this should NOT be used if possible, use VCFTypesEnum version rather
     */
    public boolean vcfCanHaveMultipleInstances(String type); //could be used as static, Java doesn’t allow static in interface, though

    /*
     * this enum version is good for checking typos while coding
     */
    public boolean vcfCanHaveMultipleInstances(VCFTypesEnum type);
}
