{
  description = "Montcoin Main Dev Environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    poetry2nix.url = "github:nix-community/poetry2nix";
  };

  outputs = { nixpkgs, flake-utils, poetry2nix, ... }:
    flake-utils.lib.eachDefaultSystem
      (system:
      let 
        pkgs = nixpkgs.legacyPackages.${system};
        inherit (poetry2nix.lib.mkPoetry2Nix { inherit pkgs; }) mkPoetryApplication;
        admin = mkPoetryApplication { projectDir = ./.; };
      in {
        apps.default = {
          type = "app";
          program = "${admin}/bin/admin";
        };
        packages.default = admin;
      });
}
