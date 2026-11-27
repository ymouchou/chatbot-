package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var titre: String,

    @Column(nullable = false)
    var dateCreation: LocalDateTime,

    @Column(nullable = false)
    var dateDerniereModification: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    var messages: MutableList<Message> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "modele_id")
    var modeleIA: ModeleIA? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    var utilisateur: Utilisateur
) {
    constructor() : this(
        id = null,
        titre = "",
        dateCreation = LocalDateTime.now(),
        dateDerniereModification = LocalDateTime.now(),
        messages = mutableListOf(),
        modeleIA = null,
        utilisateur = Utilisateur(
            id = null,
            email = "",
            prenom = "",
            nom = "",
            motDePasse = "",
            actif = true,
            role = Role(
                id = null,
                nom = "",
                nomLogique = ""
            ),
            conversations = mutableListOf()
        )
    )
}