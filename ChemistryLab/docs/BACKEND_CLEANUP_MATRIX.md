# ChemistryLab - Matriz de limpieza del backend

> Rama: `refactor/educational-smiles-layer`
>
> Objetivo: limpiar el backend de motores antiguos de representación y consolidar la representación visual basada en SMILES + CDK + SVG.
>
> Este documento se usa como índice vivo del backend. Cada vez que se revise una clase o se tome una decisión de limpieza, debe actualizarse aquí.

## Leyenda

| Estado | Significado |
|---|---|
| MANTENER | Clase necesaria para la app actual. |
| MANTENER / REVISAR | Necesaria, pero conviene simplificar o comprobar dependencias. |
| TEMPORAL | Útil durante la reconstrucción, pero puede borrarse después. |
| REVISAR | No borrar aún; hay que comprobar usos reales. |
| CANDIDATA A BORRAR | Parece parte de motores viejos o duplicados. Borrar solo tras comprobar referencias. |
| BORRADA | Eliminada de la rama actual. |

---

# Estado actual de la representación

La dirección decidida y aplicada es:

```text
formula / SMILES de BD
    -> reglas mínimas de grupos reconocibles cuando proceda
    -> SMILES
    -> CDK
    -> SVG
```

No se mantiene otro motor basado en coordenadas manuales, DTOs de átomos/enlaces o VSEPR para las tarjetas.

## Flujo principal actual

```text
MoleculeCardRepresentationService
    -> oxoácido neutro desde EducationalOxoanionSmilesCatalog
    -> RepresentationSmilesOverrideService, solo casos curados puntuales
    -> IonicSmilesBuilderService, apoyo genérico mínimo para grupos iónicos
    -> canonicalSmiles / isomericSmiles de BD
    -> imagen PubChem
    -> fórmula
```

Frontend:

```text
MoleculeStructure.jsx
    SVG       -> inline SVG
    IMAGEN_2D -> img externa
    FORMULA   -> ChemicalFormulaText
```

---

# Matriz rápida de decisión

## Se queda casi seguro

```text
ChemistryLabApplication
CorsConfig
WebClientConfig
ElementoController
MoleculaController
SmilesToolController
ElementoEntity
MoleculaEntity
ElementoRepository
MoleculaRepository
ElementoDTO
MoleculaDTO
MoleculaImportRequest
MoleculaImportResponse
MoleculaRepresentacionDTO
ElementoMapper
MoleculaMapper
ElementoService
MoleculaService
MoleculaImportService
FormulaParserService
CompoundFamily
CompoundFamilyService
CompoundTypeLabelService
IonConfig
IonCatalogService
IonMatch
IonicFormulaResolver
chemistry.ionic.IonicFormulaResolution
EducationalOxoanionSmilesCatalog
RepresentationSmilesOverrideService
SmilesToSvgService
MoleculeCardRepresentationService
```

## Mantener / revisar

```text
IonicSmilesBuilderService
ImageRepresentationSource
EstadoOxidacionEntity
CdkDepictionPlayground
Smiles SVG playground HTML
```

## Ya borrado

```text
Estructura2DService
MoleculaFormulaService
MoleculaRepresentacionService
MoleculaRepresentacionIonicaService
MoleculaRepresentacionVseprService
OxidoIonico2DService
AtomoRepresentacionDTO
EnlaceRepresentacionDTO
RepresentationDecisionService
RepresentationDecision
RepresentationFamily
RepresentationStrategy
RepresentationInputService
RepresentationInputResult
RepresentationInputSource
EducationalSmilesComposer
RepresentationDecisionPlayground
```

---

# Registro de barrida

## Barrida 1 - RepresentationSmilesOverrideService

Estado: revisado y simplificado.

Hallazgo:

- Es la capa de overrides curados.
- Debe ir reduciéndose: los casos generales deben ir a catálogos/grupos o a BD, no a más reglas sueltas.
- Ya no decide oxoácidos neutros; esa prioridad vive solo en `MoleculeCardRepresentationService`.

Decisión:

- Mantener, pero no convertirla en otro motor.
- Usarla solo para casos concretos y excepciones visuales.
- Commit de simplificación: `c87ce3171b8d84b2900e5fe4680f0f6deaf36a08`.

