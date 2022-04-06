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
A location has not be announced yet.
</#if>

Please fill the form behind the link.

Even if you don't know exactly whether you can come, you may provisionally accept the invitation.
From now on you can tell us whether you are arriving by car and how many seats your car has.

${acceptLink}

You are able to change your data at any time. Simply click on the link again.
<#-- @formatter:on -->
