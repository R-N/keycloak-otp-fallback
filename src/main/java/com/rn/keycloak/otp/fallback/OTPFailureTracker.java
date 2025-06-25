package com.rn.keycloak.otp.fallback;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.CommonClientSessionModel.ExecutionStatus;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;

import java.util.Map;

public class OTPFailureTracker implements Authenticator {

    public static final String TRACKED_STEP = "otpTrackStep";
    public static final String TRACK_HERE = "otpTrackHere";
    public static final String THROW_FAILURE = "otpTrackThrow";

    String userLanguageTag;
    AuthenticatorConfigModel config;
    String trackedStep;
    boolean trackHere;
    boolean throwFailure = false;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (!loadConfig(context)) {
            return;
        }

        if (trackHere){
            incrementFailCount(context);
            proceed(context);
            return;
        }

        String parentFlow = context.getExecution().getParentFlow();
        AuthenticationSessionModel session = context.getAuthenticationSession();
        Map<String, ExecutionStatus> status = session.getExecutionStatus();
        if (
            ExecutionStatus.FAILED.equals(status.getOrDefault(parentFlow, null)) 
            || ExecutionStatus.FAILED.equals(status.getOrDefault(trackedStep, null))
        ) {
            incrementFailCount(context);
        }

        proceed(context);
    }

    void proceed(AuthenticationFlowContext context){
        Requirement req = context.getExecution().getRequirement();
        if(throwFailure){
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        } else if (req == Requirement.ALTERNATIVE){
            context.attempted();
        } else {
            context.success();
        }
    }

    private void incrementFailCount(AuthenticationFlowContext context) {
        String sFailCount = context.getAuthenticationSession().getClientNote(OTPSelector.FAIL_COUNT);
        int failCount = sFailCount != null ? Integer.parseInt(sFailCount) : 0;
        failCount++;
        context.getAuthenticationSession().setClientNote(OTPSelector.FAIL_COUNT, String.valueOf(failCount));
    }

    @Override
    public void action(AuthenticationFlowContext context) {}

    
	private boolean loadConfig(AuthenticationFlowContext context){
		if (context == null){
			return false;
		}

        // userLanguageTag = context.getSession().getContext().resolveLocale(context.getUser()).toLanguageTag();
        config = context.getAuthenticatorConfig();
        
        if (config == null || config.getConfig() == null) {
            context.challenge(
                context.form()
                    .setError("OTP Failure Tracker not configured")
                    .createLoginUsernamePassword()
                    // .createForm("otp-selector.ftl")
                    // .createErrorPage(Status.INTERNAL_SERVER_ERROR)
            );
            return false;
        }
        // || (trackedStep.isEmpty() && !trackHere)

        trackedStep = config.getConfig().get(TRACKED_STEP);
        trackedStep = trackedStep != null ? trackedStep : "";

        String sTrackHere = config.getConfig().get(TRACK_HERE);
        trackHere = sTrackHere != null ? Boolean.parseBoolean(sTrackHere) : false;

        String sThrowFailure = config.getConfig().get(THROW_FAILURE);
        throwFailure = sThrowFailure != null ? Boolean.parseBoolean(sThrowFailure) : false;

        
        return true;
	}

    @Override public boolean requiresUser() { return false; }
    @Override public boolean configuredFor(org.keycloak.models.KeycloakSession session, org.keycloak.models.RealmModel realm, UserModel user) { return true; }
    @Override public void setRequiredActions(org.keycloak.models.KeycloakSession session, org.keycloak.models.RealmModel realm, UserModel user) {}
    @Override public void close() {}
}
