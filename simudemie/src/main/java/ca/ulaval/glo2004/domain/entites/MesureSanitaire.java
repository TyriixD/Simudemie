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
public class MesureSanitaire implements Serializable {

    private String m_nom;
    private Double m_seuil;
    private Double m_adhesion;
    private Double m_transmission;
    private Double m_reproduction;
    private boolean estActive;

    public MesureSanitaire(String p_nom, Double p_seuil, Double adhesion, Double transmission, Double reproduction) {
        m_nom = p_nom;
        m_seuil = p_seuil/100;
        m_adhesion = adhesion/100;
        m_transmission=transmission/100;
        m_reproduction=reproduction/100;
    }

    public String getNom() {
        return m_nom;
    }
    public boolean getEstActive()
    {
        return estActive;
    }
    
    public void setEstActive(boolean p_activer)
    {
        estActive = p_activer;
    }

    public void setNom(String p_nom) {
        m_nom = p_nom;
    }

    public Double getSeuil() {
        return m_seuil;
    }

    public void setSeuil(Double p_seuil) {
        m_seuil = p_seuil;
    }
    public Double getAdhesion() {
        return m_adhesion;
    }

    public void setAdhesion(Double p_taux) {
        m_adhesion= p_taux;
    }
    
    public Double getTransmission() {
        return m_transmission;
    }

    public void setTransmission(Double p_taux) {
        m_transmission = p_taux;
    }
    
    public Double getReproduction() {
        return m_reproduction;
    }

    public void setReproduction(Double p_taux) {
        m_reproduction = p_taux;
    }

}