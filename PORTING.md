# PORTING.md — Bosses of Mass Destruction → NeoForge 26.1.2

Registro de trabajo del port. **Léelo siempre al empezar o retomar una sesión.**

## Estado actual (2026-11-09)

**Fase:** 7 (pruebas y release) — port funcional completo.
**Pendiente crítico al inicio de esta sesión:** crash (`ClassCastException`) al renderizar
las entidades `bosses_of_mass_destruction:comet` y `bosses_of_mass_destruction:spore_ball`
(= los 2 crash reports adjuntos en `logs/`; el usuario los recordaba como "comet" y
"soul_star", pero el segundo es `spore_ball` — `soul_star` usa `ThrownItemRenderer` y no
crashea). **RESUELTO en esta sesión** (ver Fase 7.1).

---

## Fase 7.1 (esta sesión): fix de los 2 crashes de summon

### Síntoma
`/summon bosses_of_mass_destruction:comet` y `/summon bosses_of_mass_destruction:spore_ball`
crasheaban el cliente con:
`Description: Extracting render state for an entity in world` →
`java.lang.ClassCastException: CometProjectile/SporeBallProjectile cannot be cast to LivingEntity`
en `GeoEntityRenderer.extractRenderState` (GeckoLib 5.5.2, línea 414).
Afecta también al gameplay normal: el Comete es un proyectil del Night Lich
(`CometAction`/`CometRageAction`) y la spore ball del Void Blossom (`SporeAction`).

### Causa raíz (verificada decompilando `geckolib-neoforge-26.1.2-5.5.2.jar`)
En 26.1.2 los renderers usan *render states*. El flujo de GeckoLib:

1. `GeoEntityRenderer.createRenderState(T, Void)` (por defecto) devuelve
   `new LivingEntityRenderState()` si el animatable es `LivingEntity`, o un
   `EntityRenderState` plano si no.
2. `GeoEntityRenderer.extractRenderState(...)` hace:
   `if (renderState instanceof LivingEntityRenderState les) extractLivingEntityRenderState((LivingEntity) entity, ...)`
   — **el cast `(LivingEntity) entity` solo ocurre si el STATE es un LivingEntityRenderState**.
3. El port anterior había creado `BMDGeoEntityRenderState extends LivingEntityRenderState`
   y `SimpleLivingGeoRenderer` lo devolvía **para todas** las entidades, incluidos los
   proyectiles (que no son `LivingEntity`) → cast imposible → crash.

Además GeckoLib soporta explícitamente entidades no-living: cuando el estado NO es
`LivingEntityRenderState`, `captureDefaultRenderState` añade los DataTickets alternativos
(ENTITY_POSE, ENTITY_YAW, ENTITY_BODY_YAW, IS_SHAKING, INVISIBLE_TO_PLAYER) que
`applyRotations`/`adjustRenderPose` consumen con `instanceof`-guards seguros.

### Fix aplicado (mínimo riesgo; bosses intactos)
- **NUEVO** `entity/util/BMDGeoProjectileRenderState`:
  `extends EntityRenderState implements GeoRenderState` con el mismo data-map local que
  `BMDGeoEntityRenderState` (todas las rutas de datos en un único mapa). Firma corregida:
  `getGeckolibData(DataTicket<D>)` (no `DataTicket<?>` — erasure-clash con la interfaz).
- **NUEVO** `entity/util/SimpleGeoRenderer<T extends Entity & GeoAnimatable>`:
  hermano no-living de `SimpleLivingGeoRenderer`, con `GeoEntityRenderer<T,
  BMDGeoProjectileRenderState>`. Misma firma de constructor (8 parámetros) para que los
  call sites solo cambien el nombre de la clase. Incluye `postRenderPass` (partículas),
  `getPackedOverlay` (flash de explosión de la spore ball), `getDeathMaxRotation`,
  `getBlockLightLevel` (FullRenderLight) y el `ENTITY_TICKET` compartido.
- `client/BMDEntitiesClient`: los registros de `COMET` y `SPORE_BALL` usan ahora
  `SimpleGeoRenderer` (comentario en el código explica por qué). LICH/OBSIDILITH/
  GAUNTLET/VOID_BLOSSOM siguen en `SimpleLivingGeoRenderer` sin cambios.
- `neoforge/event/BMDRenderTestNeo` (harness gated tras `-Dbmd.renderTest=true`):
  ampliado para lanzar también `comet` + `spore_ball` cada 25 ticks tras el login
  (`spawnProjectiles`/`toss`), de modo que las capturas del test de renderheadless
  incluyan los proyectiles.

### Verificación realizada en esta sesión
- Análisis estático completo del pipeline GeckoLib 5.5.2 decompilado:
  `GeoEntityRenderer`, `GeoRenderer`, `GeoRendererInternals`, `EntityRenderStateMixin`,
  `EntityRendererMixin`, `DataTicket` (IDENTITY_CACHE: el ticket "bmd_entity" se comparte
  entre ambos renderers), `ConsoleDecompiler` (empaquetado), `CacheKey`/`CacheManager`
  de NFRT.