## Barrida 2 - classification

Estado: resuelta.

Hallazgo:

- Existen `CompoundFamily`, `CompoundFamilyService` y `CompoundTypeLabelService`.
- `CompoundFamilyService` clasifica por fórmula parseada y categorías de elementos.
- `CompoundTypeLabelService` convierte familia a etiqueta visible.

Decisión:

- Mantener classification.
- Revisar la regla `esOrganica(atomos) || tieneSmiles(molecula)`, porque puede ser demasiado amplia.

## Barrida 3 - MoleculaRepresentacionService

Estado: BORRADA.

Hallazgo:

- Era el motor viejo de representación.
- Mezclaba classification, orgánicas por entrada química, sales/ácidos/hidróxidos/óxidos metálicos por representación iónica, estructura 2D interna, VSEPR y fórmula fallback.
- Dependía de `Estructura2DService`, `MoleculaRepresentacionIonicaService`, `MoleculaRepresentacionVseprService`, `RepresentationInputService`, `RepresentationSmilesOverrideService` y classification services.

Decisión:

- Eliminada del flujo visual.
- El endpoint `/api/moleculas/{id}/representacion` usa `MoleculeCardRepresentationService`.
- Commit de borrado: `f60f38ac607c063c4135902aba70cd4ecc5cf90a`.

## Barrida 4 - Estructura2DService

Estado: BORRADA.

Hallazgo:

- Servicio de representación 2D manual.
- Convertía `MolecularConnectivity` en `AtomoRepresentacionDTO` y `EnlaceRepresentacionDTO`.
- Tenía lógica manual para cadenas, posiciones, pares libres y H2O2.
- Pertenecía al motor antiguo basado en coordenadas y DTOs.

Decisión:

- Eliminada en la rama `refactor/educational-smiles-layer`.
- Commit de borrado: `e3e47ead213929cd79770c19eca211deeaae32cf`.

## Barrida 5 - representation package

Estado: resuelta.

Hallazgo:

- Había dos posibles capas de decisión: `MoleculeCardRepresentationService`, más simple y actual, y `RepresentationDecisionService` + enums/modelos.

Decisión:

- Se mantiene un solo orquestador final: `MoleculeCardRepresentationService`.
- Se eliminaron las capas abstractas no usadas.

## Barrida 6 - RepresentationDecisionService / Decision / Family / Strategy

Estado: BORRADA.

Hallazgo:

- `RepresentationDecisionService` decidía estrategias abstractas por familia.
- `RepresentationDecision`, `RepresentationFamily` y `RepresentationStrategy` solo daban soporte a esa capa.
- `RepresentationDecisionPlayground` era una herramienta manual ligada a ese sistema.

Decisión:

- Eliminadas por no formar parte del flujo actual SMILES + CDK + SVG.
- Commits: `44014c66a33d146ee5cc1f15080d51f3edadfd3a`, `f6746c46b9a7e93993f0f5ea037cdfe12725f63a`, `467c6860fb47fbbb0ad8d651e8d6a26e830dfbfb`, `37f706a81430874df729535c153f88392f904d05`, `5f77cb1a20af96acb5dd2c9c560e85ee30491a84`.

## Barrida 7 - SmilesGenerationService

Estado: pendiente de comprobación final.

Hallazgo previo:

- `completarSmiles` devolvía canonical si existe.
- `generarDesdeInchi` siempre devolvía `Optional.empty()`.
- No contiene lógica educational.
- No convierte realmente InChI a SMILES.

Decisión:

- Candidato a borrar si nadie lo usa.

## Barrida 8 - MoleculeCardRepresentationService

Estado: revisado y modificado.

Hallazgo:

- Es el flujo individual actual de representación.
- Prioriza oxoácidos neutros mediante `EducationalOxoanionSmilesCatalog.findNeutralOxoacid(formula)`.
- Después usa `RepresentationSmilesOverrideService`, `IonicSmilesBuilderService`, SMILES de BD, imagen PubChem y fórmula.

Decisión:

- Mantener como entrada principal.
- Falta revisar si el grid/listado debe consumir explícitamente el mismo endpoint o incorporar este servicio.

