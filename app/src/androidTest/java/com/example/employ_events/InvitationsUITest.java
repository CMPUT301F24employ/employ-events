package com.example.employ_events;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.employ_events.model.Entrant;
import com.example.employ_events.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Test class for the InvitationsListFragment, InvitationFragment, and InvitationsAdapter.
 * This test uses Firebase Firestore for creating test profiles and events and leverages Espresso for UI testing.
 * Tests include:
 * - Checking if the RecyclerView is visible.
 * - Verifying the RecyclerView has the expected number of items.
 * - Verifying clicking on an invitation
 * - Verifying the acceptance of an invitation clears that invitation and navs back to list.
 * US 01.05.02 As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event
 * US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
 * @author Tina
 */
@RunWith(AndroidJUnit4.class)
public class InvitationsUITest {
    private String testEventId;

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
        editor.putString("uniqueID", "testUniqueId");
        editor.apply();

        // Create a test event in Firestore
        createTestEvent();
    }

    @Test
    public void test01_RecyclerViewDisplaysEvents() throws InterruptedException {
        // Open the navigation drawer and click on Invitations
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_invitations)).perform(click());

        // Wait for the invitations RecyclerView to be displayed
        onView(withId(R.id.invitationsRecyclerView)).check(matches(isDisplayed()));

        // Wait for Firestore to fetch and update the RecyclerView (simulated delay)
        Thread.sleep(3000);

        // Check that the RecyclerView has at least one item (assuming the test event is added)
        onView(withId(R.id.invitationsRecyclerView))
                .check(matches(hasChildCount(1)));

        // Check that the event name is displayed in the RecyclerView
        onView(withText("Invitation Party!")).check(matches(isDisplayed()));
    }

    @Test
    public void test02_DisplaysEventInvitation() throws InterruptedException {
        // Open the navigation drawer and click on Invitations
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_invitations)).perform(click());

        // Wait for the invitations RecyclerView to be displayed
        onView(withId(R.id.invitationsRecyclerView)).check(matches(isDisplayed()));

        // Wait for Firestore to fetch and update the RecyclerView (simulated delay)
        Thread.sleep(3000);

        // Check that the RecyclerView has at least one item (assuming the test event is added)
        onView(withId(R.id.invitationsRecyclerView))
                .check(matches(hasChildCount(1)));  // Assuming 1 item is added to the list

        // Check that the event name is displayed in the RecyclerView
        onView(withText("Invitation Party!")).check(matches(isDisplayed()));
        onView(withText("Invitation Party!")).perform(click());
        Thread.sleep(2000);

        // Check if on Invitation screen and accept/decline buttons present.
        onView(withText("Invitation")).check(matches(isDisplayed()));
        onView(withId(R.id.accept_invitation_button)).check(matches(isDisplayed()));
        onView(withId(R.id.decline_invitation_button)).check(matches(isDisplayed()));
    }


    @Test
    public void test03_AcceptEventInvitation() throws InterruptedException {
        // Open the navigation drawer and click on Invitations
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_invitations)).perform(click());

        // Wait for the invitations RecyclerView to be displayed
        onView(withId(R.id.invitationsRecyclerView)).check(matches(isDisplayed()));

        // Wait for Firestore to fetch and update the RecyclerView (simulated delay)
        Thread.sleep(3000);

        onView(withText("Invitation Party!")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.accept_invitation_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Accept")).perform(click());
        Thread.sleep(5000);

        // Check that we are returned to the invitations RecyclerView.
        onView(withId(R.id.invitationsRecyclerView)).check(matches(isDisplayed()));

        // Check that the RecyclerView has 0 items now
        onView(withId(R.id.invitationsRecyclerView))
                .check(matches(hasChildCount(0)));
    }

    @Test
    public void test03_DeclineEventInvitation() throws InterruptedException {
        // Open the navigation drawer and click on Invitations
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_invitations)).perform(click());

        // Wait for the invitations RecyclerView to be displayed
        onView(withId(R.id.invitationsRecyclerView)).check(matches(isDisplayed()));

        // Wait for Firestore to fetch and update the RecyclerView (simulated delay)
        Thread.sleep(3000);

        onView(withText("Invitation Party!")).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.decline_invitation_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Decline")).perform(click());
        Thread.sleep(5000);

        // Check that we are returned to the invitations RecyclerView.
        onView(withId(R.id.invitationsRecyclerView)).check(matches(isDisplayed()));

        // Check that the RecyclerView has 0 items now
        onView(withId(R.id.invitationsRecyclerView))
                .check(matches(hasChildCount(0)));
    }


    /**
     * Helper method to create a test event in Firestore.
     */
    private void createTestEvent() {
        // Initialize Firestore and prepare a test event
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Event testEvent = new Event();
        testEvent.setEventTitle("Invitation Party!");
        Date currentDate = new Date();
        testEvent.setEventDate(currentDate);

        // Add the test event to Firestore
        db.collection("events").add(testEvent)
                .addOnSuccessListener(documentReference -> {
                    // Add the test entrant to the event's entrantsList
                    testEventId = documentReference.getId();
                    Entrant testEntrant = new Entrant();
                    testEntrant.setUniqueID("testUniqueId");
                    testEntrant.setOnAcceptedList(true);

                    // Add the entrant to the event's entrantsList
                    db.collection("events").document(testEventId)
                            .collection("entrantsList")
                            .document("testUniqueId")
                            .set(testEntrant)
                            .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test entrant added to event."))
                            .addOnFailureListener(e -> Log.e("FirestoreTest", "Error adding test entrant", e));
                })
                .addOnFailureListener(e -> Log.e("FirestoreTest", "Error creating test event", e));
    }

    /**
     * Helper method to delete the test event and its data from Firestore after a test.
     */
    @After
    public void tearDown() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Ensure the event and its entrants are deleted after the test
        if (testEventId != null) {
            db.collection("events").document(testEventId)
                    .collection("entrantsList")
                    .document("testUniqueId")
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test entrant deleted"));

            // Delete the event itself
            db.collection("events").document(testEventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test event deleted"))
                    .addOnFailureListener(e -> Log.e("FirestoreTest", "Error deleting test event", e));
        }
    }
}

