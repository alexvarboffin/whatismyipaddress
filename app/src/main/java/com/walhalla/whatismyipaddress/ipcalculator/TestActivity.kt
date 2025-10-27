package com.walhalla.whatismyipaddress.ipcalculator

import android.R
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import com.walhalla.boilerplate.domain.executor.impl.BackgroundExecutor
import com.walhalla.boilerplate.threading.MainThreadImpl.Companion.instance
import com.walhalla.domain.interactors.AdvertInteractor
import com.walhalla.domain.interactors.impl.AdvertInteractorImpl
import com.walhalla.ui.DLog.d
import com.walhalla.whatismyipaddress.TApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создание корневого контейнера LinearLayout
        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL

        // Создание ScrollView
        val scrollView = ScrollView(this)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Добавление ScrollView в корневой LinearLayout
        rootLayout.addView(scrollView)

        // Создание контента для прокручивающейся активности
        val contentLayout = LinearLayout(this)
        contentLayout.orientation = LinearLayout.VERTICAL

        // Добавление текстовых элементов в контент
        for (i in 1..200) {
            val textView = TextView(this)
            textView.setText("@" + i)
            contentLayout.addView(textView)
        }
        scrollView.addView(contentLayout)
        setContentView(rootLayout)

        val content = findViewById<ContentFrameLayout>(R.id.content)
        d("@@@@@@@@@" + content.javaClass.simpleName)

        val o = AdvertInteractorImpl(CoroutineScope(Dispatchers.IO), MainScope(), TApp.repository)

        o.selectView(content, object : AdvertInteractor.Callback<View> {
            override fun onMessageRetrieved(id: Int, message: View) {
                val content = findViewById<ViewGroup?>(R.id.content)

                if (content != null) {
                    try {
                        //content.removeView(message);
                        if (message.parent != null) {
                            (message.parent as ViewGroup).removeView(message)
                        }
                        content.setPadding(0, 0, 0, 50)

                        //ViewGroup.LayoutParams mm = content.getLayoutParams();
                        val params = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.gravity = Gravity.BOTTOM or Gravity.CENTER
                        message.layoutParams = params
                        content.addView(message)
                    } catch (e: Exception) {
                        d("onMessageRetrieved: " + e.message)
                    }
                }
            }

            override fun onRetrievalFailed(error: String) {
            }
        })
    }


    override fun onResume() {
        super.onResume()
    }
}
