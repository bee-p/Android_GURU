package com.example.guru_hemjee.Home.MyHamsterManage

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.guru_hemjee.*

// 나의 햄찌 관리 페이지
// 햄찌를 관리할 수 있는 Fragment 화면
class HamsterEditFragment() : Fragment() {

    //함께한, 이름 관련
    private lateinit var myHamster_hamsterNameTextView: TextView
    private lateinit var myHamster_totalSpentTimeTextView: TextView
    private lateinit var hamsterName: String

    //변경 관련 버튼들
    private lateinit var myHamster_nameEditImageButton: ImageButton
    private lateinit var myHamster_applyImageButton: ImageButton

    //인벤토리 관련 요소들
    private lateinit var myHamster_inventoryImageView: ImageView
    private lateinit var myHamster_clothImageButton: ImageButton
    private lateinit var myHamster_furnitureImageButton: ImageButton
    private lateinit var myHamster_wallPaperImageButton: ImageButton

    //아이템 리스트 관련
    private lateinit var myHamster_itemList: LinearLayout //아이템 리스트
    private var selectedItems = ArrayList<String>() //선택 중인 아이템 리스트
    private var preselectedItems = ArrayList<String>() //이미 적용되어있던 아이템 리스트
    private var currentInventory = "clo" //현제 인밴토리 위치

    //배경(옷, 배경, 가구) 관련
    private lateinit var myHamster_BGFrameLayout: FrameLayout
    private lateinit var myHamster_clothFrameLayout: FrameLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

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

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //이름, 함께한 시간
        myHamster_hamsterNameTextView = requireView().findViewById(R.id.myHamster_hamsterNameTextView)
        //myHamster_totalSpentTimeTextView = requireView().findViewById(R.id.myHamster_totalSpentTimeTextView)

        //햄찌 이름과 총 함께한 시간 반영
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
        if(cursor.moveToNext()){
            myHamster_hamsterNameTextView.text = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
            myHamster_totalSpentTimeTextView.text = cursor.getString(cursor.getColumnIndex("total_time")).toString()
            hamsterName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        //이름 변경 팝업 연결
        myHamster_nameEditImageButton = requireView().findViewById(R.id.myHamster_nameEditImageButton)
        myHamster_nameEditImageButton.setOnClickListener {
            hNameEditPopUp()
        }

        //인벤토리 버튼에 따라 인벤토리 변화
        myHamster_clothImageButton = requireView().findViewById(R.id.myHamster_clothImageButton)
        myHamster_furnitureImageButton = requireView().findViewById(R.id.myHamster_furnitureImageButton)
        myHamster_wallPaperImageButton = requireView().findViewById(R.id.myHamster_wallPaperImageButton)
        //myHamster_inventoryImageView = requireView().findViewById(R.id.myHamster_inventoryImageView)
        //myHamster_itemList = requireView().findViewById(R.id.myHamster_itemList)

        //인벤토리에 아이템 연결
        upDateInventory(currentInventory)

        //사용 중인 아이템 미리 선택하기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
        /*while(cursor.moveToNext()){
            preselectedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }*/
        cursor.close()
        var preusingItems = ArrayList<String>()
        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
        while(cursor.moveToNext()){
            preusingItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        preusingItems.clear()
        selectedItems.addAll(preselectedItems)

        //인벤토리 위치이동 버튼 연결(옷, 가구, 배경)
        myHamster_clothImageButton.setOnClickListener {
            currentInventory = "clo"
            upDateInventory("clo")
            myHamster_inventoryImageView.setImageResource(R.drawable.inventory_cloth)
        }
        myHamster_furnitureImageButton.setOnClickListener {
            currentInventory = "furni"
            upDateInventory("furni")
            myHamster_inventoryImageView.setImageResource(R.drawable.inventory_furniture)
        }
        myHamster_wallPaperImageButton.setOnClickListener {
            currentInventory = "bg"
            upDateInventory("bg")
            myHamster_inventoryImageView.setImageResource(R.drawable.inventory_wallpapare)
        }

        //배경(옷, 가구, 배경)
        myHamster_BGFrameLayout = requireView().findViewById(R.id.myHamster_BGFrameLayout)
        myHamster_clothFrameLayout = requireView().findViewById(R.id.myHamster_clothFrameLayout)
        FunUpDateHamzzi.updateBackground(
            requireContext(),
            myHamster_BGFrameLayout,
            true,
            false
        )
        FunUpDateHamzzi.updateCloth(
            requireContext(),
            myHamster_clothFrameLayout,
            true,
            false
        )

        //적용 버튼
        //myHamster_applyImageButton = requireView().findViewById(R.id.myHamster_applyImageButton)
        myHamster_applyImageButton.setOnClickListener {
            //사용 중임을 해제할 리스트
            var deSelectItems = ArrayList<String>()

            //기존에 선택 중이던 아이템을 deselectedItems에 대입
            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase
            cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1 OR is_applied = 1",null)
            while(cursor.moveToNext()){
                var itemName = cursor.getString(cursor.getColumnIndex("item_name"))
                if(!selectedItems.contains(itemName))
                    deSelectItems.add(itemName)
            }
            cursor.close()
            sqlitedb.close()

            //사용중임 수정(selected, deselct 아이템 갱신)
            sqlitedb = dbManager.writableDatabase
            for(item in selectedItems){
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_applied = 1 WHERE item_name = '${item}'")
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
            }
            for(item in deSelectItems) {
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_applied = 0 WHERE item_name = '${item}'")
                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
            }
            sqlitedb.close()
            dbManager.close()

            //적용된 아이템 갱신
            preselectedItems.clear()
            preselectedItems.addAll(selectedItems)

            //인벤토리, 아이템 리스트 업데이트
            upDateInventory(currentInventory)
            FunUpDateHamzzi.updateBackground(
                    requireContext(),
                    myHamster_BGFrameLayout,
                    true,
                    false
            )
            FunUpDateHamzzi.updateCloth(
                    requireContext(),
                    myHamster_clothFrameLayout,
                    true,
                    false
            )

            //적용 완료 팝업 연결
            val dialog = FinalOKDialog(requireContext(), "적용 완료", "확인",
                false, R.drawable.popup_applyed, null)
            dialog.alertDialog()

            dialog.setOnClickedListener(object : FinalOKDialog.ButtonClickListener {
                override fun onClicked(isConfirm: Boolean) {
                    //내용 없음
                }
            })
        }
    }

    //이름 변경 팝업
    private fun hNameEditPopUp() {
        val dialog = HamsterEditNameDialog(requireContext(), myHamster_hamsterNameTextView.text.toString())
        dialog.EditName()

        dialog.setOnClickedListener(object : HamsterEditNameDialog.ButtonClickListener {
            override fun onClicked(isChanged: Boolean, name: String?) {
                if(isChanged){
                    //이름 변경 db와 ui에 반영
                    dbManager = DBManager(requireContext(), "hamster_db", null, 1)
                    sqlitedb = dbManager.writableDatabase
                    myHamster_hamsterNameTextView.text = name
                    sqlitedb.execSQL("UPDATE basic_info_db SET hamster_name = '${name}' WHERE" +
                            " hamster_name = '${hamsterName}'")
                }
            }
        })
    }

    //인밴토리 확인
    @SuppressLint("Range")
    private fun upDateInventory(name: String) {
        //layout 초기화
        myHamster_itemList.removeAllViews()

        //구매한 아이템만 가져와서 보여줌
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        val cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE " +
                "type = '$name' AND is_bought = 1",null)

        while(cursor.moveToNext()){
            //동적 뷰 생성
            var view: View = layoutInflater.inflate(R.layout.container_my_hamster_item, myHamster_itemList, false)

            var myHItemBgImageView = view.findViewById<ImageView>(R.id.myHItemBgImageView)

            val marketPic = cursor.getString(cursor.getColumnIndex("market_pic"))
            val itemName = cursor.getString(cursor.getColumnIndex("item_name")).toString()
            val itemCategory = cursor.getString(cursor.getColumnIndex("category")).toString()
            val id = this.resources.getIdentifier(marketPic, "drawable", requireActivity().packageName)

            //배경 설정
            myHItemBgImageView.setImageResource(id)
            if(selectedItems.contains(itemName)){
                myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.SeedBrown))
            } else {
                myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
            }

