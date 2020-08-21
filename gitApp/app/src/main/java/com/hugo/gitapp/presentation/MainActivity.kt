package com.hugo.gitapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hugo.gitapp.R
import com.hugo.gitapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by inject()

    private lateinit var repositoryAdapter: RepositoryAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        repositoryAdapter = RepositoryAdapter()

        val monitor = Runnable {
            viewModel.getRepositories()
        }
        handler.postDelayed(monitor, 1000)

        linearLayoutManager = LinearLayoutManager(
            this@MainActivity)
        recyclerRepository.apply {
            layoutManager = linearLayoutManager
            adapter = repositoryAdapter
        }

        recyclerRepository.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                var isNotLoadingAndNotLastPage = !viewModel.mIsLoading && !viewModel.mIsLastPage
                var isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                var isValidFirstItem = firstVisibleItemPosition >= 0
                var totalIsMoreThanVisible = totalItemCount >= viewModel.pageSize
                var shouldLoadMore = isValidFirstItem && isAtLastItem && totalIsMoreThanVisible
                        && isNotLoadingAndNotLastPage

                if (shouldLoadMore) {
                    progressbar.visibility = View.VISIBLE
                    viewModel.loadingMore()
                }
            }
        })

        MainViewModel.repositories.observe(this, Observer {
            repositoryAdapter.updateData(it.items)
            progressbar.visibility = View.GONE
        })

        MainViewModel.newRepositories.observe(this, Observer {
            repositoryAdapter.addData(it.items)
            progressbar.visibility = View.GONE
        })

    }
}