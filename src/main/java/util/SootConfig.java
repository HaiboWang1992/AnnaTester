package util;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SootConfig {

    public static List<String> excludeClassList;

    public static void preSootProcess(String classFolderPath) {
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_keep_line_number(true);
        Options.v().set_soot_classpath(classFolderPath);
//        Options.v().set_output_format(Options.output_format_jimple);
//        Options.v().set_output_format(Options.output_format_baf);
//        Options.v().set_output_format(Options.output_format_grimple);
        Options.v().set_output_format(Options.output_format_shimple);
        Options.v().set_process_dir(Collections.singletonList(classFolderPath));
        Options.v().set_whole_program(true);
        Options.v().set_verbose(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
//        Options.v().setPhaseOption("jb.dae","only-stack-locals:true");
//        Options.v().setPhaseOption("jb.cp", "enabled:false");
//        Options.v().setPhaseOption("jb.ls","enabled:false");
//        Options.v().setPhaseOption("jb.dae","enabled:false");
//        Options.v().setPhaseOption("jb.ulp","unsplit-original-locals:false");
//        Options.v().setPhaseOption("jb.a","enabled:false");
//        Options.v().setPhaseOption("jb.cp","enabled:false");
        Options.v().setPhaseOption("jap.npc","enabled:true");
        Options.v().setPhaseOption("jap.abc","enabled:true");
        Options.v().setPhaseOption("jap.abc","with-all:true");

        Scene.v().loadNecessaryClasses();
    }

    public static void postSootProcess() {
        // add to excluded classes
        Options.v().set_exclude(addExcludedClasses());

        PackManager.v().runPacks();

        // Enable SPARK call-graph construction
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg.spark", "enabled:true");
        Options.v().setPhaseOption("cg.spark", "verbose:true");
        Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");
    }

    public static void setupSoot(String classFolderPath) {
        preSootProcess(classFolderPath);
        postSootProcess();
    }

    public static void setupSoot(String targetClassFolderPath, String className) {
        preSootProcess(targetClassFolderPath);
        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        sootClass.setApplicationClass();
        postSootProcess();
    }

    public static void getBasicInfo() {
        SootClass mainClass = Scene.v().getMainClass();
        SootMethod mainMethod = Scene.v().getMainMethod();
        Chain<SootClass> libraryClasses = Scene.v().getLibraryClasses();
        Chain<SootClass> applicationClasses = Scene.v().getApplicationClasses();
        Set<String> basicClasses = Scene.v().getBasicClasses();
        Chain<SootClass> classes = Scene.v().getClasses();
        String sootClassPath = Scene.v().getSootClassPath();
        String s = Scene.v().defaultClassPath();
    }

    public static List<String> addExcludedClasses() {
        if (excludeClassList == null) {
            excludeClassList = new ArrayList<String>();
        }
        excludeClassList.add("java.");
        excludeClassList.add("javax.");
        excludeClassList.add("sun.");
        excludeClassList.add("sunw.");
        excludeClassList.add("com.sun.");
        excludeClassList.add("com.ibm.");
        return excludeClassList;

    }

}
