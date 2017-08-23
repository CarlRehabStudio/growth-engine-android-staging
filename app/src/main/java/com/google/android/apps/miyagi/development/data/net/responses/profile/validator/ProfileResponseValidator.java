package com.google.android.apps.miyagi.development.data.net.responses.profile.validator;

import com.google.android.apps.miyagi.development.data.error.FieldValidationException;
import com.google.android.apps.miyagi.development.data.error.ResponseStatusException;
import com.google.android.apps.miyagi.development.data.net.responses.ResponseStatusValidator;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponse;
import com.google.android.apps.miyagi.development.data.net.responses.profile.ProfileResponseData;
import com.google.android.apps.miyagi.development.utils.FieldValidator;

/**
 * Created by lukaszweglinski on 15.12.2016.
 */

public class ProfileResponseValidator extends ResponseStatusValidator<ProfileResponse> {

    /**
     * Validates API response with profile data.
     */
    public static ProfileResponseData validate(ProfileResponse response) throws ResponseStatusException, FieldValidationException {
        ProfileResponseValidator validator = new ProfileResponseValidator();
        validator.validateResponse(response);
        return response.getResponseData();
    }

    @Override
    protected void validateResponse(ProfileResponse response) throws ResponseStatusException, FieldValidationException {
        super.validateResponse(response);

        ProfileResponseData responseData = response.getResponseData();
        FieldValidator.notNull(responseData, "ResponseData");

        //TODO add field validation
        /*FieldValidator.notNull(responseData.getToolbarText(), "toolbar_text");
        FieldValidator.notNull(responseData.getPersonalSectionHeader(), "personal_section_header");
        FieldValidator.notNull(responseData.getPersonal(), "first_name");
        FieldValidator.notNull(responseData.getLastName(), "last_name");
        FieldValidator.notNull(responseData.getFirstNamePlaceholder(), "first_name_placeholder");
        FieldValidator.notNull(responseData.getLastNamePlaceholder(), "last_name_placeholder");
        FieldValidator.notNull(responseData.getValidationEmptyFirstName(), "validation_empty_first_name");
        FieldValidator.notNull(responseData.getValidationEmptyLastName(), "validation_empty_last_name");
        FieldValidator.notNull(responseData.getSettingSectionHeader(), "settings_section_header");
        FieldValidator.notNull(responseData.isPushNotifications(), "push_notifications");
        FieldValidator.notNull(responseData.getPushNotificationsText(), "push_notifications_text");
        FieldValidator.notNull(responseData.isEmailNotifications(), "email_notifications");
        FieldValidator.notNull(responseData.getEmailNotificationsText(), "email_notifications_text");
        FieldValidator.notNull(responseData.getLoginInformationSectionHeader(), "login_information_section_header");
        FieldValidator.notNull(responseData.getEditProfileButton(), "edit_profile_button");
        FieldValidator.notNull(responseData.getEditProfileUrl(), "edit_profile_url");
        FieldValidator.notNull(responseData.getDeleteAccountButton(), "delete_account_button");
        FieldValidator.notNull(responseData.getDeleteAccountDialogText(), "delete_account_dialog_text");
        FieldValidator.notNull(responseData.getDeleteAccountDialogYes(), "delete_account_dialog_yes");
        FieldValidator.notNull(responseData.getDeleteAccountDialogCancel(), "delete_account_dialog_cancel");
        FieldValidator.notNull(responseData.getSaveButton(), "save_button");*/
    }
}
