/**
 * This code was copied from the blog of Wilfried Elmenreich and can be found at the following link
 * http://demesos.blogspot.com/2015/11/random-numbers-in-java-comparison-of.html
 * 
 * A subclass of java.util.random that implements the 
 * PCG32 random number generator
 * Based on the minimal code example by M.E. O'Neill / pcg-random.org
 * Licensed under Apache License 2.0
 */

package commonutils;

import java.util.Random;

/**
 *
 * @author Wilfried Elmenreich
 */
public class PCGGenerator extends Random
{
    private long inc;
    private long state;

    public PCGGenerator(long seed)
    {
        this.state = seed;
        inc=1;
    }

    public PCGGenerator(long seed, long initseq)
    {
        // initseq selects the output sequence for the RNG
        this.state = seed;
        this.inc=initseq;
    }

    protected int next(int nbits)
    {
        long oldstate=state;
        // Advance internal state
        state=oldstate * 6364136223846793005L + (inc | 1);
        // Calculate output function (XSH RR), uses old state for max ILP
        long xorshifted = ((oldstate >> 18) ^ oldstate) >> 27;
        long rot = oldstate >> 59;
        return (int) ((xorshifted >> rot) | (xorshifted << ((-rot) & 31)));
    }
}
