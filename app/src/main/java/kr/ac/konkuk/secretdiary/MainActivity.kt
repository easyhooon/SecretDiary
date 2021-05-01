package kr.ac.konkuk.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    //val을 사용하여 늦은 초기화
    //초기화 블록에 초기화에 필요한 코드 작성
    //처음 호출될 때 초기화 블록의 코드가 실행됨
    //마지막 줄에는 초기화할 값을 명시함
    //MainActivity가 생성될 시점에는 아직 뷰가 그려지지 않음
    //뷰가 다 그려졌다고 알려지는 시점이 OnCreate함수
    //onCreate가 된 이후에 view에 대해서 접근을 해야하기 때문에
    //onCreate이후에 numberPicker1, 2, 3에 접근을 해서
    //lazy하게 초기화를 해줌
    //view에 대해서 apply를 통해 속성을 할당을 해주었는데, init되는 시점이 numberpicker를 사용하는 시점
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
                .apply {
                    minValue = 0
                    maxValue = 9
                }
    }

    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
                .apply {
                    minValue = 0
                    maxValue = 9
                }
    }

    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
                .apply {
                    minValue = 0
                    maxValue = 9
                }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.btn_open)
    }

    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.btn_changePassword)
    }

    private var changePasswordMode = false //비밀번호를 바꾸는 동안 다른 동작을 수행하지 못하도록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {
            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                //이 람다 함수만 return을 하는 것이기 때문에
                return@setOnClickListener
            }
            //저장되어있는 비밀번호와 numberPicker1,2,3에 설정되어있는 숫자와 비교
            //sharedPreference 사용
            //다른앱과 공유 X
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            //sharedPreference에서 데이터를 가져오는 법
            if (passwordPreferences.getString("password", "000").equals(passwordFromUser)) {
                //패스워드 일치(성공)
                startActivity(Intent(this, DiaryActivity::class.java))
            } else {
                //실패
                showErrorAlertDialog()
            }
        }

        changePasswordButton.setOnClickListener {
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if (changePasswordMode) {
                //번호를 저장하는 기능
//                passwordPreferences.edit {
//                    val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
//                    putString("password", passwordFromUser)
//
//                    commit()
//                    // 저장이 될때까지 기다려줌(블락), apply는 비동기적으로 저장(다른 것 먼저 실행되면 그거 실행)
//                    // 시간이 별로 안걸리는 작업이므로 commit()으로
//                }

                //commit를 까먹는 경우가 많아 인자에 commit이 들어오게 됨
                //저장이 될 때까지 UI스레드(메인스레드)를 잠시 중지하고 기다리게됨(저장될 때 까지 화면이 멈춘다는 것을 의미함, 무거운 작업을 할때는 지양 )
                passwordPreferences.edit(true) {
                    putString("password", passwordFromUser)
                }
                //원상복구
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)


            } else {
                //changePasswordMode 가 활성화 :: 비밀번호가 맞는지를 체크
//                val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
//                val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

                if (passwordPreferences.getString("password", "000").equals(passwordFromUser)) {
                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()

                    changePasswordButton.setBackgroundColor(Color.RED)
                } else {
                    //실패
                    showErrorAlertDialog()
                }
            }
        }

    }

    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
                .setTitle("실패!")
                .setMessage("비밀번호가 잘못되었습니다")
                //인자가 하나가 아니므로 명시
                .setPositiveButton("확인") { _, _ -> }
                .create()
                .show()
    }
}
