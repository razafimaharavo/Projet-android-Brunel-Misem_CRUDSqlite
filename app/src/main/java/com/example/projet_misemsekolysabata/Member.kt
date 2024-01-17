package com.example.projet_misemsekolysabata

data class Member(
    var id: Long = -1, // Utilisé pour les opérations de mise à jour
    var name: String,
    var gender: String,
    var dateOfBirth: String
)