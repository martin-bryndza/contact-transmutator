
package contacttransmut;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class describes data types (columns) in the raw InternalDoc
 *  * which column is which data type
 *  * which columns should be split and how
 *  * which columns should be merged and how
 *
 * It uses some internal XML DOM data structure that is only private
 * you should always use the methods to create, change and query the InternalDocColumnSchema
 *
 * @author Jakub Svoboda
 */
public interface InternalDocColumnSchema {

        public InternalDocColumnSchema returnClonedColumnSchema();
 
    /**
     * Is column merged in other column/mergeset?
     *
     * Merged columns are represented in a mergeset as a single column.
     *
     * @param colNumber column number
     * @return true if column is merged in mergeset, false if not merged
     */
    public boolean isColumnMergedInOther(Integer colNumber);

    /**
     * Is this column of aggregated data?
     *
     * Aggregated column is in fact more columns joined by a delimiter. Aggregated column will be split into separate columns.
     * @param colNumber column number
     * @return true if aggregated, false if not aggregated
     */
    public boolean isColumnAggregated(Integer colNumber);

    /**
     * Activate aggregation feature of column.
     * Aggregated column is in fact more columns joined by a delimiter. Aggregated column will be split into separate columns.
     * To change settings of this feature later, use *change* methods.
     * @param colNum column number
     * @param delimiter delimiter used to split the data into separate columns
     * @param numberofcolumns how many columns there in fact are in this column
     * @param intoseparatecontacts true if each newly separated part of this column shall be part of a new contact
     *                             false if this column is just split into multiple columns in the same contact
     * @param employees true if to assign original contact’s address to the related contacts (only applicable when intoseparatecontacts)
     * @param originalsourcenote whether the original contact’s source should be attached as a note in the resulting contacts (only applicable when intoseparatecontacts)
     * @param originaltargetnote whether the target contacts’ source should be attached as a note in the original contact (only applicable in intoseparatecontacts)
     * @param separatecontactsdelimiter delimiter of data between each contact’s-to-be-created data
     *        example: parent contact for company has column with data JANE, 121442; JAKE, 42232; JOHN, 434343
     *                 numberofcolumns should be set to 2 (first - name, second - number)
     *                 delimiter should be set to ,
     *                 separatecontactsdelimiter should be set to ;
     *                 3 new contacts will be generated this way
     * @param autodetectswaps if inconsistencies should be automatically detected and solved
     * @return true if successful/meaningful
     */
    public boolean columnAggregateOn(Integer colNum, String delimiter, Integer numberofcolumns,
            boolean intoseparatecontacts, boolean employees, boolean originalsourcenote,
            boolean originaltargetnote, String separatecontactsdelimiter, boolean autodetectswaps);

    /**
     * Turns off Aggregate feature of column.
     *
     * @param colNum number of column
     * @return true if successful/meaningful
     */
    public boolean columnAggregateOff(Integer colNum);

    /**
     * Returns delimiter for aggregated column
     * @param colNum column number
     * @return the delimiter
     */
    public String queryAggregateSettingDelimiter(Integer colNum);

    /**
     * Returns number of sub-columns for aggregated column
     * @param colNum column number
     * @return the number of sub-columns
     */
    public Integer queryAggregateSettingNumberofcolumns(Integer colNum);

    /**
     * Returns if intoseparatecontacts attribute for aggregated column is set
     * @param colNum column number
     * @return true if set, false otherwise or when not meaningful
     */
    public boolean queryAggregateSettingIntoseparatecontacts(Integer colNum);

    /**
     * Returns if the aggregated column has employees attribute set
     * @param colNum column number
     * @return true if set, false otherwise/when not meaningful
     */
    public boolean queryAggregateSettingEmployees(Integer colNum);

    /**
     * Returns if the aggregated column has originalsourcenote attribute set
     * @param colNum column number
     * @return true if set, false otherwise/when not meaningful
     */
    public boolean queryAggregateSettingOriginalsourcenote(Integer colNum);

    /**
     * Returns if the aggregated column has originaltargetnote attribute set
     * @param colNum
     * @return true if set, false otherwise/when not meaningful
     */
    public boolean queryAggregateSettingOriginaltargetnote(Integer colNum);

