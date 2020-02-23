package com.rulyox.linechallengememo

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.intent.IntentMonitorRegistry
import com.rulyox.linechallengememo.activity.AbstractWriteActivity.Companion.writeCountingIdlingResource
import com.rulyox.linechallengememo.activity.MainActivity
import org.hamcrest.CoreMatchers.endsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.OutputStream

@RunWith(AndroidJUnit4::class)
class CreateTest {

    @get:Rule
    val mainActivityTestRule = IntentsTestRule(MainActivity::class.java)

    @Before
    fun galleryHandler() {

        val galleryIntentStub = {

            val imgBmp = BitmapFactory.decodeResource(mainActivityTestRule.activity.resources, R.drawable.ic_gallery)
            val imgPath = MediaStore.Images.Media.insertImage(mainActivityTestRule.activity.contentResolver, imgBmp, null, null)
            val imgUri = Uri.parse(imgPath.toString())

            val resultData = Intent()
            resultData.data = imgUri

            ActivityResult(Activity.RESULT_OK, resultData)

        }

        // gallery intent needs result data as intent
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(galleryIntentStub())

    }

    @Before
    fun cameraHandler() {

        IntentMonitorRegistry.getInstance().addIntentCallback{ intent ->

            val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

            if (intent.action == MediaStore.ACTION_IMAGE_CAPTURE) {

                val imgUri: Uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT)!!
                val outputStream: OutputStream = context.contentResolver.openOutputStream(imgUri)!!

                val imgBmp: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_camera)
                imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                outputStream.flush()
                outputStream.close()

            }

        }

        // camera intent doesn't need result data, needs to save image to MediaStore.EXTRA_OUTPUT
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(ActivityResult(Activity.RESULT_OK, null))

    }

    @Before
    fun registerIdlingResource() {

        // getting image from url uses coroutine
        IdlingRegistry.getInstance().register(writeCountingIdlingResource)

    }

    @Test
    fun createMemo() {

        // add activity
        onView(withId(R.id.main_button_add))
            .perform(click())

        // add text
        onView(withId(R.id.write_edit_title))
            .perform(replaceText("Test title"), closeSoftKeyboard())

        onView(withId(R.id.write_edit_text))
            .perform(click())
            .perform(replaceText("This is text for test."), closeSoftKeyboard())

        // add images
        onView(withId(R.id.write_menu_gallery))
            .perform(click())

        onView(withId(R.id.write_menu_camera))
            .perform(click())

        onView(withId(R.id.write_menu_url))
            .perform(click())

        onView(withClassName(endsWith("EditText")))
            .perform(replaceText("https://developer.android.com/images/brand/Android_Robot.png"))

        onView(withId(android.R.id.button1)) // dialog positive button
            .perform(click())

        // save button
        onView(withId(R.id.write_menu_save))
            .perform(click())

    }

}
