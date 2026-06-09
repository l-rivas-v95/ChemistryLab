# ChemistryLab - Inventario del backend

> Rama de trabajo: `cleanup/minimal-chemistrylab`.
>
> Objetivo de este documento: dejar dentro del repositorio una referencia rápida de qué hace cada clase del backend para poder decidir después qué se borra, qué se conserva y qué se refactoriza.

## Nota importante sobre `educational`

Cuando se habló de "fórmulas bonitas", no se hacía referencia solo a subíndices visuales. La parte que se estaba buscando era la lógica educativa/curada de representación: casos explícitos y SMILES manipulados para que CDK dibuje una molécula o un ion de forma más didáctica en tarjeta.

Ejemplos conceptuales:

- `H2O` debería poder usar una forma explícita tipo `[H]O[H]` para que CDK pinte los hidrógenos.
- `NH3` debería poder usar hidrógenos explícitos.
- Algunas sales no deberían depender de SMILES iónicos totalmente disociados si eso hace que CDK pinte los iones demasiado separados.
- La vía educativa debe estar separada de VSEPR, red iónica y motores experimentales.

La idea actual recomendada es:

```text
MoleculeCardRepresentationService
    -> RepresentationSmilesOverrideService   // overrides educativos explícitos
    -> IonicSmilesBuilderService             // solo si aporta una forma visual compacta
    -> SMILES de BD
    -> imagen PubChem
    -> fórmula
```

## Arquitectura general

El backend es una aplicación Spring Boot organizada en capas:

- `controller`: expone endpoints REST.
- `service`: lógica de aplicación.
- `repository`: acceso a base de datos con Spring Data JPA.
- `entity`: entidades JPA.
- `dto`: objetos de transferencia hacia el frontend.
- `mapper`: conversión entity -> dto.
- `pubchem`: cliente y DTOs para importar datos externos desde PubChem.
- `chemistry`: lógica química propia: fórmulas, clasificación, iones, conectividad.
- `representation`: servicios de representación visual, SVG y SMILES curados.
- `tools`: clases de prueba/playground.

## Clases principales del backend

### `ChemistryLabApplication`

Paquete: `org.chemistrylab`

Clase de arranque de Spring Boot. Inicializa el contexto de la aplicación, escaneo de componentes y configuración general.

**Mantener.**

---

## Configuración

### `CorsConfig`

Paquete: `org.chemistrylab.config`

Configura CORS para permitir que el frontend pueda consumir la API del backend. Normalmente define orígenes permitidos, métodos HTTP y cabeceras.

**Mantener.**

### `WebClientConfig`

Paquete: `org.chemistrylab.config`

Configura `WebClient` para llamadas HTTP salientes, principalmente hacia PubChem u otros servicios externos.

**Mantener si se sigue importando desde PubChem.**

---

## Controladores

### `ElementoController`

Paquete: `org.chemistrylab.controller`

Expone endpoints relacionados con elementos químicos. Sirve la tabla periódica y detalles de elementos.

Responsabilidades esperadas:

- listar elementos;
- consultar elemento por id/símbolo;
- servir datos necesarios para el frontend de tabla periódica.

**Mantener.** Es parte de lo que se quiere conservar.

### `MoleculaController`

Paquete: `org.chemistrylab.controller`

Expone endpoints relacionados con moléculas y compuestos.

Responsabilidades esperadas:

- listar moléculas;
- filtrar por categoría/familia;
- consultar detalle;
- importar moléculas;
- exponer representación visual de tarjeta/detalle.

**Mantener, pero revisar endpoints de representación.** Debe delegar en un único servicio claro para la tarjeta.

---

## Entidades JPA

### `ElementoEntity`

Paquete: `org.chemistrylab.entity`

Representa un elemento químico en base de datos. Contiene información de tabla periódica: número atómico, símbolo, nombre, grupo, periodo, masa, configuración electrónica, etc.

**Mantener.**

### `EstadoOxidacionEntity`

Paquete: `org.chemistrylab.entity`

Representa estados de oxidación asociados a elementos. Se usa para clasificación química, cargas habituales o razonamiento iónico.

**Mantener si se sigue usando para clasificación/iones. Revisar si está conectado.**

### `MoleculaEntity`

Paquete: `org.chemistrylab.entity`

Representa una molécula o compuesto importado. Guarda campos de PubChem y campos químicos relevantes: CID, nombre, fórmula, masa molecular, SMILES, InChI, imagen 2D, modelo 3D, propiedades físicas, riesgos, usos, etc.

