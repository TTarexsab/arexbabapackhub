# Arexhub (Fabric 1.21.4)

Mor temalı PackHub tarzı arayüz. Oyunda **H** tuşu ile açılır (Controls > Arexhub'dan değiştirilebilir).

- Üstte sekmeler: Sword / Totem / Bow / Axe
- Sol tık: texture seç
- Sağ tık: renk düzenle (R/G/B kaydırıcılar)
- DOWNLOAD: `.minecraft/resourcepacks/Arexhub.zip` oluşturur. Oyunda Resource Packs'ten aç.

## Kendi texture'larını eklemek
PNG dosyalarını (16x16 önerilir) şu klasörlere at, otomatik görünür:

    src/main/resources/assets/arexhub/textures/packs/sword/
    src/main/resources/assets/arexhub/textures/packs/totem/
    src/main/resources/assets/arexhub/textures/packs/bow/
    src/main/resources/assets/arexhub/textures/packs/axe/

Hangi eşyanın değiştiği `Category.java` içinde.
Şu an gelen 48 texture örnek (placeholder) renkli ikonlardır.

## Derleme (GitHub Actions)
1. Bu klasörü GitHub'a yükle (push).
2. Repo > Actions > "Build Arexhub" çalışır.
3. Bitince "arexhub-jar" artifact'ını indir, içindeki `arexhub-1.0.0.jar` dosyasını `mods` klasörüne at.
4. Fabric Loader 0.16+ ve Fabric API (1.21.4) gerekir.
