/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yetaai.jfileencrypt;

/**
 *
 * @author his20166
 */
public class IntMinMaxConstraint implements Constraint {

    private final Integer min;
    private final Integer max;

    public IntMinMaxConstraint(int mini, int maxi) {
        min = mini;
        max = maxi;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public boolean isBroken(String option, String[] args) throws ConstraintException {
        int i1 = 0, i2 = 0;
        boolean isThisOption = false;
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals(option)) {
                isThisOption = true;
            } else if (args[i].substring(0, 1).equals("-")) {
                isThisOption = false;
            }
            if (isThisOption) {
                i1++;
            }
        }
        if (i1 < min || i1 > max) {
            return false;
        } else {
            return true;
        }
    }
}