**Mantener.** Es la entidad central de moléculas.

---

## Repositorios

### `ElementoRepository`

Paquete: `org.chemistrylab.repository`

Repositorio Spring Data JPA para `ElementoEntity`. Permite consultas CRUD y búsquedas específicas.

**Mantener.**

### `MoleculaRepository`

Paquete: `org.chemistrylab.repository`

Repositorio Spring Data JPA para `MoleculaEntity`. Permite consultas de moléculas y compuestos.

**Mantener.**

---

## DTOs

### `ElementoDTO`

Paquete: `org.chemistrylab.dto`

DTO usado para enviar datos de elementos al frontend sin exponer directamente la entidad JPA.

**Mantener.**

### `MoleculaDTO`

Paquete: `org.chemistrylab.dto`

DTO principal de moléculas. Transporta datos de lista y detalle de moléculas hacia el frontend.

**Mantener.**

### `MoleculaImportRequest`

Paquete: `org.chemistrylab.dto`

DTO de entrada para solicitar importaciones de moléculas, probablemente por CID, nombre o lista de identificadores.

**Mantener si se mantiene importación desde PubChem.**

### `MoleculaImportResponse`

Paquete: `org.chemistrylab.dto`

DTO de respuesta para informar del resultado de importación: éxito, errores, número de moléculas importadas, etc.

**Mantener si se mantiene importación.**

### `MoleculaRepresentacionDTO`

Paquete: `org.chemistrylab.dto`

DTO usado para devolver la representación visual de una molécula.

Campos conceptuales importantes:

- tipo de representación: `SVG`, `IMAGEN_2D`, `FORMULA`;
- fórmula visual;
- SVG generado;
- imagen 2D externa;
- motor utilizado;
- explicación o motivo de representación;
- input usado para generar la representación.

**Mantener y simplificar si tiene campos heredados no usados.** Es clave para la nueva vía de representación.

### `AtomoRepresentacionDTO`

Paquete: `org.chemistrylab.dto`

DTO de la representación antigua/experimental basada en átomos posicionados manualmente.

**Candidato a borrar** si se elimina la vía manual de representación 2D antigua.

### `EnlaceRepresentacionDTO`

Paquete: `org.chemistrylab.dto`

DTO de enlaces para la representación antigua/experimental de moléculas en 2D.

**Candidato a borrar** si se elimina la vía manual de representación 2D antigua.

---

## Mappers

### `ElementoMapper`

Paquete: `org.chemistrylab.mapper`

Convierte `ElementoEntity` a `ElementoDTO`.

**Mantener.**

### `MoleculaMapper`

Paquete: `org.chemistrylab.mapper`

Convierte `MoleculaEntity` a `MoleculaDTO`. Debe centralizar qué campos de molécula ve el frontend.

**Mantener.**

---

## Servicios de aplicación

### `ElementoService`

Paquete: `org.chemistrylab.service`

Servicio de negocio para elementos químicos. Usa `ElementoRepository` y `ElementoMapper`.

**Mantener.**

### `MoleculaService`

Paquete: `org.chemistrylab.service`

Servicio principal de moléculas. Gestiona búsqueda, listado, consulta por id y posiblemente filtros de familia/categoría.

**Mantener.**

### `MoleculaImportService`

Paquete: `org.chemistrylab.service`

Servicio encargado de importar moléculas desde PubChem y persistirlas en base de datos.

**Mantener si se seguirá alimentando la app desde PubChem.**

### `MoleculaFormulaService`

Paquete: `org.chemistrylab.service`

Servicio relacionado con fórmulas químicas. Puede normalizar fórmulas, extraer elementos o ayudar en clasificación.

**Mantener si está usado por clasificación/importación. Revisar dependencias antes de borrar.**

### `MoleculaRepresentationService` / `MoleculaRepresentacionService`

Paquete: `org.chemistrylab.service`

Servicio de representación anterior. Puede contener lógica antigua de representación atómica, enlaces, estructura 2D o VSEPR.

**Revisar con prioridad.** Si ya no alimenta la tarjeta ni el detalle, es candidato a borrar o dejar solo como histórico temporal.

### `MoleculeCardRepresentationService`

Paquete: `org.chemistrylab.service`

Servicio que debe convertirse en la entrada única para generar la representación de tarjeta.

Flujo deseado:

