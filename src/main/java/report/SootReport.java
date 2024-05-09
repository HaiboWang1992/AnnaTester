package report;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;
import util.SootConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SootReport {

    private String srcFilePath;
    private String classFolderPath;
    private List<SootClass> classes;
    private Map<String, List<Tag>> type2annotations;

    public SootReport(String srcFilePath, String classFolderPath) {
        this.srcFilePath = srcFilePath;
        this.classFolderPath = classFolderPath;
        SootConfig.setupSoot(classFolderPath);
        this.classes = new ArrayList<>(Scene.v().getApplicationClasses());
        this.type2annotations = new HashMap<>();
        this.type2annotations.put("soot.tagkit.VisibilityAnnotationTag", new ArrayList<>());
        this.type2annotations.put("soot.tagkit.VisibilityLocalVariableAnnotationTag", new ArrayList<>());
        this.type2annotations.put("soot.tagkit.VisibilityParameterAnnotationTag", new ArrayList<>());
        this.retrieveAllAnnotations();
    }

    public void retrieveAllAnnotations() {
        for(SootClass clazz : classes) {
            List<Tag> tags = new ArrayList<>(clazz.getTags());
            for (SootField field : clazz.getFields()) {
                tags.addAll(field.getTags());
            }
            for (SootMethod method : clazz.getMethods()) {
                tags.addAll(method.getTags());
            }
            for (Tag tag : tags) {
                if (isAnnotationTag(tag)) {
                    type2annotations.get(tag.getClass().getName()).add(tag);
                }
            }
        }
    }

    public Map<String, List<Tag>> getType2Annotations() {
        return this.type2annotations;
    }

    public String getFilePath() {
        return this.srcFilePath;
    }

    public boolean isAnnotationTag(Tag tag) {
        return tag instanceof VisibilityParameterAnnotationTag || tag instanceof VisibilityAnnotationTag;
    }

}
