package com.example.guru_hemjee

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.TextView

// 모든 텍스트 팝업
// code 의미
// 0 : delete 팝업(빨강색 버튼), 1 : confirm 팝업(노랑색 버튼)
class AlertDialog(context: Context, title: String, content: String, btnName: String, code: Int) {
    private val dialog = Dialog(context)

    // context 변수
    val context = MyApplication.applicationContext()

    // 팝업 제목&내용, 오른쪽 버튼의 글자, 팝업의 종류를 구분하기 위한 코드
    private var title: String = title
    private var content: String = content
    private var btnName: String = btnName
    private var code: Int = code

    // 팝업의 위젯
    private lateinit var pop_alert_title_tv: TextView // 팝업 제목
    private lateinit var pop_alert_content_tv: TextView // 팝업 내용
    private lateinit var pop_alert_cancel_btn: Button // 취소 버튼
    private lateinit var pop_alert_various_btn: Button // 삭제&이동&적용 버튼

    // 팝업 표시
    @SuppressLint("ResourceAsColor")
    fun showAlertDialog() {
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 모서리 둥글게
        dialog.setContentView(R.layout.popup_alert)

        // 위젯 연결
        pop_alert_title_tv = dialog.findViewById(R.id.pop_alert_title_tv)
        pop_alert_content_tv = dialog.findViewById(R.id.pop_alert_content_tv)
        pop_alert_cancel_btn = dialog.findViewById(R.id.pop_alert_cancel_btn)
        pop_alert_various_btn = dialog.findViewById(R.id.pop_alert_various_btn)

        when (code) {
            // delete 팝업
            0 -> {
                //팝업 제목 설정
                pop_alert_title_tv.visibility = View.VISIBLE
                pop_alert_title_tv.text = title

                // 팝업 내용 설정
                pop_alert_content_tv.text = content

                // 오른쪽 버튼 텍스트 및 색상 설정
                pop_alert_various_btn.text = btnName
                pop_alert_various_btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F54343"))
                pop_alert_various_btn.setTextColor(Color.WHITE)

                // 삭제 버튼 이벤트
                pop_alert_various_btn.setOnClickListener {
                    onClickListener.onClicked(true)
                    dialog.dismiss()
                }
            }
            // confirm 팝업
            1 -> {
                // 팝업 제목 숨기기
                pop_alert_title_tv.visibility = View.GONE

                // 팝업 내용 설정
                pop_alert_content_tv.text = content

                // 오른쪽 버튼 텍스트 및 색상 설정
                pop_alert_various_btn.text = btnName
                pop_alert_various_btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFF2A6"))
                pop_alert_various_btn.setTextColor(ColorStateList.valueOf(Color.parseColor("#2E2925")))

                // 적용&이동 버튼 이벤트
                pop_alert_various_btn.setOnClickListener {
                    onClickListener.onClicked(true)
                    dialog.dismiss()
                }
            }
        }

        // 취소 버튼 이벤트
        pop_alert_cancel_btn.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }
    }

    // 인자를 넘겨주기 위한 클릭 인터페이스(팝업을 띄우는 화면에서 처리)
    interface ButtonClickListener {
        fun onClicked(isConfirm: Boolean)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

}