package com.example.incomingdocumentprocessingsystem.utilits

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.incomingdocumentprocessingsystem.MainActivity
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.database.NODE_USERS
import com.example.incomingdocumentprocessingsystem.database.REF_DATABASE_ROOT
import com.example.incomingdocumentprocessingsystem.database.updatePhonesToDatabase
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun showToast(massage: String) {
    Toast.makeText(APP_ACTIVITY, massage, Toast.LENGTH_SHORT).show()

}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack)
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment).commit()
    else APP_ACTIVITY.supportFragmentManager.beginTransaction()
        .replace(R.id.data_container, fragment).commit()
}


fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String) {
    com.squareup.picasso.Picasso.get()
        .load(url)
        .placeholder(com.example.incomingdocumentprocessingsystem.R.drawable.default_photo)
        .fit()
        .into(this)


}

@SuppressLint("Range")
fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        var arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesToDatabase(arrayContacts)
    }

}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)

}

@SuppressLint("Range")
fun getFileFromUri(uri: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
    try {
        if (cursor!=null && cursor.moveToFirst())
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))


    } catch(e:Exception) {
        showToast(e.message.toString())

    }finally {
        cursor?.close()
        return result
    }
}

fun getPlurals(count:Int) = APP_ACTIVITY.resources.getQuantityString(
    R.plurals.count_members,count,count

)

@SuppressLint("SimpleDateFormat")
fun dateFormatter(milliseconds: String): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(milliseconds.toLong())).toString()
}
fun openDocument(name: String?) {
    val intent = Intent(Intent.ACTION_VIEW)
    val file = File(name)
    val contentUri = FileProvider.getUriForFile(APP_ACTIVITY, "xxx.fileprovider", file)
    val extension = MimeTypeMap.getFileExtensionFromUrl(contentUri.toString())
    val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    if (extension.equals("", ignoreCase = true) || mimetype == null) {
        // if there is no extension or there is no definite mimetype, still try to open the file
        intent.setDataAndType(contentUri, "text/*")
    } else {
        intent.setDataAndType(contentUri, mimetype)
    }
    // custom message for the intent
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    ContextCompat.startActivity(
        APP_ACTIVITY,
        Intent.createChooser(intent, "Choose an Application:"),
        null
    )
}

