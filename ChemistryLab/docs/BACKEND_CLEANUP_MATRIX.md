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

Recursos:

```text
src/main/resources
├── chemistry
│   └── ions.json
├── data
└── application.properties
```

---

# Índice de representación actual

## Objetivo visual actual

La tarjeta de molécula debe estar pintada siempre que sea posible. El orden deseado es:

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

### Diferencia clave

No queremos esto para sales simples:

```text
NaCl -> [Na+].[Cl-]
```

porque CDK separa los fragmentos y pinta los iones lejos.

Para tarjeta se prefiere algo visual/compacto:

```text
NaCl -> [Na]Cl
NaOH -> [Na]O[H]
Ca(OH)2 -> [H]O[Ca]O[H]
```

No es una representación de enlace real perfecta, pero es mejor para una tarjeta educativa sencilla.

### Educational overrides prioritarios

| Fórmula | SMILES educativo esperado | Motivo |
|---|---|---|
| H2O | `[H]O[H]` | Mostrar hidrógenos explícitos. |
| OH2 | `[H]O[H]` | Alias de H2O. |
| NH3 | `[H]N([H])[H]` | Mostrar hidrógenos explícitos. |
| H3N | `[H]N([H])[H]` | Alias de NH3. |
| H2O2 | `[H]OO[H]` | Mostrar enlace O-O. |
| O2H2 | `[H]OO[H]` | Alias de H2O2. |
| CO2 | `O=C=O` | Lineal y reconocible. |
| O2C | `O=C=O` | Alias de CO2. |
| CO | `[C-]#[O+]` o `C#O` | Compacto; revisar cuál pinta mejor CDK. |
| SO2 | `O=S=O` | Óxido covalente claro. |
| O2S | `O=S=O` | Alias de SO2. |
| SO3 | `O=S(=O)=O` | Oxoácido/óxido covalente claro. |
| O3S | `O=S(=O)=O` | Alias de SO3. |
| NO | `N=O` | Compacto. |
| ON | `N=O` | Alias de NO. |
| NO2 | `O=[N+][O-]` | Mejor que fórmula. |
| O2N | `O=[N+][O-]` | Alias de NO2. |
| N2O | `N#[N+]O` | Representación compacta. |
| HCl | `[H]Cl` | Evitar disociación. |
| HF | `[H]F` | Evitar disociación. |
| HBr | `[H]Br` | Evitar disociación. |
| HI | `[H]I` | Evitar disociación. |
| NaCl | `[Na]Cl` | Sal simple compacta. |
| KCl | `[K]Cl` | Sal simple compacta. |
| NaOH | `[Na]O[H]` | Hidróxido compacto. |
| KOH | `[K]O[H]` | Hidróxido compacto. |
| Ca(OH)2 | `[H]O[Ca]O[H]` | Hidróxido compacto. |
| Mg(OH)2 | `[H]O[Mg]O[H]` | Hidróxido compacto. |

**Estado actual detectado:** `RepresentationSmilesOverrideService` existe, pero aún contiene valores que no son del todo educational para algunos casos, por ejemplo `H2O -> O`, `NH3 -> N`, `H2O2 -> OO`, y varios óxidos metálicos como fragmentos iónicos (`[Ca+2].[O-2]`). Eso debe corregirse.

---

# Matriz por clases

## Arranque y configuración

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ChemistryLabApplication | org.chemistrylab | MANTENER | Arranque Spring Boot. | No tocar. |
| CorsConfig | org.chemistrylab.config | MANTENER | CORS para frontend. | No tocar salvo dominios. |
| WebClientConfig | org.chemistrylab.config | MANTENER | Configura WebClient para PubChem. | Mantener si sigue importación. |

## Controllers

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ElementoController | controller | MANTENER | Endpoints de elementos/tabla periódica. | Mantener. |
| MoleculaController | controller | MANTENER / REVISAR | Endpoints de moléculas, importación y representación. | Revisar que solo delegue representación en `MoleculeCardRepresentationService`. |

## Entidades

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ElementoEntity | entity | MANTENER | Entidad de elemento químico. | Mantener. |
| MoleculaEntity | entity | MANTENER | Entidad central de moléculas/compuestos. | Mantener. |
| EstadoOxidacionEntity | entity | MANTENER / REVISAR | Estados de oxidación. | Mantener si clasificación/iones lo usan; si no, revisar más adelante. |

## Repositories

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ElementoRepository | repository | MANTENER | Acceso JPA a elementos. | Mantener. |
| MoleculaRepository | repository | MANTENER | Acceso JPA a moléculas. | Mantener. |

