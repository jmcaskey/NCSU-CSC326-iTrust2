package edu.ncsu.csc.itrust2.controllers.api;

import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;
import edu.ncsu.csc.itrust2.utils.RandomString;

/**
 * Class that provides multiple API endpoints for interacting with the Users
 * model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIUserController extends APIController {

    /**
     * Retrieves and returns a list of all Users in the system, regardless of
     * their classification (including all Patients, all Personnel, and all
     * users who do not have a further status specified)
     *
     * @return list of users
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<User> getUsers () {
        LoggerUtil.log( TransactionType.VIEW_USERS, "" );
        return User.getUsers();
    }

    /**
     * Retrieves and returns the user with the username provided
     *
     * @param id
     *            The username of the user to be retrieved
     * @return reponse
     */
    @GetMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity getUser ( @PathVariable ( "id" ) final String id ) {
        final User user = User.getByName( id );
        LoggerUtil.log( TransactionType.VIEW_USER, id );
        return null == user ? new ResponseEntity( "No User found for id " + id, HttpStatus.NOT_FOUND )
                : new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * Creates a new user from the RequestBody provided, validates it, and saves
     * it to the database.
     *
     * @param userF
     *            The user to be saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity createUser ( @RequestBody final UserForm userF ) {
        final User user = new User( userF );
        if ( null != User.getByName( user.getUsername() ) ) {
            return new ResponseEntity( "User with the id " + user.getUsername() + " already exists",
                    HttpStatus.CONFLICT );
        }
        try {
            user.save();
            LoggerUtil.log( TransactionType.CREATE_USER, user );
            return new ResponseEntity( user, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "Could not create " + user.toString() + " because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the User with the id provided by overwriting it with the new User
     * record that is provided. If the ID provided does not match the ID set in
     * the User provided, the update will not take place
     *
     * @param id
     *            The ID of the User to be updated
     * @param userF
     *            The updated User to save in place of the existing one
     * @return response
     */
    @PutMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity updateUser ( @PathVariable final String id, @RequestBody final UserForm userF ) {
        final User user = new User( userF );
        if ( null != user.getId() && !id.equals( user.getId() ) ) {
            return new ResponseEntity( "The ID provided does not match the ID of the User provided",
                    HttpStatus.CONFLICT );
        }
        final User dbUser = User.getByName( id );
        if ( null == dbUser ) {
            return new ResponseEntity( "No user found for id " + id, HttpStatus.NOT_FOUND );
        }
        try {
            user.save(); /* Will overwrite existing user */
            LoggerUtil.log( TransactionType.UPDATE_USER, user );
            return new ResponseEntity( user, HttpStatus.OK );
        }

        catch ( final Exception e ) {
            return new ResponseEntity( "Could not update " + user.toString() + " because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Update password with random and send email to the user from input
     *
     * @return success message
     * @param username
     *            the user of passwordReset request
     */
    @PostMapping ( "/passwordReset/{username}" )
    public @ResponseBody ResponseEntity passwordReset ( @PathVariable final String username ) {

        final String easy = RandomString.DIGITS + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
        final RandomString randomizer = new RandomString( 10, new SecureRandom(), easy );
        final String newPass = randomizer.nextString();
        final User thisUser = User.getByName( username );

        if ( thisUser == null ) {
            return new ResponseEntity( "\"No user found for username\"", HttpStatus.NOT_FOUND );
        }

        // get patient, admin or hcp and find their damn emails
        final Role thisRole = thisUser.getRole();
        if ( thisRole == Role.ROLE_PATIENT ) {
            // get patient info
            final Patient patient = Patient.getPatient( username );
            final String patientEmail = patient.getEmail();
            if ( patientEmail == null ) {
                return new ResponseEntity( "\"No email found for username\"" + username, HttpStatus.NOT_FOUND );
            }
            else {

                // reset password for this user and send to email
                // thisUser.setPassword( newPass.toString() );
                // code for email to be sent
                final String email = "itrustv2@gmail.com";
                final String password = "132639di";
                final Properties props = new Properties();
                props.put( "mail.smtp.auth", "true" );
                props.put( "mail.smtp.starttls.enable", "true" );
                props.put( "mail.smtp.host", "smtp.gmail.com" );
                props.put( "mail.smtp.port", "587" );

                final Session session = Session.getInstance( props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication () {
                        return new PasswordAuthentication( email, password );
                    }
                } );

                try {

                    final Message message = new MimeMessage( session );
                    // address here is not significant
                    message.setFrom( new InternetAddress( "facebookRecruiters@gmail.com" ) );
                    message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( patientEmail ) );
                    message.setSubject( "iTrust password reset" );
                    message.setText( "Dear " + username + ", \n\n"
                            + "We have reset your password as per your request. \n" + "New Password: " + newPass + "\n"
                            + "\n" + "Regards, \n" + "\n" + "iTrust Team\n" );

                    Transport.send( message );

                    System.out.println( "Done" );

                }
                catch ( final MessagingException e ) {
                    throw new RuntimeException( e );
                }
            }
        }
        else {
            // get personnel info
            final Personnel staff = Personnel.getByName( username );

            String staffEmail = null;
            if ( staff != null ) {
                staffEmail = staff.getEmail();
            }
            else {
                // staff demographics have not been updated
                return new ResponseEntity( "\"No personnel found for username\"", HttpStatus.NOT_FOUND );
            }
            // if our staffEmail has been found we can send email
            if ( staffEmail == null ) {
                return new ResponseEntity( "\"No email found for username\"", HttpStatus.NOT_FOUND );
            }
            else {
                // code for email to be sent
                final String email = "itrustv2@gmail.com";
                final String password = "132639di";
                final Properties props = new Properties();
                props.put( "mail.smtp.auth", "true" );
                props.put( "mail.smtp.starttls.enable", "true" );
                props.put( "mail.smtp.host", "smtp.gmail.com" );
                props.put( "mail.smtp.port", "587" );
                final Session session = Session.getInstance( props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication () {
                        return new PasswordAuthentication( email, password );
                    }
                } );

                try {

                    final Message message = new MimeMessage( session );
                    // address here is not significant
                    message.setFrom( new InternetAddress( "facebookRecruiters@gmail.com" ) );
                    message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( staffEmail ) );
                    message.setSubject( "Facebook Follow Up" );
                    message.setSubject( "iTrust password reset" );
                    message.setText( "Dear " + username + ", \n\n"
                            + "We have reset your password as per your request. \n" + "New Password: " + newPass + "\n"
                            + "\n" + "Regards, \n" + "\n" + "iTrust Team\n" );

                    Transport.send( message );

                    System.out.println( "Done" );

                }
                catch ( final MessagingException e ) {
                    throw new RuntimeException( e );
                }
            }
        }

        // after successful email, update the password
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        thisUser.setPassword( pe.encode( newPass ) );
        thisUser.save();

        return new ResponseEntity( "\"Email sent to user\"", HttpStatus.OK );

    }
}
