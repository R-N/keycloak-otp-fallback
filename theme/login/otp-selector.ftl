<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=false displayMessage=true; section>

<#if section="header">
    Select OTP Methodasdfasdf
<#elseif section="form">
    <div id="kc-form">
        <div id="kc-form-wrapper">
            <#-- *** Version 1 -->
            <div class="kc-form-header">
                <#if showOptions?? && showOptions>
                    <p class="kc-form-header-welcome">Select OTP Method:</p>
                </#if>
            </div>
            <form id="kc-otp-method-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <#if showOptions?? && showOptions>
                        <div>
                            <#list otpMethods as method>
                                <div>
                                    <input 
                                        class="pf-input-outline"
                                        type="radio" 
                                        id="${method}" 
                                        name="otpMethod" 
                                        value="${method}" 
                                        <#if method == otpDefault>checked</#if>
                                    >
                                    <label for="${method}" class="${properties.kcLabelClass!}">
                                        <#-- Human-friendly label -->
                                        <#if method == "totp">Use Authenticator App
                                        <#elseif method == "email-otp">Send code to my email
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
                <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                    <button tabindex="7" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit"  value="Continue">Continue</button>
                </div>
                <div class="${properties.kcFormGroupClass!}">
                </div>
            </form>
        </div>
    </div>
</#if>
</@layout.registrationLayout>
