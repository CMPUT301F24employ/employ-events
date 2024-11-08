package com.example.employ_events;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.employ_events.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class profileTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void profileTest() {
    ViewInteraction appCompatImageButton = onView(
allOf(withContentDescription("Open navigation drawer"),
childAtPosition(
allOf(withId(R.id.toolbar),
childAtPosition(
withClassName(is("com.google.android.material.appbar.AppBarLayout")),
0)),
1),
isDisplayed()));
    appCompatImageButton.perform(click());

    ViewInteraction navigationMenuItemView = onView(
allOf(withId(R.id.nav_profile),
childAtPosition(
allOf(withId(com.google.android.material.R.id.design_navigation_view),
childAtPosition(
withId(R.id.nav_view),
0)),
3),
isDisplayed()));
    navigationMenuItemView.perform(click());

    ViewInteraction materialButton = onView(
allOf(withId(R.id.edit_profile_button), withText("Edit Profile"),
childAtPosition(
childAtPosition(
withId(R.id.nav_host_fragment_content_main),
0),
1),
isDisplayed()));
    materialButton.perform(click());

    ViewInteraction appCompatEditText = onView(
allOf(withId(R.id.editTextUserName),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.LinearLayout")),
0),
1),
isDisplayed()));
    appCompatEditText.perform(replaceText("Saharaa"), closeSoftKeyboard());

    ViewInteraction editText = onView(
allOf(withId(R.id.editTextUserName), withText("Saharaa"),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
isDisplayed()));
    editText.check(matches(withText("Saharaa")));

    ViewInteraction appCompatEditText2 = onView(
allOf(withId(R.id.editTextUserName), withText("Saharaa"),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.LinearLayout")),
0),
1),
isDisplayed()));
    appCompatEditText2.perform(pressImeActionButton());

    ViewInteraction appCompatEditText3 = onView(
allOf(withId(R.id.editTextUserEmailAddress),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.LinearLayout")),
1),
1),
isDisplayed()));
    appCompatEditText3.perform(replaceText("sahara@ualberta.ca"), closeSoftKeyboard());

    ViewInteraction editText2 = onView(
allOf(withId(R.id.editTextUserEmailAddress), withText("sahara@ualberta.ca"),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
isDisplayed()));
    editText2.check(matches(withText("sahara@ualberta.ca")));

    ViewInteraction appCompatEditText4 = onView(
allOf(withId(R.id.editTextUserEmailAddress), withText("sahara@ualberta.ca"),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.LinearLayout")),
1),
1),
isDisplayed()));
    appCompatEditText4.perform(pressImeActionButton());

    ViewInteraction appCompatEditText5 = onView(
allOf(withId(R.id.editTextUserPhone),
childAtPosition(
childAtPosition(
withClassName(is("android.widget.LinearLayout")),
2),
1),
isDisplayed()));
    appCompatEditText5.perform(replaceText("1234567"), closeSoftKeyboard());

    ViewInteraction editText3 = onView(
allOf(withId(R.id.editTextUserPhone), withText("1234567"),
withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
isDisplayed()));
    editText3.check(matches(withText("1234567")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
}
