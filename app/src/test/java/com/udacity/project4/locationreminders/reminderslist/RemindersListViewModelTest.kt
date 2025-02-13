package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var dataSource: FakeDataSource

    // for: coroutines
    // replaces Dispatchers.Main with a test dispatcher.
    // prevents: Coroutines failing due to missing Dispatchers.Main
    // ensures: coroutines run synchronously in tests.
//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()


    // for: LiveData
    // forces LiveData to execute synchronously and immediately
    // prevents: Delayed LiveData updates
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()
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
        dataSource.addReminders(reminder1, reminder2, reminder3)

        stopKoin()
        val application = ApplicationProvider.getApplicationContext<Application>()

        remindersListViewModel = RemindersListViewModel(application, dataSource)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadReminders_updatesRemindersList()  {
        assertThat(remindersListViewModel.remindersList.value.isNullOrEmpty(), `is` (true))
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.value.isNullOrEmpty(), `is` (false))
        assertThat(remindersListViewModel.remindersList.value?.count(), `is` (3))
    }

    @Test
    fun loadRemindersWhenRemindersAreUnavailable_callErrorToDisplay() {
        assertThat(remindersListViewModel.showSnackBar.value.isNullOrEmpty(), `is` (true))
        dataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.value.isNullOrEmpty(), `is` (false))
    }

    @Test
    fun loadReminders_loading() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.value, `is` (true))
        advanceUntilIdle()
        assertThat(remindersListViewModel.showLoading.value, `is` (false))
    }
}