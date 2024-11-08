package com.example.employ_events;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
    Unable to test for upload and remove image and deterministically generated from profile name.
    Not sure how to test if drawable changed from "@drawable/white_person" to whatever the user uploaded
    or if it was determined by their initial or something.
    I am able to test if the pfp exists, but not if it changes.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProfileUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void navigateToProfileTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());

        // Check if on this page a user profile exists
        onView(withId(R.id.userPFP)).check(matches(isDisplayed()));

        // Check if the edit button exists
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));

    }

    @Test
    public void provideInfoTest() throws InterruptedException {

        // US 01.02.01
        // Open side navigation bar
        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());

        // Click on the Profile option in the navigation bar
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());

        // Check if all fields are null

        // Click on the Edit Profile button
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Enter name
        onView(withId(R.id.editTextUserName)).check(matches(isDisplayed())).perform(replaceText("Jasleen"), closeSoftKeyboard());

        // Enter email
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(isDisplayed())).perform(replaceText("jasleen.h@gmail.com"), closeSoftKeyboard());

        // Click on the Confirm Changes button to save
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(1000); // 1 second delay to allow UI to update before asserting

        // Check if Name displays "Jasleen" on right-hand side
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));

        // Check if Email displays the following
        onView(withId(R.id.profile_email)).check(matches(withText("jasleen.h@gmail.com")));

        // Check if Phone Number is empty as nothing was provided
        onView(withId(R.id.profile_phone_number)).check(matches(withText("")));
    }

    @Test
    public void editProfileSwitchTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Check if on this page confirm changes and upload buttons exist
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadPFP)).check(matches(isDisplayed()));
    }

    @Test
    public void updateInfoTest() throws InterruptedException {

        // US 01.02.02
        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());


        onView(withId(R.id.editTextUserName)).check(matches(isDisplayed())).perform(replaceText("Jasleen"), closeSoftKeyboard());
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(isDisplayed())).perform(replaceText("jasleen.1@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(1000);

        // Check if Name displays "Jasleen"
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));

        // Check if Email displays the following
        onView(withId(R.id.profile_email)).check(matches(withText("jasleen.1@gmail.com")));

        // Check if Phone Number is empty as nothing was provided
        onView(withId(R.id.profile_phone_number)).check(matches(withText("")));

        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(1000);

        // Check if Name is correctly pre-filled
        onView(withId(R.id.editTextUserName)).check(matches(withText("Jasleen")));

        // Check if Email is correctly pre-filled
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(withText("jasleen.1@gmail.com")));

        // Edit the phone number so it is no longer blank
        onView(withId(R.id.editTextUserPhone)).check(matches(isDisplayed())).perform(replaceText("7801234567"), closeSoftKeyboard());

        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(1000);

        // Check if Name displays "Jasleen"
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));

        // Check if Email displays "jasleen.1@gmail.com"
        onView(withId(R.id.profile_email)).check(matches(withText("jasleen.1@gmail.com")));

        // Check if Phone Number is now filled with the correct number
        onView(withId(R.id.profile_phone_number)).check(matches(withText("7801234567")));
    }

    @Test
    public void profileNameExceptionTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Leave name and email empty and click on the Confirm Changes button
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        // Check if the error message is displayed for the name field after confirm is pressed
        Thread.sleep(1000);
        onView(withId(R.id.editTextUserName)).check(matches(hasErrorText("Name cannot be empty")));

    }

    @Test
    public void profileEmailExceptionTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Enter name
        onView(withId(R.id.editTextUserName)).check(matches(isDisplayed())).perform(replaceText("Jasleen"), closeSoftKeyboard());

        // Leave email empty
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(isDisplayed())).perform(replaceText(""), closeSoftKeyboard());

        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        // Check if the error message is displayed for the email field after confirm is pressed
        Thread.sleep(1000);
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(hasErrorText("Email cannot be empty")));

    }

    @Test
    public void backArrowTest() {

        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());

        // Check if "Edit Profile" button exists bc that indicates we're on the Profile page
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Check if "Upload" and "Confirm Changes" buttons exist bc that indicates we're on the Edit Profile page
        // And check if "Edit Profile" button does not exist
        onView(withId(R.id.uploadPFP)).check(matches(withText("UPLOAD"))).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_button)).check(matches(withText("CONFIRM CHANGES"))).check(matches(isDisplayed()));
        onView(withId(R.id.edit_profile_button)).check(doesNotExist());

        // Check if the back arrow exists and press it to go back to Profile page
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed())).perform(click());

        // Check if "Edit Profile" button exists so we know we're back
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));
    }

    @Test
    public void phoneNumberOptionalTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());


        onView(withId(R.id.editTextUserName)).check(matches(isDisplayed())).perform(replaceText("Jasleen"), closeSoftKeyboard());
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(isDisplayed())).perform(replaceText("jasleen.h@gmail.com"), closeSoftKeyboard());

        // Leave phone number empty
        onView(withId(R.id.editTextUserPhone)).check(matches(isDisplayed())).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(1000);

        // Check if the "Edit Profile" button exists so we know we're back on the Profile page
        // This indicates that no error was thrown for leaving phone number empty meaning it is optional
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));

    }

}
