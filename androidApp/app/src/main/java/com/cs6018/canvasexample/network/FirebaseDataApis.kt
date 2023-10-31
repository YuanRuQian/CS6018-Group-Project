package com.cs6018.canvasexample.network

import android.graphics.Bitmap
import android.util.Log
import com.cs6018.canvasexample.utils.getCurrentUserId
import com.cs6018.canvasexample.utils.sortDrawingsByLastModifiedDate
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.Date

data class UserDrawing(
    val id: String,
    val creatorId: String,
    val title: String,
    val imagePath: String,
    val thumbnail: String,
    val lastModifiedDate: Date,
    val createdDate: Date,
)

fun resultToUserDrawing(result: DocumentSnapshot): UserDrawing {
    return UserDrawing(
        result.id,
        result.data?.get("creatorId") as String,
        result.data?.get("title") as String,
        result.data?.get("imagePath") as String,
        result.data?.get("thumbnail") as String,
        (result.data?.get("lastModifiedDate") as Timestamp).toDate(),
        (result.data?.get("createdDate") as Timestamp).toDate(),
    )
}


fun addNewUser(user: FirebaseUser) {
    val db = Firebase.firestore
    val userObject = hashMapOf(
        "email" to user.email,
    )
    // Add a new document with a generated ID
    db.collection("users").document(user.uid)
        .set(userObject)
        .addOnSuccessListener {
            Log.d("addNewUser", "New user added with ID: ${user.uid}")
        }
        .addOnFailureListener { e ->
            Log.w("addNewUser", "Error adding document", e)
        }
}

