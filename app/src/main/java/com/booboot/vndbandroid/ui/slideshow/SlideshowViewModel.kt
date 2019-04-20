package com.booboot.vndbandroid.ui.slideshow

import android.app.Application
import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.booboot.vndbandroid.App
import com.booboot.vndbandroid.model.vndb.VN
import com.booboot.vndbandroid.model.vndbandroid.FileAction
import com.booboot.vndbandroid.repository.VNRepository
import com.booboot.vndbandroid.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class SlideshowViewModel constructor(application: Application) : BaseViewModel(application) {
    @Inject lateinit var vnRepository: VNRepository

    val vnData: MutableLiveData<VN> = MutableLiveData()
    val fileData: MutableLiveData<FileAction> = MutableLiveData()
    var position: Int = -1
    var vnId: Long = -1

    init {
        (application as App).appComponent.inject(this)
    }

    fun loadVn(force: Boolean = true) = coroutine(DISPOSABLE_VN, !force && vnData.value != null) {
        vnData.value = vnRepository.getItem(this, vnId).await()
    }

    fun downloadScreenshot(bitmap: Bitmap?, action: Int, directory: File) = coroutine(DISPOSABLE_DOWNLOAD_SCREENSHOT) {
        fileData.value = withContext(Dispatchers.Default) {
            bitmap ?: throw Exception("The image is not ready yet.")

            val filename = String.format("IMAGE_%d.jpg", System.currentTimeMillis())
            val file = File(directory, filename)

            file.createNewFile()
            val ostream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream)
            ostream.close()
            FileAction(file, bitmap, action)
        }
    }

    override fun restoreState(state: Bundle?) {
        super.restoreState(state)
        state ?: return
        position = state.getInt(POSITION)
    }

    override fun saveState(state: Bundle) {
        super.saveState(state)
        state.putInt(POSITION, position)
    }

    companion object {
        private const val DISPOSABLE_VN = "DISPOSABLE_VN"
        private const val DISPOSABLE_DOWNLOAD_SCREENSHOT = "DISPOSABLE_DOWNLOAD_SCREENSHOT"
        private const val POSITION = "POSITION"
    }
}