package com.mahindra.be_lms.util;

import com.mahindra.be_lms.BuildConfig;

/**
 * Created by Chaitali on 10/21/16.
 */

public class Constants {

   // ============BE-API===========================
   public static final String WS_TOKEN = "75ec0c1c371c35111408eabb54d83367";
   public static final String  BE_LMS_Common_URL = "http://13.76.164.143/businessexcellence/";
   //public static final String BE_LMS_QUIZ_URL = "http://137.116.169.88/mastra_lms_uat/blocks/tfksettings/tfk_form_all_courses.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&formid=47&tfkuserID=";
   public static final String BE_LMS_ROOT_URL = "http://13.76.164.143/businessexcellence/webservice/rest/server.php?wstoken=";
   public static final String BE_LMS_REGISTER_URL = "http://13.76.164.143/businessexcellence/webservice/rest/server.php?wstoken="+WS_TOKEN+"&wsfunction=custommobwebsrvices_add_user&moodlewsrestformat=json";
   public static final String BE_LMS_POSITION_URL = "http://13.76.164.143/businessexcellence/webservice/rest/server.php?wstoken="+WS_TOKEN+"&wsfunction=custommobwebsrvices_all_positions&moodlewsrestformat=json";
   public static final String BE_LMS_COMPANY_LIST_URL = "http://13.76.164.143/businessexcellence/webservice/rest/server.php?wstoken="+WS_TOKEN+"&wsfunction=custommobwebsrvices_all_orgframework&moodlewsrestformat=json";
   public static final String BE_LMS_PIC_UPLOAD_URL = "http://13.76.164.143/businessexcellence/webservice/upload.php?token=";
   public static final String BE_LMS_FORGOTPASSWORD_URL = "http://13.76.164.143/businessexcellence/webservice/rest/server.php?wstoken="+WS_TOKEN+"&moodlewsrestformat=json&wsfunction=core_forgot_password&username=";
   public static final String BE_LMS_QUIZ_URL ="http://13.76.164.143/businessexcellence/bequiz.html?userid=";


 // ============MaSTRA-API===========================

 public static final String LMS_Common_URL = "http://137.116.169.88/mastra_lms_uat/";
    public static final String LMS_QUIZ_URL = "http://137.116.169.88/mastra_lms_uat/blocks/tfksettings/tfk_form_all_courses.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&formid=47&tfkuserID=";
    public static final String LMS_ROOT_URL = "http://137.116.169.88/mastra_lms_uat/blocks/tfksettings/api.php";


