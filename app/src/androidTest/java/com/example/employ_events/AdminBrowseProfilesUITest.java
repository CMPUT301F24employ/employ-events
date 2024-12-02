package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.employ_events.model.Event;
import com.example.employ_events.model.Facility;
import com.example.employ_events.model.Profile;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;

/**
 * Test class for AdminBrowseProfilesFragment, ProfileFragment, and ProfileAdapter.
 * This test uses Firebase Firestore for creating test profiles, facility, and event, and leverages Espresso for UI testing.
 * Tests include:
 * - The Admin Browse Profiles tab exists
 * - Checking if the RecyclerView is visible and displays the correct information.
 * - Tests if an admin is unable to delete admin profiles, including their own.
 * - Tests if an admin is able to delete a user profile and it's associated data (if there is any) such as facility and events.
 * US 03.05.01 As an administrator, I want to be able to browse profiles.
 * US 03.02.01 As an administrator, I want to be able to remove profiles.
 * ISSUES: Having issues testing for toasts and scrolling. So, I'm unable to test for something that's in the list, but just not in view.
 * @author Jasleen
 * @author Tina
 * @author Connor
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

/*
    BEFORE TESTING MAKE SURE:
    There are no duplicate profiles, facilities, and events on firebase.
    Otherwise, some tests will fail.
 */

