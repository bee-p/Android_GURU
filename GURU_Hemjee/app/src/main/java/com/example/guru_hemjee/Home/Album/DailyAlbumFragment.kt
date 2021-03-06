package com.example.guru_hemjee.Home.Album

import android.annotation.SuppressLint
import android.widget.Toast
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import com.example.guru_hemjee.DBManager
import com.example.guru_hemjee.PhotoDialog
import com.example.guru_hemjee.R
import java.lang.IndexOutOfBoundsException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 나의 성취 앨범(AlbumMainActivity) -> 일간
// 해당 날짜의 일간 사진과 총 잠금 시간을 보여주는 Fragment 화면
class DailyAlbumFragment : Fragment() {

    // 화면에 보이는 날짜와 시간
    private lateinit var albumDaily_DateTextView: TextView
    private lateinit var albumDaily_timeTextView: TextView

    // 날짜 이동 버튼
    private lateinit var albumDaily_prevButton: ImageButton
    private lateinit var albumDaily_nextButton: ImageButton

    // 현재(오늘) 날짜 관련
    private lateinit var todayDate: LocalDateTime   // 오늘 날짜(전체)
    private lateinit var nowDate: LocalDateTime     // 현재 설정된 날짜

    // 앨범 사진들이 들어갈 레이아웃
    private lateinit var albumDaily_GridLayout: GridLayout

