/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.entites;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Monique
 */

public class SimulationPasser {

    private ArrayList<CarteDuMonde> etatsPasser = new ArrayList<>();
    private String nom;
    
    public SimulationPasser( String p_nom , ArrayList<CarteDuMonde> etatsCarte) {
        
        nom = p_nom;
        etatsPasser = etatsCarte;
    }
    
    public String getNom() {
        return nom;
    }
    
    public ArrayList<CarteDuMonde> getEtatsPasserSimulation() {
        return etatsPasser;
    }
    

    public void setNom(String p_nom)
    {
        nom = p_nom;
    }
}



