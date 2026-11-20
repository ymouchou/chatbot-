package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationDAO : JpaRepository<Conversation, Long>{
    fun findAllByOrderByDateCreationDesc(): List<Conversation>
}