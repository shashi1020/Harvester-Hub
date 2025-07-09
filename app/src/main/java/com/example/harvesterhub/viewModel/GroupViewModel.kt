import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.harvesterhub.data.models.GroupData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GroupViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val allActiveGroups = mutableStateListOf<GroupData>()
    val joinRequests = mutableStateListOf<JoinRequest>()

    init {
        listenToAllActiveGroups()
    }

    private fun listenToAllActiveGroups() {
        firestore.collection("groups")
            .whereEqualTo("status", "open")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("GroupViewModel", "Error fetching groups", err)
                    return@addSnapshotListener
                }
                Log.d("GroupViewModel", "Groups fetched: ${snap?.size()}")
                snap?.documents?.forEach { doc ->
                    Log.d("GroupViewModel", "Group: ${doc.data}")
                }
                allActiveGroups.apply {
                    clear()
                    snap?.documents?.mapNotNull { doc ->
                        val group = doc.toObject(GroupData::class.java)?.copy(id = doc.id)
                        // Optionally filter out groups without createdAt
                        if (group?.createdAt != null) group else null
                    }?.let(::addAll)
                }
            }
    }



    fun createGroup(
        commodity: String,
        quantity: Int,
        location: String,
        onComplete: (Boolean) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onComplete(false)
            return
        }

        val leaderName = user.displayName ?: user.phoneNumber ?: "Unknown"
        val group = mapOf(
            "name" to "$location $commodity Group",
            "leaderUid" to user.uid,
            "leaderName" to leaderName,
            "commodity" to commodity,
            "quantity" to quantity,
            "location" to location,
            "members" to 1,
            "status" to "open",
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("groups")
            .add(group)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { e ->
                Log.e("GroupViewModel", "Failed to create group", e)
                onComplete(false)
            }
    }

    fun requestToJoin(groupId: String, onComplete: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onComplete(false)
            return
        }

        val userName = user.displayName ?: user.phoneNumber ?: "Unknown"
        val request = mapOf(
            "userUid" to user.uid,
            "userName" to userName,
            "requestedAt" to FieldValue.serverTimestamp(),
            "status" to "pending"
        )

        firestore.collection("groups")
            .document(groupId)
            .collection("joinRequests")
            .add(request)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { e ->
                Log.e("GroupViewModel", "Failed to request join", e)
                onComplete(false)
            }
    }

    fun listenJoinRequests(groupId: String) {
        firestore.collection("groups")
            .document(groupId)
            .collection("joinRequests")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("GroupViewModel", "Error listening join requests", err)
                    return@addSnapshotListener
                }
                joinRequests.apply {
                    clear()
                    snap?.documents?.mapNotNull { doc ->
                        doc.toObject(JoinRequest::class.java)?.copy(id = doc.id)
                    }?.let(::addAll)
                }
            }
    }

    fun updateRequestStatus(
        groupId: String,
        requestId: String,
        newStatus: String,
        onComplete: (Boolean) -> Unit
    ) {
        firestore.collection("groups")
            .document(groupId)
            .collection("joinRequests")
            .document(requestId)
            .update("status", newStatus)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { e ->
                Log.e("GroupViewModel", "Failed to update request status", e)
                onComplete(false)
            }
    }
}

data class JoinRequest(
    val id: String = "",
    val userUid: String = "",
    val userName: String = "",
    val requestedAt: Timestamp? = null,
    val status: String = "pending"
)