    // 저장된 사진이 없을 때 보여줄 레이아웃
    private lateinit var albumDaily_FrameLayout: FrameLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_album, container, false)

    }

    override fun onStart() {
        super.onStart()

        // 오늘 날짜 불러오기
        todayDate = LocalDateTime.now()

        // 위젯 연결
//        albumDaily_DateTextView = requireView().findViewById(R.id.albumDaily_DateTextView)
//        albumDaily_timeTextView = requireView().findViewById(R.id.albumDaily_timeTextView)
//
//        albumDaily_GridLayout = requireView().findViewById(R.id.albumDaily_GridLayout)
//        albumDaily_FrameLayout = requireView().findViewById(R.id.albumDaily_FrameLayout)
//
//        albumDaily_prevButton = requireView().findViewById(R.id.albumDaily_prevButton)
//        albumDaily_nextButton = requireView().findViewById(R.id.albumDaily_nextButton)

        // 위젯에 오늘 날짜 입력
        // (현재 날짜를 오늘 날짜로 설정)
        nowDate = todayDate
        albumDaily_DateTextView.text = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // 이전/이후 날짜 이동 버튼 클릭 리스너 설정
        albumDaily_prevButton.setOnClickListener {
            nowDate = nowDate.minusDays(1)  // 하루 전 날짜로 변경
            albumDaily_DateTextView.text = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  // 위젯에 날짜 적용

            // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키기
            applyTotalDailyLockTime()

            // 해당 날짜에서 달성한 목표의 사진을 불러와서 띄우기
            applyTotalDailyPhoto()
        }
        albumDaily_nextButton.setOnClickListener {
            // 현재 설정된 날짜가 오늘 날짜인지 확인
            if(nowDate == todayDate)    // 이동하지 X
                Toast.makeText(requireActivity().applicationContext,"현재 화면이 가장 최근 일자입니다.",Toast.LENGTH_SHORT).show()
            else
            {
                nowDate = nowDate.plusDays(1)   // 하루 후 날짜로 변경
                albumDaily_DateTextView.text = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))  // 위젯에 날짜 적용

                // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키기
                applyTotalDailyLockTime()

                // 해당 날짜에서 달성한 목표의 사진을 불러와서 띄우기
                applyTotalDailyPhoto()
            }
        }


        // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키기
        applyTotalDailyLockTime()

        // 해당 날짜에서 달성한 목표의 사진을 불러와서 띄우기
        applyTotalDailyPhoto()
    }

    // 해당 날짜에서 총 잠금한 시간 불러오고 위젯에 적용시키는 함수
    @SuppressLint("Range")
    private fun applyTotalDailyLockTime() {

        // 해당 날짜 불러오기
        var year: String = albumDaily_DateTextView.text.toString().split("-")[0]
        var month: String = albumDaily_DateTextView.text.toString().split("-")[1]
        var day: String = albumDaily_DateTextView.text.toString().split("-")[2]

        var dayTotalLockTime: Int = 0   // 총 잠금한 시간을 저장할 변수

        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 대표 목표 리포트 DB 열기
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db", null)
        while(cursor.moveToNext()) {
            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

            // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
            var temp2: String = temp1.split(" ")[0]

            // 2차 분리 - 연도/월/일 분리, 가져오기
            var tempYear: String = temp2.split("-")[0]
            var tempMonth: String = temp2.split("-")[1]
            var tempDay: String = temp2.split("-")[2]

            // 오늘 날짜에 해당하는 데이터만 total_lock 시간 합산
            if(year == tempYear && month == tempMonth && day == tempDay)
            {
                dayTotalLockTime += cursor.getInt(cursor.getColumnIndex("total_lock_time"))
            }
        }

        // 위젯에 totalTime 갱신
        var tempHour = dayTotalLockTime / 1000 / 60 / 60   // 시간
        var tempMin = dayTotalLockTime / 1000 / 60 % 60    // 분
        var tempSec = dayTotalLockTime / 1000 % 60         // 초

        albumDaily_timeTextView.text = "$tempHour : $tempMin : $tempSec"

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 해당 날짜에서 달성한 목표의 사진을 불러와서 보여주는 함수
    @SuppressLint("Range")
    private fun applyTotalDailyPhoto() {

        // 모든 뷰 클리어
        albumDaily_GridLayout.removeAllViews()

        // 사진들을 보여줄 레이아웃 활성화
        albumDaily_GridLayout.visibility = View.VISIBLE
        // 사진이 없을 때 보여줄 레이아웃 비활성화
        albumDaily_FrameLayout.visibility = View.GONE

        // 해당 날짜 불러오기
        var year: String = albumDaily_DateTextView.text.toString().split("-")[0]
        var month: String = albumDaily_DateTextView.text.toString().split("-")[1]
        var day: String = albumDaily_DateTextView.text.toString().split("-")[2]

        // 세부 목표 리포트 DB 열기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE " +
                "photo_name IS NOT NULL", null)
        cursor.moveToLast() // 맨 끝으로 이동
        cursor.moveToNext() // 한 단계 앞으로(빈 곳을 가리키도록 함)

        while(cursor.moveToPrevious()) {
            var detailGoalName = cursor.getString(cursor.getColumnIndex("detail_goal_name"))
            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()
            var color = cursor.getInt(cursor.getColumnIndex("color"))
            var icon = cursor.getInt(cursor.getColumnIndex("icon"))
            var bigGoalName = cursor.getString(cursor.getColumnIndex("big_goal_name"))

            //빈 값 처리
            if(detailGoalName == null || icon == null || bigGoalName == null ||
                color == null || temp1 == null){
                Log.i("사진 저장 오류", "${detailGoalName}, ${temp1}, ${color}, ${icon}, ${bigGoalName}")
                continue
            }

            // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
            var temp2: String = temp1.split(" ")[0]

            // 2차 분리 - 연도/월/일 분리, 가져오기
            var tempYear: String = temp2.split("-")[0]
            var tempMonth: String = temp2.split("-")[1]
            var tempDay: String = temp2.split("-")[2]

            // 해당 날짜에 맞는 데이터만 사진 가져와서 적용시키기
            if(year == tempYear && month == tempMonth && day == tempDay)
            {
                var path = requireContext().filesDir.toString() + "/picture/"
                path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()

                try {
                    // imageView 생성
                    var imageView: ImageView = ImageView(requireContext())

                    var imageViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                    imageViewParams.gravity = Gravity.CENTER

                    imageView.layoutParams = imageViewParams

                    var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                    // 이미지 배율 크기 작업 - 360x360 크기로 재설정함
                    var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 360, 360, true)
                    imageView.setImageBitmap(reScaledBitmap)

                    //사진에 팝업 연결
                    imageView.setOnClickListener {
                        val dialog = PhotoDialog(requireContext(), path, icon, detailGoalName, bigGoalName, temp2, color)
                        dialog.photoPopUp()
                    }

                    // 레이아웃에 이미지 뷰 넣기
                    albumDaily_GridLayout.addView(imageView)

                }
                catch(e: Exception) {
                    Log.e("오류태그", "오늘 사진 로드/세팅 실패 \n${e.printStackTrace()}")
                }
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 현재 불러올 사진이 없어 빈 화면이라면 메시지 띄우기 **/

        // 불러올 사진이 없을 경우 dailyAlbumGridLayout에 담겨있는 View가 없어 Exception이 발생한다.
        try {
            albumDaily_GridLayout.get(0)
        }
        // Exception이 발생했을 시
        catch(e: IndexOutOfBoundsException) {

            // 사진들을 보여줄 레이아웃 비활성화
            albumDaily_GridLayout.visibility = View.GONE
            // 사진이 없을 때 보여줄 레이아웃 활성화
            albumDaily_FrameLayout.visibility = View.VISIBLE
        }
    }
}