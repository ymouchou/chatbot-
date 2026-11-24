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

    // Association OneToMany avec Message
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    var messages: MutableList<Message> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "modele_id")
    var modeleIA: ModeleIA? = null
) {
    constructor() : this(
        null,
        "",
        LocalDateTime.now(),
        LocalDateTime.now(),
        mutableListOf(),
        null
    )
}