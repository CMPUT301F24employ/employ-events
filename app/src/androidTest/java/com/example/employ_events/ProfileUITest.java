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

import static org.hamcrest.Matchers.isEmptyOrNullString;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/*
    Unable to test for upload and remove image and deterministically generated from profile name.
    Not sure how to test if drawable changed from "@drawable/white_person" to whatever the user uploaded
    or if it was determined by their initial or something.
    I am able to test if the pfp exists, but not if it changes.

    The following are the corresponding user stories to these issues:
    US 01.03.01	As an entrant I want to upload a profile picture for a more personalized experience
    US 01.03.02	As an entrant I want remove profile picture if need be
    US 01.03.03	As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)

// Running the tests in alphabetical order because the "identifiedByDeviceTest" fails if we do not check for it before provide and update info tests
// Passes if run individually
// As for all the other tests, order does not matter
// CITATION: https://junit.org/junit4/javadoc/4.12/org/junit/FixMethodOrder.html
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // This tests if we are able to go from Home to Profile
    @Test
    public void A_navigateToProfileTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());

        // Check if on this page a user profile exists
        onView(withId(R.id.userPFP)).check(matches(isDisplayed()));

        // Check if the edit button exists
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));

    }

    // US 01.07.01	As an entrant, I want to be identified by my device, so that I don't have to use a username and password
    @Test
    public void B_identifiedByDeviceTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());

        // Check if the profile name and email are empty because that means a UniqueID was created and nothing was auto-filled
        // The user is not prompted to fill in any information or asked to create a username and password
        // Instead they are able to edit their profile at any time they choose
        onView(withId(R.id.profile_name)).check(matches(withText(isEmptyOrNullString())));
        onView(withId(R.id.profile_email)).check(matches(withText(isEmptyOrNullString())));
    }

    // US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
    @Test
    public void C_provideInfoTest() throws InterruptedException {

        // Open side navigation bar
        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());

        // Click on the Profile option in the navigation bar
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());

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


    // This tests if from the Profile page we are able to switch to the Edit Profile page by pressing the button
    @Test
    public void D_editProfileSwitchTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Check if on this page confirm changes and upload buttons exist
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadPFP)).check(matches(isDisplayed()));
    }

    // US 01.02.02	As an entrant I want to update information such as name, email and contact information on my profile
    @Test
    public void E_updateInfoTest() throws InterruptedException {

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

    // This tests if the profile name error pops up when it is left empty as it is a mandatory field
    @Test
    public void F_profileNameExceptionTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        // Leave name empty and click on the Confirm Changes button
        onView(withId(R.id.editTextUserName)).check(matches(isDisplayed())).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(isDisplayed())).perform(replaceText("jasleen.1@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        // Check if the error message is displayed for the name field after confirm is pressed
        Thread.sleep(1000);
        onView(withId(R.id.editTextUserName)).check(matches(hasErrorText("Name cannot be empty")));

    }

    // This tests if the profile email error pops up when it is left empty as it is a mandatory field
    @Test
    public void G_profileEmailExceptionTest() throws InterruptedException {

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

    // This tests the back arrow in the top left corner of the Edit Profile page
    @Test
    public void H_backArrowTest() {

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

    // This tests if phone number is an optional field and no error is thrown
    @Test
    public void I_phoneNumberOptionalTest() throws InterruptedException {

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


    // This tests if the information a user entered stays consistent even if they navigate to other parts of the app
    @Test
    public void J_consistencyTest() throws InterruptedException {

        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.editTextUserName)).check(matches(isDisplayed())).perform(replaceText("Jasleen"), closeSoftKeyboard());
        onView(withId(R.id.editTextUserEmailAddress)).check(matches(isDisplayed())).perform(replaceText("jasleen.h@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.confirm_button)).check(matches(isDisplayed())).perform(click());

        // Checking if what the user entered is correctly displayed
        Thread.sleep(1000);
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_email)).check(matches(withText("jasleen.h@gmail.com")));
        onView(withId(R.id.profile_phone_number)).check(matches(withText("")));

        // Navigating to Home
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_home)).perform(click());

        // Navigating to Registered Events
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_list)).perform(click());

        // Navigating back to Profile
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());

        // Checking if all the details the user entered stayed the same
        onView(withId(R.id.profile_name)).check(matches(withText("Jasleen")));
        onView(withId(R.id.profile_email)).check(matches(withText("jasleen.h@gmail.com")));
    }
}