            view.setOnClickListener {
                //선택 해제할 아이템이 들어감
                var deselectItems = ArrayList<String>()

                val dbManager2 = DBManager(requireContext(), "hamster_db", null, 1)
                var sqlitedb2 = dbManager2.readableDatabase

                //선택 해제 중이라면
                if(!selectedItems.contains(itemName)){
                    myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(resources.getColor(
                        R.color.SeedBrown
                    ))

                    //같은 카테고리의 아이템이 선택중이면 선택해제
                    val cursor2: Cursor = sqlitedb2.rawQuery("SELECT * FROM hamster_deco_info_db " +
                            "WHERE category = '${itemCategory}'", null)
                    while(cursor2.moveToNext()){
                        val tempName = cursor2.getString(cursor2.getColumnIndex("item_name"))

                        if(selectedItems.contains(tempName) && tempName != itemName) {
                            selectedItems.remove(tempName)
                            deselectItems.add(tempName)
                        }
                    }
                    cursor2.close()

                    selectedItems.add(itemName)

                    //인벤토리, 화면 업데이트
                    upDateInventory(currentInventory)
                    FunUpDateHamzzi.updateBackground(
                            requireContext(),
                            myHamster_BGFrameLayout,
                            true,
                            true
                    )
                    FunUpDateHamzzi.updateCloth(
                            requireContext(),
                            myHamster_clothFrameLayout,
                            true,
                            true
                    )
                }
                //이미 선택중이라면
                else {
                    myHItemBgImageView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00756557"))
                    selectedItems.remove(itemName)
                    deselectItems.add(itemName)
                }
                sqlitedb2.close()

                //화면 표시
                sqlitedb2 = dbManager.writableDatabase
                for(item in selectedItems){
                    sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 1 WHERE item_name = '${item}'")
                }
                for(item in deselectItems){
                    sqlitedb2.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
                }
                sqlitedb2.close()
                dbManager2.close()

                deselectItems.clear()
                FunUpDateHamzzi.updateBackground(
                        requireContext(),
                        myHamster_BGFrameLayout,
                        true,
                        true
                )
                FunUpDateHamzzi.updateCloth(
                        requireContext(),
                        myHamster_clothFrameLayout,
                        true,
                        true
                )
            }

            myHamster_itemList.addView(view)
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }



}