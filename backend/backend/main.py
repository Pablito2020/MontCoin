from argparse import ArgumentParser, RawTextHelpFormatter

import os
import uvicorn

def parse_args():
    from backend.configuration.config import Configuration
    parser = ArgumentParser(
        prog="Montcoin Backend",
        formatter_class=RawTextHelpFormatter,
    )
    parser.add_argument(
        "-file",
        type=str,
        default = None,
        help="File path containing the configuration"
    )
    args = parser.parse_args()
    print(f"Arguments: {args}")
    if file_path := args.file:
        print(f"(args): setting config file to: {file_path}")
        Configuration.set_config_file(file_path)
    if file_path := os.environ.get("CONFIG_FILE"):
        print(f"(env): setting config file to: {file_path}")
        Configuration.set_config_file(file_path)

def main():
    parse_args()
    from backend.app import app
    uvicorn.run(app, host="0.0.0.0", port=8000)

if __name__ == "__main__":
    main()
