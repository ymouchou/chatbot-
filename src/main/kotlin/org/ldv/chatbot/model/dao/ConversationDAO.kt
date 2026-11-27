package org.ldv.chatbot.model.dao

import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.ModeleIA
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationDAO : JpaRepository<Conversation, Long>{
    fun findAllByUtilisateurOrderByDateDerniereModificationDesc(utilisateur: Utilisateur): List<Conversation>

    fun findByIdAndUtilisateur(id:Long,utilisateur: Utilisateur):Conversation?

    fun existsByModeleIA(modeleIA: ModeleIA): Boolean


}

