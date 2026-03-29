# Participate

Participation Manager for Kammerchor Wernigerode e.V.


## Keycloak

Keycloak is an authorization provider that implements the OAuth2 and OpenID
Connect protocols. It manages software clients, users, their roles and claims
for the project.

### Users

Keycloak is preconfigured with a variety of users that are more or less useful.
The username-password-combination `admin:f0rt3` might be the only one you ever
need for development and manual testing.

| Username            | Password             | Description                 | Realm  | URL                                         |
|---------------------|----------------------|-----------------------------|--------|---------------------------------------------|
| `orga`              | `l4rg0`              | User with basic permissions | local  |                                             |
| <mark>`admin`<mark> | <mark>`f0rt3`</mark> | User with all permissions   | local  | http://localhost:9080/admin/local/console/  |
| `admin`[^1]         | `4ll3gr0`[^1]        | Keycloak administrator      | master | http://localhost:9080/admin/master/console/ |

[^1]: Corresponds to the values of `KC_BOOTSTRAP_ADMIN_USERNAME` and
`KC_BOOTSTRAP_ADMIN_PASSWORD`, set for Composes' _keycloak_.

### Configuration Export

This section explains how to export updated configurations so that they can be
managed by Git. The development configuration for Keycloak is part of this
project to distribute changes through Git.

First, make sure your development stack is up and running. Perform your
necessary changes in the Keycloak web UI. Next, perform the following command.
This will start a new Keycloak instance inside the running container.
```shell
docker compose exec keycloak sh -c \
  "cp -rp /opt/keycloak/data/h2 /tmp ; \
  /opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/import --realm local --users realm_file \
    --db dev-file \
    --db-url 'jdbc:h2:file:/tmp/h2/keycloakdb;NON_KEYWORDS=VALUE'"
```
