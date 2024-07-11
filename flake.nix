{
  description = "Montcoin Main Dev Environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    pre-commit-hooks.url = "github:cachix/pre-commit-hooks.nix";
  };

  outputs = { self, nixpkgs, flake-utils, ... }@inputs:
    flake-utils.lib.eachDefaultSystem
      (system:
        let
          pkgs = import nixpkgs { inherit system; };
        in
        {
          commits.pre-commit-check = inputs.pre-commit-hooks.lib.${system}.run {
            src = ./.;
            hooks = {
              commitizen.enable = true;
            };
          };
          devShell = pkgs.mkShell {
            name = "montcoin-main";
            inherit (self.commits.${system}.pre-commit-check) shellHook;
            buildInputs = self.commits.${system}.pre-commit-check.enabledPackages;
          };
        }
      );
}
