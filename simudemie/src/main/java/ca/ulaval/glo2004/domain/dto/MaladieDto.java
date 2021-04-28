/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;
import ca.ulaval.glo2004.domain.entites.Maladie;
/**
 *
 * @author Monique
 */
public class MaladieDto {
    
    public String m_nom;
    public Double m_mortalite;
    public Double m_reproduction;
    public Double m_guerison;
    
    public MaladieDto(Maladie p_maladie) {
        m_nom = p_maladie.getNom();
        m_mortalite = p_maladie.getTauxMortalite();
        m_reproduction = p_maladie.getTauxReproduction();
        m_guerison = p_maladie.getTauxGuerison();
    }
}
