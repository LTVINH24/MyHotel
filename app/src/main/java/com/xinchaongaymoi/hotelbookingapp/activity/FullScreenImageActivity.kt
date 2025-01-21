package com.xinchaongaymoi.hotelbookingapp.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.FullscreenImageAdapter
import com.xinchaongaymoi.hotelbookingapp.model.ImageItem

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        val images = intent.getParcelableArrayListExtra<ImageItem>("images") ?: listOf()
        val position = intent.getIntExtra("position", 0)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = "Image ${position + 1}/${images.size}"
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = FullscreenImageAdapter(images)
        viewPager.adapter = adapter
        viewPager.currentItem = position

        // Lắng nghe sự kiện khi vuốt
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(newPosition: Int) {
                super.onPageSelected(newPosition)
                textView.text = "Image ${newPosition + 1}/${images.size}"
            }
        })

    }
}
