package com.bwspinv.game.util;

import java.text.DecimalFormat;

/**
 * Created by wp90195 on 30/07/2017.
 */
public class Format {

    private static DecimalFormat format = new DecimalFormat("###,###,###,##0");

    /**
     * Recibe un número entero y convierte a String agregado formato.

     * @param number
     * @return
     */
    public static String format(int number){
        return format.format(number);
    }
}
