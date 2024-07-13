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

  outputs =
    {
      nixpkgs,
      flake-utils,
      poetry2nix,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        poetryLib = poetry2nix.lib.mkPoetry2Nix { inherit pkgs; };
        inherit (poetryLib) mkPoetryApplication;
        backend = mkPoetryApplication {
          projectDir = ./.;
          preferWheels = true;
        };
      in
      rec {
        apps.default = {
          type = "app";
          program = "${backend}/bin/api";
        };
        packages.default = backend;
        image = pkgs.dockerTools.buildLayeredImage {
          name = "montcoin-backend";
          tag = "latest";
          contents = [ backend ./config.ini ];
          config = {
            Cmd = [ "${backend}/bin/api" ];
            WorkingDir = "/data";
            Volumes = { "/data" = { }; };
          };
        };
        nixosModules = {
        montcoin-api =
          { config, lib, ... }:
          let
            cfg = config.services.montcoin;
          in
          with lib;
          {
            options.services.montcoin = {
              enable = mkEnableOption "Montcoin";
              user = mkOption {
                type = types.str;
                default = "montcoin";
              };
              group = mkOption {
                type = types.str;
                default = "montcoin";
              };
              configFile = mkOption {
                type = types.str;
              };
            };

            config = mkIf cfg.enable {
              systemd.services.montcoin-backend = {
                description = "MontCoin Backend";
                after = [ "network.target" ];
                wantedBy = [ "multi-user.target" ];
                environment = {
                  CONFIG_FILE = cfg.configFile;
                };
                serviceConfig = {
                  Type = "simple";
                  ExecStart = "${apps.default.program}";
                  User = cfg.user;
                  Group = cfg.group;
                  Restart = "always";
                  RestartSec = 3;
                };
              };
            };
          };
      };

    });
}
