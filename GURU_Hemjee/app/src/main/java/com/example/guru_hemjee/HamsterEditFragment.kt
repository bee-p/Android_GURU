package com.example.guru_hemjee

import android.content.Context
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class HamsterEditFragment() : Fragment() {

    //변경 관련 버튼들
    private lateinit var myHNameEditImageButton: ImageButton
    private lateinit var myHamsterApplyImageButton: ImageButton

    //인벤토리 관련 요소들
    private lateinit var myHInventorybgImageView: ImageView
    private lateinit var myHClothImageButton: ImageButton
    private lateinit var myHFurnitureImageButton: ImageButton
    private lateinit var myHWallpaperImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hamster_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //이름 변경 팝업 연결
        myHNameEditImageButton = requireView().findViewById(R.id.myHNameEditImageButton)
        myHNameEditImageButton.setOnClickListener {
            hNameEditPopUp()
        }

        //적용 버튼
        myHamsterApplyImageButton = requireView().findViewById(R.id.myHamsterApplyImageButton)
        myHamsterApplyImageButton.setOnClickListener {
            hChangeImagePopUp()
        }

        //인벤토리 버튼에 따라 인벤토리 변화
        myHClothImageButton = requireView().findViewById(R.id.myHClothImageButton)
        myHFurnitureImageButton = requireView().findViewById(R.id.myHFurnitureImageButton)
        myHWallpaperImageButton = requireView().findViewById(R.id.myHWallPaperImageButton)

        myHInventorybgImageView = requireView().findViewById(R.id.myHInventoryImageView)

        myHClothImageButton.setOnClickListener {
            upDateInventory("cloth")
            myHInventorybgImageView.setImageResource(R.drawable.inventory_cloth)
        }
        myHFurnitureImageButton.setOnClickListener {
            upDateInventory("furniture")
            myHInventorybgImageView.setImageResource(R.drawable.inventory_furniture)
        }
        myHWallpaperImageButton.setOnClickListener {
            upDateInventory("wallpaper")
            myHInventorybgImageView.setImageResource(R.drawable.inventory_wallpapare)
        }
    }

    private fun hNameEditPopUp() {
        val dialog = HamsterEditNameDialog(requireContext())
        dialog.EditName()

        dialog.setOnClickedListener(object : HamsterEditNameDialog.ButtonClickListener{
            override fun onClicked(isChanged: Boolean, name: String?) {
                if(isChanged){
                    //이름 변경한거 db에 반영
                }
            }
        })
    }

    private fun hChangeImagePopUp() {
        val dialog = FinalOK(requireContext(), "적용 완료", "확인", false)
        dialog.alertDialog()

        //햄찌 데코 반영
        dialog.setOnClickedListener(object : FinalOK.ButtonClickListener{
            override fun onClicked(isConfirm: Boolean) {
                //생략
            }
        })
    }

    private fun upDateInventory(name: String) {
        //인벤토리 변환
        when(name){
            "cloth"-> Toast.makeText(requireContext(),"옷이당",Toast.LENGTH_SHORT).show()
            "furniture" -> Toast.makeText(requireContext(),"가구당",Toast.LENGTH_SHORT).show()
            "wallpaper" -> Toast.makeText(requireContext(),"벽지당",Toast.LENGTH_SHORT).show()
        }
    }
}