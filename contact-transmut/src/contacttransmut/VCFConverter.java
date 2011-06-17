package contacttransmut;

/**
 * Class containing two static methods that helps convert VCF names to natural names and the other way around.
 *
 * @author Martin Molnar
 */
public class VCFConverter {

    /* TO DO: return name by specified by TYPE
     * Method returns natural name for VCF tag.
     *
     * @param VCF tag to convert
     * @return Natural name for VCF tag.
     */
    public static String VCFNameToNaturalName(String VCFName) {
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

    /*
     * Method returns VCF tag and parameter TYPE for natural name.
     *
     * @param naturalName natural name to convert
     * @return Natural name for VCF tag.
     */
    public static String NaturalNameToVCFName(String naturalName) {
        String VCFName = "NOTE";

        if (naturalName.compareToIgnoreCase("Name") == 0)                       VCFName = "N";
        if (naturalName.compareToIgnoreCase("Formatted_Name") == 0)             VCFName = "FN";
        if (naturalName.compareToIgnoreCase("Nickname") == 0)                   VCFName = "NICKNAME";
        if (naturalName.compareToIgnoreCase("Nickname_work") == 0)              VCFName = "NICKNAME;TYPE=work";
        if (naturalName.compareToIgnoreCase("Nickname_home") == 0)              VCFName = "NICKNAME;TYPE=home";
        if (naturalName.compareToIgnoreCase("Photograph") == 0)                 VCFName = "PHOTO";
        if (naturalName.compareToIgnoreCase("Photograph_work") == 0)            VCFName = "PHOTO;TYPE=work";
        if (naturalName.compareToIgnoreCase("Photograph_home") == 0)            VCFName = "PHOTO;TYPE=home";
        if (naturalName.compareToIgnoreCase("Birthday") == 0)                   VCFName = "BDAY";
        if (naturalName.compareToIgnoreCase("Delivery_Address") == 0)           VCFName = "ADR";
        if (naturalName.compareToIgnoreCase("Delivery_Address_work") == 0)      VCFName = "ADR;TYPE=work";
        if (naturalName.compareToIgnoreCase("Delivery_Address_home") == 0)      VCFName = "ADR;TYPE=home";
        if (naturalName.compareToIgnoreCase("Label_Address") == 0)              VCFName = "LABEL";
        if (naturalName.compareToIgnoreCase("Label_Address_work") == 0)         VCFName = "LABEL;TYPE=work";
        if (naturalName.compareToIgnoreCase("Label_Address_home") == 0)         VCFName = "LABEL;TYPE=home";
        if (naturalName.compareToIgnoreCase("Telephone") == 0)                  VCFName = "TEL";
        if (naturalName.compareToIgnoreCase("Telephone_text") == 0)             VCFName = "TEL;TYPE=text";
        if (naturalName.compareToIgnoreCase("Telephone_voice") == 0)            VCFName = "TEL;TYPE=voice";
        if (naturalName.compareToIgnoreCase("Telephone_fax") == 0)              VCFName = "TEL;TYPE=fax";
        if (naturalName.compareToIgnoreCase("Telephone_cell") == 0)             VCFName = "TEL;TYPE=cell";
        if (naturalName.compareToIgnoreCase("Telephone_video") == 0)            VCFName = "TEL;TYPE=video";
        if (naturalName.compareToIgnoreCase("Telephone_pager") == 0)            VCFName = "TEL;TYPE=pager";
        if (naturalName.compareToIgnoreCase("Telephone_textphone") == 0)        VCFName = "TEL;TYPE=textphone";
        if (naturalName.compareToIgnoreCase("Telephone_work") == 0)             VCFName = "TEL;TYPE=work";
        if (naturalName.compareToIgnoreCase("Telephone_work_text") == 0)        VCFName = "TEL;TYPE=work,text";
        if (naturalName.compareToIgnoreCase("Telephone_work_voice") == 0)       VCFName = "TEL;TYPE=work,voice";
        if (naturalName.compareToIgnoreCase("Telephone_work_fax") == 0)         VCFName = "TEL;TYPE=work,fax";
        if (naturalName.compareToIgnoreCase("Telephone_work_cell") == 0)        VCFName = "TEL;TYPE=work,cell";
        if (naturalName.compareToIgnoreCase("Telephone_work_video") == 0)       VCFName = "TEL;TYPE=work,video";
        if (naturalName.compareToIgnoreCase("Telephone_work_pager") == 0)       VCFName = "TEL;TYPE=work,pager";
        if (naturalName.compareToIgnoreCase("Telephone_work_textphone") == 0)   VCFName = "TEL;TYPE=work,textphone";
        if (naturalName.compareToIgnoreCase("Telephone_home") == 0)             VCFName = "TEL;TYPE=home";
        if (naturalName.compareToIgnoreCase("Telephone_home_text") == 0)        VCFName = "TEL;TYPE=home,text";
        if (naturalName.compareToIgnoreCase("Telephone_home_voice") == 0)       VCFName = "TEL;TYPE=home,voice";
        if (naturalName.compareToIgnoreCase("Telephone_home_fax") == 0)         VCFName = "TEL;TYPE=home,fax";
        if (naturalName.compareToIgnoreCase("Telephone_home_cell") == 0)        VCFName = "TEL;TYPE=home,cell";
        if (naturalName.compareToIgnoreCase("Telephone_home_video") == 0)       VCFName = "TEL;TYPE=home,video";
        if (naturalName.compareToIgnoreCase("Telephone_home_pager") == 0)       VCFName = "TEL;TYPE=home,pager";
        if (naturalName.compareToIgnoreCase("Telephone_home_textphone") == 0)   VCFName = "TEL;TYPE=home,textphone";
        if (naturalName.compareToIgnoreCase("Email") == 0)                      VCFName = "EMAIL";
        if (naturalName.compareToIgnoreCase("Email_work") == 0)                 VCFName = "EMAIL;TYPE=work";
        if (naturalName.compareToIgnoreCase("Email_home") == 0)                 VCFName = "EMAIL;TYPE=home";
        if (naturalName.compareToIgnoreCase("Email_Program") == 0)              VCFName = "MAILER";
        if (naturalName.compareToIgnoreCase("Time_Zone_text") == 0)             VCFName = "TZ;TYPE=text";
        if (naturalName.compareToIgnoreCase("Time_Zone_uri") == 0)              VCFName = "TZ;TYPE=uri";
        if (naturalName.compareToIgnoreCase("Time_Zone_utc_offset") == 0)       VCFName = "TZ;TYPE=utc_offset";
        if (naturalName.compareToIgnoreCase("Global_Positioning") == 0)         VCFName = "GEO";
        if (naturalName.compareToIgnoreCase("Global_Positioning_work") == 0)    VCFName = "GEO;TYPE=work";
        if (naturalName.compareToIgnoreCase("Global_Positioning_home") == 0)    VCFName = "GEO;TYPE=home";
        if (naturalName.compareToIgnoreCase("Title") == 0)                      VCFName = "TITLE";
        if (naturalName.compareToIgnoreCase("Title_work") == 0)                 VCFName = "TITLE;TYPE=work";
        if (naturalName.compareToIgnoreCase("Title_home") == 0)                 VCFName = "TITLE;TYPE=home";
        if (naturalName.compareToIgnoreCase("Role_or_occupation") == 0)         VCFName = "ROLE";
        if (naturalName.compareToIgnoreCase("Role_or_occupation_work") == 0)    VCFName = "ROLE;TYPE=work";
        if (naturalName.compareToIgnoreCase("Role_or_occupation_home") == 0)    VCFName = "ROLE;TYPE=home";
        if (naturalName.compareToIgnoreCase("Logo") == 0)                       VCFName = "LOGO";
        if (naturalName.compareToIgnoreCase("Logo_work") == 0)                  VCFName = "LOGO;TYPE=work";
        if (naturalName.compareToIgnoreCase("Logo_home") == 0)                  VCFName = "LOGO;TYPE=home";
        if (naturalName.compareToIgnoreCase("Organization_Name_or_Organizational_unit") == 0)       VCFName = "ORG";
        if (naturalName.compareToIgnoreCase("Organization_Name_or_Organizational_unit_work") == 0)  VCFName = "ORG;TYPE=work";
        if (naturalName.compareToIgnoreCase("Organization_Name_or_Organizational_unit_home") == 0)  VCFName = "ORG;TYPE=home";
        if (naturalName.compareToIgnoreCase("Note") == 0)                       VCFName = "NOTE";
        if (naturalName.compareToIgnoreCase("Note_work") == 0)                  VCFName = "NOTE;TYPE=work";
        if (naturalName.compareToIgnoreCase("Note_home") == 0)                  VCFName = "NOTE;TYPE=home";
        if (naturalName.compareToIgnoreCase("Last_Revision") == 0)              VCFName = "REV";
        if (naturalName.compareToIgnoreCase("Sound") == 0)                      VCFName = "SOUND";
        if (naturalName.compareToIgnoreCase("Sound_work") == 0)                 VCFName = "SOUND;TYPE=work";
        if (naturalName.compareToIgnoreCase("Sound_home") == 0)                 VCFName = "SOUND;TYPE=home";
        if (naturalName.compareToIgnoreCase("URL") == 0)                        VCFName = "URL";
        if (naturalName.compareToIgnoreCase("URL_work") == 0)                   VCFName = "URL;TYPE=work";
        if (naturalName.compareToIgnoreCase("URL_home") == 0)                   VCFName = "URL;TYPE=home";
        if (naturalName.compareToIgnoreCase("Unique_Identifier") == 0)          VCFName = "UID";
        if (naturalName.compareToIgnoreCase("Public_Key") == 0)                 VCFName = "KEY";
        if (naturalName.compareToIgnoreCase("Public_Key_work") == 0)            VCFName = "KEY;TYPE=work";
        if (naturalName.compareToIgnoreCase("Public_Key_home") == 0)            VCFName = "KEY;TYPE=home";

        return VCFName;
    }
}