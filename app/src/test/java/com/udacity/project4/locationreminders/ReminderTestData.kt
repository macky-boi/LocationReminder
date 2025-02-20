package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.dto.ReminderDTO

object ReminderTestData {
    val reminder1 = ReminderDTO("Doctor's Appointment", "Check-up", "City Hospital", 37.7749, -122.4194)
    val reminder2 = ReminderDTO("Grocery Shopping", "Buy groceries", "Supermart", 40.7128, -74.0060)
    val reminder3 = ReminderDTO("Meeting with Client", "Discuss project details", "Downtown Cafe", 34.0522, -118.2437)


    val reminderList = listOf(reminder1, reminder2, reminder3)
}