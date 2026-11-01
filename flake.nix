{
  description = "Participation manager for Kammerchor Wernigerode e.V.";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-26.05";

  outputs = { self, nixpkgs }:
    let
      lib = nixpkgs.lib;
      forSystems = systems: f: lib.genAttrs systems (system: f nixpkgs.legacyPackages.${system});
      linuxSystems = [ "aarch64-linux" "x86_64-linux" ];
      allSystems = linuxSystems ++ [ "aarch64-darwin" "x86_64-darwin" ];
    in
    {
      packages = forSystems linuxSystems (pkgs: {
        default = pkgs.callPackage ./nix/package.nix { };
      });

      devShells = forSystems allSystems (pkgs: {
        default = pkgs.mkShell {
          packages = [ pkgs.jdk17 (pkgs.maven.override { jdk_headless = pkgs.jdk17; }) ];
          JAVA_HOME = pkgs.jdk17.home;
        };
      });
    };
}
