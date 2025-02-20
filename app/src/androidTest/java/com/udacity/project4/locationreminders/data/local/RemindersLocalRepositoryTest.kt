package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.ReminderTestData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminderAndGetReminderById() = runTest {
        val reminder = ReminderTestData.reminder1
        localRepository.saveReminder(reminder)

        val result = localRepository.getReminder(reminder.id)

        assertThat(result is Result.Success, `is` (true))
        result as Result.Success
        assertThat(result.data.id, `is` (reminder.id))
        assertThat(result.data.title, `is` (reminder.title))
        assertThat(result.data.description, `is` (reminder.description))
        assertThat(result.data.latitude, `is` (reminder.latitude))
        assertThat(result.data.longitude, `is` (reminder.longitude))
        assertThat(result.data.location, `is` (reminder.location))
    }

    @Test
    fun saveRemindersAndGetRemindersAndDeleteAll() = runTest {
        val reminders = ReminderTestData.reminderList
        localRepository.saveReminder(reminders[0])
        localRepository.saveReminder(reminders[1])
        localRepository.saveReminder(reminders[2])

        var result = localRepository.getReminders()

        assertThat(result is Result.Success, `is` (true))
        result as Result.Success
        assertThat(result.data.count(), `is` (3))

        localRepository.deleteAllReminders()
        result = localRepository.getReminders()
        assertThat(result is Result.Success, `is` (true))
        result as Result.Success
        assertThat(result.data.count(), `is` (0))
    }
}