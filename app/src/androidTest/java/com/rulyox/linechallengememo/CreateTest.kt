package com.rulyox.linechallengememo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
class CreateTest {

    @Rule
    @JvmField
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createMemo() {

        onView(withId(R.id.main_button_add))
            .perform(click())

        onView(withId(R.id.write_edit_title))
            .perform(typeText("Test title"), closeSoftKeyboard())

        onView(withId(R.id.write_edit_text))
            .perform(click())
            .perform(typeText("This is text for test."), closeSoftKeyboard())

        onView(withId(R.id.write_menu_save))
            .perform(click())

    }

}
