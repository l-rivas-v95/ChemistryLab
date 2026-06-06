# ChemistryLab - Matriz de limpieza del backend

> Rama: `cleanup/minimal-chemistrylab`
>
> Objetivo: decidir qué clases se mantienen, cuáles se revisan y cuáles son candidatas a borrar antes de seguir añadiendo representación química.

## Leyenda

| Estado | Significado |
|---|---|
| MANTENER | Clase necesaria para la app actual. |
| MANTENER / REVISAR | Necesaria, pero conviene simplificar o comprobar dependencias. |
| TEMPORAL | Útil durante la reconstrucción, pero puede borrarse después. |
| REVISAR | No borrar aún; hay que comprobar usos reales. |
| CANDIDATA A BORRAR | Parece parte de motores viejos o duplicados. Borrar solo tras comprobar referencias. |

## Punto más importante: capa `educational`

La parte que hay que recuperar no es un simple formateo bonito de fórmulas. Es una capa de **SMILES educativos/curados**.

Esa capa debe vivir principalmente en:

```text
org.chemistrylab.representation.RepresentationSmilesOverrideService
```

Función esperada:

```text
formula química -> SMILES educativo -> CDK -> SVG
```

Ejemplos:

| Fórmula | SMILES educativo esperado | Motivo |
|---|---|---|
| H2O | `[H]O[H]` | Mostrar hidrógenos explícitos. |
| NH3 | `[H]N([H])[H]` | Mostrar pirámide/estructura simple reconocible. |
| H2O2 | `[H]OO[H]` | Mostrar enlace O-O. |
| CO2 | `O=C=O` | Lineal y reconocible. |
| SO3 | `O=S(=O)=O` | Oxoácido/óxido covalente más claro. |
| HCl | `[H]Cl` | Evitar representación iónica separada. |
| HF | `[H]F` | Evitar representación iónica separada. |
| NaCl | `[Na]Cl` o forma compacta equivalente | Forzar cercanía visual. |
| NaOH | `[Na]O[H]` | Evitar Na+ y OH- flotando. |
| Ca(OH)2 | `[H]O[Ca]O[H]` | Hidróxido compacto. |

La capa `IonicSmilesBuilderService` puede ayudar, pero solo si genera SMILES visuales compactos. Si produce fragmentos tipo `[Na+].[Cl-]`, CDK separa los iones y vuelve a quedar mal.

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
| MoleculaRepresentacionService | service | REVISAR | Motor anterior de representación. | Revisar usos. Probable candidato a borrar si no alimenta nada. |
| MoleculaRepresentacionIonicaService | service | CANDIDATA A BORRAR | Representación iónica antigua. | Borrar si no hay lógica educational útil dentro. |
| MoleculaRepresentacionVesperService | service | CANDIDATA A BORRAR | VSEPR/geometría molecular. | Sacar del flujo de tarjetas. Mantener solo si se crea módulo educativo aparte. |
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
| chemistry.classification.* | MANTENER / REVISAR | Clasifica orgánica/inorgánica/ácido/base/óxido/sal. | Mantener; documentar clase a clase en siguiente barrida. |
| chemistry.analyzer.formula.* | REVISAR | Análisis de fórmulas. | Revisar usos. |
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
MoleculaRepresentacionVesperService
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