## DTOs

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ElementoDTO | dto | MANTENER | DTO de elementos para frontend. | Mantener. |
| MoleculaDTO | dto | MANTENER | DTO principal de moléculas. | Mantener. |
| MoleculaImportRequest | dto | MANTENER | Request para importar moléculas. | Mantener si PubChem sigue. |
| MoleculaImportResponse | dto | MANTENER | Resultado de importación. | Mantener si PubChem sigue. |
| MoleculaRepresentacionDTO | dto | MANTENER / REVISAR | DTO de SVG/imagen/fórmula. | Mantener, pero simplificar campos si sobran. |
| AtomoRepresentacionDTO | dto | CANDIDATA A BORRAR | DTO del motor manual antiguo de átomos. | Borrar si ningún endpoint actual lo usa. |
| EnlaceRepresentacionDTO | dto | CANDIDATA A BORRAR | DTO del motor manual antiguo de enlaces. | Borrar si ningún endpoint actual lo usa. |

## Mappers

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ElementoMapper | mapper | MANTENER | Entity -> DTO de elementos. | Mantener. |
| MoleculaMapper | mapper | MANTENER | Entity -> DTO de moléculas. | Mantener. |

## Servicios base

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| ElementoService | service | MANTENER | Lógica de elementos. | Mantener. |
| MoleculaService | service | MANTENER | Lógica de moléculas/listados/filtros. | Mantener. |
| MoleculaImportService | service | MANTENER | Importación desde PubChem. | Mantener. |
| MoleculaFormulaService | service | REVISAR | Utilidades de fórmula. | Ver si aporta algo frente a `FormulaParserService`. |

## Servicios de representación actuales / deseados

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| MoleculeCardRepresentationService | service | MANTENER | Entrada limpia para representación de tarjeta. | Debe ser la única vía principal. |
| SmilesToSvgService | representation | MANTENER | Convierte SMILES a SVG con CDK. | Mantener. |
| RepresentationSmilesOverrideService | representation | MANTENER / PRIORIDAD | Capa `educational`: overrides de fórmula a SMILES curado. | Ampliar y convertir en pieza central. |
| IonicSmilesBuilderService | representation | TEMPORAL / REVISAR | Construye SMILES desde fórmula iónica. | Mantener solo si genera SMILES visuales compactos; no usar disociación fea. |
| RepresentationInputService | representation | REVISAR | Decide qué input químico usar: canonical SMILES, isomeric SMILES o InChI. | Probablemente útil, pero la vía nueva puede hacerlo más simple. Revisar si se reutiliza. |
| RepresentationInputResult | representation | REVISAR | Resultado de decidir el input de representación. | Mantener solo si `RepresentationInputService` queda. |
| RepresentationInputSource | representation | REVISAR | Enum de origen del input: canonical, isomeric, InChI, etc. | Mantener solo si `RepresentationInputService` queda. |
| RepresentationDecisionService | representation | REVISAR | Servicio de decisión de estrategia de representación. | Puede ser parte del refactor nuevo o resto intermedio. Revisar antes de borrar. |
| RepresentationDecision | representation | REVISAR | Resultado de decisión de representación. | Mantener solo si se usa desde el flujo nuevo. |
| RepresentationFamily | representation | REVISAR | Familia de representación. | Puede duplicar `CompoundFamily`. Revisar. |
| RepresentationStrategy | representation | REVISAR | Estrategia seleccionada de representación. | Revisar si aporta frente al flujo simple. |
| ImageRepresentationSource | representation | REVISAR | Enum/origen de imagen representacional. | Mantener si `MoleculaRepresentacionDTO` lo usa realmente. |

## Servicios/motores antiguos de representación

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| MoleculaRepresentacionService | service | REVISAR / POSIBLE BORRAR | Motor anterior de representación: mezcla family classification, entrada química, iónica, estructura2D y VSEPR. | No debe alimentar tarjetas. Revisar si `MoleculaController` todavía lo inyecta. Si no se usa, borrar junto con dependencias antiguas. |
| MoleculaRepresentacionIonicaService | service | CANDIDATA A BORRAR | Representación iónica antigua. | Borrar si no hay lógica educational útil dentro. |
| MoleculaRepresentacionVseprService | service | CANDIDATA A BORRAR | VSEPR/geometría molecular. | Sacar del flujo de tarjetas. Mantener solo si se crea módulo educativo aparte. |
| Estructura2DService | service | CANDIDATA A BORRAR | Representación 2D manual basada en `MolecularConnectivityService`, `AtomoRepresentacionDTO` y `EnlaceRepresentacionDTO`. | Borrar si CDK SVG reemplaza. Revisar si alguna lógica de conectividad sirve para educational. |
| OxidoIonico2DService | service | CANDIDATA A BORRAR | Óxidos iónicos manuales. | Borrar si no se rescata nada útil. |

