<#import "login.ftl" as layout>
<@layout.registrationLayout title="Choose OTP Method">
<form id="kc-otp-method-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
    <div class="${properties.kcFormGroupClass!}">
        <#if showOptions?? && showOptions>
            <label for="otpMethod">Select OTP Method:</label>
            <div>
                <#list otpMethods as method>
                    <div>
                        <input 
                            type="radio" 
                            id="${method}" 
                            name="otpMethod" 
                            value="${method}" 
                            <#if method == otpDefault>checked</#if>
                        >
                        <label for="${method}">
                            <#-- Human-friendly label -->
                            <#if method == "totp">Use Authenticator App
                            <#elseif method == "email">Send code to my email
                            <#else>${method?capitalize}
                            </#if>
                        </label>
                    </div>
                </#list>
            </div>
        <#else>
            <div>
                <input type="hidden" name="otpMethod" value="${otpDefault}">
                <p>
                    <#if otpDefaultDesc??>
                        ${otpDefaultDesc}
                    <#else>
                        We will use <b>${otpDefault?capitalize}</b> for verification.
                    </#if>
                </p>
            </div>
        </#if>
    </div>
    <div class="${properties.kcFormGroupClass!}">
        <button type="submit" class="${properties.kcButtonClass!}">Continue</button>
    </div>
</form>
</@layout.registrationLayout>
