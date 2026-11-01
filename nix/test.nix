{ pkgs, self }:

let
  issuer = "http://127.0.0.1:8180/realms/test";
  discovery = builtins.toJSON {
    inherit issuer;
    authorization_endpoint = "${issuer}/protocol/openid-connect/auth";
    token_endpoint = "${issuer}/protocol/openid-connect/token";
    userinfo_endpoint = "${issuer}/protocol/openid-connect/userinfo";
    jwks_uri = "${issuer}/protocol/openid-connect/certs";
    subject_types_supported = [ "public" ];
    response_types_supported = [ "code" ];
    id_token_signing_alg_values_supported = [ "RS256" ];
  };
  secret = content: { f = { mode = "0400"; argument = content; }; };
in
pkgs.testers.runNixOSTest {
  name = "participate";

  nodes.machine = { pkgs, ... }: {
    imports = [ self.nixosModules.default ];

    virtualisation = {
      memorySize = 2048;
      cores = 2;
    };

    environment.systemPackages = [ pkgs.curl ];

    services.postgresql = {
      package = pkgs.postgresql_18;
      authentication = pkgs.lib.mkForce ''
        local all      postgres peer
        local sameuser all      peer
      '';
      settings.listen_addresses = pkgs.lib.mkForce "";
    };

    services.nginx = {
      enable = true;
      virtualHosts.issuer = {
        listen = [{ addr = "127.0.0.1"; port = 8180; }];
        locations."= /realms/test/.well-known/openid-configuration".extraConfig = ''
          default_type application/json;
          return 200 '${discovery}';
        '';
      };
    };

    services.mailpit.instances.test = { };

    systemd.tmpfiles.settings.participate-secrets = {
      "/run/secrets/participate/client-secret" = secret "client-secret";
      "/run/secrets/participate/session-secret" = secret "session-secret";
      "/run/secrets/participate/pbe-salt" = secret "8bytes!!";
      "/run/secrets/participate/participate-password" = secret "form-password";
    };

    services.participate = {
      enable = true;
      baseUrl = "https://anwendung.example.org";
      mail.port = 1025;
      participatePasswordFile = "/run/secrets/participate/participate-password";
      oidc = {
        issuerUri = issuer;
        clientSecretFile = "/run/secrets/participate/client-secret";
      };
      settings = {
        "management.endpoints.web.exposure.include" = "health,env";
        "management.endpoint.env.show-values" = "ALWAYS";
      };
      crypto = {
        sessionSecretFile = "/run/secrets/participate/session-secret";
        pbeSaltFile = "/run/secrets/participate/pbe-salt";
      };
    };

    systemd.services.participate = {
      requires = [ "nginx.service" "mailpit-test.service" ];
      after = [ "nginx.service" "mailpit-test.service" ];
    };
  };

  testScript = ''
    import json

    def status(url):
        return machine.succeed(f"curl -s -o /dev/null -w '%{{http_code}}' {url}").strip()

    machine.wait_for_unit("participate.service")
    machine.wait_for_open_port(8081, timeout=300)

    with subtest("Liquibase migrates the database of the service"):
        machine.succeed(
            "runuser -u postgres -- psql -d participate -tAc "
            "'select count(*) from databasechangelog' | grep -qv '^0$'"
        )
        machine.succeed(
            "runuser -u postgres -- psql -d participate -tAc "
            "\"select tableowner from pg_tables where tablename = 'databasechangelog'\" | grep -x participate"
        )

    with subtest("health covers database and mail and reports UP"):
        assert status("http://127.0.0.1:8081/actuator/health") == "200"
        machine.succeed("curl -sf http://127.0.0.1:8081/actuator/health | grep -F '\"status\":\"UP\"'")

    with subtest("application port answers with HTTP 200"):
        assert status("http://127.0.0.1:8080/robots.txt") == "200"

    with subtest("login redirects to the issuer from the discovery document"):
        location = machine.succeed(
            "curl -s -o /dev/null -w '%{redirect_url}' http://127.0.0.1:8080/oauth2/authorization/keycloak"
        )
        assert location.startswith("${issuer}/protocol/openid-connect/auth?"), location
        assert "client_id=participate" in location, location

    with subtest("service listens on loopback only"):
        machine.fail("ss -Hltn | grep -E ':808[01] ' | grep -v '127.0.0.1:808[01] '")

    with subtest("service runs as its own unprivileged user"):
        machine.succeed("ps -o user= -p $(systemctl show -p MainPID --value participate) | grep -x participate")

    with subtest("systemd limits are applied by the kernel"):
        cgroup = "/sys/fs/cgroup/system.slice/participate.service"
        machine.succeed(f"grep -x 1073741824 {cgroup}/memory.max")
        machine.succeed(f"grep -x '100000 100000' {cgroup}/cpu.max")

    with subtest("secret files reach the application through systemd credentials"):
        for prop, value in [
            ("spring.security.oauth2.client.registration.keycloak.client-secret", "client-secret"),
            ("app.crypto.session-secret", "session-secret"),
            ("app.crypto.pbe-salt", "8bytes!!"),
            ("app.participate-password", "form-password"),
        ]:
            env = json.loads(machine.succeed(f"curl -sf http://127.0.0.1:8081/actuator/env/{prop}"))
            assert env["property"]["value"] == value, env
            assert env["property"]["source"].startswith("Config tree"), env
  '';
}