## PubChem

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| PubChemClient | pubchem | MANTENER | Cliente de PubChem. | Mantener. |
| PubChemCompoundData | pubchem | MANTENER | Modelo de respuesta PubChem. | Mantener. |

## Chemistry - catálogo iónico

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| IonConfig | chemistry.config | MANTENER | Modelo de ion del catálogo. | Mantener. |
| IonCatalogService | chemistry.catalog | MANTENER | Carga y busca iones desde `chemistry/ions.json`. | Mantener. |
| IonMatch | chemistry.ionic | MANTENER | Ion detectado + cantidad. | Mantener. |
| IonicFormulaResolution | chemistry.ionic | MANTENER | Resultado catión/anión de una fórmula. | Mantener. |
| IonicFormulaResolver | chemistry.ionic | MANTENER | Resuelve fórmulas tipo NaCl, Ca(OH)2, Na2CO3. | Mantener. |
| IonicFormulaResolution | chemistry.formula | REVISAR / POSIBLE DUPLICADO | Parece duplicado de `chemistry.ionic.IonicFormulaResolution`. | Comprobar usos y borrar si no se usa. |

## Chemistry - fórmulas y clasificación

| Clase/paquete | Estado | Qué hace | Decisión |
|---|---|---|---|
| FormulaParserService | MANTENER | Parseo de fórmulas con paréntesis y cantidades. | Mantener. |
| ElementNode | MANTENER / REVISAR | Nodo de elemento en árbol de fórmula. | Mantener si `FormulaParserService` lo usa. |
| FormulaNode | MANTENER / REVISAR | Interfaz/base de nodo de fórmula. | Mantener si el parser lo usa. |
| GroupNode | MANTENER / REVISAR | Nodo de grupo con multiplicador. | Mantener si el parser lo usa. |
| CompoundFamily | MANTENER | Enum de familia química. | Mantener; lo usa clasificación. |
| CompoundFamilyService | MANTENER / REVISAR | Clasifica moléculas por familia química. | Mantener si se usa en filtros/listados o representación. |
| CompoundTypeLabelService | MANTENER / REVISAR | Genera etiqueta visual/tipo de compuesto. | Mantener si alimenta tarjetas/filtros. |
| chemistry.smiles.SmilesGenerationService | REVISAR | Generación propia de SMILES desde fórmula/conectividad. | Revisar si duplica `RepresentationSmilesOverrideService` o si contiene lógica educational aprovechable. |
| chemistry.smiles.SmilesGenerationResult | REVISAR | Resultado de generación SMILES. | Mantener solo si se usa `SmilesGenerationService`. |

## Chemistry - conectividad

| Clase/paquete | Estado | Qué hace | Decisión |
|---|---|---|---|
| MolecularConnectivityService | REVISAR / POSIBLE BORRAR | Construye conectividad molecular desde fórmula. Usado por `Estructura2DService`. | Si solo alimenta estructura manual, candidato a borrar. Revisar si contiene reglas educational útiles. |
| MolecularConnectivity | REVISAR | Modelo de conectividad: átomos/enlaces. | Mantener solo si se conserva connectivity. |
| MolecularBond | REVISAR | Enlace molecular. | Mantener solo si se conserva connectivity. |
| chemistry.connectivity.rules.* | REVISAR | Reglas de conectividad. | Revisar una por una; probablemente motor viejo. |

## Tools / Playgrounds

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| CdkDepictionPlayground | tools | TEMPORAL | Pruebas de render CDK. | Mantener mientras estabilizamos SVG. Luego borrar. |
| RepresentationDecisionPlayground | tools | TEMPORAL | Pruebas de decisión de representación. | Mantener temporalmente. |

---

# Primer bloque recomendado para borrar/revisar

## Bloque A: representación manual antigua

Antes de borrar, buscar usos de:

```text
AtomoRepresentacionDTO
EnlaceRepresentacionDTO
Estructura2DService
MoleculaRepresentacionService
MoleculaRepresentacionIonicaService
MoleculaRepresentacionVseprService
OxidoIonico2DService
MolecularConnectivityService
MolecularConnectivity
MolecularBond
chemistry.connectivity.rules.*
```

Si solo se usan entre sí o desde endpoints antiguos, eliminarlos en bloque.

## Bloque B: duplicados de fórmula iónica

Buscar usos de:

```text
org.chemistrylab.chemistry.formula.IonicFormulaResolution
```

Si no se usa, borrar y dejar solo:

```text
org.chemistrylab.chemistry.ionic.IonicFormulaResolution
```

## Bloque C: educational

Antes de tocar más sales, recuperar/ampliar:

```text
RepresentationSmilesOverrideService
chemistry.smiles.SmilesGenerationService
```

