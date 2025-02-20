package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.ReminderTestData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    // LiveData synchronization (database uses LiveData)
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun saveReminderAndGetById() = runTest {
        val reminder = ReminderTestData.reminder1

        database.reminderDao().saveReminder(reminder)

        // WHEN - get reminder by id from database
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - loaded data contains expected values
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is` (reminder.id))
        assertThat(loaded.title, `is` (reminder.title))
        assertThat(loaded.description, `is` (reminder.description))
        assertThat(loaded.latitude, `is` (reminder.latitude))
        assertThat(loaded.longitude, `is` (reminder.longitude))
    }

    @Test
    fun saveRemindersAndGetAllAndDeleteAll() = runTest {
        database.reminderDao().saveReminder(ReminderTestData.reminder1)
        database.reminderDao().saveReminder(ReminderTestData.reminder2)
        database.reminderDao().saveReminder(ReminderTestData.reminder3)

        var loaded = database.reminderDao().getReminders()

        assertThat(loaded.count(), `is`(3))

        database.reminderDao().deleteAllReminders()

        loaded = database.reminderDao().getReminders()

        assertThat(loaded.count(), `is`(0))


    }

}