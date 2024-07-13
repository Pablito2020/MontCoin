#  MontCoin Backend


##  Build
You can build it for your architecture with the command:

```
    $ nix build
    # Now execute it with:
    $ ./result/bin/api
```

##  Develop:
Enter a development environment with:

```
    $ nix develop
```


##  Deployment:

    * NixOS Module:
    Edit your flake.nix and add:
    ```bash
    inputs = {
        montcoin = {
            url = "github:pablito2020/montcoin?dir=backend";
            inputs.nixpkgs.follows = "nixpkgs";
        };
    };
    ```
    Then import it on your system configuration:
    ```
    inputs.nixpkgs.lib.nixosSystem {
        modules = [
          ...
          inputs.montcoin.nixosModules.<your-architecture>.montcoin-api
          ./configuration.nix # Or whatever nixos module you want:
        ];
      };
    ```

    And enable it!:
    ```
    services.montcoin = {
        enable = true;
        user = user;
        group = "users";
        configFile = "${pkgs.writeText "config.ini" ''
            [Security]
            users_public_key_path = ${./users-public.crt}
            operations_public_key_path = ${./operations-public.crt}
            [Database]
            production = false
            sqlite_path = /home/${user}/sqlite.db
            postgres_user = ${dbUser}
            postgres_password = ${dbPassword}
            postgres_host =${dbHost}
            postgres_port = ${dbPort}
            postgres_db_name = ${dbName}
        ''}";
    };
    ```

    * Docker Compose:
    ```
    $ nix build .#image.x86_64-linux  # or replace with aarch_64-linux or whatever you use
    $ docker load < result
    # Now, before using docker compose, create the volume, and add your public keys and config file there (and edit the .env file!)
    $ docker compose up -d
    ```

