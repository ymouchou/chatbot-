package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.UtilisateurDAO
import org.ldv.chatbot.model.entity.Utilisateur
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.server.ResponseStatusException

@Controller
class ChatBotController(
    private val utilisateurDAO: UtilisateurDAO
) {

    // Route pour la page d'accueil (pointe vers templates/utilisateur/index.html)
    /*@GetMapping("/")
    fun accueil(): String {
        return "utilisateur/index"
    }*/

    // Route pour le formulaire de connexion (pointe vers templates/auth/login.html)
    /*@GetMapping("/login")
    fun login(): String {
        return "auth/login"
    }*/

    fun getUtilisateurConnecte(authentication: Authentication): Utilisateur {
        val email = authentication.name
        val utilisateur = utilisateurDAO
            .findByEmailIgnoreCase(email)
            ?: throw UsernameNotFoundException("Utilisateur introuvable")
        return utilisateur
    }
}