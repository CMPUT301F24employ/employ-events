package com.example.employ_events;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import com.example.employ_events.ui.entrants.Entrant;
import com.example.employ_events.ui.events.Event;
import com.example.employ_events.ui.invitation.InvitationsListFragment;
import com.example.employ_events.ui.profile.Profile;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Tina
 * Test class for the InvitationsListFragment. This class performs unit tests to validate
 * the functionality of the InvitationsListFragment, such as verifying that the RecyclerView
 * is displayed correctly, and that the data is properly loaded and displayed in the fragment.
 *
 * Tests include:
 * - Checking if the RecyclerView is visible.
 * - Verifying the RecyclerView has the expected number of items.
 *
 * This test uses Firebase Firestore for creating test profiles and events and leverages
 * Espresso for UI testing.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvitationsListFragmentTest {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentScenario<InvitationsListFragment> fragmentScenario;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void launchActivity() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> activity.getIntent().putExtra("isTestMode", true));
    }


    /**
     * Set up the test environment before each test case. This method does the following:
     * - Creates a test user profile and test event with an invitation for the user.
     * - Initializes SharedPreferences with the test unique user ID.
     * - Launches the InvitationsListFragment for testing.
     */
    @Before
    public void setUp() throws InterruptedException {
        String testUniqueID = "testUser123";

        // Set up SharedPreferences
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uniqueID", testUniqueID);
        editor.apply();

        // Wait for Firestore writes to complete
        CountDownLatch latch = new CountDownLatch(2);

        createTestUserProfile(testUniqueID, latch);
        createTestEventWithInvitation("event123", testUniqueID, latch);

        latch.await(10, TimeUnit.SECONDS); // Wait up to 5 seconds for Firestore writes

        fragmentScenario = FragmentScenario.launchInContainer(InvitationsListFragment.class);


    }

    /**
     * Test to check if the fragment launches correctly and the RecyclerView is displayed.
     * This test validates the visibility of the RecyclerView in the fragment.
     */
    @Test
    public void testA_FragmentLaunchesWithRecyclerView() {
        // Check if the RecyclerView is displayed correctly
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    /**
     * Test to check if the RecyclerView in the fragment contains exactly one item.
     * This test performs a swipe action to ensure that items in the RecyclerView are loaded,
     * then checks if the RecyclerView has the expected number of items.
     */
    /*
    @Test
    public void testB_RecyclerViewHasOneItem() {
        // Perform a swipe up or down to scroll in the RecyclerView
        ViewInteraction recyclerView = Espresso.onView(ViewMatchers.withId(R.id.recyclerView));

        // Scroll by performing a swipe up
        recyclerView.perform(ViewActions.swipeUp());

        // Check that the recycler view has one item
        //onView(withId(R.id.recyclerView))
        //        .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)));


    }

     */

    /**
     * Clean up after the test is finished by closing the fragment scenario to release resources.
     */
    @After
    public void tearDown() {
        fragmentScenario.close();
    }

    /**
     * Helper method to create a test user profile in Firestore.
     * @param uniqueID The unique ID of the user to create.
     */
    public static void createTestUserProfile(String uniqueID, CountDownLatch latch) {
        Profile testProfile = new Profile(uniqueID);
        testProfile.setEmail("testuser@example.com");
        testProfile.setName("Test User");
        db.collection("users").document(uniqueID)
                .set(testProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreTest", "Test user profile created");
                    latch.countDown(); // Decrement latch when the write is successful
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreTest", "Error creating test user profile", e);
                    latch.countDown(); // Ensure latch is counted down even on failure
                });
    }

    /**
     * Helper method to create a test event with an invitation for the given user.
     * @param eventId   The ID of the event to create.
     * @param uniqueID  The unique ID of the user to invite.
     */
    public static void createTestEventWithInvitation(String eventId, String uniqueID, CountDownLatch latch) {
        Event testEvent = new Event();
        testEvent.setEventTitle("Test Event");
        testEvent.setId(eventId);
        db.collection("events").document(eventId)
                .set(testEvent)
                .addOnSuccessListener(aVoid -> {
                    Entrant testEntrant = new Entrant();
                    testEntrant.setUniqueID(uniqueID);
                    testEntrant.setOnAcceptedList(true);
                    db.collection("events").document(eventId)
                            .collection("entrantsList").document(uniqueID)
                            .set(testEntrant)
                            .addOnSuccessListener(innerVoid -> {
                                Log.d("FirestoreTest", "Test entrant added");
                                latch.countDown(); // Decrement latch when entrant is added
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreTest", "Error adding test entrant", e);
                                latch.countDown(); // Ensure latch is counted down even on failure
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreTest", "Error creating test event", e);
                    latch.countDown(); // Ensure latch is counted down even on failure
                });
    }
}
