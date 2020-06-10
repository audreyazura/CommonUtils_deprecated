/*
 * Copyright (C) 2020 Alban Lafuente
 *
 * This file is part of CommonUtils.
 *
 *  CommonUtils is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CommonUtils is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CommonUtils.  If not, see <https://www.gnu.org/licenses/>.
 */
package commonutils;

import java.math.BigDecimal;

/**
 * Contains different physical constants useful for calculations as well as a list of different unit order of magnitude names
 * @author Alban Lafuente
 */
public class PhysicalConstants
{
    //Boltzman constant in J/K
    static final public BigDecimal KB = new BigDecimal("1.380649e-23");
    //electron mass in kg
    static final public BigDecimal ME = new BigDecimal("9.10938188e-31");
    //elementary charge in C
    static final public BigDecimal Q = new BigDecimal("1.60217733e-19");
    //definition of the electronVolt in J
    static final public BigDecimal EV = new BigDecimal("1.602176634e-19");
    
    /**
     * lists the different unit prefixes and their multiplier in SI
     * the UNITY prefix is there to represent "pure" SI units, such as m, s, g...
     */
    static public enum UnitsPrefix
    {
        FEMTO ("1e-12", "f"),  //[SI unit]/[FEMTO unit]
        NANO ("1e-9", "n"),  //[SI unit]/[NANO unit]
        MICRO ("1e-6", "μ"), //[SI unit]/[MICRO unit]
        MILLI ("1e-3", "m"), //[SI unit]/[MILLI unit]
        CENTI ("1e-2", "c"), //[SI unit]/[CENTI unit]
        UNITY ("1.0", "");   //[SI unit]/[SI unit]
        
        private final BigDecimal m_multiplier;
        private final String m_textPrefix;

        UnitsPrefix(String p_multiplier, String p_prefix)
        {
            m_multiplier = new BigDecimal(p_multiplier);
            m_textPrefix = new String(p_prefix);
        }
        
        public BigDecimal getMultiplier()
        {
            return m_multiplier;
        }
        
        public String getPrefix()
        {
            return m_textPrefix;
        }
        
        /**
         * Select the correct unit prefix for a given passed unit
         * @param p_unit the passed unit (e.g., nm, fs...)
         * @return the correct unit prefix
         */
        static public PhysicalConstants.UnitsPrefix selectPrefix (String p_unit)
        {
            PhysicalConstants.UnitsPrefix prefixSelected;

            switch (p_unit.charAt(0))
            {
                case 'f':
                    prefixSelected = PhysicalConstants.UnitsPrefix.FEMTO;
                    break;
                case 'n':
                    prefixSelected = PhysicalConstants.UnitsPrefix.NANO;
                    break;
                case 'μ':
                    prefixSelected = PhysicalConstants.UnitsPrefix.MICRO;
                    break;
                case 'm':
                    prefixSelected = PhysicalConstants.UnitsPrefix.MILLI;
                    break;
                case 'c':
                    prefixSelected = PhysicalConstants.UnitsPrefix.CENTI;
                    break;
                default:
                    prefixSelected = PhysicalConstants.UnitsPrefix.UNITY;
                    break;
            }

            return prefixSelected;
        }
    }
}
