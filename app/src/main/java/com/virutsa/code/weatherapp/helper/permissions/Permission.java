package com.virutsa.code.weatherapp.helper.permissions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

import com.virutsa.code.weatherapp.R;


/**
 * All permissions that may be required during runtime
 */

public enum Permission {

    LOCATION (
            ACCESS_FINE_LOCATION,
            R.string.perm_location_name,
            R.string.perm_location_explanation,
            true
    );
   /* GOOGLE_ACCOUNT(
            GET_ACCOUNTS,
            R.string.perm_account,
            R.string.perm_account_explanation,
            true
    ),
    CONTACTS(
            READ_CONTACTS,
            R.string.perm_contacts,
            R.string.perm_contacts_explanation,
            false
    ),
    SMS(
            SEND_SMS,
            R.string.perm_sms,
            R.string.perm_sms_explanation,
            false
    ),
    PHONE(
            READ_PHONE_STATE,
            R.string.perm_sms,
            R.string.perm_sms_explanation,
            false
    ),
    CALL(
            CALL_PHONE,
            R.string.perm_call,
            R.string.perm_call_explanation,
            false
    );*/

    private boolean isRequired;
    private String mName;
    private int mExplanationResource;
    private int mNameResource;

    Permission(String name, int nameResource, int explanationResource, boolean isRequired){
        mName = name;
        mNameResource = nameResource;
        mExplanationResource = explanationResource;
        this.isRequired = isRequired;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public int getNameResource() {
        return mNameResource;
    }
    public String getName() {
        return mName;
    }

    public int getExplanationResource() {
        return mExplanationResource;
    }


}