package com.minerdev.greformanager.fragment

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.minerdev.greformanager.OnSaveDataListener
import com.minerdev.greformanager.customadapter.ImageListAdapter
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = imageListAdapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.imageBtnAdd.setOnClickListener {
            val permissionChecked = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionChecked != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)

            } else {
                if (imageListAdapter.itemCount < 9) {
                    saveData()
                    showAlbum()

                } else {
                    Toast.makeText(context, "사진은 최대 9장까지 업로드가 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedImageUri = data!!.data ?: return
                val path: String = getPathFromUri(requireContext(), selectedImageUri)
                val file = File(path)

                if (!file.exists() || !file.canRead()) {
                    Toast.makeText(context, "사진이 존재 하지않거나 읽을 수 없습니다!", Toast.LENGTH_SHORT).show()

                } else if (file.length() > FILE_MAX_SIZE) {
                    Toast.makeText(context, "사진의 크기는 10MB 보다 작아야 합니다!", Toast.LENGTH_SHORT).show()

                } else {
                    imageListAdapter.addItem(selectedImageUri)
                    imageListAdapter.notifyItemInserted(imageListAdapter.itemCount - 1)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + requireContext().packageName))
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
        viewModel.thumbnail = imageListAdapter.thumbnail
    }

    private fun showAlbum() {
        val intent = Intent()
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1
    }
}