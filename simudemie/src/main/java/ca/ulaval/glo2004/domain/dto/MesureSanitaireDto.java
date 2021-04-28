/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;
import ca.ulaval.glo2004.domain.entites.MesureSanitaire;
/**
 *
 * @author Monique
 */
public class MesureSanitaireDto {
    public String m_nom;
    public Double m_seuil;
    public boolean estActive;
    public Double m_adhesion;
    public Double m_transmission;
    public Double m_reproduction;

    public MesureSanitaireDto(MesureSanitaire p_mesure) {
        m_nom = p_mesure.getNom();
        m_seuil = p_mesure.getSeuil();
        estActive = p_mesure.getEstActive();
        m_adhesion =p_mesure.getAdhesion();
        m_transmission =p_mesure.getTransmission();
        m_reproduction =p_mesure.getReproduction();
    }
}
