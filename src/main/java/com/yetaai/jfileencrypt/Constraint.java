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
public interface Constraint {
    
    public boolean isBroken(String option, String[] args) throws ConstraintException;
    
}