package com.duroc.mediatracker.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * This is the filter that will intercept all incoming requests for the "users" endpoint.
 * If the request has a header called "Authorization" and that contains "Bearer " then a check for the token that comes
 * after "Bearer " is used.
 * The token is then authenticated using Firebase and from that token some credentials can be obtained
 * A custom token (FirebaseAuthenticationToken) for this specific request is created and the firebaseToken is placed into it
 * The custom token is added to the Spring Security ContextHolder which is specific to the one request and is set to be authenticated
 * Because it's authenticated the request is now able to reach the User endpoints
 */
@Component
public class FirebaseBearerTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getBearerTokenFromRequest(request);

        if(token!=null){

            FirebaseToken decodedToken;

            // Try to authenticate the FirebaseToken obtained from the frontend
            // If it's not authentic then a RuntimeException is thrown
            try {
                 decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            } catch (FirebaseAuthException e) {
                throw new RuntimeException(e);
            }

            // Custom roles can be implemented via Firebase (but we don't have to worry about roles)
            List<GrantedAuthority> authorities = getAuthoritiesFromToken(decodedToken);

            // Adding this specific request's custom Authentication Token to the Context Holder and setting it to authenticated
            SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthenticationToken(token,decodedToken,authorities));
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);

        }
        // At this point the request either goes to the endpoint or a 403 response is sent
        filterChain.doFilter(request,response);
    }

    private String getBearerTokenFromRequest(HttpServletRequest request){

        String authoriaztion = request.getHeader("Authorization");
        if (authoriaztion != null && authoriaztion.startsWith("Bearer ")) {
            return authoriaztion.substring(7);
        }

        return null;
    }
    private static List<GrantedAuthority> getAuthoritiesFromToken(FirebaseToken token) {
        // This gets the authorties from the token
        // (We don't have to worry about this because we aren't locking endpoints based on user roles)
        Object claims = token.getClaims().get("authorities");
        List<String> permissions = (List<String>) claims;
        List<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        if (permissions != null && !permissions.isEmpty()) {
            authorities = AuthorityUtils.createAuthorityList(permissions);
        }
        return authorities;
    }
}
