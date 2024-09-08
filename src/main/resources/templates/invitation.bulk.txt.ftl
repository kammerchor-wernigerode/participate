<#-- @formatter:off -->
Hi ${singer.firstName},

we would like to invite you to the following events. Please confirm whether you
are available on the dates. You can also confirm tentatively and cancel later if
you are unable to attend. If you want to change your details, you can use the
link again and simply fill out the form again.

<#list items as item>
<#assign event = item.event>
<#assign participant = item.participant>
    ${event.name}
        Location: <#if event.location??>${event.location}<#else>tba.</#if>
        Dates: ${event.startDate?string.short} – ${event.endDate?string.short}
        Link: ${link.apply(participant.token)}

</#list>

You can also find the dates in our calendar:
${calendarUrl.apply(items[0].event, .locale_object)}
<#-- @formatter:on -->
