package com.example.incomingdocumentprocessingsystem.database

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.incomingdocumentprocessingsystem.R
import com.example.incomingdocumentprocessingsystem.models.CommonModel
import com.example.incomingdocumentprocessingsystem.models.UserModel
import com.example.incomingdocumentprocessingsystem.ui.message_recycler_view.view.MessageView
import com.example.incomingdocumentprocessingsystem.utilits.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance(URL_DATABASE).reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL)
        .setValue(url).addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }

}

inline fun putFileToStorage(
    uriContent: Uri?,
    path: StorageReference,
    crossinline function: () -> Unit
) {
    if (uriContent != null)
        path.putFile(uriContent).addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES)
            .addListenerForSingleValueEvent(AppValueEventListener { it ->
                it.children.forEach { snapshot ->
                    arrayContacts.forEach { contact ->
                        if (snapshot.key == contact.phone) {
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).child(CHILD_ID)
                                .setValue(snapshot.value.toString())
                                .addOnFailureListener { showToast(it.message.toString()) }
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener { showToast(it.message.toString()) }

                        }
                    }
                }

            })
    }

}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }


}

fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {


    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP




    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }


}


fun updateCurrentUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUsername)
        .addOnSuccessListener {
            deleteOldUsername(newUsername)
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

private fun deleteOldUsername(newUsername: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username)
        .removeValue()
        .addOnSuccessListener {

            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            Log.d("MyLog", "${USER.username}__deleteOldUsername__$CURRENT_UID")
            USER.username = newUsername
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())

        }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setFileBioToDatabase(newBio: String, file: MessageView) {
    REF_DATABASE_ROOT.child(NODE_FILES).child(file.id).child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setNameToDataBase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname).addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.fullname = fullname
            APP_ACTIVITY.appDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun sendFile(
    from: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String
) {
    val refDialogUser = "$NODE_FILES"


    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = from
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}


fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    .child(id).push().key.toString()

fun getFileKey() = REF_DATABASE_ROOT.push().key.toString()

fun uploadFileToStorage(
    uriContent: Uri?,
    messageKey: String,
    from: String,
    typeMessage: String,
    filename: String = ""
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES)
        .child(messageKey)

    putFileToStorage(uriContent, path) {
        getUrlFromStorage(path) {
            sendFile(from, it, messageKey, typeMessage, filename)
        }
    }

}

fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$CURRENT_UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_TYPE] = type
    mapUser[CHILD_ID] = id

    mapReceived[CHILD_TYPE] = type
    mapReceived[CHILD_ID] = CURRENT_UID

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived
    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }


}


fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }

}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(CURRENT_UID)
                .removeValue()
                .addOnFailureListener { showToast(it.message.toString()) }
                .addOnSuccessListener { function() }
        }

}

fun createGroupToDatabase(
    nameGroup: String,
    uri: Uri?,
    listItems: List<CommonModel>,
    function: () -> Unit
) {
    val keyGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_FULLNAME] = nameGroup
    mapData[CHILD_PHOTO_URL] = "empty"

    val mapMembers = hashMapOf<String, Any>()
    listItems.forEach {
        mapMembers[it.id] = USER_MEMBER
    }
    mapMembers[CURRENT_UID] = USER_CREATOR


    mapData[NODE_MEMBERS] = mapMembers
    path.updateChildren(mapData)
        .addOnSuccessListener {

            if (uri != Uri.EMPTY) {
                val pathStorage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)
                putFileToStorage(uri, pathStorage) {
                    getUrlFromStorage(pathStorage) { it ->
                        path.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupsToMainList(mapData, listItems) {
                            function()
                        }
                    }
                }
            } else
                addGroupsToMainList(mapData, listItems) {
                    function()
                }

        }
        .addOnFailureListener { showToast(it.message.toString()) }


}

fun addGroupsToMainList(
    mapData: HashMap<String, Any>,
    listItems: List<CommonModel>,
    function: () -> Unit
) {
    val path = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()
    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listItems.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(CURRENT_UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }


}

fun getFullnameForID(id: String, function: (fullname: String) -> Unit) {

    var fullname: String = ""
    if (id.contains("@")) {
        function(id)
    } else {
        REF_DATABASE_ROOT.child(NODE_USERS).child(id)
            .addListenerForSingleValueEvent(AppValueEventListener {
                val user = it.getValue(UserModel::class.java) ?: UserModel()
                fullname = user.fullname
                function(fullname)
            })

    }
}


