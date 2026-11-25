package com.group11.cafe.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.group11.cafe.data.model.OrganizationMember
import com.group11.cafe.data.model.User

class OrganizationRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun joinOrganizer(
        inviteCode: String,
        crewId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("users")
            .whereEqualTo("role", "Organizer")
            .whereEqualTo("inviteCode", inviteCode.uppercase())
            .limit(1)
            .get()
            .addOnSuccessListener { organizers ->
                val organizer = organizers
                    .firstOrNull()
                    ?.toObject(User::class.java)

                if (organizer == null) {
                    onFailure(IllegalArgumentException("Invite code was not found."))
                    return@addOnSuccessListener
                }

                val member = mapOf(
                    "organizerId" to organizer.id,
                    "crewId" to crewId
                )

                firestore
                    .collection("organizationMembers")
                    .document("${organizer.id}_$crewId")
                    .set(member, SetOptions.merge())
                    .addOnSuccessListener {
                        onSuccess(organizer)
                    }
                    .addOnFailureListener {
                        onFailure(it)
                    }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getCrewMembers(
        organizerId: String,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("organizationMembers")
            .whereEqualTo("organizerId", organizerId)
            .get()
            .addOnSuccessListener { documents ->
                val members = documents.toObjects(OrganizationMember::class.java)
                loadUsers(
                    userIds = members.map { it.crewId },
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getOrganizersForCrew(
        crewId: String,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("organizationMembers")
            .whereEqualTo("crewId", crewId)
            .get()
            .addOnSuccessListener { documents ->
                val members = documents.toObjects(OrganizationMember::class.java)
                loadUsers(
                    userIds = members.map { it.organizerId },
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getMembershipsForOrganizer(
        organizerId: String,
        onSuccess: (List<OrganizationMember>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("organizationMembers")
            .whereEqualTo("organizerId", organizerId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.toObjects(OrganizationMember::class.java))
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getMembershipsForCrew(
        crewId: String,
        onSuccess: (List<OrganizationMember>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("organizationMembers")
            .whereEqualTo("crewId", crewId)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.toObjects(OrganizationMember::class.java))
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun setTeamName(
        organizerId: String,
        crewId: String,
        teamName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore
            .collection("organizationMembers")
            .document("${organizerId}_$crewId")
            .update("teamName", teamName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    private fun loadUsers(
        userIds: List<String>,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (userIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        val users = mutableListOf<User>()
        var remaining = userIds.size
        var finishedWithError = false

        userIds.forEach { userId ->
            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    document.toObject(User::class.java)?.let {
                        users.add(it)
                    }

                    remaining -= 1
                    if (remaining == 0 && !finishedWithError) {
                        onSuccess(users.sortedBy { it.name })
                    }
                }
                .addOnFailureListener {
                    if (!finishedWithError) {
                        finishedWithError = true
                        onFailure(it)
                    }
                }
        }
    }
}
