# ChemistryLab - Matriz de limpieza del backend

> Rama: `cleanup/minimal-chemistrylab`
>
> Objetivo: decidir qué clases se mantienen, cuáles se revisan y cuáles son candidatas a borrar antes de seguir añadiendo representación química.
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

---

# Árbol real del backend observado

Según capturas del árbol local en IntelliJ, el backend actual tiene esta estructura principal:

```text
src/main/java/org/chemistrylab
├── ChemistryLabApplication
├── chemistry
│   ├── analyzer.formula
│   │   ├── ElementNode
│   │   ├── FormulaNode
│   │   └── GroupNode
│   ├── catalog
│   │   └── IonCatalogService
│   ├── classification
│   │   ├── CompoundFamily
│   │   ├── CompoundFamilyService
│   │   └── CompoundTypeLabelService
│   ├── config
│   │   └── IonConfig
│   ├── connectivity
│   │   ├── rules
│   │   │   ├── CovalentX2OConnectivityRule
│   │   │   ├── DiatomicConnectivityRule
│   │   │   ├── HydrogenPeroxideConnectivityRule
│   │   │   ├── MolecularConnectivityContext
│   │   │   ├── MolecularConnectivityRule
│   │   │   └── NitrogenDioxideConnectivityRule
│   │   ├── MolecularBond
│   │   ├── MolecularConnectivity
│   │   └── MolecularConnectivityService
│   ├── formula
│   │   ├── FormulaParserService
│   │   └── IonicFormulaResolution
│   ├── ionic
│   │   ├── IonicFormulaResolution
│   │   ├── IonicFormulaResolver
│   │   └── IonMatch
│   └── smiles
│       ├── SmilesGenerationResult
│       └── SmilesGenerationService
├── config
│   ├── CorsConfig
│   └── WebClientConfig
├── controller
│   ├── ElementoController
│   └── MoleculaController
├── dto
│   ├── AtomoRepresentacionDTO
│   ├── ElementoDTO
│   ├── EnlaceRepresentacionDTO
│   ├── MoleculaDTO
│   ├── MoleculaImportRequest
│   ├── MoleculaImportResponse
│   └── MoleculaRepresentacionDTO
├── entity
│   ├── ElementoEntity
│   ├── EstadoOxidacionEntity
│   └── MoleculaEntity
├── mapper
│   ├── ElementoMapper
│   └── MoleculaMapper
├── pubchem
│   ├── PubChemClient
│   └── PubChemCompoundData
├── repository
│   ├── ElementoRepository
│   └── MoleculaRepository
├── representation
│   ├── ImageRepresentationSource
│   ├── IonicSmilesBuilderService
│   ├── RepresentationDecision
│   ├── RepresentationDecisionService
│   ├── RepresentationFamily
│   ├── RepresentationInputResult
│   ├── RepresentationInputService
│   ├── RepresentationInputSource
│   ├── RepresentationSmilesOverrideService
│   ├── RepresentationStrategy
│   └── SmilesToSvgService
├── service
│   ├── ElementoService
│   ├── Estructura2DService
│   ├── MoleculaFormulaService
│   ├── MoleculaImportService
│   ├── MoleculaRepresentacionIonicaService
│   ├── MoleculaRepresentacionService
│   ├── MoleculaRepresentacionVseprService
│   ├── MoleculaService
│   ├── MoleculeCardRepresentationService
│   └── OxidoIonico2DService
└── tools
```

---

# Índice de representación actual

La tarjeta de molécula debe estar pintada siempre que sea posible:

