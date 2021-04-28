//package ca.ulaval.glo2004;
//
//import ca.ulaval.glo2004.domain.entites.CarteDuMonde;
//import ca.ulaval.glo2004.domain.entites.Maladie;
//import ca.ulaval.glo2004.domain.entites.Pays;
//import ca.ulaval.glo2004.domain.entites.Region;
//import ca.ulaval.glo2004.helpers.StatistiqueHelper;
//import static com.google.common.truth.Truth.assertThat;
//import java.awt.Color;
//import java.awt.Point;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import org.junit.Assert;
//import org.junit.Before;
//
//
//import org.junit.Test;
///**
// *
// * @author Maï-Anh
// */
//
//public class StatistiqueHelperTest 
//{
//    private CarteDuMonde carteDuMonde;
//        @Before
//    public void setUp(){
//        carteDuMonde = new CarteDuMonde();
//    }
//
//    private Pays remplirPaysAvecRegion(){
//        //Cree un pays avec 100 de population 10 infecté, region séparer avec 60-40 ratio
//        ArrayList<Point> arrayListDePointVide = new ArrayList<>();
//        Color couleur = Color.RED;
//        Point point = new Point(4, 2);
//        ArrayList<Region> listeDeRegion = new ArrayList<>();
//        Pays pays = new Pays("Pepperoni", point, arrayListDePointVide, couleur, 100, 10);
//        Region region1 = remplirRegion1();
//        Region region2 = remplirRegion2();
//        pays.addRegionForTest(region1);
//        pays.addRegionForTest(region2);
//        
//        return pays;
//    }
//    private Pays remplirPaysSansRegion(){
//        ArrayList<Point> arrayListDePointVide = new ArrayList<>();
//        Color couleur = Color.RED;
//        Point point = new Point(4, 2);
//        ArrayList<Region> listeDeRegion = new ArrayList<>();
//        Pays pays = new Pays("Pepperoni", point, arrayListDePointVide, couleur, 100, 10);
//        pays.setTaillePop(100);
//        pays.setNbSaine(90);
//        pays.setNbInfecter(10);
//        return pays;
//    }
//    private Region remplirRegion1(){
//        ArrayList<Point> arrayListDePointVide = new ArrayList<>();
//        Color couleur = Color.RED;
//        Point point = new Point(4, 2);
//        Region region = new Region("Quebec", point, point, arrayListDePointVide, couleur);
//        region.setNbInfecter(10);
//        region.setPourcentPop(60);
//        region.setNbSaine(50);
//        region.setTaillePop(60);
//        return region;
//    }
//    private Region remplirRegion2(){
//        ArrayList<Point> arrayListDePointVide = new ArrayList<>();
//        Color couleur = Color.RED;
//        Point point = new Point(4, 2);        
//        Region region = new Region("Quebec", point, point, arrayListDePointVide, couleur);
//        region.setNbInfecter(5);
//        region.setPourcentPop(40);
//        region.setNbSaine(35);
//        region.setTaillePop(40);
//        
//        return region;
//    }
//
//    @Test
//    public void givenMapOfProbability_whenCallingValeurLaPlusProche_returnValueClosestToTheInput()
//    {
//        Map<Double,Integer> mapDeProb = new HashMap<>();
//        mapDeProb.put(0.5, 5);
//        mapDeProb.put(0.6, 7);
//        mapDeProb.put(0.1, 10);
//        Double probabilite = 0.55;
//        
//        Double valeurPlusProche = StatistiqueHelper.valeurPlusProche(mapDeProb, probabilite);
//        assertThat(valeurPlusProche.equals(0.5));
//        
//    }
//    @Test
//    public void givenAPopulationAndInfectedAndPercentage_whenCallingDistributionBinomial_returnValidProbability(){
//        int population = 100;
//        int infecte = 10;
//        double tauxInfection = 0.1;
//        
//        Map<Double,Integer> listeDeProbabiliteEtSucces = new HashMap<>() ;
//        listeDeProbabiliteEtSucces = StatistiqueHelper.distributionBinomial(population, infecte, tauxInfection);
//        
//        System.out.print(listeDeProbabiliteEtSucces);
//        assertThat(listeDeProbabiliteEtSucces.isEmpty() == false);
//    }
//    @Test
//    public void givenZeroInfected_whenCallingDistributionBinomial_returnMapWithZero(){
//        int population = 100;
//        int infecte = 0;
//        double tauxInfection = 0.1;
//        
//        Map<Double,Integer> listeDeProbabiliteEtSucces = new HashMap<>() ;
//        listeDeProbabiliteEtSucces = StatistiqueHelper.distributionBinomial(population, infecte, tauxInfection);
//        
//        assertThat(listeDeProbabiliteEtSucces.isEmpty() == false);
//    }
//    
//    @Test
//    public void givenStats_whenCallingCalculerNbPersonneInfecte_returnValidNumber(){
//        int population = 100;
//        int infecte = 10;
//        double tauxInfection = 0.1;
//        Map<Double,Integer> listeDeProbabiliteEtSucces = new HashMap<>() ;
//        listeDeProbabiliteEtSucces = StatistiqueHelper.distributionBinomial(population, infecte, tauxInfection);
//        
//        int nbNouveauInfecte =  StatistiqueHelper.calculerNombreDePersonneInfectees(population, infecte, tauxInfection);
//        
//        assertThat(listeDeProbabiliteEtSucces.containsValue(nbNouveauInfecte));
//    }
//    
//    @Test
//    public void givenStats_whenCallingCalculerNbPersonneDecede_returnValidNumber(){
//        int population = 100;
//        int infecte = 10;
//        double tauxInfection = 0.1;
//        Map<Double,Integer> listeDeProbabiliteEtSucces = new HashMap<>() ;
//        listeDeProbabiliteEtSucces = StatistiqueHelper.distributionBinomial(population, infecte, tauxInfection);
//        
//        int nbNouveauDecede =  StatistiqueHelper.calculerNombePersonneDecedees(infecte, tauxInfection);
//        
//        assertThat(listeDeProbabiliteEtSucces.containsValue(nbNouveauDecede));
//    }
//    
//    @Test
//    public void givenStats_whenCallingCalculerNbPersonneGueries_returnValidNumber(){
//        int population = 100;
//        int infecte = 10;
//        double tauxInfection = 0.1;
//        Map<Double,Integer> listeDeProbabiliteEtSucces = new HashMap<>() ;
//        listeDeProbabiliteEtSucces = StatistiqueHelper.distributionBinomial(population, infecte, tauxInfection);
//        
//        int nbNouveauGueris =  StatistiqueHelper.calculerNombrePersonneGueries(infecte, tauxInfection);
//        
//        assertThat(listeDeProbabiliteEtSucces.containsValue(nbNouveauGueris));
//    }
//    
//    @Test
//    public void givenPopulationAndInfected_whenCallingCalculerTauxMalade_returnValidPourcentage(){
//        int population = 100;
//        int infecte = 10;
//        
//        double taux = StatistiqueHelper.calculerTauxPopulationMalade(infecte, population);
//        assertThat(taux == 0.1);
//    }
//    @Test
//    public void givenPopulationAndRecoved_whenCallingCalculerTauxMalade_returnValidPourcentage(){
//        int population = 100;
//        int gueris = 10;
//        
//        double taux = StatistiqueHelper.calculerTauxPersonneGuerie(gueris, population);
//        assertThat(taux == 0.1);
//    }
//    @Test
//    public void givenADecease_whenCallingAvanceJour_returnValidDead(){
//        Maladie maladie = new Maladie("pizza", 100, 0, 0);
//        Pays paysAvecRegion = remplirPaysAvecRegion();
//        carteDuMonde.addPaysToListForTest(paysAvecRegion);
//        
//        carteDuMonde.setMaladieForTest(maladie);
//        carteDuMonde.avanceDeUnJour();
//        Assert.assertEquals(15, carteDuMonde.getNbDecedeSimulation());
//        
//    }
////    @Test
////    public void givenADeceaseWithOneCountry_whenCallingAvanceJour_returnValidDead(){
////        Maladie maladie = new Maladie("pizza", 100.00, 0.4, 0);
////        Pays pays = remplirPaysSansRegion();
////        carteDuMonde.addPaysToListForTest(pays);
////        
////        carteDuMonde.setMaladieForTest(maladie);
////        carteDuMonde.avanceDeUnJour();
////        
////        Assert.assertEquals(10, carteDuMonde.getNbDecedeSimulation());   
////        
////    }
////    
////    @Test
////    public void givenADeceaseWithOneCountry_whenCallingAvanceJour_returnValidInfected(){
////        Maladie maladie = new Maladie("pizza", 0, 100, 0);
////        Pays paysAvecRegion = remplirPaysAvecRegion();
////        carteDuMonde.addPaysToListForTest(paysAvecRegion);
////        
////        carteDuMonde.setMaladieForTest(maladie);
////        carteDuMonde.avanceDeUnJour();
////        
////        Assert.assertEquals(15, carteDuMonde.getNbInfecteSimulation());   
////        
////    }
//    
////    @Test
////    public void givenAchalandage_whenCallingCalculerInfectionVoyageur_returnValidInfected(){
////        Maladie maladie = new Maladie("pizza", 0, 100, 0);
////        Pays pays = remplirPaysSansRegion();
////        Pays pays2 = remplirPaysSansRegion();
////        carteDuMonde.addPaysToListForTest(pays);
////        carteDuMonde.addPaysToListForTest(pays2);
////        carteDuMonde.addVoieToList(pays, pays2, true, 100, "terrestre");
////        
////        pays.calculerInfectionDesVoyageur(maladie, 100, pays2);
////        
////        Assert.assertNotEquals(10, pays2.getNbInfecter());
////        
////    }
//
//
//    
//
//}