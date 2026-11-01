{ lib, maven, jdk17_headless, dart-sass, makeWrapper }:

let
  pomVersion = builtins.head (builtins.match
    ".*<artifactId>participate</artifactId>[[:space:]]*<packaging>jar</packaging>[[:space:]]*<version>([^<]+)</version>.*"
    (builtins.readFile ../pom.xml));
  sassOutput = "target/generated-sources/src/main/java/de/vinado/wicket/participate/resources/css";
in
maven.buildMavenPackage {
  pname = "participate";
  version = pomVersion;

  src = lib.fileset.toSource {
    root = ../.;
    fileset = lib.fileset.unions [ ../pom.xml ../lombok.config ../src/main ];
  };

  mvnJdk = jdk17_headless;
  mvnHash = "sha256-Caut7+1kXY2KZS3nU7veT4gw7XC9DEnfdkxer+QZY6U=";
  mvnParameters = "-Dsass.skip";
  doCheck = false;

  nativeBuildInputs = [ dart-sass makeWrapper ];

  preBuild = ''
    sass --no-source-map src/main/resources/scss:${sassOutput}
  '';

  installPhase = ''
    runHook preInstall
    install -Dm644 target/participate-*.jar $out/share/participate/participate.jar
    makeWrapper ${jdk17_headless}/bin/java $out/bin/participate \
      --add-flags "-Djava.security.egd=file:/dev/urandom -Duser.language=de -Duser.country=DE" \
      --add-flags "-jar $out/share/participate/participate.jar"
    runHook postInstall
  '';

  meta = {
    description = "Participation manager for Kammerchor Wernigerode e.V.";
    license = lib.licenses.asl20;
    mainProgram = "participate";
    platforms = lib.platforms.linux;
  };
}
