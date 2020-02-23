package com.rulyox.linechallengememo

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.rulyox.linechallengememo.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteTest {

    @get:Rule
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun deleteMemo() {

        // choose first item of recycler view
        onView(withId(R.id.main_recycler_memo))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // delete button
        onView(withId(R.id.read_menu_delete))
            .perform(click())

        onView(withId(android.R.id.button1)) // dialog positive button
            .perform(click())

    }

}
