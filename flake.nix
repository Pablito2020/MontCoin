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
          createCertificates = path: pkgs.writeShellScriptBin "createCerts" ''
            ${pkgs.openssl}/bin/openssl genrsa -out ${path}/private_key.pem 2048
            ${pkgs.openssl}/bin/openssl rsa -in ${path}/private_key.pem -outform PEM -pubout -out ${path}/public.crt
          '';
          usersCerts = createCertificates "./certificates/users";
          operationsCerts = createCertificates "./certificates/operations";
          createAllCerts = pkgs.writeShellScriptBin "createAll" ''
            ${usersCerts}/bin/createCerts
            ${operationsCerts}/bin/createCerts
          '';
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
          apps.default = {
            type = "app";
            program = "${createAllCerts}/bin/createAll";
          };
        }
      );
}
