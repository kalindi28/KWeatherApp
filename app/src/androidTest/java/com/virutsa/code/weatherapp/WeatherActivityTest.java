package com.virutsa.code.weatherapp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.virutsa.code.weatherapp.helper.permissions.PermissionHandler;
import com.virutsa.code.weatherapp.instrumentedTest.Util;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WeatherActivityTest {

    @Rule
    public ActivityScenarioRule<WeatherActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(WeatherActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");
    private UiDevice uiDevice;
    private PermissionHandler permissionHandler;
    private Activity activity;
    private final static String TEXT_DENY = "Deny";
    private final static String TEXT_ALLOW = "Allow";
    public static final String TEXT_PERMISSIONS = "Permissions";
    public static final String TEXT_NEVER_ASK_AGAIN = "Never ask again";
    private final static  String[] PERMISSION_NAMES_IN_SETTINGS = { "Location"};
    @Test
    public void weatherActivityTest() {
        ActivityScenario.launch(WeatherActivity.class).onActivity(new ActivityScenario.ActivityAction<WeatherActivity>() {
            @Override
            public void perform(WeatherActivity activity11) {
                activity=activity11;
            }
        });
    }
    @Before
    public void prepareDevice()throws  Exception{
        uiDevice = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());
        weatherActivityTest();
        // prepare the activity and check whether the preparation was successful
        permissionHandler = new PermissionHandler(InstrumentationRegistry.getInstrumentation().getContext());
        if(permissionHandler.isAllPermissionsGranted()) {
            Util.revokePermissions(permissionHandler);
            assertFalse("permissions were not revoked, try restarting this test", permissionHandler.isAllPermissionsGranted());
        }

        //activity = mActivityRule.getScenario();



    }

    @Test
    //TODO: Check the behavior if only one permission was revoked
    public void testPermissionHandler() throws Exception {

        // Check whether the window opens up, then deny one of the options, this should result in short
        permissionHandler.askForAllPermissions(activity);

        //check if the window is open
        assertTrue( "Permission screen has not been opened, make sure the device is unlocked and that permissions are not permanently removed",
                isPermissionScreenOpen(uiDevice));

        //deny all open windows
        while(isPermissionScreenOpen(uiDevice)){
            denyPermissionRequest(uiDevice);}

        // Check whether a rationale is showing
        onView(withText(R.string.ok)).check(matches(isDisplayed()));

        //click okay and check whether the permissions are requested again
        onView(withText(R.string.ok)).perform(click());
        assertTrue("Permission screen should be showing after clicking ok on rationale",isPermissionScreenOpen(uiDevice));

        //deny permissions permanently
        while(isPermissionScreenOpen(uiDevice)){
            denyCurrentPermissionPermanently(uiDevice);}

        //check whether the PermissionRevokedPermanently Activity has been activated
        onView(withText(R.string.goToSettings)).check(matches(isDisplayed()));

        // click on the Open App Settings button and grant the permissions permanently
        onView(withText(R.string.goToSettings)).perform(click());
        openPermissionInSettings(uiDevice);
        grantPermissionsInSettings(uiDevice);

        // now that all permissions are granted, check the return value and assert that no window will open when asking for permissions.
        assertTrue("Permissions were granted, but isAllPermissionGranted is false", permissionHandler.isAllPermissionsGranted());
        permissionHandler.askForAllPermissions(activity);
        assertFalse(isPermissionScreenOpen(uiDevice));

        uiDevice.pressBack();
        uiDevice.pressBack();


    }



    public static void openPermissionInSettings(UiDevice device) throws UiObjectNotFoundException {
        UiObject permissions = device.findObject(new UiSelector().text(TEXT_PERMISSIONS));
        permissions.click();
    }

    // Grants permission in the Settings->App->Permissions screen
    public static void grantPermissionsInSettings(UiDevice device) throws UiObjectNotFoundException {

        for (String permissionName : PERMISSION_NAMES_IN_SETTINGS) {
            UiObject permissionEntry = device.findObject(new UiSelector().text(permissionName));
            permissionEntry.click();
        }
    }

    /*
     * Check whether a "permission like" screen is open: in the sense that it has an ALLOW and a DENY button
     * */
    public static boolean isPermissionScreenOpen(UiDevice device) throws Exception {
        boolean screenExists = true;


        UiObject button = device.findObject(new UiSelector().text(TEXT_ALLOW));
        button.waitForExists(5000);

        if (!button.exists()) {
            screenExists = false;
        }

        button = device.findObject(new UiSelector().text(TEXT_DENY));
        if (!button.exists()) {
            screenExists = false;

        }

        return screenExists;
    }


    public static void denyCurrentPermissionPermanently(UiDevice device) throws UiObjectNotFoundException {
        UiObject neverAskAgainCheckbox = device.findObject(new UiSelector().text(TEXT_NEVER_ASK_AGAIN));
        neverAskAgainCheckbox.click();
        denyPermissionRequest(device);
    }

    public static void denyPermissionRequest(UiDevice device) throws UiObjectNotFoundException {
        UiObject denyButton = device.findObject(new UiSelector().text(TEXT_DENY));
        denyButton.click();
    }

    public static void allowOpenPermissionRequests(UiDevice device) throws Exception{
        while(isPermissionScreenOpen(device)){
            allowPermissionRequest(device);
        }

    }

    public static void allowPermissionRequest(UiDevice device) throws UiObjectNotFoundException{
        UiObject allowButton = device.findObject(new UiSelector().text(TEXT_ALLOW));
        allowButton.click();
    }


    @After
    public void cleanUp() throws  Exception{
        Util.allowPermissions(permissionHandler);
    }

}
