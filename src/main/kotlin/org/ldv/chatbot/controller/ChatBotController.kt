package org.ldv.chatbot.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatBotController {

    // Route pour la page d'accueil (pointe vers templates/utilisateur/index.html)
    @GetMapping("/")
    fun accueil(): String {
        return "utilisateur/index"
    }

    // Route pour le formulaire de connexion (pointe vers templates/auth/login.html)
    @GetMapping("/login")
    fun login(): String {
        return "auth/login"
    }
}