```text
MoleculeCardRepresentationService
    -> RepresentationSmilesOverrideService      // capa educational
    -> IonicSmilesBuilderService                // solo si genera SMILES visual compacto
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

## Punto más importante: capa `educational`

La parte que hay que recuperar no es un simple formateo bonito de fórmulas. Es una capa de **SMILES educativos/curados**.

Debe vivir principalmente en:

```text
org.chemistrylab.representation.RepresentationSmilesOverrideService
```

Función esperada:

```text
formula química -> SMILES educativo -> CDK -> SVG
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
RepresentationSmilesOverrideService
SmilesToSvgService
MoleculeCardRepresentationService
```

## Revisar antes de decidir

```text
MoleculaFormulaService
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
```

## Candidatos fuertes a borrar

```text
MoleculaRepresentacionService
MoleculaRepresentacionIonicaService
MoleculaRepresentacionVseprService
Estructura2DService
OxidoIonico2DService
AtomoRepresentacionDTO
EnlaceRepresentacionDTO
chemistry.smiles.SmilesGenerationService
chemistry.smiles.SmilesGenerationResult
chemistry.formula.IonicFormulaResolution
MolecularConnectivityService
MolecularConnectivity
MolecularBond
chemistry.connectivity.rules.*
```

---

# Registro de barrida

## Barrida 1 - RepresentationSmilesOverrideService

Estado: revisado.

Hallazgo:

- La clase existe y se usa como capa de overrides.
- Actualmente contiene algunos valores contrarios al objetivo educational:
  - `H2O -> O`, cuando debería ser `[H]O[H]`.
  - `NH3 -> N`, cuando debería ser `[H]N([H])[H]`.
  - `H2O2 -> OO`, cuando debería ser `[H]OO[H]`.
  - Óxidos metálicos como `CaO -> [Ca+2].[O-2]`, lo que puede volver a separar fragmentos.
- Contiene buenos overrides para algunos covalentes pequeños:
  - `SO3 -> O=S(=O)=O`
  - `SO2 -> O=S=O`
  - `NO -> N=O`
  - `NO2 -> O=[N+][O-]`
  - `CO2 -> O=C=O`

Decisión:

- Mantener la clase.
- Convertirla en la capa educational principal.
- Corregir primero overrides explícitos de moléculas pequeñas.
- Después añadir sales/hidróxidos compactos caso a caso.

## Barrida 2 - classification

Estado: resuelta.

Hallazgo:

- Existen `CompoundFamily`, `CompoundFamilyService` y `CompoundTypeLabelService`.
- `CompoundFamilyService` clasifica por fórmula parseada y categorías de elementos.
- `CompoundTypeLabelService` solo convierte familia a etiqueta visible.

Decisión:

- Mantener classification.
- Revisar la regla `esOrganica(atomos) || tieneSmiles(molecula)`, porque puede ser demasiado amplia.

## Barrida 3 - MoleculaRepresentacionService

Estado: revisado.

Hallazgo:

- Es el motor viejo de representación.
- Mezcla demasiadas decisiones: classification, orgánicas por entrada química, sales/ácidos/hidróxidos/óxidos metálicos por representación iónica, estructura 2D interna, VSEPR y fórmula fallback.
- Depende de `Estructura2DService`, `MoleculaRepresentacionIonicaService`, `MoleculaRepresentacionVseprService`, `RepresentationInputService`, `RepresentationSmilesOverrideService` y classification services.

Decisión:

- No debe alimentar la tarjeta actual.
- Si `MoleculaController` ya usa `MoleculeCardRepresentationService`, esta clase queda como candidata clara a borrar.

## Barrida 4 - Árbol real del backend

Estado: actualizado con capturas del usuario.

Hallazgo:

- El árbol real confirma paquetes que GitHub Search no devolvía.

Decisión:

- Usar el árbol de IntelliJ como fuente para el inventario.
- GitHub Search no es fiable para confirmar inexistencia de clases en esta rama.

## Barrida 5 - Estructura2DService

Estado: inspección parcial desde captura.

Hallazgo:

- La clase existe en `service`.
- Importa `MolecularBond`, `MolecularConnectivity`, `MolecularConnectivityService`, `AtomoRepresentacionDTO`, `EnlaceRepresentacionDTO` y `MoleculaRepresentacionDTO`.
- Tiene método `intentarConstruir(String formulaVisual)`.
- Parece convertir conectividad molecular interna en DTOs manuales de átomos/enlaces.

Decisión:

- Candidata a borrar si se elimina la representación manual.

## Barrida 6 - representation package

Estado: árbol identificado.

Hallazgo:

- Hay dos posibles capas de decisión: `MoleculeCardRepresentationService`, más simple y actual, y `RepresentationDecisionService` + enums/modelos, posiblemente refactor intermedio.

Decisión:

- Evitar tener dos orquestadores finales. La app debería quedarse con uno solo.

## Barrida 7 - RepresentationDecisionService

Estado: revisado.

Hallazgo:

- Clase muy pequeña.
- No tiene `@Service`.
- Decide por `RepresentationFamily`: sales binarias, hidróxidos, oxisales y oxisales ácidas -> `EDUCATIONAL_CANDIDATE`; complejos y organofosfatos -> `SPECIAL_CASE`; desconocida -> `FALLBACK`; resto -> `DIRECT_SMILES`.

Decisión:

- Si no se usa, borrar o fusionar su lógica en `MoleculeCardRepresentationService`.
- Si se conserva, añadir `@Service` y convertirlo en parte oficial del flujo.

## Barrida 8 - SmilesGenerationService

Estado: revisado.

Hallazgo:

- `completarSmiles` devuelve canonical si existe.
- `generarDesdeInchi` siempre devuelve `Optional.empty()`.
- No contiene lógica educational.
- No convierte realmente InChI a SMILES.

Decisión:

- Candidato a borrar si nadie lo usa.

## Barrida 9 - MoleculeCardRepresentationService

Estado: revisado.

Hallazgo:

- Es el flujo actual de tarjeta.
- Orden: `RepresentationSmilesOverrideService`, `IonicSmilesBuilderService`, canonical/isomeric SMILES, imagen PubChem, fórmula.
- Genera SVG mediante `SmilesToSvgService.renderSvg(smiles)`.
- No usa `RepresentationInputService`, aunque duplica parte de su lógica.

Decisión:

- Mantener como entrada principal.
- El problema visual actual no está aquí, sino en qué SMILES devuelven `RepresentationSmilesOverrideService` e `IonicSmilesBuilderService`.

## Barrida 10 - RepresentationInputService

Estado: revisado.

Hallazgo:

- Decide input por orden: canonicalSmiles, isomericSmiles, inchi, unknown.
- Devuelve origen y motivo.
- No convierte InChI a SMILES.

Decisión:

- Decidir entre incorporarlo al flujo actual o borrarlo junto con `RepresentationInputResult` y `RepresentationInputSource`.

## Barrida 11 - IonicSmilesBuilderService

Estado: revisado.

Hallazgo:

- Usa `IonicFormulaResolver` para obtener catión/anión.
- Tiene tabla de oxoaniones.
- Genera compactos para sales monoatómicas, hidróxidos, óxidos y cianuros.
- Para oxoaniones añade cationes como fragmentos separados con punto (`.`), lo que todavía puede provocar que CDK coloque iones alejados.

Decisión:

- Mantener temporalmente.
- Las sales/hidróxidos importantes deberían pasar primero por `RepresentationSmilesOverrideService` con overrides educational específicos.

## Barrida 12 - MolecularConnectivityService

Estado: revisado.

Hallazgo:

- Construye conectividad molecular desde fórmula visual.
- Usa `FormulaParserService`, `ElementoRepository` y una lista de `MolecularConnectivityRule`.
- Solo acepta moléculas covalentes pequeñas: 2 o 3 tipos de átomos, máximo 6 átomos totales y todos no metales/metaloides.
- Intenta aplicar reglas específicas primero.
- Si ninguna regla aplica, elige átomo central, terminales, órdenes de enlace y pares libres.
- Su salida es `MolecularConnectivity`, con átomo central, lista de `MolecularBond` y lonePairs.
- Esto es motor de representación manual/VSEPR-like, no la capa educational de SMILES.

Decisión:

- Candidato a borrar si se elimina `Estructura2DService`.
- Antes de borrar, revisar reglas en `chemistry.connectivity.rules.*`, porque podrían contener casos explícitos útiles, aunque probablemente estén orientadas al motor manual.

## Barrida 13 - MolecularConnectivity y MolecularBond

Estado: revisado.

Hallazgo:

- Son modelos simples: `MolecularConnectivity` tiene central, bonds y lonePairs; `MolecularBond` tiene from, to y order.
- No contienen lógica.

Decisión:

- Borrables si se borra `MolecularConnectivityService` y `Estructura2DService`.

## Barrida 14 - chemistry.connectivity.rules

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
- `MolecularConnectivityRule` solo define `tryBuild(context)`.
- `MolecularConnectivityContext` solo transporta `formulaVisual`, `atomosFormula` y `elementos`.
- `DiatomicConnectivityRule` construye enlaces para moléculas de 2 átomos y trata CO como triple enlace.
- `HydrogenPeroxideConnectivityRule` fuerza H-O-O-H.
- `NitrogenDioxideConnectivityRule` fuerza NO2 con N central y dos oxígenos.
- `CovalentX2OConnectivityRule` intenta representar fórmulas X2O con enlace X-X y X-O.
- Lo interesante de estas reglas no es la conectividad manual, sino que confirman qué casos deberían estar en educational overrides:
  - CO
  - H2O2
  - NO2
  - posibles X2O/N2O

Decisión:

- Marcar `chemistry.connectivity.rules.*` como candidato fuerte a borrar junto con `MolecularConnectivityService` y `Estructura2DService`.
- Rescatar antes sus casos como entradas de `RepresentationSmilesOverrideService`.
