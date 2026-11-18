package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.FournisseurIA
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FournisseurIADAO : JpaRepository<FournisseurIA, Long> {
}