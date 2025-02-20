package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.ReminderTestData
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.hamcrest.MatcherAssert.assertThat

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
    @Test
    fun addReminderFabClicked_navigateToSaveReminderFragment() = runTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform( click() )

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }


//    TODO: test the displayed data on the UI.
    private val fakeDataSource = FakeDataSource()

    private val testModule = module {
        single { fakeDataSource as ReminderDataSource}
    }

    @Before
    fun setup() = runTest {
//        startKoin {
//            androidContext(ApplicationProvider.getApplicationContext<Application>())
//            modules(listOf(testModule))
//        }
        loadKoinModules(testModule)
    }

    @After
    fun cleanupDb() = runTest {
        fakeDataSource.deleteAllReminders()
        unloadKoinModules(testModule)
    }

    @Test
    fun noReminders_noDataTextViewDisplayed() = runTest {
        assertThat(fakeDataSource.reminders.isNullOrEmpty(), `is`(true))
        launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)
        onView(withId(R.id.noDataTextView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addedReminders_DisplayedInUi() = runTest {
        val reminder1 = ReminderTestData.reminder1
        val reminder2 = ReminderTestData.reminder2

        fakeDataSource.saveReminder(reminder1)
        fakeDataSource.saveReminder(reminder2)

        launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)

        onView(withText(reminder1.title))
            .check(matches(isDisplayed()))
        onView(withText(reminder1.description))
            .check(matches(isDisplayed()))

        onView(withText(reminder2.title))
            .check(matches(isDisplayed()))
        onView(withText(reminder2.description))
            .check(matches(isDisplayed()))
    }
//    TODO: add testing for the error messages.

}