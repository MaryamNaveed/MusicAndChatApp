package com.ass2.i190426_i190435;

import static androidx.core.os.BundleKt.bundleOf;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.Matchers.not;

import android.view.Gravity;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.core.StringEndsWith;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class Espresso3 {


    @Rule
    public ActivityScenarioRule<TabLayout> rule  = new ActivityScenarioRule<TabLayout>(TabLayout.class);




    @Test
    public void Test1() throws Exception {

        onView(withId(R.id.menu)).perform(click());

        onView(withId(R.id.userName)).check(matches(isDisplayed()));

        onView(withId(R.id.dpImage)).check(matches(isDisplayed()));

    }
}
