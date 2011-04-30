/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contacttransmut;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ovečka
 */
public interface InternalDocColumnSchema {

    public boolean isColumnMergedInOther(Integer colNumber);

    public boolean isColumnAggregated(Integer colNumber);

    public boolean columnAggregateOn(Integer colNum, String delimiter, Integer numberofcolumns,
            boolean intoseparatecontacts, boolean employees, boolean originalsourcenote,
            boolean originaltargetnote, String separatecontactsdelimiter, boolean autodetectswaps);

    public boolean columnAggregateOff(Integer colNum);

    public String queryAggregateSettingDelimiter(Integer colNum);

    public Integer queryAggregateSettingNumberofcolumns(Integer colNum);

    public boolean queryAggregateSettingIntoseparatecontacts(Integer colNum);

    public boolean queryAggregateSettingEmployees(Integer colNum);

    public boolean queryAggregateSettingOriginalsourcenote(Integer colNum);

    public boolean queryAggregateSettingOriginaltargetnote(Integer colNum);

    //only meaningful if queryAggregateSettingIntoseparatecontacts
    public String queryAggregateSettingSeparatecontactsdelimiter(Integer colNum);

    //only meaningful if queryAggregateSettingIntoseparatecontacts
    public boolean queryAggregateSettingAutodetectswaps(Integer colNum);

    public boolean changeAggregateSettingDelimiter(Integer colNum, String delimiter);

    public boolean changeAggregateSettingNumberofcolumns(Integer colNum, Integer numberofcolumns);

    //This will delete corresponding "separatecontacts" subelement!
    public boolean changeAggregateSettingIntoseparatecontactsOff(Integer colNum);

    //This will create corresponding "separatecontacts" subelement
    public boolean changeAggregateSettingIntoseparatecontactsOn(Integer colNum, String delimiter, boolean autodetectswaps);

    public boolean changeAggregateSettingEmployees(Integer colNum, boolean employees);

    public boolean changeAggregateSettingOriginalsourcenote(Integer colNum, boolean originalinsourcenote);

    public boolean changeAggregateSettingOriginaltargetnote(Integer colNum, boolean originalintargetnote);

    public boolean changeAggregateSettingSeparatecontactsdelimiter(Integer colNum, String delimiter);

    public boolean changeAggregateSettingAutodetectswaps(Integer colNum, boolean autodetectswaps);

    public boolean columnMergeOn(Integer colNum, Integer mergeiniset, Integer order);

    public boolean columnMergeOff(Integer colNum);

    public Integer queryMergeSet(Integer colNum);

    public Integer queryMergeOrder(Integer colNum);

    public boolean createMergeset(Integer num, String newDelimiter);

    public boolean deleteMergeset(Integer num);

    public void setMergesetCandidateType(Integer mergesetNum, String type);

    public void setMergesetSelectedType(Integer mergesetNum, String type);

    public void changeMergesetDelimiter(Integer mergesetNum, String delim);

    public String queryMergesetCandidateType(Integer mergesetNum);

    public String queryMergesetSelectedType(Integer mergesetNum);

    public String queryMergesetDelimiter(Integer mergesetNum);

    public void setCandidateType(Integer colNum, String type);

    public void setSelectedtypeType(Integer colNum, String type);

    public String queryCandidateType(Integer colNum);

    public String querySelectedtypeType(Integer colNum);

    //colNum - number of column with attribute "aggregated"
    //colNum2 - number of the //columnschema/column/aggregatedcolumns/column column
    public void setAggregatedCandidateType(Integer colNum, Integer colNum2, String type);

    public void setAggregatedSelectedtypeType(Integer colNum, Integer colNum2, String type);

    public String queryAggregatedCandidateType(Integer colNum, Integer colNum2);

    public String queryAggregatedSelectedtypeType(Integer colNum, Integer colNum2);

    public ArrayList<Integer> getAllMergesets();

    //<order, columnNumber>
    public HashMap<Integer, Integer> getAllMergesetMembers(Integer mergeset);
}
