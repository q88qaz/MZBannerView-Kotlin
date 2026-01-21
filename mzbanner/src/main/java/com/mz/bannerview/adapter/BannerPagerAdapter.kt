package com.mz.bannerview.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Banner适配器
 */
class BannerPagerAdapter<T>(
    private val data: List<T>,
    private val layoutInflater: (position: Int) -> View,
    private val onPageClick: (position: Int) -> Unit
) : RecyclerView.Adapter<BannerPagerAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val position = if (data.isNotEmpty()) 0 else 0
        val view = layoutInflater(position)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualPosition = if (data.isNotEmpty()) position % data.size else 0
        
        // 移除之前的点击监听器（如果有的话）
        holder.view.setOnClickListener(null)
        
        // 添加新的点击监听器
        holder.view.setOnClickListener {
            onPageClick(actualPosition)
        }
        
        // 重新绑定视图内容
        val view = layoutInflater(actualPosition)
        if (holder.view !== view) {
            // 如果视图不同，替换内容
            val parent = holder.view.parent as? ViewGroup
            val index = parent?.indexOfChild(holder.view) ?: -1
            parent?.removeView(holder.view)
            parent?.addView(view, index)
        }
    }

    override fun getItemCount(): Int {
        return if (data.isEmpty()) 0 else if (data.size <= 1) data.size else data.size * 100 // 为了实现无限循环
    }
}