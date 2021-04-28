/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;

import ca.ulaval.glo2004.domain.entites.Pays;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author PC
 */
public class PaysDto {
    public Integer m_taillePop;
    public Integer m_nbSaine;
    public Integer m_nbInfecter;
    public Integer m_nbGueris;
    public Integer m_nbDecede;
    public Double m_tauxTransmisionRegion;
    public Color m_couleur;
    public String m_nom;
    public FormeDto m_forme;
    public Point m_positionAjouter;
    public List<RegionDto> m_listeRegion;
    public List<MesureSanitaireDto> m_listeMesureActive;
    
    
    public ArrayList<Integer> statsMalade;
    public ArrayList<Integer> statsGueri;
    public ArrayList<Integer> statsDeces;
    public ArrayList<Integer> statsPop;
    public ArrayList<String> statsMesureEtatActive;
    
    public PaysDto(Pays pays){
        m_taillePop = pays.getTaillePop();
        m_nbSaine = pays.getNbSaine();
        m_nbInfecter = pays.getNbInfecter();
        m_nbGueris = pays.getNbGueris();
        m_nbDecede = pays.getNbDecede();
        m_tauxTransmisionRegion = pays.getTauxTransmissionRegion();
        m_couleur = pays.getCouleur();
        m_nom = pays.getNom();
        m_forme = new FormeDto(pays.getForme());
        m_positionAjouter = pays.getPosition();
        m_listeRegion = pays.getListeRegion().stream().map(m -> new RegionDto(m)).collect(Collectors.toList());
        m_listeMesureActive = pays.getListeMesureActive().stream().map(m -> new MesureSanitaireDto(m)).collect(Collectors.toList());
        
        statsMalade = pays.getArrayMalade();
        statsGueri = pays.getArrayGuerie();
        statsDeces = pays.getArrayDeces();
        statsPop = pays.getArrayPopTot();
        statsMesureEtatActive =pays.getArrayMesureActive();
    }
}


