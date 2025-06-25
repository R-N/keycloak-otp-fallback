package com.rn.keycloak.otp.fallback;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.browser.OTPFormAuthenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.UserModel.RequiredAction;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class OTPFailureTrackerFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "otp-failure-tracker";
    public static final OTPFailureTracker SINGLETON = new OTPFailureTracker();

    @Override public String getId() { return PROVIDER_ID; }
    @Override public String getDisplayType() { return "Tracker - OTP Failure"; }
    @Override public String getHelpText() { return "Tracker that checks for OTP failure."; }
    // @Override public Authenticator create(KeycloakSession session) { return new OTPFailureTracker(); }
    @Override public Authenticator create(KeycloakSession session) { return SINGLETON; }
    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();
    static {
        ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(OTPFailureTracker.TRACKED_STEP);
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setLabel("Tracked OTP Step");
        property.setHelpText("The OTP step this tracker checks for.");
        property.setDefaultValue("totp");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(OTPFailureTracker.TRACK_HERE);
        property.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        property.setLabel("Track Here");
        property.setHelpText("Check this if tracker is placed where it means fail. Track step will be ignored.");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(OTPFailureTracker.THROW_FAILURE);
        property.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        property.setLabel("Throw Failure");
        property.setHelpText("Check this if tracker should throw soft failure.");
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
        Requirement.DISABLED,
        Requirement.ALTERNATIVE
    };
    @Override public Requirement[] getRequirementChoices() { return REQUIREMENT_CHOICES; }
}