1. SMILES educativo explícito (`RepresentationSmilesOverrideService`).
2. SMILES visual compacto de sales/iones si procede (`IonicSmilesBuilderService`).
3. SMILES de base de datos.
4. Imagen externa PubChem.
5. Fórmula.

**Mantener.** Es la vía limpia que se está construyendo.

### `MoleculaRepresentacionIonicaService`

Paquete: `org.chemistrylab.service`

Servicio antiguo o experimental para representación iónica.

**Candidato a borrar o aislar** si no se usa en la vía actual. Puede contener ideas útiles, pero no debería alimentar la tarjeta directamente si mezcla motores.

### `MoleculaRepresentacionVesperService`

Paquete: `org.chemistrylab.service`

Servicio de representación VSEPR. Antes generaba geometrías tipo lineal, angular, trigonal, etc.

**Candidato a borrar del flujo de tarjeta.** Podría mantenerse solo como módulo educativo independiente si se decide añadir una vista de teoría molecular, no para tarjetas.

### `Estructura2DService`

Paquete: `org.chemistrylab.service`

Servicio antiguo de estructura 2D. Probablemente genera átomos y enlaces propios para frontend.

**Candidato a borrar** si CDK SVG sustituye la representación manual.

### `OxidoIonico2DService`

Paquete: `org.chemistrylab.service`

Servicio específico para óxidos iónicos en 2D. Ha generado resultados visuales poco estables en tarjetas.

**Candidato a borrar** si se decide que los óxidos se pintan con CDK/SMILES curado o PubChem.

---

## PubChem

### `PubChemClient`

Paquete: `org.chemistrylab.pubchem`

Cliente HTTP para consultar PubChem. Recupera datos de compuestos y moléculas.

**Mantener.**

### `PubChemCompoundData`

Paquete: `org.chemistrylab.pubchem`

DTO/modelo interno para mapear la respuesta de PubChem antes de convertirla a entidad.

**Mantener.**

---

## Química: configuración y catálogo de iones

### `IonConfig`

Paquete: `org.chemistrylab.chemistry.config`

Modelo de configuración de un ion. Contiene fórmula, nombre, carga y metadatos del catálogo.

**Mantener.** Es base para sales, ácidos, hidróxidos y oxoaniones.

### `IonCatalogService`

Paquete: `org.chemistrylab.chemistry.catalog`

Servicio que carga/consulta el catálogo de iones. Permite buscar cationes y aniones por fórmula.

**Mantener.** Es útil para clasificación y representación educativa.

---

## Química: fórmulas

### `FormulaParserService`

Paquete: `org.chemistrylab.chemistry.formula`

Parsea fórmulas químicas como `Ca(OH)2`, `Na2CO3`, `Al2O3`. Debe entender paréntesis, cantidades y símbolos de elementos.

**Mantener.** Muy importante para clasificación y resolver sales.

### `IonicFormulaResolution` en `chemistry.formula`

Paquete: `org.chemistrylab.chemistry.formula`

Parece una clase duplicada o anterior frente a `org.chemistrylab.chemistry.ionic.IonicFormulaResolution`.

**Revisar. Candidato a borrar o fusionar** si no se usa.

---

## Química: iónica

### `IonMatch`

Paquete: `org.chemistrylab.chemistry.ionic`

Representa una coincidencia de ion dentro de una fórmula: ion detectado y cantidad.

Ejemplo conceptual:

```text
Ca(OH)2 -> cation Ca cantidad 1, anion OH cantidad 2
```

**Mantener.**

### `IonicFormulaResolution`

Paquete: `org.chemistrylab.chemistry.ionic`

Resultado de resolver una fórmula iónica. Agrupa catión y anión detectados.

**Mantener.**

### `IonicFormulaResolver`

Paquete: `org.chemistrylab.chemistry.ionic`

Intenta descomponer una fórmula completa en catión y anión usando el catálogo de iones.

Ejemplos:

- `NaCl` -> `Na+` + `Cl-`.
- `Ca(OH)2` -> `Ca2+` + `OH-`.
- `Na2CO3` -> `Na+` + `CO3 2-`.

**Mantener.** Es útil para sales, hidróxidos y ácidos.

---

## Química: clasificación

Las clases bajo `org.chemistrylab.chemistry.classification` se encargan de decidir si una molécula es orgánica, inorgánica, ácido, base/hidróxido, óxido, sal, etc.

**Mantener.** Es una parte central del proyecto.

Pendiente: documentar clase por clase cuando se revise el paquete completo.

---

## Química: conectividad

