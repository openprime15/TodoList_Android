package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.util.*

class EditActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance() //인스턴스 얻기

    val calender:Calendar = Calendar.getInstance() //캘린더 객체 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //업데이트 조건
        val id = intent.getLongExtra("id", -1L)
        if(id == -1L){
            insertMode()
        }else{
            updateMode(id)
        }

        //캘린더 뷰의 날짜를 선택했을 때 Calendar 객체에 설정
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR, year)
            calender.set(Calendar.MONTH, month)
            calender.set(Calendar.DAY_OF_YEAR, dayOfMonth)
        }
    }

    //추가 모드 설정
    private fun insertMode(){
        //삭제버튼 숨김
        deleteFab.visibility = View.GONE

        //완료버튼 클릭시 추가함수 작동
        doneFab.setOnClickListener {
            insertTodo()
        }

    }
    //수정 모드 초기화
    private fun updateMode(id: Long){
        //id에 해당하는 객체를 화면에 표시
        val todo = realm.where<Todo>().equalTo("id", id).findFirst()!!
        todoEditText.setText(todo.title)
        calendarView.date = todo.date

        //완료 버튼 클릭하면 수정
        doneFab.setOnClickListener {
            updateTodo(id)
        }
        //삭제버튼 클릭하면 삭제
        deleteFab.setOnClickListener {
            deleteTodo(id)
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        realm.close() //인스턴스 해제
    }



    private fun insertTodo(){
        realm.beginTransaction() //트랜잭션 시작

        val newItem = realm.createObject<Todo>(nextId()) //새 객체 생성

        //값 설정
        newItem.title = todoEditText.text.toString()
        newItem.date = calender.timeInMillis

        realm.commitTransaction() //트랜잭션 종료 반영

        alert("내용이 추가되었습니다."){
            yesButton{finish()}
        }.show()

    }
    //다음 id를 반환
    private fun nextId(): Int {
        val maxId = realm.where<Todo>().max("id")
        if(maxId != null){
            return maxId.toInt() + 1
        }
        return 0
    }

    //할일 수정 메서드 작성
    private fun updateTodo(id:Long){
        realm.beginTransaction() //트랜잭션 시작

        val updateItem = realm.where<Todo>().equalTo("id", id).findFirst()!!

        updateItem.title = todoEditText.text.toString()
        updateItem.date = calender.timeInMillis

        realm.commitTransaction() //트랜잭션 종료

        alert("내용이 변경되었습니다."){
            yesButton{finish()}
        }.show()

    }

    //할일 삭제 메서드 작성
    private fun deleteTodo(id:Long){
        realm.beginTransaction() //트랜잭션 시작

        val deleteItem = realm.where<Todo>().equalTo("id", id).findFirst()!!

        //삭제
        deleteItem.deleteFromRealm()
        realm.commitTransaction()

        alert("내용이 삭제되었습니다."){
            yesButton{finish()}
        }.show()

    }



}
