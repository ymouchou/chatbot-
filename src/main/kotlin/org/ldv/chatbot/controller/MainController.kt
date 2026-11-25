package org.ldv.chatbot.controller

import org.ldv.chatbot.service.IAServiceFactory
import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Controller
class MainController(
    private val iaServiceFactory: IAServiceFactory,
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO
) {

    @GetMapping("/login")
    fun login(): String {
        return "auth/login"
    }

    // Page d'accueil : liste des conversations
    @GetMapping("/")
    fun accueil(model: Model): String {
        model.addAttribute("conversation", null)
        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateCreationDesc()
        )
        return "utilisateur/chat"
    }

    // Création d'une nouvelle conversation
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String,
        @RequestParam idModele: Long
    ): String {
        val modele = modeleIADAO.findById(idModele)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Modèle IA introuvable"
                )
            }

        val conversation = Conversation(
            titre = prompt.take(50),
            dateCreation = LocalDateTime.now(),

            modeleIA = modele
        )

        val conversationEnregistree = conversationDAO.save(conversation)

        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversationEnregistree
        )

        messageDAO.save(messageUtilisateur)

        try {
            val iaService = iaServiceFactory.getService(modele)
            val reponse = iaService.envoyerPrompt(prompt, modele)

            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversationEnregistree
            )

            messageDAO.save(messageAssistant)
        } catch (e: Exception) {
            println("Erreur lors de l’appel au modèle ${modele.nom} : ${e.message}")
        }

        //conversationEnregistree.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversationEnregistree)

        return "redirect:/conversations/${conversationEnregistree.id}"
    }

    // Affichage d'une conversation existante
    @GetMapping("/conversations/{id}")
    fun afficherConversation(
        @PathVariable id: Long,
        model: Model
    ): String {
        val conversation = conversationDAO.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateCreationDesc()
        )
        model.addAttribute("conversation", conversation)
        model.addAttribute(
            "messages",
            conversation.messages.sortedBy { it.dateCreation }
        )

        return "utilisateur/chat"
    }

    // Envoi d'un nouveau message dans une conversation existante
    @PostMapping("/conversations/{id}/messages")
    fun envoyerMessage(
        @PathVariable id: Long,
        @RequestParam prompt: String
    ): String {
        val conversation = conversationDAO.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversation
        )

        messageDAO.save(messageUtilisateur)

        try {
            val modele = conversation.modeleIA
                ?: throw IllegalStateException("Aucun modèle IA associé à la conversation")
            val iaService = iaServiceFactory.getService(modele)
            val reponse = iaService.envoyerPrompt(prompt, modele)

            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)
        } catch (e: Exception) {
            println("Erreur lors de l'appel à ${conversation.modeleIA?.nom} : ${e.message}")
        }

        //conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/conversations/$id"
    }
}