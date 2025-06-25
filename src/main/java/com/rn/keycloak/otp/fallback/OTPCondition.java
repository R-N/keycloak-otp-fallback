package com.rn.keycloak.otp.fallback;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;

public class OTPCondition implements Authenticator {

    public static final String EXPECTED_METHOD = "expectedMethod";

    String userLanguageTag;
    AuthenticatorConfigModel config;
    String expectedMethod;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (!loadConfig(context)) {
            return;
        }
        String selectedMethod = context.getAuthenticationSession().getAuthNote(OTPSelector.SELECTED_METHOD);

        if (expectedMethod != null && expectedMethod.equals(selectedMethod)) {
            context.success();
        } else {
            context.attempted();
        }
    }

    @Override public void action(AuthenticationFlowContext context) {}

	private boolean loadConfig(AuthenticationFlowContext context){
		if (context == null){
			return false;
		}

        // userLanguageTag = context.getSession().getContext().resolveLocale(context.getUser()).toLanguageTag();
        config = context.getAuthenticatorConfig();

        expectedMethod = config.getConfig().get(EXPECTED_METHOD);
        expectedMethod = expectedMethod != null ? expectedMethod : "";

        if (config == null || config.getConfig() == null || expectedMethod.isEmpty()) {
            context.challenge(
                context.form()
                    .setError("OTP Condition not configured")
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
