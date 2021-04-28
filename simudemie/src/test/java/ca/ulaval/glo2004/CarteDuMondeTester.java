///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ca.ulaval.glo2004;
//
//import ca.ulaval.glo2004.domain.entites.Forme;
//import ca.ulaval.glo2004.domain.entites.Maladie;
//import ca.ulaval.glo2004.domain.entites.MesureSanitaire;
//import static com.google.common.truth.Truth.assertThat;
//import ca.ulaval.glo2004.domain.entites.CarteDuMonde;
//import ca.ulaval.glo2004.domain.entites.Pays;
//import ca.ulaval.glo2004.domain.entites.Region;
//import ca.ulaval.glo2004.domain.entites.VoiesDeLiaisons;
//import java.awt.Color;
//import java.awt.Point;
//import java.util.ArrayList;
//import org.junit.Before;
//import org.junit.Test;
///**
// *
// * @author Maï-Anh
// */
//public class CarteDuMondeTester {
//    private CarteDuMonde carteDuMonde;
//    
//    @Before
//    private void setUp(){
//        carteDuMonde = new CarteDuMonde();
//        Pays pays1 = remplirPays();
//        carteDuMonde.addPaysToListForTest(pays1);
//
//    }
//
//    private Pays remplirPays(){
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
//    private Region remplirRegion1(){
//        ArrayList<Point> arrayListDePointVide = new ArrayList<>();
//        Color couleur = Color.RED;
//        Point point = new Point(4, 2);
//        Region region = new Region("Quebec", point, point, arrayListDePointVide, couleur);
//        region.setNbInfecter(6);
//        region.setPourcentPop(60);
//        region.setTaillePop(60);
//        return region;
//    }
//    private Region remplirRegion2(){
//        ArrayList<Point> arrayListDePointVide = new ArrayList<>();
//        Color couleur = Color.RED;
//        Point point = new Point(4, 2);        
//        Region region = new Region("Quebec", point, point, arrayListDePointVide, couleur);
//        region.setNbInfecter(4);
//        region.setPourcentPop(40);
//        region.setTaillePop(40);
//        return region;
//    }
//    @Test
//    public void givenADecease_whenCallingAvanceJour_returnValidInfecte(){
//        Maladie maladie = new Maladie("pizza", 1, 0.8, 0);
//        carteDuMonde.setMaladieForTest(maladie);
//        carteDuMonde.avanceDeUnJour();
//        assertThat(carteDuMonde.getNbInfecteSimulation() != 0);
//        
//        
//    }
//}
