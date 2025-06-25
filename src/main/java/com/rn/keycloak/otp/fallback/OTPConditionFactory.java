package com.rn.keycloak.otp.fallback;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class OTPConditionFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "otp-condition";
    public static final OTPCondition SINGLETON = new OTPCondition();

    @Override public String getId() { return PROVIDER_ID; }
    @Override public String getDisplayType() { return "Condition - OTP Method"; }
    @Override public String getHelpText() { return "Condition that checks if the selected OTP method matches the expected one."; }
    // @Override public Authenticator create(KeycloakSession session) { return new OTPCondition(); }
    @Override public Authenticator create(KeycloakSession session) { return SINGLETON; }
	private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();
    static {
        ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(OTPCondition.EXPECTED_METHOD);
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setLabel("Expected OTP Method");
        property.setHelpText("The OTP method (e.g., 'totp', 'email', 'sms', 'wa') this condition checks for.");
        CONFIG_PROPERTIES.add(property);
    }
	@Override public List<ProviderConfigProperty> getConfigProperties() { return CONFIG_PROPERTIES; }

    @Override public void init(org.keycloak.Config.Scope config) {}
    @Override public void postInit(KeycloakSessionFactory factory) {}
    @Override public void close() {}
    @Override public boolean isConfigurable() { return true; }
    @Override public boolean isUserSetupAllowed() { return false; }
    @Override public String getReferenceCategory() { return PasswordCredentialModel.TYPE; }
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        Requirement.REQUIRED,
        Requirement.DISABLED
    };
    @Override public Requirement[] getRequirementChoices() { return REQUIREMENT_CHOICES; }
}
