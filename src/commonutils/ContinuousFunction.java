/*
 * Copyright (C) 2020 Alban Lafuente
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package commonutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * Represents a continuous function as an ensemble of value associated with an abscissa
 * To make up for the fact there is a finite number of abscissa, value in-between are approximated by doing a linear interpolation between the two closest points
 * @author Alban Lafuente
 */
public class ContinuousFunction
{
    //do not truncate values here: the field is also defined outside the absorber. Only the absorber knows if a particle exited itself. A ContinuousFunction can only say if a given position is in its range.
    protected final Map<BigDecimal, BigDecimal> m_values;
    protected final TreeSet<BigDecimal> m_abscissa;
    
    public ContinuousFunction()
    {
        m_values = new HashMap<>();
        m_abscissa = new TreeSet<>();
    }
    
    public ContinuousFunction (ContinuousFunction p_passedFunction)
    {
        m_values = p_passedFunction.getFunction();
        m_abscissa = new TreeSet(m_values.keySet());
    }
    
    public ContinuousFunction (HashMap<BigDecimal, BigDecimal> p_values)
    {
        m_values = p_values;
        m_abscissa = new TreeSet(m_values.keySet());
    }
    
    /**
     * Create a continuous function from a file given by SCAPS-1D file
     * @param p_inputFile the SCAPS file from which the values of the abscissa and the field need to be extracted
     * @param p_abscissaUnitMultiplier the multiplier to convert the abscissa to metres
     * @param p_valuesUnitMultiplier the multiplier to convert the field values to SI
     * @param p_expectedExtension the extension of the file from which the value need to be extracted
     * @param p_ncolumn  the total number of column in the type of file given
     * @param p_columnToExtract the column to be taken, first number is the abscissa, second the value of the continuous function
     * @throws FileNotFoundException
     * @throws DataFormatException
     * @throws ArrayIndexOutOfBoundsException
     * @throws IOException 
     */
    protected ContinuousFunction (File p_inputFile, BigDecimal p_abscissaUnitMultiplier, BigDecimal p_valuesUnitMultiplier, String p_expectedExtension, String p_separator, int p_ncolumn, int[] p_columnToExtract) throws FileNotFoundException, DataFormatException, ArrayIndexOutOfBoundsException, IOException
    {
        m_values = new HashMap<>();
        m_abscissa = new TreeSet<>();
        
        String[] nameSplit = p_inputFile.getPath().split("\\.");
        
        if (!nameSplit[nameSplit.length-1].equals(p_expectedExtension))
        {
            throw new DataFormatException();
        }
        
        BufferedReader fieldFile = new BufferedReader(new FileReader(p_inputFile));
        Pattern numberRegex = Pattern.compile("^\\-?\\d+(\\.\\d+(e(\\+|\\-)\\d+)?)?");
	
	String line;
	while (((line = fieldFile.readLine()) != null))
	{	    
	    String[] lineSplit = line.strip().split(p_separator);
	    
	    if(lineSplit.length == p_ncolumn && numberRegex.matcher(lineSplit[0]).matches())
	    {
		//we put the abscissa in meter in order to do all calculations in SI
                BigDecimal currentAbscissa = formatBigDecimal((new BigDecimal(lineSplit[p_columnToExtract[0]].strip())).multiply(p_abscissaUnitMultiplier));
                
                if (!m_abscissa.contains(currentAbscissa))
                {
                    m_values.put(currentAbscissa, formatBigDecimal((new BigDecimal(lineSplit[p_columnToExtract[1]].strip())).multiply(p_valuesUnitMultiplier)));
                    m_abscissa.add(currentAbscissa);
                }
	    }
        }
    }
    
    /**
     * Tell if the passed position is comprised between the minimum and maximum abscissa of the continuous function
     * @param p_position
     * @return true if the position is between the two extrema
     */
    private boolean isInRange(BigDecimal p_position)
    {
        return p_position.compareTo(m_abscissa.first()) >= 0 && p_position.compareTo(m_abscissa.last()) <= 0;
    }
    
    protected BigDecimal formatBigDecimal(BigDecimal p_toBeFormatted)
    {
        return p_toBeFormatted.stripTrailingZeros();
    }
    
    @Override
    public boolean equals(Object o)
    {
        boolean result = this.getClass().equals(o.getClass());
        
        if (result)
        {
            result = m_values.equals(((ContinuousFunction) o).getFunction());
        }
        
        return result;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(m_values);
    }
    
    @Override
    public String toString()
    {
        String result = "";
        
        for (BigDecimal currentAbscissa: m_abscissa)
        {
            result = result.concat(currentAbscissa+"\t=> "+m_values.get(currentAbscissa)+"\n");
        }
        
        return result;
    }
    
    public TreeSet<BigDecimal> getAbscissa()
    {
        return new TreeSet(m_abscissa);
    }
    
    public HashMap<BigDecimal, BigDecimal> getFunction()
    {
        return new HashMap(m_values);
    }
    
    /**
     * Add two continuous function together
     * @param p_passedFunction the function to be added.
     * @return 
     */
    public ContinuousFunction add(ContinuousFunction p_passedFunction)
    {
        Map<BigDecimal, BigDecimal> addedValues = new HashMap<>();
        
        for (BigDecimal position: m_abscissa)
        {
            try
            {
                addedValues.put(position, formatBigDecimal(m_values.get(position).add(p_passedFunction.getValueAtPosition(position))));
            }
            catch (NoSuchElementException ex)
            {
                addedValues.put(position, m_values.get(position));
            }
        }
        
        return new ContinuousFunction((HashMap) addedValues);
    }
    
