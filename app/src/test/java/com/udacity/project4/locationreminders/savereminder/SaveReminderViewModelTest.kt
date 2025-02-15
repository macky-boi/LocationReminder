package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var dataSource: FakeDataSource

    private var testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var reminder = ReminderDataItem(
        title = "Doctor's Appointment",
        description = "Visit Dr. Smith for a check-up",
        location = "City Hospital",
        latitude = 37.7749,
        longitude = -122.4194
    )

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()

        stopKoin()
        val application = ApplicationProvider.getApplicationContext<Application>()

        saveReminderViewModel = SaveReminderViewModel(application, dataSource)

        Dispatchers.setMain(testDispatcher)

        saveReminderViewModel.reminderTitle.value = reminder.title
        saveReminderViewModel.reminderDescription.value = reminder.description
        saveReminderViewModel.reminderSelectedLocationStr.value = reminder.location
        saveReminderViewModel.latitude.value = reminder.latitude
        saveReminderViewModel.longitude.value = reminder.longitude
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun onClear_clearLiveDataValues() {
        saveReminderViewModel.onClear()

        assertThat(saveReminderViewModel.reminderTitle.value.isNullOrEmpty(), `is` (true))
        assertThat(saveReminderViewModel.reminderDescription.value.isNullOrEmpty(), `is` (true))
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value.isNullOrEmpty(), `is` (true))
        assertThat(saveReminderViewModel.latitude.value ?: false, `is` (false))
        assertThat(saveReminderViewModel.longitude.value ?: false, `is` (false))
    }

    @Test
    fun validateAndSaveReminder_valid_updatesDataSource() = runTest {
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (true))

        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (false))
        assertThat(dataSource.reminders?.first()?.id, `is` (reminder.id))
    }

    @Test
    fun validateAndSaveReminder_notValid_doesNotUpdatesDataSource() = runTest {
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (true))

        val reminder1 = reminder.copy(title = "")
        saveReminderViewModel.validateAndSaveReminder(reminder1)
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (true))

        val reminder2 = reminder.copy(title = null)
        saveReminderViewModel.validateAndSaveReminder(reminder2)
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (true))

        val reminder3 = reminder.copy(location = "")
        saveReminderViewModel.validateAndSaveReminder(reminder3)
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (true))

        val reminder4 = reminder.copy(location = null)
        saveReminderViewModel.validateAndSaveReminder(reminder4)
        assertThat(dataSource.reminders.isNullOrEmpty(), `is` (true))
    }

    @Test
    fun validateAndSaveReminder() {

    }



}