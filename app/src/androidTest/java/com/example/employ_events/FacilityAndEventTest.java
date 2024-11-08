package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;

import android.view.KeyEvent;
import android.widget.TextView;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class FacilityAndEventTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    //Test wont work if your organizer setting is true for your user id. Rerun the test and it should work.
    @Test
    public void test01_HomeToFacilityScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TEST TOAST Facility ID not found!
        // Check if it's on the facility dialog since facility has not created yet screen
        onView(withId(R.id.editFacilityName)).check(matches(isDisplayed()));
        onView(withId(R.id.editFacilityEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.editFacilityPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.editFacilityAddress)).check(matches(isDisplayed()));
        onView(withText("Not Now")).check(matches(isDisplayed()));
        onView(withText("Create")).check(matches(isDisplayed()));
    }

    @Test
    public void test02_FacilityToHomeScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Not now" button on the DiaLog
        onView(withText("Not Now")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check it goes back to the starting screen
        onView(withId(R.id.admin_view_button)).check(matches(isDisplayed()));

        // TEST FOR TOAST Facility creation canceled. You can start again anytime!
    }

    @Test
    public void test04_CreateAndEditFacilityScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if it's on the facility dialog since facility has not created yet screen
        onView(withText("Create Facility")).check(matches(isDisplayed()));

        // Verify the "Create" button is displayed
        onView(withText("Create")).check(matches(isDisplayed()));

        // Enter facility information.
        onView(withId(R.id.editFacilityName)).perform(ViewActions.typeText("Test"));
        onView(withId(R.id.editFacilityEmail)).perform(ViewActions.typeText("test@gmail.com"));
        onView(withId(R.id.editFacilityAddress)).perform(ViewActions.typeText("Edmonton"));
        // Click the "Create" button is to create the facility
        onView(withText("Create")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check if the information been updated to the screen
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Find the TextView in the fragment_home layout
            TextView facilityNameTV = activity.findViewById(R.id.facilityNameTV);
            TextView facilityEmailTV = activity.findViewById(R.id.facilityEmailTV);
            TextView facilityAddressTV = activity.findViewById(R.id.facilityAddressTV);

            // Get the actual text from the TextView
            String actualName = facilityNameTV.getText().toString();
            String actualEmail = facilityEmailTV.getText().toString();
            String actualAddress = facilityAddressTV.getText().toString();

            // Check if the actual text matches the expected text
            String expectedName = "Test";
            assertEquals("TextView name does not match expected text", expectedName, actualName);
            String expectedEmail = "test@gmail.com";
            assertEquals("TextView email does not match expected text", expectedEmail, actualEmail);
            String expectedAddress = "Edmonton";
            assertEquals("TextView address does not match expected text", expectedAddress, actualAddress);
        });
        onView(withId(R.id.edit_facility_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.uploadPFP)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextFacilityName)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextFacilityEmailAddress)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextFacilityPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextFacilityAddress)).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed()));
        onView(withText("Test")).check(matches(isDisplayed()));
        onView(withText("test@gmail.com")).check(matches(isDisplayed()));
        onView(withText("Edmonton")).check(matches(isDisplayed()));

        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.replaceText("Tests"));
        onView(withId(R.id.editTextFacilityEmailAddress)).perform(ViewActions.replaceText("test12@gmail.com"));
        onView(withId(R.id.editTextFacilityAddress)).perform(ViewActions.replaceText("edmonton"));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.confirm_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Tests")).check(matches(isDisplayed()));
        onView(withText("test12@gmail.com")).check(matches(isDisplayed()));
        onView(withText("edmonton")).check(matches(isDisplayed()));
    }

    @Test
    public void test03_CreateFacilityErrorMessage() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
       // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Test if there is any error in each section
        onView(withText("Create")).perform(click());
        onView(withId(R.id.editFacilityName)).check(matches(hasErrorText("Facility name cannot be empty")));
        onView(withId(R.id.editFacilityName)).perform(ViewActions.typeText("Test"));
        onView(withText("Create")).perform(click());
        onView(withId(R.id.editFacilityEmail)).check(matches(hasErrorText("Facility email cannot be empty")));
        onView(withId(R.id.editFacilityEmail)).perform(ViewActions.typeText("test@gmail.com"));
        onView(withText("Create")).perform(click());
        onView(withId(R.id.editFacilityAddress)).check(matches(hasErrorText("Facility address cannot be empty")));
    }

    @Test
    public void test05_FacilityToEventListBackScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if on the event list page
        onView(withText("Event List")).check(matches(isDisplayed()));
        onView(withId(R.id.addEventButton)).check(matches(isDisplayed()));
        // Check if you can go back to facility page
        onView(withContentDescription("Navigate up")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Tests")).check(matches(isDisplayed()));
        onView(withText("test12@gmail.com")).check(matches(isDisplayed()));
        onView(withText("edmonton")).check(matches(isDisplayed()));
    }

    @Test
    public void test06_EventListToAddEventScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click to add event
        onView(withId(R.id.addEventButton)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if on add event page
        onView(withId(R.id.bannerImage)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadBannerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
        onView(withId(R.id.description)).check(matches(isDisplayed()));
        onView(withId(R.id.event_date)).check(matches(isDisplayed()));
        onView(withId(R.id.registration_start_deadline)).check(matches(isDisplayed()));
        onView(withId(R.id.registration_date_deadline)).check(matches(isDisplayed()));
        onView(withId(R.id.event_capacity)).check(matches(isDisplayed()));
        onView(withId(R.id.limit)).check(matches(isDisplayed()));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 14; i++) {
            onView(withId(android.R.id.content)).perform(pressKey(KeyEvent.KEYCODE_TAB));
        }
        onView(withId(R.id.description)).perform(closeSoftKeyboard());
        onView(withId(R.id.feeText)).check(matches(isDisplayed()));
        onView(withId(R.id.geolocation_status)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
    }

    @Test
    public void test07_CreateEvent() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click to add event
        onView(withId(R.id.addEventButton)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 15; i++) {
            onView(withId(android.R.id.content)).perform(pressKey(KeyEvent.KEYCODE_TAB));
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Add event information
        onView(withId(R.id.description)).perform(closeSoftKeyboard());
        onView(withId(R.id.event_title)).perform(ViewActions.typeText("Test Event"));
        onView(withId(R.id.description)).perform(ViewActions.typeText("Test case event"));
        onView(withId(R.id.description)).perform(closeSoftKeyboard());
        onView(withId(R.id.event_date)).perform(click());

        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.registration_start_deadline)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.registration_date_deadline)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("OK")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.event_capacity)).perform(ViewActions.typeText("33"));
        onView(withId(R.id.description)).perform(closeSoftKeyboard());
        onView(withId(R.id.geolocation_status)).perform(click());
        onView(withId(R.id.save_event_button)).perform(click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if event is in the list
        onView(withText("Test Event")).check(matches(isDisplayed()));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test08_EventListToMangeEventScreen() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click to view more about the event
        onView(withText("Test Event")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if you are in the manage event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Event")));
        onView(withId(R.id.description)).check(matches(withText("Test case event")));
        onView(withId(R.id.event_capacity)).check(matches(withText("Event Capacity: 33")));//error
        onView(withId(R.id.geolocation_status)).check(matches(withText("Geolocation required: true")));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrants_button)).check(matches(isDisplayed()));
    }

    @Test
    public void test09_MangeEventToUpload_UpdateBannerScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click to view more about the event
        onView(withText("Test Event")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if you are in the manage event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Event")));
        onView(withId(R.id.description)).check(matches(withText("Test case event")));
        onView(withId(R.id.event_capacity)).check(matches(withText("Event Capacity: 33")));//error
        onView(withId(R.id.geolocation_status)).check(matches(withText("Geolocation required: true")));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrants_button)).check(matches(isDisplayed()));
        // Go to the update/upload Banner page
        onView(withId(R.id.edit_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if on that page
        onView(withId(R.id.uploadBannerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click the update button
        onView(withId(R.id.save_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        // Check if you gone back to manage screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Event")));
        onView(withId(R.id.description)).check(matches(withText("Test case event")));
        onView(withId(R.id.event_capacity)).check(matches(withText("Event Capacity: 33")));//error
        onView(withId(R.id.geolocation_status)).check(matches(withText("Geolocation required: true")));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrants_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_button)).perform(click());
    }

    @Test
    public void test10_MangeEventToQRCOdeScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click to view more about the event
        onView(withText("Test Event")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if you are in the manage event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Event")));
        onView(withId(R.id.description)).check(matches(withText("Test case event")));
        onView(withId(R.id.event_capacity)).check(matches(withText("Event Capacity: 33")));//error
        onView(withId(R.id.geolocation_status)).check(matches(withText("Geolocation required: true")));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrants_button)).check(matches(isDisplayed()));

        // Click to go to QR code page
        onView(withId(R.id.qr_code_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        // Check if on QR page
        onView(withId(R.id.imageQRCode)).check(matches(isDisplayed()));
        onView(withId(R.id.downloadButton)).check(matches(isDisplayed()));
        // Go back to the manage screen
        onView(withContentDescription("Navigate up")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        // Check if on manage screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Event")));
        onView(withId(R.id.description)).check(matches(withText("Test case event")));
        onView(withId(R.id.event_capacity)).check(matches(withText("Event Capacity: 33")));//error
        onView(withId(R.id.geolocation_status)).check(matches(withText("Geolocation required: true")));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrants_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_button)).perform(click());
    }

    @Test
    public void test11_MangeEventToWaitingListScreen() {
        // Open the sidebar menu (assumes you have a menu icon with content description "Open navigation drawer")
        onView(withContentDescription("Open navigation drawer")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait for the screen to update
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Click to view more about the event
        onView(withText("Test Event")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if you are in the manage event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Event")));
        onView(withId(R.id.description)).check(matches(withText("Test case event")));
        onView(withId(R.id.event_capacity)).check(matches(withText("Event Capacity: 33")));//error
        onView(withId(R.id.geolocation_status)).check(matches(withText("Geolocation required: true")));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrants_button)).check(matches(isDisplayed()));
        // Go to the entrant list screen
        onView(withId(R.id.view_entrants_button)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check if in entrant list screen
        onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.entrants_list)).check(matches(isDisplayed()));
        onView(withId(R.id.sample_entrants)).check(matches(isDisplayed()));
        onView(withId(R.id.send_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.remove_entrant)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_map)).check(matches(isDisplayed()));
        // Click to sample entrants
        onView(withId(R.id.sample_entrants)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the list of selected entrant
        onView(withText("Selected")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Go to the list of cancelled entrant
        onView(withText("Cancelled")).perform(click());

        // Go to the registered list to see final list of entrants
        onView(withText("Registered")).perform(click());
    }
}
