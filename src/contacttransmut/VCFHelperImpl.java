/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

/**
 *
 * @author ovečka
 */
public class VCFHelperImpl implements VCFHelper {
    private InternalDocColumnSchema  docColumnSchema; //InternalDocColumnSchemaImpl for the ReadText-produced document (must be valid)

    VCFHelperImpl(InternalDocColumnSchema newSchema) {
        docColumnSchema = newSchema;
    }

    public boolean vcfCanHaveMultipleInstances(Integer columnNumber) {
        boolean answer = false;

        return false;
    }

    public boolean vcfCanHaveMultipleInstances(String type) {
        boolean answer = false;

        return answer;
    }

}
