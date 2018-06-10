package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.DeleteUserForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Unit tests for the User class.
 *
 * @author jshore
 *
 */
public class UserTest {

    /**
     * Tests equals comparison of two user objects. Also verifies getters and
     * setters of the used properties.
     */
    @Test
    public void testEqualsAndProperties () {
        final User u1 = new User();
        final User u2 = new User();

        assertFalse( u1.equals( new Object() ) );
        assertFalse( u1.equals( null ) );
        assertTrue( u1.equals( u1 ) );

        u1.setEnabled( 1 );
        assertTrue( 1 == u1.getEnabled() );
        u2.setEnabled( 1 );

        u1.setPassword( "abcdefg" );
        assertEquals( "abcdefg", u1.getPassword() );
        u2.setPassword( "abcdefg" );

        u1.setRole( Role.valueOf( "ROLE_PATIENT" ) );
        assertEquals( Role.valueOf( "ROLE_PATIENT" ), u1.getRole() );
        u2.setRole( Role.valueOf( "ROLE_PATIENT" ) );

        u1.setUsername( "abcdefg" );
        assertEquals( "abcdefg", u1.getUsername() );
        u2.setUsername( "abcdefg" );

        assertTrue( u1.equals( u2 ) );
        
        // One user is not enabled
        u2.setEnabled(0);
        assertFalse(u2.equals(u1));
        assertFalse(u1.equals(u2));
        u2.setEnabled(1);
        
        // One user has a missing password
        u2.setPassword(null);
        assertFalse(u2.equals(u1));
        
        // Passwords do not match up
        u2.setPassword("123456");
        assertFalse(u2.equals(u1));
        u2.setPassword("abcdefg");
        
        // Roles do not match up
        u2.setRole(Role.valueOf("ROLE_HCP"));
        assertFalse(u2.equals(u1));
        u2.setRole(Role.valueOf("ROLE_PATIENT"));
        assertTrue(User.getPatients() != null);
        
        // One user has a null username
        u2.setUsername(null);
        assertFalse(u2.equals(u1));
        
        // Usernames do not match
        u2.setUsername("ABC123");
        assertFalse(u2.equals(u1));
        u2.setUsername("abcdefg");
    }
    
    /**
     * Test the DeleteUserForm class.
     */
    @Test
    public void testDeleteUserForm() {
    		final DeleteUserForm delF = new DeleteUserForm();
    		delF.setName("ronny");
    		delF.setConfirm("Y");
    		assertEquals(delF.getName(), "ronny");
    		assertEquals(delF.getConfirm(), "Y");
    }

}
