package com.example.harvesterhub.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.harvesterhub.data.models.GroupData
import com.example.harvesterhub.data.models.farmerprofile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FarmerViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _farmerState = MutableStateFlow<farmerprofile?>(null)
    val farmerState: StateFlow<farmerprofile?> = _farmerState

    // Holds the groups this farmer has created and are still open
    val myActiveGroups = mutableStateListOf<GroupData>()

    init {
        // When user signs in, automatically fetch farmer profile & active groups
        auth.currentUser?.uid?.let { uid ->
            fetchFarmerByPhone(uid)
            listenActiveGroups(uid)
        }
    }

    fun fetchFarmerByPhone(uid: String) {
        if (_farmerState.value != null) return // Avoid re-fetching
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val farmer = farmerprofile(
                        fullName = doc.getString("fullName").orEmpty(),
                        district = doc.getString("district").orEmpty(),
                        state = doc.getString("state").orEmpty()
                    )
                    _farmerState.value = farmer
                } else {
                    _farmerState.value = null
                }
            }
            .addOnFailureListener {
                _farmerState.value = null
            }
    }

    /**
     * Listens in real-time for groups where this farmer is the leader and status is open.
     */
    private fun listenActiveGroups(uid: String) {
        db.collection("groups")
            .whereEqualTo("leaderUid", uid)
            .whereEqualTo("status", "open")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) return@addSnapshotListener
                myActiveGroups.apply {
                    clear()
                    snap?.documents?.mapNotNull { doc ->
                        doc.toObject(GroupData::class.java)?.copy(id = doc.id)
                    }?.let(::addAll)
                }
            }
    }
}
