# Icône TimeFlies : le sablier ailé

Choisie le 15/11/2026 (docs/UI_DESIGN_SPEC.md, § 6). `timeflies-icon.svg` est la **seule source** : les autres fichiers en sont tirés.

| Fichier | Usage |
|---|---|
| `timeflies-icon.svg` | Dessin maître, grille 108 × 108, fond en carré arrondi. Favicon du futur site. |
| `timeflies.ico` | Windows : installeur `.msi`, raccourcis, exécutable (`windows.iconFile` dans `src/desktopApp/build.gradle.kts`). 9 tailles, de 16 à 256 px, en PNG compressé. |
| `timeflies-256.png` | Fenêtre et barre des tâches de l'app PC, copiée dans les ressources au build. |
| `timeflies-512.png` | README, documentation, site, image d'aperçu. |

L'icône Android est dans `src/androidApp/src/main/res/` : `ic_launcher_foreground.xml` et `ic_launcher_monochrome.xml` reprennent les tracés du SVG maître, à l'échelle 0,66 pour tenir dans la zone sûre de l'icône adaptative.

## Régénérer les PNG et le `.ico` après une modification du SVG

1. Rendre le SVG en 512 × 512 sur fond transparent, par exemple avec Edge sans interface :
   ```bash
   msedge --headless=new --default-background-color=00000000 --window-size=512,512 --screenshot=icon-512.png page.html
   ```
   où `page.html` affiche `timeflies-icon.svg` en 512 px, sans marge.
2. Réduire ce PNG aux tailles du tableau (rééchantillonnage bicubique) et assembler le `.ico` avec des entrées PNG : n'importe quel outil d'icônes convient (le 15/11/2026, un script PowerShell avec `System.Drawing`).
3. Reporter tout changement de tracé dans les deux vecteurs Android.
