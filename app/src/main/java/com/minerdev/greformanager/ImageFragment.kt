package com.minerdev.greformanager

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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.minerdev.greformanager.databinding.FragmentImageBinding
import java.io.File
import java.util.*

// TODO: 앨범으로 이동시 대표사진이 초기화 되는 버그 고치기
class ImageFragment : Fragment(), OnSaveDataListener {
    private val imageListAdapter = ImageListAdapter()
    private val binding by lazy { FragmentImageBinding.inflate(layoutInflater) }
    private val viewModel: HouseModifyViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false) as ViewGroup

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.houseModifyRecyclerView.layoutManager = manager
        binding.houseModifyRecyclerView.adapter = imageListAdapter
        binding.houseModifyRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.houseModifyImageButtonAdd.setOnClickListener {
            val permissionChecked = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionChecked != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)

            } else {
                if (imageListAdapter.itemCount < 9) {
                    showAlbum()

                } else {
                    Toast.makeText(context, "사진은 최대 9장까지 업로드가 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
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
                val path: String = AppHelper.instance.getPathFromUri(requireContext(), selectedImageUri)
                val file = File(path)

                if (!file.exists() || !file.canRead()) {
                    Toast.makeText(context, "사진이 존재 하지않거나 읽을 수 없습니다!", Toast.LENGTH_SHORT).show()

                } else if (file.length() > Constants.instance.FILE_MAX_SIZE) {
                    Toast.makeText(context, "사진의 크기는 10MB 보다 작아야 합니다!", Toast.LENGTH_SHORT).show()

                } else {
                    imageListAdapter.addItem(selectedImageUri)
                    imageListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            101 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlbum()

                } else {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("앱 권한")
                    alertDialog.setMessage("해당 앱의 원활한 기능을 이용하시려면 애플리케이션 정보>권한에서 '저장공간' 권한을 허용해 주십시오.")
                    alertDialog.setPositiveButton("권한설정") { dialog: DialogInterface, which: Int ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + requireContext().packageName))
                        startActivity(intent)
                        dialog.cancel()
                    }
                    alertDialog.setNegativeButton("취소") { dialog: DialogInterface, which: Int -> dialog.cancel() }
                    alertDialog.show()
                }

            else -> {
            }
        }
    }

    override fun checkData(): Boolean {
        return imageListAdapter.itemCount > 0
    }

    override fun saveData() {
        context?.let {
            viewModel.imageUris = imageListAdapter.items
            viewModel.thumbnail = imageListAdapter.thumbnail
            viewModel.saveImages(it)
        }
    }

    private fun initData() {
        imageListAdapter.items = viewModel.imageUris as ArrayList<Uri>
        imageListAdapter.thumbnail = viewModel.thumbnail
        imageListAdapter.notifyDataSetChanged()
    }

    fun showAlbum() {
        val intent = Intent()
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1
    }
}