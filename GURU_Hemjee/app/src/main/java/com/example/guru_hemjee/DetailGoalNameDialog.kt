package com.example.guru_hemjee

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

class DetailGoalNameDialog(context: Context, name: String) {
    private val dialog = Dialog(context)

    //기존 이름 textView
    private lateinit var nameTextView: TextView
    private var name = name

    //수정 이름 editText
    private lateinit var editNameEditText: EditText

    //취소, 확인 버튼
    private lateinit var hamsterCancelImageButton: ImageButton
    private lateinit var nameEditImageButton: ImageButton

    fun EditName() {
        dialog.show()
        dialog.setContentView(R.layout.popup_detail_goal_name_edit)

        //기존 이름
        nameTextView = dialog.findViewById(R.id.originDetailGoalNameTextView)
        nameTextView.text = name

        editNameEditText = dialog.findViewById(R.id.detailNameEditText)
        hamsterCancelImageButton = dialog.findViewById(R.id.goalNameEditCancelImageButton)
        nameEditImageButton = dialog.findViewById(R.id.detailGoalNameOkImageButton)

        hamsterCancelImageButton.setOnClickListener {
            onClickListener.onClicked(false, null)
            dialog.dismiss()
        }

        nameEditImageButton.setOnClickListener {
            if(editNameEditText.text != null){
                name = editNameEditText.text.toString()
            }
            onClickListener.onClicked(true, name)
            dialog.dismiss()
        }
    }

    interface ButtonClickListener {
        fun onClicked(isChanged: Boolean, name: String?)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}