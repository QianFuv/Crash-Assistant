# Crash Assistant

Adds a GUI after Minecraft crashed, allowing you to open, upload crash reports or logs to Pastebin, copy links, and perform other actions for easier reporting,debugging or troubleshooting.

## Contributing:
Use gradle `build` task. Compiled jars can be found in:
* `fabric\build\libs` for fabric
* `forge_coremod\build\libs` for forge

## Project structure:
`\app` has code of gui app

`\fabric` has code of fabric mod
* `app` is inluded in jar in jar

`\forge` has code of forge mod

`\common` has code for fabric and forge mods shared code.

`\forge_coremod` has code of forge coremod from which `forge` and `app` launched.

* `app` and `forge` are inluded in jar in jar

### How it works?
Coremod includes 2 services:
* [CrashAssistantTransformationService](forge_coremod%2Fsrc%2Fmain%2Fjava%2Fdev%2Fkostromdan%2Fmods%2Fcrash_assistant%2Fcore_mod%2Fservices%2FCrashAssistantTransformationService.java)
* * `app` should be launched as soon as possible after game start to be able to help players even with coremod/mixin/hs_err crashes. So we launch it from static block of ITransformationService, the first point, we can launch it from forge mod.
* [CrashAssistantDependencyLocator](forge_coremod%2Fsrc%2Fmain%2Fjava%2Fdev%2Fkostromdan%2Fmods%2Fcrash_assistant%2Fcore_mod%2Fservices%2FCrashAssistantDependencyLocator.java)
* * We want to have singlefile mod, not `forge.jar` and `forge_coremod.jar`. Since forge doesn't load jar in jar mods from coremods, we should do it by ourselves.


`\common_loading_utils` has code for `fabric` and `forge_coremod` shared code used for launching gui app.