// Makes tests run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminBrowseProfilesUITest {
    private FirebaseFirestore db;
    String adminUID = "AdminProfileTesterUID_Admin", organizerUID = "AdminProfileTesterUID_Organizer",
            facilityID = "AdminProfileTester_FID", eventName = "Tea Party Time", eventID = "AdminProfileTester_EID";

    // Grants permission to send notifications so pop-up doesn't appear
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Set the uniqueID in SharedPreferences (simulate a logged-in user)
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uniqueID", adminUID);
        editor.apply();
    }

    /**
     * Helper method to create data in firestore for the tests.
     * @author Jasleen
     * @author Connor
     * @author Tina
     */
    public void loadData() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Create a test profile with admin set to true
        Profile testProfileAdmin = new Profile(adminUID);
        testProfileAdmin.setName("Jasleen");
        testProfileAdmin.setEmail("jasleen@gmail.com");
        testProfileAdmin.setAdmin(true);

        // Upload test profile to Firestore
        db.collection("userProfiles").document(adminUID)
                .set(testProfileAdmin)
                .addOnSuccessListener(aVoid -> Log.d("AdminBrowseProfilesUITest", "Test profile added"))
                .addOnFailureListener(e -> Log.e("AdminBrowseProfilesUITest", "Error adding profile", e));

        // Create a test profile for an organizer
        Profile testProfileOrganizer = new Profile(organizerUID);
        testProfileOrganizer.setName("Tina Le");
        testProfileOrganizer.setEmail("tinale@gmail.com");
        testProfileOrganizer.setPhoneNumber("100 1000 1000");

        // Upload test profile to Firestore
        db.collection("userProfiles").document(organizerUID)
                .set(testProfileOrganizer)
                .addOnSuccessListener(aVoid -> Log.d("AdminBrowseProfilesUITest", "Test profile added"))
                .addOnFailureListener(e -> Log.e("AdminBrowseProfilesUITest", "Error adding profile", e));

        Facility facility = new Facility("Tina's Facility", "tina@facility.com", "Tina St, Tina Town", organizerUID);
        db.collection("facilities").document(facilityID).set(facility)
                .addOnSuccessListener(aVoid -> Log.d("AdminBrowseProfilesUITest", "Test profile added"))
                .addOnFailureListener(e -> Log.e("AdminBrowseProfilesUITest", "Error adding profile", e));

        Event event = new Event();
        event.setEventTitle(eventName);
        event.setFacilityID(facilityID);
        event.setEventDate(new Date());
        event.setRegistrationStartDate(new Date());
        event.setRegistrationDateDeadline(new Date());
        event.setGeoLocation(false);
        event.setDescription("Tea with Tina");
        event.setEventCapacity(5);
        event.setId(eventID);

        db.collection("events").document(eventID).set(event)
                .addOnSuccessListener(aVoid -> Log.d("AdminBrowseProfilesUITest", "Test profile added"))
                .addOnFailureListener(e -> Log.e("AdminBrowseProfilesUITest", "Error adding profile", e));
    }

    /**
     * Initialize data
     * @author Connor
     */
    @Test
    public void A_begin() throws InterruptedException {
        loadData();
        Thread.sleep(3000);
    }


    /**
     * Tests if Admin Browse Profiles tab exists in navigation bar
     * @author Jasleen
     */
    @Test
    public void B_navigateToBrowseProfilesTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.nav_browse_profiles)).perform(click());
        Thread.sleep(1000);

    }


    /**
     * Tests if the information I'm seeing in the list, matches the information I see on firebase
     * @author Jasleen
     */
    @Test
    public void C_BrowseProfilesTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_browse_profiles)).perform(click());
        Thread.sleep(2000);

        onView(withText("Jasleen")).check(matches(isDisplayed()));
        onView(withText("jasleen@gmail.com")).check(matches(isDisplayed()));
    }


    /**
     * Tests if I'm unable to delete admin profiles, including my own.
     * @author Jasleen
     */
    @Test
    public void D_DeleteAdminProfileTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_browse_profiles)).perform(click());
        Thread.sleep(2000);

        onView(withText("Jasleen")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(2000);

        // Checking if I'm on the profile details page
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_email)).check(matches(isDisplayed())).check(matches(withText("jasleen@gmail.com")));
        onView(withId(R.id.profile_phone_number)).check(matches(isDisplayed())).check(matches(withText("")));
        // Pressing the delete button, but it should not do anything since it's an admin profile
        onView(withId(R.id.delete_profile_button)).check(matches(isDisplayed())).perform(click());
        Thread.sleep(2000);

        // Unfortunately, I am having trouble testing for toasts, so the only way I can verify if the profile is not deleted is by checking if it still exists in the browse list.
        // Going back to the list and verifying if the profile still exists, meaning it did not get deleted
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(2000);

        onView(withText("Jasleen")).check(matches(isDisplayed()));
        onView(withText("jasleen@gmail.com")).check(matches(isDisplayed()));
    }

    /**
     * Checks that deleting a profile deletes affiliated facility and events.
     * @author Jasleen
     * @author Tina
     */
    @Test
    public void E_DeleteUserProfileTest() throws InterruptedException {
        // Confirming if facility corresponding to profile exists
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.adminBrowseFacilitiesFragment)).perform(click());
        Thread.sleep(2000);

        onView(withText("Tina's Facility")).check(matches(isDisplayed()));
        onView(withText("tina@facility.com")).check(matches(isDisplayed()));
        onView(withText("Tina St, Tina Town")).check(matches(isDisplayed()));

        // Confirming if event corresponding to profile and facility exists
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_browse_event)).perform(click());
        Thread.sleep(2000);

        onView(withText(eventName)).check(matches(isDisplayed()));

        // Going to browse profiles page, and deleting a user profile
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_browse_profiles)).perform(click());
        Thread.sleep(2000);

        onView(withText("Tina Le")).check(matches(isDisplayed())).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.profile_name)).check(matches(withText("Tina Le")));
        onView(withId(R.id.profile_email)).check(matches(isDisplayed())).check(matches(withText("tinale@gmail.com")));
        onView(withId(R.id.profile_phone_number)).check(matches(isDisplayed())).check(matches(withText("100 1000 1000")));

        // Pressing the delete button, and it should send me back to the list page after deleting
        onView(withId(R.id.delete_profile_button)).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);

        // Verifying if I'm back on the list page, and the profile does not exist anymore, meaning it got deleted
        onView(withText("Tina Le")).check(doesNotExist());
        onView(withText("tinale@gmail.com")).check(doesNotExist());
        onView(withText("100 1000 1000")).check(doesNotExist());

        // Verifying if the associated facility got deleted as well
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.adminBrowseFacilitiesFragment)).perform(click());
        Thread.sleep(1000);

        onView(withText("Tina's Facility")).check(doesNotExist());
        onView(withText("tina@facility.com")).check(doesNotExist());
        onView(withText("Tina St, Tina Town")).check(doesNotExist());

        // Verifying if the associated event got deleted as well
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.nav_browse_event)).perform(click());
        Thread.sleep(2000);

        onView(withText(eventName)).check(doesNotExist());

    }
}
