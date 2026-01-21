package com.mz.bannerview

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.mz.bannerview.adapter.BannerPagerAdapter
import com.mz.bannerview.indicator.IndicatorAlign
import com.mz.bannerview.indicator.IndicatorStyle
import kotlin.math.abs

/**
 * MZBannerView - Kotlin版轮播图控件
 */
class MZBannerView<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewPager: ViewPager2
    private val indicatorContainer: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var autoPlayRunnable: Runnable? = null
    private var adapter: BannerPagerAdapter<T>? = null
    private var dataList: List<T>? = null
    private var onPageClickListener: ((position: Int) -> Unit)? = null
    private var delayedTime = 3000L // 默认3秒
    private var isAutoPlaying = false
    private var isAutoPlayEnabled = true
    private var indicatorStyle = IndicatorStyle.DOT
    private var indicatorAlign = IndicatorAlign.CENTER
    private var selectedIndicatorColor = Color.WHITE
    private var unselectedIndicatorColor = Color.GRAY
    private var currentPage = 0
    private var totalPageCount = 0

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.banner_view_layout, this, true)
        viewPager = view.findViewById(R.id.viewPager)
        indicatorContainer = view.findViewById(R.id.indicatorContainer)

        setupViewPager()
    }

    private fun setupViewPager() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                
                // 计算实际位置（考虑无限循环）
                if (totalPageCount > 0) {
                    val actualPosition = position % totalPageCount
                    updateIndicators(actualPosition)
                }
                
                // 当用户手动滑动时，重置自动播放定时器
                if (isAutoPlaying) {
                    restartAutoPlayTimer()
                }
            }
            
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        // 检查是否到达边界，如果是则跳转到实际对应的位置
                        dataList?.let { data ->
                            if (data.isNotEmpty() && data.size > 1) {
                                val currentPosition = viewPager.currentItem
                                if (currentPosition == 0) {
                                    viewPager.currentItem = data.size
                                    currentPage = data.size
                                } else if (currentPosition == data.size + 1) {
                                    viewPager.currentItem = 1
                                    currentPage = 1
                                }
                            }
                        }
                    }
                }
            }
        })
        
        // 设置页面变换效果
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager.setPageTransformer(compositePageTransformer)
    }

    /**
     * 设置页面
     */
    fun setPages(
        layoutInflater: (position: Int) -> View,
        data: List<T>,
        onPageClick: ((position: Int) -> Unit)? = null
    ) {
        this.dataList = data
        this.onPageClickListener = onPageClick
        this.totalPageCount = data.size
        
        if (data.isEmpty()) {
            viewPager.adapter = null
            indicatorContainer.removeAllViews()
            return
        }
        
        adapter = BannerPagerAdapter(data, layoutInflater) { position ->
            onPageClickListener?.invoke(position)
        }
        
        viewPager.adapter = adapter
        
        // 如果数据大于1个，则设置无限循环
        if (data.size > 1) {
            viewPager.currentItem = 1
            currentPage = 1
        } else {
            viewPager.currentItem = 0
            currentPage = 0
        }
        
        // 初始化指示器
        setupIndicators(data.size)
    }

    /**
     * 设置指示器
     */
    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        
        // 设置指示器容器的对齐方式
        when (indicatorAlign) {
            IndicatorAlign.LEFT -> indicatorContainer.gravity = Gravity.START
            IndicatorAlign.CENTER -> indicatorContainer.gravity = Gravity.CENTER_HORIZONTAL
            IndicatorAlign.RIGHT -> indicatorContainer.gravity = Gravity.END
        }
        
        when (indicatorStyle) {
            IndicatorStyle.DOT -> {
                for (i in 0 until count) {
                    val indicator = View(context).apply {
                        layoutParams = LayoutParams(20, 20).apply {
                            setMargins(8, 0, 8, 0)
                        }
                        setBackgroundResource(R.drawable.indicator_dot_normal)
                        setIndicatorColor(this, false)
                    }
                    indicatorContainer.addView(indicator)
                }
            }
            IndicatorStyle.NUMBER -> {
                val indicatorText = TextView(context).apply {
                    layoutParams = LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    textSize = 14f
                    setTextColor(selectedIndicatorColor)
                }
                indicatorContainer.addView(indicatorText)
            }
            IndicatorStyle.DASH -> {
                // 简单的横线指示器
                for (i in 0 until count) {
                    val indicator = View(context).apply {
                        layoutParams = LayoutParams(30, 4).apply {
                            setMargins(4, 0, 4, 0)
                        }
                        setBackgroundResource(R.drawable.indicator_dash_normal)
                        setIndicatorColor(this, false)
                    }
                    indicatorContainer.addView(indicator)
                }
            }
        }
        
        updateIndicators(0)
    }

    /**
     * 设置指示器颜色
     */
    private fun setIndicatorColor(view: View, isSelected: Boolean) {
        if (isSelected) {
            view.setBackgroundColor(selectedIndicatorColor)
        } else {
            view.setBackgroundColor(unselectedIndicatorColor)
        }
    }

    /**
     * 更新指示器状态
     */
    private fun updateIndicators(currentPosition: Int) {
        when (indicatorStyle) {
            IndicatorStyle.DOT, IndicatorStyle.DASH -> {
                val childCount = indicatorContainer.childCount
                if (childCount == 0) return

                for (i in 0 until childCount) {
                    val child = indicatorContainer.getChildAt(i)
                    setIndicatorColor(child, i == currentPosition)
                }
            }
            IndicatorStyle.NUMBER -> {
                if (indicatorContainer.childCount > 0) {
                    val textView = indicatorContainer.getChildAt(0) as TextView
                    textView.text = "${currentPosition + 1}/${totalPageCount}"
                }
            }
        }
    }

    /**
     * 启用/禁用自动播放
     */
    fun isAutoPlay(enabled: Boolean) {
        isAutoPlayEnabled = enabled
        if (enabled) {
            startAutoPlay()
        } else {
            stopAutoPlay()
        }
    }

    /**
     * 设置延迟时间（毫秒）
     */
    fun setDelayedTime(time: Long) {
        delayedTime = time
    }

    /**
     * 设置指示器样式
     */
    fun setIndicatorStyle(style: IndicatorStyle) {
        this.indicatorStyle = style
    }

    /**
     * 设置指示器对齐方式
     */
    fun setIndicatorAlign(align: IndicatorAlign) {
        this.indicatorAlign = align
    }

    /**
     * 设置指示器颜色
     */
    fun setIndicatorColor(selectedColor: Int, unselectedColor: Int) {
        this.selectedIndicatorColor = selectedColor
        this.unselectedIndicatorColor = unselectedColor
    }

    /**
     * 设置页面变换器
     */
    fun setPageTransformer(transformer: ViewPager2.PageTransformer) {
        viewPager.setPageTransformer(transformer)
    }

    /**
     * 开始自动播放
     */
    fun start() {
        if (isAutoPlayEnabled && dataList != null && dataList!!.size > 1) {
            startAutoPlay()
        }
    }

    /**
     * 停止自动播放
     */
    fun stop() {
        stopAutoPlay()
    }

    private fun startAutoPlay() {
        if (isAutoPlaying || dataList.isNullOrEmpty() || dataList!!.size <= 1) return
        isAutoPlaying = true
        restartAutoPlayTimer()
    }

    private fun stopAutoPlay() {
        isAutoPlaying = false
        autoPlayRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun restartAutoPlayTimer() {
        autoPlayRunnable?.let { handler.removeCallbacks(it) }
        autoPlayRunnable = Runnable {
            val nextItem = viewPager.currentItem + 1
            viewPager.currentItem = nextItem
            
            if (isAutoPlaying) {
                handler.postDelayed(autoPlayRunnable!!, delayedTime)
            }
        }
        handler.postDelayed(autoPlayRunnable!!, delayedTime)
    }

    /**
     * 获取当前页码
     */
    fun getCurrentPage(): Int {
        return if (totalPageCount > 0) currentPage % totalPageCount else 0
    }

    /**
     * 获取总页数
     */
    fun getTotalPage(): Int {
        return totalPageCount
    }

    /**
     * 生命周期方法 - 恢复自动播放
     */
    fun onResume() {
        if (isAutoPlayEnabled) {
            startAutoPlay()
        }
    }

    /**
     * 生命周期方法 - 暂停自动播放
     */
    fun onPause() {
        stopAutoPlay()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoPlay()
    }
}