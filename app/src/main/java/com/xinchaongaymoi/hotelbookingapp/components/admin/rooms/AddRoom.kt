package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.Util
import com.xinchaongaymoi.hotelbookingapp.adapter.ExtraImagesAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAddRoomBinding
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAdminRoomsBinding
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.CloudinaryService
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import kotlin.math.max
import android.util.Log


class AddRoom : Fragment() {
    private var _binding: FragmentAddRoomBinding? = null
    private val binding get() = _binding!!
    private val roomService =RoomService()
    private val cloudinaryService = CloudinaryService()
    private var selectedMainImageUri: Uri? = null
    private  val REQUEST_MAIN_IMAGE = 123
    private val REQUEST_EXTRA_IMAGES =124
    private lateinit var extraImagesAdapter: ExtraImagesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExtraImagesRecyclerView()
        setupClickListeners()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupExtraImagesRecyclerView(){
        extraImagesAdapter = ExtraImagesAdapter{
                position->extraImagesAdapter.removeImage(position)
        }
        binding.extraImagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = extraImagesAdapter
        }
    }
    private fun setupClickListeners(){
        binding.btnSelectMainImage.setOnClickListener{
           openImagePicker(REQUEST_MAIN_IMAGE)
        }
        binding.btnAddExtraImages.setOnClickListener{
            openImagePicker(REQUEST_EXTRA_IMAGES)
        }
        binding.btnSaveRoom.setOnClickListener{
            saveRoom()
        }
    }
    private fun openImagePicker(requestCode:Int){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type= "image/*"
        startActivityForResult(intent,requestCode)
    }
    private fun saveRoom()
    {
        try{
            val roomName = binding.edtRoomName.text.toString()
            val roomType = binding.edtRoomType.text.toString()
            val area = binding.edtArea.text.toString().toDoubleOrNull() ?: 0.0
            val bedType = binding.edtBedType.text.toString()
            val totalBed = binding.edtTotalBed.text.toString().toIntOrNull() ?: 0
            val maxGuests = binding.edtMaxGuests.text.toString().toIntOrNull() ?: 0
            val pricePerNight = binding.edtPricePerNight.text.toString().toDoubleOrNull() ?: 0.0
            val utilities = binding.edtUtilities.text.toString()
            val sale = binding.edtSale.text.toString().toDoubleOrNull() ?: 0.0
            if (roomName.isEmpty() || roomType.isEmpty()) {
                Toast.makeText(context, "Please fill in all information", Toast.LENGTH_SHORT).show()
                return
            }
            if (selectedMainImageUri == null) {
                Toast.makeText(context, "Please choose main image", Toast.LENGTH_SHORT).show()
                return
            }
            selectedMainImageUri?.let { mainUri->
                cloudinaryService.uploadImage(requireContext(),mainUri){
                    mainImageUri->
                    if(mainImageUri!=null)
                    {
                        uploadExtraImages {
                            extraImageUrls->
                            val newRoom = Room(
                                roomName = roomName,
                                mainImage = mainImageUri,
                                images = extraImageUrls,
                                roomType = roomType,
                                area = area,
                                bedType = bedType,
                                totalBed = totalBed,
                                maxGuests = maxGuests,
                                pricePerNight = pricePerNight,
                                utilities = utilities,
                                sale = sale
                            )
                            roomService.addRoom(newRoom){
                                success->
                                if(success){
                                    activity?.runOnUiThread {
                                        Toast.makeText(context, "Thêm phòng thành công", Toast.LENGTH_SHORT).show()
                                        findNavController().navigateUp()
                                    }
                                }
                                else{
                                    activity?.runOnUiThread {
                                        Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                    }
                    else{
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Không thể tải ảnh lên", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        catch (e: Exception)
        {
            activity?.runOnUiThread {
                Toast.makeText(context, "Có lỗi xảy ra: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uploadExtraImages(callback:(List<String>)->Unit){
        val extraImages = extraImagesAdapter.getAllImages()
        if (extraImages.isEmpty()) {
            callback(emptyList())
            return
        }
        val uploadedUrls = mutableListOf<String>()
        var uploadCount = 0
        extraImages.forEach { uri ->
            cloudinaryService.uploadImage(requireContext(), uri) { imageUrl ->
                imageUrl?.let { uploadedUrls.add(it) }
                uploadCount++
                if (uploadCount == extraImages.size) {
                    callback(uploadedUrls)
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_MAIN_IMAGE -> {
                    selectedMainImageUri = data?.data
                    binding.roomImageView.setImageURI(selectedMainImageUri)
                }
                REQUEST_EXTRA_IMAGES -> {
                    data?.data?.let { uri ->
                        extraImagesAdapter.addImage(uri)
                    }
                }
            }
        }
    }
}