Las clases bajo `org.chemistrylab.chemistry.connectivity` parecen formar parte del motor que intentaba deducir conexiones químicas entre átomos.

Han aparecido nombres como reglas de conectividad covalente y contexto molecular.

**Revisar con cuidado.** Si solo se usaban para el motor manual de representación, podrían ser candidatas a borrar. Si se usan para clasificación o generación educativa, conservar.

Pendiente: documentar clase por clase cuando se revise el paquete completo.

---

## Química: analyzer/formula/smiles

Paquetes como:

- `chemistry.analyzer.formula`
- `chemistry.smiles`

parecen contener utilidades para analizar fórmulas y/o convertir representaciones químicas.

**Revisar dependencias antes de borrar.**

---

## Representación

### `SmilesToSvgService`

Paquete: `org.chemistrylab.representation`

Servicio que convierte SMILES en SVG usando CDK. Es el motor principal recomendado para representación visual 2D.

**Mantener.**

### `RepresentationSmilesOverrideService`

Paquete: `org.chemistrylab.representation`

Servicio de overrides de SMILES educativos/curados por fórmula.

Debe contener casos donde el SMILES de PubChem o el SMILES por defecto no da una representación buena en tarjeta.

Ejemplos deseados:

- `H2O` -> `[H]O[H]`.
- `NH3` -> `[H]N([H])[H]`.
- `H2O2` -> `[H]OO[H]`.
- `CO2` -> `O=C=O`.
- `SO3` -> `O=S(=O)=O`.

**Mantener y ampliar.** Esta es probablemente la parte llamada informalmente `educational`.

### `IonicSmilesBuilderService`

Paquete: `org.chemistrylab.representation`

Servicio creado para construir SMILES visuales desde fórmulas iónicas usando `IonicFormulaResolver`.

Estado actual: útil pero todavía no reproduce exactamente el comportamiento anterior. Hay que decidir si:

- genera SMILES compactos visuales;
- o solo se usa para oxoaniones;
- o se reemplaza por overrides educativos específicos.

**Mantener temporalmente. Revisar después de pruebas visuales.**

---

## Tools / Playgrounds

### `CdkDepictionPlayground`

Paquete: `org.chemistrylab.tools`

Clase de pruebas para comparar cómo CDK dibuja distintos SMILES.

**Mantener temporalmente mientras se estabiliza la representación. Luego podría borrarse.**

### `RepresentationDecisionPlayground`

Paquete: `org.chemistrylab.tools`

Clase de pruebas para comparar decisiones de representación.

**Mantener temporalmente.**

---

## Duplicidades y candidatos claros a revisar

### Posibles duplicados

- `org.chemistrylab.chemistry.formula.IonicFormulaResolution`
- `org.chemistrylab.chemistry.ionic.IonicFormulaResolution`

Conviene quedarse con una sola versión si una está en desuso.

### Posibles restos del motor antiguo

- `AtomoRepresentacionDTO`
- `EnlaceRepresentacionDTO`
- `Estructura2DService`
- `MoleculaRepresentacionIonicaService`
- `MoleculaRepresentacionVesperService`
- `OxidoIonico2DService`
- clases de conectividad si solo servían a ese motor

### Cosas a conservar seguro

- entidades;
- repositorios;
- controladores principales;
- servicios de elementos/moléculas/importación;
- clasificación;
- catálogo de iones;
- parser de fórmulas;
- `SmilesToSvgService`;
- `RepresentationSmilesOverrideService`;
- `MoleculeCardRepresentationService`.

## Plan recomendado para empezar a borrar

1. Buscar referencias de cada clase candidata con `Find Usages`.
2. Si una clase solo se llama desde tools/playground, moverla a experimental o borrarla.
3. Dejar un único endpoint de representación de tarjeta.
4. Eliminar DTOs de representación manual si el frontend ya solo usa SVG/imagen/fórmula.
5. Mantener `educational`/overrides como capa separada, no mezclada con VSEPR ni red iónica.

## Estado del flujo de representación

Flujo deseado final:

```text
MoleculaController
    -> MoleculeCardRepresentationService
        -> RepresentationSmilesOverrideService
        -> IonicSmilesBuilderService, solo si mejora el caso
        -> SmilesToSvgService
        -> PubChem image fallback
        -> Formula fallback
```

Frontend:

```text
MoleculeStructure.jsx
    SVG       -> inline SVG
    IMAGEN_2D -> img
    FORMULA   -> ChemicalFormulaText
```
