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

La dirección decidida es:

```text
formula / SMILES de BD
    -> reglas mínimas de grupos reconocibles cuando proceda
    -> SMILES
    -> CDK
    -> SVG
```

No se quiere mantener otro motor basado en coordenadas manuales, DTOs de átomos/enlaces o VSEPR para las tarjetas.

## Flujo principal deseado

```text
MoleculeCardRepresentationService
    -> oxoácido neutro desde EducationalOxoanionSmilesCatalog
    -> RepresentationSmilesOverrideService, solo casos curados puntuales
    -> IonicSmilesBuilderService, apoyo genérico para grupos iónicos
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

## Revisar antes de decidir

```text
IonicSmilesBuilderService
RepresentationInputService
RepresentationInputResult
RepresentationInputSource
RepresentationDecisionService
RepresentationDecision
RepresentationFamily
RepresentationStrategy
ImageRepresentationSource
EstadoOxidacionEntity
MoleculaRepresentacionDTO
AtomoRepresentacionDTO
EnlaceRepresentacionDTO
```

## Candidatos fuertes a borrar

```text
MoleculaFormulaService
MoleculaRepresentacionService
MoleculaRepresentacionIonicaService
MoleculaRepresentacionVseprService
OxidoIonico2DService
chemistry.smiles.SmilesGenerationService
chemistry.smiles.SmilesGenerationResult
chemistry.formula.IonicFormulaResolution
MolecularConnectivityService
MolecularConnectivity
MolecularBond
chemistry.connectivity.rules.*
```

## Ya borrado

```text
Estructura2DService
```

---

# Registro de barrida

## Barrida 1 - RepresentationSmilesOverrideService

Estado: revisado.

Hallazgo:

- Es la capa de overrides curados.
- Debe ir reduciéndose: los casos generales deben ir a catálogos/grupos o a BD, no a más reglas sueltas.
- Los casos curados puntuales como H2O, NH3, H2O2, CO, NO, NO2 o N2O pueden vivir aquí mientras se decide si se pisan en BD.

Decisión:

- Mantener, pero no convertirla en otro motor.
- Usarla solo para casos concretos y excepciones visuales.

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

Estado: revisado.

Hallazgo:

- Es el motor viejo de representación.
- Mezcla classification, orgánicas por entrada química, sales/ácidos/hidróxidos/óxidos metálicos por representación iónica, estructura 2D interna, VSEPR y fórmula fallback.
- Dependía de `Estructura2DService`, `MoleculaRepresentacionIonicaService`, `MoleculaRepresentacionVseprService`, `RepresentationInputService`, `RepresentationSmilesOverrideService` y classification services.

Decisión:

- No debe alimentar la tarjeta actual.
- `MoleculaController` ya usa `MoleculeCardRepresentationService` para `/api/moleculas/{id}/representacion`, así que esta clase queda como candidata clara a borrar si no hay otros usos.

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

Estado: árbol identificado.

Hallazgo:

- Hay dos posibles capas de decisión: `MoleculeCardRepresentationService`, más simple y actual, y `RepresentationDecisionService` + enums/modelos, posiblemente refactor intermedio.

Decisión:

- Evitar tener dos orquestadores finales. La app debería quedarse con uno solo.

## Barrida 6 - RepresentationDecisionService

Estado: revisado.

Hallazgo:

- Clase muy pequeña.
- No tiene `@Service`.
- Decide por `RepresentationFamily`: sales binarias, hidróxidos, oxisales y oxisales ácidas -> `EDUCATIONAL_CANDIDATE`; complejos y organofosfatos -> `SPECIAL_CASE`; desconocida -> `FALLBACK`; resto -> `DIRECT_SMILES`.

Decisión:

- Si no se usa, borrar o fusionar su lógica en `MoleculeCardRepresentationService`.
- Si se conserva, añadir `@Service` y convertirlo en parte oficial del flujo.

## Barrida 7 - SmilesGenerationService

Estado: revisado.

Hallazgo:

- `completarSmiles` devuelve canonical si existe.
- `generarDesdeInchi` siempre devuelve `Optional.empty()`.
- No contiene lógica educational.
- No convierte realmente InChI a SMILES.

Decisión:

- Candidato a borrar si nadie lo usa.

## Barrida 8 - MoleculeCardRepresentationService

Estado: revisado y modificado.

Hallazgo:

- Es el flujo individual actual de representación.
- Ya prioriza oxoácidos neutros mediante `EducationalOxoanionSmilesCatalog.findNeutralOxoacid(formula)`.
- Después usa `RepresentationSmilesOverrideService`, `IonicSmilesBuilderService`, SMILES de BD, imagen PubChem y fórmula.

Decisión:

- Mantener como entrada principal.
- Falta revisar si el grid/listado usa otro flujo distinto.

## Barrida 9 - RepresentationInputService

Estado: revisado.

Hallazgo:

- Decide input por orden: canonicalSmiles, isomericSmiles, inchi, unknown.
- Devuelve origen y motivo.
- No convierte InChI a SMILES.

Decisión:

- Decidir entre incorporarlo al flujo actual o borrarlo junto con `RepresentationInputResult` y `RepresentationInputSource`.

## Barrida 10 - IonicSmilesBuilderService

Estado: revisado y modificado.

Hallazgo:

- Usa `IonicFormulaResolver` para obtener catión/anión.
- Usa `EducationalOxoanionSmilesCatalog` como fuente compartida de oxoaniones.
- Genera compactos para sales monoatómicas, hidróxidos, óxidos y cianuros.
- Para oxoaniones añade cationes como fragmentos separados con punto (`.`), lo que puede parecerse más a PubChem en sales iónicas.

Decisión:

- Mantener temporalmente.
- No convertirlo en motor visual nuevo.

## Barrida 11 - MolecularConnectivityService

Estado: revisado.

Hallazgo:

- Construye conectividad molecular desde fórmula visual.
- Usa `FormulaParserService`, `ElementoRepository` y una lista de `MolecularConnectivityRule`.
- Solo acepta moléculas covalentes pequeñas: 2 o 3 tipos de átomos, máximo 6 átomos totales y todos no metales/metaloides.
- Esto es motor de representación manual/VSEPR-like, no la capa de SMILES.

Decisión:

- Candidato a borrar tras eliminar dependencias restantes.

## Barrida 12 - MolecularConnectivity y MolecularBond

Estado: revisado.

Hallazgo:

- Modelos simples: `MolecularConnectivity` tiene central, bonds y lonePairs; `MolecularBond` tiene from, to y order.
- No contienen lógica.

Decisión:

- Borrables si se borra `MolecularConnectivityService` y VSEPR.

## Barrida 13 - chemistry.connectivity.rules

Estado: revisado.

Clases revisadas:

- `MolecularConnectivityRule`
- `MolecularConnectivityContext`
- `DiatomicConnectivityRule`
- `HydrogenPeroxideConnectivityRule`
- `NitrogenDioxideConnectivityRule`
- `CovalentX2OConnectivityRule`

Hallazgo:

- Todas pertenecen al motor manual de conectividad.
- Lo útil de estas reglas son sus casos especiales: CO, H2O2, NO2 y posibles X2O/N2O.

Decisión:

- Candidatas a borrar junto con `MolecularConnectivityService`.
- Casos útiles ya están contemplados como SMILES curados.

## Barrida 14 - MoleculaRepresentacionIonicaService

Estado: revisado.

Hallazgo:

- No genera estructuras ni SVG. Genera texto iónico tipo `Na⁺ + Cl⁻`, `Ca²⁺ + 2OH⁻`, `2H⁺ + CO3²⁻`.
- Usa `IonicFormulaResolver`, `IonCatalogService`, `FormulaParserService` y `ElementoRepository`.
- Contiene casos manuales: ácido carbónico, ácido bórico, hidróxido de aluminio, hidróxido amónico, dióxido de titanio, fosfatos y fosfatos ácidos.

Decisión:

- Candidata a borrar del flujo visual.
- Podría tener valor futuro solo como explicación textual de disociación iónica, separada de tarjetas.

## Barrida 15 - MoleculaRepresentacionVseprService

Estado: revisado.

Hallazgo:

- Depende directamente de `MolecularConnectivityService`.
- Construye DTO VSEPR con átomo central, terminales, enlaces, pares libres, código AXE, geometría y polaridad.
- No genera SMILES ni SVG CDK.

Decisión:

- Candidata fuerte a borrar salvo futura sección educativa VSEPR separada.

## Barrida 16 - OxidoIonico2DService

Estado: revisado.

Hallazgo:

- Servicio manual de dibujo de óxidos iónicos.
- Genera `AtomoRepresentacionDTO` y `EnlaceRepresentacionDTO` con posiciones fijas.
- Para óxidos 1:1 crea una red 3x3 de iones.
- Es origen de representaciones de óxidos grandes y feas.

Decisión:

- Candidata muy fuerte a borrar.

## Barrida 17 - MoleculaFormulaService

Estado: revisado.

Hallazgo:

- Clase mínima.
- Solo inyecta `FormulaParserService` y expone `obtenerFormulaVisible(nombre, formula)`, que devuelve `formulaParserService.normalizarFormulaVisual(formula)`.
- El parámetro `nombre` no se usa.

Decisión:

- Candidata fuerte a borrar.
- Sustituir llamadas por `FormulaParserService.normalizarFormulaVisual(formula)` directamente.

## Barrida 18 - MoleculaController

Estado: revisado.

Hallazgo:

- El endpoint `/api/moleculas/{id}/representacion` usa `MoleculeCardRepresentationService`.
- No inyecta `MoleculaRepresentacionService`.

Decisión:

- Mantener controller.
- Confirmar qué usa el grid/listado porque los cambios del endpoint individual no se reflejan en la página 2.

## Barrida 19 - RepresentationInputResult y RepresentationInputSource

Estado: revisado.

Hallazgo:

- `RepresentationInputResult` es un wrapper simple con `value`, `source`, `reason` y `hasValue()`.
- `RepresentationInputSource` es un enum con `EDUCATIONAL_RULE`, `CANONICAL_SMILES`, `ISOMERIC_SMILES`, `INCHI`, `UNKNOWN`.

Decisión:

- Mantener solo si se integra `RepresentationInputService` en `MoleculeCardRepresentationService`.
- Si no, borrar los tres juntos.

## Barrida 20 - RepresentationDecision, RepresentationFamily, RepresentationStrategy

Estado: revisado.

Hallazgo:

- `RepresentationDecision` es un record con familia, estrategia y motivo.
- `RepresentationFamily` es otro enum de familias, más detallado que `CompoundFamily`, pero no parece conectado al flujo actual.
- `RepresentationStrategy` enumera estrategias como `DIRECT_SMILES`, `INCHI_CONVERSION`, `EDUCATIONAL_CANDIDATE`, `SPECIAL_CASE`, `FALLBACK`.

Decisión:

- Candidatas a borrar junto con `RepresentationDecisionService`, salvo que se decida rehacer un orquestador formal.

## Barrida 21 - ImageRepresentationSource

Estado: revisado.

Hallazgo:

- Enum con fuentes: `PUBCHEM_IMAGE_2D`, `SMILES`, `STRUCTURE_2D`, `VSEPR`, `CARD_TEXT_ONLY`, `FORMULA_ONLY`.
- Conserva valores del sistema antiguo (`STRUCTURE_2D`, `VSEPR`).

Decisión:

- Candidata a borrar si no hay usos reales.
- Si se mantiene, limpiar valores antiguos.
