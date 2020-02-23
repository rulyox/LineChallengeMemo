package com.rulyox.linechallengememo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.rulyox.linechallengememo.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchTest {

    @get:Rule
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun searchMemo() {

        // search activity
        onView(withId(R.id.main_menu_search))
            .perform(click())

        // search query
        onView(withId(R.id.search_edit_query))
            .perform(replaceText("text"), closeSoftKeyboard())

        // search button
        onView(withId(R.id.search_button_search))
            .perform(click())

    }

}
