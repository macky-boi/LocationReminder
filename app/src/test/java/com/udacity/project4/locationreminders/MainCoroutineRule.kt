package com.udacity.project4.locationreminders

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainCoroutineRule(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()):
    TestWatcher() {

    val testScope = TestScope(dispatcher)

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    // Prevents memory leaks and cross-test interference.
    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}