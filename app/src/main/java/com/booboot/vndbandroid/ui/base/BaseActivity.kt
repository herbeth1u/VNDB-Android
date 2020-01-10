package com.booboot.vndbandroid.ui.base

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.booboot.vndbandroid.model.vndbandroid.EXTRA_ERROR_MESSAGE
import com.booboot.vndbandroid.model.vndbandroid.RESULT_ERROR
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.progress_bar.*

abstract class BaseActivity : AppCompatActivity() {
    open fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    open fun showLoading(loading: Int) {
        progressBar?.visibility = if (loading > 0) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return

        when (resultCode) {
            RESULT_ERROR -> data.getStringExtra(EXTRA_ERROR_MESSAGE)?.let { message ->
                showError(message)
            }
        }
    }
}