    public static final String LMS_URL = LMS_ROOT_URL;
   // public static final String LMS_Common_URL = BuildConfig.LMS_Common_URL;
    public static final String DB_NAME = "m-astraLMS.db";
    public static final String REGISTRATION_ACTION = "registration";
    public static final String CHECK_OTP_ACTION = "check_otp";
    public static final String LOGIN_ACTION = "login";
    public static final String FORGOT_ACTION = "forgot_password";
    public static final String CHANGE_PWD_ACTION = "change_password";
    public static final String RESEND_OTP_ACTION = "resend_otp";
    public static final String COURSE_LIST_ACTION ="moodle_enrol_get_users_courses";
    public static final String PLATFORM = "ANDROID";
    public static final String PATH_LIST = "path_list";
    public static final String UPDATE_PROFILE_ACTION = "update_profile";
    public static final String UPDATE_PROFILE_IMAGE_ACTION = "update_profile_pic";
    public static final String QUIZ_RESULT_ACTION = "quiz_result";
    public static final String PROLIE_IMG_DEVICE_PATH = "/user/";
    public static final String REQUEST_PROGRAMS = "request_programs";
    public static final String REQUEST_PROFILE = "request_profile";
    public static final String DOC_TREE_DOWNLOAD_FOLDER = "documentDB/";
    public static final String DOC_UPLOAD_DOWNLOAD_FOLDER = "uploads/technical_uploads/";
    public static final String DOC_TREE_DOWNLOAD_URL = Constants.LMS_Common_URL + DOC_TREE_DOWNLOAD_FOLDER;
    public static final String DOC_UPLOAD_DOWNLOAD_URL = Constants.LMS_Common_URL + DOC_UPLOAD_DOWNLOAD_FOLDER;
    public static final String ACTION_UPLOAD_DOCUMENT = "upload_document";
    public static final String MASTER_DATA_ACTION = "master_data";
    public static final String TECHNICAL_UPLOAD_ACTION = "get_all_uploads";
    public static final String ACTION_GET_QUIZ_DOC = "get_quiz_doc";
    public static final String ACTION_GET_QUIZ_DATA = "get_quiz_data";
    public static final String FETCH_SURVEY_ACTION = "get_survey_question";
    public static final String SAVE_FEEDBACK_ACTION = "save_survey";
    public static final String FETCH_QUIZ_RESULT_FEEDBACK_ACTION = "get_feedback_question";
    public static final String QUESTION_TYPE_SURVEY = "survey_question";
    public static final String QUESTION_TYPE_FEEDBACK = "feedback_question";
    public static final String QUESTION_TYPE_POST_FEEDBACK = "post_feedback_question";
    public static final String SAVE_DOCUMENT_HIT_ACTION = "document_hit";
    public static final String FETCH_DOCUMENT_HIT_ACTION = "document_hit_totalcount";
    public static final String ACTION_FETCH_APPROVED_PROFILE_DATA = "fetch_approved_profile_data";
    public static final String MOODLE_TOKEN = "b5a3ccd8331a2ed6e9def6b84e8a52ef";
    public static final String SECERET_KEY = "PKBMahindraRiseSecret";
    public static final String ROLE_COMPANY = "supplier";
    public static final String ROLE_TRAINER = "mahindratrainer";
    public static final String ROLE_MANAGER = "champion";
    public static final String ROLE_COMPANY_EMPLOYEE = "supplier_employee";
    public static final String ROLE_TECH_MANAGER = "tech_manager";
    public static final String ROLE_COORDINATOR = "coordinator";
    public static final String ROLE_CUSTOMER = "Customer";
    //public static final String LMS_QUIZ_URL = BuildConfig.LMS_QUIZ_URL;
    public static final String LMS_MANUALS_BULLETIN_URL = LMS_Common_URL + "blocks/tfksettings/treeView_webview.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&tfkuserID=";
    public static final String LMS_MOST_VIEW_URL = LMS_Common_URL + "blocks/tfksettings/mostview.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&tfkuserID=";
    public static final String LMS_ABOUT_US = LMS_Common_URL + "blocks/tfksettings/tfk_about_pkb.php";
    public static final String LMS_SEARCH_URL = LMS_Common_URL + "blocks/tfksettings/search.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&formid=223&masterMenu=70&tfkuserID=";
    public static final String LMS_TRAINING_PASSPORT_URL = LMS_Common_URL + "blocks/tfksettings/tfk_my_training_passport.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&tfkuserID=";
    public static final String LMS_MY_FIELD_RECORD_FORM = LMS_Common_URL + "blocks/tfksettings/tfk_my_training_passport.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&tfkuserID=";
    public static final String LMS_SURVEY_FEEDBACK_FORM = LMS_Common_URL + "blocks/tfksettings/question_survey_list_selection.php?token=b5a3ccd8331a2ed6e9def6b84e8a52ef&tfkuserID=";
    //public static final String LMS_SURVEY_SUBMIT_LINK=BuildConfig.LMS_Common_URL+"blocks/tfksettings/question_survey_list.php?result=1";
    public static final String LMS_SURVEY_SUBMIT_LINK = LMS_Common_URL + "blocks/tfksettings/question_survey_list_selection.php?result=1";
    public static final String LMS_TRAINING_CALENDAR_LINK = LMS_Common_URL + "blocks/tfksettings/tfk_framework.php?formid=11&masterMenu=215&token=b5a3ccd8331a2ed6e9def6b84e8a52ef&tfkuserID=";
    public static final String USER_HELP_LINK = LMS_Common_URL + "blocks/tfksettings/help_view.php?";
    private static final String PROFILE_PIC_FOLDER = "img/userpic/";
    public static final String PROFILE_UPLOAD_URL = LMS_Common_URL + PROFILE_PIC_FOLDER;
}
