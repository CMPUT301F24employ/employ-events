package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Root;

import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.Manifest;
import android.widget.TextView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
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
 */

// Makes tests run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminBrowseProfilesUITest {


    // Grants permission to send notifications so pop-up doesn't appear
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private View decorView;
    @Before
    public void setUp() {
        mActivityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    // Tests if Admin Browse Profiles tab exists in navigation bar
    @Test
    public void A_navigateToBrowseProfilesTest() {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());
    }


    // Unfortunately at the one moment the following tests only pass if there is exactly one item in the list with the following details:
    // name- Jasleen and email - jasleen@gmail.com
    // I'm having issues with checking for multiple items in recycler view
    @Test
    public void B_BrowseProfilesTest() {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());
        onView(withId(R.id.profile_name)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_email)).check(matches(isDisplayed())).check(matches(withText("jasleen@gmail.com")));
        onView(withId(R.id.profile_phone_number)).check(matches(isDisplayed())).check(matches(withText("No phone number provided")));
    }


    @Test
    public void C_DeleteAdminProfileTest() throws InterruptedException {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_browse_profiles)).perform(click());
        onView(withId(R.id.profile_name)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_email)).check(matches(isDisplayed())).check(matches(withText("jasleen@gmail.com")));
        onView(withId(R.id.profile_phone_number)).check(matches(isDisplayed())).check(matches(withText("")));
        onView(withId(R.id.delete_profile_button)).check(matches(isDisplayed())).perform(click());
        onView(withText("Sorry, you cannot delete admin profiles.")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));

    }

    @Test
    public void D_DeleteUserProfileTest() {

    }

}