    /**
     * Returns the separatecontactsdelimiter of aggregated column
     * only meaningful if queryAggregateSettingIntoseparatecontacts
     * @param colNum column number
     * @return String of delimiter or null when not meaningful
     */
    public String queryAggregateSettingSeparatecontactsdelimiter(Integer colNum);

    /**
     * Returns true if aggregated column has autodetection of irregularities activated
     * only meaningful if queryAggregateSettingIntoseparatecontacts
     * @param colNum column number
     * @return true if set/false otherwise
     */
    public boolean queryAggregateSettingAutodetectswaps(Integer colNum);

    /**
     * Changes delimiter setting of aggregated column
     * @param colNum number of column
     * @param delimiter new delimiter
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingDelimiter(Integer colNum, String delimiter);

    /**
     * Changes number of sub-columns of aggregated column.
     * @param colNum number of column
     * @param numberofcolumns number of sub-columns
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingNumberofcolumns(Integer colNum, Integer numberofcolumns);

    /**
     * Deactivates intoseparatecontacts attribute of aggregated column and deletes associated settings for this attribute.
     * @param colNum number of column
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingIntoseparatecontactsOff(Integer colNum);

    /**
     * Activates intoseparatecontacts attribute for aggregated column.
     * @param colNum number of column
     * @param delimiter new delimiter
     * @param autodetectswaps true to autodetect swaps and irregularities, false (recommended) for deterministic results
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingIntoseparatecontactsOn(Integer colNum, String delimiter, boolean autodetectswaps);

    /**
     * Changes employees attribute for aggregated column
     * @param colNum number of column
     * @param employees true to set false to unset
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingEmployees(Integer colNum, boolean employees);

    /**
     * Changes originalsourcenote attribute for aggregated column
     * @param colNum number of column
     * @param originalinsourcenote true to set false to unset
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingOriginalsourcenote(Integer colNum, boolean originalinsourcenote);

    /**
     * Changes originaltargetnote attribute for aggregated column
     * @param colNum number of column
     * @param originalintargetnote true to set false to unset
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingOriginaltargetnote(Integer colNum, boolean originalintargetnote);

    /**
     * Changes separatecontacts delimiter for aggregated column
     * @param colNum number of column
     * @param delimiter new delimiter
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingSeparatecontactsdelimiter(Integer colNum, String delimiter);

    /**
     * Changes autodetection setting for aggregated column
     * @param colNum number of column
     * @param autodetectswaps true to activate, false to deactivate
     * @return true if successful/meaningful
     */
    public boolean changeAggregateSettingAutodetectswaps(Integer colNum, boolean autodetectswaps);

    /**
     * Activates merge feature for this column. Column will be included in a mergeset.
     * You must create at least one mergeset for this to be meaningful/valid.
     * @param colNum number of column
     * @param mergeiniset which mergeset to include in
     * @param order order in the mergeset
     * @return true if successful/meaningful
     */
    public boolean columnMergeOn(Integer colNum, Integer mergeiniset, Integer order);

    /**
     * Deactivates merge feature for column - removes column from a mergeset.
     * @param colNum number of column
     * @return true if successful/meaningful
     */
    public boolean columnMergeOff(Integer colNum);

    /**
     * Returns mergeset number in which the column is merged
     * @param colNum number of column
     * @return Integer of mergeset or null if not applicable
     */
    public Integer queryMergeSet(Integer colNum);

    /**
     * Returns order of column in its mergeset.
     * @param colNum number of column
     * @return Integer of order or null if not applicable
     */
    public Integer queryMergeOrder(Integer colNum);

    /**
     * Creates a new mergeset. You specify handling number of mergeset.
     * @param num number referring to the mergeset
     * @param newDelimiter delimiter that will be used to join data from the included columns
     * @return true if successful/meaningful
     */
    public boolean createMergeset(Integer num, String newDelimiter);

    /**
     * Deletes a mergeset and turns merge feature off for all member columns.
     * @param num number of mergeset
     * @return true if successful/meaningful
     */
    public boolean deleteMergeset(Integer num);

    /**
     * Sets candidate type for the mergeset. Candidate type is inferior to selected type and can be used for autodetection or by input filter.
     * @param mergesetNum number of mergeset
     * @param type type
     */
    public void setMergesetCandidateType(Integer mergesetNum, String type);