## Barrida 9 - RepresentationInputService / Result / Source

Estado: BORRADA.

Hallazgo:

- Decidía input por orden: canonicalSmiles, isomericSmiles, inchi, unknown.
- Devolvía origen y motivo.
- No convertía InChI a SMILES.
- Era una capa intermedia del orquestador antiguo.

Decisión:

- Eliminadas tras retirar `MoleculaRepresentacionService`.
- Commits: `fb72fb7200099358d70e2979c68e3e6ee67b051f`, `6a6459e4100e82145c92b2a6015b549277ef51cd`, `73c1a75ed3e8a832fb737ed0766c812dad8ff6bd`.

## Barrida 10 - IonicSmilesBuilderService

Estado: revisado y adaptado.

Hallazgo:

- Usa `IonicFormulaResolver` para obtener catión/anión.
- Usa `EducationalOxoanionSmilesCatalog` como fuente compartida de oxoaniones.
- Genera compactos para sales monoatómicas, hidróxidos, óxidos y cianuros.
- Para oxoaniones añade cationes como fragmentos separados con punto (`.`), lo que puede parecerse más a PubChem en sales iónicas.

Decisión:

- Mantener temporalmente.
- No convertirlo en motor visual nuevo.
- Adaptado a `IonMatch` e `IonicFormulaResolution` como records.
- Commit: `c26c811c0c336f151539dca92982fae7517ff72d`.

## Barrida 11 - MolecularConnectivityService

Estado: BORRADA / sin uso tras retirar motor VSEPR.

Hallazgo:

- Construía conectividad molecular desde fórmula visual.
- Usaba `FormulaParserService`, `ElementoRepository` y una lista de `MolecularConnectivityRule`.
- Solo aceptaba moléculas covalentes pequeñas: 2 o 3 tipos de átomos, máximo 6 átomos totales y todos no metales/metaloides.
- Esto era motor de representación manual/VSEPR-like, no la capa de SMILES.

Decisión:

- Retirada funcionalmente al eliminar `MoleculaRepresentacionVseprService` y `AtomoRepresentacionDTO`/`EnlaceRepresentacionDTO`.

## Barrida 12 - MolecularConnectivity y MolecularBond

Estado: BORRADA / sin uso tras retirar motor VSEPR.

Hallazgo:

- Modelos simples: `MolecularConnectivity` tenía central, bonds y lonePairs; `MolecularBond` tenía from, to y order.
- No contenían lógica.

Decisión:

- Borrables si quedan físicamente en el árbol local.
- No forman parte del flujo actual.

## Barrida 13 - chemistry.connectivity.rules

Estado: BORRADA / sin uso tras retirar motor VSEPR.

Clases revisadas:

- `MolecularConnectivityRule`
- `MolecularConnectivityContext`
- `DiatomicConnectivityRule`
- `HydrogenPeroxideConnectivityRule`
- `NitrogenDioxideConnectivityRule`
- `CovalentX2OConnectivityRule`

Hallazgo:

- Todas pertenecen al motor manual de conectividad.
- Los casos útiles ya están contemplados como SMILES curados.

Decisión:

- Borrables si quedan físicamente en el árbol local.
- No forman parte del flujo actual.

## Barrida 14 - MoleculaRepresentacionIonicaService

Estado: BORRADA.

Hallazgo:

- No generaba estructuras ni SVG. Generaba texto iónico tipo `Na+ + Cl-`, `Ca2+ + 2OH-`, `2H+ + CO3 2-`.
- Usaba `IonicFormulaResolver`, `IonCatalogService`, `FormulaParserService` y `ElementoRepository`.
- Contenía casos manuales: ácido carbónico, ácido bórico, hidróxido de aluminio, hidróxido amónico, dióxido de titanio, fosfatos y fosfatos ácidos.

Decisión:

- Eliminada del flujo visual.
- Commit de borrado: `4641f8811ce55368a1e9094dd4f7e71262d393db`.

## Barrida 15 - MoleculaRepresentacionVseprService

Estado: BORRADA.

Hallazgo:

- Dependía directamente de `MolecularConnectivityService`.
- Construía DTO VSEPR con átomo central, terminales, enlaces, pares libres, código AXE, geometría y polaridad.
- No generaba SMILES ni SVG CDK.

