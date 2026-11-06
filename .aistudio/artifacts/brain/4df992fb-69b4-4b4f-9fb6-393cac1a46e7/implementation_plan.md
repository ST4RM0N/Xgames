# Grokpot - Bot Recipe Trees & Mind Garden Knowledge Graphs

Grokpot transforms unstructured workflow documents, SOPs, and procedural notes into structured, actionable Bot Recipe Trees. These automation pathways feed into an interactive, evolving "Mind Garden" knowledge graph that begins as a single seed thought and sprouts branches, blooming into deeper automation hierarchies as the user earns Thought Experience (XP) through interaction, execution, and node mastery.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following directions were confirmed during interactive clarification and are locked into the design:

- **Confirmed Decision 1 (Workflow Ingestion)**: Users paste workflow documents, markdown SOPs, or raw outlines into a smart document parser. Grokpot decomposes them into hierarchical automation steps: Triggers, Decision Branches, Actions, Bot Workers, and Verification Gates. Sample workflow templates (e.g., *DevOps Deployment Bot*, *Customer Support Triage*, *Content Pipeline*, *Data Scraper & Analyzer*) are included for instant demonstration.
- **Confirmed Decision 2 (Mind Garden Progression & XP Engine)**: Thought nodes start as tender sprouts (Level 1). Nodes accumulate Thought XP whenever the user inspects details, executes/simulates automation steps, logs field notes, or completes sub-tasks. At threshold XP tiers (Sprout -> Sapling -> Blooming Node -> Arbiter Garden), nodes unlock deeper branches, secret automation recipes, and child workflows.
- **Confirmed Decision 3 (Bioluminescent Garden Aesthetic)**: Deep interstellar dark canvas (`#0A0E17`) with glowing bioluminescent filaments (`#00F5D4` cyan-teal, `#7928CA` electric violet, `#10B981` emerald, and `#F59E0B` amber). Custom canvas-rendered interactive graph with node blooms, pulsing energy particles, and smooth pan/zoom gesture physics.
- **Data Persistence**: Offline-first reactive Room database capturing Recipes, Recipe Steps, Thought Nodes, Connections, Experience Logs, and Garden States with zero loss on device restart.

---

### 1. Overview & Core Concept

**What It Does**:
Grokpot bridges workflow automation and human cognition. Complex operational guides or messy thoughts are ingested, analyzed, and mapped into clear tree hierarchies called "Bot Recipes". Each recipe step represents an automated bot routine or manual intervention. Simultaneously, these trees link into the user's "Mind Garden"—an interconnected knowledge graph where ideas, automations, and operational logic grow organically as they are exercised and leveled up.

**Target Audience & Persona**:
Engineers, automation creators, system architects, and knowledge workers who manage complex operational pipelines, SOPs, or personal thinking systems and need a visual, rewarding map of how their processes interconnect and scale.

**Key Value**:
1. **Clarity from Chaos**: Ingests dense documentation into intuitive hierarchical bot recipe trees.
2. **Actionable Automation**: Every node has defined input, trigger, bot execution spec, and completion criteria.
3. **Engaging Growth (Mind Garden)**: Gamified Thought Experience (XP) tracks real system mastery and unlocks deeper operational pathways.
4. **Interactive Exploration**: Pinch-to-zoom, pan, drag-and-drop, and filterable bioluminescent graph visualization.

---

### 2. User Experience & Visual Design

#### Key User Flows

1. **Garden View (Home Canvas)**:
   - Floating camera over a boundless bioluminescent space.
   - Seed thoughts and recipe clusters pulse with living glow shaders.
   - HUD displays Garden Level, Total Thought XP, Active Bot Trees, and Filter Chips (All, Recipes, Thoughts, High XP).
   - Tap a node to open the bottom sheet **Node Inspector** showing XP progress, linked bot recipes, and action triggers.
   - Long-press or double-tap canvas to plant a new **Seed Thought**.

2. **Workflow Parser & Recipe Studio**:
   - Ingest tab with a rich text/markdown editor and "Parse Workflow" engine.
   - Instant template presets (*e.g., Incident Response Bot, Social Media Content Engine, CI/CD Pipeline*).
   - Grokpot breaks text into a tree of triggers, conditions, automated actions, and validation gates.
   - Option to link the generated Recipe Tree directly to an existing thought branch in the Mind Garden.

3. **Node Execution & Experience Growth**:
   - Inside any Recipe Node, user can review the Action Spec, simulate/mark action execution, add operational logs, or test conditions.
   - Completing actions awards Thought XP (+25 to +100 XP), triggering particle bloom animations and leveling up the node.
   - Once a node reaches Level 2 or 3, it "blooms", sprouting suggested deeper branches and automation optimizations.

4. **Bot Recipe Tree Navigator**:
   - Dedicated hierarchical tree view alongside the 2D radial garden graph.
   - Visual branch collapse/expand, step-by-step progress tracking, and bot export options (JSON / Markdown recipe format).

#### Visual Identity & Theme

- **Aesthetic Direction**: Bioluminescent Digital Arboretum — sleek, ethereal, high-tech organic, and luminous.
- **Color Palette**:
  - `Background`: Deep Abyss Black (`#080C14`) & Dark Nebula Navy (`#0F172A`)
  - `Bioluminescent Primary`: Neon Spring / Emerald (`#10B981`)
  - `Synaptic Cyan Accent`: Phosphor Glow (`#00F0FF`)
  - `Thought Violet / Secondary`: Astral Purple (`#8B5CF6`)
  - `Solar XP Amber`: Radiant Glow (`#F59E0B`)
  - `Surface Panels`: Frosted Obsidian (`#141E33` with 80% opacity and subtle glowing borders)
