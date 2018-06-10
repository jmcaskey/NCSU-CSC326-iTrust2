package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.*;
import java.util.Random;
import org.junit.Test;
import edu.ncsu.csc.itrust2.utils.RandomString;

/**
 * Tests the RandomString class
 *
 * @author Felix Kim
 *
 */
public class RandomStringTest {

    /**
     * Test the RandomString class.
     */
    @Test
    public void testRandomString () {
    		final int length1 = 3;
    		final int length2 = 5;
    		final int length3 = 0;
    		
    		final long seed = 3;
    	
        final String symbols1 = RandomString.LOWER;
        final String symbols2 = RandomString.ALPHANUM;
        final String symbols3 = "A";
        
        RandomString creator1 = new RandomString(length1, new Random(seed), symbols1);
        RandomString creator2 = new RandomString(length2, new Random(seed), symbols2);
        
        String s1 = creator1.nextString();
        String s2 = creator2.nextString();
        
        assertThat(s1, not(equalTo(s2)));
        
        int count = 0;
        try {
        		creator1 = new RandomString(length3, new Random(seed), symbols1);
        }
        catch (IllegalArgumentException e) {
        		// Should get to this region
        		count++;
        }
        
        try {
        		creator2 = new RandomString(length1, new Random(seed), symbols3);
        }
        catch (IllegalArgumentException e) {
        		// Should get to this region
        		count++;
        }
        
        assertEquals(count, 2);      
    }
}
