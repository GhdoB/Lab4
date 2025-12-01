package com.example.lab4;

import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddNoteActivityTest {

    @Rule
    public ActivityTestRule<AddNoteActivity> activityRule =
            new ActivityTestRule<>(AddNoteActivity.class, true, false);

    @Test
    public void testSaveButtonWithValidData() {
        // Start activity
        activityRule.launchActivity(new Intent());

        // 1. Enter note name
        onView(withId(R.id.etNoteName))
                .perform(typeText("Test Note Title"), closeSoftKeyboard());

        // 2. Enter note content
        onView(withId(R.id.etNoteContent))
                .perform(typeText("Test Note Content"), closeSoftKeyboard());

        // 3. Click save button
        onView(withId(R.id.btnSave)).perform(click());

        // 4. Verify storage selection dialog appears
        onView(withText("Select Storage Method"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }
}