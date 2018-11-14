package com.flayone.trapezoidbuttons

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        irr.setOnSelectedListener(object : IrregularButton.BaseBooleanListener {
            override fun isLeftClick(i: Boolean) {
                if (i) {
                    toast("left")
                } else {
                    toast("right")
                }
            }
        })
        btn1.onClick {
            turnLeftOn(true)
        }
        btn2.onClick {
            turnLeftOn(false)
        }
    }

    private fun turnLeftOn(b: Boolean) {
        irr.setLeftSelected(b)
        irr1.setLeftSelected(b)
        irr2.setLeftSelected(b)
    }
}
