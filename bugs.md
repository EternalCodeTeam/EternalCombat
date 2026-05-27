# Bugs Report (EternalCombat)

## Wysoki priorytet

### [EC-002] Błędna logika `headDropOnlyInCombat` i `headDropChance`
- Kategoria: config mismatch / gameplay bug
- Objaw: głowa dropi zawsze w combat, a poza combat może dropić mimo `headDropOnlyInCombat=true`.
- Reprodukcja:
  1. `headDropEnabled=true`, `headDropOnlyInCombat=true`, `headDropChance=1.0`.
  2. Zgiń w combat i poza combat.
- Oczekiwane: tylko w combat i z uwzględnieniem chance.
- Obecne: w combat bezwarunkowo true, poza combat wciąż losowanie.
- Ryzyko: wysokie (ekonomia/farm głów).
- Lokalizacja: `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/drop/DropController.java:83`

### [EC-003] Off-by-one na granicach regionów (krawędź bypass)
- Kategoria: exploit / edge-case
- Objaw: możliwe wejście do strefy po granicznym bloku bez poprawnej blokady.
- Reprodukcja:
  1. Wejdź w combat.
  2. Wejdź dokładnie na granicę regionu (max X/Z).
- Oczekiwane: pełne pokrycie regionu.
- Obecne: `contains()` używa `< max`, a `getMax()` zwraca punkt graniczny inkluzyjny.
- Ryzyko: wysokie.
- Lokalizacja:
  - `eternalcombat-api/src/main/java/com/eternalcode/combat/region/Region.java:20`
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/region/ChunkRegion.java:24`
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/region/bukkit/DefaultRegionProvider.java:43`

### [EC-004] `ignoredWorlds` nie wyłącza wszystkich restrykcji
- Kategoria: config mismatch
- Objaw: gracz tagged w innym świecie po wejściu do ignored world nadal podlega restrykcjom/krom.
- Reprodukcja:
  1. Otaguj gracza.
  2. Teleportuj go do świata z `ignoredWorlds`.
  3. Sprawdź command block / logout punishment.
- Oczekiwane: “not affected by combat rules”.
- Obecne: ignorowane światy sprawdzane głównie przy samym tagowaniu.
- Ryzyko: wysokie.
- Lokalizacja:
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/config/implementation/PluginConfig.java:144`
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/controller/FightTagController.java:127`
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/logout/LogoutController.java:60`

## Średni priorytet

### [EC-005] Utrata `allowFlight` po untag (survival fly)
- Kategoria: gameplay bug
- Objaw: gracze z legalnym `/fly` tracą możliwość lotu po combat.
- Reprodukcja:
  1. Survival + włączony fly z innego pluginu.
  2. Wejdź i wyjdź z combat.
- Oczekiwane: przywrócenie stanu lotu po untag.
- Obecne: restore tylko dla creative/spectator.
- Ryzyko: średnie/wysokie.
- Lokalizacja:
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/controller/FightTagController.java:61`
  - `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/controller/FightActionBlockerController.java:168`

### [EC-006] `/combatlog tag <self>` działa mimo komunikatu “cannot tag self”
- Kategoria: gameplay bug
- Objaw: self-tag działa dla komendy 1-target.
- Reprodukcja: `/combatlog tag <własny_nick>`.
- Oczekiwane: blokada self-tag.
- Obecne: check self tylko w wariancie 2-target.
- Ryzyko: średnie.
- Lokalizacja: `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/FightTagCommand.java:55`

### [EC-007] `tag player1 player2` nie jest atomowe
- Kategoria: gameplay bug
- Objaw: jeden gracz tagged, drugi odrzucony; komunikacja nie mówi o partial success.
- Reprodukcja:
  1. Daj A tagout.
  2. Użyj `/combatlog tag A B`.
- Oczekiwane: all-or-nothing lub jawny partial success.
- Obecne: oba tagowane przed walidacją cancel.
- Ryzyko: średnie.
- Lokalizacja: `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/FightTagCommand.java:96`

### [EC-008] `FightTask` przerywa tick po pierwszym expired tagu
- Kategoria: gameplay bug / edge-case
- Objaw: część graczy nie dostaje update/untag w tym samym ticku.
- Reprodukcja: wielu graczy w combat, pierwszy z iteracji wygasa.
- Oczekiwane: obsługa wszystkich wpisów.
- Obecne: `return` zamiast `continue`.
- Ryzyko: średnie.
- Lokalizacja: `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/FightTask.java:38`

### [EC-009] Możliwa utrata itemów przy respawnie (overflow inventory)
- Kategoria: data loss / edge-case
- Objaw: część itemów “zachowanych” nie wraca, gdy inventory pełne.
- Reprodukcja:
  1. Wymuś keep po drop-modifier.
  2. Respawn z pełnym inventory.
- Oczekiwane: bezstratny zwrot (inv + drop leftovers).
- Obecne: wynik `addItem` nieobsłużony.
- Ryzyko: średnie.
- Lokalizacja: `eternalcombat-plugin/src/main/java/com/eternalcode/combat/fight/drop/DropController.java:142`

### [EC-010] (Podejrzenie) Async reload configu
- Kategoria: edge-case / stabilność wpływająca na gameplay
- Objaw: potencjalne niespójności podczas `reload` pod obciążeniem.
- Reprodukcja: PvP spam + wielokrotne `/combatlog reload`.
- Oczekiwane: spójny reload.
- Obecne: `@Async` i współdzielony mutable config.
- Ryzyko: średnie.
- Lokalizacja: `eternalcombat-plugin/src/main/java/com/eternalcode/combat/EternalCombatReloadCommand.java:31`

## Niespójności README/config (dodatkowo)
- README mówi Java 17+, build ustawiony na Java 21.
- README nie dokumentuje `eternalcombat.untagall` i `eternalcombat.tagout`.
- Komentarz `InventorySettings`: “PLAYER/CREATIVE never blocked” nie jest twardo wymuszony w kontrolerze.
