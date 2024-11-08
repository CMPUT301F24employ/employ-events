package com.example.employ_events;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;

public class QRCodeUITest {

    /*
    Current issues: Don't want to actually open scanner, but rather pass data into the activity result to be
    interpreted.
     */

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
    @Test
    public void viewEventDetailsTest() throws InterruptedException {

        ActivityScenario.launch(MainActivity.class);

        // Create the mock Intent data that represents a successful scan with "sampleEventId" as the scanned content
        Intents.init();
        Intent resultData = new Intent();
        resultData.putExtra("EVENT_ID", "EWxlDsmBkb2JmcQwxvSK");

        // Create an ActivityResult with the mock data
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasExtra("EVENT_ID", "EWxlDsmBkb2JmcQwxvSK")).respondWith(result);

        // Trigger the QR scan
        onView(withContentDescription("Open navigation drawer")).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.scan_qr_code)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(2000);
        Intents.release();
    }
}