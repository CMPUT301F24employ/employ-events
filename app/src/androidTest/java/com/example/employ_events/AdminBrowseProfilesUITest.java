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
import com.example.employ_events.model.Profile;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Test class for AdminBrowseProfilesFragment, ProfileFragment, and ProfileAdapter.
 * This test uses Firebase Firestore for creating a test profile and leverages Espresso for UI testing.
 * Tests include:
 * - The Admin Browse Profiles tab exists
 * - Checking if the RecyclerView is visible and displays the correct information.
 * - Tests if an admin is unable to delete admin profiles, including their own.
 * - Tests if an admin is able to delete a user profile and it's associated data (if there is any) such as facility and events.
 * US 03.05.01 As an administrator, I want to be able to browse profiles.
 * US 03.02.01 As an administrator, I want to be able to remove profiles.
 * @author Jasleen
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

/*
    BEFORE TESTING MAKE SURE:
    - There are no duplicate profiles, facilities, and events on firebase
    - And the following user profile exists as that is the one this test targets
        Name:
        Email:
        Phone Number:
    - If this profile exists, then it's corresponding facility and event will too.
 */

// Makes tests run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminBrowseProfilesUITest {
    private FirebaseFirestore db;

    // Grants permission to send notifications so pop-up doesn't appear
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws InterruptedException {
        // Set the uniqueID in SharedPreferences (simulate a logged-in user)
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uniqueID", "testProfileUniqueID");
        editor.apply();

        // Create a test profile in Firestore
        createProfile();
    }

    /**
     * Helper method to create a test profile in Firestore.
     */
    public void createProfile() throws InterruptedException {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Create a test profile with admin set to true
        Profile testProfile = new Profile("TestProfileUniqueID");
        testProfile.setName("Jasleen");
        testProfile.setEmail("jasleen@gmail.com");
        testProfile.setAdmin(true);

        // Upload test profile to Firestore
        db.collection("userProfiles").document("testProfileUniqueID")
                .set(testProfile)
                .addOnSuccessListener(aVoid -> Log.d("AdminBrowseProfilesUITest", "Test profile added"))
                .addOnFailureListener(e -> Log.e("AdminBrowseProfilesUITest", "Error adding profile", e));

    }

    // Refreshing the app allows the admin status to update, otherwise all the tests fail
    @Test
    public void A_refreshTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());

    }


    // Tests if Admin Browse Profiles tab exists in navigation bar
    @Test
    public void B_navigateToBrowseProfilesTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());

    }


    // Tests if the information I'm seeing in the list, matches the information I see on firebase
    @Test
    public void C_BrowseProfilesTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());
        Thread.sleep(2000);
        onView(withText("Jasleen")).check(matches(isDisplayed()));
        onView(withText("jasleen@gmail.com")).check(matches(isDisplayed()));
        //onView(withText("Connor")).check(matches(isDisplayed()));
        //onView(withText("conemail")).check(matches(isDisplayed()));
        //onView(withText("7")).check(matches(isDisplayed()));
    }


    // Tests if I'm unable to delete admin profiles, including my own.
    // Unfortunately, I am having trouble testing for toasts, so the only way I can verify if the profile is not deleted is by checking if it still exists in the browse list.
    @Test
    public void D_DeleteAdminProfileTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());
        onView(withText("Jasleen")).check(matches(isDisplayed())).perform(click());
        // Checking if I'm on the profile details page
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_email)).check(matches(isDisplayed())).check(matches(withText("jasleen@gmail.com")));
        onView(withId(R.id.profile_phone_number)).check(matches(isDisplayed())).check(matches(withText("")));
        // Pressing the delete button, but it should not do anything since it's an admin profile
        onView(withId(R.id.delete_profile_button)).check(matches(isDisplayed())).perform(click());

        // Going back to the list and verifying if the profile still exists, meaning it did not get deleted
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed())).perform(click());
        onView(withText("Jasleen")).check(matches(isDisplayed()));
        onView(withText("jasleen@gmail.com")).check(matches(isDisplayed()));
    }

    @Test
    public void E_DeleteUserProfileTest() throws InterruptedException {
        // Confirming if facility corresponding to profile exists
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.adminBrowseFacilitiesFragment)).perform(click());
        onView(withText("CFac")).check(matches(isDisplayed()));
        onView(withText("conemail")).check(matches(isDisplayed()));
        onView(withText("11")).check(matches(isDisplayed()));

        // Confirming if event corresponding to profile and facility exists
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());
        onView(withText("Conevent")).check(matches(isDisplayed()));

        // Going to browse profiles page, and deleting a user profile
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());
        onView(withText("Connor")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.profile_name)).check(matches(withText("Connor")));
        onView(withId(R.id.profile_email)).check(matches(isDisplayed())).check(matches(withText("conemail")));
        onView(withId(R.id.profile_phone_number)).check(matches(isDisplayed())).check(matches(withText("7")));

        // Pressing the delete button, and it should send me back to the list page after deleting
        onView(withId(R.id.delete_profile_button)).check(matches(isDisplayed())).perform(click());
        Thread.sleep(1000);
        // Verifying if I'm back on the list page, and the profile does not exist anymore, meaning it got deleted
        onView(withText("Connor")).check(doesNotExist());
        onView(withText("conemail")).check(doesNotExist());
        onView(withText("7")).check(doesNotExist());

        // Verifying if the associated facility got deleted as well
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.adminBrowseFacilitiesFragment)).perform(click());
        Thread.sleep(1000);
        onView(withText("CFac")).check(doesNotExist());
        onView(withText("conemail")).check(doesNotExist());
        onView(withText("11")).check(doesNotExist());

        // Verifying if the associated event got deleted as well
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());
        onView(withText("Conevent")).check(doesNotExist());

    }

    /**
     * Helper method to delete the test profile and its data from Firestore after a test.
     */
    @After
    public void tearDown() {
        db.collection("userProfiles")
                .whereEqualTo("uniqueID", "testProfileUniqueID")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete the profile
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(aVoid -> Log.d("ProfileTest", "Profile deleted"))
                                .addOnFailureListener(e -> Log.e("ProfileTest", "Error deleting profile", e));
                    } else {
                        Log.d("AdminBrowseProfilesUITest", "No document found with uniqueID = TestProfileUniqueID");
                    }
                });
        }
}
