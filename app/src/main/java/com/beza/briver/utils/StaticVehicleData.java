package com.beza.briver.utils;

import java.util.HashMap;

/**
 * Created by fi8er1 on 29/10/2016.
 */

public class StaticVehicleData {

    public static HashMap<String, HashMap<String ,Integer>> hmMain = new HashMap<>();

    static {
        HashMap<String, Integer> hmAudi = new HashMap<>();
        hmAudi.put("A3", 3);
        hmAudi.put("A4", 3);
        hmAudi.put("A6", 3);
        hmAudi.put("A7", 3);
        hmAudi.put("A8", 3);
        hmAudi.put("Q1", 3);
        hmAudi.put("Q3", 3);
        hmAudi.put("Q5", 3);
        hmAudi.put("Q7", 3);
        hmAudi.put("R8", 3);
        hmAudi.put("RS 6", 3);
        hmAudi.put("RS 7", 3);
        hmAudi.put("S5", 3);
        hmAudi.put("TT", 3);
        hmMain.put("Audi", hmAudi);

        HashMap<String, Integer> hmBMW = new HashMap<>();
        hmBMW.put("3 Series", 3);
        hmBMW.put("5 Series", 3);
        hmBMW.put("6 Series Grand Coupe", 3);
        hmBMW.put("7 Series", 3);
        hmBMW.put("Gran Tourismo", 3);
        hmBMW.put("i8", 3);
        hmBMW.put("M3", 3);
        hmBMW.put("M4", 3);
        hmBMW.put("M5", 3);
        hmBMW.put("M6", 3);
        hmBMW.put("X1", 3);
        hmBMW.put("X3", 3);
        hmBMW.put("X5", 3);
        hmBMW.put("X6", 3);
        hmBMW.put("Z4 Roadster", 3);
        hmMain.put("BMW", hmBMW);

        HashMap<String, Integer> hmChevrolet = new HashMap<>();
        hmChevrolet.put("Beat", 1);
        hmChevrolet.put("Cruze", 2);
        hmChevrolet.put("Sail", 1);
        hmChevrolet.put("Spark", 0);
        hmChevrolet.put("Tavera", 2);
        hmMain.put("Chevrolet", hmChevrolet);


        HashMap<String, Integer> hmDatsun = new HashMap<>();
        hmDatsun.put("Go", 0);
        hmDatsun.put("GO+", 0);
        hmMain.put("Datsun", hmDatsun);

        HashMap<String, Integer> hmFiat = new HashMap<>();
        hmFiat.put("Punto", 1);
        hmMain.put("Fiat", hmFiat);

        HashMap<String, Integer> hmFord = new HashMap<>();
        hmFord.put("EcoSport", 2);
        hmFord.put("Fiesta", 2);
        hmFord.put("Figo", 1);
        hmMain.put("Ford", hmFord);

        HashMap<String, Integer> hmHonda = new HashMap<>();
        hmHonda.put("Brio", 1);
        hmHonda.put("City", 2);

        HashMap<String, Integer> hmHyundai = new HashMap<>();
        hmHyundai.put("Amaze", 2);
        hmHyundai.put("Creta", 0);
        hmHyundai.put("Elantra", 0);
        hmHyundai.put("Eon", 0);
        hmHyundai.put("Verna", 2);
        hmHyundai.put("Xcent", 2);
        hmHyundai.put("i10", 1);
        hmHyundai.put("i20", 1);
        hmMain.put("Hyundai", hmHyundai);

        HashMap<String, Integer> hmJaguar = new HashMap<>();
        hmJaguar.put("Jaguar XE", 3);
        hmMain.put("Jaguar", hmJaguar);

        HashMap<String, Integer> hmLandrover = new HashMap<>();
        hmLandrover.put("Discovery Sport S", 3);
        hmMain.put("Landrover", hmLandrover);

        HashMap<String, Integer> hmMercedes = new HashMap<>();
        hmMercedes.put("A-Class", 3);
        hmMercedes.put("AMGs", 3);
        hmMercedes.put("B-Class", 3);
        hmMercedes.put("C-Class", 3);
        hmMercedes.put("CLA-Class", 3);
        hmMercedes.put("CLS", 3);
        hmMercedes.put("E-Class", 3);
        hmMercedes.put("GLA-Class", 3);
        hmMercedes.put("GLC", 3);
        hmMercedes.put("GLE", 3);
        hmMercedes.put("GLS", 3);
        hmMercedes.put("M-Class", 3);
        hmMercedes.put("Maybach", 3);
        hmMercedes.put("MLA-Class", 3);
        hmMercedes.put("S-Class", 3);
        hmMain.put("Mercedes", hmMercedes);

        HashMap<String, Integer> hmMini = new HashMap<>();
        hmMini.put("Cooper D", 3);
        hmMain.put("Mini", hmMini);

        HashMap<String, Integer> hmMahindra = new HashMap<>();
        hmMahindra.put("e2o", 0);
        hmMahindra.put("KUV100", 1);
        hmMahindra.put("TUV300", 2);
        hmMahindra.put("TUV301", 2);
        hmMahindra.put("TUV302", 2);
        hmMahindra.put("TUV303", 2);
        hmMahindra.put("Thar", 2);
        hmMahindra.put("Xylo", 2);
        hmMahindra.put("TUV304", 2);
        hmMahindra.put("TUV305", 2);
        hmMahindra.put("TUV306", 2);
        hmMahindra.put("TUV500", 2);
        hmMahindra.put("TUV501", 2);
        hmMain.put("Mahindra", hmMahindra);

        HashMap<String, Integer> hmMaruti = new HashMap<>();
        hmMaruti.put("Alto 800", 0);
        hmMaruti.put("Alto K10", 0);
        hmMaruti.put("Omni", 0);
        hmMaruti.put("Celerio", 0);
        hmMaruti.put("Eeco", 0);
        hmMaruti.put("WagonR", 1);
        hmMaruti.put("Ritz", 1);
        hmMaruti.put("Swift", 1);
        hmMaruti.put("Baleno", 1);
        hmMaruti.put("Ertiga", 1);
        hmMaruti.put("Ciaz", 2);
        hmMaruti.put("S-Cross", 2);
        hmMain.put("Maruti", hmMaruti);

        HashMap<String, Integer> hmNissan = new HashMap<>();
        hmNissan.put("Micra", 1);
        hmNissan.put("Evalia", 2);
        hmNissan.put("Sunny", 2);
        hmMain.put("Nissan", hmNissan);

        HashMap<String, Integer> hmPremier = new HashMap<>();
        hmPremier.put("Rio", 1);
        hmMain.put("Premier", hmPremier);

        HashMap<String, Integer> hmRenault = new HashMap<>();
        hmRenault.put("Kwid", 0);
        hmRenault.put("Duster", 2);
        hmRenault.put("Fluence", 2);
        hmMain.put("Renault", hmRenault);

        HashMap<String, Integer> hmSkoda = new HashMap<>();
        hmSkoda.put("Octavia", 2);
        hmSkoda.put("Rapid", 2);
        hmMain.put("Skoda", hmSkoda);

        HashMap<String, Integer> hmTata = new HashMap<>();
        hmTata.put("Aria", 2);
        hmTata.put("Bolt", 1);
        hmTata.put("Indica", 1);
        hmTata.put("Indigo", 1);
        hmTata.put("Nano", 0);
        hmTata.put("Safari", 2);
        hmTata.put("Sumo", 2);
        hmTata.put("Venture", 1);
        hmTata.put("Xenon", 2);
        hmTata.put("Zest", 1);
        hmMain.put("Tata", hmTata);

        HashMap<String, Integer> hmToyota = new HashMap<>();
        hmToyota.put("Corolla", 2);
        hmToyota.put("Etios", 2);
        hmToyota.put("Innova", 2);
        hmMain.put("Toyota", hmToyota);

        HashMap<String, Integer> hmVolkswagen = new HashMap<>();
        hmVolkswagen.put("Polo", 1);
        hmVolkswagen.put("Beetle", 3);
        hmMain.put("Volkswagen", hmVolkswagen);

        HashMap<String, Integer> hmVolvo = new HashMap<>();
        hmVolvo.put("S80", 3);
        hmVolvo.put("V40", 3);
        hmVolvo.put("V41", 3);
        hmVolvo.put("XC60", 3);
        hmVolvo.put("XC61", 3);
        hmMain.put("Volvo", hmVolvo);
    }

}
