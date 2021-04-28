/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.entites;

import java.io.Serializable;

/**
 *
 * @author Monique
 */
public class Maladie implements Serializable {

    private String m_nom;
    private double m_mortalite;
    private double m_guerison;
    private double m_reproduction;

    public Maladie(String nomMaladie, double p_mortalite, double p_reproduction, double p_guerison) {
        m_nom = nomMaladie;
        m_mortalite = p_mortalite / 100;
        m_guerison = p_guerison / 100;
        m_reproduction = p_reproduction;
    }

    public String getNom() {
        return m_nom;
    }

    public void setNom(String p_nom) {
        m_nom = p_nom;
    }

    public Double getTauxMortalite() {
        return m_mortalite;
    }

    public void setTauxMortalite(double p_taux) {
        m_mortalite = (double) p_taux / 100;
    }


    public Double getTauxGuerison() {
        return m_guerison;
    }

    public void setTauxGuerison(double p_taux) {
        m_guerison = (double) p_taux / 100;
    }
    public Double getTauxReproduction(){
        return m_reproduction;
    }
    
    public void setTauxReproduction(double p_taux){
        m_reproduction = p_taux;
    }
    
    public double reductionTauxReproduction(double tauxReproduction,double p_tauxReduction){
        double tauxReproductionInitial = tauxReproduction;
        double tauxReproductionReduit = tauxReproductionInitial - (tauxReproductionInitial*p_tauxReduction);
        return tauxReproductionReduit;
    }
    
    public double reductionTauxTransmission(double tauxTransmission, double tauxReductionTransmission){
        double tauxTransmissionReduit = tauxTransmission - (tauxTransmission * tauxReductionTransmission);
        return tauxTransmissionReduit;
    }
    
    
    

}
