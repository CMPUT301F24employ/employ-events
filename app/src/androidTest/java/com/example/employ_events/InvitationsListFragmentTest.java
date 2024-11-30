package com.example.employ_events;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
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
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class InvitationsListFragmentTest {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentScenario<InvitationsListFragment> fragmentScenario;

    /**
     * Set up the test environment before each test case. This method does the following:
     * - Creates a test user profile and test event with an invitation for the user.
     * - Initializes SharedPreferences with the test unique user ID.
     * - Launches the InvitationsListFragment for testing.
     */
    @Before
    public void setUp() {
        // Set up a test profile and event data
        String testUniqueID = "testUser123";

        // Set up SharedPreferences with test data
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uniqueID", testUniqueID); // Store the test unique ID
        editor.apply();

        // Add a test user and test event into firebase
        createTestUserProfile(testUniqueID);
        createTestEventWithInvitation("event123", testUniqueID);

        // Initialize the fragment scenario before each test
        fragmentScenario = FragmentScenario.launchInContainer(InvitationsListFragment.class);
    }

    /**
     * Test to check if the fragment launches correctly and the RecyclerView is displayed.
     * This test validates the visibility of the RecyclerView in the fragment.
     */
    @Test
    public void testFragmentLaunchesWithRecyclerView() {
        // Check if the RecyclerView is displayed correctly
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    /**
     * Test to check if the RecyclerView in the fragment contains exactly one item.
     * This test performs a swipe action to ensure that items in the RecyclerView are loaded,
     * then checks if the RecyclerView has the expected number of items.
     */
    @Test
    public void testRecyclerViewHasOneItem() {
        // Perform a swipe up or down to scroll in the RecyclerView
        ViewInteraction recyclerView = Espresso.onView(ViewMatchers.withId(R.id.recyclerView));

        // Scroll by performing a swipe up
        recyclerView.perform(ViewActions.swipeUp());

        // Check that the recycler view has one item
        onView(withId(R.id.recyclerView))
                .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)));
    }

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
    public static void createTestUserProfile(String uniqueID) {
        // Create a test user profile in the users collection
        Profile testProfile = new Profile(uniqueID);
        testProfile.setEmail("testuser@example.com");
        testProfile.setName("Test User");
        db.collection("users").document(uniqueID)
                .set(testProfile)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test user profile created"))
                .addOnFailureListener(e -> Log.e("FirestoreTest", "Error creating test user profile", e));
    }

    /**
     * Helper method to create a test event with an invitation for the given user.
     * @param eventId   The ID of the event to create.
     * @param uniqueID  The unique ID of the user to invite.
     */
    public static void createTestEventWithInvitation(String eventId, String uniqueID) {
        // Create an event and add it to the events collection
        Event testEvent = new Event();
        testEvent.setEventTitle("Test Event");
        testEvent.setId(eventId);
        db.collection("events").document(eventId)
                .set(testEvent)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test event created"))
                .addOnFailureListener(e -> Log.e("FirestoreTest", "Error creating test event", e));

        // Create an entrant based off the created profile
        Entrant testEntrant = new Entrant();
        testEntrant.setUniqueID(uniqueID);
        testEntrant.setOnAcceptedList(true);
        // Add the test user to the entrants list for the event
        db.collection("events").document(eventId)
                .collection("entrantsList").document(uniqueID)
                .set(testEntrant)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test entrant added"))
                .addOnFailureListener(e -> Log.e("FirestoreTest", "Error adding test entrant", e));
    }
}
