# summer-idlgen
IDL template generator


## For Developers

The `Model` is loaded using the `ModelBuilder` from `idl.yaml`. `Generator` is loaded 
from `GeneratorBuilder` using the `Model` and `idl-template.yaml` to process source files from 
templates. The `Processor` is managing the template engine.

The `Controller` is loaded with a part of `idl-generate.yaml` and controls the `Generator`
for processing the templates. The `Controller` execute exact one `generate` block
out of `idl-generate.yaml`.