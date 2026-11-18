package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ModeleIADAO : JpaRepository<ModeleIA, Long> {
}