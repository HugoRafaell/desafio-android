package com.hugo.gitapp.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hugo.gitapp.R
import com.hugo.gitapp.data.entities.ResponsePull
import com.hugo.gitapp.data.entities.ResponseRepository
import com.hugo.gitapp.databinding.ActivityDetailsBinding
import com.hugo.gitapp.presentation.adapter.PullAdapter
import com.hugo.gitapp.utils.Constants
import com.hugo.gitapp.view.DetailsViewModel
import kotlinx.android.synthetic.main.activity_details.*
import org.koin.android.ext.android.inject

class DetailsActivity : AppCompatActivity(), PullAdapter.ItemListener {

    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: DetailsViewModel by inject()

    private var pullAdapter: PullAdapter = PullAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val handler = Handler()
    private var progressDialog: AlertDialog? = null

    private var item: ResponseRepository.Itens? = null

    private var state: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (intent.getSerializableExtra(Constants.PREF_ITEM) != null)
            item = intent.getSerializableExtra(Constants.PREF_ITEM) as ResponseRepository.Itens?

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = item?.full_name

        val monitor = Runnable {
            viewModel.getPrs(item?.owner?.login!!, item?.name!!, this, state)
        }
        handler.post(monitor)

        linearLayoutManager = LinearLayoutManager(
            this@DetailsActivity)
        recyclerPull.apply {
            layoutManager = linearLayoutManager
            adapter = pullAdapter
        }

        pullAdapter.setItemListener(this)

        recyclerPull.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) {
                    linearLayout.visibility = View.VISIBLE
                } else if (dy > 0)
                    linearLayout.visibility = View.GONE

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
                    viewModel.loadingMore(item?.owner?.login!!, item?.name!!, state)
                }
            }
        })

        DetailsViewModel.pulls.observe(this, Observer {
            pullAdapter.updateData(it)
            progressDialog?.dismiss()
        })

        DetailsViewModel.newPulls.observe(this, Observer {
            pullAdapter.addData(it)
            progressbar.visibility = View.GONE
        })

        all.setOnClickListener {
            state = "all"
            updateButtons()
            val monitor = Runnable {
                viewModel.getPrs(item?.owner?.login!!, item?.name!!, this, state)
            }
            handler.post(monitor)
        }

        opened.setOnClickListener {
            state = "open"
            updateButtons()
            val monitor = Runnable {
                viewModel.getPrs(item?.owner?.login!!, item?.name!!, this, state)
            }
            handler.post(monitor)
        }

        closed.setOnClickListener {
            state = "closed"
            updateButtons()
            val monitor = Runnable {
                viewModel.getPrs(item?.owner?.login!!, item?.name!!, this, state)
            }
            handler.post(monitor)
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun updateButtons() {
        when (state) {
            "closed" -> {
                closed.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.white))
                all.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.black))
                opened.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.black))

                closed.backgroundTintList = ContextCompat
                    .getColorStateList(this, R.color.blueAccent)
                opened.backgroundTintList = ContextCompat
                    .getColorStateList(this, android.R.color.white)
                all.backgroundTintList = ContextCompat
                    .getColorStateList(this, android.R.color.white)
            }
            "open" -> {
                opened.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.white))
                all.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.black))
                closed.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.black))

                opened.backgroundTintList = ContextCompat
                    .getColorStateList(this, R.color.blueAccent)
                all.backgroundTintList = ContextCompat
                    .getColorStateList(this, android.R.color.white)
                closed.backgroundTintList = ContextCompat
                    .getColorStateList(this, android.R.color.white)
            }
            "all" -> {
                all.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.white))
                opened.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.black))
                closed.setTextColor(
                    ContextCompat
                        .getColor(this, android.R.color.black))

                all.backgroundTintList = ContextCompat
                    .getColorStateList(this, R.color.blueAccent)
                opened.backgroundTintList = ContextCompat
                    .getColorStateList(this, android.R.color.white)
                closed.backgroundTintList = ContextCompat
                    .getColorStateList(this, android.R.color.white)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    override fun onItemClick(pull: ResponsePull) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pull.html_url))
        startActivity(browserIntent)
    }

    @SuppressLint("InflateParams")
    fun progressLoading() {
        val alertDialog = AlertDialog.Builder(this)
        val row = layoutInflater.inflate(R.layout.progress_dialog, null)

        alertDialog.setCancelable(false)
        alertDialog.setView(row)

        progressDialog = alertDialog.create()
        progressDialog?.show()
    }
}