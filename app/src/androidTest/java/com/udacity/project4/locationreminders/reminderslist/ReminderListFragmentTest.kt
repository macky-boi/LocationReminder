package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.media3.test.utils.FakeDataSource
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.

//    TODO: test the displayed data on the UI.
    private val fakeDataSource = FakeDataSource()

    @Test
    fun addedReminders_DisplayedInUi() {
        val reminder1 = ReminderDTO(
        title = "Doctor's Appointment",
        description = "Visit Dr. Smith for a check-up",
        location = "City Hospital",
        latitude = 37.7749,
        longitude = -122.4194
        )
        val reminder2 = ReminderDTO(
            title = "Grocery Shopping",
            description = "Buy vegetables and fruits",
            location = "Supermart",
            latitude = 40.7128,
            longitude = -74.0060
        )
        val reminder3 = ReminderDTO(
            title = "Meeting with Client",
            description = "Discuss project details",
            location = "Downtown Cafe",
            latitude = 34.0522,
            longitude = -118.2437
        )

        launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)
        Thread.sleep(2000)

    }
//    TODO: add testing for the error messages.
}