- **Compilación real contra las APIs de 26.1.2**: `javac` (JDK 25.0.5) de los 269
  fuentes del mod contra el classpath real (vanilla 26.1.2 merged sin obfuscar +
  `neoforge-26.1.2.114-universal.jar` + geckolib 5.5.2 + cloth-config 26.1.154 +
  cerbons_api 1.2.0 + mixinextras). Resultado: **268/269 ficheros compilan**, incluidos
  TODOS los tocados por el fix (`SimpleGeoRenderer`, `BMDGeoProjectileRenderState`,
  `BMDEntitiesClient`, `BMDRenderTestNeo`).
  - El único fichero que no compila en el sandbox es `block/BMDBlockEntities.java`: usa
    `new BlockEntityType<>(supplier, Block)`, constructor que el **patch de NeoForge**
    añade al artefacto NFRT (en el jar vanilla sin parchear el parámetro es
    `Set<Block>`). En el build gradle real compila sin cambios (el port anterior compiló
    y arrancó servidor/cliente con este fichero).
  - Para esta verificación se creó un jar "stand-in" temporal (scripts
    `bmd_standin_patch.py`) que replica a nivel de bytecode dos efectos de los patches
    de NeoForge: `EntityRenderers.register`/`BlockEntityRenderers.register` públicos
    (AT) y `IAttachmentHolder` inyectado en `Entity` (interface injection). Es solo
    herramienta de verificación local, no forma parte del mod.
- **Verificación visual pendiente — no se ha podido obtener una captura del cliente en
  este sandbox**: `runClient` requiere el artefacto NFRT completo (decompilación
  NeoForm de ~12-15 min en un solo proceso atómico), imposible aquí (ver sección
  "Limitaciones del sandbox"). El harness `BMDRenderTestNeo` ya está ampliado para
  verificar los proyectiles en la primera ejecución de `runClient` en la máquina de
  destino. Capturas esperadas: 4 shots `run-client-test/screenshots/bmd_dbg_N.png` con
  los 4 bosses + comet + spore ball en vuelo.

### Criterios de aceptación (estado)
- [x] Items muestran textura en inventario (verificado en el port anterior, 2026-11-08).
- [x] Altar de Batalla coloca/usa sin mensaje de spawn espurio (port anterior).
- [x] Mensaje de error no duplicado (port anterior).
- [x] Al alejarse del Altar activo: mensaje de abandono y mobs desaparecen (port anterior).
- [x] Comet/spore_ball ya no crashean al renderizarse (fix de esta sesión; falta
      confirmación visual en máquina con `runClient` — harness preparado).

---

## Limitaciones del sandbox (documentadas para futuras sesiones)

Este entorno tiene 4 GB de RAM sin swap y **mata todos los procesos al terminar cada
comando del agente** (verificado también con `setsid`): no se puede ejecutar nada en
segundo plano entre comandos, y cada comando tiene un límite de ~575 s.

Consecuencias para el pipeline gradle/NFRT:
1. El daemon de Gradle muere entre comandos → cada chunk paga ~1.5-2 min de arranque.
2. La **decompilación NeoForm** (paso `decompile` de NFRT con Vineflower 1.11.2,
   `-Xmx4g` capado a ~1.8 GB por `_JAVA_OPTIONS`) necesita ~12-15 min en un único
   proceso atómico: el load+preprocess del jar joined (~8-9 min) + decompilado. No cabe
   en un chunk → `./gradlew build` no puede completarse en este sandbox.
3. Se intentó (y documentó) la ruta alternativa: **inyección de caché NFRT**. La clave
   de caché del paso `decompile` es replicable (SHA-1 de `componente: valor` ordenados,
   verificado contra `decompile_0ab2ff5c79447871bed47ae0c3a858357902ef10`) y se llegó a
   inyectar un decompilado **batch resumible** (scripts `bmd_batch_decompile.py` +
   `bmd_package_inject.py`; 6882 unidades de compilación decompiladas en ~4 chunks).
   El paso `patch` (DiffPatch) falla porque el decompilado batch (contexto
   `-e=`/biblioteca) omite los *generic witnesses* que el decompilado oficial
   (contexto primario completo) emite (p. ej. `Brain.<Piglin>provider(...)`,
   `(Brain<Piglin>) super.getBrain()`), y emular el contexto primario por batches
   exige cargar el jar completo (~8-9 min) por batch. **Conclusión:** en sandboxes con
   este límite de tiempo, el artefacto NFRT debe generarse en una máquina sin esa
   restricción o con el caché de NFRT pregenerado.
