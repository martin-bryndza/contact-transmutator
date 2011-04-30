/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

/**
 *
 * @author ovečka
 */

/* PLEASE READ THIS!
 * This class should have two tasks:
 *
 * Task 1
 * ======
 * VCFHelper is queried whether there can be more fields of XXXXX type per contact
 *  - returns true/false
 *
 * Task 2
 * ======
 * There is no method for this task, but pay attention:
 * There should be a list of VCF data types in VCFHelperImpl. (Kuba must finish the work!)
 * Others should take this list and use it in their classes. For instance in GUI.
 * The GUI should only allow user to choose columns that are from this list and disable
 * "next" button if there are some columns without "SelectedType" from this list!
 * If this is fulfilled, there is no need to check VCF conformity in every damn class then,
 * so please respect this!
 *
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