El primero debe ser la capa principal de overrides por fórmula. El segundo puede contener lógica aprovechable para generar SMILES educativos; hay que revisarlo antes de borrar nada de `chemistry.smiles`.

## Orden recomendado de trabajo

1. Congelar flujo actual: `MoleculeCardRepresentationService` como única entrada.
2. Ampliar `RepresentationSmilesOverrideService` con educational SMILES.
3. Revisar `chemistry.smiles.SmilesGenerationService` para rescatar lógica useful.
4. Limitar `IonicSmilesBuilderService` a casos donde mejore visualmente.
5. Borrar representación manual antigua si ya no se usa.
6. Simplificar DTOs.
7. Simplificar frontend si quedan componentes viejos sin uso.

---

# Registro de barrida

## Barrida 1 - RepresentationSmilesOverrideService

Estado: revisado parcialmente.

Hallazgo:

- La clase existe y se usa como capa de overrides.
- Actualmente contiene algunos valores contrarios al objetivo educational:
  - `H2O -> O`, cuando debería ser `[H]O[H]`.
  - `NH3 -> N`, cuando debería ser `[H]N([H])[H]`.
  - `H2O2 -> OO`, cuando debería ser `[H]OO[H]`.
  - Óxidos metálicos como `CaO -> [Ca+2].[O-2]`, lo que puede volver a separar fragmentos.

Decisión:

- Mantener la clase.
- Convertirla en la capa educational principal.
- Corregir primero overrides explícitos de moléculas pequeñas.
- Después añadir sales/hidróxidos compactos caso a caso.

## Barrida 2 - Búsquedas de paquetes classification

Estado: resuelta con captura del árbol local.

Hallazgo:

- Sí existen:
  - `CompoundFamily`
  - `CompoundFamilyService`
  - `CompoundTypeLabelService`

Decisión:

- Mantener classification por ahora.
- Revisar después si el frontend/filtros usan directamente esas familias.

## Barrida 3 - MoleculaRepresentacionService

Estado: revisado.

Hallazgo:

- Es el motor viejo de representación.
- Mezcla demasiadas decisiones:
  - family classification;
  - orgánicas por entrada química;
  - sales/ácidos/hidróxidos/óxidos metálicos por representación iónica;
  - estructura 2D interna;
  - VSEPR;
  - fórmula fallback.
- Depende de muchos servicios antiguos:
  - `Estructura2DService`;
  - `MoleculaRepresentacionIonicaService`;
  - `MoleculaRepresentacionVseprService`;
  - `RepresentationInputService`;
  - `RepresentationSmilesOverrideService`;
  - classification services.

Decisión:

- No debe alimentar la tarjeta actual.
- Si `MoleculaController` ya usa `MoleculeCardRepresentationService`, esta clase queda como candidata clara a borrar.
- Antes de borrarla, revisar si algún endpoint secundario o tool la usa.

## Barrida 4 - Árbol real del backend

Estado: actualizado con capturas del usuario.

Hallazgo:

- El árbol real confirma paquetes que GitHub Search no devolvía:
  - `chemistry.analyzer.formula`
  - `chemistry.classification`
  - `chemistry.connectivity`
  - `chemistry.smiles`
  - `representation.RepresentationInput*`
  - `representation.RepresentationDecision*`

Decisión:

- Usar el árbol de IntelliJ como fuente para el inventario.
- GitHub Search no es fiable para confirmar inexistencia de clases en esta rama.

## Barrida 5 - Estructura2DService

Estado: inspección parcial desde captura.

Hallazgo:

- La clase existe en `service`.
- Importa:
  - `MolecularBond`
  - `MolecularConnectivity`
  - `MolecularConnectivityService`
  - `AtomoRepresentacionDTO`
  - `EnlaceRepresentacionDTO`
  - `MoleculaRepresentacionDTO`
- Tiene método `intentarConstruir(String formulaVisual)`.
- Parece convertir conectividad molecular interna en DTOs manuales de átomos/enlaces.

Decisión:

- Candidata a borrar si se elimina la representación manual.
- Antes de borrar, revisar si `MolecularConnectivityService` contiene alguna lógica que sirva para educational SMILES.

## Barrida 6 - representation package

Estado: árbol identificado.

Hallazgo:

- Hay dos posibles capas de decisión:
  - `MoleculeCardRepresentationService`, más simple y actual.
  - `RepresentationDecisionService` + enums/modelos, posiblemente refactor intermedio.

Decisión:

- Revisar `RepresentationDecisionService` antes de borrar, porque puede contener reglas útiles para elegir entre educational/CDK/PubChem.
- Evitar tener dos orquestadores finales. La app debería quedarse con uno solo.
