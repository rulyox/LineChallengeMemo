package com.rulyox.linechallengememo

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.rulyox.linechallengememo.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditTest {

    @get:Rule
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun editMemo() {

        // choose first item of recycler view
        onView(withId(R.id.main_recycler_memo))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // edit activity
        onView(withId(R.id.read_menu_edit))
            .perform(click())

        // change title
        onView(withId(R.id.write_edit_title))
            .perform(click())
            .perform(replaceText("Edited title"), closeSoftKeyboard())

        // save button
        onView(withId(R.id.write_menu_save))
            .perform(click())

        // go to main activity
        Espresso.pressBack()

        // choose first item of recycler view
        onView(withId(R.id.main_recycler_memo))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // check title
        onView(withId(R.id.read_text_title))
            .check(ViewAssertions.matches(ViewMatchers.withText("Edited title")))

    }

}
