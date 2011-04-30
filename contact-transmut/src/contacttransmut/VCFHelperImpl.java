/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contacttransmut;

/**
 *
 * @author ovečka
 */

/*
 *
VCFname "Human" name    cardinality
N	Name	(0,1)
FN	Formatted_Name	(1,n)
NICKNAME	Nickname	(0,n)
PHOTO	Photograph	(0,n)
BDAY	Birthday	(0,1)
ADR	Delivery_Address	(0,n)
LABEL	Label_Address	(0,n)
TEL	Telephone	(0,n)
 *      type-param-tel = "text" / "voice" / "fax" / "cell" / "video"
                    / "pager" / "textphone" / iana-token / x-name
EMAIL	Email	(0,n)
MAILER	Email_Program (Optional)	(0,n)
TZ	Time_Zone	 (0,n)
GEO	Global_Positioning	(0,n)
TITLE	Title	(0,n)
ROLE	Role_or_occupation	(0,n)
LOGO	Logo	(0,n)
AGENT	Agent	DO NOT SUPPORT
ORG	Organization_Name_or_Organizational_unit	(0,n)
NOTE	Note	(0,n)
REV	Last_Revision	 (0,1)
SOUND	Sound	(0,n)
URL	URL	(0,n)
UID	Unique_Identifier	(0,1)
VERSION	Version	 (1,1)	!This should be unavailable in GUI!
KEY	Public_Key	(0,n)

 *
 * http://tools.ietf.org/html/draft-ietf-vcarddav-vcardrev-12#page-6
 *
 * VCF supports many tags, only some of them are commonly used, however
 * to simplify tag handling, we used the "human" identifiers with type-tags appended
 * in the program (see implementation below)
 * GUI can display the string directly (with only _->space transformation)
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

    /*
     *
     */
    public boolean vcfCanHaveMultipleInstances(String type) {
        boolean answer = false;
        if (type.equals("Name"))                                          answer = false;
        if (type.equals("Formatted_Name"))                                answer = true;
        if (type.equals("Nickname"))                                      answer = true;
        if (type.equals("Nickname_work"))                                 answer = true;
        if (type.equals("Nickname_home"))                                 answer = true;
        if (type.equals("Photograph"))                                    answer = true;
        if (type.equals("Photograph_work"))                               answer = true;
        if (type.equals("Photograph_home"))                               answer = true;
        if (type.equals("Birthday"))                                      answer = false;
        if (type.equals("Delivery_Address"))                              answer = true;
        if (type.equals("Delivery_Address_work"))                         answer = true;
        if (type.equals("Delivery_Address_home"))                         answer = true;
        if (type.equals("Label_Address"))                                 answer = true;
        if (type.equals("Label_Address_work"))                            answer = true;
        if (type.equals("Label_Address_home"))                            answer = true;
        if (type.equals("Telephone"))                                     answer = true;
        if (type.equals("Telephone_text"))                                answer = true;
        if (type.equals("Telephone_voice"))                               answer = true;
        if (type.equals("Telephone_fax"))                                 answer = true;
        if (type.equals("Telephone_cell"))                                answer = true;
        if (type.equals("Telephone_video"))                               answer = true;
        if (type.equals("Telephone_pager"))                               answer = true;
        if (type.equals("Telephone_textphone"))                           answer = true;
        if (type.equals("Telephone_work"))                                answer = true;
        if (type.equals("Telephone_work_text"))                           answer = true;
        if (type.equals("Telephone_work_voice"))                          answer = true;
        if (type.equals("Telephone_work_fax"))                            answer = true;
        if (type.equals("Telephone_work_cell"))                           answer = true;
        if (type.equals("Telephone_work_video"))                          answer = true;
        if (type.equals("Telephone_work_pager"))                          answer = true;
        if (type.equals("Telephone_work_textphone"))                      answer = true;
        if (type.equals("Telephone_home"))                                answer = true;
        if (type.equals("Telephone_home_text"))                           answer = true;
        if (type.equals("Telephone_home_voice"))                          answer = true;
        if (type.equals("Telephone_home_fax"))                            answer = true;
        if (type.equals("Telephone_home_cell"))                           answer = true;
        if (type.equals("Telephone_home_video"))                          answer = true;
        if (type.equals("Telephone_home_pager"))                          answer = true;
        if (type.equals("Telephone_home_textphone"))                      answer = true;
        if (type.equals("Email"))                                         answer = true;
        if (type.equals("Email_work"))                                    answer = true;
        if (type.equals("Email_home"))                                    answer = true;
        if (type.equals("Email_Program"))                                 answer = true;
        if (type.equals("Time_Zone_text"))                                answer = true;
        if (type.equals("Time_Zone_uri"))                                 answer = true;
        if (type.equals("Time_Zone_utc_offset"))                          answer = true;
        if (type.equals("Global_Positioning"))                            answer = true;
        if (type.equals("Global_Positioning_work"))                       answer = true;
        if (type.equals("Global_Positioning_home"))                       answer = true;
        if (type.equals("Title"))                                         answer = true;
        if (type.equals("Title_work"))                                    answer = true;
        if (type.equals("Title_home"))                                    answer = true;
        if (type.equals("Role_or_occupation"))                            answer = true;
        if (type.equals("Role_or_occupation_work"))                       answer = true;
        if (type.equals("Role_or_occupation_home"))                       answer = true;
        if (type.equals("Logo"))                                          answer = true;
        if (type.equals("Logo_work"))                                     answer = true;
        if (type.equals("Logo_home"))                                     answer = true;
        if (type.equals("Organization_Name_or_Organizational_unit"))      answer = true;
        if (type.equals("Organization_Name_or_Organizational_unit_work")) answer = true;
        if (type.equals("Organization_Name_or_Organizational_unit_home")) answer = true;
        if (type.equals("Note"))                                          answer = true;
        if (type.equals("Note_work"))                                     answer = true;
        if (type.equals("Note_home"))                                     answer = true;
        if (type.equals("Last_Revision"))                                 answer = false;
        if (type.equals("Sound"))                                         answer = true;
        if (type.equals("Sound_work"))                                    answer = true;
        if (type.equals("Sound_home"))                                    answer = true;
        if (type.equals("URL"))                                           answer = true;
        if (type.equals("URL_work"))                                      answer = true;
        if (type.equals("URL_home"))                                      answer = true;
        if (type.equals("Unique_Identifier"))                             answer = false;
        if (type.equals("Public_Key"))                                    answer = true;
        if (type.equals("Public_Key_work"))                               answer = true;
        if (type.equals("Public_Key_home"))                               answer = true;
        return answer;
    }

}
