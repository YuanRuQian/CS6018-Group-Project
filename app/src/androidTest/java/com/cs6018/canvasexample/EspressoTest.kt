package com.cs6018.canvasexample

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule

import org.junit.Rule
import org.junit.Test

class EspressoTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

//    @Test
//    fun testAddButton() {
//        Espresso.onView(ViewMatchers)
//    }

    @Test
    fun testListView() {

    }

//    @Test
//    fun testSplitButton() {
//        Espresso.onView(ViewMatchers.withText("Split Email")).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.usernameView))
//            .check(ViewAssertions.matches(ViewMatchers.withText("try")))
//        Espresso.onView(withId(R.id.emailInput)).perform(ViewActions.clearText())
//            .perform(ViewActions.typeText("new@email.com"))
//        Espresso.onView(ViewMatchers.withText("Split Email")).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.domainView))
//            .check(ViewAssertions.matches(ViewMatchers.withText("email.com")))
//
//    }
}