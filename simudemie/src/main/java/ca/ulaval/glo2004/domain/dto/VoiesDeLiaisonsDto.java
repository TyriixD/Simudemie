/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;
import ca.ulaval.glo2004.domain.entites.VoiesDeLiaisons;
/**
 *
 * @author Monique
 */
public class VoiesDeLiaisonsDto {
    public Double m_seuil;
    public Double m_tauxTransmission;
    public Double m_tauxAdhesion;;
    public PaysDto m_pays1;
    public PaysDto m_pays2;
    public String m_type;
    public String m_nom;
    public boolean estOuvert;
    public boolean aMesure;
    public Integer ancienAchalandage;

    public VoiesDeLiaisonsDto(VoiesDeLiaisons p_voie) {
        m_pays1 =new PaysDto(p_voie.getPays1());
        m_pays2 = new PaysDto(p_voie.getPays2());
        m_type = p_voie.getType();
        m_nom = p_voie.getNom();
        m_seuil = p_voie.getSeuil();
        estOuvert = p_voie.getEstOuvert();
        aMesure =p_voie.getAUneMesure();
        m_tauxTransmission =p_voie.getTauxTransmission();
        m_tauxAdhesion = p_voie.getTauxAdhesion();
    }
}
