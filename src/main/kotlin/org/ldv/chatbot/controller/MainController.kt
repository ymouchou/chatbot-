package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.ldv.chatbot.model.entity.Utilisateur
import org.ldv.chatbot.service.IAServiceFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
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
    private val modeleIADAO: ModeleIADAO,
    private val utilisateurDAO: UtilisateurDAO
) {

    @GetMapping("/login")
    fun login(model: Model): String {
        model.addAttribute("pageConnexion", true)
        return "auth/login"
    }

    // Page d'accueil : liste des conversations
    @GetMapping("/")
    fun accueil(
        model: Model,
        authentication: Authentication
    ): String {
        // Recupere l'utilisateur de la session
        val utilisateur = this.getUtilisateurConnecte(authentication)
        // Ajout de l'utilisateur à la vue
        model.addAttribute("utilisateur", utilisateur)

        model.addAttribute("conversation", null)
        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())
        model.addAttribute(
            "conversations",
            conversationDAO.findAllByUtilisateurOrderByDateDerniereModificationDesc(utilisateur)
        )
        return "utilisateur/chat"
    }

    // Création d'une nouvelle conversation
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String,
        @RequestParam idModele: Long,
        authentication: Authentication
    ): String {
        // Recupere l'utilisateur
        val utilisateur = this.getUtilisateurConnecte(authentication)

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
            modeleIA = modele,
            utilisateur = utilisateur
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
        model: Model,
        authentication: Authentication
    ): String {
        // Recupere l'utilisateur
        val utilisateur = this.getUtilisateurConnecte(authentication)
        model.addAttribute("utilisateur", utilisateur)

        val conversation = conversationDAO
            .findByIdAndUtilisateur(id, utilisateur)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable")

        model.addAttribute(
            "conversations",
            conversationDAO
                .findAllByUtilisateurOrderByDateDerniereModificationDesc(
                    utilisateur
                )
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
        @RequestParam prompt: String,
        authentication: Authentication
    ): String {
        val utilisateur = this.getUtilisateurConnecte(authentication)

        val conversation = conversationDAO
            .findByIdAndUtilisateur(id, utilisateur)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable")

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

    @PostMapping("/conversations/{id}/supprimer")
    fun supprimerConversation(
        @PathVariable id: Long,
        authentication: Authentication
    ): String {
        val utilisateur = this.getUtilisateurConnecte(authentication)
        val conversation = conversationDAO
            .findByIdAndUtilisateur(id, utilisateur)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable")

        conversationDAO.delete(conversation)
        return "redirect:/"
    }

    @PostMapping("/conversations/{id}/renommer")
    fun renommerConversation(
        @PathVariable id: Long,
        @RequestParam titre: String,
        authentication: Authentication
    ): String {
        val utilisateur = this.getUtilisateurConnecte(authentication)
        val conversation = conversationDAO
            .findByIdAndUtilisateur(id, utilisateur)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation introuvable")

        conversation.titre = titre.take(50)
        conversation.dateDerniereModification = LocalDateTime.now()
        conversationDAO.save(conversation)
        return "redirect:/conversations/$id"
    }

    fun getUtilisateurConnecte(authentication: Authentication): Utilisateur {
        val email = authentication.name
        return utilisateurDAO
            .findByEmailIgnoreCase(email)
            ?: throw UsernameNotFoundException("Utilisateur introuvable")
    }
}