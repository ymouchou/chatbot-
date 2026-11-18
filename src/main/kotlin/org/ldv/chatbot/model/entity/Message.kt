package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    var contenu: String,

    @Column(nullable = false)
    var dateCreation: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var typeMessage: TypeMessage,

    // Association ManyToOne avec Conversation (c'est le maître de la relation)
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    var conversation: Conversation? = null
) {
    constructor() : this(null, "", LocalDateTime.now(), TypeMessage.UTILISATEUR, null)
}