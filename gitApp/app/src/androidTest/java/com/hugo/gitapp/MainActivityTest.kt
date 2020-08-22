package com.hugo.gitapp

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.*
import androidx.test.espresso.action.ViewActions.click
import com.google.gson.Gson
import androidx.test.espresso.contrib.RecyclerViewActions
import com.hugo.gitapp.data.entities.ResponseRepository
import com.hugo.gitapp.presentation.activity.MainActivity
import com.hugo.gitapp.presentation.adapter.RepositoryAdapter
import com.hugo.gitapp.view.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RunWith(AndroidJUnit4ClassRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {

    private lateinit var stringToBetyped: String

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    var responseRepository: ResponseRepository? = null

    @Before
    fun initValidString() {
        stringToBetyped = "GitApp"
        var json: String? = null
        try {
            val `is` = activityRule.activity.assets.open("repositories.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        val gson = Gson()
        responseRepository = gson.fromJson(json, ResponseRepository::class.java)
    }

    @Test
    fun activityLaunches() {
        loadScreen()
    }

    @Test
    fun testSampleRecyclerVisible() {
        loadScreen()
        Espresso.onView(ViewMatchers.withId(R.id.recyclerRepository))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.`is`(activityRule.activity.window.decorView)
                )
            )
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCaseForRecyclerClick() {
        loadScreen()
        Espresso.onView(ViewMatchers.withId(R.id.recyclerRepository))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.`is`(activityRule.activity.window.decorView)
                )
            )
            .perform(RecyclerViewActions.actionOnItemAtPosition<RepositoryAdapter.ValueViewHolder>(0, click()))
    }

    @Test
    fun testCaseForRecyclerScroll() {
        loadScreen()
        val recyclerView = activityRule.activity.findViewById<RecyclerView>(R.id.recyclerRepository)
        val itemCount = recyclerView.adapter!!.itemCount

        Espresso.onView(ViewMatchers.withId(R.id.recyclerRepository))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.`is`(activityRule.activity.window.decorView)
                )
            )
            .perform(RecyclerViewActions.scrollToPosition<RepositoryAdapter.ValueViewHolder>(itemCount - 1))
    }

    private fun loadScreen() {
        val intent = Intent(activityRule.activity, MainActivity::class.java)
        activityRule.launchActivity(intent)
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                MainViewModel.repositories.value = responseRepository
            }
        }
    }

}