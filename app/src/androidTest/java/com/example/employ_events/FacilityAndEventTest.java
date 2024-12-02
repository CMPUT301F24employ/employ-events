package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;

import android.Manifest;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * UI Tests for organizer managing their facility.
 * @author Aaron
 * - Initially created
 * @author Tina
 * - Modifying to reduce warnings, fix errors related to updates, using realistic data and adding documentation.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class FacilityAndEventTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // Grants permission to send notifications so pop-up doesn't appear
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Test
    public void test01_HomeToFacilityScreen() {
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Wait for the menu to open
        onView(withText("Facility")).perform(click());

        // Check that the facility dialog is shown
        onView(withId(R.id.editFacilityName)).check(matches(isDisplayed()));
        onView(withId(R.id.editFacilityEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.editFacilityPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.editFacilityAddress)).check(matches(isDisplayed()));
        onView(withText("Not Now")).check(matches(isDisplayed()));
        onView(withText("Create")).check(matches(isDisplayed()));
    }

    @Test
    public void test02_FacilityToHomeScreen() {
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Wait for the menu to open
        onView(withText("Facility")).perform(click());

        // Click on the "Not Now" button on the dialog
        onView(withText("Not Now")).perform(click());

        // Check if it goes back to the home screen
        onView(withId(R.id.wonLotteryCount)).check(matches(isDisplayed()));
    }

    @Test
    public void test03_CreateFacilityErrorMessage() {
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());

        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());

        // Test if error messages are shown when fields are empty
        onView(withText("Create")).perform(click());
        onView(withId(R.id.editFacilityName)).check(matches(hasErrorText("Facility name cannot be empty")));
        onView(withId(R.id.editFacilityName)).perform(ViewActions.typeText("UoA"));

        onView(withText("Create")).perform(click());
        onView(withId(R.id.editFacilityEmail)).check(matches(hasErrorText("Facility email cannot be empty")));
        onView(withId(R.id.editFacilityEmail)).perform(ViewActions.typeText("UoA@ualberta.ca"));

        onView(withText("Create")).perform(click());
        onView(withId(R.id.editFacilityAddress)).check(matches(hasErrorText("Facility address cannot be empty")));
    }

    /**
     * US 02.01.03 As an organizer, I want to create and manage my facility profile.
     */
    @Test
    public void test04_CreateAndEditFacilityScreen() throws InterruptedException{
        // Open the sidebar menu
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Facility")).perform(click());

        // Check if it's on the facility dialog since facility has not been created yet screen
        onView(withText("Create Facility")).check(matches(isDisplayed()));
        onView(withText("Create")).check(matches(isDisplayed()));

        // Enter facility information.
        onView(withId(R.id.editFacilityName)).perform(ViewActions.typeText("UoA"));
        onView(withId(R.id.editFacilityEmail)).perform(ViewActions.typeText("UoA@ualberta.ca"));
        onView(withId(R.id.editFacilityAddress)).perform(ViewActions.typeText("UoA Campus, Edmonton, AB"));

        // Click the "Create" button to create the facility
        onView(withText("Create")).perform(click());
        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        onView(withId(R.id.facilityNameTV)).check(matches(withText("UoA")));
        onView(withId(R.id.facilityEmailTV)).check(matches(withText("UoA@ualberta.ca")));
        onView(withId(R.id.facilityAddressTV)).check(matches(withText("UoA Campus, Edmonton, AB")));
    }


    @Test
    public void test05_FacilityToEventListBackScreen() throws InterruptedException{
        // Open navigation drawer and navigate to facility screen
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Facility")).perform(click());
        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        // Verify we are on the event list screen
        onView(withText("Events")).check(matches(isDisplayed()));
        onView(withId(R.id.addEventButton)).check(matches(isDisplayed()));

        // Go back to facility screen
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        // Check that facility details are still displayed
        onView(withId(R.id.facilityNameTV)).check(matches(withText("UoA")));
        onView(withId(R.id.facilityEmailTV)).check(matches(withText("UoA@ualberta.ca")));
        onView(withId(R.id.facilityAddressTV)).check(matches(withText("UoA Campus, Edmonton, AB")));
    }

    @Test
    public void test06_EventListToAddEventScreen() throws InterruptedException{
        // Open navigation drawer and navigate to facility screen
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Facility")).perform(click());
        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        // Go to event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        // Click to add a new event
        onView(withId(R.id.addEventButton)).perform(click());
        Thread.sleep(2000);

        // Verify that we are on the "Add Event" screen
        onView(withId(R.id.bannerImage)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadBannerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
    }

    /**
     * US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code
     * that links to the event description and event poster in the app
     */
    @Test
    public void test07_CreateEvent() throws InterruptedException{
        // Open the sidebar menu
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        // Click to add event
        onView(withId(R.id.addEventButton)).perform(click());
        Thread.sleep(2000);

        // Add event information
        onView(withId(R.id.event_title)).perform(ViewActions.typeText("UoA Welcome Party"));
        onView(withId(R.id.description)).perform(ViewActions.typeText("Welcome to UoA! Join us for a party on campus!"));
        closeKeyboard();

        // Set event date, registration start and end deadlines
        onView(withId(R.id.event_date)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.registration_start_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.registration_date_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());


        // Set event capacity and geolocation status
        onView(withId(R.id.event_capacity)).perform(ViewActions.typeText("5000"));
        closeKeyboard();

        onView(withId(R.id.add_event_scroll_view)).perform(ViewActions.swipeUp());

        onView(withId(R.id.limit)).perform(ViewActions.typeText("10000"));
        closeKeyboard();

        onView(withId(R.id.feeText)).perform(ViewActions.typeText("5"));
        closeKeyboard();

        onView(withId(R.id.geolocation_status)).perform(click());

        // Save event
        onView(withId(R.id.save_event_button)).perform(click());
        Thread.sleep(2000);

        // Check if event is in the list
        onView(withText("UoA Welcome Party")).check(matches(isDisplayed()));
    }

    @Test
    public void test08_EventListToManageEventScreen() throws InterruptedException{
        // Open the sidebar menu
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        Thread.sleep(2000);

        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withText("UoA Welcome Party")).perform(click());
        Thread.sleep(2000);

        // Check if on Manage Event screen where title and description match.
        onView(withId(R.id.event_title)).check(matches(withText("UoA Welcome Party")));
        onView(withId(R.id.description)).check(matches(withText("Welcome to UoA! Join us for a party on campus!")));
    }

    /**
     *
     * @throws InterruptedException Used to allow the UI to update.
     */
    @Test
    public void test09_ManageEventToUploadUpdateBannerScreen() throws InterruptedException{
        // Open the sidebar menu
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        // Check if event is in the list
        onView(withText("UoA Welcome Party")).check(matches(isDisplayed()));

        onView(withText("UoA Welcome Party")).perform(click());
        Thread.sleep(2000);

        // Check if on Manage Event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_button)).check(matches(isDisplayed()));

        // Go to update/upload Banner page
        onView(withId(R.id.edit_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.uploadBannerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));

        // Save event and check if back to manage screen
        onView(withId(R.id.save_event_button)).perform(click());
        Thread.sleep(2000);

        onView(withText("Manage Event")).check(matches(isDisplayed()));
    }

    /**
     * US 02.01.02 As an organizer I want to store the generated QR code in my database
     * @throws InterruptedException Used to allow the UI to update.
     */
    @Test
    public void test10_ManageEventToQRCodeScreen() throws InterruptedException{
        // Open the sidebar menu
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        // Check if event is in the list
        onView(withText("UoA Welcome Party")).check(matches(isDisplayed()));

        onView(withText("UoA Welcome Party")).perform(click());
        Thread.sleep(2000);

        // Check if on Manage Event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_button)).check(matches(isDisplayed()));

        // Go to QR code page
        onView(withId(R.id.qr_code_button)).perform(click());
        Thread.sleep(2000);

        // Check if on QR page
        onView(withId(R.id.imageQRCode)).check(matches(isDisplayed()));
        onView(withId(R.id.downloadButton)).check(matches(isDisplayed()));

        // Go back to the manage screen
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        onView(withText("Manage Event")).check(matches(isDisplayed()));
    }

    /**
     * US 02.02.01 As an organizer I want to view the list of entrants who joined my event waiting list
     * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply
     * US 02.06.02 As an organizer I want to see a list of all the cancelled entrants
     * US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event
     */
    @Test
    public void test11_ManageEventToEntrantsList() throws InterruptedException{
        // Open the sidebar menu
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Click on the "Facility" menu item
        onView(withText("Facility")).perform(click());
        Thread.sleep(2000);

        // Go to the event list screen
        onView(withId(R.id.view_event_button)).perform(click());
        Thread.sleep(2000);

        // Check if event is in the list
        onView(withText("UoA Welcome Party")).check(matches(isDisplayed()));

        onView(withText("UoA Welcome Party")).perform(click());
        Thread.sleep(2000);

        // Check if on Manage Event screen
        onView(withText("Manage Event")).check(matches(isDisplayed()));

        // Go to entrant list screen
        onView(withId(R.id.view_entrants_button)).perform(click());
        Thread.sleep(2000);

        // Check if in entrant list screen
        onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.entrants_list)).check(matches(isDisplayed()));
        onView(withId(R.id.sample_entrants)).check(matches(isDisplayed()));
        onView(withId(R.id.send_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.remove_entrant)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_map)).check(matches(isDisplayed()));

        // Navigate through the different tabs
        onView(withText("Waitlisted")).perform(click());
        onView(withText("Selected")).perform(click());
        onView(withText("Cancelled")).perform(click());
        onView(withText("Registered")).perform(click());
    }

    // Helper method to close the keyboard
    public void closeKeyboard() {
        // Perform the action to hide the keyboard
        ViewInteraction viewInteraction = onView(isRoot());
        viewInteraction.perform(ViewActions.closeSoftKeyboard());
    }

}