fun deleteFile(file: MessageView, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_FILES).child(file.id).removeValue().addOnSuccessListener {
        showToast("Файл удален")
        function()
    }.addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteLabelFormFile(id: String, file: MessageView, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_LABELS).child(id).child(NODE_LABELS_OWNERS).child(file.id)
        .removeValue().addOnSuccessListener {
            showToast("Характеристика удалена")
            function()
        }.addOnFailureListener { showToast(it.message.toString()) }

}

fun addLabelToDB(text: String, function: () -> Unit) {

    val labelKey = REF_DATABASE_ROOT.child(NODE_LABELS).push().key.toString()
    val refDialogUser = "$NODE_LABELS"
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_ID] = labelKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FULLNAME] = text

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$labelKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener {
            showToast(it.message.toString())
        }.addOnSuccessListener { function() }


}


fun addTagToDB(text: String, label: CommonModel, function: () -> Unit) {

    val tagKey = REF_DATABASE_ROOT.child(NODE_TAGS).child(label.id).push().key.toString()
    val refDialogUser = "$NODE_TAGS/${label.id}"
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_ID] = tagKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FULLNAME] = text

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$tagKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener {
            showToast(it.message.toString())
        }.addOnSuccessListener { function() }


}

fun labelNeExist(text: String): Boolean {
    var ans = true
    REF_DATABASE_ROOT.child(NODE_LABELS)
        .addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach {
                if (it.child(CHILD_FULLNAME).value == text) {
                    ans = false
                }
            }
        })
    return ans
}

fun deleteLabel(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_LABELS).child(id).removeValue().addOnSuccessListener {
        showToast("Свойство удалено")
        function()
    }.addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteTag(id: String, label: CommonModel, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_TAGS).child(label.id).child(id).removeValue()
        .addOnSuccessListener {
            showToast("Характеристика удалена")
            function()
        }.addOnFailureListener { showToast(it.message.toString()) }
}


fun attachLabelToFile(label: CommonModel, file: MessageView, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_LABELS).child(label.id).setValue(file.id).addOnSuccessListener {
        function()
    }.addOnFailureListener { showToast(it.message.toString()) }
}

fun detachLabelToFile(label: CommonModel, file: MessageView, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_LABELS).child(label.id).child(file.id).removeValue()
        .addOnSuccessListener {
            function()
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun addListLabelToDB(
    listContacts: MutableList<CommonModel>,
    file: MessageView,
    functionFail: () -> Unit,
    functionSuccess: () -> Unit
) {
    listContacts.forEach {
        REF_DATABASE_ROOT.child(NODE_LABELS).child(it.id).child(NODE_LABELS_OWNERS).child(file.id)
            .setValue("")
            .addOnFailureListener {
                showToast(it.message.toString())
                functionFail()
            }
    }
    functionSuccess()
}


fun getBioFile(file: MessageView, function: (fileBio: String) -> Unit) {

    REF_DATABASE_ROOT.child(NODE_FILES).child(file.id).child(CHILD_BIO)
        .addListenerForSingleValueEvent(AppValueEventListener {
            var fileBio = it.getValue<String>(String::class.java).toString()
            function(fileBio)
        })

}

fun getExecutionToDb(file: MessageView, function: (date: String) -> Unit) {
    REF_DATABASE_ROOT.child(NODE_FILES).child(file.id).child(CHILD_PHONE)
        .addListenerForSingleValueEvent(AppValueEventListener {
            var date = it.getValue<String>(String::class.java).toString()
            function(date)
        })

}

fun setExecutionToDb(date: String, file: MessageView, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_FILES).child(file.id).child(CHILD_PHONE).setValue(date)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }

}


fun addTagToFile(file: MessageView, label: CommonModel, tag: CommonModel, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_LABELS).child(label.id).child(NODE_LABELS_OWNERS).child(file.id)
        .setValue(tag.fullname)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}


fun addAttachToDBFinal(file: File, mail: CommonModel, function: () -> Unit) {
    val uri = file.toUri()
    val messageKey = getFileKey()
    val filename = file.name
    uploadFileToStorage(uri, messageKey, mail.from, TYPE_MESSAGE_FILE, filename)
    function()
}

