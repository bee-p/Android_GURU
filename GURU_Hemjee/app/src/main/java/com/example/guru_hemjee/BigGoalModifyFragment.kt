package com.example.guru_hemjee

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class BigGoalModifyFragment : Fragment() {

    // 내부DB 사용을 위한 변수
    lateinit var dbManager: DBManager
    lateinit var dbManager2: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 대표 목표
    lateinit var modBigGoalEditText: EditText

    // 라디오 그룹
    lateinit var modColorRadioGroup1: RadioGroup // 1행 라디오 그룹 변수
    lateinit var modColorRadioGroup2: RadioGroup // 2행 라디오 그룹 변수

    // 라디오 버튼
    lateinit var modOrangeRadioBtn: RadioButton // 오렌지색 라디오 버튼
    lateinit var modYellowRadioBtn: RadioButton // 노랑색 라디오 버튼
    lateinit var modNoteYellowRadioBtn: RadioButton // 노트노랑색 라디오 버튼
    lateinit var modApricotRadioBtn: RadioButton // 살구색 라디오 버튼
    lateinit var modSeedBrownRadioBtn: RadioButton // 갈색 라디오 버튼
    lateinit var modDarkBrownRadioBtn: RadioButton // 고동색 라디오 버튼
    lateinit var modLightGreenRadioBtn: RadioButton // 연두색 라디오 버튼
    lateinit var modGreenRadioBtn: RadioButton // 초록색 라디오 버튼
    lateinit var modLightBlueRadioBtn: RadioButton // 하늘색 라디오 버튼
    lateinit var modBlueRadioBtn: RadioButton // 파랑색 라디오 버튼
    lateinit var modPurpleRadioBtn: RadioButton // 보라색 라디오 버튼
    lateinit var modPinkRadioBtn: RadioButton // 분홍색 라디오 버튼

    // 목표 잠금 시간
    lateinit var modTodayLockHourView: EditText // 시간
    lateinit var modTodayLockMinView: EditText // 분

    // 삭제, 확인 버튼
    lateinit var modDeleteButton: androidx.appcompat.widget.AppCompatButton // 삭제 버튼
    lateinit var modCompleteButton: androidx.appcompat.widget.AppCompatButton // 확인 버튼

    private lateinit var str_big_goal: String // 대표목표
    private var integer_color: Int = 0 // 대표목표 색상
    private lateinit var total_time: String

    var mainActivity: MainActivity? = null // 메인 액티비티 변수

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()

        mainActivity = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_big_goal_modify, container, false)

        // 대표 목표
        modBigGoalEditText = view.findViewById(R.id.modBigGoalEditText)

        // 라디오 그룹
        modColorRadioGroup1 = view.findViewById(R.id.modColorRadioGroup1)
        modColorRadioGroup2 = view.findViewById(R.id.modColorRadioGroup2)

        // 라디오 버튼
        modOrangeRadioBtn = view.findViewById(R.id.modOrangeRadioBtn)
        modYellowRadioBtn = view.findViewById(R.id.modYellowRadioBtn)
        modNoteYellowRadioBtn = view.findViewById(R.id.modNoteYellowRadioBtn)
        modApricotRadioBtn = view.findViewById(R.id.modApricotRadioBtn)
        modSeedBrownRadioBtn = view.findViewById(R.id.modSeedBrownRadioBtn)
        modDarkBrownRadioBtn = view.findViewById(R.id.modDarkBrownRadioBtn)
        modLightGreenRadioBtn = view.findViewById(R.id.modLightGreenRadioBtn)
        modGreenRadioBtn = view.findViewById(R.id.modGreenRadioBtn)
        modLightBlueRadioBtn = view.findViewById(R.id.modLightBlueRadioBtn)
        modBlueRadioBtn = view.findViewById(R.id.modBlueRadioBtn)
        modPurpleRadioBtn = view.findViewById(R.id.modPurpleRadioBtn)
        modPinkRadioBtn = view.findViewById(R.id.modPinkRadioBtn)

        // 목표 잠금 시간
        modTodayLockHourView = view.findViewById(R.id.modTodayLockHourView)
        modTodayLockMinView = view.findViewById(R.id.modTodayLockMinView)

        // 삭제, 확인 버튼
        modDeleteButton = view.findViewById(R.id.modDeleteButton)
        modCompleteButton = view.findViewById(R.id.modCompleteButton)

        // DetailGoalSetupFragment에서 넘어온 값(대표 목표) 받기
        if (arguments != null) {
            str_big_goal = requireArguments().getString("bundle_biggoal_2").toString() // 대표목표

            // 대표목표 DB
            dbManager = DBManager(context, "big_goal_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 대표목표 찾기
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';", null)

            // 색상, 시간 값 찾기
            if (cursor.moveToNext()) {
                integer_color = cursor.getInt(cursor.getColumnIndex("color"))
                total_time = cursor.getString(cursor.getColumnIndex("big_goal_lock_time")).toString()
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            // 위젯에 반영하기
            modBigGoalEditText.setText(str_big_goal)
            when (integer_color) {
                ContextCompat.getColor(requireContext(), R.color.Orange) -> modOrangeRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Yellow) -> modYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.NoteYellow) -> modNoteYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Apricot) -> modNoteYellowRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.SeedBrown) -> modSeedBrownRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.DarkBrown) -> modDarkBrownRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.LightGreen) -> modLightGreenRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Green) -> modGreenRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.LightBlue) -> modLightBlueRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Blue) -> modBlueRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Purple) -> modPurpleRadioBtn.isChecked = true
                ContextCompat.getColor(requireContext(), R.color.Pink) -> modPinkRadioBtn.isChecked = true
            }

            Log.d("재접속했을 때의 변경한 시간 : ", total_time)
            var lock_timeArray = total_time.split(':')
            modTodayLockHourView.setText(lock_timeArray[0])
            modTodayLockMinView.setText(lock_timeArray[1])
        }

        // TODO: 라디오 그룹간의 전환 시 다른 라디오 그룹에 있는 버튼을 2번 눌러야만 선택되는 문제 수정 필요
        // 색깔 라디오 버튼 클릭시 이벤트 연결
        modColorRadioGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹1에서 버튼을 눌렀다면
                R.id.modOrangeRadioBtn,
                R.id.modYellowRadioBtn,
                R.id.modNoteYellowRadioBtn,
                R.id.modApricotRadioBtn,
                R.id.modSeedBrownRadioBtn,
                R.id.modDarkBrownRadioBtn -> {
                    modColorRadioGroup2.clearCheck() // 라디오 그룹2에서 선택되어 있는 버튼 초기화
                }
            }
        }
        modColorRadioGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) { // 라디오 그룹2에서 버튼을 눌렀다면
                R.id.modLightGreenRadioBtn,
                R.id.modGreenRadioBtn,
                R.id.modLightBlueRadioBtn,
                R.id.modBlueRadioBtn,
                R.id.modPurpleRadioBtn,
                R.id.modPinkRadioBtn -> {
                    modColorRadioGroup1.clearCheck() // 라디오 그룹1에서 선택되어 있는 버튼 초기화
                }
            }
        }

        // 대표목표 DB
        dbManager = DBManager(context, "big_goal_db", null, 1)
        // 세부목표 DB
        dbManager2 = DBManager(context, "detail_goal_db", null, 1)

        // 확인버튼을 눌렀을 경우
        modCompleteButton.setOnClickListener {
            var big_goal = modBigGoalEditText.text.toString() // 대표 목표
            var color: Int = integer_color // 색상
            var str_hour: String = modTodayLockHourView.text.toString() // 잠금 시간
            var str_min: String = modTodayLockMinView.text.toString()// 잠금 분

            if (big_goal.isNullOrBlank()) { // EditText가 비어있다면
                Toast.makeText(context, "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (str_hour.isNullOrBlank() && str_min.isNullOrBlank()) {
                Toast.makeText(context, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                when (modColorRadioGroup1.checkedRadioButtonId) {
                    R.id.modOrangeRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Orange)
                    R.id.modYellowRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Yellow)
                    R.id.modNoteYellowRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.NoteYellow)
                    R.id.modApricotRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Apricot)
                    R.id.modSeedBrownRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.SeedBrown)
                    R.id.modDarkBrownRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.DarkBrown)
                }
                when (modColorRadioGroup2.checkedRadioButtonId) {
                    R.id.modLightGreenRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.LightGreen)
                    R.id.modGreenRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Green)
                    R.id.modLightBlueRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.LightBlue)
                    R.id.modBlueRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Blue)
                    R.id.modPurpleRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Purple)
                    R.id.modPinkRadioBtn -> color = ContextCompat.getColor(requireContext(), R.color.Pink)
                }
            }

            // TODO : 더 깔끔하게 코드를 바꿀 수 있도록 고민하기
            lateinit var total_time: String
            if (str_hour.isNullOrBlank()) { // 시간이 공란인 경우
                if (str_min.toInt() < 0 || str_min.toInt() >= 60) {
                    Toast.makeText(context, "분을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    total_time = FunTimeConvert.timeConvert(null, str_min.toInt().toString(), null)
                }
            } else if (str_min.isNullOrBlank()) { // 분이 공란인 경우
                if (str_hour.toInt() < 0 || str_hour.toInt() > 24) {
                    Toast.makeText(context, "시간을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    total_time = FunTimeConvert.timeConvert(str_hour.toInt().toString(), null, null)
                }
            } else if (str_hour.toInt() < 0 || str_hour.toInt() > 24) { // 시간 범위
                Toast.makeText(context, "시간을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (str_min.toInt() < 0 || str_min.toInt() >= 60) { // 분 범위
                Toast.makeText(context, "분을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                total_time = FunTimeConvert.timeConvert(str_hour.toInt().toString(), str_min.toInt().toString(), null)
            }

            /** <데이터를 수정하는 방법>
             * 1. 새로운 테이블 생성
             * 2. 기존 테이블에 있던 특정 데이터 값들만 복사해서 새로운 테이블에 붙여넣기
             * 3. 기존 테이블에 있는 복사된 데이터 삭제
             * 4. 복사한 테이블의 데이터 수정
             * 5. 기존 테이블에 복사한 테이블의 데이터 추가하기
             * 6. 새로 만든 테이블 삭제
             **/
            sqlitedb = dbManager.writableDatabase

            sqlitedb.execSQL("CREATE TABLE copy_goal_db (big_goal_name text, color INT, big_goal_lock_time text)")
            sqlitedb.execSQL("INSERT INTO copy_goal_db SELECT * FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.execSQL("UPDATE copy_goal_db SET color = " + color + " WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.execSQL("UPDATE copy_goal_db SET big_goal_lock_time = '" + total_time + "' WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.execSQL("UPDATE copy_goal_db SET big_goal_name = '" + big_goal + "' WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.execSQL("INSERT INTO big_goal_db SELECT * FROM copy_goal_db WHERE big_goal_name = '" + big_goal + "';")
            sqlitedb.execSQL("DROP TABLE copy_goal_db")

            sqlitedb.close()

            sqlitedb = dbManager2.writableDatabase

            sqlitedb.execSQL("UPDATE detail_goal_db SET big_goal_name = '" + big_goal + "' WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.close()

            Toast.makeText(context, "목표 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()

            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val detailGoalSetupFragment = DetailGoalSetupFragment()
            val bundle = Bundle()
            bundle.putString("bundle_biggoal", big_goal)

            detailGoalSetupFragment.setArguments(bundle)

            transaction.replace(R.id.fragment_main, SetupFragment())
            transaction.commit() // 저장
        }

        // 삭제 버튼을 눌렀을 경우
        modDeleteButton.setOnClickListener {
            dbManager = DBManager(context, "big_goal_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            sqlitedb.execSQL("DELETE FROM big_goal_db WHERE big_goal_name = '" + str_big_goal + "';")
            sqlitedb.close()
            dbManager.close()

            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val setupFragment = SetupFragment()
            val bundle = Bundle()
            bundle.putString("bundle_biggoal", str_big_goal)

            setupFragment.setArguments(bundle)

            transaction.replace(R.id.fragment_main, SetupFragment())
            transaction.commit() // 저장
        }
        return view
    }
}