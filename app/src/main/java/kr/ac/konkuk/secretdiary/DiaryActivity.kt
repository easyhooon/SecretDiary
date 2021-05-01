package kr.ac.konkuk.secretdiary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    //    private val diaryEditText: EditText by lazy {
//        findViewById<EditText>(R.id.diaryEditText)
//    }
    private val handler = Handler(Looper.getMainLooper()) //MainLooper -> 메인스레드에 연결

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val diaryEditText = findViewById<EditText>(R.id.diaryEditText)

        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreferences.getString("detail", ""))

        //잠깐 멈칫했을때 저장하기 위한 스레드
        //commit이 들어갈 자리를 비우면 자동으로 commit=false -> apply (비동기식 저장) 이게 스레드에 더 잘 어울림(적합함)
        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())
            }

            Log.d("DiaryActivity", "Save ${diaryEditText.text.toString()}")
        }

        /*
        val t = Thread(Runnable {
            //여기서 선언한 스레드 내부에서는 메인스레드로의 접근이 불가능함

            //메인스레드를 잠깐 블록으로 열어주는 기능
            runOnUiThread {
                //이 안에서만 UI작업을 수행해야함 (변경된 것을 update하거나 등등), 바깥에서 ui작업을 하면 에러가 발생함, 앱이 죽음
                //이 runOnUiThread에서 사용하는 기능이 바로 핸들러
            }

            handler.post{
                //이 post안쪽 블럭을 메인스레드에 던짐으로써 메인스레드에서 실행을 시켜주게되는 것
            }

            Log.d("aa", "aa")
        })
        */


        //text가 change 될 때마다 람다가 실행
        diaryEditText.addTextChangedListener {
//            detailPreferences.edit {
//                putString("detail", diaryEditText.text.toString())
//            }
            //핸들러를 사용 (스레스와 스레드간의 통신을 엮어주는 안드로이드에서 제공하는 기능

            //스레드를 열었을때 UI스레드(메인스레드)가 아닌 새로운 스레드(메인스레드가 아닌 다른 새로운 스레드)를 관리를 할때
            //UI스레드와 새로운스레드를 연결할 필요가 있음(핸들러 사용 이유)
            //메인스레드가 아닌 곳에서는 UI change를 하는 동작을 할 수 없기 때문

            //0.5초 이전에 아직 실행되지 않고 pending 되어있는 runnable이 있다면 지워주기 위한 메소드
            //제일 처음 addTextChangedListener에 들어왔을 때 이전에 있는 runnable이 있다면 지우고 없으면 그냥 냅두고

            Log.d("DiaryActivity", "TextChanged :: $it")
            handler.removeCallbacks(runnable)
            //0.5초에 한번씩 runnable을 실행
            handler.postDelayed(runnable, 500)
        }

    }
}