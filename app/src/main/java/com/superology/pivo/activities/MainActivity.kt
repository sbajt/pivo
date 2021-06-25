package com.superology.pivo.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.superology.pivo.R
import com.superology.pivo.firebase.database.DatabaseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private val TAG = MainActivity::class.java.canonicalName
    private val inputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        initViews()
        observeData()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun initViews() {
        textView?.text = ""
        inputView?.addTextChangedListener { t ->
            buttonView?.isEnabled = !t.isNullOrBlank() && t != textView?.text
        }
        buttonView?.run {
            isEnabled = false
            setOnClickListener { onSendButtonClick(inputView?.text?.toString()) }
        }
        loadingView?.visibility = View.INVISIBLE
    }

    private fun observeData() {
        disposable.add(DatabaseService.observeData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                textView?.text = it
                buttonView?.setText(R.string.label_button_send)
                inputView?.text?.clear()
                loadingView?.visibility = View.INVISIBLE
            }) { Log.e(TAG, it.message.toString(), it) })
    }

    private fun onSendButtonClick(inputText: String?) {
        if (inputText != null && inputText.isNotBlank()) {
            loadingView?.visibility = View.VISIBLE
            buttonView?.run {
                this.text = ""
                isEnabled = false
            }
            inputMethodManager?.hideSoftInputFromWindow(inputView?.windowToken, 0)
            DatabaseService.setMessage(this, inputText)
        }
    }
}