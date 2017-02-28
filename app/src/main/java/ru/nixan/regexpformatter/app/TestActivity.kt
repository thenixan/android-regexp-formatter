package ru.nixan.regexpformatter.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import ru.nixan.regexpformatter.RegExpFormatter

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val regexpFormatter1 = RegExpFormatter("\\d{1,3}wasd")
        (findViewById(R.id.input1) as TextView?)?.addTextChangedListener(regexpFormatter1)
        (findViewById(R.id.input1) as TextView?)?.inputType = regexpFormatter1.inputType
    }
}
