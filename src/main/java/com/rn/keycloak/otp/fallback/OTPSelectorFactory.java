package com.rn.keycloak.otp.fallback;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

public class OTPSelectorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "otp-selector";
    public static final OTPSelector SINGLETON = new OTPSelector();

    // @Override public Authenticator create(KeycloakSession session) { return new OTPSelector(); }
    @Override public Authenticator create(KeycloakSession session) { return SINGLETON; }
    @Override public void init(org.keycloak.Config.Scope config) {}
    @Override public void postInit(KeycloakSessionFactory factory) {}
    @Override public void close() {}
    @Override public String getId() { return PROVIDER_ID; }
    @Override public String getReferenceCategory() { return PasswordCredentialModel.TYPE; }
    @Override public String getDisplayType() { return "OTP Method Selector"; }
    @Override public String getHelpText() { return "Allows user to select TOTP or Email OTP."; }
    @Override public boolean isConfigurable() { return true; }
    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        Requirement.REQUIRED,
        Requirement.DISABLED
    };
    @Override public Requirement[] getRequirementChoices() { return REQUIREMENT_CHOICES; }

	private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();
    static {
        ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(OTPSelector.OPTIONS);
        property.setLabel("Allowed OTP Methods");
        property.setType(ProviderConfigProperty.MULTIVALUED_STRING_TYPE);
        property.setHelpText("List of allowed OTP methods (e.g., 'totp', 'email', 'sms', 'wa') separated by new line.");
        property.setDefaultValue("totp");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(OTPSelector.DEFAULT_METHOD);
        property.setLabel("Default OTP Method");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Default OTP method (e.g., 'totp').");
        property.setDefaultValue("totp");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(OTPSelector.DEFAULT_DESCRIPTION);
        property.setLabel("Default OTP Method Description");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Default OTP Method Description");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(OTPSelector.MAX_DEFAULT_ATTEMPTS);
        property.setLabel("Max Default Attempts");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Max attempts before showing OTP options");
        property.setDefaultValue("1");
        CONFIG_PROPERTIES.add(property);
    }
	@Override public List<ProviderConfigProperty> getConfigProperties() { return CONFIG_PROPERTIES; }
    @Override public boolean isUserSetupAllowed() { return false; }
}
