package com.example.employ_events;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;

import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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

import static org.junit.Assert.*;
import com.example.employ_events.ui.profile.Profile;
import com.example.employ_events.ui.admin.ImageBrowseAdapter;
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class AdminTest {
    private Profile profile;

    @Rule
     public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void setUp() {
        profile = new Profile("test");
        profile.setAdmin(true);

    }
    //TEST WON'T WORK IF YOUR ORGANIZER SETTING IS TRUE FOR YOUR USER ID. RERUN THE TEST AND IT SHOULD WORK.
    @Test
    public void test01_AdminButton() throws InterruptedException{
        //onView(withText("Allow")).perform(click());
        onView(withContentDescription("Open navigation drawer")).perform(click());
        Thread.sleep(1500);
//        ActivityScenario<MainActivity> scenario = mActivityScenarioRule.getScenario();
//        scenario.onActivity(activity -> {
//                });
        //scenario.recreate();
        Thread.sleep(1500);
        // Click on the "Facility" menu item
        onView(withId(R.id.nav_image)).check(matches(isDisplayed()));
        //onView(withId(R.id.adminEventListFragment)).check(matches(isDisplayed()));
        //onView(withId(R.id.adminBrowseProfilesFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.adminBrowseFacilitiesFragment)).check(matches(isDisplayed()));
    }
//    @Test
//    public void test02_AdminImageButtonAndBackToHome() throws InterruptedException{
//        ActivityScenario<MainActivity> scenario = mActivityScenarioRule.getScenario();
//        scenario.onActivity(activity -> {
//            profile.setAdmin(true);
//
//        onView(withContentDescription("Open navigation drawer")).perform(click());
//        //Thread.sleep(1500);
//        // Click on the "Facility" menu item
//        onView(withId(R.id.nav_image)).perform(click());
//        //Thread.sleep(1500);
//        onView(withId(R.id.recycler)).check(matches(isDisplayed()));
//        onView(withId(R.id.adminAction)).check(matches(isDisplayed()));
//        //Thread.sleep(1500);
//
//
//        onView(withId(R.id.recycler)).check(matches(isDisplayed()));
//                    RecyclerView recyclerView = activity.findViewById(R.id.recycler);
//                    int initialItemCount = recyclerView.getAdapter().getItemCount();
//                    recyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
//                    onView(withId(R.id.adminAction)).perform(click());
//                    int finalItemCount = recyclerView.getAdapter().getItemCount();
//                    assertEquals(initialItemCount - 1, finalItemCount);
//                });
//        Thread.sleep(1500);
//        onView(withText("Not Now")).perform(click());
//        Thread.sleep(1500);
//        onView(withId(R.id.welcomeMessage)).check(matches(isDisplayed()));
//        onView(withId(R.id.organizersSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventCreateCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventEntrantsCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.entrantsSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.anEntrantCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.wonLotteryCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.invitationsSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.invitationsCount)).check(matches(isDisplayed()));
//    }
//    public void test03_AdminEventButtonAndBackToHome() throws InterruptedException{
//        ActivityScenario<MainActivity> scenario = mActivityScenarioRule.getScenario();
//        scenario.onActivity(activity -> {
//            profile.setAdmin(true);
//
//        onView(withContentDescription("Open navigation drawer")).perform(click());
//        //Thread.sleep(1500);
//        // Click on the "Facility" menu item
//        onView(withId(R.id.adminEventListFragment)).perform(click());
//        //Thread.sleep(1500);
//        onView(withId(R.id.all_events_recycler_view)).check(matches(isDisplayed()));
//
//            RecyclerView recyclerView = activity.findViewById(R.id.all_events_recycler_view);
//            int initialItemCount = recyclerView.getAdapter().getItemCount();
//            recyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
//            onView(withText("DELETE EVENT")).perform(click());
//            onView(withText("Not Now")).perform(click());
//            int finalItemCount = recyclerView.getAdapter().getItemCount();
//            assertEquals(initialItemCount - 1, finalItemCount);
//        });
//        Thread.sleep(1500);
//        onView(withText("Not Now")).perform(click());
//        Thread.sleep(1500);
//        onView(withId(R.id.welcomeMessage)).check(matches(isDisplayed()));
//        onView(withId(R.id.organizersSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventCreateCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventEntrantsCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.entrantsSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.anEntrantCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.wonLotteryCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.invitationsSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.invitationsCount)).check(matches(isDisplayed()));
//    }
//    public void test04_AdminProfilesButtonAndBackToHome() throws InterruptedException{
//        ActivityScenario<MainActivity> scenario = mActivityScenarioRule.getScenario();
//        scenario.onActivity(activity -> {
//            profile.setAdmin(true);
//
//        onView(withContentDescription("Open navigation drawer")).perform(click());
//        //Thread.sleep(1500);
//        // Click on the "Facility" menu item
//        onView(withId(R.id.adminBrowseProfilesFragment)).perform(click());
//        //Thread.sleep(1500);
//        onView(withId(R.id.allProfilesRecyclerView)).check(matches(isDisplayed()));
//        //Thread.sleep(1500);
//            RecyclerView recyclerView = activity.findViewById(R.id.allProfilesRecyclerView);
//            int initialItemCount = recyclerView.getAdapter().getItemCount();
//            recyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
//            onView(withText("DELETE PROFILE")).perform(click());
//            int finalItemCount = recyclerView.getAdapter().getItemCount();
//            assertEquals(initialItemCount - 1, finalItemCount);
//        });
//        onView(withText("Not Now")).perform(click());
//        Thread.sleep(1500);
//        onView(withId(R.id.welcomeMessage)).check(matches(isDisplayed()));
//        onView(withId(R.id.organizersSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventCreateCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventEntrantsCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.entrantsSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.anEntrantCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.wonLotteryCount)).check(matches(isDisplayed()));
//        onView(withId(R.id.invitationsSection)).check(matches(isDisplayed()));
//        onView(withId(R.id.invitationsCount)).check(matches(isDisplayed()));
//    }
}