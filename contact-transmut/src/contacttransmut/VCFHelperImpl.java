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
        if (type.equals("Nickname-work"))                                 answer = true;
        if (type.equals("Nickname-home"))                                 answer = true;
        if (type.equals("Photograph"))                                    answer = true;
        if (type.equals("Photograph-work"))                               answer = true;
        if (type.equals("Photograph-home"))                               answer = true;
        if (type.equals("Birthday"))                                      answer = false;
        if (type.equals("Delivery_Address"))                              answer = true;
        if (type.equals("Delivery_Address-work"))                         answer = true;
        if (type.equals("Delivery_Address-home"))                         answer = true;
        if (type.equals("Label_Address"))                                 answer = true;
        if (type.equals("Label_Address-work"))                            answer = true;
        if (type.equals("Label_Address-home"))                            answer = true;
        if (type.equals("Telephone"))                                     answer = true;
        if (type.equals("Telephone-text"))                                answer = true;
        if (type.equals("Telephone-voice"))                               answer = true;
        if (type.equals("Telephone-fax"))                                 answer = true;
        if (type.equals("Telephone-cell"))                                answer = true;
        if (type.equals("Telephone-video"))                               answer = true;
        if (type.equals("Telephone-pager"))                               answer = true;
        if (type.equals("Telephone-textphone"))                           answer = true;
        if (type.equals("Telephone-work"))                                answer = true;
        if (type.equals("Telephone-work-text"))                           answer = true;
        if (type.equals("Telephone-work-voice"))                          answer = true;
        if (type.equals("Telephone-work-fax"))                            answer = true;
        if (type.equals("Telephone-work-cell"))                           answer = true;
        if (type.equals("Telephone-work-video"))                          answer = true;
        if (type.equals("Telephone-work-pager"))                          answer = true;
        if (type.equals("Telephone-work-textphone"))                      answer = true;
        if (type.equals("Telephone-home"))                                answer = true;
        if (type.equals("Telephone-home-text"))                           answer = true;
        if (type.equals("Telephone-home-voice"))                          answer = true;
        if (type.equals("Telephone-home-fax"))                            answer = true;
        if (type.equals("Telephone-home-cell"))                           answer = true;
        if (type.equals("Telephone-home-video"))                          answer = true;
        if (type.equals("Telephone-home-pager"))                          answer = true;
        if (type.equals("Telephone-home-textphone"))                      answer = true;
        if (type.equals("Email"))                                         answer = true;
        if (type.equals("Email-work"))                                    answer = true;
        if (type.equals("Email-home"))                                    answer = true;
        if (type.equals("Email_Program"))                                 answer = true;
        if (type.equals("Time_Zone-text"))                                answer = true;
        if (type.equals("Time_Zone-uri"))                                 answer = true;
        if (type.equals("Time_Zone-utc-offset"))                          answer = true;
        if (type.equals("Global_Positioning"))                            answer = true;
        if (type.equals("Global_Positioning-work"))                       answer = true;
        if (type.equals("Global_Positioning-home"))                       answer = true;
        if (type.equals("Title"))                                         answer = true;
        if (type.equals("Title-work"))                                    answer = true;
        if (type.equals("Title-home"))                                    answer = true;
        if (type.equals("Role_or_occupation"))                            answer = true;
        if (type.equals("Role_or_occupation-work"))                       answer = true;
        if (type.equals("Role_or_occupation-home"))                       answer = true;
        if (type.equals("Logo"))                                          answer = true;
        if (type.equals("Logo-work"))                                     answer = true;
        if (type.equals("Logo-home"))                                     answer = true;
        if (type.equals("Organization_Name_or_Organizational_unit"))      answer = true;
        if (type.equals("Organization_Name_or_Organizational_unit-work")) answer = true;
        if (type.equals("Organization_Name_or_Organizational_unit-home")) answer = true;
        if (type.equals("Note"))                                          answer = true;
        if (type.equals("Note-work"))                                     answer = true;
        if (type.equals("Note-home"))                                     answer = true;
        if (type.equals("Last_Revision"))                                 answer = false;
        if (type.equals("Sound"))                                         answer = true;
        if (type.equals("Sound-work"))                                    answer = true;
        if (type.equals("Sound-home"))                                    answer = true;
        if (type.equals("URL"))                                           answer = true;
        if (type.equals("URL-work"))                                      answer = true;
        if (type.equals("URL-home"))                                      answer = true;
        if (type.equals("Unique_Identifier"))                             answer = false;
        if (type.equals("Public_Key"))                                    answer = true;
        if (type.equals("Public_Key-work"))                               answer = true;
        if (type.equals("Public_Key-home"))                               answer = true;
        return answer;
    }

}
