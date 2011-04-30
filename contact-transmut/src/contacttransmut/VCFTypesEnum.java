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
 * this is good for VCFHelper, better for checking typos and auto-completion in IDE
 */
public enum VCFTypesEnum {

Name ("Name"),
Formatted_Name ("Formatted_Name"),
Nickname ("Nickname"),
Nickname_work ("Nickname_work"),
Nickname_home ("Nickname_home"),
Photograph ("Photograph"),
Photograph_work ("Photograph_work"),
Photograph_home ("Photograph_home"),
Birthday ("Birthday"),
Delivery_Address ("Delivery_Address"),
Delivery_Address_work ("Delivery_Address_work"),
Delivery_Address_home ("Delivery_Address_home"),
Label_Address ("Label_Address"),
Label_Address_work ("Label_Address_work"),
Label_Address_home ("Label_Address_home"),
Telephone ("Telephone"),
Telephone_text ("Telephone_text"),
Telephone_voice ("Telephone_voice"),
Telephone_fax ("Telephone_fax"),
Telephone_cell ("Telephone_cell"),
Telephone_video ("Telephone_video"),
Telephone_pager ("Telephone_pager"),
Telephone_textphone ("Telephone_textphone"),
Telephone_work ("Telephone_work"),
Telephone_work_text ("Telephone_work_text"),
Telephone_work_voice ("Telephone_work_voice"),
Telephone_work_fax ("Telephone_work_fax"),
Telephone_work_cell ("Telephone_work_cell"),
Telephone_work_video ("Telephone_work_video"),
Telephone_work_pager ("Telephone_work_pager"),
Telephone_work_textphone ("Telephone_work_textphone"),
Telephone_home ("Telephone_home"),
Telephone_home_text ("Telephone_home_text"),
Telephone_home_voice ("Telephone_home_voice"),
Telephone_home_fax ("Telephone_home_fax"),
Telephone_home_cell ("Telephone_home_cell"),
Telephone_home_video ("Telephone_home_video"),
Telephone_home_pager ("Telephone_home_pager"),
Telephone_home_textphone ("Telephone_home_textphone"),
Email ("Email"),
Email_work ("Email_work"),
Email_home ("Email_home"),
Email_Program ("Email_Program"),
Time_Zone_text ("Time_Zone_text"),
Time_Zone_uri ("Time_Zone_uri"),
Time_Zone_utc_offset ("Time_Zone_utc_offset"),
Global_Positioning ("Global_Positioning"),
Global_Positioning_work ("Global_Positioning_work"),
Global_Positioning_home ("Global_Positioning_home"),
Title ("Title"),
Title_work ("Title_work"),
Title_home ("Title_home"),
Role_or_occupation ("Role_or_occupation"),
Role_or_occupation_work ("Role_or_occupation_work"),
Role_or_occupation_home ("Role_or_occupation_home"),
Logo ("Logo"),
Logo_work ("Logo_work"),
Logo_home ("Logo_home"),
Organization_Name_or_Organizational_unit ("Organization_Name_or_Organizational_unit"),
Organization_Name_or_Organizational_unit_work ("Organization_Name_or_Organizational_unit_work"),
Organization_Name_or_Organizational_unit_home ("Organization_Name_or_Organizational_unit_home"),
Note ("Note"),
Note_work ("Note_work"),
Note_home ("Note_home"),
Last_Revision ("Last_Revision"),
Sound ("Sound"),
Sound_work ("Sound_work"),
Sound_home ("Sound_home"),
URL ("URL"),
URL_work ("URL_work"),
URL_home ("URL_home"),
Unique_Identifier ("Unique_Identifier"),
Public_Key ("Public_Key"),
Public_Key_work ("Public_Key_work"),
Public_Key_home ("Public_Key_home");


    private final String name;

    private VCFTypesEnum(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(VCFTypesEnum other) {
        return this.toString().equals(other.toString());
    }
}
