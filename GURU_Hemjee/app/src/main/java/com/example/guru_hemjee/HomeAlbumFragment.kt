package com.example.guru_hemjee

import android.util.Log

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeAlbumFragment : Fragment() {

    private lateinit var dateTextView: TextView // 오늘 날짜
    private lateinit var timeTextView: TextView // 오늘 총 잠금한 시간

    // 현재 날짜 저장 변수
    private lateinit var today: String  // 현재 날짜(전체)
    private lateinit var year: String   // 현재 년도
    private lateinit var month: String  // 현재 월
    private lateinit var day: String    // 현재 일

    // 상단 전체 사진 이미지 뷰 ArrayList
    private var todayPhoto = ArrayList<ImageView>()

    // 대표 목표별 앨범 사진이 들어갈 레이아웃
    private lateinit var goalAlbumLayout: GridLayout

    // 카테고리별 앨범 사진이 들어갈 레이아웃
    private lateinit var categoryAlbumLayout: LinearLayout

    //DB 관련
    private lateinit var dbManager: DBManager
    private lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_album, container, false)
    }

    override fun onStart() {
        super.onStart()

        // 위젯 연결
        dateTextView = requireView().findViewById(R.id.homeAlbum_dateTextView)
        timeTextView = requireView().findViewById(R.id.homeAlbum_timeTextView)

        goalAlbumLayout = requireView().findViewById(R.id.homeAlbum_goalAlbumLayout)
        categoryAlbumLayout = requireView().findViewById(R.id.homeAlbum_categoryLinearLayout)

        for(i: Int in 1..6) // 1~6까지 반복
        {
            var tempPhotoView: ImageView = requireView().findViewById(resources.getIdentifier("homeAlbum_albumView$i", "id", requireContext().packageName))
            todayPhoto.add(tempPhotoView)
        }


        // 현재 날짜 불러오기
        today = SimpleDateFormat("yyyy.MM.dd").format(Date(System.currentTimeMillis() + 32400000))
        // 현재 날짜 위젯에 집어넣기
        dateTextView.text = today

        // 현재 연도/월/일 각각 뽑아오기
        year = today.split(".")[0]
        month = today.split(".")[1]
        day = today.split(".")[2]

        // 오늘 총 잠금한 시간 불러오기 & 위젯에 적용
        applyTotalDailyLockTime()

        // 오늘 달성한 목표들의 사진 불러오기 & 위젯에 적용
        applyTotalDailyPhoto()

        // 대표 목표별 앨범 클리어
        goalAlbumLayout.removeAllViews()
        // 대표 목표별 앨범 세팅
        applyDailyBigGoalPhoto()

        // 카테고리별 앨범 클리어
        categoryAlbumLayout.removeAllViews()
        // 카테고리별 앨범 세팅
        applyDailyCategoryPhoto()
    }

    // 오늘 총 잠금한 시간 불러오고 위젯에 적용시키는 함수
    private fun applyTotalDailyLockTime() {
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
        var tempHour = dayTotalLockTime / 1000 / 60 / 60 % 24   // 시간
        var tempMin = dayTotalLockTime / 1000 / 60 % 60         // 분
        var tempSec = dayTotalLockTime / 1000 % 60              // 초

        timeTextView.text = "$tempHour : $tempMin : $tempSec"

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 오늘 달성한 목표의 사진을 불러오고 위젯에 세팅하는 함수
    // 사진은 최대 최근 6개까지만 불러옴
    private fun applyTotalDailyPhoto() {
        // 세부 목표 리포트 DB 열기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db WHERE photo_name IS NOT NULL", null)
        cursor.moveToLast() // 맨 끝으로 이동
        cursor.moveToNext() // 한 단계 앞으로(빈 곳을 가리키도록 함)

        var count: Int = 0

        while(count < 6 && cursor.moveToPrevious()) {
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

            // 오늘 날짜에 해당하는 데이터만 사진 가져와서 적용시키기
            if(year == tempYear && month == tempMonth && day == tempDay)
            {
                var path = requireContext().filesDir.toString() + "/picture/"
                path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()

                try {
                    var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                    // 이미지 배율 크기 작업 - 132x120 크기로 재설정함
                    var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 132, 120, true)
                    todayPhoto.get(count).setImageBitmap(reScaledBitmap)

                    //사진에 팝업 연결
                    todayPhoto[count].setOnClickListener {
                        val dialog = PhotoDialog(requireContext(), path, icon, detailGoalName, bigGoalName, temp2, color)
                        dialog.photoPopUp()
                    }
                }
                catch(e: Exception) {
                    Log.e("오류태그", "오늘 사진 로드/세팅 실패 \n${e.printStackTrace()}")
                }
            }

            count++                 // 카운트 증가
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }

    // 대표 목표별 앨범 사진 세팅하는 함수
    private fun applyDailyBigGoalPhoto() {

        /** view 동적 생성, 대표 목표 이름과 색상 가져와서 세팅하기 - big_goal_db **/

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 대표 목표 DB 열기
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_db", null)

        while(cursor.moveToNext())
        {
            // view goalAlbumLayout에 부풀리기
            var view: View = layoutInflater.inflate(R.layout.container_small_album, goalAlbumLayout, false)

            // 아이콘 색상을 대표 목표 색상으로 변경
            var icon: ImageView = view.findViewById(R.id.smallAlbum_iconImageView)
            icon.setColorFilter(cursor.getInt(cursor.getColumnIndex("color")), PorterDuff.Mode.SRC_IN)

            // 제목을 대표 목표 이름으로 변경
            var goalName: TextView = view.findViewById(R.id.smallAlbum_goalNameTextView)
            goalName.text = cursor.getString(cursor.getColumnIndex("big_goal_name")).toString()

            // view 추가
            goalAlbumLayout.addView(view)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 세부 목표 리포트 + 세부 목표 DB에서 사진 뽑아오기 - detail_goal_time_report_db + detail_goal_db **/

        // goalAlbumLayout에 생성한 뷰 개수 가져오기
        var totalCountIndex: Int = goalAlbumLayout.childCount - 1
        var removeCount = 0 // 삭제한 뷰의 개수

        // 0 ~ totalCountIndex만큼 반복
        for(index in 0..(totalCountIndex-1))
        {
            // DB 불러오기
            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 해당 뷰 가져오기
            var view: View = goalAlbumLayout.get(index)

            // 대표 목표 이름 가져오기
            var goalNameTextView: TextView = view.findViewById(R.id.smallAlbum_goalNameTextView)
            var goalName: String = goalNameTextView.text.toString()

            // 세부 목표 리포트 + 세부 목표 DB 열기
            cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db "
                    + " WHERE big_goal_name = '$goalName'", null)

            // 만일 해당 대표 목표에서 저장된 사진이 없다면
            if(!cursor.moveToNext())
            {
                // 해당 대표 목표 폴더 삭제
                goalAlbumLayout.removeViewAt(index - removeCount)

                // 삭제한 횟수 증가
                removeCount++
            }

            var isDone = false     // 날짜 비교 반복문 탈출하기 위한 flag
            cursor.moveToLast()    // 가장 최근 데이터를 가져오기 위해 커서를 마지막으로 이동
            cursor.moveToNext()    // 한 단계 앞으로(빈 곳을 가리키도록 함)

            // 세부 목표 리포트에서 날짜 비교하기
            while(!isDone && cursor.moveToPrevious())
            {
                var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

                // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
                var temp2: String = temp1.split(" ")[0]

                // 2차 분리 - 연도/월/일 분리, 가져오기
                var tempYear: String = temp2.split("-")[0]
                var tempMonth: String = temp2.split("-")[1]
                var tempDay: String = temp2.split("-")[2]

                // 오늘 날짜에 해당하는 데이터만 사진 가져와서 적용시키기
                if(year == tempYear && month == tempMonth && day == tempDay)
                {
                    var path = requireContext().filesDir.toString() + "/picture/"
                    path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()

                    try {
                        var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                        // 이미지 배율 크기 작업 - 156x156 크기로 재설정함
                        var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 156, 156, true)

                        var goalPhoto: ImageView = view.findViewById(R.id.smallAlbum_goalAlbumImageView)
                        goalPhoto.setImageBitmap(reScaledBitmap)

                        isDone = true
                    }
                    catch(e: Exception) {
                        Log.e("오류태그", "대표 목표별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
                        break
                    }
                }
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            /** 대표 목표의 총 잠금한 시간 가져와서 세팅하기 - big_goal_time_report_db **/

            // DB 불러오기
            dbManager = DBManager(requireContext(), "hamster_db", null, 1)
            sqlitedb = dbManager.readableDatabase

            // 대표 목표 리포트 DB 열기 - 해당 대표 목표의 데이터만 가져오기
            cursor = sqlitedb.rawQuery("SELECT * FROM big_goal_time_report_db WHERE big_goal_name = '$goalName'", null)

            // 대표 목표별 총 잠금한 시간 변수
            var totalGoalLockTime: Int = 0

            // 대표 목표 리포트에서 날짜 비교하기
            while(cursor.moveToNext())
            {
                // 날짜 가져오기
                var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

                // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
                var temp2: String = temp1.split(" ")[0]

                // 2차 분리 - 연도/월/일 분리, 가져오기
                var tempYear: String = temp2.split("-")[0]
                var tempMonth: String = temp2.split("-")[1]
                var tempDay: String = temp2.split("-")[2]

                // 오늘 날짜에 해당하는 데이터만 가져와서 총 잠금시간 더하기
                if(year == tempYear && month == tempMonth && day == tempDay)
                {
                    totalGoalLockTime += cursor.getInt(cursor.getColumnIndex("total_lock_time"))
                }
            }

            // 위젯에 totalTime 갱신
            var tempHour = totalGoalLockTime / 1000 / 60 / 60 % 24   // 시간
            var tempMin = totalGoalLockTime / 1000 / 60 % 60         // 분
            var tempSec = totalGoalLockTime / 1000 % 60              // 초

            var timeTextView: TextView = view.findViewById(R.id.smallAlbum_timeTextView)
            timeTextView.text = "$tempHour : $tempMin : $tempSec"

            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }
    }

    // 카테고리별 앨범 사진 세팅하는 함수
    private fun applyDailyCategoryPhoto() {

        /** 아이콘 뽑아오기 & 뷰 생성해놓기 **/

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 세부 목표 DB 열기 - icon 가져오기 위함(중복 데이터 제거해서 들고 옴)
        var cursor: Cursor = sqlitedb.rawQuery("SELECT DISTINCT icon FROM detail_goal_db", null)

        // icon 값을 저장할 ArrayList
        var iconList = ArrayList<Int>()

        //사진 개수를 저장할 ArrayList
        var picNums = ArrayList<Int>()

        while(cursor.moveToNext())
        {
            // view goalAlbumLayout에 부풀리기
            var view: View = layoutInflater.inflate(R.layout.container_big_album, categoryAlbumLayout, false)

            // 아이콘 변경
            var icon: ImageView = view.findViewById(R.id.bigAlbum_albumIconImageView)
            icon.setImageResource(cursor.getInt(cursor.getColumnIndex("icon")))

            // icon 값 받아와서 저장하기
            iconList.add(cursor.getInt(cursor.getColumnIndex("icon")))

            // view 추가
            categoryAlbumLayout.addView(view)

            //사진 개수 추가
            picNums.add(0)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 해당 아이콘 앨범에 맞는 이미지 적용시키기 **/

        // DB 불러오기
        dbManager = DBManager(requireContext(), "hamster_db", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 세부 목표 리포트
        cursor = sqlitedb.rawQuery("SELECT * FROM detail_goal_time_report_db", null)

        cursor.moveToLast() // 최근 데이터를 가져오기 위해 맨 마지막으로 커서 이동
        cursor.moveToNext() // 다음 단계로 한 칸 이동(빈 곳을 가리키도록 함)

        while(cursor.moveToPrevious())
        {
            /** 어떤 아이콘인지 구분하기 **/
            var tempIcon: Int = cursor.getInt(cursor.getColumnIndex("icon"))
            // 몇 번째 아이콘인지 뽑아오기
            var iconNum: Int = iconList.indexOf(tempIcon)
            // 해당 뷰 연결
            var view: View = categoryAlbumLayout.get(iconNum)
            //몇 번째 사진 인지. 3개의 사진이 이미 들어갔다면 사진 추가를 하지 않는다.
            if(++picNums[iconNum] >= 4)
                continue

            /** 날짜 데이터 가져와서 비교하기 **/
            var temp1: String = cursor.getString(cursor.getColumnIndex("lock_date")).toString()

            // 1차 분리 - 날짜와 시간 분리, 날짜 가져오기
            var temp2: String = temp1.split(" ")[0]

            // 2차 분리 - 연도/월/일 분리, 가져오기
            var tempYear: String = temp2.split("-")[0]
            var tempMonth: String = temp2.split("-")[1]
            var tempDay: String = temp2.split("-")[2]

            // 오늘 날짜에 해당하는 데이터만 사진 가져와서 적용시키기
            if(year == tempYear && month == tempMonth && day == tempDay)
            {
                var path = requireContext().filesDir.toString() + "/picture/"
                path += cursor.getString(cursor.getColumnIndex("photo_name")).toString()

                try {
                    var bitmap: Bitmap = BitmapFactory.decodeFile(path)
                    // 이미지 배율 크기 작업 - 156x155 크기로 재설정함
                    var reScaledBitmap = Bitmap.createScaledBitmap(bitmap, 156, 155, true)

                    var categotyPhoto: ImageView = view.findViewById(resources.getIdentifier("bigAlbum_bigAlbumImageView" + picNums[iconNum], "id", requireContext().packageName))
                    categotyPhoto.setImageBitmap(reScaledBitmap)
                }
                catch(e: Exception) {
                    Log.e("오류태그", "카테고리별 사진 로드/세팅 실패 => 강제 탈출 \n${e.printStackTrace()}")
                    break
                }
            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        /** 현재 사진이 없는 카테고리는 제거 **/

        // 레이아웃에 있는 View 하나를 제거하면 나머지 View들이 자동으로
        // 그 빈 공간을 채워 앞으로 당겨지기 때문에
        // 아래와 같은 삭제 횟수 변수를 사용함
        var removeCount = 0

        for(index in picNums.indices)
        {
            // 해당 카테고리에 들어가 있는 사진이 없다면
            if(picNums[index] == 0)
            {
                // 해당 카테고리 폴더를 삭제한다
                categoryAlbumLayout.removeViewAt(index - removeCount)

                // 삭제한 횟수 증가
                removeCount++
            }
        }
    }
}