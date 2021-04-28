/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;

import ca.ulaval.glo2004.domain.entites.Region;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * @author PC
 */
public class RegionDto {
    public Integer m_taillePop;
    public Double m_pourcentagePop;
    public Integer m_nbSaine;
    public Integer m_nbInfecter;
    public Integer m_nbGueris;
    public Integer m_nbDecedes;
    public Color m_couleur;
    public String m_nom;
    public FormeDto m_forme;
    public ArrayList<Point> m_points = new ArrayList<Point>();
    public Point m_positionRegion;
    public Point m_positionRelativeRegion;
    
    public RegionDto(Region p_region){
        m_taillePop = p_region.getTaillePop();
        m_pourcentagePop = p_region.getPourcentPop();
        m_nbSaine = p_region.getNbSaine();
        m_nbInfecter = p_region.getNbInfecter();
        m_nbGueris = p_region.getNbGueris();
        m_nbDecedes = p_region.getNbDecede();
        m_couleur = p_region.getCouleur();
        m_nom = p_region.getNom();
        m_forme = new FormeDto(p_region.getForme());
        m_points = p_region.getPoints();
        m_positionRegion = p_region.getPositionRegion();
        m_positionRelativeRegion = p_region.getRelativePositionRegion();
       
    }
}
