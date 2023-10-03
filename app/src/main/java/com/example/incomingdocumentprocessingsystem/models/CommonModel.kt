package com.example.incomingdocumentprocessingsystem.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "https://firebasestorage.googleapis.com/v0/b/incoming-document.appspot.com/o/profile_image%2Fdefault-profile-icon-24.jpg?alt=media&token=e02011b0-982c-4ec1-81d3-59361263b377",

    var admin: Boolean = false,
    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl: String = "empty",
    var lastMessage: String = "",
    var access:String=""
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}
