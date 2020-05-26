package com.example.todolist

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance() //할일 데이터 가져오기 위한 작업

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //전체 할 일 정보를 가져와서 날짜기준 내림차순으로 정렬
        val realmResult = realm.where<Todo>().findAll().sort("date",Sort.DESCENDING)

        val adapter = TodoListAdapter(realmResult)
        listView.adapter = adapter

        //data modify apply
        realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }


        listView.setOnItemClickListener { parent, view, position, id ->
            startActivity<EditActivity>("id" to id)

        }


        fab.setOnClickListener {

            startActivity<EditActivity>()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
