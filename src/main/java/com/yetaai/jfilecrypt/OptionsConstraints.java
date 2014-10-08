/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yetaai.jfilecrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author his20166
 */
public class OptionsConstraints {
    ArrayList<Constraint> alc = new ArrayList();
    
    public boolean isBroken(String[] args) {
        Boolean result = true;
        for (Constraint c:alc) {
            try {
                result = result && c.isBroken(args);
            } catch (ConstraintException ex) {
                Logger.getLogger(OptionsConstraints.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    public void add(Constraint c) {
        alc.add(c);
    }    
}