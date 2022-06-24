package com.example.guru_hemjee

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import java.util.*
import kotlin.collections.ArrayList

// MainActivity -> 홈
// 햄찌, 잠금 시작 등을 할 수 있는 홈 Fragment 화면
class HomeFragment : Fragment() {

//    //씨앗 개수
//    private lateinit var home_seedPointTextView: TextView
//
//    //시작 버튼
//    private lateinit var home_startButton: Button
//
//    //잠금 수정 버튼
//    private lateinit var home_goalSelectButton: MaterialButton
//    private var bigGoalName: String = ""
//    private var bigGoalColor: Int = 0
//
//    //잠금 시간 안내
//    private var time = "00:00:00"
//    private lateinit var home_goalTimeTextView: TextView
//
//    //햄찌 관련
//    private lateinit var home_BGFrameLayout: FrameLayout
//    private lateinit var home_clothFrameLayout: FrameLayout
//    //햄찌 말 관련
//    private var hamsterTalkList = ArrayList<String>()
//    private lateinit var home_hamsterTalkTextView: TextView
//
//    //db관련
//    private lateinit var dbManager: DBManager
//    private lateinit var sqlitedb: SQLiteDatabase
//    private lateinit var hamName: String
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }
//
//    @SuppressLint("Range")
//    override fun onStart() {
//        super.onStart()
//
//        //씨앗 표시
//        home_seedPointTextView = requireView().findViewById(R.id.home_seedPointTextView)
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//
//        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM basic_info_db", null)
//        if(cursor.moveToNext()){
//            home_seedPointTextView.text = cursor.getString(cursor.getColumnIndex("seed")).toString()
//            hamName = cursor.getString(cursor.getColumnIndex("hamster_name")).toString()
//        }
//
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        //잠금 버튼 수정
//        home_goalSelectButton = requireView().findViewById(R.id.home_goalSelectButton)
//        dbManager = DBManager(context, "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//        cursor= sqlitedb.rawQuery("SELECT * FROM big_goal_db",null)
//        var isThereBigGoal = false
//        if(cursor.moveToNext()){
//            bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name"))
//            bigGoalColor = cursor.getInt(cursor.getColumnIndex("color"))
//            time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time"))
//
//            home_goalSelectButton.text = bigGoalName
//            home_goalSelectButton.setTextColor(resources.getColor(R.color.Black))
//            home_goalSelectButton.iconTint = ColorStateList.valueOf(bigGoalColor)
//
//            isThereBigGoal = true
//        }
//        cursor.close()
//        sqlitedb.close()
//        dbManager.close()
//
//        if(!isThereBigGoal){
//            bigGoalName = "목표를 생성해주세요"
//            bigGoalColor = R.color.Gray
//            home_goalSelectButton.text = bigGoalName
//            home_goalSelectButton.setTextColor(bigGoalColor)
//            home_goalSelectButton.setIconTintResource(R.color.Gray)
//        }
//        home_goalSelectButton.setOnClickListener {
//            showLockSettingPopUp()
//        }
//
//        //메인 화면 햄찌 설정(선처리)
//        var preselectedItems = ArrayList<String>()
//        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
//        sqlitedb = dbManager.readableDatabase
//        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_applied = 1",null)
//        while(cursor.moveToNext()){
//            preselectedItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
//        }
//        cursor.close()
//        var preusingItems = ArrayList<String>()
//        cursor = sqlitedb.rawQuery("SELECT * FROM hamster_deco_info_db WHERE is_using = 1",null)
//        while(cursor.moveToNext()){
//            preusingItems.add(cursor.getString(cursor.getColumnIndex("item_name")))
//        }
//        cursor.close()
//        sqlitedb.close()
//
//        sqlitedb = dbManager.writableDatabase
//        for(item in preusingItems){
//            if(!preselectedItems.contains(item))
//                sqlitedb.execSQL("UPDATE hamster_deco_info_db SET is_using = 0 WHERE item_name = '${item}'")
//        }
//        sqlitedb.close()
//        dbManager.close()
//
//        home_BGFrameLayout = requireView().findViewById(R.id.home_BGFrameLayout)
//        home_clothFrameLayout = requireView().findViewById(R.id.home_clothFrameLayout)
//        FunUpDateHamzzi.upDate(requireContext(), home_BGFrameLayout, home_clothFrameLayout, false, false)
//
//        //햄찌 말 처리
//        hamsterTalkList.add("미안하지만\n햄찌는 F 상대 안한다 햄찌,,")
//        hamsterTalkList.add("한 시간이 작아보이지?\n한 달이면 31시간이다 햄찌!")
//        hamsterTalkList.add("그러다가 나중에\n놀기만 한 바부가 된다 햄찌!")
//        hamsterTalkList.add("하나둘 미루다가\n하나둘 멀어지는 거다 햄찌!")
//        hamsterTalkList.add("또 유튜브 보냐 햄찌...?")
//        hamsterTalkList.add("또 트위터 보냐 햄찌...?")
//        hamsterTalkList.add("또 넷플릭스 보냐 햄찌...?")
//        hamsterTalkList.add("오늘도 안하지는 않았겠지...\n집사는 사람이니까...")
//        hamsterTalkList.add("오늘도 놀꺼냐 집사!")
//        hamsterTalkList.add("작은 거라도 조금씩하면\n목표에 가까워진다 햄찌!")
//        hamsterTalkList.add("어제보다 오늘 하나 더하면\n집사는 멋진 사람!!")
//        hamsterTalkList.add("목표는 생각보다 가까이 있다!\n끝까지 화이팅이다 햄찌!")
//        hamsterTalkList.add("미래에 후회하는 나?\n미래에 뿌듯한 나?")
//        hamsterTalkList.add("24시간은 생각보다\n모자르다 햄찌!!")
//        val listSize = hamsterTalkList.size
//        home_hamsterTalkTextView = requireView().findViewById(R.id.home_hamsterTalkTextView)
//        val random = Random()
//        var num = random.nextInt(listSize)
//        home_hamsterTalkTextView.text = hamsterTalkList[num]
//        home_hamsterTalkTextView.setOnClickListener {
//            var newNum = random.nextInt(listSize)
//            //이전 말과 무조건 다르게 나오게 처리
//            while(newNum == num){
//                newNum = random.nextInt(listSize)
//            }
//            num = newNum
//            home_hamsterTalkTextView.text = hamsterTalkList[num]
//        }
//
//
//        //잠금 시간 안내
//        home_goalTimeTextView = requireView().findViewById(R.id.home_goalTimeTextView)
//        home_goalTimeTextView.text = (time.split(':')[0]+"시 " + time.split(':')[1] + "분 "
//                + time.split(':')[2]+"초")
//
//        //lock 화면 연결
//        home_startButton = requireView().findViewById(R.id.home_startButton)
//        home_startButton.setOnClickListener {
//            if(!isThereBigGoal)   {
//                Toast.makeText(context, "목표를 생성해주세요!", Toast.LENGTH_SHORT).show()
//            } else {
//                showSettingConfirmPopUp()
//                home_hamsterTalkTextView.callOnClick()
//            }
//        }
//
//
//    }
//
//    //잠금 시작
//    private fun showSettingConfirmPopUp() {
//        val dialog = LockSettingConfirmDialog(requireContext(),bigGoalName,bigGoalColor,time)
//        dialog.myDig()
//
//        dialog.setOnClickedListener(object : LockSettingConfirmDialog.ButtonClickListener{
//            override fun onClicked(isLock: Boolean) {
//                if(isLock){
//                    // 잠금 서비스 실행
//                    LockScreenUtil.active()
//
////                    var intent = Intent(requireActivity(), LockActivity::class.java)
////
////                    // 유저 정보 보내기
////                    intent.putExtra("seed", home_seedPointTextView.text)
////                    intent.putExtra("hamsterName", hamName)
////
////                    // 대표 목표 이름 보내기
////                    intent.putExtra("bigGoalName", home_goalSelectButton.text)
////
////                    // 타이머 시간 데이터 보내기
////                    intent.putExtra("hour", time.split(':')[0])
////                    intent.putExtra("min", time.split(':')[1])
////                    intent.putExtra("sec", time.split(':')[2])
////
////                    startActivity(intent)
//
//                }
//            }
//        })
//    }
//
//    //잠금 설정
//    private fun showLockSettingPopUp() {
//        val dialog = LockSettingDialog(requireContext(), bigGoalName, bigGoalColor, time)
//        //dialog.lockSetting()
//
//        dialog.setOnClickedListener(object : LockSettingDialog.ButtonClickListener{
//            override fun onClicked(isChanged: Boolean, bigGoalTitle: String, changedBigGoalColor: Int, getTime: String) {
//                if(isChanged){
//                    bigGoalName = bigGoalTitle
//                    home_goalSelectButton.text = bigGoalTitle
//                    bigGoalColor = changedBigGoalColor
//                    home_goalSelectButton.iconTint = ColorStateList.valueOf(bigGoalColor)
//                    if(getTime != "00:00:00"){
//                        time = getTime
//                    }
//                    var timeArray = time.split(':')
//                    home_goalTimeTextView.text = timeArray[0] + "시 " + timeArray[1]+"분 " + timeArray[2]+"초"
//                }
//            }
//        })
//    }

}