package contacttransmut;

/**
 *
 * @author Martin Molnar
 */
public class VCFConverter {

    /*
     * Method returns natural name for VCF tag.
     * @return Natural name for VCF tag.
     */
    public String VCFNameToNaturalName(String VCFName) {
        String naturalName = "Note";

        String[] arr = VCFName.split(";");
        VCFName = arr[0];

        if (VCFName.compareToIgnoreCase("N") == 0)          naturalName = "Name";
        if (VCFName.compareToIgnoreCase("FN") == 0)         naturalName = "Formatted_Name";
        if (VCFName.compareToIgnoreCase("NICKNAME") == 0)   naturalName = "Nickname";
        if (VCFName.compareToIgnoreCase("PHOTO") == 0)      naturalName = "Photograph";
        if (VCFName.compareToIgnoreCase("BDAY") == 0)       naturalName = "Birthday";
        if (VCFName.compareToIgnoreCase("ADR") == 0)        naturalName = "Delivery_Address";
        if (VCFName.compareToIgnoreCase("LABEL") == 0)      naturalName = "Label_Address";
        if (VCFName.compareToIgnoreCase("TEL") == 0)        naturalName = "Telephone";
        if (VCFName.compareToIgnoreCase("EMAIL") == 0)      naturalName = "Email";
        if (VCFName.compareToIgnoreCase("MAILER") == 0)     naturalName = "Email_Program";
        if (VCFName.compareToIgnoreCase("TZ") == 0)         naturalName = "Time_Zone";
        if (VCFName.compareToIgnoreCase("GEO") == 0)        naturalName = "Global_Positioning";
        if (VCFName.compareToIgnoreCase("TITLE") == 0)      naturalName = "Title";
        if (VCFName.compareToIgnoreCase("ROLE") == 0)       naturalName = "Role_or_occupation";
        if (VCFName.compareToIgnoreCase("ORG") == 0)        naturalName = "Organization_Name_or_Organizational_unit";
        if (VCFName.compareToIgnoreCase("NOTE") == 0)       naturalName = "Note";
        if (VCFName.compareToIgnoreCase("REV") == 0)        naturalName = "Last_Revision";
        if (VCFName.compareToIgnoreCase("SOUND") == 0)      naturalName = "Sound";
        if (VCFName.compareToIgnoreCase("URL") == 0)        naturalName = "URL";
        if (VCFName.compareToIgnoreCase("UID") == 0)        naturalName = "Unique_Identifier";
        if (VCFName.compareToIgnoreCase("KEY") == 0)        naturalName = "Public_Key";

        return naturalName;
    }
}
