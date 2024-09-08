<#-- @formatter:off -->
Hallo ${singer.firstName},

wir möchten dich zu den folgenden Terminen einladen. Bitte bestätige uns, ob du
an den Terminen Zeit hast. Du kannst auch gerne vorläufig zusagen und später
noch absagen, falls du doch nicht kannst. Wenn du deine Angaben ändern möchtest,
kannst du den Link erneut benutzen und das Formular einfach erneut ausfüllen.

<#list items as item>
<#assign event = item.event>
<#assign participant = item.participant>
    ${printer.print(eventName.apply(event), .locale_object)}
        Ort: <#if event.location??>${event.location}<#else>N/A</#if>
        Datum: ${event.startDate?string.short} – ${event.endDate?string.short}
        Link: ${link.apply(participant.token)}

</#list>

Die Termine findest du außerdem in unserem Kalender:
${calendarUrl.apply(items[0].event, .locale_object)}
<#-- @formatter:on -->
