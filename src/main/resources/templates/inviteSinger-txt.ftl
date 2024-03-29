<#-- @formatter:off -->
Hey ${singer.firstName},
<#if event.location??>
    <#if event.startDate == event.endDate>
You got invited to a ${event.eventType} in ${event.location} on ${event.startDate}.
    <#else>
You got invited to a ${event.eventType} in ${event.location} from ${event.startDate} to ${event.endDate}.
    </#if>
<#else>
    <#if event.startDate == event.endDate>
You got invited to a ${event.eventType} on ${event.startDate}.
    <#else>
You got invited to a ${event.eventType} from ${event.startDate} to ${event.endDate}.
    </#if>
A location has not been announced yet.
</#if>

Please fill the form behind the link.

${acceptLink}

You are able to change your data at any time. Simply click on the link again.

The dates are also available in our calendar: ${calendarUrl}
<#-- @formatter:on -->
