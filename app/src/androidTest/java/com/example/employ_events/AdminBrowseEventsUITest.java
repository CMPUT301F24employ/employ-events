package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.employ_events.model.Facility;
import com.example.employ_events.model.Profile;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest

/**
 * US 03.04.01 As an administrator, I want to be able to browse events.
 * US 03.01.01 As an administrator, I want to be able to remove events.
 * US 03.03.02 As an administrator, I want to be able to remove hashed QR code data
 * @author Connor
 */
// Makes tests run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminBrowseEventsUITest {

    String eventName1 = "Swimming Event 5567", eventName2 = "Golfing Lessons 0234",
    testAdminProfileID = "testUIDAdminBrowseEvents", testFacilityID = "testFacilityForAdminBrowse",
    docName1, docName2;

    // Grants permission to send notifications so pop-up doesn't appear
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Making sure we are using the test profile
     */
    @Before
    public void setUp() {
        // Set the uniqueID in SharedPreferences (simulate a logged-in user)
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uniqueID", testAdminProfileID);
        editor.apply();
    }

    @After
    public void tearDown() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // removing events if they're still there
        db.collection("events").document(eventName1).delete()
                .addOnSuccessListener(unused -> Log.d("TestingTests", "Event: " + eventName1 + " successfully deleted!"))
                .addOnFailureListener(e -> Log.d("TestingTests", "Error: Failed to delete event: " + eventName1));
        db.collection("events").document(eventName2).delete()
                .addOnSuccessListener(unused -> Log.d("TestingTests", "Event: " + eventName2 + " successfully deleted!"))
                .addOnFailureListener(e -> Log.d("TestingTests", "Error: Failed to delete event: " + eventName2));
    }

    /**
     * Helper method to make sure the test profile is an admin, there is a test facility,
     * and all the events are cleared before starting
     */
    private void loadTestData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef1 = db.collection("userProfiles").document(testAdminProfileID);
        DocumentReference docRef2 = db.collection("facilities").document(testFacilityID);

        // Setting up test profile if it doesn't exist
        docRef1.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("TestingTests", "Test profile already exists: " + testAdminProfileID);
            } else {
                // Profile does not exist, create it
                Profile p = new Profile(testAdminProfileID);
                p.setAdmin(true);
                p.setOrganizer(true);
                p.setName("AdminTester");
                docRef1.set(p);
                Log.d("TestingTests", "Test profile created: " + testAdminProfileID);
            }
        }).addOnFailureListener(e -> Log.e("TestingTests", "Error checking for test profile: " + e));

        // Setting up test facility if it doesn't exist
        docRef2.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("TestingTests", "Test facility already exists: " + documentSnapshot.getId());
            } else {
                // Facility does not exist, create it
                Facility f = new Facility("TestBrowseFacility", "testemail", "12 street", testAdminProfileID);
                docRef2.set(f);
                Log.d("TestingTests", "Test facility created: " + testAdminProfileID);
            }
        }).addOnFailureListener(e -> Log.e("TestingTests", "Error checking for test facility: " + e));
    }

    /**
     * Just to open the app and load the info
     * @throws InterruptedException for sleep function
     */
    @Test
    public void A_begin() throws InterruptedException {
        // Just to open the app and load the info
        loadTestData();
        Thread.sleep(3000);
    }

    /**
     * Tests if Admin Browse Events tab exists in navigation bar
     * @throws InterruptedException for sleep function
     */
    @Test
    public void A_navigateToBrowseEventsTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void B_createEventToUseTest1() throws InterruptedException {
        // First going to facility and creating an event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_facility)).perform(click());
        // Click view events
        onView(withId(R.id.view_event_button)).perform(click());
        // Click add event
        onView(withId(R.id.addEventButton)).perform(click());
        // Enter name + all the other required fields
        onView(withId(R.id.event_title)).check(matches(isDisplayed())).perform(replaceText(eventName1), closeSoftKeyboard());
        onView(withId(R.id.description)).check(matches(isDisplayed())).perform(replaceText("d"), closeSoftKeyboard());

        // For the dates just choose the option it shows and hit "OK" twice
        onView(withId(R.id.event_date)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.registration_start_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.registration_date_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());

        // Enter event capacity
        onView(withId(R.id.event_capacity)).check(matches(isDisplayed())).perform(replaceText("1"), closeSoftKeyboard());

        // Scroll to bottom of screen
        Espresso.onView(ViewMatchers.withId(R.id.add_event_scroll_view)).perform(ViewActions.swipeUp());

        // Wait so we can see the details/allow ui to update
        Thread.sleep(1000);

        // Create the event and wait a bit for toast to clear
        onView(withId(R.id.save_event_button)).perform(click());
        Thread.sleep(5000);
    }

    @Test
    public void B_createEventToUseTest2() throws InterruptedException {
        // First going to facility and creating an event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_facility)).perform(click());
        // Click view events
        onView(withId(R.id.view_event_button)).perform(click());
        // Click add event
        onView(withId(R.id.addEventButton)).perform(click());
        // Enter name + all the other required fields
        onView(withId(R.id.event_title)).check(matches(isDisplayed())).perform(replaceText(eventName2), closeSoftKeyboard());
        onView(withId(R.id.description)).check(matches(isDisplayed())).perform(replaceText("d"), closeSoftKeyboard());

        // For the dates just choose the option it shows and hit "OK" twice
        onView(withId(R.id.event_date)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.registration_start_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.registration_date_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());

        // Enter event capacity
        onView(withId(R.id.event_capacity)).check(matches(isDisplayed())).perform(replaceText("1"), closeSoftKeyboard());

        // Scroll to bottom of screen
        Espresso.onView(ViewMatchers.withId(R.id.add_event_scroll_view)).perform(ViewActions.swipeUp());

        // Wait so we can see the details/allow ui to update
        Thread.sleep(1000);

        // Create the event
        onView(withId(R.id.save_event_button)).perform(click());
        Thread.sleep(5000);
    }

    /**
     * Testing to see if the event is in the recycler view
     */
    @Test
    public void C_browseEventsTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.nav_browse_event)).perform(click());

        // Check to see if the event is in the recycler view
        onView(withText(eventName1)).check(matches(isDisplayed()));
    }

    /** Checks to see if delete event button is there, deletes the event,
     * then goes back to browse events page to check if event is gone.
     * US 03.01.01 As an administrator, I want to be able to remove events.
     * @throws InterruptedException for sleep function
     */
    @Test
    public void D_deleteEventTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());
        onView(withText(eventName1)).perform(click());

        // Check to see if delete event button is there
        onView(withId(R.id.delete_event_button)).check(matches(isDisplayed()));

        // Click the delete event button and go back
        onView(withId(R.id.delete_event_button)).perform(click());
        Thread.sleep(1000);
        pressBack();

        // Check to see that event is gone
        onView(withText(eventName1)).check(doesNotExist());
    }

    /**
     * Checks to see if delete event qr code button is there, delete the code,
     * then goes to the manage event screen to confirm that the qr code is gone
     * @throws InterruptedException for sleep function
     */
    @Test
    public void E_removeQRDataTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());
        onView(withText(eventName2)).perform(click());

        // Check to see if delete event qr code button is there
        onView(withId(R.id.delete_qr_button)).check(matches(isDisplayed()));

        // Click it
        onView(withId(R.id.delete_qr_button)).perform(click());
        Thread.sleep(1000);

        // Making your way back to viewing your event details
        pressBack();
        Thread.sleep(1000);
        pressBack();
        Thread.sleep(1000);

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_facility)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.view_event_button)).perform(click());
        onView(withText(eventName2)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.qr_code_button)).perform(click());

        // Check to see that image isn't loaded ??
    }
}