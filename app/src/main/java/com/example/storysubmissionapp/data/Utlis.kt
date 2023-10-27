package com.example.storysubmissionapp.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.storysubmissionapp.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ValidateType {
    const val REQUIRED = 0
    const val MIN_CHAR = 1
    const val EMAIL = 3
}

private const val MAXIMAL_SIZE = 1000000

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val file = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(file)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return file
}

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun ImageView.loadImage(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun EditText.validate(
    name: String = "field",
    validateType: Int,
    msg: String? = null
): Boolean {
    val text = this.text.toString().trim()
    when (validateType) {
        ValidateType.REQUIRED -> {
            if (text.isEmpty()) {
                var errorMessage = msg
                if (errorMessage == null) {
                    errorMessage =
                        context.getString(
                            com.example.storysubmissionapp.R.string.err_msg_required,
                            name
                        )
                }
                this.error = errorMessage
                return false
            }
        }


        ValidateType.EMAIL -> {
            if (!text.contains("@")) {
                var errorMessage = msg
                if (errorMessage == null) {
                    errorMessage =
                        context.getString(com.example.storysubmissionapp.R.string.err_msg_email)
                }
                this.error = errorMessage
                return false
            }
        }
    }
    return true
}

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun AppCompatActivity.showToast(msg: String, isLong: Boolean = false) {
    if (isLong) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}