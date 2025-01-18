package com.xinchaongaymoi.hotelbookingapp.components.admin.rooms
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.rotationMatrix
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.xinchaongaymoi.hotelbookingapp.adapter.ExtraImagesAdapter
import com.xinchaongaymoi.hotelbookingapp.databinding.FragmentAddRoomBinding
import com.xinchaongaymoi.hotelbookingapp.model.Room
import com.xinchaongaymoi.hotelbookingapp.service.CloudinaryService
import com.xinchaongaymoi.hotelbookingapp.service.RoomService
import android.util.Log

class EditRoomFragment : Fragment() {
    private var _binding: FragmentAddRoomBinding? = null
    private val binding get() = _binding!!
    private val roomService =RoomService()
    private val cloudinaryService = CloudinaryService()
    private var selectedMainImageUri: Uri? = null
    private var currentRoom: Room? = null
    private var originalMainImage: String? = null
    private  val REQUEST_MAIN_IMAGE = 127
    private val REQUEST_EXTRA_IMAGES =128
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
        val roomId = arguments?.getString("roomId")
        roomId?.let{
            id->
            roomService.getRoomById(id){
                room->room?.let{
                    currentRoom=it
                originalMainImage  = it.mainImage
                activity?.runOnUiThread{
                    loadRoomData()
                }
            }
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadRoomData(){
        currentRoom?.let {
            room->
            binding.apply {
                edtRoomName.setText(room.roomName)
                edtRoomType.setText(room.roomType)
                edtArea.setText(room.area.toString())
                edtBedType.setText(room.bedType)
                edtTotalBed.setText(room.totalBed.toString())
                edtMaxGuests.setText(room.maxGuests.toString())
                edtPricePerNight.setText(room.pricePerNight.toString())
                edtUtilities.setText(room.utilities)
                edtSale.setText(room.sale.toString())
                Glide.with(requireContext())
                    .load(room.mainImage)
                    .into(roomImageView)
                room.images?.forEach{
                    image->extraImagesAdapter.addImageUrl(image)
                }
            }
        }
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
            updateRoom()
        }
    }
    private fun openImagePicker(requestCode:Int){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type= "image/*"
        startActivityForResult(intent,requestCode)
    }
    private fun updateRoom()
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
            val processUpdate = {mainImageUrl:String
            ->uploadExtraImages {
                extraImageUrls->
                val updatedRoom = currentRoom?.copy(
                    roomName = roomName,
                    mainImage = mainImageUrl,
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
                updatedRoom?.let {
                    room->roomService.updateRoom(room){
                        success->
                        activity?.runOnUiThread{
                            if(success)
                            {
                                Toast.makeText(context,"Update successfully",Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                            else{
                                Toast.makeText(context,"Update failed",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                }
            }
            }

            if(selectedMainImageUri !=null){
                cloudinaryService.uploadImage(requireContext(),selectedMainImageUri!!){
                    mainImageUri->
                    if(mainImageUri!=null){
                        processUpdate(mainImageUri)
                    }
                    else{
                        activity?.runOnUiThread{
                            Toast.makeText(context,"Error upload image",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            else{
                originalMainImage?.let{processUpdate(it)}
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
        val existingUrls = extraImagesAdapter.getExistingImageUrls()
        if (extraImages.isEmpty()) {
            callback(existingUrls)
            return
        }
        val uploadedUrls = mutableListOf<String>()
        var uploadCount = 0
        extraImages.forEach { uri ->
            cloudinaryService.uploadImage(requireContext(), uri) { imageUrl ->
                imageUrl?.let { uploadedUrls.add(it) }
                uploadCount++
                if (uploadCount == extraImages.size) {
                    callback(uploadedUrls+existingUrls)
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