fun addNewDrawing(
    title: String,
    imagePath: String,
    thumbnail: String,
    onSuccess: () -> Unit
) {
    val creatorId = getCurrentUserId()
    val db = Firebase.firestore
    val drawingObject = hashMapOf(
        "creatorId" to creatorId,
        "title" to title,
        "lastModifiedDate" to FieldValue.serverTimestamp(),
        "createdDate" to FieldValue.serverTimestamp(),
        "imagePath" to imagePath,
        "thumbnail" to thumbnail,
    )

    // add new drawing to the public feed collection
    db.collection("feeds")
        .add(drawingObject)
        .addOnSuccessListener { documentReference ->
            Log.d(
                "addNewDrawing",
                "DocumentSnapshot added with ID: ${documentReference.id} in public feeds collection"
            )


            // add the new drawing's id to the user's drawing collection
            db.collection("drawings")
                .document(creatorId)
                .collection("userDrawings")
                .document(documentReference.id)
                .set(drawingObject)
                .addOnSuccessListener {
                    onSuccess()
                    Log.d(
                        "addNewDrawing",
                        "New drawing added with ID: ${documentReference.id} in user drawings collection"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("addNewDrawing", "Error adding document in user drawings collection", e)
                }
        }
        .addOnFailureListener { e ->
            Log.w("addNewDrawing", "Error adding document in public feeds collection", e)
        }
}


fun updateDrawingInfo(
    drawingId: String,
    title: String,
    imagePath: String,
    thumbnail: String,
    onSuccess: () -> Unit
) {
    val creatorId = getCurrentUserId()
    val db = Firebase.firestore
    val userDrawingRef =
        db.collection("drawings").document(creatorId).collection("userDrawings").document(drawingId)

    val updates = hashMapOf(
        "title" to title,
        "imagePath" to imagePath,
        "thumbnail" to thumbnail,
        "lastModifiedDate" to FieldValue.serverTimestamp(),
    )

    // update the drawing in the user's drawing collection
    userDrawingRef
        .update(updates)
        .addOnSuccessListener {
            Log.d(
                "updateDrawingInfo",
                "Drawing updated with ID: $drawingId in user drawings collection"
            )

            // update the drawing in the public feeds collection
            val feedRef = db.collection("feeds").document(drawingId)
            feedRef
                .update(updates)
                .addOnSuccessListener {
                    Log.d(
                        "updateDrawingInfo",
                        "Drawing updated with ID: $drawingId in public feeds collection"
                    )
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.w(
                        "updateDrawingInfo",
                        "Error updating document in public feeds collection",
                        e
                    )
                }
        }
        .addOnFailureListener { e ->
            Log.w("updateDrawingInfo", "Error updating document in user drawings collection", e)
        }


}

fun getCurrentUserDrawings(onSuccess: (List<UserDrawing>) -> Unit) {
    val userId = getCurrentUserId()
    val db = FirebaseFirestore.getInstance()

    db.collection("drawings")
        .document(userId)
        .collection("userDrawings")
        .get()
        .addOnSuccessListener { result ->
            Log.d("getCurrentUserDrawings", "Current user's drawings: ${result.size()}")

            val list = mutableListOf<UserDrawing>()
            for (document in result) {
                list.add(
                    resultToUserDrawing(document)
                )
            }

            onSuccess(sortDrawingsByLastModifiedDate(list))
        }
        .addOnFailureListener { exception ->
            Log.w("getCurrentUserDrawings", "Error getting documents.", exception)
        }
}

fun getPublicFeed(onSuccess: (List<UserDrawing>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("feeds")
        .get()
        .addOnSuccessListener { result ->
            Log.d("getPublicFeed", "Public feed: ${result.size()}")

            val list = mutableListOf<UserDrawing>()
            for (document in result) {
                list.add(
                    resultToUserDrawing(document)
                )
            }

            onSuccess(sortDrawingsByLastModifiedDate(list))
        }
        .addOnFailureListener { exception ->
            Log.w("getPublicFeed", "Error getting documents.", exception)
        }
}

fun getDrawingByDrawingId(
    drawingId: String,
    onSuccess: (UserDrawing) -> Unit,
    onError: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var drawing: UserDrawing?
    db.collection("feeds")
        .document(drawingId)
        .get()
        .addOnSuccessListener { result ->
            Log.d("getDrawingByDrawingId", "${result.id} => ${result.data}")
            drawing = resultToUserDrawing(result)
            onSuccess(drawing!!)
        }
        .addOnFailureListener { exception ->
            Log.w("getDrawingByDrawingId", "Error getting document by id $drawingId", exception)
            onError()
        }
}

fun deleteDrawingByDrawingId(drawingId: String, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val userId = getCurrentUserId()

    getDrawingByDrawingId(drawingId, onSuccess = {
        val imagePathInCloudStorage = it.imagePath
        val imagePathInCloudStorageRef =
            Firebase.storage.reference.child(imagePathInCloudStorage)
        // delete the drawing in the user's drawing collection
        db.collection("drawings")
            .document(userId)
            .collection("userDrawings")
            .document(drawingId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    "deleteDrawingById",
                    "Drawing deleted with ID: $drawingId in user drawings collection"
                )
                // delete the drawing in the public feeds collection
                db.collection("feeds")
                    .document(drawingId)
                    .delete()
                    .addOnSuccessListener {
                        onSuccess()
                        Log.d(
                            "deleteDrawingById",
                            "Drawing deleted with ID: $drawingId in public feeds collection"
                        )
                        // TODO: delete the drawing referenced image in the cloud storage by imagePathInCloudStorage
                        imagePathInCloudStorageRef.delete()
                            .addOnSuccessListener {
                                Log.d(
                                    "deleteDrawingById",
                                    "Drawing image deleted with ID: $drawingId in cloud storage"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "deleteDrawingById",
                                    "Error deleting drawing image with ID: $drawingId in cloud storage",
                                    e
                                )
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("deleteDrawingById", "Error deleting document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("deleteDrawingById", "Error deleting document", e)
            }
    }, onError = {
        Log.d("deleteDrawingByDrawingId", "Failed to get drawing by id $drawingId")
    })
}

fun uploadImageToCloudStorage(
    bitmap: Bitmap,
    onSuccess: (String, Bitmap) -> Unit,
    onError: () -> Unit
) {
    val storage = Firebase.storage
    val storageRef = storage.reference

    val imagePath = "images/${getCurrentUserId()}/${System.currentTimeMillis()}.jpg"
    val imageRef = storageRef.child(imagePath)

    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val data = byteArrayOutputStream.toByteArray()

    val uploadTask = imageRef.putBytes(data)
    uploadTask.addOnFailureListener {
        // Handle unsuccessful uploads
        Log.d("uploadImageToCloudStorage", "Failed to upload image")
        onError()
    }.addOnSuccessListener { taskSnapshot ->
        Log.d("uploadImageToCloudStorage", "Successfully uploaded image")
        onSuccess(taskSnapshot.metadata?.path ?: "", bitmap)
    }
}

fun overwriteImageToCloudStorage(
    bitmap: Bitmap,
    imagePath: String,
    onSuccess: (Bitmap) -> Unit,
    onError: () -> Unit
) {
    val storage = Firebase.storage
    val storageRef = storage.reference

    val imageRef = storageRef.child(imagePath)

    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val data = byteArrayOutputStream.toByteArray()

    val uploadTask = imageRef.putBytes(data)
    uploadTask.addOnFailureListener {
        // Handle unsuccessful uploads
        Log.d("overwriteImageToCloudStorage", "Failed to upload image")
        onError()
    }.addOnSuccessListener {
        Log.d("overwriteImageToCloudStorage", "Successfully uploaded image")
        onSuccess(bitmap)
    }
}