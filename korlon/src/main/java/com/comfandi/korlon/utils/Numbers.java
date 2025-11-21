package com.comfandi.korlon.utils;

import org.springframework.stereotype.Component;

@Component
public class Numbers {
    private static final String[] UNIDADES = {
            "", "UNO", "DOS", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
            "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis",
            "diecisiete", "dieciocho", "diecinueve", "veinte"
    };

    private static final String[] DECENAS = {
            "", "", "veinte", "treinta", "cuarenta", "cincuenta",
            "sesenta", "setenta", "ochenta", "noventa"
    };

    private static final String[] CENTENAS = {
            "", "ciento", "doscientos", "trescientos", "cuatrocientos",
            "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"
    };

    private static String convertirNumero(int numero) {
        if (numero == 0) return "CERO";
        if (numero < 21) return UNIDADES[numero];
        if (numero < 100) return DECENAS[numero / 10] + (numero % 10 != 0 ? " y " + UNIDADES[numero % 10] : "");
        if (numero == 100) return "CIEN";
        if (numero < 1000) return CENTENAS[numero / 100] + (numero % 100 != 0 ? " " + convertirNumero(numero % 100) : "");

        if (numero < 1_000_000) {
            int miles = numero / 1000;
            int resto = numero % 1000;
            String milesStr = (miles == 1) ? "mil" : convertirNumero(miles) + " mil";
            return milesStr + (resto != 0 ? " " + convertirNumero(resto) : "");
        }

        if (numero < 1_000_000_000) {
            int millones = numero / 1_000_000;
            int resto = numero % 1_000_000;
            String millonesStr = (millones == 1) ? "UN MILLON" : convertirNumero(millones) + " MILLONES";
            return millonesStr + (resto != 0 ? " " + convertirNumero(resto) : "");
        }

        return "número demasiado grande";
    }

    public static String convertir(Double valor) {
        int parteEntera = valor.intValue();


        String letras = convertirNumero(parteEntera).trim();
        letras += parteEntera == 1 ? " PESO MCTE" : " PESOS MCTE";



        return letras.toUpperCase();
    }
}