Decisión:

- Eliminada.
- Commit de borrado: `748dc4a2a4b2e89950fecbcad5aaf720884984b5`.

## Barrida 16 - OxidoIonico2DService

Estado: BORRADA.

Hallazgo:

- Servicio manual de dibujo de óxidos iónicos.
- Generaba `AtomoRepresentacionDTO` y `EnlaceRepresentacionDTO` con posiciones fijas.
- Para óxidos 1:1 creaba una red 3x3 de iones.
- Era origen de representaciones de óxidos grandes y feas.

Decisión:

- Eliminada.
- Commit de borrado: `934bde512c7cf2b14ac0e25a3d113192656ca067`.

## Barrida 17 - MoleculaFormulaService

Estado: BORRADA.

Hallazgo:

- Clase mínima.
- Solo inyectaba `FormulaParserService` y exponía `obtenerFormulaVisible(nombre, formula)`, que devolvía `formulaParserService.normalizarFormulaVisual(formula)`.
- El parámetro `nombre` no se usaba.

Decisión:

- Sustituida por `FormulaParserService.normalizarFormulaVisual(formula)` directamente en `MoleculaMapper`.
- Commits: `18d8dab2bd3b7b667791c7374a4db9758b0ab4fa`, `e98dc5a551328d8d096e8ca321b2ffbd79fab33c`.

## Barrida 18 - MoleculaController

Estado: revisado.

Hallazgo:

- El endpoint `/api/moleculas/{id}/representacion` usa `MoleculeCardRepresentationService`.
- No inyecta `MoleculaRepresentacionService`.

Decisión:

- Mantener controller.
- Confirmar qué usa el grid/listado porque los cambios del endpoint individual no se reflejaban en la página 2.

## Barrida 19 - MoleculaRepresentacionDTO / DTOs manuales

Estado: simplificada / DTOs manuales borrados.

Hallazgo:

- `MoleculaRepresentacionDTO` contenía restos del sistema antiguo: VSEPR, estructura 2D, átomos, enlaces, texto iónico y SMILES como representación primaria.
- `AtomoRepresentacionDTO` y `EnlaceRepresentacionDTO` eran propios del motor manual.

Decisión:

- `MoleculaRepresentacionDTO` queda reducida a SVG, imagen 2D y fórmula.
- `AtomoRepresentacionDTO` y `EnlaceRepresentacionDTO` eliminados.
- Commits: `2570f4d76ff2ff2e7c7a48bcf1da902893a5dcac`, `a5d8cbc721dc6bb1d474007e6ccd366fead300fd`, `d8c33ef6f8e45844bdca6c51578572cd01ea2583`.

## Barrida 20 - EducationalSmilesComposer

Estado: BORRADA.

Hallazgo:

- Componía fragmentos SMILES manualmente para oxoaniones y cationes.
- Era el inicio de otro motor educativo de SMILES.
- No formaba parte de la decisión final.

Decisión:

- Eliminada.
- Commit de borrado: `e87b28d36534ded046a00ae9da9854a86ba80d9c`.

## Barrida 21 - IonicFormulaResolver / records iónicos

Estado: simplificado.

Hallazgo:

- `IonicFormulaResolver` sigue siendo útil para resolver una fórmula como catión/anión.
- `resolverAnionRestante` era un resto del motor iónico textual antiguo.
- `IonMatch` e `IonicFormulaResolution` eran JavaBeans mutables sin lógica.

Decisión:

- Mantener `IonicFormulaResolver`, pero con una sola responsabilidad.
- Eliminado `resolverAnionRestante`.
- Convertidos `IonMatch` e `IonicFormulaResolution` a records.
- Commits: `9c9c668fd2bff7c7ad24ce9171a244fc1d8b3888`, `3509c6281f7fb75a61b13d35b0c2caf5a528b781`, `0eb945c7d20bb9c5d2bd02231972e50ae57e0309`.

## Barrida 22 - ImageRepresentationSource

Estado: revisar.

Hallazgo:

- Enum con fuentes de imagen/representación.
- Puede conservar restos semánticos del sistema antiguo.

Decisión:

- Revisar usos reales antes de borrar o simplificar.
