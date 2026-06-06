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

## Servicios/motores antiguos de representación

| Clase | Paquete | Estado | Qué hace | Decisión |
|---|---|---|---|---|
| MoleculaRepresentacionService | service | REVISAR / POSIBLE BORRAR | Motor anterior de representación: mezcla family classification, entrada química, iónica, estructura2D y VSEPR. | No debe alimentar tarjetas. Revisar si `MoleculaController` todavía lo inyecta. Si no se usa, borrar junto con dependencias antiguas. |
| MoleculaRepresentacionIonicaService | service | CANDIDATA A BORRAR | Representación iónica antigua. | Borrar si no hay lógica educational útil dentro. |
| MoleculaRepresentacionVseprService | service | CANDIDATA A BORRAR | VSEPR/geometría molecular. | Sacar del flujo de tarjetas. Mantener solo si se crea módulo educativo aparte. |
| Estructura2DService | service | CANDIDATA A BORRAR | Representación 2D manual. | Borrar si CDK SVG reemplaza. |
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
| chemistry.classification.* | MANTENER / REVISAR | Clasifica orgánica/inorgánica/ácido/base/óxido/sal. | Existe en `MoleculaRepresentacionService`, pero la búsqueda de GitHub no lo devuelve bien. Confirmar árbol real local antes de borrar. |
| chemistry.analyzer.formula.* | REVISAR | Análisis de fórmulas. | No apareció en búsqueda por `Analyzer`; confirmar árbol real local. |
| chemistry.smiles.* | REVISAR | Utilidades SMILES. | Revisar si duplican `representation`. |

## Chemistry - conectividad

| Clase/paquete | Estado | Qué hace | Decisión |
|---|---|---|---|
| chemistry.connectivity.* | REVISAR | Reglas para deducir enlaces/conectividad. | Si solo alimenta motor manual, borrar. Si ayuda a educational, conservar parcialmente. |

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
```

Debe contener overrides por fórmula para moléculas y compuestos pequeños. Esto es prioritario frente a intentar resolver todo con un generador iónico automático.

## Orden recomendado de trabajo

1. Congelar flujo actual: `MoleculeCardRepresentationService` como única entrada.
2. Ampliar `RepresentationSmilesOverrideService` con educational SMILES.
3. Limitar `IonicSmilesBuilderService` a casos donde mejore visualmente.
4. Borrar representación manual antigua si ya no se usa.
5. Simplificar DTOs.
6. Simplificar frontend si quedan componentes viejos sin uso.

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

Estado: pendiente de confirmar estructura real.

Se intentó localizar `CompoundFamily`, `classification` y `CompoundTypeLabelService` mediante búsqueda en GitHub, pero no aparecieron resultados en la rama actual. Sin embargo, `MoleculaRepresentacionService` importa esas clases, así que el paquete existe en el árbol real o el índice de búsqueda no lo está devolviendo.

Decisión:

- No borrar nada relacionado con classification hasta revisar el árbol local o hasta compilar después de una limpieza.
- Si `MoleculaRepresentacionService` se elimina, comprobar si classification sigue siendo usado por `MoleculaService` o filtros del frontend.

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

## Barrida 4 - OxidoIonico2DService / Analyzer

Estado: búsqueda inicial.

Hallazgo:

- La búsqueda por `OxidoIonico2DService` y `Analyzer` no devolvió resultados en GitHub.
- Puede que esas clases ya no existan en la rama actual o que el índice no las devuelva.

Decisión:

- No incluirlas como borrado directo hasta confirmar con árbol local.
- Si aparecen en local, probablemente pertenecen a motores antiguos y deben revisarse junto con `Estructura2DService`.
