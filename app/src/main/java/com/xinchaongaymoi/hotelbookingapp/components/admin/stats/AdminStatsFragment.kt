package com.xinchaongaymoi.hotelbookingapp.components.admin.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.components.AxisBase
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminStatsBinding
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry

class AdminStatsFragment : Fragment() {
    private var _binding: FragmentAdminStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminStatsBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Khởi tạo với entries rỗng để có cấu hình mặc định
        updateChartConfig(binding.checkinLineChart, emptyList())
        updateChartConfig(binding.checkoutLineChart, emptyList())
        
        setupTabLayouts()
        loadUserStats()
        loadRoomStats()
        loadRevenueStats("day")
        loadCheckinStats("day")
        loadCheckoutStats("day")
    }

    private fun setupCharts() {
        // Cài đặt PieChart cho thống kê phòng
        binding.roomPieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            legend.apply {
                isEnabled = true
                orientation = Legend.LegendOrientation.HORIZONTAL
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                setDrawInside(false)
                yOffset = 10f
                textSize = 12f
                form = Legend.LegendForm.SQUARE
                formSize = 12f
                formToTextSpace = 5f
                xEntrySpace = 10f
            }
            holeRadius = 0f
            transparentCircleRadius = 0f
            setDrawCenterText(false)
        }

        // Cài đặt LineChart cho doanh thu
        binding.revenueLineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = true
        }

        // Cài đặt LineChart cho check-in
        binding.checkinLineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = true
            
            // Thêm padding cho biểu đồ
            setExtraOffsets(8f, 16f, 8f, 16f)
            
            // Cấu hình trục X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                labelRotationAngle = 45f
                granularity = 1f
                setDrawGridLines(true)
            }
            
            // Cấu hình trục Y bên trái
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
                setDrawLabels(true)
                granularity = 1f
                axisMinimum = 0f
                axisMaximum = 1f  // Giá trị mặc định là 1
                setLabelCount(2, true)  // Chỉ hiển thị 0 và 1
            }
            
            // Tắt trục Y bên phải
            axisRight.isEnabled = false
            
            // Cấu hình legend
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                yOffset = 15f
            }

            // Cấu hình animation
            animateX(1000)
        }

        // Cài đặt LineChart cho check-out (tương tự)
        binding.checkoutLineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = true
            
            setExtraOffsets(8f, 16f, 8f, 16f)
            
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
                setDrawLabels(true)
                granularity = 1f
            }
            
            axisRight.isEnabled = false
            
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                yOffset = 15f
            }
        }
    }

    private fun setupTabLayouts() {
        // Thêm tabs cho doanh thu
        binding.revenueTabLayout.apply {
            addTab(newTab().setText("Ngày"))
            addTab(newTab().setText("Tuần"))
            addTab(newTab().setText("Tháng"))
            addTab(newTab().setText("Năm"))
            
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        0 -> loadRevenueStats("day")
                        1 -> loadRevenueStats("week")
                        2 -> loadRevenueStats("month")
                        3 -> loadRevenueStats("year")
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        // Thêm tabs cho check-in
        binding.checkinTabLayout.apply {
            addTab(newTab().setText("Ngày"))
            addTab(newTab().setText("Tuần"))
            addTab(newTab().setText("Tháng"))
            addTab(newTab().setText("Năm"))
            
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        0 -> loadCheckinStats("day")
                        1 -> loadCheckinStats("week")
                        2 -> loadCheckinStats("month")
                        3 -> loadCheckinStats("year")
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        // Thêm tabs cho check-out
        binding.checkoutTabLayout.apply {
            addTab(newTab().setText("Ngày"))
            addTab(newTab().setText("Tuần"))
            addTab(newTab().setText("Tháng"))
            addTab(newTab().setText("Năm"))
            
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        0 -> loadCheckoutStats("day")
                        1 -> loadCheckoutStats("week")
                        2 -> loadCheckoutStats("month")
                        3 -> loadCheckoutStats("year")
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun loadUserStats() {
        database.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalUsers = 0
                var adminCount = 0
                var userCount = 0

                for (userSnapshot in snapshot.children) {
                    totalUsers++
                    when (userSnapshot.child("role").getValue(String::class.java)) {
                        "admin" -> adminCount++
                        else -> userCount++  // Mọi role khác đều tính là user
                    }
                }

                binding.tvTotalUsers.text = "Tổng số người dùng: $totalUsers"
                binding.tvUsersByRole.text = """
                    Admin: $adminCount người dùng
                    User: $userCount người dùng
                """.trimIndent()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
                binding.tvTotalUsers.text = "Không thể tải thông tin người dùng"
                binding.tvUsersByRole.text = "Đã xảy ra lỗi: ${error.message}"
            }
        })
    }

    private fun loadRoomStats() {
        database.child("Booking").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lấy ngày hiện tại theo GMT+7
                val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
                val calendar = Calendar.getInstance(vietnamTimeZone)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                    timeZone = vietnamTimeZone
                }
                val today = dateFormat.format(calendar.time)
                
                // Debug log
                println("Checking bookings for date: $today")
                
                var occupiedRoomIds = mutableSetOf<String>() // Lưu roomId của các phòng đã đặt
                
                for (bookingSnapshot in snapshot.children) {
                    val checkInDate = bookingSnapshot.child("checkInDate").getValue(String::class.java)
                    val checkOutDate = bookingSnapshot.child("checkOutDate").getValue(String::class.java)
                    val roomId = bookingSnapshot.child("roomId").getValue(String::class.java)
                    val status = bookingSnapshot.child("status").getValue(String::class.java)
                    
                    if (checkInDate != null && checkOutDate != null && roomId != null && 
                        status != null && status != "cancelled") {
                        try {
                            val checkIn = dateFormat.parse(checkInDate)
                            val checkOut = dateFormat.parse(checkOutDate)
                            val currentDate = dateFormat.parse(today)
                            
                            if (currentDate != null && checkIn != null && checkOut != null) {
                                if (!currentDate.before(checkIn) && !currentDate.after(checkOut)) {
                                    occupiedRoomIds.add(roomId)
                                    println("Room $roomId is occupied: CheckIn=$checkInDate, CheckOut=$checkOutDate")
                                }
                            }
                        } catch (e: Exception) {
                            println("Date parsing error: ${e.message}")
                        }
                    }
                }

                // Lấy tổng số phòng từ node rooms
                database.child("rooms").get().addOnSuccessListener { roomsSnapshot ->
                    val totalRooms = roomsSnapshot.childrenCount.toInt()
                    val occupiedRooms = occupiedRoomIds.size
                    val availableRooms = totalRooms - occupiedRooms
                    
                    println("Total rooms: $totalRooms")
                    println("Occupied rooms: $occupiedRooms")
                    println("Available rooms: $availableRooms")
                    
                    updateRoomPieChart(availableRooms, occupiedRooms)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error loading bookings: ${error.message}")
            }
        })
    }

    private fun updateRoomPieChart(available: Int, occupied: Int) {
        val entries = listOf(
            PieEntry(available.toFloat(), "Phòng trống ($available)"),
            PieEntry(occupied.toFloat(), "Phòng đã đặt ($occupied)")
        )

        val dataSet = PieDataSet(entries, "Trạng thái phòng").apply {
            colors = listOf(Color.rgb(67, 160, 71), Color.rgb(239, 83, 80))
            valueFormatter = PercentFormatter()
        }

        val pieData = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.BLACK)
        }

        binding.roomPieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            invalidate()
        }
    }

    private fun loadRevenueStats(period: String) {
        // TODO: Implement revenue statistics based on period
        // Cần thêm logic để tính toán doanh thu theo ngày/tuần/tháng/năm
    }

    private fun updateChartConfig(chart: LineChart, entries: List<Entry>) {
        val maxValue = entries.maxOfOrNull { it.y } ?: 0f
        val yMax = if (maxValue > 0) maxValue + 0.2f else 1f
        
        chart.apply {
            // Cấu hình trục Y
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = yMax
                setLabelCount((yMax + 1).toInt(), true)
                granularity = 1f
            }
            
            // Cấu hình trục X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                labelRotationAngle = 45f
                granularity = 1f
            }
            
            // Cấu hình chung
            setExtraOffsets(8f, 16f, 8f, 16f)
            description.isEnabled = false
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                yOffset = 15f
            }
        }
    }

    private fun loadCheckinStats(period: String) {
        val entries = ArrayList<Entry>()
        val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = vietnamTimeZone
        }
        
        // Lấy ngày hiện tại theo GMT+7
        val calendar = Calendar.getInstance(vietnamTimeZone)
        val currentDate = calendar.time
        
        // Tính toán khoảng thời gian dựa trên period
        val (startDate, endDate) = when (period) {
            "day" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val start = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 2)
                val end = calendar.time
                Pair(start, end)
            }
            "week" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                val start = calendar.time
                calendar.add(Calendar.WEEK_OF_YEAR, 2)
                val end = calendar.time
                Pair(start, end)
            }
            "month" -> {
                calendar.add(Calendar.MONTH, -1)
                val start = calendar.time
                calendar.add(Calendar.MONTH, 2)
                val end = calendar.time
                Pair(start, end)
            }
            "year" -> {
                calendar.add(Calendar.YEAR, -1)
                val start = calendar.time
                calendar.add(Calendar.YEAR, 2)
                val end = calendar.time
                Pair(start, end)
            }
            else -> Pair(currentDate, currentDate)
        }
        
        database.child("Booking").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val checkinMap = mutableMapOf<String, Int>()
                
                // Nếu là ngày hoặc tuần, tạo tất cả các ngày trong khoảng
                if (period == "day" || period == "week") {
                    val tempCalendar = Calendar.getInstance(vietnamTimeZone)
                    tempCalendar.time = startDate
                    while (!tempCalendar.time.after(endDate)) {
                        checkinMap[dateFormat.format(tempCalendar.time)] = 0
                        tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                
                // Đếm số lượng check-in
                for (bookingSnapshot in snapshot.children) {
                    val checkInDate = bookingSnapshot.child("checkInDate").getValue(String::class.java)
                    val status = bookingSnapshot.child("status").getValue(String::class.java)
                    
                    if (checkInDate != null && status != "cancelled") {
                        try {
                            val date = dateFormat.parse(checkInDate)
                            if (date != null && !date.before(startDate) && !date.after(endDate)) {
                                val dateStr = dateFormat.format(date)
                                checkinMap[dateStr] = checkinMap.getOrDefault(dateStr, 0) + 1
                            }
                        } catch (e: Exception) {
                            println("Date parsing error: ${e.message}")
                        }
                    }
                }
                
                // Sắp xếp theo ngày
                val sortedData = checkinMap.entries.sortedBy { 
                    dateFormat.parse(it.key)?.time ?: 0
                }
                
                // Chuyển đổi dữ liệu thành entries
                sortedData.forEachIndexed { index, entry ->
                    entries.add(Entry(index.toFloat(), entry.value.toFloat()))
                }
                
                // Cập nhật cấu hình trước khi set data
                updateChartConfig(binding.checkinLineChart, entries)
                
                val dataSet = LineDataSet(entries, "Số lượt check-in").apply {
                    color = Color.BLUE
                    setCircleColor(Color.BLUE)
                    lineWidth = 2f
                    circleRadius = 4f
                    valueTextSize = 10f
                    setDrawValues(true)
                    valueFormatter = object : PercentFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return value.toInt().toString()
                        }
                    }
                }
                
                binding.checkinLineChart.apply {
                    data = LineData(dataSet)
                    invalidate()
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                println("Error loading check-in stats: ${error.message}")
            }
        })
    }

    private fun loadCheckoutStats(period: String) {
        val entries = ArrayList<Entry>()
        val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = vietnamTimeZone
        }
        
        // Lấy ngày hiện tại theo GMT+7
        val calendar = Calendar.getInstance(vietnamTimeZone)
        val currentDate = calendar.time
        
        // Tính toán khoảng thời gian dựa trên period
        val (startDate, endDate) = when (period) {
            "day" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val start = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 2)
                val end = calendar.time
                Pair(start, end)
            }
            "week" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                val start = calendar.time
                calendar.add(Calendar.WEEK_OF_YEAR, 2)
                val end = calendar.time
                Pair(start, end)
            }
            "month" -> {
                calendar.add(Calendar.MONTH, -1)
                val start = calendar.time
                calendar.add(Calendar.MONTH, 2)
                val end = calendar.time
                Pair(start, end)
            }
            "year" -> {
                calendar.add(Calendar.YEAR, -1)
                val start = calendar.time
                calendar.add(Calendar.YEAR, 2)
                val end = calendar.time
                Pair(start, end)
            }
            else -> Pair(currentDate, currentDate)
        }
        
        database.child("Booking").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val checkoutMap = mutableMapOf<String, Int>()
                
                // Nếu là ngày hoặc tuần, tạo tất cả các ngày trong khoảng
                if (period == "day" || period == "week") {
                    val tempCalendar = Calendar.getInstance(vietnamTimeZone)
                    tempCalendar.time = startDate
                    while (!tempCalendar.time.after(endDate)) {
                        checkoutMap[dateFormat.format(tempCalendar.time)] = 0
                        tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                
                // Đếm số lượng check-out
                for (bookingSnapshot in snapshot.children) {
                    val checkOutDate = bookingSnapshot.child("checkOutDate").getValue(String::class.java)
                    val status = bookingSnapshot.child("status").getValue(String::class.java)
                    
                    if (checkOutDate != null && status != "cancelled") {
                        try {
                            val date = dateFormat.parse(checkOutDate)
                            if (date != null && !date.before(startDate) && !date.after(endDate)) {
                                val dateStr = dateFormat.format(date)
                                checkoutMap[dateStr] = checkoutMap.getOrDefault(dateStr, 0) + 1
                            }
                        } catch (e: Exception) {
                            println("Date parsing error: ${e.message}")
                        }
                    }
                }
                
                // Sắp xếp theo ngày
                val sortedData = checkoutMap.entries.sortedBy { 
                    dateFormat.parse(it.key)?.time ?: 0
                }
                
                // Chuyển đổi dữ liệu thành entries
                sortedData.forEachIndexed { index, entry ->
                    entries.add(Entry(index.toFloat(), entry.value.toFloat()))
                }
                
                val dataSet = LineDataSet(entries, "Số lượt check-out").apply {
                    color = Color.RED
                    setCircleColor(Color.RED)
                    lineWidth = 2f
                    circleRadius = 4f
                    valueTextSize = 10f
                }
                
                binding.checkoutLineChart.apply {
                    data = LineData(dataSet)
                    invalidate()
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                println("Error loading check-out stats: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 