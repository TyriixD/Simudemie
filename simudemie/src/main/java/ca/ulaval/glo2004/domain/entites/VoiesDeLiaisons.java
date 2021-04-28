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
public class VoiesDeLiaisons implements Serializable {
    private Double m_seuil;
    private Double m_tauxTransmission;
    private Double m_tauxAdhesion;
    private Pays m_pays1;
    private Pays m_pays2;
    private String m_type;
    private String m_nom;
    private boolean estOuvert;
    private boolean aUneMesure;
    

    public VoiesDeLiaisons(Pays p_pays1, Pays p_pays2, double p_seuil, double p_transmission, double p_adhesion, String p_type, boolean aMesure) {
        m_pays1 = p_pays1;
        m_pays2 = p_pays2;
        m_seuil = p_seuil/100;
        m_tauxTransmission = p_transmission/250;
        m_tauxAdhesion = p_adhesion/100;
        m_type = p_type;
        m_nom = (p_pays1.getNom() + " - " + p_type + " - " + p_pays2.getNom());
        estOuvert= true;
        aUneMesure = aMesure;
    }

    public String getNom() {
        return m_nom;
    }
    
    public Boolean getEstOuvert() {
        return estOuvert;
    }
    public void setEstOuvert(boolean p_ouvert) {
        estOuvert = p_ouvert;
    }
    
    public Boolean getAUneMesure() {
        return aUneMesure;
    }
    
    public void setAUneMesure(boolean p_AMesure) {
        aUneMesure = p_AMesure;
    }
    
    public void setNom(String p_nom) {
        m_nom = p_nom;
    }

    public Double getSeuil() {
        return m_seuil;
    }

    public Double getTauxTransmission() {
        return m_tauxTransmission;
    }
    
    public Double getTauxAdhesion() {
        return m_tauxAdhesion;
    }

    public Pays getPays1() {
        return m_pays1;
    }

    public Pays getPays2() {
        return m_pays2;
    }

    public String getType() {
        return m_type;
    }

    public void setTauxTransmission(double p_taux) {
        m_tauxTransmission = p_taux / 2.5;
    }
    
    public void setTauxAdhesion(double p_taux) {
        m_tauxAdhesion = p_taux;
    }

    public void setSeuil(Double p_seuil) {
        m_seuil = p_seuil;
    }

    public void setPays1(Pays p_pays1) {
        m_pays1 = p_pays1;
    }

    public void setPays2(Pays p_pays2) {
        m_pays2 = p_pays2;
    }

    public void setType(String p_type) {
        m_type = p_type;
    }

}
