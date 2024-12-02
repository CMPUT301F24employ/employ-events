package com.example.employ_events;
import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;

import android.Manifest;
import android.widget.Button;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
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
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static org.hamcrest.Matchers.not;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)

/*
BEFORE TESTING MAKE SURE:
 - You have a profile set up that has admin set to true on firebase
 - You might have to run test one by one
 */
/**
 * Admin browse image and browse facility ui test
 * @author Aaron
 **/
public class AdminBrowseImageAndFacilityTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void test01_AdminButton() throws InterruptedException{
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Wait for the menu to open
        Thread.sleep(1500);
        // Check if admin buttons are shown
        onView(withId(R.id.nav_image)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_browse_event)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_browse_profiles)).check(matches(isDisplayed()));
        onView(withId(R.id.adminBrowseFacilitiesFragment)).check(matches(isDisplayed()));
    }
    /**
     * US 03.03.01 As an administrator, I want to be able to remove images. &
     * US 03.06.01 As an administrator, I want to be able to browse images.
     */


    @Test
    public void test02_AdminImageButtonAndBackToHome() throws InterruptedException{
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Wait for the menu to open
        Thread.sleep(1500);
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
     */

    public void test03_AdminFacilitiesButtonAndBackToHome() throws InterruptedException{
        Thread.sleep(1500);
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        // Wait for the menu to open
        Thread.sleep(1500);
        // CLick on facility button to make a facility
        onView(withText("Facility")).perform(click());
        // Wait for the screen to open
        Thread.sleep(1500);
        // Enter the information in the right place
        onView(withId(R.id.editFacilityName)).perform(ViewActions.replaceText("Tests"));
        onView(withId(R.id.editFacilityEmail)).perform(ViewActions.replaceText("test12@gmail.com"));
        onView(withId(R.id.editFacilityAddress)).perform(ViewActions.replaceText("edmonton"));
        // Wait for the action to open
        Thread.sleep(1500);
        // Create the facility
        onView(withText("Create")).perform(click());
        // Wait for it to complete cresting
        Thread.sleep(1500);
        // Open navigation drawer
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(1500);
        //Click on the admin facilities browse
        onView(withId(R.id.adminBrowseFacilitiesFragment)).perform(click());
        // Wait for the menu to open
        Thread.sleep(1500);
        // Check if you are on the right screen
        onView(withId(R.id.allFacilitiesRecyclerView)).check(matches(isDisplayed()));
        // Swipe up and down to see the other things from the list
        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(ViewActions.swipeUp());
        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(ViewActions.swipeDown());
        // Click on the facility that you just made
        onView(withText("Tests")).perform(click());
        Thread.sleep(1500);
        // Check if the information are right
        onView(withText("Tests")).check(matches(isDisplayed()));
        onView(withText("test12@gmail.com")).check(matches(isDisplayed()));
        onView(withText("edmonton")).check(matches(isDisplayed()));
        Thread.sleep(1500);
        // Delete the facility
        onView(withText("DELETE FACILITY")).perform(click());
        Thread.sleep(1500);
        // Check if the facility that you just deleted still in the list
        onView(withId(R.id.adminBrowseFacilitiesFragment)).check(matches(not(hasDescendant(withText("test")))));
        Thread.sleep(1500);
        //Go back to the main screen
        onView(withContentDescription("Navigate up")).perform(click());
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
}