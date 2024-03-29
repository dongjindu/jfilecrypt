/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yetaai.jfilecrypt;

import com.sun.istack.internal.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author his20166
 */
public class IntMinMaxConstraint implements Constraint {

    private final String opt;
    private final Integer min;
    private final Integer max;

    public IntMinMaxConstraint(String popt, int mini, int maxi) {
        min = mini;
        max = maxi;
        opt = popt;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getOpt() {
        return opt;
    }

    @Override
    public boolean isBroken(String[] args) throws ConstraintException {
        int i1 = 0;
        boolean result = false;
        boolean isThisOption = false;
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals(opt)) {
                isThisOption = true;
            } else if (args[i].substring(0, 1).equals("-")) {
                isThisOption = false;
            }
            if (isThisOption) {
                i1++;
            }
        }
        i1--;
        if (i1 < 0 && min == 0) {
            result = false;
        } else if (i1 < min || i1 > max) {
            result = true;
        } else {
            result = false;
        }
        if (result) {
            Logger.getLogger(this.getClass()).log(Level.SEVERE, "Error in option: " + opt);
        }
        return result;
    }
}
