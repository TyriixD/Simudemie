/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.helpers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 *
 * @author Maï-Anh
 */
public class StatistiqueHelper {
    
    public static Map<Double, Integer> distributionBinomial(int p_nbPersonnePourCalcul, double p_taux) {
        Map<Double, Integer> listeDeProbabiliteEtSucces = new HashMap<>();
        // La population du pays est le nombre d'essai dans notre loi binomiale et la
        // probabilite est le taux de transmission
        BinomialDistribution binomialDistribution = new BinomialDistribution(p_nbPersonnePourCalcul, p_taux);

        double seuilDeTolerance = 0.0;
        int esperanceBinomial = Math.round((int) (p_nbPersonnePourCalcul * p_taux));
        int esperanceBinomial2 = Math.round((int) (p_nbPersonnePourCalcul * p_taux)) - 1;
        double probabilite = binomialDistribution.probability(esperanceBinomial);

        // if(esperanceBinomial > p_essaie){
        // probabilite = 1.0;
        // }

        if (probabilite == 0 || probabilite == Double.NaN || p_taux == Double.NaN || p_taux == 0.0) {
            listeDeProbabiliteEtSucces.put(0.0, 0);
        }
        if (p_nbPersonnePourCalcul == 0) {
            probabilite = 0;
            listeDeProbabiliteEtSucces.put(0.0, 0);
        }

        // On rempli notre distribution en partant de l'esperance et on incrémente soit
        // minime
        while (probabilite > seuilDeTolerance) {
            listeDeProbabiliteEtSucces.put(probabilite, esperanceBinomial);
            esperanceBinomial += 1;
            probabilite = binomialDistribution.probability(esperanceBinomial);
        }

        // On rempli notre distribution en partant de l'esperance et on décrémente
        // jusqu'a temps que le seuil soit minime
        if (esperanceBinomial2 < 0.0) {
            esperanceBinomial2 = 0;
        }
        while (probabilite > seuilDeTolerance && esperanceBinomial2 >= 0) {
            listeDeProbabiliteEtSucces.put(probabilite, esperanceBinomial2);
            esperanceBinomial2 -= 1;

            probabilite = binomialDistribution.probability(esperanceBinomial2);
        }
        return listeDeProbabiliteEtSucces;
    }

    public static double calculerTauxPopulationMalade(int p_nbMalade, int p_population) {

        return (double) ((p_nbMalade / p_population));
    }

    public static double calculerTauxPersonneGuerie(int p_nbPersonneGuerie, int p_population) {
        return (double) ((p_nbPersonneGuerie / p_population) * 100);
    }

    public static int calculerNombreDePersonneInfectees(int p_nbPersonneInfecte,
        double p_tauxDeTransmission, double p_tauxDeReproduction) {
        // On calcule nos probabilite avec une distribution binomiale
        Map<Double, Integer> mapDeProbabilite = distributionBinomial(p_nbPersonneInfecte,
                p_tauxDeTransmission);

        // On effectue la pige en gérérant un nombre entre 0 et 1 et on regarde quel est
        // sa clé dans la map
        Random random = new Random();
        double randomNumber = random.nextDouble();
        double probabiliteDonnee = valeurPlusProche(mapDeProbabilite, randomNumber);
        int nbPersonneInfectees = mapDeProbabilite.get(probabiliteDonnee);
        int nbPersonneReelInfectee = (int)Math.floor(nbPersonneInfectees * p_tauxDeReproduction);
        
        return nbPersonneReelInfectee;

    }

    public static int calculerNombePersonneDecedees(int p_nbPersonneInfecte, double p_tauxDeMortalite) {
        Map<Double, Integer> mapDeProbabilite = distributionBinomial(p_nbPersonneInfecte,
                p_tauxDeMortalite);

        Random random = new Random();
        double randomNumber = random.nextDouble();
        double probabiliteDonnee = valeurPlusProche(mapDeProbabilite, randomNumber);

        int nbPersonneDecedes = mapDeProbabilite.get(probabiliteDonnee);
        return nbPersonneDecedes;
    }

    public static int calculerNombrePersonneGueries(int p_nbPersonneInfecte, double p_tauxDeGuerison) {
        Map<Double, Integer> mapDeProbabilite = distributionBinomial(p_nbPersonneInfecte,
                p_tauxDeGuerison);

        Random random = new Random();
        double randomNumber = random.nextDouble();
        double probabiliteDonnee = valeurPlusProche(mapDeProbabilite, randomNumber);

        int nbPersonneGueries = mapDeProbabilite.get(probabiliteDonnee);
        return nbPersonneGueries;
    }


    public static int calculerNombreInfecterSiEsperanceTropBasse(int nb_infecte, int nb_populationSaine,
            double p_tauxTransmission, double p_tauxReproduction) {
        int nombreDinfection = 0;
        if (nb_infecte == 1) {
            nb_infecte = 10;
        }
        BigDecimal taux = BigDecimal.valueOf(p_tauxTransmission);
        BigDecimal infecte = BigDecimal.valueOf(nb_infecte);
        BigDecimal popSaine = BigDecimal.valueOf(nb_populationSaine);

        BigDecimal probDePersonneInfecter = infecte.divide(popSaine, MathContext.DECIMAL128);
        BigDecimal probDinfection = probDePersonneInfecter.multiply(taux, MathContext.DECIMAL128);

        nombreDinfection = (probDinfection.multiply(popSaine)).intValue();
        int nombreInfectionReel = (int)Math.floor(nombreDinfection * p_tauxReproduction);

        return nombreInfectionReel;
    }

    // Retourne la clé dans la map ayant la probabilité la plus proche de celle
    // donnée en paramètre
    public static double valeurPlusProche(Map<Double, Integer> map, double probabilite) {
        double minDiff = Double.MAX_VALUE;
        double valeurLaPlusProche = 0.0;
        for (double key : map.keySet()) {
            double difference = Math.abs((double) probabilite - key);
            if (difference < minDiff) {
                valeurLaPlusProche = key;
                minDiff = difference;
            }
        }
        return valeurLaPlusProche;
    }

}
