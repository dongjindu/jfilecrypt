/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yetaai.jfileencrypt;

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
    HashMap<String, Constraint> hmc = new HashMap();
    
    public boolean isBroken(String[] args) {
        Boolean result = true;
        Set entryset = hmc.entrySet();
        for (Map.Entry<String, Constraint> entry:hmc.entrySet()) {
            try {
                String key = entry.getKey();
                Constraint value = entry.getValue();
                result = result && value.isBroken(key, args);
            } catch (ConstraintException ex) {
                Logger.getLogger(OptionsConstraints.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    public void add(String opt, Constraint c) {
        hmc.put(opt, c);
    }    
}