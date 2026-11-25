package com.group11.cafe.viewmodel

import androidx.lifecycle.ViewModel
import com.group11.cafe.data.model.OrganizationMember
import com.group11.cafe.data.model.User
import com.group11.cafe.data.repository.OrganizationRepository

class OrganizationViewModel : ViewModel() {

    private val repository = OrganizationRepository()

    fun joinOrganizer(
        inviteCode: String,
        crewId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.joinOrganizer(
            inviteCode.trim(),
            crewId,
            onSuccess,
            onFailure
        )
    }

    fun getCrewMembers(
        organizerId: String,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getCrewMembers(
            organizerId,
            onSuccess,
            onFailure
        )
    }

    fun getOrganizersForCrew(
        crewId: String,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getOrganizersForCrew(
            crewId,
            onSuccess,
            onFailure
        )
    }

    fun getMembershipsForOrganizer(
        organizerId: String,
        onSuccess: (List<OrganizationMember>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getMembershipsForOrganizer(
            organizerId,
            onSuccess,
            onFailure
        )
    }

    fun getMembershipsForCrew(
        crewId: String,
        onSuccess: (List<OrganizationMember>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.getMembershipsForCrew(
            crewId,
            onSuccess,
            onFailure
        )
    }

    fun setTeamName(
        organizerId: String,
        crewId: String,
        teamName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.setTeamName(
            organizerId,
            crewId,
            teamName.trim(),
            onSuccess,
            onFailure
        )
    }
}
