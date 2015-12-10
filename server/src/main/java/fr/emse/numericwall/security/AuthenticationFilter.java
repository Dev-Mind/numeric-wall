package fr.emse.numericwall.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.emse.numericwall.model.Authority;
import fr.emse.numericwall.model.Role;
import fr.emse.numericwall.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * In Spring we can add an interceptor in the Spring MVC filters chain. But it's only for Spring MVC and the filter won't be used if you use
 * an EndPoint Actuator. The solution is to use a web filter. This filter checks if the user token is in the request. If this
 * token is present the current user is loaded. If the token is not in request or if the user doesn't exist a 401 error is send
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    public final static String TOKEN_REQUEST_HEADER_PARAM = "X-XSRF-TOKEN";
    public final static String TOKEN_COOKIE_NAME = "XSRF-TOKEN";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PathMatcher pathMatcher;

    private final List<String> includePatterns = new ArrayList<>();

    private final List<String> excludePatterns = new ArrayList<>();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //If request is secured we test id user is authenticated
        if (request instanceof HttpServletRequest && matches(request.getServletPath())) {
            //TODO in the future we will call the LDAP
            //For the moment we only create a user
            User user = new User()
                    .setEsmeid(UUID.randomUUID().toString())
                    .setEmail("user.fake@gmail.com")
                    .setFirstname("User")
                    .setLastname("FAKE")
                    .addAuthority(new Authority().setName(Role.PUBLIC))
                    .addAuthority(new Authority().setName(Role.ADMIN));

            //Monitoring is only visible for admin
            if(pathMatcher.match("/monitoring/**", request.getServletPath()) && !user.getRoles().contains(Role.ADMIN.toString())){
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "User unknown");
            }
            else {
                CurrentUser currentUser = applicationContext.getBean(CurrentUser.class);
                currentUser.setCredentials(user);
                filterChain.doFilter(request, response);
            }
        }
        else {
            filterChain.doFilter(request, response);
        }
    }


    /**
     * Add URL patterns to which the registered interceptor should apply to.
     */
    public AuthenticationFilter addPathPatterns(String... patterns) {
        this.includePatterns.addAll(Arrays.asList(patterns));
        return this;
    }

    /**
     * Add URL patterns to which the registered interceptor should not apply to.
     */
    public AuthenticationFilter excludePathPatterns(String... patterns) {
        this.excludePatterns.addAll(Arrays.asList(patterns));
        return this;
    }

    /**
     * Add a path matcher to the filter
     */
    public AuthenticationFilter setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        return this;
    }

    /**
     * Returns {@code true} if the interceptor applies to the given request path.
     *
     * @param lookupPath the current request path
     */
    public boolean matches(String lookupPath) {
        if (this.excludePatterns != null) {
            for (String pattern : this.excludePatterns) {
                if (pathMatcher.match(pattern, lookupPath)) {
                    return false;
                }
            }
        }
        if (this.includePatterns == null) {
            return true;
        }
        else {
            for (String pattern : this.includePatterns) {
                if (pathMatcher.match(pattern, lookupPath)) {
                    return true;
                }
            }
            return false;
        }
    }
}
