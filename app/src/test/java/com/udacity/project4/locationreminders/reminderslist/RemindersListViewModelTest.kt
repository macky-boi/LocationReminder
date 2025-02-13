package com.udacity.project4.locationreminders.reminderslist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var dataSource: FakeDataSource

    // replaces Dispatchers.Main with a test dispatcher.
    // ensures coroutines run synchronously in tests.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()



}