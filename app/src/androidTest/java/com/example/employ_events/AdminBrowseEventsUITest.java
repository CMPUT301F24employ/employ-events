package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@LargeTest

/*
BEFORE TESTING MAKE SURE:
 - You have a profile set up that has admin set to true on firebase
 - You have a facility set up that does not have an event with a name that matches the eventName variable
 */

// Makes tests run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminBrowseEventsUITest {

    String eventName = "Cool Event 542263";

    // Grants permission to send notifications so pop-up doesn't appear
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    // Tests if Admin Browse Events tab exists in navigation bar
    @Test
    public void A_navigateToBrowseEventsTest() {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());
    }

    @Test
    public void B_createToUseTest() throws InterruptedException {
        // First going to facility and creating an event
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_facility)).perform(click());
        // Click view events
        onView(withId(R.id.view_event_button)).perform(click());
        // Click add event
        onView(withId(R.id.addEventButton)).perform(click());
        // Enter name + all the other required fields
        onView(withId(R.id.event_title)).check(matches(isDisplayed())).perform(replaceText(eventName), closeSoftKeyboard());
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

        // Wait 2 seconds so we can see the details/allow ui to update
        Thread.sleep(2000);

        // Create the event
        onView(withId(R.id.save_event_button)).perform(click());
        Thread.sleep(1000);
    }

    // US 03.04.01 As an administrator, I want to be able to browse events.
    @Test
    public void C_browseEventsTest() {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_event)).perform(click());

        // Check to see if the event is in the recycler view
        onView(withId(R.id.all_events_recycler_view)).check(matches(ViewMatchers.hasDescendant(withText(eventName))));
    }

    // US 03.01.01 As an administrator, I want to be able to remove events.
    @Test
    public void D_deleteEventTest() {

    }

    // US 03.03.02 As an administrator, I want to be able to remove hashed QR code data
    @Test
    public void E_removeQRDataTest() {

    }
}