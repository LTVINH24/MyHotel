package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.ImageAdapter
import com.xinchaongaymoi.hotelbookingapp.helper.GridSpacingItemDecoration
import com.xinchaongaymoi.hotelbookingapp.model.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gallery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var galleryView = findViewById<RecyclerView>(R.id.gallery_view)
        val storage = Firebase.storage
        val listRef = storage.reference.child("gallery")
// Setup RecyclerView
        val imageList = mutableListOf<ImageItem>()

//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                val listResult = withContext(Dispatchers.IO) { listRef.listAll().await()
//                }
//                listResult.items.forEach { item ->
//                    val uri = withContext(Dispatchers.IO) { item.downloadUrl.await() }
//                    imageList.add(ImageItem(uri.toString()))
//                }
//                // Gắn adapter sau khi hoàn tất
//                val adapter = ImageAdapter(imageList)
//                Log.d("ImageURL", "Adapter size: ${imageList.size}")
//                galleryView.adapter = adapter
//                galleryView.layoutManager = GridLayoutManager(this@GalleryActivity, 2)
//            } catch (e: Exception) {
//                Log.e("FirebaseError", "Lỗi khi tải: ${e.message}")
//            }
//        }

        listRef.listAll()
            .addOnSuccessListener { listResult ->
                val totalItems = listResult.items.size
                if (totalItems == 0) {
                    Log.d("Firebase", "Thư mục rỗng")
                } else {
                    listResult.items.forEach { item ->
                        item.downloadUrl
                            .addOnSuccessListener { uri ->
                                imageList.add(ImageItem(uri.toString()))
                                Log.d("ImageURL", "URL: $uri")

                                // Cập nhật adapter khi tất cả URL đã được thêm
                                if (imageList.size == totalItems) {
                                    val adapter = ImageAdapter(imageList){ selectedImage ->
                                        val intent = Intent(this, FullScreenImageActivity::class.java)
                                        intent.putParcelableArrayListExtra("images", ArrayList(imageList))
                                        intent.putExtra("position", imageList.indexOf(selectedImage))
                                        startActivity(intent)
                                    }
                                    Log.d("ImageURL", "Adapter size: ${imageList.size}")
                                    galleryView.adapter = adapter
                                    galleryView.layoutManager = GridLayoutManager(this@GalleryActivity, 2)
                                    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing)
                                    galleryView.addItemDecoration(
                                        GridSpacingItemDecoration(2, spacingInPixels, true)
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirebaseError", "Lỗi khi lấy URL: ${exception.message}")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Lỗi khi lấy danh sách tệp: ${exception.message}")
            }

    }
}