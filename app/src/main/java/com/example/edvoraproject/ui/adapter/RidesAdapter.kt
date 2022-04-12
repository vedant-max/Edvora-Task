package com.example.edvoraproject.ui.adapter

import android.content.Context
import android.os.Build
import android.service.autofill.UserData
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edvoraproject.R
import com.example.edvoraproject.databinding.ItemRiderInfoBinding
import com.example.edvoraproject.models.ride.RideResponse
import com.example.edvoraproject.models.user.User

class RidesAdapter(
    private val ridesData: RideResponse?,
    private val userData: User?,
    private val context: Context
): RecyclerView.Adapter<RidesAdapter.RidesViewHolder>() {

    class RidesViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ItemRiderInfoBinding.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidesViewHolder {
        return RidesViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.item_rider_info,
                    parent,
                    false
                )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RidesViewHolder, position: Int) {
        var value: Int = Int.MAX_VALUE
        for(station in ridesData!![position].station_path){
            value = Math.min(value, Math.abs(userData!!.station_code - station))
        }
        with(holder) {
            binding.city.text = ridesData[position].city
            binding.state.text = ridesData[position].state
            binding.rideId.text = getHtmlText("Ride Id :", ridesData[position].id.toString())
            binding.date.text = getHtmlText("Date :", ridesData[position].date)
            binding.originStation.text = getHtmlText("Origin Station :", ridesData[position].origin_station_code.toString())
            binding.stationPath.text = getHtmlText("station_path :", ridesData[position].station_path.toString())
            binding.distance.text = getHtmlText("Distance :", "${value}")
            Glide.with(this.itemView).load(ridesData[position].map_url).into(binding.riderImage)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getHtmlText(title: String, value: String): Spanned? {
        return Html.fromHtml("<font color='#c4c4c4'>${title} </font> $value", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun getItemCount() = ridesData!!.size

}