warning: in the working copy of 'app/src/main/java/com/example/dkpace/MainActivity.kt', LF will be replaced by CRLF the next time Git touches it
[1mdiff --git a/.idea/deploymentTargetSelector.xml b/.idea/deploymentTargetSelector.xml[m
[1mindex fd84701..d98f7c2 100644[m
[1m--- a/.idea/deploymentTargetSelector.xml[m
[1m+++ b/.idea/deploymentTargetSelector.xml[m
[36m@@ -4,10 +4,10 @@[m
     <selectionStates>[m
       <SelectionState runConfigName="app">[m
         <option name="selectionMode" value="DROPDOWN" />[m
[31m-        <DropdownSelection timestamp="2026-10-01T15:13:30.470942200Z">[m
[32m+[m[32m        <DropdownSelection timestamp="2026-10-04T13:54:38.970093100Z">[m
           <Target type="DEFAULT_BOOT">[m
             <handle>[m
[31m-              <DeviceId pluginId="PhysicalDevice" identifier="serial=R58N335R5ZB" />[m
[32m+[m[32m              <DeviceId pluginId="LocalEmulator" identifier="path=C:\Users\felix\.android\avd\Medium_Phone.avd" />[m
             </handle>[m
           </Target>[m
         </DropdownSelection>[m
[1mdiff --git a/app/src/main/java/com/example/dkpace/MainActivity.kt b/app/src/main/java/com/example/dkpace/MainActivity.kt[m
[1mindex 2bdfb56..d768911 100644[m
[1m--- a/app/src/main/java/com/example/dkpace/MainActivity.kt[m
[1m+++ b/app/src/main/java/com/example/dkpace/MainActivity.kt[m
[36m@@ -4,6 +4,7 @@[m [mimport android.os.Bundle[m
 import androidx.activity.ComponentActivity[m
 import androidx.activity.compose.setContent[m
 import androidx.activity.enableEdgeToEdge[m
[32m+[m[32mimport androidx.core.view.WindowInsetsControllerCompat[m
 import androidx.compose.animation.AnimatedVisibility[m
 import androidx.compose.animation.fadeOut[m
 import androidx.compose.animation.core.tween[m
[36m@@ -26,6 +27,7 @@[m [mclass MainActivity : ComponentActivity() {[m
     override fun onCreate(savedInstanceState: Bundle?) {[m
         super.onCreate(savedInstanceState)[m
         enableEdgeToEdge()[m
[32m+[m[32m        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false[m
         setContent {[m
             DKPaceTheme {[m
                 var splashVisible by remember { mutableStateOf(true) }[m
