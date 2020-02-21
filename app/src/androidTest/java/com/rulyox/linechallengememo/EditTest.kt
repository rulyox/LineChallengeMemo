package com.rulyox.linechallengememo

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.rulyox.linechallengememo.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class EditTest {

    @Rule
    @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun editMemo() {

        onView(withId(R.id.main_recycler_memo))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.read_menu_edit))
            .perform(click())

        onView(withId(R.id.write_edit_title))
            .perform(click())
            .perform(typeText(" edited"), closeSoftKeyboard())

        onView(withId(R.id.write_menu_save))
            .perform(click())

    }

}
