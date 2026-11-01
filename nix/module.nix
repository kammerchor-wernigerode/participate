self:
{ config, lib, pkgs, ... }:

let
  cfg = config.services.participate;
  name = "participate";
  inherit (lib) mkOption mkEnableOption types;

  str = default: description: mkOption { type = types.str; inherit default description; };
  nullStr = description: mkOption { type = types.nullOr types.str; default = null; inherit description; };
  int = default: description: mkOption { type = types.int; inherit default description; };
  secretFile = description: mkOption {
    type = types.nullOr types.str;
    default = null;
    example = "/run/secrets/participate/client-secret";
    description = "${description} Path to a file outside the Nix store, read by systemd as credential.";
  };
  feature = cron: offset: {
    enable = mkEnableOption "this scheduled notification";
    cronExpression = str cron "Quartz cron expression for the job.";
    offset = int offset "Days before the event the job looks ahead.";
  };

  secrets = lib.filterAttrs (_: path: path != null) {
    "spring.security.oauth2.client.registration.keycloak.client-secret" = cfg.oidc.clientSecretFile;
    "app.crypto.session-secret" = cfg.crypto.sessionSecretFile;
    "app.crypto.pbe-salt" = cfg.crypto.pbeSaltFile;
    "app.participate-password" = cfg.participatePasswordFile;
    "app.google-calendar.source" = cfg.googleCalendar.sourceFile;
    "spring.mail.username" = cfg.mail.usernameFile;
    "spring.mail.password" = cfg.mail.passwordFile;
  };

  properties = lib.filterAttrs (_: value: value != null) ({
    "server.address" = "127.0.0.1";
    "server.port" = cfg.port;
    "management.server.address" = "127.0.0.1";
    "management.server.port" = cfg.managementPort;

    "spring.application.name" = cfg.applicationName;
    "spring.datasource.url" = "jdbc:postgresql://localhost/${name}"
      + "?socketFactory=org.newsclub.net.unix.AFUNIXSocketFactory$FactoryArg"
      + "&socketFactoryArg=/run/postgresql/.s.PGSQL.5432";
    "spring.datasource.username" = name;
    "spring.datasource.hikari.maximum-pool-size" = cfg.databasePoolSize;

    "spring.mail.host" = cfg.mail.host;
    "spring.mail.port" = cfg.mail.port;
    "spring.mail.properties.mail.smtp.auth" = cfg.mail.passwordFile != null;
    "spring.mail.properties.mail.from" = cfg.mail.from;
    "spring.mail.properties.mail.reply-to" = cfg.mail.replyTo;
    "spring.mail.properties.mail.smtp.localhost" = cfg.mail.smtpLocalhost;
    "spring.mail.properties.mail.smtp.starttls.enable" = cfg.mail.startTls;
    "spring.mail.properties.mail.smtp.starttls.required" = cfg.mail.startTls;

    "spring.security.oauth2.client.provider.keycloak.issuer-uri" = cfg.oidc.issuerUri;
    "spring.security.oauth2.client.provider.keycloak.user-name-attribute" = cfg.oidc.userNameAttribute;
    "spring.security.oauth2.client.registration.keycloak.client-name" = cfg.oidc.clientName;
    "spring.security.oauth2.client.registration.keycloak.client-id" = cfg.oidc.clientId;
    "spring.security.oauth2.client.registration.keycloak.authorization-grant-type" = "authorization_code";
    "spring.security.oauth2.client.registration.keycloak.redirect-uri" = "${cfg.baseUrl}/login/oauth2/code/keycloak";
    "spring.security.oauth2.client.registration.keycloak.scope" = "openid";
    "app.security.oauth2.client.registration.keycloak.roles-json-path" = cfg.oidc.rolesJsonPath;

    "app.base-url" = cfg.baseUrl;
    "app.customer" = cfg.customer;
    "app.deadline-offset" = cfg.deadlineOffset;
    "app.organization-responsible" = cfg.organizationResponsible;
    "app.sleeping-place-responsible" = cfg.sleepingPlaceResponsible;
    "app.google-calendar.timezone" = cfg.googleCalendar.timezone;
    "app.crypto.pbe-iteration-count" = cfg.crypto.pbeIterationCount;

    "app.notification.email.dispatch.concurrent-transmissions" = cfg.emailDispatch.concurrentTransmissions;
    "app.notification.email.dispatch.shutdown-grace-period" = cfg.emailDispatch.shutdownGracePeriod;
    "app.notification.email.dispatch.recipient-threshold" = cfg.emailDispatch.recipientThreshold;

    "app.features.remind-overdue.enabled" = cfg.features.remindOverdue.enable;
    "app.features.remind-overdue.cron-expression" = cfg.features.remindOverdue.cronExpression;
    "app.features.remind-overdue.offset" = cfg.features.remindOverdue.offset;
    "app.features.scores-manager-notification.enabled" = cfg.features.scoresManagerNotification.enable;
    "app.features.scores-manager-notification.cron-expression" = cfg.features.scoresManagerNotification.cronExpression;
    "app.features.scores-manager-notification.offset" = cfg.features.scoresManagerNotification.offset;
    "app.features.scores-manager-notification.scores-manager-email" = cfg.features.scoresManagerNotification.email;
  } // cfg.settings);

  configFile = (pkgs.formats.yaml { }).generate "${name}.yml" properties;
in
{
  options.services.participate = {
    enable = mkEnableOption "the participation manager of Kammerchor Wernigerode";
    package = mkOption {
      type = types.package;
      default = self.packages.${pkgs.stdenv.hostPlatform.system}.default;
      defaultText = lib.literalExpression "participate.packages.\${system}.default";
      description = "Package that provides `bin/participate`.";
    };

    port = int 8080 "HTTP port on 127.0.0.1 for the reverse proxy.";
    managementPort = int 8081 "Port on 127.0.0.1 for Spring Boot Actuator.";
    databasePoolSize = int 5 "Maximum JDBC connections. The PostgreSQL instance is shared.";

    applicationName = str "Participate" "Name shown in the UI and in mails.";
    baseUrl = mkOption { type = types.str; example = "https://anwendung.example.org"; description = "Public URL behind the reverse proxy."; };
    customer = str "Default Customer" "Name of the organization.";
    participatePasswordFile = secretFile "Password of the participant form.";
    deadlineOffset = int (-1) "Days before an event the registration closes.";
    organizationResponsible = nullStr "Person organizing events.";
    sleepingPlaceResponsible = nullStr "Person organizing accommodation.";

    googleCalendar = {
      sourceFile = secretFile "Private URL of the Google Calendar.";
      timezone = nullStr "Time zone of the calendar, e.g. `Europe/Berlin`.";
    };

    crypto = {
      sessionSecretFile = secretFile "Key for encrypted Wicket URLs.";
      pbeSaltFile = secretFile "Salt of exactly 8 bytes for encrypted Wicket URLs.";
      pbeIterationCount = int 1000 "PBE iteration count for encrypted Wicket URLs.";
    };

    mail = {
      host = str "localhost" "SMTP host.";
      port = int 587 "SMTP port.";
      usernameFile = secretFile "SMTP username.";
      passwordFile = secretFile "SMTP password. Enables SMTP authentication.";
      from = str "smtp@example.com" "Sender address.";
      replyTo = nullStr "Reply-To address. Defaults to the sender.";
      smtpLocalhost = nullStr "Host name sent in HELO.";
      startTls = mkOption { type = types.bool; default = false; description = "Require STARTTLS."; };
    };

    oidc = {
      issuerUri = mkOption { type = types.str; example = "https://id.example.org/realms/example"; description = "OIDC issuer. Spring loads its discovery document on startup."; };
      userNameAttribute = str "preferred_username" "Claim used as user name.";
      clientName = str "Keycloak" "Name of the login button.";
      clientId = str name "OIDC client ID.";
      clientSecretFile = secretFile "OIDC client secret.";
      rolesJsonPath = str "$.resource_access.participate.roles[*]" "JSON path to the roles in the token.";
    };

    emailDispatch = {
      concurrentTransmissions = int 1 "Mails sent in parallel.";
      shutdownGracePeriod = str "30s" "Time to finish sending on shutdown.";
      recipientThreshold = int 100 "Maximum recipients per mail.";
    };

    features = {
      remindOverdue = feature "0 0 9 ? * SUN" 14;
      scoresManagerNotification = feature "0 0 9 ? * FRI" 7 // {
        email = nullStr "Recipient of the scores manager notification.";
      };
    };

    settings = mkOption {
      type = types.attrsOf (types.oneOf [ types.str types.int types.bool ]);
      default = { };
      example = { "spring.mail.test-connection" = false; };
      description = "Further Spring properties. They override the options above.";
    };
  };

  config = lib.mkIf cfg.enable {
    assertions = lib.mapAttrsToList (property: path: {
      assertion = lib.hasPrefix "/" path && !lib.hasPrefix builtins.storeDir path;
      message = "services.participate: secret for ${property} must be an absolute path outside the Nix store.";
    }) secrets;

    services.postgresql = {
      enable = true;
      ensureDatabases = [ name ];
      ensureUsers = [{ inherit name; ensureDBOwnership = true; }];
    };

    systemd.services.${name} = {
      description = "Participation manager of Kammerchor Wernigerode";
      wantedBy = [ "multi-user.target" ];
      requires = [ "postgresql.target" ];
      after = [ "network-online.target" "postgresql.target" ];
      wants = [ "network-online.target" ];

      environment = {
        SPRING_PROFILES_ACTIVE = "oidc";
        SPRING_CONFIG_ADDITIONAL_LOCATION = "file:${configFile}";
        SPRING_CONFIG_IMPORT = "optional:configtree:%d/";
      };

      serviceConfig = {
        ExecStart = lib.getExe cfg.package;
        User = name;
        DynamicUser = true;
        LoadCredential = lib.mapAttrsToList (property: path: "${property}:${path}") secrets;
        Restart = "on-failure";
        RestartSec = 10;
        SuccessExitStatus = 143;

        MemoryMax = "1G";
        MemoryHigh = "896M";
        CPUQuota = "100%";
        TasksMax = 256;

        PrivateTmp = true;
        PrivateDevices = true;
        ProtectSystem = "strict";
        ProtectHome = true;
        ProtectProc = "invisible";
        ProcSubset = "pid";
        ProtectClock = true;
        ProtectHostname = true;
        ProtectKernelLogs = true;
        ProtectKernelModules = true;
        ProtectKernelTunables = true;
        ProtectControlGroups = true;
        NoNewPrivileges = true;
        CapabilityBoundingSet = "";
        RestrictAddressFamilies = [ "AF_UNIX" "AF_INET" "AF_INET6" ];
        RestrictNamespaces = true;
        RestrictRealtime = true;
        RestrictSUIDSGID = true;
        LockPersonality = true;
        RemoveIPC = true;
        SystemCallArchitectures = "native";
        SystemCallFilter = [ "@system-service" "~@privileged" ];
        UMask = "0077";
      };
    };
  };
}
