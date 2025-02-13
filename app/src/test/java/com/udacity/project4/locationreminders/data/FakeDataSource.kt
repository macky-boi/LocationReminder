package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
//        TODO("Return the reminders") (x)
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }

        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            "NO reminders in fake data source"
        )
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
//        TODO("save the reminder") (x)
        reminders?.add(reminder)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
//        TODO("return the reminder with the id") (x)
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }

        reminders?.forEach {
            if (it.id == id) return Result.Success(it)
        }
        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
//        TODO("delete all the reminders") (x)
        reminders?.clear()
    }

    fun addReminders(vararg reminders: ReminderDTO) {
        for (reminder in reminders) {
            runBlocking {
                saveReminder(reminder)
            }
        }
    }


}