This is the RecraftorsMC port of the [BLAHAJ](https://github.com/Hibiii/Blahaj)
mod for [Quilt](https://quiltmc.org)

![Icon](.pretty_readme/icon.png)

---

# Blåhaj

A Minecraft mod that adds the IKEA BLÅHAJ Soft toy shark and a giant bread pillow as items.

![Players holding Blåhaj](./.pretty_readme/banner.png)

| Item           | Preview                                                          | Recipe                                                                      |
|----------------|------------------------------------------------------------------|-----------------------------------------------------------------------------|
| Soft Toy Shark | <img src="./.pretty_readme/preview_blue_shark.png" height=124/>  | ![Crafting recipe for Blåhaj](./.pretty_readme/recipe_blue_shark.png)       |
| Gray Toy Shark | <img src="./.pretty_readme/preview_gray_shark.png" height=124/>  | ![Crafting recipe for Klappar Haj](./.pretty_readme/trade_gray_shark.png)   |
| Pink Toy Shark | <img src="./.pretty_readme/preview_trans_shark.png" height=124/> | ![Crafting recipe for Beyou Blåhaj](./.pretty_readme/trade_trans_shark.png) |
| Bread Pillow   | <img src="./.pretty_readme/preview_bread.png" height=124/>       | ![Crafting recipe for Bread Pillow](./.pretty_readme/recipe_bread.png)      |
| Blue Whale toy | <img src="./.pretty_readme/preview_blue_whale.png" height=124/>  | ![Crafting recipe for Blavingad](./.pretty_readme/recipe_blue_whale.png)    |

Since 0.6.0:

[![Unruled API badge](https://github.com/RecraftorsMC/.github/blob/main/pictures/unruled-badge.png)](https://modrinth.com/mod/unruled-api)

## Extra features

Sleeping cuddles!

| Seen from outside                                  | In first-player vision                            |
|----------------------------------------------------|---------------------------------------------------|
| <img src="./.pretty_readme/preview_sleep_out.png"> | <img src="./.pretty_readme/preview_sleep_in.png"> |

## Experimental features

<details><summary><b>Blavingad contained use</b></summary>
This feature allows you to use items in your world from inside a container plushie, much like the Blavingad.

As it stands, when enabled, this feature allows you to throw potions and use totems much like in vanilla, but
from the blavingad.

However! This feature is highly unstable and it is unrecommended to use with other items until certified as
working properly! Hence, it is restrained to a definite item tag `blahaj:blavingad_usable`, and restrained
by the `blahaj.contained.enable_use` gamerule, which by default is `false`.

While certified to work fine with vanilla totems and both splash and lingering potions, it is not
guaranteed that adding other items to the tag will work. It is therefore recommended to make backups before
testing anything risky!
</details>

# Builtin Compatibilities

## [Trinkets](https://github.com/emilyalexandra/trinkets)

Plushies can be worn in multiple Trinket slots! Each with their own rendering!

<img src="./.pretty_readme/preview_trinkets.png" width="90" />

## [Not Enough Animations](https://modrinth.com/mod/not-enough-animations)

Sleeping cuddle pose and item cuddling works despite the Not Enough Animations' "no sleeping item" setting.

This does not force the setting or temporarily changes it! The setting is only ignored for plushies

---

# License

As a great part of this port's code has been rewritten over the course of its evolution, the latter is
now licensed under the [GPL-3.0](LICENSE) license, as a matter of protection over the provided work.

The original mod is licensed under the [Unlicense](https://github.com/Hibiii/Blahaj/LICENSE).
Feel free to learn from it, and incorporate it in your own projects.

The icon of this project is the Blobhaj by [Heatherhorns](https://www.weasyl.com/~heatherhorns),
and is licensed under a [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/).