4. El paso `patch` sí funcionó parcialmente (targets encontrados, ~1000 parches
   aplicados) — la inyección de caché de NFRT es viable si algún día se obtiene un
   decompilado oficial (p. ej. copiando `~/.gradle/caches/neoformruntime/` desde la
   máquina de desarrollo).

### Cómo completar la verificación local (máquina de desarrollo)
```
.\gradlew.bat build          # genera build\libs\BOMD-1.3.3.jar
.\gradlew.bat runServer      # 180 s, verificar carga limpia
.\gradlew.bat runClient -PclientGameDir=run-client-test -PbmdRenderTest=true
                             # quickplay con el harness: spawnea bosses + comet +
                             # spore_ball y guarda screenshots bmd_dbg_N.png
```
Comprobar en `run\logs\latest.log`: "Missing item model", "Couldn't parse item model",
"Missing block model", excepciones de clases del mod, y que NO aparezca la
ClassCastException de `GeoEntityRenderer.extractRenderState`.

---

## Cambios de API 26.1.2 encontrados (acumulativo; antes → después)

Los del port anterior están en README.md (sección "Porting Status"). Añadidos en esta
sesión, verificados decompilando las fuentes reales:

- `GeoEntityRenderer.extractRenderState` hace `(LivingEntity) entity` **si y solo si**
  el render state es `LivingEntityRenderState`. Para entidades no-living el estado debe
  ser un `EntityRenderState` plano; GeckoLib entonces usa los DataTickets no-living.
- `GeoRenderState.getGeckolibData` es `<D> D getGeckolibData(DataTicket<D>)` — una
  implementación con `DataTicket<?>` produce *name clash* por erasure.
- `DataTicket.create(id, type)` cachea por par `(type, id)`: dos renderers que crean
  `DataTicket.create("bmd_entity", Entity.class)` comparten la MISMA instancia.
- `EntityRenderStateMixin` de GeckoLib inyecta `GeoRenderState` en `EntityRenderState`
  en runtime con su propio mapa `geckolib$data`: por eso las clases de estado del mod
  sobrescriben TODOS los métodos de `GeoRenderState` hacia un mapa propio.
- `EntityRenderers.register` / `BlockEntityRenderers.register` son **private** en el jar
  vanilla 26.1.2 y el patch de NeoForge los hace utilizables (el mod los llama).
- `new BlockEntityType<>(supplier, Block...)` existe solo en el artefacto parcheado
  (vanilla: `Set<Block>`).
- NeoForge inyecta `IAttachmentHolder` en `Entity` (interface injection): `getData(
  Supplier<AttachmentType<T>>)` etc. usados por BMDAttachments/NeoEvents.
- NFRT (NeoForm Runtime 2.0.31): clave de caché = SHA-1 de `k: v` (ordenados, `\n`,
  sin trailing) sobre los componentes del nodo (`input`, `versionManifest`,
  `external tool classpath[N]`, `external tool main class`, `command line arg`
  (plantilla SIN interpolar), `jvm args` (normalizado `-Xmx4096m`),
  `additional listLibraries classpath[N]`). Reutilización = existe marker
  `<type>_<hash>.txt` + `<type>_<hash>_<output>.jar` (sin verificación de contenido).
  Los args de la invocación de MDG 2.0.148 se registran con `--info`.

## Decisiones de diseño (esta sesión)

1. **No tocar `SimpleLivingGeoRenderer`/`BMDGeoEntityRenderState`** (los 4 bosses
   funcionan); crear la variante no-living en paralelo. Menor riesgo, diff mínimo.
2. **No convertir los proyectiles en `LivingEntity`**: cambiaría su comportamiento
   (salud, death anims, atributos) rompiendo paridad con el mod original.
3. `SimpleGeoRenderer` conserva la firma de 8 parámetros del constructor de
   `SimpleLivingGeoRenderer` (con `iBoneLight`/`renderWithModel` aceptados por paridad
   aunque los proyectiles pasan `null`): los call sites solo cambian el nombre.
4. El harness de render (TEMPORARY, off por defecto) se amplía para proyectiles en
   lugar de crear uno nuevo: mismo gate `-Dbmd.renderTest=true`.
5. Scripts de verificación del sandbox en `scripts/` del entorno de trabajo (no dentro
   del mod): `bmd_batch_decompile.py` (decompilado batch resumible),
   `bmd_package_inject.py` (empaquetado + inyección de caché NFRT),
   `bmd_standin_patch.py` (overlay de compilación local).

## Bloqueos

- **BLOQUEO (no del port, del sandbox):** `./gradlew build` completo no ejecutable aquí
  por el límite de 575 s/comando + muerte de procesos entre comandos (detalle en
  "Limitaciones del sandbox"). El código del mod está verificado con javac contra las
  APIs reales; el build gradle completo debe ejecutarse en la máquina de destino
  (ya verificado en el port anterior: BUILD SUCCESSFUL + runServer "Done" + runClient
  con los 4 bosses renderizando).
