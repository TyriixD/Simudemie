/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;
import ca.ulaval.glo2004.domain.entites.CarteDuMonde;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author PC
 */
public class CarteDuMondeDto {
    public float m_scale; 
    public Dimension m_translation; 
    public Dimension m_panel_size_to_move;    
    public List<PaysDto> m_listePays;
    public List<VoiesDeLiaisonsDto> m_listeVoies;
    public List<MaladieDto> m_listeMaladie;
    public List<SimulationPasserDto> m_listeSimulationPasser;
    
    public Integer nbMalade;
    public Integer nbGuerie;
    public Integer nbPersonneDecede;
    public Integer population;
    public Integer nbSain;
    
    
    public ArrayList<Integer> statsMalade;
    public ArrayList<Integer> statsGueri;
    public ArrayList<Integer> statsDeces;
    public ArrayList<Integer> statsPop;
    public ArrayList<String> statsFrontiereOuvert;
    
    //public MaladieDto m_maladie;
    //public PaysDto m_paySelectionner;
    //public RegionDto m_regionSelectionner;
    //public PaysDto m_payHovered;
    //public RegionDto m_regionHovered;
  
    public CarteDuMondeDto(CarteDuMonde carteDuMonde){
        m_scale = carteDuMonde.getScale();
        m_translation = carteDuMonde.getTranslate();
        m_panel_size_to_move = carteDuMonde.getPanelSize();
        m_listePays = carteDuMonde.getListePays().stream().map(m -> new PaysDto(m)).collect(Collectors.toList());
        m_listeVoies = carteDuMonde.getListeVoies().stream().map(m -> new VoiesDeLiaisonsDto(m)).collect(Collectors.toList());
        m_listeMaladie = carteDuMonde.getListeMaladie().stream().map(m -> new MaladieDto(m)).collect(Collectors.toList());
        m_listeSimulationPasser = carteDuMonde.getListeSimulationPasser().stream().map(m -> new SimulationPasserDto(m)).collect(Collectors.toList());
        
        nbMalade = carteDuMonde.getNbInfecteSimulation();
        nbGuerie = carteDuMonde.getNbGuerieSimulation();
        nbPersonneDecede = carteDuMonde.getNbDecedeSimulation();
        population = carteDuMonde.getPopulationTotale();
        nbSain = carteDuMonde.getNbSainsSimulation();
        
        statsMalade = carteDuMonde.getArrayMalade();
        statsGueri = carteDuMonde.getArrayGuerie();
        statsDeces = carteDuMonde.getArrayDeces();
        statsPop = carteDuMonde.getArrayPopTot();
        statsFrontiereOuvert = carteDuMonde.getArrayFrontiereOuvert();
        
        //m_maladie = new MaladieDto(carteDuMonde.getMaladieCourante());
        //m_paySelectionner = new PaysDto(carteDuMonde.getPaysSelectionner());
        //m_regionSelectionner = new RegionDto(carteDuMonde.getRegionSelectionner());
        //m_payHovered = new PaysDto(carteDuMonde.getPaysHovered());
        //m_regionHovered = new RegionDto(carteDuMonde.getRegionHovered());
        
    }
}

