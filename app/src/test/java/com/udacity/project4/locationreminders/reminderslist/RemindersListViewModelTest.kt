package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.ReminderTestData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import com.udacity.project4.locationreminders.data.FakeDataSource

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects (x)

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var dataSource: FakeDataSource

    // for: coroutines
    // replaces Dispatchers.Main with a test dispatcher.
    // prevents: Coroutines failing due to missing Dispatchers.Main
    // ensures: coroutines run synchronously in tests.
//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()


    // for: LiveData
    // runTest only controls coroutine execution within the test scope.
    // However, LiveData observers run on the Android main thread and rely on the
    // Architecture Components’ background executors for their behavior.
    // It replaces the Android background executor used by LiveData with a synchronous one for the
    // duration of your test.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()

        dataSource.addReminders(
            ReminderTestData.reminder1,
            ReminderTestData.reminder2,
            ReminderTestData.reminder3)

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

    @Test
    fun loadReminders_noData_showNoDataIsTrue() = runTest {
        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showNoData.value, `is` (true))
    }
}