    /**
     * Sets selected type for the mergeset. Selected type is superior to candidatetype and will be used if present.
     * @param mergesetNum number of mergeset
     * @param type type
     */
    public void setMergesetSelectedType(Integer mergesetNum, String type);

    /**
     * Changes delimiter for mergeset. Delimiter will join the data from member columns.
     * @param mergesetNum number of mergeset
     * @param delim new delimiter
     */
    public void changeMergesetDelimiter(Integer mergesetNum, String delim);

    /**
     * Returns candidatetype of a mergeset.
     * @param mergesetNum number of mergeset
     * @return String of candidatetype or null if not applicable
     */
    public String queryMergesetCandidateType(Integer mergesetNum);

    /**
     * Returns selectedtype of a mergeset.
     * @param mergesetNum number of mergeset
     * @return String of selectedtype or null if not applicable
     */
    public String queryMergesetSelectedType(Integer mergesetNum);

    /**
     * Returns delimiter of a mergeset
     *
     * @param mergesetNum number of mergeset
     * @return String of delimiter or null if not applicable
     */
    public String queryMergesetDelimiter(Integer mergesetNum);

    /**
     * Sets candidate type of column. Candidate type is inferior to selected type and can be used for autodetection or by input filter.
     * @param colNum number of column
     * @param type
     */
    public void setCandidateType(Integer colNum, String type);

    /**
     * Sets selected type of column. Selected type is superior to candidatetype and will be used if present.
     * @param colNum number of column
     * @param type type
     */
    public void setSelectedtypeType(Integer colNum, String type);

    /**
     * Returns candidate type of column
     * @param colNum number of column
     * @return String of type or null if not applicable
     */
    public String queryCandidateType(Integer colNum);

    /**
     * Returns selected type of column
     * @param colNum number of column
     * @return String of type or null if not applicable
     */
    public String querySelectedtypeType(Integer colNum);

    /**
     * Sets candidate type for aggregated column’s sub-column. Candidate type is inferior to selected type and can be used for autodetection or by input filter.
     * @param colNum number of column - number of column with attribute "aggregated"
     * @param colNum2 number of sub-column - number of the //columnschema/column/aggregatedcolumns/column column
     * @param type type
     */
    public void setAggregatedCandidateType(Integer colNum, Integer colNum2, String type);

    /**
     * Sets selected type for aggregated column’s sub-column.  Selected type is superior to candidatetype and will be used if present.
     * @param colNum number of column
     * @param colNum2 number of sub-column
     * @param type type
     */
    public void setAggregatedSelectedtypeType(Integer colNum, Integer colNum2, String type);

    /**
     * Returns candidate type of aggregated column’s sub-column.
     * @param colNum number of column
     * @param colNum2 number of sub-column
     * @return String of type or null if not applicable
     */
    public String queryAggregatedCandidateType(Integer colNum, Integer colNum2);

    /**
     * Returns selected type of aggregated column’s sub-column.
     * @param colNum number of column
     * @param colNum2 number of sub-column
     * @return String of type or null if not applicable
     */
    public String queryAggregatedSelectedtypeType(Integer colNum, Integer colNum2);

    /**
     * Returns arraylist of all mergesets’ numbers.
     *
     * @return ArrayList<Integer> of all mergesets
     */
    public ArrayList<Integer> getAllMergesets();


    /**
     * Returns members of certain mergeset.
     * @param mergeset number of mergeset
     * @return HashMap of <order, columnNumber> for the mergest’s members
     */
    public HashMap<Integer, Integer> getAllMergesetMembers(Integer mergeset);

    //<editor-fold defaultstate="collapsed" desc="added by Martin B.">
    /**
     * Returns number of columns in this columnschema.
     * @return number of columns
     */
    public int getColumnCount();

    /**
     * Returns true if the queried type is used in this columnschema
     * @param type type
     * @return true if present, false if not found
     */
    public boolean isTypeInColumnSchema(VCFTypesEnum type);

    /**
     * overriden toString method for more comfortable debugging/dumps
     * @return
     */
    @Override
    public String toString();

    public void addColumn();

    //</editor-fold>
}
