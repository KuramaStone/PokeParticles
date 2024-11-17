# PokeParticles

This Fabric 1.20.1 mod allows you to define custom particle effects for Cobblemon, which are triggered based on certain conditions.

---

## **Creating a New Effect**

1. **Define the Effect Name**
   Each effect begins with its unique identifier (e.g., `fire aura`, `powerful bands`). This identifier is used to reference the effect in the config.

2. **Configure Basic Properties**
   - **`animation`**: Choose the animation type (e.g., `AURA`, `ORBIT`, `DUAL_DISK`).
   - **`size modifier`**: Adjust the size of the effect (e.g., `1.0`, `1.25`).
   - **`priority`**:
     - **`ignores priority`**: Set to `true` if the effect should always appear, bypassing other priority rules.
     - **`priority group`**: Define the group the effect belongs to (`normal`, `special`, etc.). This allows for easy overriding within a group.
     - **`priority`**: Set the priority value (higher values take precedence).

3. **Set Conditions**
   Define the criteria that must be met for the effect to apply.
   - **`target pokemon`**: Specify the Pokémon this effect applies to (e.g., `charmander`, `charmeleon`, `charizard`). Use `any` for all Pokémon.
   - **`shiny only`** (optional): Set to `true` if this effect is exclusive to shiny Pokémon.
   - **`target form`** (optional): Define the form the Pokémon must have (use `any` to ignore).
   - **`minimum level`** (optional): Set the minimum level required for the effect to apply.

4. **Define Particle Properties**
   Specify the particles used in the effect and their behavior.
   - **`particles to spawn`**: A list of particle types and their configurations.
     - **`percentage`**: The percentage chance that this particle will be picked
     - **`scaled amount`**: The number of particles per cubic block. Larger pokemon will have more particles.
     - **`data`** (optional): Additional particle-specific data (e.g., `255/0/255` for RGB colors).

---

## **Example Configuration**

### **Fire Aura**
```yaml
fire aura:
  animation: AURA
  size modifier: 1.25
  priority:
    ignores priority: false
    priority group: normal
    priority: 100
  conditions:
    target pokemon:
      - charmander
      - charmeleon
      - charizard
    shiny only: false
    target form: any
    minimum level: 50
  particles to spawn:
    "minecraft:dust":
      percentage: 100
      scaled amount: 0.5
      data: 255/0/255