    public ContinuousFunction negate()
    {
        Map<BigDecimal, BigDecimal> negatedFunction = new HashMap<>();
        
        for (BigDecimal position: m_abscissa)
        {
            negatedFunction.put(position, m_values.get(position).negate());
        }
        
        return new ContinuousFunction((HashMap) negatedFunction);
    }
    
    public ContinuousFunction substract(ContinuousFunction p_passedFunction)
    {
        return this.add(p_passedFunction.negate());
    }
    
    public ContinuousFunction multiply(ContinuousFunction p_passedFunction)
    {
        Map<BigDecimal, BigDecimal> multilpliedValues = new HashMap<>();
        
        for (BigDecimal position: m_abscissa)
        {
            try
            {
                multilpliedValues.put(position, formatBigDecimal(m_values.get(position).multiply(p_passedFunction.getValueAtPosition(position))));
            }
            catch (NoSuchElementException ex)
            {
                multilpliedValues.put(position, m_values.get(position));
            }
        }
        
        return new ContinuousFunction((HashMap) multilpliedValues);
    }
    
    public ContinuousFunction multiply(BigDecimal p_multiplier)
    {
        Map<BigDecimal, BigDecimal> multilpliedValues = new HashMap<>();
        
        for (BigDecimal position: m_abscissa)
        {
            multilpliedValues.put(position, formatBigDecimal(getValueAtPosition(position).multiply(p_multiplier)));
        }
        
        return new ContinuousFunction((HashMap) multilpliedValues);
    }
    
    public ContinuousFunction invert() throws ArithmeticException
    {
        Map<BigDecimal, BigDecimal> invertedFunction = new HashMap<>();
        
        for (BigDecimal position: m_abscissa)
        {
            invertedFunction.put(position, BigDecimal.ONE.divide(getValueAtPosition(position), MathContext.DECIMAL128));
        }
        
        return new ContinuousFunction((HashMap) invertedFunction);
    }
    
    public ContinuousFunction divide(ContinuousFunction p_passedFunction) throws ArithmeticException
    {
        return this.multiply(p_passedFunction.invert());
    }
    
    public ContinuousFunction divide(BigDecimal p_divider) throws ArithmeticException
    {
        return this.multiply(BigDecimal.ONE.divide(p_divider, MathContext.DECIMAL128));
    }
    
    public ContinuousFunction avoidZeros()
    {
        Map<BigDecimal, BigDecimal> noZeroFunction = new HashMap();
        List<BigDecimal> abscissa = new ArrayList(m_values.keySet());
        BigDecimal next, previous, currentValue, currentAbscissa;                
        int lastIndex = abscissa.size()-1;
        
        /*
        if there is only one point in the function (why would you do that?)
        we test if the only element is zero and put in a positive DOUBLE_MIN_VALUE in if it is
        */
        if (lastIndex == 0)
        {
            currentAbscissa = abscissa.get(0);
            currentValue = m_values.get(currentAbscissa);
            
            if (currentValue.compareTo(BigDecimal.ZERO) == 0)
            {
                noZeroFunction.put(currentAbscissa, new BigDecimal(Double.MIN_VALUE));
            }
            else
            {
                noZeroFunction.put(currentAbscissa, currentValue);
            }
        }
        /*
        Otherwise we loop on all element, testing each of them to be zero
        If they are, we put instead DOUBLE_MIN_VALUE in, with a sign given by the environment
        */
        else
        {
            for(int i = 0 ; i <= lastIndex ; i += 1)
            {
                currentAbscissa = abscissa.get(i);
                currentValue = m_values.get(currentAbscissa);

                if (currentValue.compareTo(BigDecimal.ZERO) == 0)
                {
                    if (i == lastIndex)
                    {
                        noZeroFunction.put(currentAbscissa, new BigDecimal(m_values.get(abscissa.get(i-1)).signum() * Double.MIN_VALUE));
                    }
                    else
                    {
                        next = m_values.get(abscissa.get(i+1));
                        
                        if (i == 0)
                        {
                            noZeroFunction.put(currentAbscissa, new BigDecimal(next.signum() * Double.MIN_VALUE));
                        }
                        else
                        {
                            previous = noZeroFunction.get(abscissa.get(i-1));
                            
                            if (currentValue.subtract(next).abs().compareTo(currentValue.subtract(previous).abs()) > 0)
                            {
                                noZeroFunction.put(currentAbscissa, new BigDecimal(previous.signum() * Double.MIN_VALUE));
                            }
                            else 
                            {
                                noZeroFunction.put(currentAbscissa, new BigDecimal(next.signum() * Double.MIN_VALUE));
                            }
                        }
                    }
                }
                else
                {
                    noZeroFunction.put(currentAbscissa, currentValue);
                }
            }
        }
        
        return new ContinuousFunction((HashMap) noZeroFunction);
    }
            
    /**
     * Give the value of the continuous function at the given position
     * If the position given is not in the abscissa list of the continuous function, the value is approximating by doing a linear approximation between the two closes points
     * @param position
     * @return 
     */
    public BigDecimal getValueAtPosition(BigDecimal position)
    {
        BigDecimal value;
        
        if (isInRange(position))
        {
            if (m_abscissa.contains(position))
            {
                value = m_values.get(position);
            }
            else
            {
                BigDecimal previousPosition = m_abscissa.lower(position);
                BigDecimal nextPosition = m_abscissa.higher(position);
                
                BigDecimal interpolationSlope = (m_values.get(nextPosition).subtract(m_values.get(previousPosition))).divide(nextPosition.subtract(previousPosition), MathContext.DECIMAL128);
                BigDecimal interpolationOffset = m_values.get(previousPosition).subtract(interpolationSlope.multiply(previousPosition));
                
                value = (interpolationSlope.multiply(position)).add(interpolationOffset);
            }
        }
        else
        {
            throw new NoSuchElementException("No field value for position:" + String.valueOf(position));
        }

        return value;
    }
}
