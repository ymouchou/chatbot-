package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageDAO : JpaRepository<Message, Long> {
}