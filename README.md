# Fun Fishing (`fishing_fih`) - Fabric 1.20.1 Minecraft Mod

A custom fishing mod for Fabric 1.20.1 introducing custom fish entities, 3D Blockbench models, custom items, and an interactive **Dead by Daylight-style Fishing Minigame** with dynamic difficulty and multi-stage reeling.

🎨 **Figma Design & Concept Board**: [View Figma Board](https://www.figma.com/board/LKVRcCa3sczDXfD0DAfnuL/Fishing-Mod?node-id=0-1&p=f&t=l2eCSarcDhjGg50b-0)

---

## 🎣 Features & Mechanics

### 1. Custom Fish Entity & Model
- **Living Mob (`FihEntity`)**: Schooling water ambient fish with realistic swimming animations, flopping physics on land, and water bucket capture.
- **Custom 3D Geometry (`FihModel`)**: Built with Blockbench featuring Body, Head, Left/Right Fins, Top Fin, and an animated Tail Fin.
- **2D & 3D Items**: Custom items for Raw Fih, Cooked Fih, Fish Bucket, and Spawn Egg.

---

### 2. Dead by Daylight-Style Fishing Minigame

#### A. Flow Overview
```
1. Player Casts Fishing Rod -> Bobber waits in water
2. Fish Bites (Splash & Dip) -> Minigame triggers instantly
3. Server Pre-rolls Catch -> Determines Target Fish & Base Difficulty (1-10)
4. Difficulty Calculator -> Evaluates (Base - Rod Modifier - Enchantment Modifier)
5. Multi-Stage Skill Check -> Player must complete 1, 2, or 3 consecutive reels using Right-Click
6. Outcome -> All stages cleared: Fish caught! Any stage missed: Line snaps and fish escapes.
```

#### B. Difficulty Scaling (1 - 10) & Multi-Stage Reeling
The overall difficulty rating ($1-10$) determines both the wheel speed and the number of consecutive skill checks required:

| Difficulty Tier | Required Stages | Needle Speed | Zone Size | Experience |
| :---: | :---: | :---: | :---: | :--- |
| **1 – 5** | **1 Stage** | Normal ($4.0 - 5.5^\circ/\text{tick}$) | Wide | Standard common fish. |
| **6 – 8** | **2 Stages** | Fast ($6.0 - 7.5^\circ/\text{tick}$) | Medium | Rare fish; requires two successful reels in a row. |
| **9 – 10** | **3 Stages** | Very Fast ($8.0 - 9.0^\circ/\text{tick}$) | Tight | Legendary catches; requires three consecutive reels. |

#### C. Extensible Modifiers
$$\mathbf{Difficulty} = \text{clamp}\Big(\text{Base Fish Difficulty} - \text{Rod Tier Modifier} - \text{Enchantment Modifiers}, 1, 10\Big)$$

- **Catch Registry (`CatchRegistry`)**: Weighted probability roll choosing what bit the hook and its base difficulty.
- **Fishing Rod Modifiers**: Future rods (Iron, Diamond, Netherite, Custom) subtract from effective difficulty.
- **Enchantment Modifiers**: Future custom enchantments (e.g. *Steady Line*) further reduce difficulty and widen zones.

---

## 📂 Project Architecture

```
fun_fishing/
├── src/main/java/com/example/funfishing/
│   ├── FunFishingMod.java                   (Mod initializer, registry setups)
│   ├── FunFishingClient.java                (Client setup, model layers, client ticks)
│   ├── entity/
│   │   ├── ModEntities.java                 (Entity type registration)
│   │   ├── FihEntity.java                   (Fish mob AI, schooling, sound, bucket)
│   │   └── client/
│   │       ├── ModModelLayers.java          (Model layer definitions)
│   │       ├── FihModel.java                (3D bone geometry & swim animations)
│   │       └── FihRenderer.java             (Renderer & texture binding)
│   ├── item/
│   │   └── ModItems.java                    (Item definitions & creative tab)
│   ├── minigame/
│   │   ├── CatchEntry.java                  (Catch definition: loot, weight, base difficulty)
│   │   ├── CatchRegistry.java               (Weighted random catch selector)
│   │   ├── FishingDifficultyEngine.java     (Difficulty & stage calculator)
│   │   └── FishingSkillCheckManager.java    (Server-side fishing session orchestrator)
│   ├── network/
│   │   └── FunFishingNetworking.java        (S2C and C2S packet channels)
│   ├── client/
│   │   └── FishingSkillCheckOverlay.java    (Client HUD renderer, dial wheel, stage pips)
│   └── mixin/
│       ├── FishingBobberEntityMixin.java    (Intercepts bite & vanilla reel-in)
│       └── MouseMixin.java                  (Dedicated Right-Click input hook)
└── src/main/resources/
    ├── fabric.mod.json
    ├── fun_fishing.mixins.json
    └── assets/fun_fishing/
        ├── textures/entity/fish/fih.png     (3D mob texture)
        ├── textures/gui/skill_check_ring.png(Minigame dial ring)
        ├── textures/gui/needle.png          (Minigame rotating needle)
        └── textures/item/                   (2D and 3D item textures)
```

---

## 🛠️ Prerequisites & Requirements

> [!NOTE]
> **No `requirements.txt` needed**: Unlike Python projects, Java & Fabric mods do not use a `requirements.txt`. All libraries, Minecraft mappings (Yarn), Fabric Loader, and Fabric API are automatically downloaded and managed by the Gradle build system when you run any Gradle command.

- **Java Development Kit (JDK)**: **Java 17** or higher (OpenJDK 17, Eclipse Temurin 17, etc.)
- **Operating System**: Windows, macOS, or Linux
- **Git** (for cloning and version control)

Check your Java version:
```bash
java -version
```

---

## 🚀 Building & Running

Clone the repository:
```bash
git clone https://github.com/<your-username>/fishing_fih.git
cd fishing_fih
```

### 1. Build the Mod JAR
Compiles the mod, remaps bytecode, and produces the distributable `.jar` file in `build/libs/`:

```bash
# Linux / macOS
./gradlew build

# Windows (Command Prompt / PowerShell)
gradlew.bat build
```
The output JAR file will be located at:
`build/libs/fun_fishing-1.0.0.jar`

### 2. Launch Minecraft Test Client
Launches an integrated Minecraft client with the mod loaded for live in-game testing:

```bash
# Linux / macOS
./gradlew runClient

# Windows
gradlew.bat runClient
```

### 3. Launch Minecraft Test Server (Optional)
Launches a dedicated Fabric test server:

```bash
# Linux / macOS
./gradlew runServer

# Windows
gradlew.bat runServer
```

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