- **Typography**: Clean modern sans-serif typography with high-contrast numerical badges for XP, levels, and step counts.
- **Interactive Feedback**:
  - Custom canvas drawing for synaptic filament connections with animated energy pulse dots travelling along active paths.
  - Haptic feedback and spring bloom animations when nodes level up or workflows are parsed.
  - Intuitive gesture controls: pinch-to-zoom, drag-to-pan, and tap-to-focus with smooth camera centering.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Custom Canvas vs Webview / Graph Library**
  - *Chosen Approach*: Pure Jetpack Compose `Canvas` with touch gesture detection (`detectTransformGestures`, `detectTapGestures`) and customized node rendering.
  - *Why*: Eliminates heavy external dependencies, guarantees 60fps buttery fluid zooming and panning, and enables custom particle blooms and glowing filament lines native to Android.
  - *Alternatives Considered*: WebView with D3/Cytoscape was rejected due to slow render speeds, memory overhead, and jarring touch latency on mobile.

- **Decision 2: Workflow Parser Architecture**
  - *Chosen Approach*: On-device deterministic heuristic workflow decomposition engine that analyzes headings, numbered steps, conditional phrases ("if", "when", "then", "trigger", "verify"), and code blocks to generate structured recipes instantly, with clean extensible hooks.
  - *Why*: Instant offline performance, zero latency, no API key barriers on first launch, while maintaining rock-solid structure and reliability.

- **Decision 3: Persistence with Room Database**
  - *Chosen Approach*: Reactive Room database with Kotlin Flow streams for Garden Nodes, Tree Edges, Bot Recipes, Step Actions, and Experience Logs.
  - *Why*: Robust offline-first transactional storage. All user-created thought trees, customized recipes, and earned XP levels persist reliably across app restarts.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│         Scaffold, Bioluminescent Theme, Safe Insets, M3 Nav           │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
       ┌────────────────────────────┼───────────────────────────┐
       ▼                            ▼                           ▼
┌──────────────┐             ┌──────────────┐            ┌──────────────┐
│ GardenCanvas │             │ RecipeStudio │            │ TreeExplorer │
│ Interactive  │             │ Workflow Doc │            │ Hierarchical │
│ Knowledge    │             │ Parsing &    │            │ Automation   │
│ Graph        │             │ Ingestion    │            │ Step Viewer  │
└──────┬───────┘             └──────┬───────┘            └──────┬───────┘
       │                            │                           │
       └────────────────────────────┼───────────────────────────┘
                                    ▼
                     ┌───────────────────────────────┐
                     │        GrokpotViewModel       │
                     │  Garden State & XP Engine     │
                     │  Parser Logic & Node Filtering│
                     └──────────────┬────────────────┘
                                    │
                                    ▼
                     ┌───────────────────────────────┐
                     │       GrokpotRepository       │
                     │  Room DB (Nodes, Edges, Steps)│
                     └───────────────────────────────┘
```

#### Data Entities

1. **GardenNodeEntity**:
   - `id`: Long (PK)
   - `title`: String
   - `summary`: String
   - `category`: String ("Thought", "Trigger", "BotAction", "Decision", "Validation")
   - `level`: Int (1 = Sprout, 2 = Sapling, 3 = Blooming, 4 = Ancient Arbor)
   - `experience`: Int
   - `experienceToNext`: Int
   - `posX`: Float, `posY`: Float
   - `recipeId`: Long? (optional link to parent bot recipe)
   - `isCompleted`: Boolean
   - `bloomUnlocked`: Boolean

2. **GardenEdgeEntity**:
   - `id`: Long (PK)
   - `sourceNodeId`: Long
   - `targetNodeId`: Long
   - `relationType`: String ("LeadsTo", "Automates", "Requires", "SproutsFrom")

3. **BotRecipeEntity**:
   - `id`: Long (PK)
   - `title`: String
   - `description`: String
   - `rawDocument`: String
   - `createdAt`: Long
   - `rootNodeId`: Long

4. **RecipeStepEntity**:
   - `id`: Long (PK)
   - `recipeId`: Long
   - `stepIndex`: Int
   - `actionType`: String ("HTTP_CALL", "SCHEDULE_TASK", "FILTER_DATA", "NOTIFY_USER", "CONDITION_CHECK")
   - `description`: String
   - `botPayload`: String
   - `isExecuted`: Boolean
   - `xpReward`: Int

#### Interactive Component & State Mapping

- **Garden Canvas**:
  - `transformGestures`: updates canvas scale (0.4x - 2.5x) and pan offset `(panX, panY)`.
  - Node hit testing: maps tap coordinates to nearest node center; selects node and triggers bottom sheet inspection.
  - Edge drawing: computes cubic Bezier or straight vectors between node coordinates with animated traveling pulse dots.
- **Workflow Ingestion Dialog / View**:
  - Text field with instant paste support and template picker.
  - "Decompose & Sprout" button: parses text into recipe steps, creates connected garden nodes, positions them radially/hierarchically, and animates camera focus.
- **Node Inspector & XP Leveling Sheet**:
  - Shows node rank badge, XP progress bar with gradient glow.
  - "Execute Step" button: simulates execution, awards XP, triggers ripple particle bursts, and checks for level-up bloom.
  - "Sprout Sub-Thought" button: adds connected child thought node.
- **Recipe Step Navigator**:
  - Reorderable / checklist step view with live status indicators and recipe JSON export copy sheet.
