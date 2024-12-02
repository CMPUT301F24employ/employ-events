package com.example.employ_events;
import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static org.hamcrest.Matchers.not;

import com.example.employ_events.model.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
/*
Test must be ran all at once due to test_01 allowing for refresh of admin status.
 */
/**
 * Admin browse image and browse facility ui test
 * @author Aaron
 * @author Tina
 * @author Jasleen
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class AdminBrowseImageAndFacilityTest {
    private FirebaseFirestore db;
    String adminImageUID = "adminImageUID";

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Sets the uniqueID for testing and create an admin profile.
     * @author Tina
     */
    @Before
    public void setUp() throws InterruptedException {
        // Set the uniqueID in SharedPreferences (simulate a logged-in user)
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uniqueID", adminImageUID);
        editor.apply();

        // Create a test profile in Firestore
        createProfile();
    }

    /**
     * Helper method to create a test profile in Firestore.
     * @author Jasleen
     */
    public void createProfile() throws InterruptedException {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Create a test profile with admin set to true
        Profile testProfile = new Profile(adminImageUID);
        testProfile.setName("Jasleen Tina");
        testProfile.setEmail("jasleentina@gmail.com");
        testProfile.setAdmin(true);

        // Upload test profile to Firestore
        db.collection("userProfiles").document(adminImageUID)
                .set(testProfile)
                .addOnSuccessListener(aVoid -> Log.d("AdminBrowseProfilesUITest", "Test profile added"))
                .addOnFailureListener(e -> Log.e("AdminBrowseProfilesUITest", "Error adding profile", e));

    }

    /**
     * Refreshing the app allows the admin status to update, otherwise all the tests fail
     * @author Jasleen
      */
    @Test
    public void test01_refreshTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
    }

    @Test
    public void test02_AdminButton() throws InterruptedException{
        Thread.sleep(2000);
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Wait for the menu to open
        Thread.sleep(2000);
        // Check if admin buttons are shown
        onView(withId(R.id.nav_image)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_browse_event)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_browse_profiles)).check(matches(isDisplayed()));
        onView(withId(R.id.adminBrowseFacilitiesFragment)).check(matches(isDisplayed()));
    }

    /**
     * US 03.03.01 As an administrator, I want to be able to remove images. &
     * US 03.06.01 As an administrator, I want to be able to browse images.
     * @author Aaron
     */
    @Test
    public void test03_AdminImageButtonAndBackToHome() throws InterruptedException{
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Wait for the menu to open
        Thread.sleep(3000);
        // Click on the "Admin image" menu item
        onView(withId(R.id.nav_image)).perform(click());
        // Wait for the screen to open
        Thread.sleep(1500);
        // Check with if it's on the admin image screen
        onView(withId(R.id.recycler)).check(matches(isDisplayed()));
        onView(withId(R.id.adminAction)).check(matches(isDisplayed()));
        Thread.sleep(2000);
        // Swipe up and down to see the other things from the list
        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(ViewActions.swipeUp());
        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(ViewActions.swipeDown());
        ActivityScenario<MainActivity> scenario = mActivityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            // Check if there is more than one item in the recyclerView
            RecyclerView recyclerView = activity.findViewById(R.id.recycler);
            if(recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
                Button button = activity.findViewById(R.id.adminAction);
                // Click on the first item of the list
                recyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
                // Click the button to remove the image
                button.performClick();
            }else {
                // RecyclerView is empty
                Toast.makeText(getContext(), "No image ", Toast.LENGTH_SHORT).show();
            }
        });

        // Wait for the action to happen
        Thread.sleep(1500);
        // Go back to the main screen
        onView(withContentDescription("Navigate up")).perform(click());
        // Wait for the screen to open
        Thread.sleep(1500);
        // Check if you are in the main screen
        onView(withId(R.id.welcomeMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.organizersSection)).check(matches(isDisplayed()));
        onView(withId(R.id.eventCreateCount)).check(matches(isDisplayed()));
        onView(withId(R.id.eventEntrantsCount)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantsSection)).check(matches(isDisplayed()));
        onView(withId(R.id.anEntrantCount)).check(matches(isDisplayed()));
        onView(withId(R.id.wonLotteryCount)).check(matches(isDisplayed()));
        onView(withId(R.id.invitationsSection)).check(matches(isDisplayed()));
        onView(withId(R.id.invitationsCount)).check(matches(isDisplayed()));
    }
    /**
     * US 03.07.01 As an administrator I want to remove facilities that violate app policy
     * @author Aaron
     * @author Tina
     */
    @Test
    public void test04_AdminFacilitiesButtonAndBackToHome() throws InterruptedException{
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Wait for the menu to open
        Thread.sleep(3000);

        // CLick on facility button to make a facility
        onView(withId(R.id.nav_facility)).perform(click());
        Thread.sleep(2000);

        // Enter the information in the right place
        onView(withId(R.id.editFacilityName)).perform(ViewActions.replaceText("Employ Centre"));
        onView(withId(R.id.editFacilityEmail)).perform(ViewActions.replaceText("employ@events.com"));
        onView(withId(R.id.editFacilityAddress)).perform(ViewActions.replaceText("Employ St, Edmonton, AB"));
        Thread.sleep(1500);

        // Create the facility
        onView(withText("Create")).perform(click());
        Thread.sleep(1500);

        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(3000);

        //Click on the admin facilities browse
        onView(withId(R.id.adminBrowseFacilitiesFragment)).perform(click());
        Thread.sleep(3000);

        // Check if you are on the right screen
        onView(withId(R.id.allFacilitiesRecyclerView)).check(matches(isDisplayed()));

        // Swipe up and down to see the other things from the list
        Espresso.onView(ViewMatchers.withId(R.id.allFacilitiesRecyclerView)).perform(ViewActions.swipeUp());
        Espresso.onView(ViewMatchers.withId(R.id.allFacilitiesRecyclerView)).perform(ViewActions.swipeDown());

        // Click on the facility that you just made
        onView(withText("Employ Centre")).perform(click());
        Thread.sleep(2000);

        // Check if the information are right
        onView(withText("Employ Centre")).check(matches(isDisplayed()));
        onView(withText("employ@events.com")).check(matches(isDisplayed()));
        onView(withText("Employ St, Edmonton, AB")).check(matches(isDisplayed()));

        // Delete the facility
        onView(withText("DELETE FACILITY")).perform(click());
        Thread.sleep(2000);
        // Check if the facility that you just deleted still in the list
        onView(withId(R.id.adminBrowseFacilitiesFragment)).check(matches(not(hasDescendant(withText("test")))));

        //Go back to the main screen
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);

        // Check if you are in the main screen
        onView(withId(R.id.welcomeMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.organizersSection)).check(matches(isDisplayed()));
        onView(withId(R.id.eventCreateCount)).check(matches(isDisplayed()));
        onView(withId(R.id.eventEntrantsCount)).check(matches(isDisplayed()));
        onView(withId(R.id.entrantsSection)).check(matches(isDisplayed()));
        onView(withId(R.id.anEntrantCount)).check(matches(isDisplayed()));
        onView(withId(R.id.wonLotteryCount)).check(matches(isDisplayed()));
        onView(withId(R.id.invitationsSection)).check(matches(isDisplayed()));
        onView(withId(R.id.invitationsCount)).check(matches(isDisplayed()));
    }

    /**
     * Helper method to delete the test profile and its data from Firestore after a test.
     * @author Tina
     */
    @After
    public void tearDown() {
        db.collection("userProfiles")
                .whereEqualTo("uniqueID", adminImageUID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete the profile
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(aVoid -> Log.d("FacilityTest", "Profile deleted"))
                                .addOnFailureListener(e -> Log.e("FacilityTest", "Error deleting profile", e));
                    }
                });

        db.collection("facilities")
                .whereEqualTo("organizer_id", adminImageUID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete the profile
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference().delete()
                                .addOnSuccessListener(aVoid -> Log.d("FacilityTest", "Profile deleted"))
                                .addOnFailureListener(e -> Log.e("FacilityTest", "Error deleting profile", e));
                    }
                });

    }
}