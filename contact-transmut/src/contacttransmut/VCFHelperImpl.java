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
        if (type.equals(VCFTypesEnum.Name.toString()))                                          answer = false;
        if (type.equals(VCFTypesEnum.Formatted_Name.toString()))                                answer = true;
        if (type.equals(VCFTypesEnum.Nickname.toString()))                                      answer = true;
        if (type.equals(VCFTypesEnum.Nickname_work.toString()))                                 answer = true;
        if (type.equals(VCFTypesEnum.Nickname_home.toString()))                                 answer = true;
        if (type.equals(VCFTypesEnum.Photograph.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Photograph_work.toString()))                               answer = true;
        if (type.equals(VCFTypesEnum.Photograph_home.toString()))                               answer = true;
        if (type.equals(VCFTypesEnum.Birthday.toString()))                                      answer = false;
        if (type.equals(VCFTypesEnum.Delivery_Address.toString()))                              answer = true;
        if (type.equals(VCFTypesEnum.Delivery_Address_work.toString()))                         answer = true;
        if (type.equals(VCFTypesEnum.Delivery_Address_home.toString()))                         answer = true;
        if (type.equals(VCFTypesEnum.Label_Address.toString()))                                 answer = true;
        if (type.equals(VCFTypesEnum.Label_Address_work.toString()))                            answer = true;
        if (type.equals(VCFTypesEnum.Label_Address_home.toString()))                            answer = true;
        if (type.equals(VCFTypesEnum.Telephone.toString()))                                     answer = true;
        if (type.equals(VCFTypesEnum.Telephone_text.toString()))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_voice.toString()))                               answer = true;
        if (type.equals(VCFTypesEnum.Telephone_fax.toString()))                                 answer = true;
        if (type.equals(VCFTypesEnum.Telephone_cell.toString()))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_video.toString()))                               answer = true;
        if (type.equals(VCFTypesEnum.Telephone_pager.toString()))                               answer = true;
        if (type.equals(VCFTypesEnum.Telephone_textphone.toString()))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work.toString()))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_text.toString()))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_voice.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_fax.toString()))                            answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_cell.toString()))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_video.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_pager.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_textphone.toString()))                      answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home.toString()))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_text.toString()))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_voice.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_fax.toString()))                            answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_cell.toString()))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_video.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_pager.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_textphone.toString()))                      answer = true;
        if (type.equals(VCFTypesEnum.Email.toString()))                                         answer = true;
        if (type.equals(VCFTypesEnum.Email_work.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Email_home.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Email_Program.toString()))                                 answer = true;
        if (type.equals(VCFTypesEnum.Time_Zone_text.toString()))                                answer = true;
        if (type.equals(VCFTypesEnum.Time_Zone_uri.toString()))                                 answer = true;
        if (type.equals(VCFTypesEnum.Time_Zone_utc_offset.toString()))                          answer = true;
        if (type.equals(VCFTypesEnum.Global_Positioning.toString()))                            answer = true;
        if (type.equals(VCFTypesEnum.Global_Positioning_work.toString()))                       answer = true;
        if (type.equals(VCFTypesEnum.Global_Positioning_home.toString()))                       answer = true;
        if (type.equals(VCFTypesEnum.Title.toString()))                                         answer = true;
        if (type.equals(VCFTypesEnum.Title_work.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Title_home.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Role_or_occupation.toString()))                            answer = true;
        if (type.equals(VCFTypesEnum.Role_or_occupation_work.toString()))                       answer = true;
        if (type.equals(VCFTypesEnum.Role_or_occupation_home.toString()))                       answer = true;
        if (type.equals(VCFTypesEnum.Logo.toString()))                                          answer = true;
        if (type.equals(VCFTypesEnum.Logo_work.toString()))                                     answer = true;
        if (type.equals(VCFTypesEnum.Logo_home.toString()))                                     answer = true;
        if (type.equals(VCFTypesEnum.Organization_Name_or_Organizational_unit.toString()))      answer = true;
        if (type.equals(VCFTypesEnum.Organization_Name_or_Organizational_unit_work.toString())) answer = true;
        if (type.equals(VCFTypesEnum.Organization_Name_or_Organizational_unit_home.toString())) answer = true;
        if (type.equals(VCFTypesEnum.Note.toString()))                                          answer = true;
        if (type.equals(VCFTypesEnum.Note_work.toString()))                                     answer = true;
        if (type.equals(VCFTypesEnum.Note_home.toString()))                                     answer = true;
        if (type.equals(VCFTypesEnum.Last_Revision.toString()))                                 answer = false;
        if (type.equals(VCFTypesEnum.Sound.toString()))                                         answer = true;
        if (type.equals(VCFTypesEnum.Sound_work.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Sound_home.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.URL.toString()))                                           answer = true;
        if (type.equals(VCFTypesEnum.URL_work.toString()))                                      answer = true;
        if (type.equals(VCFTypesEnum.URL_home.toString()))                                      answer = true;
        if (type.equals(VCFTypesEnum.Unique_Identifier.toString()))                             answer = false;
        if (type.equals(VCFTypesEnum.Public_Key.toString()))                                    answer = true;
        if (type.equals(VCFTypesEnum.Public_Key_work.toString()))                               answer = true;
        if (type.equals(VCFTypesEnum.Public_Key_home.toString()))                               answer = true;
        return answer;
    }

        public boolean vcfCanHaveMultipleInstances(VCFTypesEnum type) {
        boolean answer = false;
        if (type.equals(VCFTypesEnum.Name))                                          answer = false;
        if (type.equals(VCFTypesEnum.Formatted_Name))                                answer = true;
        if (type.equals(VCFTypesEnum.Nickname))                                      answer = true;
        if (type.equals(VCFTypesEnum.Nickname_work))                                 answer = true;
        if (type.equals(VCFTypesEnum.Nickname_home))                                 answer = true;
        if (type.equals(VCFTypesEnum.Photograph))                                    answer = true;
        if (type.equals(VCFTypesEnum.Photograph_work))                               answer = true;
        if (type.equals(VCFTypesEnum.Photograph_home))                               answer = true;
        if (type.equals(VCFTypesEnum.Birthday))                                      answer = false;
        if (type.equals(VCFTypesEnum.Delivery_Address))                              answer = true;
        if (type.equals(VCFTypesEnum.Delivery_Address_work))                         answer = true;
        if (type.equals(VCFTypesEnum.Delivery_Address_home))                         answer = true;
        if (type.equals(VCFTypesEnum.Label_Address))                                 answer = true;
        if (type.equals(VCFTypesEnum.Label_Address_work))                            answer = true;
        if (type.equals(VCFTypesEnum.Label_Address_home))                            answer = true;
        if (type.equals(VCFTypesEnum.Telephone))                                     answer = true;
        if (type.equals(VCFTypesEnum.Telephone_text))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_voice))                               answer = true;
        if (type.equals(VCFTypesEnum.Telephone_fax))                                 answer = true;
        if (type.equals(VCFTypesEnum.Telephone_cell))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_video))                               answer = true;
        if (type.equals(VCFTypesEnum.Telephone_pager))                               answer = true;
        if (type.equals(VCFTypesEnum.Telephone_textphone))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_text))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_voice))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_fax))                            answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_cell))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_video))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_pager))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_work_textphone))                      answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home))                                answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_text))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_voice))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_fax))                            answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_cell))                           answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_video))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_pager))                          answer = true;
        if (type.equals(VCFTypesEnum.Telephone_home_textphone))                      answer = true;
        if (type.equals(VCFTypesEnum.Email))                                         answer = true;
        if (type.equals(VCFTypesEnum.Email_work))                                    answer = true;
        if (type.equals(VCFTypesEnum.Email_home))                                    answer = true;
        if (type.equals(VCFTypesEnum.Email_Program))                                 answer = true;
        if (type.equals(VCFTypesEnum.Time_Zone_text))                                answer = true;
        if (type.equals(VCFTypesEnum.Time_Zone_uri))                                 answer = true;
        if (type.equals(VCFTypesEnum.Time_Zone_utc_offset))                          answer = true;
        if (type.equals(VCFTypesEnum.Global_Positioning))                            answer = true;
        if (type.equals(VCFTypesEnum.Global_Positioning_work))                       answer = true;
        if (type.equals(VCFTypesEnum.Global_Positioning_home))                       answer = true;
        if (type.equals(VCFTypesEnum.Title))                                         answer = true;
        if (type.equals(VCFTypesEnum.Title_work))                                    answer = true;
        if (type.equals(VCFTypesEnum.Title_home))                                    answer = true;
        if (type.equals(VCFTypesEnum.Role_or_occupation))                            answer = true;
        if (type.equals(VCFTypesEnum.Role_or_occupation_work))                       answer = true;
        if (type.equals(VCFTypesEnum.Role_or_occupation_home))                       answer = true;
        if (type.equals(VCFTypesEnum.Logo))                                          answer = true;
        if (type.equals(VCFTypesEnum.Logo_work))                                     answer = true;
        if (type.equals(VCFTypesEnum.Logo_home))                                     answer = true;
        if (type.equals(VCFTypesEnum.Organization_Name_or_Organizational_unit))      answer = true;
        if (type.equals(VCFTypesEnum.Organization_Name_or_Organizational_unit_work)) answer = true;
        if (type.equals(VCFTypesEnum.Organization_Name_or_Organizational_unit_home)) answer = true;
        if (type.equals(VCFTypesEnum.Note))                                          answer = true;
        if (type.equals(VCFTypesEnum.Note_work))                                     answer = true;
        if (type.equals(VCFTypesEnum.Note_home))                                     answer = true;
        if (type.equals(VCFTypesEnum.Last_Revision))                                 answer = false;
        if (type.equals(VCFTypesEnum.Sound))                                         answer = true;
        if (type.equals(VCFTypesEnum.Sound_work))                                    answer = true;
        if (type.equals(VCFTypesEnum.Sound_home))                                    answer = true;
        if (type.equals(VCFTypesEnum.URL))                                           answer = true;
        if (type.equals(VCFTypesEnum.URL_work))                                      answer = true;
        if (type.equals(VCFTypesEnum.URL_home))                                      answer = true;
        if (type.equals(VCFTypesEnum.Unique_Identifier))                             answer = false;
        if (type.equals(VCFTypesEnum.Public_Key))                                    answer = true;
        if (type.equals(VCFTypesEnum.Public_Key_work))                               answer = true;
        if (type.equals(VCFTypesEnum.Public_Key_home))                               answer = true;
        return answer;
    }

}
