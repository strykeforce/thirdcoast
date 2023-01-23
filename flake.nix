{
  description = "Dev shell for robot";

  inputs =
    {
      nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    };

  outputs = { self, nixpkgs }:
    let
      pkgs = nixpkgs.legacyPackages.aarch64-darwin;
      pkgs-x86 = nixpkgs.legacyPackages.x86_64-darwin;
    in
    {

      devShells.aarch64-darwin.default = pkgs.mkShell {
        nativeBuildInputs = with pkgs; [
          jdk17_headless
        ];
      };

    };
}
