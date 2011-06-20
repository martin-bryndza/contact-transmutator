package contacttransmut;

import java.util.ArrayList;

/**
 * Class containing two static methods that helps convert VCF names to natural names and the other way around.
 *
 * @author Martin Molnar
 */
public class VCFConverter {

    /*
     * Method returns natural names for VCF tag considering property TYPE.
     * If no property TYPE present returns simple name, otherwise returns home/work/both variations.
     * Process is the same with secondary properties. Returns simple name or text/phone/voice../all variations.
     *
     * @param VCF tag to convert
     * @return Natural names for VCF tag.
     */
    public static ArrayList<String> VCFNameToNaturalName(String VCFName, int VCFVersion) {
        
        ArrayList<String> naturalName = new ArrayList<String>();

        boolean home = false;
        boolean work = false;
        boolean text = false;
        boolean voice = false;
        boolean fax = false;
        boolean cell = false;
        boolean video = false;
        boolean pager = false;
        boolean textphone = false;
        boolean uri = false;
        boolean utc_offset = false;

        // Split string containing type;property;property..
        String[] arr = VCFName.split(";");
        VCFName = arr[0].toUpperCase();

        if (VCFVersion == 3) {
            // For ever property
            for (int i = 1; i < arr.length; i++) {
                // If it's TYPE property (we ignore the others)
                if (arr[i].toUpperCase().startsWith("TYPE=")) {
                    // Get property values
                    String[] values = arr[i].substring(5).split(",");
                    for (String property : values) {
                        if (property.compareToIgnoreCase("home") == 0)          home = true;
                        if (property.compareToIgnoreCase("work") == 0)          work = true;
                        if (property.compareToIgnoreCase("text") == 0)          text = true;
                        if (property.compareToIgnoreCase("voice") == 0)         voice = true;
                        if (property.compareToIgnoreCase("fax") == 0)           fax = true;
                        if (property.compareToIgnoreCase("cell") == 0)          cell = true;
                        if (property.compareToIgnoreCase("video") == 0)         video = true;
                        if (property.compareToIgnoreCase("pager") == 0)         pager = true;
                        if (property.compareToIgnoreCase("textphone") == 0)     textphone = true;
                        if (property.compareToIgnoreCase("uri") == 0)           uri = true;
                        if (property.compareToIgnoreCase("utc_offset") == 0)    utc_offset = true;
                    }
                }
            }
        }

        if (VCFName.compareTo("N") == 0)        naturalName.add("Name");
        if (VCFName.compareTo("FN") == 0)       naturalName.add("Formatted_Name");
        if (VCFName.compareTo("NICKNAME") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Nickname_work");
                if (home)   naturalName.add("Nickname_home");
            } else {
                            naturalName.add("Nickname");
            }
        }
        if (VCFName.compareTo("PHOTO") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Photograph_work");
                if (home)   naturalName.add("Photograph_home");
            } else {
                            naturalName.add("Photograph");
            }
        }
        if (VCFName.compareTo("BDAY") == 0)     naturalName.add("Birthday");
        if (VCFName.compareTo("ADR") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Delivery_Address_work");
                if (home)   naturalName.add("Delivery_Address_home");
            } else {
                            naturalName.add("Delivery_Address");
            }
        }
        if (VCFName.compareTo("LABEL") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Label_Address_work");
                if (home)   naturalName.add("Label_Address_home");
            } else {
                            naturalName.add("Label_Address");
            }
        }
        if (VCFName.compareTo("TEL") == 0) {
            if (work || home) {
                if (work) {
                    if (text || voice || fax || cell || video || pager || textphone) {
                        if (text)       naturalName.add("Telephone_work_text");
                        if (voice)      naturalName.add("Telephone_work_voice");
                        if (fax)        naturalName.add("Telephone_work_fax");
                        if (cell)       naturalName.add("Telephone_work_cell");
                        if (video)      naturalName.add("Telephone_work_video");
                        if (pager)      naturalName.add("Telephone_work_pager");
                        if (textphone)  naturalName.add("Telephone_work_textphone");
                    } else {
                                        naturalName.add("Telephone_work");
                    }
                }
                if (home) {
                    if (text || voice || fax || cell || video || pager || textphone) {
                        if (text)       naturalName.add("Telephone_home_text");
                        if (voice)      naturalName.add("Telephone_home_voice");
                        if (fax)        naturalName.add("Telephone_home_fax");
                        if (cell)       naturalName.add("Telephone_home_cell");
                        if (video)      naturalName.add("Telephone_home_video");
                        if (pager)      naturalName.add("Telephone_home_pager");
                        if (textphone)  naturalName.add("Telephone_home_textphone");
                    } else {
                                        naturalName.add("Telephone_home");
                    }
                }
            } else {
                if (text || voice || fax || cell || video || pager || textphone) {
                    if (text)       naturalName.add("Telephone_text");
                    if (voice)      naturalName.add("Telephone_voice");
                    if (fax)        naturalName.add("Telephone_fax");
                    if (cell)       naturalName.add("Telephone_cell");
                    if (video)      naturalName.add("Telephone_video");
                    if (pager)      naturalName.add("Telephone_pager");
                    if (textphone)  naturalName.add("Telephone_textphone");
                } else {
                                    naturalName.add("Telephone");
                }
            }
        }
        if (VCFName.compareTo("EMAIL") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Email_work");
                if (home)   naturalName.add("Email_home");
            } else {
                            naturalName.add("Email");
            }
        }
        if (VCFName.compareTo("MAILER") == 0)     naturalName.add("Email_Program");
        if (VCFName.compareTo("TZ") == 0) {
            if (text)       naturalName.add("Time_Zone_text");
            if (uri)        naturalName.add("Time_Zone_uri");
            if (utc_offset) naturalName.add("Time_Zone_utc_offset");
        }
        if (VCFName.compareTo("GEO") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Global_Positioning_work");
                if (home)   naturalName.add("Global_Positioning_home");
            } else {
                            naturalName.add("Global_Positioning");
            }
        }
        if (VCFName.compareTo("TITLE") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Title_work");
                if (home)   naturalName.add("Title_home");
            } else {
                            naturalName.add("Title");
            }
        }
        if (VCFName.compareTo("ROLE") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Role_or_occupation_work");
                if (home)   naturalName.add("Role_or_occupation_home");
            } else {
                            naturalName.add("Role_or_occupation");
            }
        }
        if (VCFName.compareTo("LOGO") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Logo_work");
                if (home)   naturalName.add("Logo_home");
            } else {
                            naturalName.add("Logo");
            }
        }
        if (VCFName.compareTo("ORG") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Organization_Name_or_Organizational_unit_work");
                if (home)   naturalName.add("Organization_Name_or_Organizational_unit_home");
            } else {
                            naturalName.add("Organization_Name_or_Organizational_unit");
            }
        }
        if (VCFName.compareTo("NOTE") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Note_work");
                if (home)   naturalName.add("Note_home");
            } else {
                            naturalName.add("Note");
            }
        }
        if (VCFName.compareTo("REV") == 0)     naturalName.add("Last_Revision");
        if (VCFName.compareTo("SOUND") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Sound_work");
                if (home)   naturalName.add("Sound_home");
            } else {
                            naturalName.add("Sound");
            }
        }
        if (VCFName.compareTo("REV") == 0)     naturalName.add("Last_Revision");
        if (VCFName.compareTo("URL") == 0) {
            if (work || home) {
                if (work)   naturalName.add("URL_work");
                if (home)   naturalName.add("URL_home");
            } else {
                            naturalName.add("URL");
            }
        }
        if (VCFName.compareTo("UID") == 0)     naturalName.add("Unique_Identifier");
        if (VCFName.compareTo("KEY") == 0) {
            if (work || home) {
                if (work)   naturalName.add("Public_Key_work");
                if (home)   naturalName.add("Public_Key_home");
            } else {
                            naturalName.add("Public_Key");
            }
        }

        if (naturalName.isEmpty()) naturalName.add("Note");

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