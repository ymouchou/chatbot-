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

    // Association OneToMany avec Message
    @OneToMany(mappedBy = "conversation", cascade = [CascadeType.ALL], orphanRemoval = true)
    var messages: MutableList<Message> = mutableListOf()
) {
    constructor() : this(null, "", LocalDateTime.now(), mutableListOf())
}