package com.minerdev.greformanager.view.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.minerdev.greformanager.custom.adapter.ImageListAdapter
import com.minerdev.greformanager.databinding.FragmentImageBinding
import com.minerdev.greformanager.utils.AppHelper.getPathFromUri
import com.minerdev.greformanager.utils.Constants.FILE_MAX_SIZE
import com.minerdev.greformanager.viewmodel.SharedViewModel
import java.io.File

class ImageFragment : Fragment(), OnSaveDataListener {
    private val imageListAdapter = ImageListAdapter()
    private val binding by lazy { FragmentImageBinding.inflate(layoutInflater) }
    private val viewModel: SharedViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) = SharedViewModel() as T
        }
    }

    private lateinit var getAlbum: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = imageListAdapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.imageBtnAdd.setOnClickListener {
            val permissionChecked = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (permissionChecked != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )

            } else {
                if (imageListAdapter.itemCount < 9) {
                    saveData()
                    showAlbum()

                } else {
                    Toast.makeText(context, "사진은 최대 9장까지 업로드가 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        getAlbum = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    val path: String = getPathFromUri(requireContext(), uri)
                    val file = File(path)

                    if (!file.exists() || !file.canRead()) {
                        Toast.makeText(context, "사진이 존재 하지않거나 읽을 수 없습니다!", Toast.LENGTH_SHORT)
                            .show()

                    } else if (file.length() > FILE_MAX_SIZE) {
                        Toast.makeText(context, "사진의 크기는 10MB 보다 작아야 합니다!", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        imageListAdapter.addItem(uri)
                        imageListAdapter.notifyItemInserted(imageListAdapter.itemCount - 1)
                    }
                }

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveData()
                    showAlbum()

                } else {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("앱 권한")
                    alertDialog.setMessage("해당 앱의 원활한 기능을 이용하시려면 애플리케이션 정보>권한에서 '저장공간' 권한을 허용해 주십시오.")
                    alertDialog.setPositiveButton("권한설정") { dialog: DialogInterface, _: Int ->
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + requireContext().packageName))
                        startActivity(intent)
                        dialog.cancel()
                    }
                    alertDialog.setNegativeButton("취소") { dialog: DialogInterface, _: Int -> dialog.cancel() }
                    alertDialog.show()
                }

            else -> {
            }
        }
    }

    private fun initData() {
        imageListAdapter.images = viewModel.images
        imageListAdapter.deletedImages = viewModel.deletedImages
        imageListAdapter.thumbnail = viewModel.thumbnail
        imageListAdapter.notifyDataSetChanged()
    }

    override fun checkData(): Boolean {
        return imageListAdapter.itemCount > 0
    }

    override fun saveData() {
        for ((index, image) in viewModel.images.withIndex()) {
            image.position = index.toByte()
            image.thumbnail = if (image.position == imageListAdapter.thumbnail.toByte()) 1 else 0
        }

        viewModel.thumbnail = imageListAdapter.thumbnail
    }

    private fun showAlbum() {
        val intent = Intent()
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        intent.action = Intent.ACTION_PICK
        getAlbum.launch(intent)
    }
}