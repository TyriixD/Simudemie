/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.domain.entites.CarteDuMonde;
import ca.ulaval.glo2004.domain.entites.Pays;


/**
 *
 * @author Charles
 */
import java.io.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;

public class Serializable {

    public static void addEnteteToCSV(StringBuilder sb)
    {
        sb.append("Jour");
        sb.append(',');
        sb.append("Malade");
        sb.append(',');
        sb.append("Gueri");
        sb.append(',');
        sb.append("Deces");
        sb.append(',');
        sb.append("Population");
        sb.append('\n');
    }

    public static void addPaysToCSV(StringBuilder sb, String pays)
    {
        sb.append('\n');
        sb.append("Pays");
        sb.append(',');
        sb.append(pays);
        sb.append('\n');
    }

    public static void saveStatsToCSV(CarteDuMonde carte)
    {
        SimudemieController.TalkToUser("Sauvegarde des statistiques au format CSV.");
        try (PrintWriter writer = new PrintWriter(new File("stats.csv"))) 
        {
            StringBuilder sb = new StringBuilder();

            for(Pays p : carte.getListePays())
            {
                addPaysToCSV(sb, p.getNom());
                addEnteteToCSV(sb);

                for (int i = 0; i < p.getArrayMalade().size(); i++) 
                {
                    sb.append(i + 1);
                    sb.append(',');
                    sb.append(p.getArrayMalade().get(i));
                    sb.append(',');
                    sb.append(p.getArrayGuerie().get(i));
                    sb.append(',');
                    sb.append(p.getArrayDeces().get(i));
                    sb.append(',');
                    sb.append(p.getArrayPopTot().get(i));
                    sb.append('\n');
                }
            }

            addPaysToCSV(sb, "Carte du monde");
            addEnteteToCSV(sb);

            for (int i = 0; i < carte.getArrayMalade().size(); i++) 
            {
                sb.append(i + 1);
                sb.append(',');
                sb.append(carte.getArrayMalade().get(i));
                sb.append(',');
                sb.append(carte.getArrayGuerie().get(i));
                sb.append(',');
                sb.append(carte.getArrayDeces().get(i));
                sb.append(',');
                sb.append(carte.getArrayPopTot().get(i));
                sb.append('\n');
            }

            
            
            writer.write(sb.toString());
            SimudemieController.TalkToUser("Sauvegarde des statistiques terminée.");

        } 
        catch (FileNotFoundException e) 
        {
            System.out.println(e.getMessage());
        }
    }

    

    public static void save(CarteDuMonde carte) {
        SimudemieController.TalkToUser("Sauvegarde de la Carte.");
        try {
            String patch = chooseFileSave();

            if (!patch.endsWith(".sml")) {
                patch += ".sml";
            }
            FileOutputStream fileOut = new FileOutputStream(patch);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(carte);
            out.close();
            fileOut.close();
            SimudemieController.TalkToUser("Sauvegarde de la Carte terminée.");

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void openCarteDuMonde(SimudemieController p_controller) {
        SimudemieController.TalkToUser("Chargement de la Carte.");
        CarteDuMonde carte = null;
        try {
            String patch = chooseFileOpen();
            if (patch != "") {
                FileInputStream fileIn = new FileInputStream(patch);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                carte = (CarteDuMonde) in.readObject();
                in.close();
                fileIn.close();
                p_controller.setCarteDuMonde(carte);
                SimudemieController.TalkToUser("Chargement de la Carte terminée.");
            }
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            // System.out.println("CarteDuMonde class not found");
            c.printStackTrace();
        }
    }

    public static String chooseFileOpen() throws IOException {
        SimudemieController.TalkToUser("Choisir le fichier à ouvrir.");
        JFileChooser dialogue = new JFileChooser(new File("."));

        dialogue.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Simudemie file", "sml");
        dialogue.addChoosableFileFilter(filter);

        PrintWriter sortie;
        File fichier;
        String patch = "";

        if (dialogue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            fichier = dialogue.getSelectedFile();
            // sortie = new PrintWriter
            // (new FileWriter(fichier.getPath(), true));
            // sortie.close();
            // System.out.println(fichier);

            for (int i = 0; i < fichier.getPath().length(); i++) {
                if (fichier.getPath().charAt(i) == '\\') {
                    patch = patch + "/";
                } else {
                    patch = patch + fichier.getPath().charAt(i);
                }

            }
        } else if (dialogue.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
            SimudemieController.TalkToUser("Cancel was selected");
        }

        return patch;
    }

    public static String chooseFileSave() {
        SimudemieController.TalkToUser("Choisir la destination de la sauvegarde.");
        JFileChooser fileChooser = new JFileChooser(new File("."));
        File fileToSave;
        fileChooser.setDialogTitle("Specify a file to save");
        String patch = "";

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
            patch = fileToSave.getAbsolutePath();
        }

        return patch;
    }

    public static byte[] TransformToStream(CarteDuMonde p_carte) {
        // Reference for stream of bytes
        byte[] stream = null;
        // ObjectOutputStream is used to convert a Java object into OutputStream
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(p_carte);
            stream = baos.toByteArray();
        } catch (IOException e) {
        }
        return stream;
    }

    public static CarteDuMonde TransformToCarte(byte[] stream) {
        CarteDuMonde p_carte = null;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(stream);
                ObjectInputStream ois = new ObjectInputStream(bais);) {
            p_carte = (CarteDuMonde) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        // You are converting an invalid stream to Student
        return p_carte;
    }
}
