package com.rn.keycloak.otp.fallback;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.sessions.AuthenticationSessionModel;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.*;

public class OTPSelector implements Authenticator {

    public static final String OPTIONS = "otpMethods";
    public static final String DEFAULT_METHOD = "otpDefault";
    public static final String DEFAULT_DESCRIPTION = "otpDefaultDesc";
    public static final String SELECTED_METHOD = "otpMethod";
    public static final String FAIL_COUNT = "otpFailCount";
    public static final String MAX_DEFAULT_ATTEMPTS = "otpMaxDefaultAttempts";
    public static final String SHOW_OPTIONS = "showOptions";
    public static final String SECTION = "otp-selector";

    String userLanguageTag;
    AuthenticatorConfigModel config;
    List<String> methods;
    int maxDefaultAttempts = 1;
    String defaultMethod = "totp";
    String defaultDescription;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (!loadConfig(context)) {
            return;
        }
        boolean showOptions = shouldShowOptions(context);
        
        if (showOptions){
            Response challenge = prepareForm(context)
                .createForm("otp-selector.ftl");
            context.challenge(challenge);
        }else{
            context.getAuthenticationSession().setAuthNote(SELECTED_METHOD, defaultMethod);
            context.success();
        }
    }

    public boolean shouldShowOptions(AuthenticationFlowContext context){
        String sFailCount = context.getAuthenticationSession().getClientNote(FAIL_COUNT);
        int failCount = sFailCount != null ? Integer.parseInt(sFailCount) : 0;
        return defaultMethod.isEmpty() || failCount >= maxDefaultAttempts;
    }

    public LoginFormsProvider prepareForm(AuthenticationFlowContext context){
        return prepareForm(context, null);
    }

    public LoginFormsProvider prepareForm(AuthenticationFlowContext context, LoginFormsProvider form){
        boolean showOptions = shouldShowOptions(context);

        if (form == null){
            form = context.form();
        }
        
        return form
            .setAttribute(OPTIONS, methods)
            .setAttribute(DEFAULT_METHOD, defaultMethod)
            .setAttribute(DEFAULT_DESCRIPTION, defaultDescription)
            .setAttribute(SHOW_OPTIONS, showOptions)
            .setAttribute("section", SECTION);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        if (!loadConfig(context)) {
            return;
        }
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String selectedMethod = formData.getFirst(SELECTED_METHOD);

        if (selectedMethod == null || !methods.contains(selectedMethod)) {
            Response challenge = prepareForm(context)
                .setError("Invalid selection")
                .createForm("otp-selector.ftl");
            context.challenge(challenge);
            return;
        }

        context.getAuthenticationSession().setAuthNote(SELECTED_METHOD, selectedMethod);
        context.success();
    }

    
	private boolean loadConfig(AuthenticationFlowContext context){
		if (context == null){
			return false;
		}

        userLanguageTag = context.getSession().getContext().resolveLocale(context.getUser()).toLanguageTag();
        config = context.getAuthenticatorConfig();

        defaultMethod = config.getConfig().get(DEFAULT_METHOD);
        defaultMethod = defaultMethod != null ? defaultMethod : "";

        defaultDescription = config.getConfig().get(DEFAULT_DESCRIPTION);
        defaultDescription = defaultDescription != null ? defaultDescription : "";

        String sMethods = config.getConfig().get(OPTIONS);
        methods = sMethods != null ? Arrays.asList(sMethods.split("##")) : Collections.emptyList();

        String sMaxDefaultAttempts = config.getConfig().get(MAX_DEFAULT_ATTEMPTS);
        if (sMaxDefaultAttempts == null || sMaxDefaultAttempts.trim().isEmpty()){
            sMaxDefaultAttempts = "1";
        }
        maxDefaultAttempts = Integer.parseInt(sMaxDefaultAttempts);

        if (config == null || config.getConfig() == null || methods.isEmpty()) {
            context.challenge(
                context.form()
                    .setError("OTP Selector not configured")
                    .createLoginUsernamePassword()
                    // .createForm("otp-selector.ftl")
                    // .createErrorPage(Status.INTERNAL_SERVER_ERROR)
            );
            return false;
        }
        
        return true;
	}

    @Override public boolean requiresUser() { return false; }
    @Override public boolean configuredFor(org.keycloak.models.KeycloakSession session, org.keycloak.models.RealmModel realm, UserModel user) { return true; }
    @Override public void setRequiredActions(org.keycloak.models.KeycloakSession session, org.keycloak.models.RealmModel realm, UserModel user) {}
    @Override public void close() {}
}
