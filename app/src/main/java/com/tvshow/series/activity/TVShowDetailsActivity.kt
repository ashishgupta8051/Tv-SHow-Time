package com.tvshow.series.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import com.tvshow.series.R
import com.tvshow.series.adapter.ImageSliderAdapter
import com.tvshow.series.connection.CheckInternetConnection
import com.tvshow.series.model.TVShow
import com.tvshow.series.response.TVShowDetailsResponse
import com.tvshow.series.viewmodel.TVShowDetailsViewModel
import com.tvshow.series.viewmodel.WatchListViewModel
import java.lang.Exception
import java.util.*

class TVShowDetailsActivity : AppCompatActivity() {
    private lateinit var tvShow:TVShow
    private lateinit var tvShowDetailsViewModel:TVShowDetailsViewModel
    private lateinit var watchListViewModel:WatchListViewModel
    private lateinit var tvShowDetailsProgressBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var linearLayout: LinearLayout
    private lateinit var imageTvShow:ImageView
    private lateinit var addFevShow:ImageView
    private lateinit var tvShowName: TextView
    private lateinit var tvShowNetwork: TextView
    private lateinit var tvShowStarted: TextView
    private lateinit var tvShowStatus: TextView
    private lateinit var tvShowDescription: TextView
    private lateinit var tvShowDescriptionMore: TextView
    private lateinit var tvShowGenres: TextView
    private lateinit var viewDivider1: View
    private lateinit var viewDivider2: View
    private lateinit var linearLayout2: LinearLayout
    private lateinit var tvShowRating: TextView
    private lateinit var tvShowRuntime: TextView
    private lateinit var downloadBtn: Button
    private lateinit var episodeBtn: Button
    private var check:Boolean = false
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tvshow_details)

        supportActionBar!!.title = "Show Details"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);

        setUpUI()

        //Get details about tv series
        getDetailsOfMovie()
    }


    override fun onResume() {
        super.onResume()
        var intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpUI() {
        //Get data from intent
        val intent = intent
        val extras = intent.extras

        if (extras != null){
            tvShow = extras.getSerializable("tvShow") as TVShow
        }else{
            Log.i("Error Message","Value is empty")
        }

        //Initialize TvShowDetailsViewModel
        tvShowDetailsViewModel = ViewModelProvider(this).get(TVShowDetailsViewModel::class.java)
        watchListViewModel = ViewModelProvider(this).get(WatchListViewModel::class.java)

        broadcastReceiver = CheckInternetConnection()

        //View Binding
        tvShowDetailsProgressBar = findViewById(R.id.tvShowDetailsLoader)
        viewPager = findViewById(R.id.viewPager)
        linearLayout = findViewById(R.id.indicatorLayoutManager)
        imageTvShow = findViewById(R.id.tvShowImage)
        tvShowName = findViewById(R.id.tvShowName)
        tvShowNetwork = findViewById(R.id.tvShowNetwork)
        tvShowStarted = findViewById(R.id.tvShowStarted)
        tvShowStatus = findViewById(R.id.tvShowStatus)
        tvShowDescription = findViewById(R.id.tvShowDescription)
        tvShowDescriptionMore = findViewById(R.id.tvShowDescriptionMore)
        tvShowGenres = findViewById(R.id.tvShowGenres)
        viewDivider1 = findViewById(R.id.viewDivider1)
        viewDivider2 = findViewById(R.id.viewDivider2)
        linearLayout2 = findViewById(R.id.linearLayout)
        tvShowRating = findViewById(R.id.tvShowRating)
        tvShowRuntime = findViewById(R.id.tvShowRuntime)
        downloadBtn = findViewById(R.id.button1)
        episodeBtn = findViewById(R.id.button2)
        addFevShow = findViewById(R.id.addFevShow)

        imageSliderAdapter = ImageSliderAdapter()
        viewPager.adapter = imageSliderAdapter
    }

    private fun getDetailsOfMovie() {
        tvShowDetailsViewModel.getTvShowDetails(tvShow.id.toString()).observe(this) { response ->
            tvShowDetailsProgressBar.visibility = View.GONE
            loadViewPagerImages(response.tvShow.pictures)
            loadBasicDetails(response)
            checkWatchListById()
        }
    }

    private fun checkWatchListById() {
        watchListViewModel.getWatchListById(tvShow.id.toString()).observe(this){
            for (item in it){
                if (item.id == tvShow.id){
                    check = true
                    addFevShow.setImageResource(R.drawable.ic_round_check_24)
                }else{
                    check = false
                    addFevShow.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadBasicDetails(response: TVShowDetailsResponse?) {
        tvShowDescriptionMore.visibility = View.VISIBLE
        viewDivider1.visibility = View.VISIBLE
        viewDivider2.visibility = View.VISIBLE
        linearLayout2.visibility = View.VISIBLE
        downloadBtn.visibility = View.VISIBLE
        episodeBtn.visibility = View.VISIBLE

        Picasso.get().load(response!!.tvShow.image_thumbnail_path).into(imageTvShow)
        tvShowName.text = response.tvShow.name
        tvShowNetwork.text = response.tvShow.network + " ("+response.tvShow.country+")"
        tvShowStarted.text = "Started on : "+response.tvShow.start_date
        tvShowStatus.text = "Status : "+response.tvShow.status
        tvShowDescription.text = HtmlCompat.fromHtml(response.tvShow.description,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        tvShowDescriptionMore.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(response.tvShow.url)
                startActivity(intent)
            }catch (e:Exception){
                Toast.makeText(this,"Some thing is wrong.",Toast.LENGTH_SHORT).show()
            }
        }

        downloadBtn.setOnClickListener {
            val intent = Intent(this,TVShowDownloadActivity::class.java)
            intent.putExtra("name",tvShow.name)
            startActivity(intent)
        }
        var listString = ""
        for (item in response.tvShow.genres) listString += "$item , "
        tvShowGenres.text = listString

        tvShowRuntime.text = response.tvShow.runtime.toString()+" Min"
        tvShowRating.text = String.format(Locale.getDefault(),"%.2f",response.tvShow.rating.toDouble())

        episodeBtn.setOnClickListener {
            val intent = Intent(this,EpisodeDetails::class.java)
            intent.putExtra("id",tvShow.id.toString())
            intent.putExtra("image",response.tvShow.image_thumbnail_path)
            startActivity(intent)
        }

        addFevShow.setOnClickListener {
            check = if (check){
                addFevShow.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                watchListViewModel.removeFromWatchList(tvShow)
                Toast.makeText(this,"Remove From WatchList",Toast.LENGTH_SHORT).show()
                false
            }else{
                addFevShow.setImageResource(R.drawable.ic_round_check_24)
                watchListViewModel.addToWatchList(tvShow)
                Toast.makeText(this,"Added to WatchList",Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun loadViewPagerImages(pictures: List<String>) {
        viewPager.offscreenPageLimit = 1
        imageSliderAdapter.getPictures(pictures)
        setupSliderIndicator(pictures.size)
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentSliderIndicator(position)
            }
        })
    }

    private fun setupSliderIndicator(size: Int) {
        val indicator = arrayOfNulls<ImageView>(size)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(8, 0, 8, 0)
        for (i in indicator.indices) {
            indicator[i] = ImageView(applicationContext)
            indicator[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,R.drawable.image_selector_inactive
                )
            )
            indicator[i]!!.layoutParams = params
            linearLayout.addView(indicator[i])
        }
        setCurrentSliderIndicator(0)
    }

    fun setCurrentSliderIndicator(position :Int){
        val count = linearLayout.childCount
        for (item in 0 until count){
            val imageView:ImageView = linearLayout.getChildAt(item) as ImageView
            if (item == position){
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.image_selector_active))
            }else{
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.image_selector_inactive))
            }
        }
    }
}