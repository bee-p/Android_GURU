package com.example.guru_hemjee.Home.Home

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_hemjee.Home.Goal.DetailGoalItem
import com.example.guru_hemjee.R

// 잠금 시간 및 목표 설정/확인을 할 때 보이는 세부 목록 리스트를 생성하는 어댑터
class DetailGoalListAdapter(val context: Context, private val items: ArrayList<DetailGoalListItem>): RecyclerView.Adapter<DetailGoalListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View?): RecyclerView.ViewHolder(view!!){
        val icon = view!!.findViewById<ImageView>(R.id.goalIconImageView)
        val name = view!!.findViewById<TextView>(R.id.detailGoalListNameTextView)

        fun bind(item: DetailGoalListItem, context: Context){
            icon.setImageResource(item.picSource)
            icon.setColorFilter(item.picColor, PorterDuff.Mode.SRC_IN)
            name.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.container_lock_detail_gaol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], context)
    }

    override fun getItemCount() = items.size

}