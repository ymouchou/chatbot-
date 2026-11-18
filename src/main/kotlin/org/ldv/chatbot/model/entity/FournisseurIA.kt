package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "fournisseur_ia")
class FournisseurIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var nom: String,

    var cheminLogo: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    // Relation OneToMany vers ModeleIA
    @OneToMany(mappedBy = "fourniseurIA", cascade = [CascadeType.ALL], orphanRemoval = true)
    var modeles: MutableList<ModeleIA> = mutableListOf()
) {
    // Constructeur vide obligatoire pour JPA
    constructor() : this(null, "", null, null, mutableListOf())
}