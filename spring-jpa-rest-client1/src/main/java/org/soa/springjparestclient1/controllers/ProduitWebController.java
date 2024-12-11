package org.soa.springjparestclient1.controllers;

import org.soa.springjparestclient1.services.ProduitClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import soa.entities.Produit;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/produits")
public class ProduitWebController {
    private final ProduitClientService produitClientService;

    public ProduitWebController(ProduitClientService produitClientService) {
        this.produitClientService = produitClientService;
    }

    // Conversion de la date
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new org.springframework.beans.propertyeditors.CustomDateEditor(dateFormat, true));
    }

    // Afficher la liste des produits
    @GetMapping
    public String listArticles(Model model) {
        model.addAttribute("produits", produitClientService.getAllProduits());
        return "produits/list"; // La vue list.html
    }

    // Afficher le formulaire pour créer un nouveau produit
    // Afficher le formulaire pour créer un nouveau produit
    @GetMapping("/add")
    public String createArticleForm(Model model) {
        model.addAttribute("produit", new Produit());
        return "produits/form"; // La vue form.html pour la création
    }
    // Enregistrer un nouveau produit
    @PostMapping("/add")
    public String saveArticle(@ModelAttribute Produit produit) {
        produitClientService.createProduit(produit);
        return "redirect:/produits"; // Rediriger vers la liste des produits après création
    }

    // Afficher le formulaire pour modifier un produit existant
    @GetMapping("/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Produit produit = produitClientService.getProduitById(id);
        if (produit == null) {
            model.addAttribute("error", "Produit non trouvé");
            return "redirect:/produits"; // Rediriger si le produit n'existe pas
        }
        model.addAttribute("produit", produit);
        return "produits/form"; // La vue form.html pour l'édition
    }

    // Mettre à jour un produit existant
    @PutMapping("/{id}")
    public String updateArticle(@ModelAttribute Produit produit, @PathVariable Long id, Model model) {
        try {
            produit.setId(id);  // S'assurer que l'ID est bien passé dans le produit
            produitClientService.updateProduit(produit);
            return "redirect:/produits"; // Rediriger vers la liste des produits après mise à jour
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la mise à jour du produit.");
            return "produits/form"; // Retourner au formulaire en cas d'erreur
        }
    }

    // Supprimer un produit
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id, Model model) {
        try {
            produitClientService.deleteProduit(id);
            model.addAttribute("message", "Produit supprimé avec succès");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression du produit.");
        }
        return "redirect:/produits"; // Rediriger vers la liste des produits après suppression
    }
}
