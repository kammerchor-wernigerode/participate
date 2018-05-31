<!doctype html>
<html>
<head>
    <meta name="viewport" content="width=device-width"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Feedback</title>
</head>
<body class="">
<table border="0" cellpadding="0" cellspacing="0" style="margin: 10px 15px;">
<#if senderName?? && email??>
    <tr>
        <td><span style="font-weight: bold;">Von:</span> <a href="mailto:${email}">${senderName} &lt;${email}&gt;</a></td>
    </tr>
</#if>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td><span style="font-weight: bold;">Nachricht:</span></td>
    </tr>
    <tr>
        <td>${message}</td>
    </tr>
</table>
</body>
</html>