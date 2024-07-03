{
  description = "Montcoin Backend environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    poetry2nix = {
      url = "github:nix-community/poetry2nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { nixpkgs, flake-utils, poetry2nix, ... }:
    flake-utils.lib.eachDefaultSystem
      (system:
      let 
        pkgs = nixpkgs.legacyPackages.${system};
        poetryLib = poetry2nix.lib.mkPoetry2Nix { inherit pkgs; };
        inherit (poetryLib) mkPoetryApplication;
        backend = mkPoetryApplication { projectDir = ./.; preferWheels=true;};
      in {
        apps.default = {
          type = "app";
          program = "${backend}/bin/backend";
        };
        packages.default = backend;
      });
}
