package com.duroc.mediatracker.config;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * This is the custom Authentication token made for a request
 * It contains the full idToken received from the request and the decoded Firebase token (the one with the users details)
 */
public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private FirebaseToken firebaseToken;
    private String idToken;


    public FirebaseAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public FirebaseAuthenticationToken(String idToken, FirebaseToken firebaseToken, List<GrantedAuthority> authorities){
        super(authorities);
        this.idToken = idToken;
        this.firebaseToken = firebaseToken;
        setAuthenticated(true);

    }

    // The getCredentials is a bit misleading because the idToken doesn't have the user's details ready to be obtained
    // The getPrinciple is the one with the user's details ready to be obtained (* see getUser() in userServiceImpl *)
    @Override
    public Object getCredentials() {
        return idToken;
    }

    @Override
    public Object getPrincipal() {
        return firebaseToken;
    }
}
