# 🌿 EDEN — Jogo de Plataformas 2D para Android

> Projeto académico desenvolvido na Universidade Católica de Moçambique (UCM)  
> Disciplina: **Programação Móvel** — 3.º Ano, 2.º Semestre  
> Framework: **LibGDX** | Plataforma: **Android** + Desktop (LWJGL3)

---

## 📖 Índice

1. [Visão Geral do Jogo](#visao-geral)
2. [Tecnologias Utilizadas](#tecnologias)
3. [Estrutura do Projeto](#estrutura)
4. [Arquitetura e Lógica](#arquitetura)
5. [Mecânicas de Jogo](#mecanicas)
6. [Sistema de Animações](#animacoes)
7. [Diagrama de Classes](#diagrama)
8. [Fluxo de Ecrãs](#fluxo)
9. [Como Compilar e Executar](#compilar)
10. [Estrutura de Assets](#assets)

---

## 🎮 Visão Geral do Jogo <a name="visao-geral"></a>

**EDEN** é um jogo de plataformas 2D de scrolling horizontal desenvolvido para Android. O jogador controla um personagem que se move por um nível com plataformas, usando um controlador virtual na ecrã tátil. O jogo inclui sistema de física (gravidade, colisões), animações por estados e câmera dinâmica que segue o personagem.

**Características principais:**
- Controlador virtual na ecrã para dispositivos móveis (botões esquerda, direita, salto)
- Suporte a teclado para testes no desktop (WASD / setas + ESPAÇO)
- Sistema de física com gravidade e colisões com plataformas
- Câmera dinâmica que segue o jogador horizontalmente
- Fundo de tela renderizado em parallax à câmera
- Animações por estado: Idle, Corrida, Salto, Queda

---

## 🛠️ Tecnologias Utilizadas <a name="tecnologias"></a>

| Tecnologia | Versão | Função |
|---|---|---|
| **LibGDX** | 1.14.2 | Framework de jogos 2D multiplataforma |
| **Java** | 8+ | Linguagem de programação principal |
| **Android SDK** | API 21–36 | Plataforma alvo (Android 5.0+) |
| **Gradle** | 9.7.1 | Sistema de build |
| **LWJGL3** | 3.4.1 | Backend desktop (para testes) |

---

## 📁 Estrutura do Projeto <a name="estrutura"></a>

O projeto segue a estrutura multi-módulo padrão do LibGDX:

```
EDEN/
├── core/                          ← Lógica partilhada (independente de plataforma)
│   └── src/main/java/mz/ac/ucm/eden/
│       ├── Main.java              ← Ponto de entrada da aplicação (Game)
│       ├── screens/
│       │   ├── MenuScreen.java    ← Ecrã do Menu Principal
│       │   └── GameScreen.java    ← Ecrã do Jogo (nível 1)
│       ├── entities/
│       │   ├── Player.java        ← Personagem jogável (física + animações)
│       │   └── SuperPlataform.java ← Plataformas sólidas do nível
│       ├── controls/
│       │   └── VirtualController.java ← Botões tácteis na ecrã
│       └── world/
│           └── GameWorld.java     ← (Reservado para expansão do mundo)
│
├── android/                       ← Módulo Android (APK)
│   ├── src/main/java/mz/ac/ucm/eden/android/
│   │   └── AndroidLauncher.java   ← Entrada da app no Android
│   ├── AndroidManifest.xml
│   └── build.gradle
│
├── lwjgl3/                        ← Módulo Desktop (para testes no PC)
│   └── src/main/java/mz/ac/ucm/eden/lwjgl3/
│       └── Lwjgl3Launcher.java    ← Entrada da app no desktop
│
├── assets/                        ← Recursos partilhados por todas as plataformas
│   ├── Morioh.png                 ← Fundo do nível de jogo
│   ├── player/                    ← Sprites do personagem
│   │   ├── frame_raw_0.png        ← Idle
│   │   ├── frame_raw_1-5.png      ← Corrida (5 frames)
│   │   ├── frame_raw_9.png        ← Salto (subida)
│   │   └── frame_raw_10.png       ← Queda
│   └── assets.txt                 ← Lista de assets gerada pelo Gradle
│
├── build.gradle                   ← Configuração raiz do Gradle
├── gradle.properties              ← Propriedades JVM e versões de dependências
└── settings.gradle                ← Lista de módulos do projeto
```

---

## 🏗️ Arquitetura e Lógica <a name="arquitetura"></a>

O projeto segue o padrão **Screen-based Architecture** do LibGDX:

```
AndroidLauncher / Lwjgl3Launcher
         │
         ▼
       Main.java  (extends Game)
         │  onCreate → setScreen(MenuScreen)
         │
    ┌────┴──────────────────────────┐
    ▼                               ▼
MenuScreen.java               GameScreen.java
(ecrã de menu)                (lógica do jogo)
    │                               │
    │ btnPlay.click()               │  atualiza e renderiza
    └──────► setScreen(GameScreen)  │
                                    ├── Player.update()
                                    ├── checkCollisions()
                                    ├── camera.update()
                                    └── render(batch)
```

### Ciclo de Vida de um Ecrã (LibGDX)

Cada `Screen` segue o seguinte ciclo de vida:

```
show()      ← chamado quando o ecrã se torna ativo (inicializar recursos)
  │
render()    ← chamado a cada frame (60fps) → atualizar lógica + desenhar
  │
resize()    ← chamado ao rodar o dispositivo ou redimensionar janela
  │
hide()      ← chamado quando o ecrã é substituído por outro
  │
dispose()   ← chamado para libertar memória (texturas, stages, etc.)
```

---

## ⚙️ Mecânicas de Jogo <a name="mecanicas"></a>

### 🎯 Física do Personagem (`Player.java`)

O sistema de física é implementado manualmente (sem motor externo):

```
A cada frame (delta time):
  velocidade.y += GRAVIDADE × delta     (-1500 unidades/s²)
  posição.x    += velocidade.x × delta
  posição.y    += velocidade.y × delta
  hitbox.setPosition(posição)
```

| Constante | Valor | Descrição |
|---|---|---|
| `GRAVITY` | -1500 u/s² | Força da gravidade (para baixo) |
| `JUMP_VELOCITY` | 600 u/s | Velocidade inicial do salto |
| `MOVE_SPEED` | 250 u/s | Velocidade horizontal de corrida |

### 💥 Sistema de Colisões

A deteção de colisões é feita por **AABB (Axis-Aligned Bounding Box)**:

```java
for (SuperPlataform platform : platforms) {
    if (player.getBounds().overlaps(platform.getBounds())) {
        player.handlePlatformCollision(platform.getBounds());
    }
}
```

Dois tipos de colisão são tratados:
- **Por cima (chão):** o jogador aterra numa plataforma → `isGrounded = true`
- **Por baixo (teto):** o jogador bate a cabeça numa plataforma

### 📷 Câmera Dinâmica

A câmera segue o jogador horizontalmente com um limite mínimo:

```java
camera.position.x = Math.max(player.getPosition().x + 100, 400);
```

O fundo é desenhado relativo à câmera para simular parallax:
```java
float bgX = camera.position.x - (viewportWidth / 2);
float bgY = camera.position.y - (viewportHeight / 2);
```

### 🕹️ Controlos

| Controlo | Teclado | Ecrã Tátil |
|---|---|---|
| Mover Esquerda | `←` ou `A` | Botão 🔴 (esquerda) |
| Mover Direita | `→` ou `D` | Botão 🟢 (direita) |
| Saltar | `ESPAÇO` ou `W` | Botão 🔵 (salto) |

---

## 🎨 Sistema de Animações <a name="animacoes"></a>

O personagem tem 4 estados de animação controlados pelo `enum State`:

```
IDLE    → frame_raw_0.png        (posição de repouso)
RUNNING → frame_raw_1 a 5.png    (ciclo de corrida — 5 frames a 0.10s cada)
JUMPING → frame_raw_9.png        (no ar, subindo — velocidade.y > 50)
FALLING → frame_raw_10.png       (no ar, descendo)
```

A transição de estados é determinada pela física:
```
isGrounded=false + velocidade.y > 50  → JUMPING
isGrounded=false + velocidade.y ≤ 50  → FALLING
isGrounded=true  + |velocidade.x| > 10 → RUNNING
isGrounded=true  + velocidade.x ≈ 0   → IDLE
```

O personagem é **espelhado horizontalmente** (flip X) para olhar na direção certa.

---

## 📊 Diagrama de Classes <a name="diagrama"></a>

```
┌─────────────────┐      ┌──────────────────────┐
│   Main          │      │   MenuScreen         │
│  (Game)         │─────▶│  (Screen)            │
│  + create()     │      │  - stage: Stage      │
└────────┬────────┘      │  - game: Game        │
         │               │  + show()            │
         │               │  + render(delta)     │
         │               └──────────────────────┘
         │               ┌──────────────────────┐
         └──────────────▶│   GameScreen         │
                         │  (Screen)            │
                         │  - player: Player    │
                         │  - platforms: Array  │
                         │  - camera            │
                         │  - controller        │
                         │  - background        │
                         │  + show()            │
                         │  + render(delta)     │
                         │  + checkCollisions() │
                         └──────┬───────────────┘
                                │ usa
               ┌────────────────┼────────────────┐
               ▼                ▼                ▼
   ┌──────────────────┐  ┌────────────────┐  ┌─────────────────────┐
   │  Player          │  │ SuperPlataform │  │ VirtualController   │
   │  - position      │  │ - bounds       │  │ - stage: Stage      │
   │  - velocity      │  │ - texture      │  │ - leftPressed       │
   │  - bounds        │  │ + render()     │  │ - rightPressed      │
   │  - currentState  │  │ + getBounds()  │  │ - jumpPressed       │
   │  - animation     │  │ + dispose()    │  │ + draw()            │
   │  + update()      │  └────────────────┘  │ + getStage()        │
   │  + render()      │                       │ + resize()          │
   │  + handleInput() │                       └─────────────────────┘
   │  + handleCollision()│
   └──────────────────┘
```

---

## 🔄 Fluxo de Ecrãs <a name="fluxo"></a>

```
┌──────────────┐
│  App Inicia  │
└──────┬───────┘
       │ Main.create()
       ▼
┌──────────────┐
│  MenuScreen  │  ← Ecrã inicial com título "EDEN" e botão "JOGAR"
│              │
│  [JOGAR] ────┼────────────────────────────────────┐
└──────────────┘                                    │
                                                    ▼
                                          ┌──────────────────┐
                                          │   GameScreen     │
                                          │                  │
                                          │  • Câmera 800x480│
                                          │  • Fundo (Morioh)│
                                          │  • Player        │
                                          │  • Plataformas   │
                                          │  • Controlador   │
                                          └──────────────────┘
```

### Nível de Teste (GameScreen)

O nível é montado com 4 plataformas hardcoded:

```
Plataforma      X    Y    Largura  Altura   Descrição
─────────────────────────────────────────────────────
Chão            0    50   1200     30       Chão principal contínuo
Plataforma 1    300  150  200      20       Plataforma suspensa
Plataforma 2    600  230  180      20       Plataforma mais alta
Plataforma 3    850  180  250      20       Plataforma larga
```

---

## ▶️ Como Compilar e Executar <a name="compilar"></a>

### Pré-requisitos

- **Android Studio** (recomendado) ou IntelliJ IDEA com plugin Android
- **Android SDK** (API 21 ou superior)
- **Java JDK 21** (instalado automaticamente pelo Gradle)

### Executar no Desktop (para testes rápidos)

```bash
# Na raiz do projeto:
./gradlew :lwjgl3:run
```

### Compilar APK Debug para Android

```bash
./gradlew :android:assembleDebug
# APK gerado em: android/build/outputs/apk/debug/android-debug.apk
```

### Instalar diretamente num dispositivo conectado

```bash
./gradlew :android:installDebug
```

### Configurações JVM (gradle.properties)

```properties
org.gradle.daemon=true
org.gradle.jvmargs=-Xms512M -Xmx2048m -Dfile.encoding=UTF-8
```

---

## 🖼️ Estrutura de Assets <a name="assets"></a>

```
assets/
├── Morioh.png           ← Fundo do nível (imagem cidade anime)
├── player/
│   ├── frame_raw_0.png  ← IDLE  (97×176 px)
│   ├── frame_raw_1.png  ← RUN frame 1 (120×170 px)
│   ├── frame_raw_2.png  ← RUN frame 2 (108×170 px)
│   ├── frame_raw_3.png  ← RUN frame 3 (104×181 px)
│   ├── frame_raw_4.png  ← RUN frame 4 (108×190 px)
│   ├── frame_raw_5.png  ← RUN frame 5 (116×166 px)
│   ├── frame_raw_9.png  ← JUMP_UP (113×190 px)
│   └── frame_raw_10.png ← JUMP_FALL (83×156 px)
└── assets.txt           ← Lista de assets (gerada automaticamente)
```

> **Nota sobre as Sprites:** Os sprites `frame_raw_*` são os frames originais do personagem com o corpo completo. Os `frame_aligned_*` (128×128px) foram descartados porque o redimensionamento cortava a cabeça do personagem.

---

## 👥 Equipa

| Nome | Contribuição |
|---|---|
| **Calton** | Design e criação dos sprites do personagem |
| **Equipa UCM** | Implementação da lógica de jogo e estrutura de código |

---

## 📚 Referências

- [Documentação Oficial do LibGDX](https://libgdx.com/wiki/)
- [LibGDX — Screen Management](https://libgdx.com/wiki/app/the-application-framework#screens)
- [LibGDX — SpriteBatch e Rendering](https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites)
- [LibGDX — Input Handling](https://libgdx.com/wiki/input/input-handling)

---

*Projeto desenvolvido como trabalho prático da disciplina de Programação Móvel — UCM Pemba, 2026*
