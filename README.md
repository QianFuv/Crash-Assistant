# Crash Assistant

Shows a GUI after Minecraft crashes, immediately showing all affected game/launcher logs, crash reports, or hs_err files. Provides a one-click solution to upload them, copy the link, and perform other actions for easier reporting, debugging, and troubleshooting.

![image](https://github.com/user-attachments/assets/3bc47fdb-08c0-49b3-a6e2-8d66af841af1)

## Contributing:
Use gradle `build` task of root project. Compiled jars can be found in: `build\libs`:
* `crash_assistant-fabric-<version>.jar)` fabric mod.
* `crash_assistant-forge-<version>.jar)` forge mod.

## Project structure:
`\app` has code of gui app

`\fabric` has code of fabric mod
* `app` is inluded in jar in jar

`\forge` has code of forge mod

`\common` has code for fabric and forge mods shared code.

`\common_config` has code for `app`, `fabric`, `forge_coremod` shared code used for runtime config.

`\common_loading_utils` has code for `fabric` and `forge_coremod` shared code used for launching gui app.

`\forge_coremod` has code of forge coremod from which `forge` mod and `app` launched.

* `app` and `forge` are inluded in jar in jar

### How it works?
Coremod includes 2 services:
* [CrashAssistantTransformationService](forge_coremod%2Fsrc%2Fmain%2Fjava%2Fdev%2Fkostromdan%2Fmods%2Fcrash_assistant%2Fcore_mod%2Fservices%2FCrashAssistantTransformationService.java)
  * `app` should be launched as soon as possible after game start to be able to help players even with coremod/mixin/hs_err crashes. So we launch it from static block of ITransformationService, the first point, we can launch it from forge mod.
* [CrashAssistantDependencyLocator](forge_coremod%2Fsrc%2Fmain%2Fjava%2Fdev%2Fkostromdan%2Fmods%2Fcrash_assistant%2Fcore_mod%2Fservices%2FCrashAssistantDependencyLocator.java)
  * We want to have singlefile mod, not `forge_mod.jar` and `forge_coremod.jar`. Since forge doesn't load jar in jar mods from coremods, we should do it by ourselves.
