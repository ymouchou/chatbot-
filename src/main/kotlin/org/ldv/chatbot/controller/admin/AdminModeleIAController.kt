package org.ldv.chatbot.controller.admin

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.FournisseurIADAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/modeles-ia")
class AdminModeleIAController(
    private val modeleIADAO: ModeleIADAO,
    private val fourniseurIADAO: FournisseurIADAO,
    private val conversationDAO: ConversationDAO
) {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("modelesIA", modeleIADAO.findAll())
        return "admin/modeleIA/index"
    }

    @GetMapping("/create")
    fun create(model: Model): String {
        model.addAttribute("fournisseursIA", fourniseurIADAO.findAll())
        return "admin/modeleIA/create"
    }

    @PostMapping
    fun store(
        @RequestParam nom: String,
        @RequestParam urlAPI: String,
        @RequestParam description: String,
        @RequestParam cheminLogo: String,
        @RequestParam fourniseurIAId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        if (nom.isBlank() || urlAPI.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Le nom et l'URL sont obligatoires")
        }
        val fourniseurIA = fourniseurIADAO.findById(fourniseurIAId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Fournisseur IA introuvable")
            }
        val modeleIA = ModeleIA(
            nom = nom,
            urlAPI = urlAPI,
            description = description,
            cheminLogo = cheminLogo,
            fourniseurIA = fourniseurIA
        )
        modeleIADAO.save(modeleIA)
        redirectAttributes.addFlashAttribute(
            "succes", "Le modèle IA a été créé avec succès.")
        return "redirect:/admin/modeles-ia"
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND,
                "Modèle IA introuvable")
        }
        model.addAttribute("modeleIA", modeleIA)
        return "admin/modeleIA/show"
    }

    @GetMapping("/{id}/edit")
    fun edit(@PathVariable id: Long, model: Model): String {
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND,
                "Modèle IA introuvable")
        }
        model.addAttribute("modeleIA", modeleIA)
        model.addAttribute("fourniseursIA", fourniseurIADAO.findAll())
        return "admin/modeleIA/edit"
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestParam nom: String,
        @RequestParam urlAPI: String,
        @RequestParam description: String,
        @RequestParam cheminLogo: String,
        @RequestParam fourniseurIAId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        if (nom.isBlank() || urlAPI.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Le nom et l'URL sont obligatoires")
        }
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND,
                "Modèle IA introuvable")
        }
        val fourniseurIA = fourniseurIADAO.findById(fourniseurIAId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Fournisseur IA introuvable")
            }
        modeleIA.nom = nom
        modeleIA.urlAPI = urlAPI
        modeleIA.description = description
        modeleIA.cheminLogo = cheminLogo
        modeleIA.fourniseurIA = fourniseurIA
        modeleIADAO.save(modeleIA)
        redirectAttributes.addFlashAttribute(
            "succes", "Le modèle IA a été modifié avec succès.")
        return "redirect:/admin/modeles-ia"
    }

    @DeleteMapping("/{id}")
    fun destroy(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val modeleIA = modeleIADAO.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND,
                "Modèle IA introuvable")
        }
        if (conversationDAO.existsByModeleIA(modeleIA)) {
            redirectAttributes.addFlashAttribute("erreur",
                "Ce modèle IA est utilisé dans une conversation.")
        } else {
            try {
                modeleIADAO.delete(modeleIA)
                redirectAttributes.addFlashAttribute("succes",
                    "Le modèle IA a été supprimé avec succès.")
            } catch (exception: DataIntegrityViolationException) {
                redirectAttributes.addFlashAttribute("erreur",
                    "Ce modèle IA ne peut pas être supprimé.")
            }
        }
        return "redirect:/admin/modeles-ia"
    }




}
