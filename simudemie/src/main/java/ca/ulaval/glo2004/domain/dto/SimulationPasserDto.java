/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;

import ca.ulaval.glo2004.domain.entites.SimulationPasser;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Monique
 */
public class SimulationPasserDto {
    
    public String m_nom;
    public List<CarteDuMondeDto> m_etatsPasser;


    public SimulationPasserDto(SimulationPasser p_simulation) {
        m_nom = p_simulation.getNom();
        m_etatsPasser = p_simulation.getEtatsPasserSimulation().stream().map(m -> new CarteDuMondeDto(m)).collect(Collectors.toList());
    }
}
