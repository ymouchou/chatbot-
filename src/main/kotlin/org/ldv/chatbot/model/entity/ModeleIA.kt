package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "modele_ia")
class ModeleIA(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null,

    @Column(nullable = false)
    var nom: String,

    var urlAPI: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    var cheminLogo: String? = null,

    @Column(nullable = false)
    var actif: Boolean = true,

    // Relation ManyToOne vers FournisseurIA
    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    var fourniseurIA: FournisseurIA? = null,

    @OneToMany(mappedBy = "modeleIA")
    var conversations: MutableList<Conversation> = mutableListOf()
) {
    constructor() : this(null, "", null, null, null, true, null, mutableListOf())
}