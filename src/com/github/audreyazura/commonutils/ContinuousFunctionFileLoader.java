/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.audreyazura.commonutils;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 *
 * @author audreyazura
 */
public interface ContinuousFunctionFileLoader
{
    /**
     * method to overwrite to write your specific call to the ContinuousFunction constructor
     * @param p_functionFile the file containing the function values
     * @param p_abscissaScale the unit of the abscissa
     * @param p_ordinateScale the unit of the ordinate
     * @return 
     */
    public ContinuousFunction loadFunction (File p_functionFile, PhysicsTools.UnitsPrefix p_abscissaUnit, PhysicsTools.UnitsPrefix p_ordinateunit) throws DataFormatException, IOException, ArrayIndexOutOfBoundsException;
}
